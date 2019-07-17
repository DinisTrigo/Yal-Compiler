package Yal2Jvm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the symbol table of a module
 */
public class ModuleST extends ST
{
    /**
     * A String representing the name of the module that this symbol table respects to.
     */
    String moduleName;
    /**
     * maps a global var to a boolean indicating weather it is an array or not.
     */
    public HashMap<String, Boolean> globals = new HashMap<String, Boolean>();
    /**
     * maps a function name (String) to an instance of the class FunctionST that represents it.
     */
    HashMap<String, FunctionST> functions = new HashMap<String, FunctionST>();

    /**
     * Getter for the HashMap attribute globals.
     * @return the HashMap attribute globals.
     */
    public HashMap<String, Boolean> getGlobals()
    {
        return globals;
    }

    /**
     * Adds a variable to this symbol table. Is called when a new variable is defined on the module that this ST represents.
     * @param symbol A string representing the symbol of the variable that is being added to the ST (the newly declared variable).
     * @param isArray A boolean value indicating weather the variable that is being added to the ST is an array or not (int if not array).
     */
    @Override
    public void addVariable(String symbol, Boolean isArray)
    {
        globals.put(symbol, isArray);
    }

    /**
     * Adds a function to this symbol table. Is called when a new function is defined on the module that this ST represents.
     * @param symbol A string representing the name of the function that is being added to the ST (the newly declared function).
     * @param functionST An instance of the class FunctionST which represents the ST of the function that is being added to the ST (the newly declared function).
     */
    public void addFunction(String symbol, FunctionST functionST)
    {
        functions.put(symbol, functionST);
    }

    /**
     * Setter for the attribute moduleName.
     * @param moduleName The new value to be assigned to the attribute moduleName.
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * Getter for retrieving an instance of the class FunctionST that represents the function with the same name as the one received as parameter. Returns null if the module contains no function by the given name.
     * @param functionName The name of the function of which to get the instance of FunctionST representing it.
     * @return An instance of the class FunctionST that represents the function with the same name as the one received as parameter. It is null if the module contains no function by the given name.
     */
    public FunctionST getFunctionST(String functionName)
    {
        return functions.get(functionName);
    }

    /**
     * Checks weather a global variable with the name passed as parameter is an array or not (int if not array).
     * @param symbol The name of the variable of which to check weather is an array or not (int if not array).
     * @return A boolean value indicating weather the variable with the name passed as parameter is an array or not (int if not array).
     */
    @Override
    public Boolean isVariableArray(String symbol)
    {
        return globals.get(symbol);
    }

    /**
     * Getter for the a function's return type.
     * @param functionName A String representing the name of the function of which to get the return type.
     * @return An int value that has the following meaning for the type of the function with the name of the String received as parameter:
     * 0 - is int
     * 1 - is array
     * 2 - is void
     * 3 - doesn't exist in this module, assume its external module call
     */
    public int getFunctionReturnType(String functionName)
    {
        FunctionST functionST = functions.get(functionName);
        if (functionST == null)
            return 3;
        else if (functionST.returnIsArray == null)
            return 2;
        else if (functionST.returnIsArray)
            return 1;
        else
            return 0;
    }

    /**
     * Displays information about this ModuleST. Correctly formats the information for more user-friendly reading on the console.
     * The method is overridden in the derived classes whenever special behaviour is necessary.
     * @param prefix The prefix (one or more spaces) to correctly format the information.
     */
    @Override
    public void dump(String prefix)
    {
        System.out.println("Global vars: ");
        printMap(globals, prefix);
        System.out.println("Functions: ");
        printFunctions(functions, prefix + " ");
    }

    /**
     * Auxiliary method for the dump method of this class. Displays information about this ModuleST. Correctly formats the information for more user-friendly reading on the console.
     * @param mp The hashmap of the functions of this module.
     * @param prefix The prefix (one or more spaces) to correctly format the information.
     */
    public static void printFunctions(Map mp, String prefix)
    {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println("Function name: " + pair.getKey());
            ((FunctionST) pair.getValue()).dump(prefix);
        }
    }
}
