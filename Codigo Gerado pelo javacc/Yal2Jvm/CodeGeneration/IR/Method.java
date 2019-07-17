package Yal2Jvm.CodeGeneration.IR;

import Yal2Jvm.Utils.pair;

import java.util.*;

public class Method implements IRContainer
{
    public static int blockCounter = 0;

    public VariableTable globalVariables;
    public VariableTable localVariables;
    public String methodName;
    public ArrayList<BasicBlock> blocks;
    public ArrayList<String> methodArgs;
    public Boolean returnIsArray;
    public Operand returnVar;
    public ControlFlowGraph parentCFG;

    /**
     * create a method
     * @param globals the globbals
     * @param methodName method name
     * @param methodArgs method args
     * @param isArray method retun type
     * @param returnVar method return var
     * @param parentCFG parent CFG
     */
    public Method(VariableTable globals, String methodName, ArrayList<String> methodArgs, Boolean isArray, Operand returnVar, ControlFlowGraph parentCFG)
    {
        this(globals, methodName, methodArgs, isArray, parentCFG);
        this.returnVar = returnVar;
        this.localVariables.addLocalVariable(returnVar.value);
    }

    /**
     * create a method
     * @param globals the globals
     * @param methodName method name
     * @param methodArgs method args
     * @param isArray return is array
     * @param parentCFG parent CFG
     */
    public Method(VariableTable globals, String methodName, ArrayList<String> methodArgs, Boolean isArray, ControlFlowGraph parentCFG)
    {
        this.returnIsArray = isArray;
        this.globalVariables = globals;
        this.localVariables = new VariableTable();
        this.methodName = methodName;
        this.methodArgs = new ArrayList<>();

        if (this.methodName.equals("main"))
        {
            this.localVariables.addLocalVariable("");
            this.methodArgs.add("[Ljava/lang/String;");
        }
        for (String arg : methodArgs)
        {
            this.methodArgs.add(arg);
        }

        this.blocks = new ArrayList<>();
        this.blocks.add(new BasicBlock(this.localVariables, this, "B1"));
        this.parentCFG = parentCFG;
    }

    /**
     * add a node
     * @param node node
     */
    public void addNode(IRNode node)
    {
        this.blocks.get(this.blocks.size() - 1).addNode(node);
    }

    /**
     * add local var
     * @param id id
     */
    public void addLocalVariable(String id)
    {
        this.localVariables.addLocalVariable(id);
    }

    /**
     * add global var
     * @param id id
     */
    public void addGlobalVariable(String id)
    {
        this.globalVariables.addLocalVariable(id);
    }

    /**
     * add block
     * @param block block
     */
    public void addBlock(BasicBlock block)
    {
        block.parentMethod = this;
        if(this.blocks.size()!=0)
        {
            this.blocks.get(this.blocks.size()-1).setNextBlock(block);
        }
        this.blocks.add(block);
    }

    /**
     * get the instructions
     * @return instructions
     */
    public String getInstructions()
    {
        String ret = ".method public static " + this.methodName + "(";
        for (int i = 0; i < methodArgs.size(); ++i)
        {
            ret += methodArgs.get(i) + "";
        }
        ret += ")";

        if (this.returnIsArray == null)
        {
            ret += "V\n";
        } else if (this.returnIsArray == true)
        {
            ret += "[I\n";
        } else if (this.returnIsArray == false)
        {
            ret += "I\n";
        }

        //placeholder limits
        ret += ".limit stack "+(this.localVariables.varTable.size()*5+10)+"\n";
        ret += ".limit locals "+(this.localVariables.varTable.size()*5+10)+"\n";
        //end of placeholder limits

        for (int i = 0; i < blocks.size(); ++i)
        {
            ret += blocks.get(i).getInstructions();
        }

        //put the return var on top of the stack for return
        if (this.returnVar != null)
        {
            if(this.returnVar.isArray)
            {
                ret+="aload "+this.localVariables.varTable.get(this.returnVar.value)+"\n";
            }else if(this.returnVar.isArrayAccess)
            {
                //load the reference on the stack
                ret+="aload "+this.localVariables.varTable.get(this.returnVar.value)+"\n";

                //load the index on the stack
                Operand index = returnVar.arrayIndex;
                ret+=index.load(localVariables);

                //load the value from the array
                ret+="iaload\n";
            }else {
                int offset = this.localVariables.varTable.get(this.returnVar.value);
                ret += "iload " + offset + "\n";
            }
        }
        if (this.returnIsArray == null)
        {
            ret += "return\n";
        } else if (this.returnIsArray == true)
        {
            ret += "areturn\n";
        } else if (this.returnIsArray == false)
        {
            ret += "ireturn\n";
        }
        ret += ".end method\n\n";
        return ret;
    }

    /**
     * set the return operator
     * @param returnVar the operator
     */
    public void setReturnVar(Operand returnVar)
    {
        this.returnVar = returnVar;
    }

    /**
     * generate a block id
     * @return block id
     */
    public static String generateBlockID()
    {
        return ("block"+blockCounter++);
    }

