package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a call node (generated when a function is called) on the intermediate representation used for code generation.
 */
public class CallNode extends IRNode
{
    /**
     * An instance of the class Method which represents the method that is being called in the intermediate representation, in case the call uses a method.
     */
    public Method method;
    /**
     * An ArrayList of Operands which represents the arguments to the function that is being called. This operands are instances of the class Operand which represents an operand in the intermediate representation.
     */
    public ArrayList<Operand> args;

    /**
     * A boolean value indicating if the call node uses literal instructions or calls a method (which is represented by the method attribute).
     */
    public boolean isInstructions = false;
    /**
     * An instance of the class Operand representing the Operand in case the call uses literal instructions.
     */
    public Operand instructionsOperand = null;

    /**
     * Constructor of the class that receives the called Method, an ArrayList with the operands representing the arguments given on the function call.
     * This version of the constructor is used when the CallNode represents a call to a function.
     * @param method The intermediate representation of the method that is being called.
     * @param args An ArrayList with Operands which represent the operands to the function call.
     * @param variableTable The variable table of the current method where the call took place (not the variable table of the called method) because it is needed to push the variables passed as arguments to the stack.
     */
    public CallNode(Method method, ArrayList<Operand> args, VariableTable variableTable)
    {
        this.method = method;
        this.args = args;
        this.varTable = variableTable;
        this.generateInstructions();
    }

    /**
     * Constructor of the class that receives an Operand representing the instructions to be executed on the call.
     * This version of the constructor is used when the CallNode represents literal instructions to be executed and not a call to a function.
     * @param instructs An Operand representing the instructions to be executed on the call.
     */
    public CallNode(Operand instructs)
    {
        this.instructionsOperand = instructs;
        this.isInstructions = true;
        this.instructions = instructs.value;
    }

    /**
     * This methods performs the necessary actions, operating on internal information received on the constructor, to generate the assembly instructions for a call node.
     */
    @Override
    public void generateInstructions()
    {
        String ret = "";
        if(this.isInstructions)
        {
            ret = this.instructionsOperand.load(varTable);
        }
        else
        {
            ret = this.invokeMethod(this.method, this.args, this.varTable);
        }
        this.instructions = ret;
    }

    /**
     * Method inherited from the superclass IRNode that needs to be implemented by this class since it is abstract and in this case it is overridden only to perform no action because no action is needed in the special case of a CallNode.
     */
    @Override
    public void insertOP()
    {

    }

    /**
     * Returns the uses of this call node. This is used for liveness analysis.
     * @return A Set of String containing the uses of this call node.
     */
    @Override
    public Set<String> getUses() {
        Set<String>variables = new HashSet<>();

        if(!this.isInstructions)
        {
            for (Operand arg : this.args) {
                variables.addAll(arg.getVariableNames());
            }
        }
        else
        {
            variables.addAll(this.instructionsOperand.getVariableNames());
        }

        return variables;
    }

    /**
     * Returns the defs of this call node. This is used for liveness analysis.
     * @return A Set of String containing the defs of this call node.
     */
    @Override
    public Set<String> getDefs() {
        return new HashSet<>();
    }

    /**
     * Returns the defs and uses of this call node. This is used for liveness analysis.
     * @return A Set of String containing the defs and uses of this call node.
     */
    @Override
    public pair<Set<String>, Set<String>> getDefsAndUses() {
        return new pair<>(new HashSet<>(),this.getUses());
    }

    /**
     * Generates the assembly instructions for this call node (by calling the method generateInstructions of this class) and returns a String containing those instructions.
     * @return A String with the generated instructions for this call node.
     */
    @Override
    public String getInstructions()
    {
        this.generateInstructions();
        return this.instructions;
    }
}
