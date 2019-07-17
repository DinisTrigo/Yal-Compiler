package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.globals;
import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents an arithmetic node on the intermediate representation used for code generation.
 */
public class ArithNode extends IRNode
{

    /**
     * An instance of the class Operand representing the operand where the value of the assign will be stored.
     */
    public Operand storeVar;
    /**
     * A String representing the operator of the arithmetic expression.
     */
    public String operator;

    /**
     * Constructor of the class that receives the Operator of the expression, two operands representing the left hand side and the right hand side of the expression, the variable table where both Operands are contained on, the bock that is the parent of the current block and operand where the result of the expression will be stored. Blocks are used to perform branching on certain conditions.
     * @param operator A String representing the operator of the arithmetic expression.
     * @param lhs An instance of the class Operand representing the left hand side operand of the expression.
     * @param rhs An instance of the class Operand representing the right hand side operand of the expression.
     * @param variableTable An instance of the class VariableTable which represents the variable table where both Operands are contained on.
     * @param parent The parent block of the current block. Blocks are used to perform branching on certain conditions.
     * @param StoreVar An instance of the class Operand representing the operand where the result of the arithmetic expression wil be stored.
     */
    public ArithNode(String operator, Operand lhs, Operand rhs, VariableTable variableTable, BasicBlock parent, Operand StoreVar)
    {
        this.operands = new ArrayList<>();
        this.operands.add(lhs);
        this.operands.add(rhs);
        this.varTable = variableTable;
        this.parentBlock = parent;
        this.storeVar = StoreVar;

        this.operator = operator;

        switch (operator)
        {
            case ("+"):
            {
                this.opcode = "iadd";
                break;
            }
            case ("-"):
            {
                this.opcode = "isub";
                break;
            }
            case ("*"):
            {
                this.opcode = "imul";
                break;
            }
            case ("/"):
            {
                this.opcode = "idiv";
                break;
            }
            case ("<<"):
            {
                this.opcode = "ishl";
                break;
            }
            case (">>"):
            {
                this.opcode = "ishr";
                break;
            }
            case (">>>"):
            {
                this.opcode = "iushr";
                break;
            }
            case ("&"):
            {
                this.opcode = "iand";
                break;
            }
            case ("|"):
            {
                this.opcode = "ior";
                break;
            }
            case ("^"):
            {
                this.opcode = "ixor";
                break;
            }
        }
        this.generateInstructions();
        //this.insertOP();
    }

