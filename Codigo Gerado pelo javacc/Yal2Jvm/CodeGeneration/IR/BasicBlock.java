package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.globals;
import Yal2Jvm.Utils.pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BasicBlock
{

    public String blockID;
    public BasicBlock parent = null;
    public Method parentMethod;
    public VariableTable varTable;
    public ArrayList<IRNode> orderedNodelist;
    public BasicBlock nextBlock = null;
    public boolean hasBranch = false;
    //public BasicBlock nextFalseBlock = null;

    //ins and outs
    protected ArrayList<Set<String>> in;
    protected ArrayList<Set<String>> out;

    /**
     * Creates a Basic block of the IR
     * @param varTable the variable table
     * @param parentMethod tha parent method of this block
     * @param blockID the block if for the block
     */
    public BasicBlock(VariableTable varTable, Method parentMethod, String blockID)
    {
        this.orderedNodelist = new ArrayList<>();
        this.blockID = blockID;
        this.varTable = varTable;
        this.parentMethod = parentMethod;

        this.in = new ArrayList<>();
        this.out = new ArrayList<>();
    }

    /**
     * Adds a node to the basic block, a node represent a single operation
     * @param node the node to be added to the block
     */
    public void addNode(IRNode node)
    {
        node.parentBlock = this;
        this.orderedNodelist.add(node);

        this.in.add(new HashSet<>());
        this.out.add(new HashSet<>());
    }

    /**
     * obtain the jvm instructions that make up the block
     * @return the jvm instructions that makes up the basic block
     */
    public String getInstructions()
    {
        String ret = blockID + ":\n";
        for (int i = 0; i < orderedNodelist.size(); ++i)
        {
            if(globals.constant_propagation)
            {
                if(i!=orderedNodelist.size()-1) {
                    boolean canChange = true;
                    IRNode thisnode = orderedNodelist.get(i);

                    for (int k = i + 1; k < orderedNodelist.size(); ++k) {
                        IRNode nextNode = orderedNodelist.get(k);

                        if(!canChange)
                        {
                            break;
                        }

                        if (thisnode instanceof AssignNode) {
                            if(thisnode.operands.get(1).value!=null)
                            {
                                if (thisnode.operands.get(1).value.equals("invokestatic io/read()I\n")) {
                                    break;
                                }
                            }
                            if(thisnode.operands.get(1).isMethod)
                            {
                                break;
                            }
                            if(thisnode.operands.get(1).isInstructions)
                            {
                                break;
                            }
                            if (nextNode instanceof ArithNode) {
                                if (thisnode.operands.get(0).equals(nextNode.operands.get(0))) {
                                    nextNode.operands.set(0, thisnode.operands.get(1));
                                }
                                if (thisnode.operands.get(0).equals(nextNode.operands.get(1))) {
                                    nextNode.operands.set(1, thisnode.operands.get(1));
                                }
                            } else if (nextNode instanceof CallNode) {
                                if (((CallNode) nextNode).isInstructions) {
                                    ArrayList<Operand> args = ((CallNode) nextNode).instructionsOperand.args;
                                    for (int j = 0; j < args.size(); ++j) {
                                        if (args.get(j).equals(thisnode.operands.get(0))) {
                                            args.set(j, thisnode.operands.get(1));
                                        }
                                    }
                                } else {
                                    for (int j = 0; j < ((CallNode) nextNode).args.size(); ++j) {
                                        if (thisnode.operands.get(0).equals(((CallNode) nextNode).args.get(j))) {
                                            ((CallNode) nextNode).args.set(j, thisnode.operands.get(1));
                                        }
                                    }
                                }
                            } else if (nextNode instanceof AssignNode) {
                                if (thisnode.operands.get(0).equals(nextNode.operands.get(1))) {
                                    nextNode.operands.set(1, thisnode.operands.get(1));
                                }
                                if(nextNode.operands.get(0).equals(thisnode.operands.get(0)))
                                {
                                    canChange = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                ret += orderedNodelist.get(i).getInstructions();
            }
            else
            {
                ret += orderedNodelist.get(i).getInstructions();
            }
        }
        return ret;
    }

    /**
     * sets the block that comes before this one
     * @param block the basic block
     */
    public void setParentBlock(BasicBlock block)
    {
        this.parent = block;
    }

    /**
     * sets the block that comes after this one
     * @param block the block
     */
    public void setNextBlock(BasicBlock block)
    {
        this.nextBlock = block;
        this.nextBlock.parent = this;
    }

    ///code for uses and defs

    /**
     * get the defined variables in this block
     * @return the defs of this block
     */
    public ArrayList<Set<String>> getDef()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();
        for(IRNode node:this.orderedNodelist)
        {
            ret.add(node.getDefs());
        }
        return ret;
    }

    /**
     * returns the used variables of this block
     * @return the used variables
     */
    public ArrayList<Set<String>> getUse()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();
        for(IRNode node:this.orderedNodelist)
        {
            ret.add(node.getUses());
        }
        return ret;
    }

    /**
     * obtains the defs and uses for this block
     * @return the uses and defs
     */
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> getDefAndUse()
    {
        ArrayList<Set<String>> defs = new ArrayList<>();
        ArrayList<Set<String>> uses = new ArrayList<>();
        for(IRNode node:this.orderedNodelist)
        {
            defs.add(node.getDefs());
            uses.add(node.getUses());
        }
        return new pair<>(defs,uses);
    }

    //code for liveness analysis

    /**
     * executes liveness analysis for this block
     * @param hasChanged a variable that contains wether or not the liveness should go onto the next generation
     * @param defuses the uses and defs for this block
     * @return the in and out variables for this block
     */
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> livenessAnalysis(Boolean hasChanged,pair<ArrayList<Set<String>>,ArrayList<Set<String>>>defuses)
    {
        if(this.orderedNodelist.size()!=0)
        {
            for (int i = this.orderedNodelist.size()-1;i>=0;--i)
            {
                if(i==(this.orderedNodelist.size()-1))
                {
                    if(this.nextBlock!=null)
                    {
                        //is in a block boundry section

                        BasicBlock next = this.nextBlock;

                        if(next instanceof WhileBlock)
                        {
                            boolean changed;
                            changed = this.out.get(i).addAll(next.in.get(0));
                            if(changed)
                            {
                                hasChanged = true;
                            }
                        }
                        else if(next instanceof IfBlock)
                        {
                            boolean changed;
                            changed = this.out.get(i).addAll(next.in.get(0));
                            if(changed)
                            {
                                hasChanged = true;
                            }
                        }
                        else
                        {
                            if(next.orderedNodelist.size()==0)
                            {
                                if(next.nextBlock!=null)
                                {
                                    next = next.nextBlock;
                                    boolean changed;
                                    if(next.in.size()!=0) {
                                        changed = this.out.get(i).addAll(next.in.get(0));
                                        if (changed) {
                                            hasChanged = true;
                                        }
                                    }
                                }
                                else
                                {
                                    //is the final block and is empty
                                    boolean changed;
                                    if(this.parentMethod.returnVar!=null)
                                    {
                                        changed = this.out.get(i).addAll(this.parentMethod.returnVar.getVariableNames());
                                        if (changed) {
                                            hasChanged = true;
                                        }
                                    }

                                    if(this.parent instanceof BranchBlock)
                                    {
                                        if(this.parent.parent instanceof WhileBlock)
                                        {
                                            WhileBlock parent = (WhileBlock) this.parent.parent;
                                            changed = this.out.get(i).addAll(parent.getUse().get(0));
                                            if (changed) {
                                                hasChanged = true;
                                            }
                                            changed = this.out.get(i).addAll(parent.in.get(0));
                                            if (changed) {
                                                hasChanged = true;
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                //regular next block
                                boolean changed;
                                changed = this.out.get(i).addAll(next.in.get(0));
                                if(changed)
                                {
                                    hasChanged = true;
                                }
                            }
                        }

                        boolean changed;
                        changed = this.in.get(i).addAll(defuses.getSecond().get(i));

                        if(changed)
                        {
                            hasChanged = true;
                        }

                        Set<String> outcopy = new HashSet<>(this.out.get(i));
                        outcopy.removeAll(defuses.getFirst().get(i));

                        changed = in.get(i).addAll(outcopy);
                        if(changed)
                        {
                            hasChanged = true;
                        }

                    }
                    else
                    {
                        //is the last node of the method
                        boolean changed;
                        if(parentMethod.returnVar!=null) {
                            changed = this.out.get(i).addAll(this.parentMethod.returnVar.getVariableNames());
                            if(changed)
                            {
                                hasChanged = true;
                            }
                        }


                        changed = this.in.get(i).addAll(defuses.getSecond().get(i));

                        if(changed)
                        {
                            hasChanged = true;
                        }

                        Set<String> outcopy = new HashSet<>(this.out.get(i));
                        outcopy.removeAll(defuses.getFirst().get(i));

                        changed = in.get(i).addAll(outcopy);
                        if(changed)
                        {
                            hasChanged = true;
                        }

                    }
                }
                else
                {
                    boolean changed;
                    changed = this.out.get(i).addAll(this.in.get(i+1));

                    if(changed)
                    {
                        hasChanged = true;
                    }

                    changed = this.in.get(i).addAll(defuses.getSecond().get(i));

                    if(changed)
                    {
                        hasChanged = true;
                    }

                    Set<String> outcopy = new HashSet<>(this.out.get(i));
                    outcopy.removeAll(defuses.getFirst().get(i));

                    changed = in.get(i).addAll(outcopy);
                    if(changed)
                    {
                        hasChanged = true;
                    }
                }
            }
        }
        return new pair<>(this.in,this.out);
    }

    /**
     * sets the vartable for this block
     * @param table the variable table
     */
    public void setVarTable(VariableTable table)
    {
        this.varTable = table;
        for(IRNode node:this.orderedNodelist)
        {
            node.setVarTable(table);
        }
    }

}
