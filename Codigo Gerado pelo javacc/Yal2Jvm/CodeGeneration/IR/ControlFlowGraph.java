package Yal2Jvm.CodeGeneration.IR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ControlFlowGraph implements IRContainer
{
    /**
     * An instance of the class VariableTable representing the variable table containing the global variables.
     */
    public VariableTable globalVariables;
    /**
     * An hash that represents all the method of the module.
     */
    public HashMap<String, Method> methodRegistry;
    /**
     * An ArrayList of Method containing the Methods sorted.
     */
    public ArrayList<Method> orderedMethodList;
    /**
     * A String representing the name of the module.
     */
    public String moduleName;
    /**
     * A String representing the public name of the module.
     */
    public static String publicModuleName;

    /**
     * Constructor of the class that received the module name as parameter.
     * @param moduleName The name of the module that is being compiled.
     */
    public ControlFlowGraph(String moduleName)
    {
        this.moduleName = moduleName;
        publicModuleName = moduleName;
        this.globalVariables = new VariableTable();
        this.orderedMethodList = new ArrayList<>();
        this.methodRegistry = new HashMap<>();
    }

    /**
     * Adds a Method to the method registry of this control flow graph. This is called when a new method declaration is found.
     * @param method The method to be added to the method registry.
     */
    public void addMethod(Method method)
    {
        this.orderedMethodList.add(method);
        this.methodRegistry.put(method.methodName, method);
    }

    /**
     * Returns an instance of a Method that represents the method corresponding to the name received as parameter.
     * @param methodName The name of the method to get the instance of.
     * @return An instance of Method that corresponds to the name received as parameter.
     */
    public Method getMethod(String methodName)
    {
        return this.methodRegistry.get(methodName);
    }

    /**
     * adds a global variable to the method
     * @param id id of the global
     * @param isArray is the global array ?
     */
    public void addGlobalVariable(String id, boolean isArray)
    {
        this.globalVariables.addGlobalVariable(id, isArray);
    }

    /**
     * adds a global variable to the method
     * @param id the id of the variable
     * @param isArray if the variable is an array
     * @param defaultValue the default value or size of the variable
     */
    public void addGlobalVariable(String id, boolean isArray, String defaultValue)
    {
        this.globalVariables.addGlobalVariable(id, isArray, defaultValue);
    }

    /**
     * obtain the instruction of this file
     * @return the instruction that make up the file
     */
    public String getInstructions()
    {
        String ret = ".class public " + this.moduleName + "\n.super java/lang/Object\n";

        Iterator it = globalVariables.varTable.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            boolean isArray = globalVariables.types.get(pair.getKey());
            String val = globalVariables.defaultValues.get(pair.getKey());
            ret += ".field public static " + pair.getKey() + " ";
            if (isArray)
            {
                ret += "[I ";
            } else
            {
                ret += "I ";
                if (val != null)
                {
                    ret += "= " + val;
                }
            }
            ret += "\n";
        }

        ret += "\n.method public <init>()V\naload_0\ninvokespecial java/lang/Object/<init>()V\n";
        ret += "return\n.end method\n\n";

        String inter = "";
        boolean good = false;
        inter+=".method static public <clinit>()V\n";
        it = globalVariables.varTable.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            boolean isArray = globalVariables.types.get(pair.getKey());
            String val = globalVariables.defaultValues.get(pair.getKey());
            if (isArray)
            {
                good = true;
                //load size on the stack
                inter+="ldc "+val+"\n";
                //create the array
                inter += "newarray int\n";
                //store the reference on the global var
                inter += "putstatic "+ControlFlowGraph.publicModuleName+"/"+pair.getKey()+" [I\n";
            }
        }
        inter+="return\n.end method\n\n";

        if(good)
        {
            ret+=inter;
        }

        for (int i = 0; i < orderedMethodList.size(); ++i)
        {
            ret += orderedMethodList.get(i).getInstructions();
        }
        return ret;
    }

}
