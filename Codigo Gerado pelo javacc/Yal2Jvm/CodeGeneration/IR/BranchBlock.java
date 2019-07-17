package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BranchBlock extends BasicBlock {

    public ArrayList<BasicBlock>innerBlocks;

    /**
     * creates a branch block
     * @param varTable the variable table
     * @param parentMethod the parent method
     * @param blockID the block id
     * @param parent the parent block
     */
    public BranchBlock(VariableTable varTable, Method parentMethod, String blockID, BasicBlock parent) {
        super(varTable, parentMethod, blockID);
        this.parent = parent;
        this.innerBlocks = new ArrayList<>();

        this.in = new ArrayList<>();
        this.out = new ArrayList<>();
    }

    /**
     * ads a block to this block
     * @param block the block
     */
    public void addBlock(BasicBlock block)
    {
        if(this.innerBlocks.size()!=0)
        {
            this.innerBlocks.get(this.innerBlocks.size()-1).setNextBlock(block);
        }
        this.innerBlocks.add(block);
    }

    /**
     * adds a node to the last block of this block
     * @param node the node to be added to the block
     */
    @Override
    public void addNode(IRNode node)
    {
        if(this.innerBlocks.size()==0)
        {
            this.innerBlocks.add(new BasicBlock(this.varTable,this.parentMethod,Method.generateBlockID()));
        }
        this.innerBlocks.get(this.innerBlocks.size()-1).addNode(node);
    }

    /**
     * obtain the jvm instruction that make up this block
     * @return the instructions of this block
     */
    @Override
    public String getInstructions()
    {
        String ret="";
        for(int i=0;i<this.innerBlocks.size();++i)
        {
            ret+=this.innerBlocks.get(i).getInstructions();
        }

        return ret;
    }


    /**
     * obtains the uses of this block
     * @return the used variables of this block
     */
    @Override
    public ArrayList<Set<String>> getUse()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();

        for(BasicBlock block:this.innerBlocks)
        {
            ret.addAll(block.getUse());
        }

        return ret;
    }

    /**
     * the defs of this block
     * @return the defs of this block
     */
    @Override
    public ArrayList<Set<String>> getDef()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();

        for(BasicBlock block:this.innerBlocks)
        {
            ret.addAll(block.getDef());
        }

        return ret;
    }

    /**
     * obtains the defined and used variables of this block
     * @return the defs and used varaibles
     */
    @Override
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> getDefAndUse()
    {
        ArrayList<Set<String>> uses = new ArrayList<>();
        ArrayList<Set<String>> defs = new ArrayList<>();

        for(BasicBlock block:this.innerBlocks)
        {
            uses.addAll(block.getUse());
            defs.addAll(block.getDef());
        }

        return new pair<>(defs,uses);
    }

    /**
     * performs liveness analysis for this block
     * @param hasChanged a variable that contains wether or not the liveness should go onto the next generation
     * @param defuses the uses and defs for this block
     * @return
     */
    @Override
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> livenessAnalysis(Boolean hasChanged,pair<ArrayList<Set<String>>,ArrayList<Set<String>>>defuses)
    {
        ArrayList<Set<String>> ins = new ArrayList<>();
        ArrayList<Set<String>> outs = new ArrayList<>();

        if(this.innerBlocks.size()!=0)
        {
            for (int i = this.innerBlocks.size()-1;i>=0;--i)
            {
                pair<ArrayList<Set<String>>,ArrayList<Set<String>>> ret = this.innerBlocks.get(i).livenessAnalysis(hasChanged,this.innerBlocks.get(i).getDefAndUse());
                if(this.parent instanceof WhileBlock)
                {
                    ret.getSecond().get(ret.getSecond().size()-1).addAll(this.parent.in.get(0));
                    ret.getFirst().get(ret.getFirst().size()-1).addAll(ret.getSecond().get(ret.getSecond().size()-1));
                }
                for(int j=0;j<ret.getFirst().size();++j)
                {
                    ins.add(ret.getFirst().get(j));
                    outs.add(ret.getSecond().get(j));
                }
            }
        }

        //check that the sizes matter
        int delta = Math.abs(this.in.size()-ins.size());
        if(delta!=0)
        {
            hasChanged = true;
            for(int i=0;i<delta;++i)
            {
                this.in.add(new HashSet<>());
                this.out.add(new HashSet<>());
            }
        }

        //add the new sets to the existing ones and check for changes
        if(ins.size()!=0)
        {
            for (int i = ins.size() - 1; i >= 0; --i)
            {
                boolean changed;
                changed = this.in.get(i).addAll(ins.get(i));

                if(changed)
                {
                    hasChanged = true;
                }

                changed = this.out.get(i).addAll(outs.get(i));

                if(changed)
                {
                    hasChanged = true;
                }
            }
        }

        return new pair<>(this.in,this.out);
    }

    /**
     * sets the next block
     * @param block the block
     */
    @Override
    public void setNextBlock(BasicBlock block)
    {
        if(this.innerBlocks.size()!=0)
        {
            this.innerBlocks.get(this.innerBlocks.size()-1).setNextBlock(block);
        }
        this.nextBlock = block;
    }

    /**
     * sets the variable table for this block
     * @param table the variable table
     */
    @Override
    public void setVarTable(VariableTable table)
    {
        this.varTable = table;
        for(BasicBlock block:this.innerBlocks)
        {
            block.setVarTable(table);
        }
    }

}