    /**
     * This methods performs the necessary actions, operating on internal information received on the constructor, to generate the assembly instructions for an arithmetic node.
     */
    @Override
    public void generateInstructions()
    {
        this.instructions="";
        if(!globals.constant_folding) {
            //load the right hand side operand onto to stack for usage
            {
                Operand op = this.operands.get(0);
                this.instructions += op.load(this.varTable);
            }

            //load the left hand side operand onto stack for usage
            {
                Operand op = this.operands.get(1);
                this.instructions += op.load(this.varTable);
            }

            this.instructions += this.opcode + "\n";
        }
        else
        {
            String ret = "";
            boolean isConstant1 = false;
            boolean isConstant2 = false;
            //load the right hand side operand onto to stack for usage
            {
                Operand op = this.operands.get(0);
                String inst = op.load(this.varTable);

                if(op.literalConst)
                {
                    isConstant1 = true;
                }

                ret += inst;
            }

            //load the left hand side operand onto stack for usage
            {
                Operand op = this.operands.get(1);
                String inst = op.load(this.varTable);

                if(op.literalConst)
                {
                    isConstant2 = true;
                }

                ret += inst;
            }

            if(isConstant1&&isConstant2)
            {
                //ret="ldc ";

                int result = 0;
                int val1 = Integer.parseInt(this.operands.get(0).value);
                int val2 = Integer.parseInt(this.operands.get(1).value);
                switch (operator)
                {
                    case ("+"):
                    {
                        result = val1+val2;
                        break;
                    }
                    case ("-"):
                    {
                        result = val1-val2;
                        break;
                    }
                    case ("*"):
                    {
                        result = val1*val2;
                        break;
                    }
                    case ("/"):
                    {
                        result = val1/val2;
                        break;
                    }
                    case ("<<"):
                    {
                        result = val1<<val2;
                        break;
                    }
                    case (">>"):
                    {
                        result = val1>>val2;
                        break;
                    }
                    case (">>>"):
                    {
                        result = val1>>>val2;
                        break;
                    }
                    case ("&"):
                    {
                        result = val1&val2;
                        break;
                    }
                    case ("|"):
                    {
                        result = val1|val2;
                        break;
                    }
                    case ("^"):
                    {
                        result = val1^val2;
                        break;
                    }
                }

                Operand resu = new Operand(""+result,false,false,false);
                ret=resu.load(this.varTable);
                //ret+=result+"\n";

            }else
            {
                ret += this.opcode + "\n";
            }
            this.instructions+=ret;
        }

        //the result of the operation is now on top of the stack and any instruction that needs it should use if from there
        if (!storeVar.isGlobal)
        {
            if(storeVar.isArrayAccess)
            {

                int size = this.varTable.varTable.size();
                this.instructions += "istore " + size + "\n";

                int offset = this.varTable.varTable.get(storeVar.value);
                this.instructions += "aload "+offset+"\n";//load array from the stack
                Operand index = storeVar.arrayIndex;
                if(index.isVariable)
                {
                    if(index.isGlobal)
                    {
                        this.instructions += "getstatic "+ControlFlowGraph.publicModuleName+"/"+index.value+" I\n";
                    }else {
                        int offset2 = this.varTable.varTable.get(index.value);
                        this.instructions += "iload " + offset2 + "\n";
                    }
                }else
                {
                    this.instructions += "ldc "+index.value+"\n";
                }

                this.instructions += "iload "+size+"\n";
                this.instructions += "iastore\n";

            }
            else {
                if (this.varTable.varTable.containsKey(storeVar.value)) {
                    int offset = this.varTable.varTable.get(storeVar.value);
                    this.instructions += "istore " + offset + "\n";
                } else {
                    this.varTable.addLocalVariable(this.storeVar.value);
                    int offset = this.varTable.varTable.get(this.storeVar.value);
                    this.instructions += "istore " + offset + "\n";
                }
            }
        } else
        {
            if(storeVar.isArrayAccess)
            {
                int size = this.varTable.varTable.size();
                this.instructions += "istore " + size + "\n";

                this.instructions += "getstatic "+ControlFlowGraph.publicModuleName+"/"+storeVar.value+" [I\n";

                Operand index = storeVar.arrayIndex;
                this.instructions+=index.load(varTable);
                this.instructions += "iload "+size+"\n";
                this.instructions += "iastore\n";
            }else
            {
                this.instructions += "putstatic " + ControlFlowGraph.publicModuleName + "/" + storeVar.value + " I\n";
            }
        }

    }

    /**
     * Inserts this arithmetic node on the parent block. Blocks are used to perform branching on certain conditions.
     */
    @Override
    public void insertOP()
    {
        this.parentBlock.orderedNodelist.add(this);
    }

    /**
     * Returns the uses of this arithmetic node. This is used for liveness analysis.
     * @return A Set of String containing the uses of this arithmetic node.
     */
    @Override
    public Set<String> getUses() {
        Set<String> ret = new HashSet<>();

        ret.addAll(this.operands.get(0).getVariableNames());
        ret.addAll(this.operands.get(1).getVariableNames());

        return ret;
    }

    /**
     * Returns the defs of this arithmetic node. This is used for liveness analysis.
     * @return A Set of String containing the defs of this arithmetic node.
     */
    @Override
    public Set<String> getDefs() {
        Set<String> ret = new HashSet<>();

        ret.addAll(this.storeVar.getVariableNames());

        return ret;
    }

    /**
     * Returns the defs and uses of this arithmetic node. This is used for liveness analysis.
     * @return A Set of String containing the defs and uses of this arithmetic node.
     */
    @Override
    public pair<Set<String>, Set<String>> getDefsAndUses() {
        Set<String> uses = new HashSet<>();
        Set<String> defs = new HashSet<>();

        uses.addAll(this.operands.get(0).getVariableNames());
        uses.addAll(this.operands.get(1).getVariableNames());

        defs.addAll(this.storeVar.getVariableNames());

        return new pair<>(defs,uses);
    }

    /**
     * Generates the assembly instructions for this arithmetic node (by calling the method generateInstructions of this class) and returns a String containing those instructions.
     * @return A String with the generated instructions for this arithmetic node.
     */
    @Override
    public String getInstructions()
    {
        this.generateInstructions();
        return this.instructions;
    }
}