    /**
     * get the defs
     * @return defs
     */
    public ArrayList<Set<String>> getDef()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();
        for(BasicBlock block:this.blocks)
        {
            ArrayList<Set<String>>defs = block.getDef();
            for(Set<String> set:defs)
            {
                ret.add(set);
            }
        }
        return ret;
    }

    /**
     * the uses
     * @return uses
     */
    public ArrayList<Set<String>> getUse()
    {
        ArrayList<Set<String>> ret = new ArrayList<>();
        for(BasicBlock block:this.blocks)
        {
            ArrayList<Set<String>>uses = block.getUse();
            for(Set<String> set:uses)
            {
                ret.add(set);
            }
        }
        return ret;
    }

    /**
     * the defs and uses
     * @return the first array list contains the set of defs and the second the set of uses
     */
    public pair<ArrayList<Set<String>>,ArrayList<Set<String>>> getDefAndUse()
    {
        ArrayList<Set<String>> defs = new ArrayList<>();
        ArrayList<Set<String>> uses = new ArrayList<>();
        for(BasicBlock block:this.blocks)
        {
            pair<ArrayList<Set<String>>,ArrayList<Set<String>>> blockDefsUses = block.getDefAndUse();

            //defs
            for(Set<String>set:blockDefsUses.getFirst())
            {
                defs.add(set);
            }

            //uses
            for(Set<String>set:blockDefsUses.getSecond())
            {
                uses.add(set);
            }
        }

        if(this.returnVar!=null) {
            if(defs.size()==0)
            {
                defs.add(new HashSet<>());
            }
            if(uses.size()==0)
            {
                uses.add(new HashSet<>());
            }
            defs.get(0).addAll(this.returnVar.getVariableNames());
            uses.get(uses.size()-1).addAll(this.returnVar.getVariableNames());
        }

        for(int i=0;i<this.methodArgs.size();++i)
        {
            Iterator it = this.localVariables.varTable.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                if(((Integer)pair.getValue())==i)
                {
                    defs.get(0).add((String)pair.getKey());
                }
            }
        }
        if(this.returnVar!=null) {
            Set<String> lastset = new HashSet<>();
            lastset.addAll(this.returnVar.getVariableNames());
            uses.add(lastset);
            defs.add(new HashSet<>());
        }

        return new pair<>(defs,uses);
    }

    /**
     * execute liveness analysis
     * @return the in and out for method
     */
    public HashMap<String,HashSet<Integer>> livenessAnalysis()
    {
        pair<ArrayList<Set<String>>,ArrayList<Set<String>>> defAndUse = this.getDefAndUse();

        pair<ArrayList<Set<String>>,ArrayList<Set<String>>> inout = new pair<>(new ArrayList<>(),new ArrayList<>());

        Boolean hasChanged = true;
        while(hasChanged)
        {
            hasChanged = false;

            if(this.blocks.size()!=0)
            {
                for(int i=this.blocks.size()-1;i>=0;--i)
                {
                    BasicBlock block = this.blocks.get(i);
                    block.livenessAnalysis(hasChanged,block.getDefAndUse());
                }
            }
        }

        for(int i=0;i<this.blocks.size();++i)
        {
            BasicBlock block = this.blocks.get(i);
            pair<ArrayList<Set<String>>,ArrayList<Set<String>>> ret =  block.livenessAnalysis(hasChanged,block.getDefAndUse());
            inout.getFirst().addAll(ret.getFirst());
            inout.getSecond().addAll(ret.getSecond());
        }

        HashMap<String,HashSet<Integer>> ret = new HashMap<>();
        HashMap<String,ArrayList<Integer>> check = new HashMap<>();
        for(int i=0;i<inout.getSecond().size();++i)
        {
            Set<String> in = inout.getFirst().get(i);
            for(String var:in)
            {
                if(ret.containsKey(var))
                {
                    int last = check.get(var).get(check.get(var).size()-1);
                    int delta = i-last;
                    if(delta>1)
                    {
                        for(int j=1;j<=delta;++j)
                        {
                            check.get(var).add(last+j);
                            ret.get(var).add(last+j);
                        }
                    }
                    check.get(var).add(i);
                    ret.get(var).add(i);
                }
                else
                {
                    check.put(var,new ArrayList<>());
                    check.get(var).add(i);
                    ret.put(var,new HashSet<>());
                    ret.get(var).add(i);
                }
            }

            in = inout.getSecond().get(i);
            for(String var:in)
            {
                if(ret.containsKey(var))
                {
                    int last = check.get(var).get(check.get(var).size()-1);
                    int delta = i-last;
                    if(delta>1)
                    {
                        for(int j=1;j<=delta;++j)
                        {
                            check.get(var).add(last+j);
                            ret.get(var).add(last+j);
                        }
                    }
                    check.get(var).add(i);
                    ret.get(var).add(i);
                }
                else
                {
                    check.put(var,new ArrayList<>());
                    check.get(var).add(i);
                    ret.put(var,new HashSet<>());
                    ret.get(var).add(i);
                }
            }
        }

        return ret;
    }

    /**
     * set the variable table
     * @param table variable table
     */
    public void setVarTable(VariableTable table)
    {
        this.localVariables = table;
        for(BasicBlock block:this.blocks)
        {
            block.setVarTable(table);
        }
    }
}
