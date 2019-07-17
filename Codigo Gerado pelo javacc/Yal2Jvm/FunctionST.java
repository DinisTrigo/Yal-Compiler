package Yal2Jvm;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the symbol table of a function
 */
public class FunctionST extends ST
{
    /**
     * An instance of the class ModuleST that represents the Module symbol table where this FunctionST is contained on.
     */
    public ModuleST globalST;
    /**
     * wheter or not the return of the function is an array
     */
    public Boolean returnIsArray = null;
    /**
     * Id of the variable representing the return of the function that this ST respects to
     */
    public String returnId;
    /**
     * an arraylist with the length equal to the number of params and the value indicating weather it is an array or not
     */
    public ArrayList<Boolean> paramsTypes = new ArrayList<Boolean>();
    /**
     * an arraylist with the length equal to the number of params and the value indicating the name of the parameter
     */
    public ArrayList<String> paramsNames = new ArrayList<String>();
    /**
     * maps a global var to a boolean indicating weather it is an array or not
     */
    public HashMap<String, Boolean> locals = new HashMap<String, Boolean>();
    /**
     * maps a global var to a boolean indicating weather it is initialized or not
     */
    public HashMap<String, Boolean> localIsInitializd = new HashMap<String, Boolean>();

    /**
     * Setter for the attribute returnIsArray.
     * @param returnIsArray The new value to assign to the attribute returnIsArray.
     */
    public void setReturnIsArray(Boolean returnIsArray)
    {
        this.returnIsArray = returnIsArray;
    }

    /**
     * Setter for the attribute returnId.
     * @param returnId The new value to assign to the attribute returnId.
     */
    public void setReturnId(String returnId)
    {
        this.returnId = returnId;
    }

    /**
     * Adds a variable to this symbol table. Is called when a new variable is defined on the function that this ST represents.
     * @param symbol A string representing the symbol of the variable that is being added to the ST (the newly declared variable).
     * @param isArray A boolean value indicating weather the variable that is being added to the ST is an array or not (int if not array).
     */
    @Override
    public void addVariable(String symbol, Boolean isArray)
    {
        locals.put(symbol, isArray);
    }

    /**
     * Adds a parameter type to this symbol table. Is called when the parameters are being filled in this ST.
     * @param isArray A boolean value indicating weather the parameter that is being added to the ST is an array or not (int if not array).
     */
    public void addParamType(boolean isArray)
    {
        paramsTypes.add(isArray);
    }

    /**
     * Adds a parameter type to this symbol table. Is called when the parameters are being filled in this ST.
     * @param name A String representing the
     */
    public void addParamName(String name)
    {
        paramsNames.add(name);
    }

    /**
     * Checks weather a the variable represented by the String received as parameter is an array or not.
     * @param symbol A String representing the variable to check weather is an array or not.
     * @return A boolean value indicating weather the variable represented by the String received as parameter is an array or not.
     */
    @Override
    public Boolean isVariableArray(String symbol)
    {
        Boolean isArray = locals.get(symbol);
        if (isArray != null)
            return isArray;
        else
            return globalST.isVariableArray(symbol);
    }

    /**
     * Checks weather a the variable represented by the String received as parameter is local of the function represented by this ST or not (in which case it is a global variable of the module).
     * @param symbol A String representing the variable to check weather is an array or not.
     * @return A boolean value indicating weather the variable represented by the String received as parameter is an array or not.
     */
    public boolean isVariableLocal(String symbol)
    {
        if (locals.get(symbol) != null)
            return true;
        else
            return false;
    }

    /**
     * Displays information about this functionST. Correctly formats the information for more user-friendly reading on the console.
     * The method is overridden in the derived classes whenever special behaviour is necessary.
     * @param prefix The prefix (one or more spaces) to correctly format the information.
     */
    @Override
    public void dump(String prefix)
    {
        System.out.print(prefix + "Params: ");
        for (Boolean b : paramsTypes)
        {
            System.out.print(prefix);
            if (b)
                System.out.print("array, ");
            else
                System.out.print("int, ");
        }
        System.out.println();
        System.out.print(prefix + "Return: ");
        if (returnIsArray == null)
            System.out.print("void\n");
        else if (returnIsArray)
            System.out.print("array\n");
        else
            System.out.print("int\n");

        System.out.println(prefix + "Locals: ");
        printMap(locals, prefix);
    }

    /**
     * This method is called to declare that the code inside this function has semantic errors. It is used to know if the code has compile successfully or not in the end and to count the errors.
     */
    @Override
    public void declareCodeHasSemanticErrors()
    {
        globalST.declareCodeHasSemanticErrors();
    }
}
