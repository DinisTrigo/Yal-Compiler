package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WhileBlock extends BasicBlock{

    public BranchBlock  whileBody = null;

    String      comparator;
    Operand     lhs;
    Operand     rhs;

    /**
     * create an while block
     * @param varTable the var table
     * @param parentMethod the parent method
     * @param blockID the block id
     * @param comparator the compartor
     * @param op1 lhs
     * @param op2 rhs
     */
    public WhileBlock(VariableTable varTable, Method parentMethod, String blockID,String comparator,Operand op1,Operand op2) {
        super(varTable, parentMethod, blockID);

        this.comparator = comparator;
        this.lhs = op1;
        this.rhs = op2;
        whileBody = new BranchBlock(this.varTable,this.parentMethod,this.blockID,this);

        this.in = new ArrayList<>();
        this.in.add(new HashSet<>());
        this.out = new ArrayList<>();
        this.out.add(new HashSet<>());
    }

    /**
     * get the while body
     * @return the body block
     */
    public BasicBlock getWhileBody()
    {
        return this.whileBody;
    }

    /**
     * add a block to the body
     * @param block block
     */
    public void addBlock(BasicBlock block)
    {
        this.whileBody.addBlock(block);
    }

    /**
     * add a node
     * @param node the node to be added to the block
     */
    public void addNode(IRNode node)
    {
        this.whileBody.addNode(node);
    }

    /**
     * get the comparator opcode
     * @return the comparator opcode
     */
    private String getComparatorOpcode(){
        String ret = "";
        switch (this.comparator)
        {
            case("<"):
            {
                ret = "if_icmplt";
                break;
            }
            case(">"):
            {
                ret = "if_icmpgt";
                break;
            }
            case("<="):
            {
                ret = "if_icmple";
                break;
            }
            case(">="):
            {
                ret = "if_icmpge";
                break;
            }
            case("=="):
            {
                if(lhs.isArray)
                {
                    ret = "if_acmpeq";
                }else
                {
                    ret = "if_icmpeq";
                }
                break;
            }
            case("!="):
            {
                if(lhs.isArray)
                {
                    ret = "if_acmpne";
                }else
                {
                    ret = "if_icmpne";
                }
                break;
            }
            default:break;
        }
        return ret;
    }

    /**
     * get the instructions that make up the while
     * @return the instructions
     */
    @Override
    public String getInstructions()
    {
        String ret="";
        ret+=this.blockID+"loop:\n";

        {
            Operand op = lhs;
            ret+=op.load(this.varTable);
        }

        {
            Operand op = rhs;
            ret+=op.load(this.varTable);
        }
        //load varaibles on the stack

        ret+=this.getComparatorOpcode()+" "+this.whileBody.blockID+"branch\n";
        ret+="goto "+this.nextBlock.blockID+"\n";

        //the body of the while
        ret+=this.whileBody.blockID+"branch:\n";
        ret+=this.whileBody.getInstructions();

        {
            Operand op = lhs;
            ret+=op.load(this.varTable);
        }

        {
            Operand op = rhs;
            ret+=op.load(this.varTable);
        }
        //load varaibles on the stack
        ret+=this.getComparatorOpcode()+" "+this.whileBody.blockID+"branch\n";

        return ret;
    }

    /**
     * the uses
     * @return uses
     */
    @Override
    public ArrayList<Set<String>> getUse()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();

        Set<String> initialVariables = new HashSet<>();
        initialVariables.addAll(this.lhs.getVariableNames());
        initialVariables.addAll(this.rhs.getVariableNames());

        ret.add(initialVariables);
        ret.addAll(this.whileBody.getUse());

        return ret;
    }

    /**
     * the defs
     * @return defs
     */
    @Override
    public ArrayList<Set<String>> getDef()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();

        ret.add(new HashSet<>());
        ret.addAll(this.whileBody.getDef());

        return ret;
    }

    /**
     * the defs and uses
     * @return defs and uses
     */
    @Override
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> getDefAndUse()
    {
        ArrayList<Set<String>> uses = new ArrayList<>();
        ArrayList<Set<String>> defs = new ArrayList<>();

        Set<String> initialVariables = new HashSet<>();
        initialVariables.addAll(this.lhs.getVariableNames());
        initialVariables.addAll(this.rhs.getVariableNames());

        uses.add(initialVariables);
        defs.add(new HashSet<>());

        pair<ArrayList<Set<String>>,ArrayList<Set<String>>> bodyVariables = whileBody.getDefAndUse();
        defs.addAll(bodyVariables.getFirst());
        uses.addAll(bodyVariables.getSecond());

        return new pair<>(defs,uses);
    }

    /**
     * set the next block
     * @param block the block
     */
    @Override
    public void setNextBlock(BasicBlock block)
    {
        this.whileBody.setNextBlock(block);
        this.nextBlock = block;
    }

    /**
     * execute liveness analysis
     * @param hasChanged a variable that contains wether or not the liveness should go onto the next generation
     * @param defuses the uses and defs for this block
     * @return the in and out sets
     */
    @Override
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> livenessAnalysis(Boolean hasChanged,pair<ArrayList<Set<String>>,ArrayList<Set<String>>>defuses)
    {
        pair<ArrayList<Set<String>>,ArrayList<Set<String>>> bodyinout = this.whileBody.livenessAnalysis(hasChanged,this.whileBody.getDefAndUse());

        Set<String>compin = new HashSet<>();
        Set<String>compout = new HashSet<>();

        compin.addAll(this.lhs.getVariableNames());
        compin.addAll(this.rhs.getVariableNames());

        compout.addAll(bodyinout.getFirst().get(0));

        Set<String>copy = new HashSet<>(compout);
        copy.removeAll(defuses.getFirst().get(0));
        compin.addAll(copy);

        if(this.nextBlock!=null)
        {
            BasicBlock next = this.nextBlock;
            if(next instanceof WhileBlock)
            {
                compout.addAll(next.in.get(0));
            }
            else if(next instanceof IfBlock)
            {
                compout.addAll(next.in.get(0));
            }
            else
            {
                if(next.orderedNodelist.size()==0)
                {
                    if(next.nextBlock!=null)
                    {
                        //is a dummy block
                        next = next.nextBlock;
                        compout.addAll(next.in.get(0));
                    }
                    else
                    {
                        //is last block and is empty
                        boolean changed;
                        if(this.parentMethod.returnVar!=null) {
                            changed = compout.addAll(this.parentMethod.returnVar.getVariableNames());
                            if (changed) {
                                hasChanged = true;
                            }
                        }
                    }
                }
                else
                {
                    compout.addAll(next.in.get(0));
                }
            }
        }

        /*
        BasicBlock next = this.nextBlock;
        if(next.orderedNodelist.size()==0)
        {
            if(next.nextBlock!=null)
            {
                //was a dummy block
                next = next.nextBlock;
                compout.addAll(next.in.get(0));
            }
            else
            {
                //was last block and is empty
                boolean changed;
                if(this.parentMethod.returnVar!=null) {
                    changed = compout.addAll(this.parentMethod.returnVar.getVariableNames());
                    if (changed) {
                        hasChanged = true;
                    }
                }
            }
        }*/

        //compin.addAll(compout);

        boolean changed;
        changed = this.in.get(0).addAll(compin);
        if(changed)
        {
            hasChanged = true;
        }
        changed = this.out.get(0).addAll(compout);
        if(changed)
        {
            hasChanged = true;
        }

        if(this.in.size()==1 && bodyinout.getSecond().size()>0)
        {
            hasChanged = true;
            for(int i=0;i<bodyinout.getSecond().size();++i)
            {
                this.in.add(new HashSet<>());
                this.out.add(new HashSet<>());
            }
        }

        for(int i=0;i<bodyinout.getFirst().size();++i)
        {
            changed =this.in.get(i+1).addAll(bodyinout.getFirst().get(i));
            if(changed)
            {
                hasChanged = true;
            }
            changed =this.out.get(i+1).addAll(bodyinout.getSecond().get(i));
            if(changed)
            {
                hasChanged = true;
            }
        }

        return new pair<>(this.in,this.out);
    }

    /**
     * set the variable table
     * @param table the variable table
     */
    @Override
    public void setVarTable(VariableTable table)
    {
        this.varTable = table;
        this.whileBody.setVarTable(table);
    }

}
