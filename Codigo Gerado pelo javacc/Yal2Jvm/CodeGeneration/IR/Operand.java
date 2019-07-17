package Yal2Jvm.CodeGeneration.IR;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Operand {

    public boolean isMethod = false;
    public Method invokeMethod;
    public boolean isGlobal;
    public boolean isVariable;
    public boolean isArray;
    public boolean isArrayAccess;
    public Operand arrayIndex;
    public String value;
    public boolean shouldNegate = false;
    public boolean isInstructions = false;
    public ArrayList<Operand> args = new ArrayList<>();
    public boolean isArrayLiteral = false;
    public boolean isArrayLen = false;

    public boolean literalConst = false;

    //fields to handle register allocation for literal instruction operands
    private Set<String>instructionUsedVariables = null;

    public String printString = null;
    public String invokeMethodString = null;

    public ArrayList<String> parameters;
    public String returnType = null;

    /**
     * create a regular operand
     * @param value the value
     * @param isVariable if is variable
     * @param isArray if is array
     * @param isGlobal if is global
     */
    public Operand(String value, boolean isVariable, boolean isArray, boolean isGlobal) {
        this.isGlobal = isGlobal;
        this.isArray = isArray;
        this.isArrayAccess = false;
        this.arrayIndex = null;
        this.isVariable = isVariable;
        this.value = value;
    }

    /**
     * create an array access
     * @param value the value
     * @param arrayIndex the array index
     * @param isGlobal if is global
     */
    public Operand(String value, Operand arrayIndex, boolean isGlobal) {
        this.isGlobal = isGlobal;
        this.isVariable = true;
        this.isArray = false;
        this.isArrayAccess = true;
        this.arrayIndex = arrayIndex;
        this.value = value;
    }

    /**
     *  Used specifically for a literal array declaration of the type a=[5], this method is used to define the [5] literal array
     * @param arraySize the size of the array to create
     */
    public Operand(Operand arraySize)
    {
        this.isGlobal = false;
        this.isArray = true;
        this.isArrayAccess = false;
        this.arrayIndex = null;
        this.isVariable = false;
        this.value = null;

        this.isArrayLiteral = true;
        this.arrayIndex = arraySize;
    }

    /**
     * create an array lenght
     * @param array array
     * @param isArrayLen is len
     */
    public Operand(Operand array,boolean isArrayLen)
    {
        this.isGlobal = false;
        this.isArray = false;
        this.isArrayAccess = false;
        this.arrayIndex = null;
        this.isVariable = false;
        this.value = null;

        this.arrayIndex = array;
        this.isArrayLen = isArrayLen;
    }

    /**
     * invoke a method operator
     * @param invokeMethod method to invoke
     * @param args the argumetns
     */
    public Operand(Method invokeMethod, ArrayList<Operand> args)
    {
        this.isGlobal = false;
        this.isArray = false;
        this.isArrayAccess = false;
        this.arrayIndex = null;
        this.isVariable = false;
        this.value = null;
        this.isMethod = true;
        this.invokeMethod = invokeMethod;
        this.args = args;
    }

    /**
     * instructions operand
     * @param instructions instructions
     */
    public Operand(String instructions)
    {
        this.isGlobal = false;
        this.isArray = false;
        this.isArrayAccess = false;
        this.arrayIndex = null;
        this.isVariable = false;
        this.value = null;
        this.isMethod = true;
        this.isInstructions = true;
        this.value = instructions;
    }

    //start of code directed at loadding a variable from the operand

    /**
     * load the operand
     * @param variableTable the variable table to use
     * @return instructions
     */
    public String load(VariableTable variableTable)
    {
        String ret="";

        if(shouldNegate)
        {
            ret+=this.negate();
        }

        if(isVariable)
        {
            if(isArrayAccess)
            {
                if(isGlobal)
                {
                    ret+=this.loadGlobalArrayAccess(variableTable);
                }
                else
                {
                    ret+=this.loadLocalArrayAccess(variableTable);
                }
            }
            else if(isArray)
            {
                if(isArrayLiteral)
                {
                    ret+=this.loadArrayLiteral(variableTable);
                }
                else {
                    if (isGlobal) {
                        ret += this.loadGlobalArray();
                    } else {
                        ret += this.loadLocalArray(variableTable);
                    }
                }
            }
            else
            {
                if(isGlobal)
                {
                    ret+=this.loadGlobalBasicInteger();
                }
                else
                {
                    ret+=this.loadLocalBasicInteger(variableTable);
                }
            }
        }
        else if(isArray)
        {
            if(isArrayLiteral)
            {
                ret+=this.loadArrayLiteral(variableTable);
            }
            else {
                if (isGlobal) {
                    ret += this.loadGlobalArray();
                } else {
                    ret += this.loadLocalArray(variableTable);
                }
            }
        }
        else if(isArrayLen)
        {
            if(isGlobal)
            {
                ret+=this.loadGlobalArraySize();
            }
            else
            {
                ret+=this.loadLocalArraySize(variableTable);
            }
        }
        else if(isInstructions)
        {
            ret+=this.loadInstructions(variableTable);
        }
        else if(isMethod)
        {
            ret+=this.loadMethod(variableTable);
        }
        else
        {
            ret+=this.loadConstant();
        }

        return ret;
    }

    /**
     * load a constant
     * @return instructions
     */
    private String loadConstant()
    {
        this.literalConst = true;
        int value = Integer.parseInt(this.value);
        if(value>=0 && value<=5)
        {
            return "iconst_"+value+"\n";
        }else if(value==-1)
        {
            return "iconst_m1\n";
        }
        else if(value>=-128 && value<=127)
        {
            return "bipush "+value+"\n";
        }
        else if(value>=-32768  && value<=32767)
        {
            return "sipush "+value+"\n";
        }
        return "ldc "+this.value+"\n";
    }

    /**
     * load a local integer variable
     * @param variableTable the vartable
     * @return instructions
     */
    private String loadLocalBasicInteger(VariableTable variableTable)
    {
        String ret = "";
        int offset = variableTable.varTable.get(this.value);
        if(offset>=0&&offset<=3)
        {
            ret+="iload_"+offset+"\n";
        }
        else
        {
            ret += "iload " + offset + "\n";
        }
        return ret;
    }

    /**
     * load a global integer var
     * @return instructions
     */
    private String loadGlobalBasicInteger()
    {
        return "getstatic "+ControlFlowGraph.publicModuleName+"/"+this.value+" I\n";
    }

    /**
     * load a local array var
     * @param variableTable vartable
     * @return instructions
     */
    private String loadLocalArray(VariableTable variableTable)
    {
        String ret = "";
        int offset = variableTable.varTable.get(this.value);
        if(offset>=0 && offset<=3)
        {
            ret += "aload_"+offset+"\n";
        }
        else
        {
            ret += "aload "+offset+"\n";
        }
        return ret;
    }

    /**
     * load a global array var
     * @return instructions
     */
    private String loadGlobalArray()
    {
        return "getstatic "+ControlFlowGraph.publicModuleName+"/"+this.value+" [I\n";
    }

    /**
     * load a local array size
     * @param variableTable vartable
     * @return instructions
     */
    private String loadLocalArraySize(VariableTable variableTable)
    {
        String ret="";
        ret+=this.arrayIndex.load(variableTable);
        ret+="arraylength\n";
        return ret;
    }

    /**
     * load a global array size
     * @return instructions
     */
    private String loadGlobalArraySize()
    {
        String ret="";
        ret+=loadGlobalArray();
        ret+="arraylength\n";
        return ret;
    }

    /**
     * load a local array access
     * @param variableTable vartable
     * @return instructions
     */
    private String loadLocalArrayAccess(VariableTable variableTable)
    {
        String ret="";

        //load array index on the stack
        ret+=loadLocalArray(variableTable);

        //load the array index
        Operand index = this.arrayIndex;
        ret+=index.load(variableTable);

        //load the array index value
        ret+="iaload\n";

        return ret;
    }

    /**
     * load a global array access
     * @param variableTable vartable
     * @return instructions
     */
    private String loadGlobalArrayAccess(VariableTable variableTable)
    {
        String ret="";

        //load array index on the stack
        ret+=loadGlobalArray();

        //load the array index
        Operand index = this.arrayIndex;
        ret+=index.load(variableTable);

        //load the array index value
        ret+="iaload\n";

        return ret;
    }

    /**
     * load an array literal
     * @param variableTable varatable
     * @return instructions
     */
    private String loadArrayLiteral(VariableTable variableTable)
    {
        String ret = "";

        Operand size = this.arrayIndex;
        ret+=size.load(variableTable);

        ret+="newarray int\n";

        return ret;
    }

    /**
     * load a method
     * @param variableTable vartable
     * @return instructions
     */
    private String loadMethod(VariableTable variableTable)
    {
        return IRNode.invokeMethod(this.invokeMethod,this.args,variableTable);
    }

    /**
     * load instructions
     * @param variableTable variable table
     * @return instructions
     */
    private String loadInstructions(VariableTable variableTable)
    {
        if(this.invokeMethodString!=null)
        {
            switch (this.invokeMethodString)
            {
                case("io/print"):
                {
                    if(this.printString!=null)
                    {
                        if(this.args.size()==1)
                        {
                            return IRNode.ioprint(this.printString,this.args.get(0),variableTable).value;
                        }
                        else
                        {
                            return IRNode.ioprint(this.printString).value;
                        }
                    }
                    else if(this.args.size()>0)
                    {
                        return IRNode.ioprint(this.args.get(0),variableTable).value;
                    }
                    return this.value;
                }
                case("io/println"):
                {
                    if(this.printString!=null)
                    {
                        if(this.args.size()==1)
                        {
                            return IRNode.ioprintln(this.printString,this.args.get(0),variableTable).value;
                        }
                        else
                        {
                            return IRNode.ioprintln(this.printString).value;
                        }
                    }
                    else if(this.args.size()>0)
                    {
                        return IRNode.ioprintln(this.args.get(0),variableTable).value;
                    }
                    else
                    {
                        return IRNode.ioprintln().value;
                    }
                }
                default:
                {
                    if(this.parameters!=null)
                    {
                        String[] names = this.invokeMethodString.split("/");
                        return IRNode.invokeExternal(names[0],names[1],this.args,this.parameters,variableTable,this.returnType).value;
                    }
                    else
                    {
                        return this.value;
                    }
                }
            }
        }
        else
        {
            return this.value;
        }
    }

    /**
     * negate operations
     * @return instructions
     */
    private String negate()
    {
        return "ineg\n";
    }

    //code for obtaining the variables associated with this operand

    /**
     * get the varible names
     * @return the var names
     */
    public Set<String> getVariableNames()
    {
        Set<String> ret = new HashSet<>();

        if(this.isVariable)
        {
            if(this.isArrayAccess)
            {
                ret.addAll(this.getArrayAccessVariableNames());
            }
            else if(this.isArray)
            {
                if(this.isArrayLiteral)
                {
                    ret.addAll(this.getArrayLiteralVariableNames());
                }
                else
                {
                    String add = this.getArrayVariableNames();
                    if(add!=null) {
                        ret.add(add);
                    }
                }
            }
            else
            {
                String add = this.getBasicIntegerVariableNames();
                if(add!=null) {
                    ret.add(add);
                }
            }
        }
        else if(this.isArray)
        {
            if(this.isArrayLiteral)
            {
                ret.addAll(this.getArrayLiteralVariableNames());
            }
            else
            {
                String add = this.getArrayVariableNames();
                if(add!=null) {
                    ret.add(add);
                }
            }
        }
        else if(this.isArrayLen)
        {
            String add = this.getArraySizeVariableNames();
            if(add!=null) {
                ret.add(add);
            }
        }
        else if(this.isInstructions)
        {
            if(this.instructionUsedVariables!=null)
            {
                ret.addAll(this.instructionUsedVariables);
            }
        }
        else if(this.isMethod)
        {
            ret.addAll(this.getMethodVariableNames());
        }

        return ret;
    }

    /**
     * used vars for array
     * @return used vars
     */
    private String getArrayVariableNames()
    {
        if(!this.isGlobal)
        {
            return this.value;
        }
        return null;
    }

    /**
     * used vars for integer
     * @return used vars
     */
    private String getBasicIntegerVariableNames()
    {
        if(!this.isGlobal)
        {
            return this.value;
        }
        return null;
    }

    /**
     * used vars
     * @return used vars for array access
     */
    private Set<String> getArrayAccessVariableNames()
    {
        Set<String> ret = new HashSet<>();
        //add the array to the involved variables
        if(!this.isGlobal)
        {
            ret.add(this.value);
        }

        //add the array index to the involved variables if such is required
        Operand index = this.arrayIndex;
        ret.addAll(index.getVariableNames());

        return ret;
    }

    /**
     * used vars for array size
     * @return used vars
     */
    private String getArraySizeVariableNames()
    {
        if(!this.isGlobal)
        {
            return this.arrayIndex.value;
        }
        return null;
    }

    /**
     * used vars for array literal
     * @return used vars
     */
    private Set<String> getArrayLiteralVariableNames()
    {
        Set<String> ret = new HashSet<>();
        Operand size = this.arrayIndex;
        ret.addAll(size.getVariableNames());
        return ret;
    }

    /**
     * used vars for method
     * @return used vars
     */
    private Set<String> getMethodVariableNames()
    {
        Set<String> ret = new HashSet<>();
        for(Operand arg:this.args)
        {
            ret.addAll(arg.getVariableNames());
        }
        return ret;
    }

    /**
     * set variable names
     * @param variables variable
     */
    public void setVariableNames(Set<String> variables)
    {
        this.instructionUsedVariables = variables;
    }

    /**
     * comparator
     * @param e object to compare to
     * @return if is equal
     */
    @Override
    public boolean equals(Object e)
    {
        Operand op = (Operand)e;

        if(isVariable)
        {
            if(isArrayAccess)
            {
                if(isGlobal)
                {
                    if(op.isGlobal && op.isVariable && op.isArrayAccess)
                    {
                        if(this.value.equals(op.value)&&this.arrayIndex.equals(op.arrayIndex))
                        {
                            return true;
                        }
                        return false;
                    }
                }
                else
                {
                    if(!op.isGlobal && op.isVariable && op.isArrayAccess)
                    {
                        if(this.value.equals(op.value)&&this.arrayIndex.equals(op.arrayIndex))
                        {
                            return true;
                        }
                        return false;
                    }
                }
            }
            else if(isArray)
            {
                if(isArrayLiteral)
                {
                    if(op.isArray && op.isArrayLiteral &&op.isVariable)
                    {
                        if(op.arrayIndex.equals(this.arrayIndex))
                        {
                            return true;
                        }
                        return false;
                    }
                }
                else {
                    if (isGlobal) {
                        if(op.isGlobal&&op.isArray&&!op.isArrayLiteral&&op.isVariable)
                        {
                            if(this.value.equals(op.value))
                            {
                                return true;
                            }
                            return false;
                        }
                    } else {
                        if(!op.isGlobal&&op.isArray&&!op.isArrayLiteral&&op.isVariable)
                        {
                            if(this.value.equals(op.value))
                            {
                                return true;
                            }
                            return false;
                        }
                    }
                }
            }
            else
            {
                if(isGlobal)
                {
                    if(op.isVariable&&op.isGlobal&&!op.isArrayLiteral&&!op.isArray)
                    {
                        if(op.value.equals(this.value))
                        {
                            return true;
                        }
                        return false;
                    }
                }
                else
                {
                    if(op.isVariable&&!op.isGlobal&&!op.isArrayLiteral&&!op.isArray)
                    {
                        if(op.value.equals(this.value))
                        {
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        else if(isArray)
        {
            if(isArrayLiteral)
            {
                if(op.isArray && op.isArrayLiteral &&op.isVariable)
                {
                    if(op.arrayIndex.equals(this.arrayIndex))
                    {
                        return true;
                    }
                    return false;
                }
            }
            else {
                if (isGlobal) {
                    if(op.isGlobal&&op.isArray&&!op.isArrayLiteral)
                    {
                        if(this.value.equals(op.value))
                        {
                            return true;
                        }
                        return false;
                    }
                } else {
                    if(!op.isGlobal&&op.isArray&&!op.isArrayLiteral)
                    {
                        if(this.value.equals(op.value))
                        {
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        else if(isArrayLen)
        {
            if(isGlobal)
            {
                if(op.isGlobal&&op.isArray&&!op.isVariable)
                {
                    if(this.arrayIndex.equals(op.arrayIndex))
                    {
                        return true;
                    }
                    return false;
                }
            }
            else
            {
                if(!op.isGlobal&&op.isArray&&!op.isVariable)
                {
                    if(this.arrayIndex.equals(op.arrayIndex))
                    {
                        return true;
                    }
                    return false;
                }
            }
        }
        else if(isInstructions)
        {
            if(op.isInstructions)
            {
                return this.value.equals(op.value);
            }
        }
        else if(isMethod)
        {
            if(op.isMethod)
            {
                if(op.invokeMethod.methodName.equals(op.invokeMethod.methodName))
                {
                    for(int i=0;i<op.args.size();++i)
                    {
                        if(this.args.size()!=op.args.size())
                        {
                            return false;
                        }
                        if(!op.args.get(i).equals(this.args.get(i)))
                        {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        else
        {
            return this.value.equals(op.value);
        }

        return false;
    }
}
