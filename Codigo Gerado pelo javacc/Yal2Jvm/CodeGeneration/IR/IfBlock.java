package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IfBlock extends BasicBlock {

    public String comparator;
    public Operand lhs;
    public Operand rhs;

    public BranchBlock trueBlock = null;
    public BranchBlock falseBlock = null;

    /**
     * create an if block
     * @param varTable the variable table
     * @param parentMethod the parent method
     * @param blockID the block id
     * @param comparison the comparison operator
     * @param op1 the lhs operator
     * @param op2 the rhs operator
     * @param hasFalseBranch if the if has a false branch
     */
    public IfBlock(VariableTable varTable, Method parentMethod, String blockID, String comparison, Operand op1, Operand op2, boolean hasFalseBranch) {
        super(varTable, parentMethod, blockID);

        this.comparator = comparison;
        this.varTable = varTable;
        this.parentMethod = parentMethod;
        this.blockID = blockID;
        this.lhs = op1;
        this.rhs = op2;

        this.trueBlock = new BranchBlock(this.varTable, this.parentMethod, this.blockID + "true", this);
        if (hasFalseBranch) {
            this.falseBlock = new BranchBlock(this.varTable, this.parentMethod, this.blockID + "false", this);
        }

        this.in = new ArrayList<>();
        this.in.add(new HashSet<>());//hash set for the comparison in
        this.out = new ArrayList<>();
        this.out.add(new HashSet<>());//hash set for the comparison out
    }

    /**
     * obtain the true branch
     * @return the true branch
     */
    public BasicBlock getTrueBranch() {
        return this.trueBlock;
    }

    /**
     * ontain the false branch
     * @return the false branch
     */
    public BasicBlock getFalseBranch() {
        return this.falseBlock;
    }

    /**
     * add a node to the true branch
     * @param node the node
     */
    public void addNodeToTrueBranch(IRNode node) {
        this.trueBlock.addNode(node);
    }

    /**
     * add the node to the false branch
     * @param node the node
     */
    public void addNodeToFalseBranch(IRNode node) {
        this.falseBlock.addNode(node);
    }

    /**
     * add block to the true branch
     * @param block the block
     */
    public void addBlockToTrueBranch(BasicBlock block) {
        this.trueBlock.addBlock(block);
    }

    /**
     * add a block to the false branch
     * @param block the block
     */
    public void addBlockToFalseBranch(BasicBlock block) {
        this.falseBlock.addBlock(block);
    }

    /**
     * adds a node to the selected branch
     * @param node the node to add
     * @param branch 0 - false branch 1 -true branch
     */
    public void addNode(IRNode node, boolean branch) {
        if (branch) {
            this.trueBlock.addNode(node);
        } else {
            this.falseBlock.addNode(node);
        }
    }

    /**
     * add a node the to true branch
     * @param node the node to be added to the block
     */
    @Override
    public void addNode(IRNode node) {
        this.trueBlock.addNode(node);
    }

    /**
     * add a block the the branch
     * @param block the block to add
     * @param branch the branch to add to
     */
    public void addBranch(BasicBlock block, boolean branch) {
        if (branch) {
            this.trueBlock.addBlock(block);
        } else {
            this.falseBlock.addBlock(block);
        }
    }

    /**
     * add a block to the true branch
     * @param block the block to add
     */
    public void addBranch(BasicBlock block) {
        this.trueBlock.addBlock(block);
    }

    /**
     * sets the end block of the if
     * @param block the blocks
     */
    public void setIfEndBlock(BasicBlock block) {
        this.nextBlock = block;
        this.trueBlock.nextBlock = block;
        if (this.falseBlock != null) {
            this.falseBlock.nextBlock = block;
        }
        this.parentMethod.addBlock(block);
    }

    /**
     * obtain the opcode for the supplied comparator
     * @return the opcode of the comparator
     */
    private String getComparatorOpcode() {
        String ret = "";
        switch (this.comparator) {
            case ("<"): {
                ret = "if_icmplt";
                break;
            }
            case (">"): {
                ret = "if_icmpgt";
                break;
            }
            case ("<="): {
                ret = "if_icmple";
                break;
            }
            case (">="): {
                ret = "if_icmpge";
                break;
            }
            case ("=="): {
                if (lhs.isArray) {
                    ret = "if_acmpeq";
                } else {
                    ret = "if_icmpeq";
                }
                break;
            }
            case ("!="): {
                if (lhs.isArray) {
                    ret = "if_acmpne";
                } else {
                    ret = "if_icmpne";
                }
                break;
            }
            default:
                break;
        }
        return ret;
    }

    /**
     * obtain the instructions of the if block
     * @return the instructions
     */
    @Override
    public String getInstructions() {
        String ret = "" + this.blockID + ":\n";
        {
            Operand op = lhs;
            ret += op.load(this.varTable);
        }

        {
            Operand op = rhs;
            ret += op.load(this.varTable);
        }
        //load the variables on the stack

        //get the opcode
        ret += this.getComparatorOpcode() + " " + this.trueBlock.blockID + "\n";

        if (this.falseBlock != null) {
            //generate code for the false blocK{
            //ret += this.falseBlock.blockID + ":\n";
            ret += this.falseBlock.getInstructions();
            ret += "goto " + this.nextBlock.blockID + "\n";
        } else {
            ret += "goto " + this.nextBlock.blockID + "\n";
        }

        //generate code for the true block
        ret += this.trueBlock.blockID + ":\n";
        ret += this.trueBlock.getInstructions();

        return ret;
    }

    /**
     * get the uses
     * @return the uses
     */
    @Override
    public ArrayList<Set<String>> getUse() {
        ArrayList<Set<String>> ret = new ArrayList<>();

        Set<String> initialVariables = new HashSet<>();
        initialVariables.addAll(this.lhs.getVariableNames());
        initialVariables.addAll(this.rhs.getVariableNames());

        ret.add(initialVariables);
        ret.addAll(this.trueBlock.getUse());
        if (this.falseBlock != null) {
            ret.addAll(this.falseBlock.getUse());
        }

        return ret;
    }

    /**
     * get the defs
     * @return the defs
     */
    @Override
    public ArrayList<Set<String>> getDef() {
        ArrayList<Set<String>> ret = new ArrayList<>();

        ret.add(new HashSet<>());
        ret.addAll(this.trueBlock.getDef());
        if (this.falseBlock != null) {
            ret.addAll(this.falseBlock.getDef());
        }

        return ret;
    }

    /**
     * get the defs and uses
     * @return the defs and uses
     */
    @Override
    public pair<ArrayList<Set<String>>, ArrayList<Set<String>>> getDefAndUse() {
        ArrayList<Set<String>> uses = new ArrayList<>();
        ArrayList<Set<String>> defs = new ArrayList<>();

        Set<String> initialVariables = new HashSet<>();
        initialVariables.addAll(this.lhs.getVariableNames());
        initialVariables.addAll(this.rhs.getVariableNames());

        uses.add(initialVariables);
        defs.add(new HashSet<>());

        pair<ArrayList<Set<String>>, ArrayList<Set<String>>> bodyVariables = trueBlock.getDefAndUse();
        defs.addAll(bodyVariables.getFirst());
        uses.addAll(bodyVariables.getSecond());

        if (this.falseBlock != null) {
            bodyVariables = falseBlock.getDefAndUse();
            defs.addAll(bodyVariables.getFirst());
            uses.addAll(bodyVariables.getSecond());
        }

        return new pair<>(defs, uses);
    }

    /**
     * set the next block
     * @param block the block
     */
    @Override
    public void setNextBlock(BasicBlock block) {
        this.trueBlock.setNextBlock(block);
        if (this.falseBlock != null) {
            this.falseBlock.setNextBlock(block);
        }
        this.nextBlock = block;
    }

    /**
     * execure the liveness anlisys
     * @param hasChanged a variable that contains wether or not the liveness should go onto the next generation
     * @param defuses the uses and defs for this block
     * @return the in and out variables
     */
    @Override
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> livenessAnalysis(Boolean hasChanged,pair<ArrayList<Set<String>>,ArrayList<Set<String>>>defuses)
    {
        pair<ArrayList<Set<String>>,ArrayList<Set<String>>> falseinout = new pair<>(new ArrayList<>(),new ArrayList<>());
        pair<ArrayList<Set<String>>,ArrayList<Set<String>>> trueinout = new pair<>(new ArrayList<>(),new ArrayList<>());

        //rebuild the in out sets
        if(this.falseBlock!=null)
        {
            falseinout = this.falseBlock.livenessAnalysis(hasChanged,this.falseBlock.getDefAndUse());
        }
        trueinout = this.trueBlock.livenessAnalysis(hasChanged,this.trueBlock.getDefAndUse());

        Set<String>compout = new HashSet<>();
        Set<String>compin = new HashSet<>();

        compin.addAll(lhs.getVariableNames());
        compin.addAll(rhs.getVariableNames());

        compout.addAll(this.trueBlock.in.get(0));
        if(this.falseBlock!=null)
        {
            compout.addAll(this.falseBlock.in.get(0));
        }
        else
        {
            BasicBlock next = this.nextBlock;
            if(next instanceof WhileBlock)
            {
                compout.addAll(this.nextBlock.in.get(0));
            }
            else if(next instanceof IfBlock)
            {
                compout.addAll(this.nextBlock.in.get(0));
            }
            else
            {
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
                }
                else
                {
                    //normal block
                    compout.addAll(next.in.get(0));
                }
            }
        }

        BasicBlock next = this.nextBlock;
        if(next.orderedNodelist.size()==0)
        {
            if(next.nextBlock!=null)
            {
                //was a dummy block
                next = next.nextBlock;
                if(next.in.size()!=0) {
                    compout.addAll(next.in.get(0));
                }
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
        }

        compin.addAll(compout);

        //add new sets and check for changes
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

        if(this.in.size()==1)
        {
            hasChanged=true;

            //add the ins of the true block
            this.in.addAll(trueinout.getFirst());

            //add the outs of the true block
            this.out.addAll(trueinout.getSecond());

            if(this.falseBlock!=null) {
                //add the ins for the false block
                this.in.addAll(falseinout.getFirst());

                //add the outs for the false block
                this.out.addAll(falseinout.getSecond());
            }

        }
        else
        {
            //add the true block in and out
            for(int i=0;i<trueinout.getFirst().size();++i)
            {
                changed = this.in.get(i+1).addAll(trueinout.getFirst().get(i));
                if(changed)
                {
                    hasChanged = true;
                }
                changed = this.out.get(i+1).addAll(trueinout.getSecond().get(i));
                if(changed)
                {
                    hasChanged = true;
                }
            }

            //add the false block in and out
            for(int i=0;i<falseinout.getFirst().size();++i)
            {
                changed = this.in.get(i+1+trueinout.getFirst().size()).addAll(falseinout.getFirst().get(i));
                if(changed)
                {
                    hasChanged = true;
                }
                changed = this.out.get(i+1+trueinout.getFirst().size()).addAll(falseinout.getSecond().get(i));
                if(changed)
                {
                    hasChanged = true;
                }
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
        this.trueBlock.setVarTable(varTable);
        if(this.falseBlock!=null)
        {
            this.falseBlock.setVarTable(varTable);
        }
    }
}
