package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an assign node on the intermediate representation used for code generation.
 */
public class AssignNode extends IRNode
{
    /**
     * Constructor of the class that receives the Operand where the result of the right hand side should be stored in, an Operand representing the right hand side of the assign (the value that will be stored in the left hand side), the variable table where the Operand on the left and right hand side are contained on and the bock that is the parent of the current block.
     * @param storeVar An instance of the class Operand representing the operand where the result of the arithmetic expression wil be stored.
     * @param value An instance of the class Operand that represents the variable where the result of the right hand side (represented by Operand value) will be stored on.
     * @param variableTable An instance of the class VariableTable which represents the variable table where the left and right hand side operands are contained on.
     * @param parent The parent block of the current block. Blocks are used to perform branching on certain conditions.
     */
    public AssignNode(Operand storeVar, Operand value, VariableTable variableTable, BasicBlock parent)
    {
        this.operands = new ArrayList<>();
        this.operands.add(storeVar);
        this.operands.add(value);
        this.varTable = variableTable;
        this.parentBlock = parent;
        this.generateInstructions();
    }

    /**
     * This methods performs the necessary actions, operating on internal information received on the constructor, to generate the assembly instructions for an arithmetic node.
     */
    @Override
    public void generateInstructions()
    {
        this.instructions="";
        if(this.operands.get(0).isArray && !this.operands.get(1).isArray  && !this.operands.get(1).isInstructions  && !this.operands.get(1).isMethod)
        {
            Operand op1 = this.operands.get(0);
            Operand op2 = this.operands.get(1);

            int sizeoffset = this.varTable.varTable.size();
            int counteroffset = this.varTable.varTable.size()+1;

            //load the arrayref on the stack
            this.instructions+=op1.load(this.varTable);

            //get the array size
            this.instructions+="arraylength\n";

            //save the array size to the variable table
            this.instructions+="istore "+sizeoffset+"\n";

            //start the counter and save it
            this.instructions+="ldc 0\n";
            this.instructions+="istore "+counteroffset+"\n";

            //start the loop
            String blockName = Method.generateBlockID();
            this.instructions+=blockName+"loopstart:\n";

            //laod the varibles to compares
            this.instructions+="iload "+counteroffset+"\n";
            this.instructions+="iload "+sizeoffset+"\n";

            //compare the variable
            this.instructions+="if_icmplt "+blockName+"body\n";
            this.instructions+="goto "+blockName+"end\n";

            //while body
            this.instructions+=blockName+"body:\n";

            //load the array reference
            this.instructions+=op1.load(this.varTable);

            //load the index
            this.instructions+="iload "+counteroffset+"\n";

            //load the value to store
            Operand val = op2;
            this.instructions += val.load(this.varTable);

            //store the index
            this.instructions+="iastore\n";

            //increment the index
            this.instructions+="iload "+counteroffset+"\n";
            this.instructions+="ldc 1\n";
            this.instructions+="iadd\n";
            this.instructions+="istore "+counteroffset+"\n";

            //jump to while header
            this.instructions+="goto "+blockName+"loopstart\n";

            //end of the loop
            this.instructions+=blockName+"end:\n";

            return;
        }


        {
            Operand op = this.operands.get(1);
            this.instructions+=op.load(this.varTable);
        }

        //value to store is now on top of the stack
        {
            Operand op = this.operands.get(0);
            if (op.isGlobal)
            {
                if (op.isArray)
                {
                    this.instructions += "putstatic "+ ControlFlowGraph.publicModuleName +"/" + op.value + " [I\n";
                } else if (op.isArrayAccess)
                {
                    //save the value on the stack to a temporary register for later use
                    int offset = this.varTable.varTable.size();
                    this.instructions+="istore "+offset+"\n";

                    //load arrayref on the stack
                    this.instructions+="getstatic "+ControlFlowGraph.publicModuleName+"/"+op.value+" [I\n";

                    //load array index on the stack
                    Operand index = op.arrayIndex;
                    this.instructions+=index.load(this.varTable);

                    //reload the value to store
                    this.instructions+="iload "+offset+"\n";

                    //save the value to the array
                    this.instructions+="iastore\n";

                } else
                {
                    this.instructions += "putstatic "+ ControlFlowGraph.publicModuleName +"/" + op.value + " I\n";
                }
            } else
            {
                if (op.isArray)
                {
                    if(this.varTable.varTable.containsKey(op.value))
                    {
                        this.instructions += "astore " + this.varTable.varTable.get(op.value) + "\n";
                    }
                    else {
                        this.instructions += "astore " + this.varTable.varTable.size() + "\n";
                        this.varTable.addLocalVariable(op.value);
                    }
                } else if (op.isArrayAccess)
                {

                    //save the value on the stack to a temporary register for later use
                    int offset = this.varTable.varTable.size();
                    this.instructions+="istore "+offset+"\n";

                    //load arrayref on the stack
                    this.instructions+="aload "+this.varTable.varTable.get(op.value)+"\n";

                    //load array index on the stack
                    Operand index = op.arrayIndex;
                    this.instructions+=index.load(this.varTable);

                    //reload the value to store
                    this.instructions+="iload "+offset+"\n";

                    //save the value to the array
                    this.instructions+="iastore\n";

                } else if (op.isVariable)
                {
                    if (this.varTable.varTable.containsKey(op.value))
                    {
                        int offset = this.varTable.varTable.get(op.value);
                        this.instructions += "istore " + offset + "\n";
                    } else
                    {
                        this.varTable.addLocalVariable(op.value);
                        int offset = this.varTable.varTable.get(op.value);
                        this.instructions += "istore " + offset + "\n";
                    }
                }
            }
        }
        //variable should now have been stored properly
    }

    /**
     * Method inherited from the superclass IRNode that needs to be implemented by this class since it is abstract and in this case it is overridden only to perform no action because no action is needed in the special case of an AssignNode.
     */
    @Override
    public void insertOP()
    {

    }

    /**
     * Returns the uses of this assign node. This is used for liveness analysis.
     * @return A Set of String containing the uses of this assign node.
     */
    @Override
    public Set<String> getUses() {
        Set<String>ret = new HashSet<>();
        ret.addAll(this.operands.get(1).getVariableNames());
        return ret;
    }

    /**
     * Returns the defs of this assign node. This is used for liveness analysis.
     * @return A Set of String containing the defs of this assign node.
     */
    @Override
    public Set<String> getDefs() {
        Set<String>ret = new HashSet<>();
        ret.addAll(this.operands.get(0).getVariableNames());
        if(this.operands.get(0).isArrayAccess)
        {
            ret.addAll(this.operands.get(0).getVariableNames());
        }
        return ret;
    }

    /**
     * Returns the defs and uses of this assign node. This is used for liveness analysis.
     * @return A Set of String containing the defs and uses of this assign node.
     */
    @Override
    public pair<Set<String>, Set<String>> getDefsAndUses() {
        Set<String>uses = new HashSet<>();
        Set<String>defs = new HashSet<>();

        defs.addAll(this.operands.get(0).getVariableNames());
        uses.addAll(this.operands.get(1).getVariableNames());

        if(this.operands.get(0).isArrayAccess)
        {
            uses.addAll(this.operands.get(0).getVariableNames());
        }

        return new pair<>(defs,uses);
    }

    /**
     * Generates the assembly instructions for this assign node (by calling the method generateInstructions of this class) and returns a String containing those instructions.
     * @return A String with the generated instructions for this assign node.
     */
    @Override
    public String getInstructions()
    {
        this.generateInstructions();
        return this.instructions;
    }
}
