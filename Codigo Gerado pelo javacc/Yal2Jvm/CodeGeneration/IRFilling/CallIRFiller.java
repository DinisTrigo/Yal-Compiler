package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.*;
import Yal2Jvm.CodeGeneration.IR.*;

import java.util.ArrayList;

/**
 * This class implements the necessary actions to fill the IR for an Call node.
 */
public class CallIRFiller
{

    /** 
     * This method is called recursively to go through the entire AST to fill the intermediate representation (IR).
     * This implements the function fillIRSecondPass from the class ASTCall which overrides the method from the class SimpleNode it extends. 
     * The method fillIRSecondPass overrides SimpleNode's method because it implements the needed special behavior.
     * This function gets all the arguments from the Yal call, constructing an Operand object for each argument and pushing each into a list of Operand objects.
     * This function then calls getIrNode generate the node to add to the currentBlock.
     * @param astCall ASTCall node which this class is replacing the function fillIRSecondPass
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @param currentBlock A class representing the current block on the IR of this AST node. This is used to perform branching on certain conditions.
     */
    public static void fillIRSecondPass(ASTCall astCall, ST st, IRContainer irContainer, BasicBlock currentBlock)
    {
        ControlFlowGraph flow = ((Method) irContainer).parentCFG;
        Method currentMethodIR = (Method) irContainer;
        ArrayList<Operand> args_op = new ArrayList<Operand>();

        if (astCall.getChildren() != null)
            if (astCall.getChildren()[0] != null)
            {
                ASTArgumentList argumentList = (ASTArgumentList) astCall.getChildren()[0];
                for (Node node : argumentList.getChildren())
                {
                    ASTArgument argument = (ASTArgument) node;
                    boolean isGlobal = false;
                    boolean isVariable = false;
                    boolean isArray = false;

                    String value = null;

                    if (argument.id != null)
                    {
                        isGlobal = ((FunctionST) st).globalST.globals.containsKey(argument.id);
                        isVariable = true;
                        value = argument.id;
                        if (st.isVariableArray(argument.id))
                            isArray = true;
                    } else if (argument.integer != null)
                    {
                        value = argument.integer + "";
                    } else if (argument.str != null)
                    {
                        value = argument.str;
                    } else
                    {
                        System.out.println("Error, argument doesn't have a value." + " on line: " + astCall.getLine());
                        return;
                    }

                    args_op.add(new Operand(value, isVariable, isArray, isGlobal));
                }
            }

        IRNode irnode = null;
        irnode = getIrNode(flow, args_op, currentMethodIR,
                astCall.outerCalledID, astCall.innerCalledID, astCall.getLine(),
                astCall.getParent(), st, currentBlock);
        if (irnode == null)
            return;

        currentBlock.addNode(irnode);
    }
    
    /**
     * This function returns a Callnode depending on the type of call.
     * If its a method from the IO module then the function getMethodOperandInCaseOfExternalCallForIOModule is called.
     * If it's an ext invocation like "invokestatic ext/f()V", then getMethodOperandInCaseOfExternalCall is called.
     * If it's a regular function invocation then it will return CallNode constructed with the method.
     * @param flow Parent CFG of the method.
     * @param args_op Argument list of the method being invoked in Yal.
     * @param currentMethodIR A class representing the intermediate representation to be filled.
     * @param outerCalledID If it's a method of a module being invoked, then this variable is either IO or ext, otherwise it's the name of the method.
     * @param innerCalledID If it's a method of a module being invoked then this variable is the name of the method, otherwise it's null.
     * @param currLine The current line.
     * @param parent The parent of the ASTCall node.
     * @param st An Object that represents the already filled symbol table.
     * @param currentBlock A class representing the current block on the IR of this AST node. This is used to perform branching on certain conditions.
     * @return Returns the node of the invocation.
     */
    public static IRNode getIrNode(ControlFlowGraph flow, ArrayList<Operand> args_op, Method currentMethodIR,
                                   String outerCalledID, String innerCalledID, int currLine, Node parent, ST st, BasicBlock currentBlock)
    {
        IRNode irnode;
        if (innerCalledID != null)
        {
            Operand operand;
            if (outerCalledID.equals("io"))
                operand = getMethodOperandInCaseOfExternalCallForIOModule(args_op, currentMethodIR, innerCalledID, currLine, currentBlock);
            else
            {
                operand = getMethodOperandInCaseOfExternalCall(args_op, currentMethodIR, outerCalledID, innerCalledID, parent, st);
                if (operand == null) return null;
            }
            irnode = new CallNode(operand);
            if (irnode == null)
                return null;
        } else{

            Method methodToInvoke = flow.getMethod(outerCalledID);

            irnode = new CallNode(methodToInvoke, args_op, currentMethodIR.localVariables);
        }
        return irnode;
    }

