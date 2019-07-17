package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class IRNode
{

    public String opcode;
    public String instructions = "";
    public ArrayList<Operand> operands;
    public VariableTable varTable;
    public BasicBlock parentBlock;

    /**
     * get the instructions
     * @return the instructions for the node
     */
    public String getInstructions()
    {
        return this.instructions;
    }

    /**
     * generate the insttuctions
     */
    public abstract void generateInstructions();

    /**
     * DEPRECATED
     */
    public abstract void insertOP();

    /**
     * invoke an internal method
     * @param method the method
     * @param args the arguments of the method
     * @param variableTable the variable table to use
     * @return string with instructions for callling method
     */
    public static String invokeMethod(Method method, ArrayList<Operand> args, VariableTable variableTable)
    {
        String ret = "";

        if(method.methodName.equals("main"))
        {
            ret+="ldc 1\n" + "anewarray java/lang/String\n";
            ret += "invokestatic " + ControlFlowGraph.publicModuleName + "/" + method.methodName+"([Ljava/lang/String;)V\n";
        }
        else
        {
            for (int i = 0; i < args.size(); ++i) {
                Operand op = args.get(i);
                ret += op.load(variableTable);
            }

            ret += "invokestatic " + ControlFlowGraph.publicModuleName + "/" + method.methodName;
            ret += "(";
            ArrayList<String> paraments = method.methodArgs;
            for (int i = 0; i < paraments.size(); ++i) {
                ret += paraments.get(i) + "";
            }
            ret += ")";
            if (method.returnIsArray == null) {
                ret += "V\n";
            } else if (method.returnIsArray == true) {
                ret += "[I\n";
            } else if (method.returnIsArray == false) {
                ret += "I\n";
            }
        }
        return ret;
    }

    /**
     * invoke io read
     * @return instructions operand
     */
    public static Operand ioread()
    {
        String ret = "invokestatic io/read()I\n";
        return new Operand(ret);
    }

    /**
     * invoke ioprint
     * @param str the format string
     * @param op the operator to print
     * @param variableTable the variable table
     * @return instructions operand
     */
    public static Operand ioprint(String str, Operand op, VariableTable variableTable)
    {
        Set<String> variables = new HashSet<>();
        String ret = "";
        ret = "ldc " + str + "\n";

        ret += op.load(variableTable);
        variables.addAll(op.getVariableNames());

        ret += "invokestatic io/print(Ljava/lang/String;I)V\n";

        Operand instructions = new Operand(ret);
        instructions.setVariableNames(variables);

        instructions.args.add(op);
        instructions.printString = str;
        instructions.invokeMethodString = "io/print";

        return instructions;
    }

    /**
     * invoke ioprint
     * @param str the format string
     * @return instructions operand
     */
    public static Operand ioprint(String str)
    {
        String ret = "ldc " + str + "\n";
        ret += "invokestatic io/print(Ljava/lang/String;)V\n";
        return new Operand(ret);
    }

    /**
     * invoke ioprint
     * @param op the operator to print
     * @param variableTable variable table
     * @return instructions operand
     */
    public static Operand ioprint(Operand op, VariableTable variableTable)
    {
        Set<String> variables = new HashSet<>();

        String ret = "";
        ret+=op.load(variableTable);

        variables.addAll(op.getVariableNames());

        ret += "invokestatic io/print(I)V\n";

        Operand instructions = new Operand(ret);
        instructions.setVariableNames(variables);

        instructions.args.add(op);
        instructions.invokeMethodString = "io/print";

        return instructions;
    }

    /**
     * invoke ioprintln
     * @param str the format string
     * @param op operator to print
     * @param variableTable the variable table
     * @return instructions operand
     */
    public static Operand ioprintln(String str, Operand op, VariableTable variableTable)
    {
        Set<String> variables = new HashSet<>();
        String ret = "";
        ret = "ldc " + str + "\n";

        ret += op.load(variableTable);
        variables.addAll(op.getVariableNames());

        ret += "invokestatic io/println(Ljava/lang/String;I)V\n";

        Operand instructions = new Operand(ret);
        instructions.setVariableNames(variables);

        instructions.args.add(op);
        instructions.printString = str;
        instructions.invokeMethodString = "io/println";
        return instructions;
    }

    /**
     * ioprintln
     * @param str format string
     * @return instructions
     */
    public static Operand ioprintln(String str)
    {
        String ret = "";
        ret = "ldc " + str + "\n";
        ret += "invokestatic io/println(Ljava/lang/String;)V\n";
        return new Operand(ret);
    }

    /**
     * ioprintln
     * @param op operator
     * @param variableTable variable table
     * @return instructions
     */
    public static Operand ioprintln(Operand op, VariableTable variableTable)
    {
        Set<String> variables = new HashSet<>();

        String ret = "";
        ret += op.load(variableTable);

        variables.addAll(op.getVariableNames());

        ret += "invokestatic io/println(I)V\n";

        Operand instructions = new Operand(ret);
        instructions.setVariableNames(variables);

        instructions.args.add(op);
        instructions.invokeMethodString = "io/println";

        return instructions;
    }

    /**
     * ioprintln
     * @return instructions
     */
    public static Operand ioprintln()
    {
        String ret = "";
        ret += "invokestatic io/println()V\n";
        return new Operand(ret);
    }

    /**
     * invoke externam method
     * @param packageName method package
     * @param method method
     * @param args args
     * @param parameters parameters
     * @param vartable varible table
     * @param returnType method return type
     * @return instructions
     */
    public static Operand invokeExternal(String packageName, String method, ArrayList<Operand> args, ArrayList<String> parameters, VariableTable vartable, String returnType)
    {
        Set<String> variables = new HashSet<>();

        String ret = "";
        for (int i = 0; i < args.size(); ++i)
        {
            Operand op = args.get(i);
            ret += op.load(vartable);

            variables.addAll(op.getVariableNames());
        }
        ret += "invokestatic " + packageName + "/" + method + "(";
        for (int i = 0; i < args.size(); ++i)
        {
            ret += parameters.get(i) + "";
        }
        ret += ")" + returnType + "\n";

        Operand instructions = new Operand(ret);
        instructions.setVariableNames(variables);

        instructions.args = args;
        instructions.invokeMethodString = packageName+"/"+method;
        instructions.parameters = parameters;
        instructions.returnType = returnType;

        return instructions;
    }

    /**
     * the uses
     * @return uses
     */
    public abstract Set<String> getUses();

    /**
     * the defs
     * @return defs
     */
    public abstract Set<String> getDefs();

    /**
     * the defs and uses
     * @return defs and uses
     */
    public abstract pair<Set<String>,Set<String>> getDefsAndUses();

    /**
     * sets the variable table
     * @param table the table
     */
    public void setVarTable(VariableTable table)
    {
        this.varTable = table;
    }

}