    /**
     * This method returns the Operand with the JVM instruction to invoke the ext method.
     * @param args_op Argument list of the method being invoked in Yal.
     * @param currentMethodIR A class representing the intermediate representation to be filled.
     * @param outerCalledID If it's a method of a module being invoked, then this variable is either IO or ext, otherwise it's the name of the method.
     * @param innerCalledID If it's a method of a module being invoked then this variable is the name of the method, otherwise it's null.
     * @param parent The parent of the ASTCall node.
     * @param st An Object that represents the already filled symbol table.
     * @return Returns the Operand with the external call.
     */
    public static Operand getMethodOperandInCaseOfExternalCall(ArrayList<Operand> args_op, Method currentMethodIR,
                                                               String outerCalledID, String innerCalledID, Node parent,
                                                               ST st) {
        Operand operand;
        ArrayList<String> varTypes = getArrayListOfVarTypes(args_op);
        //check if function is void (or return type is ignored) or if it is being assigned to a variable (and therefore assume the function returns whatever is the type of the variable)
        String returnType;
        if (!(parent instanceof ASTTerm)) //assume void return type
            returnType = "V";
        else
        {
            ASTRhs rhs = (ASTRhs) ((ASTTerm) parent).getParent();
            ASTAssign assign = (ASTAssign) rhs.getParent();
            ASTLhs lhs = (ASTLhs) assign.getChildren()[0];
            boolean variableIsArray;
            if (lhs.getChildren()[0] instanceof ASTArrayAccess)
                variableIsArray = false;
            else if(lhs.getChildren()[0] instanceof ASTScalarAccess)
            {
                String lhsID = ((ASTScalarAccess) lhs.getChildren() [0]).id;
                variableIsArray = st.isVariableArray(lhsID);
            }
            else
                return null;
            if (variableIsArray)
                returnType = "[I";
            else
                returnType = "I";
        }
        operand = IRNode.invokeExternal(outerCalledID, innerCalledID, args_op, varTypes,
                currentMethodIR.localVariables, returnType);
        return operand;
    }
    /**
     * Method to get the types of the arguments for the JVM instructions.
     * @param operands The list of arguments.
     * @return Returns an array of strings with the type of each argument in JVM instruction format.
     */
    private static ArrayList<String> getArrayListOfVarTypes(ArrayList<Operand> operands)
    {
        ArrayList<String> varTypes = new ArrayList<>();
        for (Operand operand : operands)
        {
            if (operand.isArray)
                varTypes.add("[I");
            else
                varTypes.add("I");
        }

        return varTypes;
    }

    /**
     * This method is specifically for functions in the IO module.
     * Methods can be 'read()', 'print(String, int)', 'print(String)', 'print(int)', 'println(String, int)', 'println(String)', 'println(int)' and 'println()'.
     * IRNode has a method for each IO function which return an Operand with the valid JVM instruction.
     * A switch is used for read, print and println and within those the number and type of arguments is taken into account when calling the right IRNode method.
     * @param args_op List of the Yal method arguments.
     * @param currentMethodIR A class representing the intermediate representation to be filled.
     * @param innerCalledID The name of the Yal function being called.
     * @param currLine The current line. 
     * @param currentBlock A class representing the current block on the IR of this AST node. This is used to perform branching on certain conditions.
     * @return Returns the Operand with the IO module JVM instruction.
     */
    public static Operand getMethodOperandInCaseOfExternalCallForIOModule(ArrayList<Operand> args_op, Method currentMethodIR,
                                                                          String innerCalledID, int currLine, BasicBlock currentBlock)
    {
        Operand operation = null;
        switch (innerCalledID)
        {
            case "read":

                operation = IRNode.ioread();
                break;

            case "print":

                if (args_op.size() == 2)
                {
                    operation = IRNode.ioprint(args_op.get(0).value, args_op.get(1), currentMethodIR.localVariables);
                } else if (args_op.size() == 1)
                {
                    try
                    {
                        Integer.parseInt(args_op.get(0).value);
                        operation = IRNode.ioprint(args_op.get(0), currentMethodIR.localVariables);
                    } catch (NumberFormatException e)
                    {
                        if (args_op.get(0).isVariable)
                            operation = IRNode.ioprint(args_op.get(0), currentMethodIR.localVariables);
                        else
                            operation = IRNode.ioprint(args_op.get(0).value);
                    }
                } else
                {
                    System.out.println("Bad number of arguments for io call." + " on line: " + currLine);
                    return null;
                }
                break;

            case "println":

                if (args_op.size() == 0)
                {
                    operation = IRNode.ioprintln();
                } else if (args_op.size() == 2)
                {
                    operation = IRNode.ioprintln(args_op.get(0).value, args_op.get(1), currentMethodIR.localVariables);
                } else if (args_op.size() == 1)
                {

                    try
                    {
                        Integer.parseInt(args_op.get(0).value);
                        operation = IRNode.ioprintln(args_op.get(0), currentMethodIR.localVariables);
                    } catch (NumberFormatException e)
                    {
                        if (args_op.get(0).isVariable)
                            operation = IRNode.ioprintln(args_op.get(0), currentMethodIR.localVariables);
                        else
                            operation = IRNode.ioprintln(args_op.get(0).value);
                    }
                } else
                {
                    System.out.println("Bad number of arguments for io call." + " on line: " + currLine);
                    return null;
                }
                break;

        }
        return operation;
    }

    /**
     * Receives a ASTCall node and runs through all its arguments to create an Operand object for each and return an array of all arguments as Operands.
     * @param astCall ASDCall node.
     * @param functionST The functions symbol table handler. 
     * @return Returns a list of all arguments as operands.
     */
    public static ArrayList<Operand> getArgsAsIROperands(ASTCall astCall, FunctionST functionST)
    {
        ArrayList<Operand> operands = new ArrayList<>();
        if (astCall.getChildren() == null)
            return operands;
        Node[] args = ((SimpleNode) (astCall.getChildren()[0])).getChildren();

        for (Node node : args)
        {
            ASTArgument arg = (ASTArgument) node;
            String value;
            boolean isVariable;
            boolean isGlobal;
            boolean isArray;
            if (arg.integer != null)
            {
                value = arg.integer.toString();
                isVariable = false;
                isGlobal = false;
                isArray = false;
            } else if (arg.id != null)
            {
                value = arg.id;
                isVariable = true;
                if (functionST.locals.get(value) != null)
                    isGlobal = false;
                else
                    isGlobal = true;
                if (functionST.isVariableArray(arg.id))
                    isArray = true;
                else
                    isArray = false;
            } else
                continue;
            Operand operand = new Operand(value, isVariable, isArray, isGlobal);
            operands.add(operand);
        }
        return operands;
    }

}
