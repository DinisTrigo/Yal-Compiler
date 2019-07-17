package Yal2Jvm;

import java.util.Iterator;
import java.util.Map;

/**
 * An abstract representing a symbol table.
 */
public abstract class ST
{
    /**
     * A boolean value indicating weather the code has semantic errors or not.
     */
    private boolean codeHasSemanticErrors = false;
    /**
     * An int value representing the number of semantic errors on the compiled program.
     */
    private int numberOfSemanticErrors = 0;

    /**
     * Adds a variable to this symbol table. Is called when a new variable is defined.
     * This method is abstract because it is only implement in the derived classes, since special behaviour is needed for adding a variable depending on the type of symbol table.
     * Please refer to the derived classes for more information.
     * @param symbol A string representing the symbol of the variable that is being added to the ST (the newly declared variable).
     * @param isArray A boolean value indicating weather the variable that is being added to the ST is an array or not (int if not array).
     */
    public abstract void addVariable(String symbol, Boolean isArray);

    /**
     * Displays information about this ST. Correctly formats the information for more user-friendly reading on the console.
     * This method is abstract because it is only implement in the derived classes, since special behaviour is needed for displaying the information.
     * Please refer to the derived classes for more information.
     * The method is overridden in the derived classes whenever special behaviour is necessary.
     * @param prefix The prefix (one or more spaces) to correctly format the information.
     */
    public abstract void dump(String prefix);

    /**
     * Auxiliary method for the dump method of the derived classes. Displays information about this ST. Correctly formats the information for more user-friendly reading on the console.
     * @param mp The hashmap of symbols of this ST.
     * @param prefix The prefix (one or more spaces) to correctly format the information.
     */
    public static void printMap(Map mp, String prefix)
    {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.print(prefix + pair.getKey() + ": ");
            Boolean isArray = (Boolean) pair.getValue();
            if (isArray == null)
                System.out.print("void\n");
            else if (isArray)
                System.out.print("array\n");
            else
                System.out.print("int\n");
        }
    }

    /**
     * Checks weather a variable with the name passed as parameter is an array or not (int if not array).
     * This method is abstract because it is only implement in the derived classes, since special behaviour is needed for checking the type of a variable depending on the type of symbol table.
     * Please refer to the derived classes for more information.
     * @param symbol The name of the variable of which to check weather is an array or not (int if not array).
     * @return A boolean value indicating weather the variable with the name passed as parameter is an array or not (int if not array).
     */
    public abstract Boolean isVariableArray(String symbol);

    /**
     * Getter for the attribute codeHasSemanticErrors (which identifies weather the code hash semantic errors or not).
     * @return The value of the attribute codeHasSemanticErrors (which identifies weather the code hash semantic errors or not).
     */
    public boolean codeHasSemanticErrors()
    {
        return codeHasSemanticErrors;
    }

    /**
     * This method is called to declare that the code inside the program has semantic errors. It is used to know if the code has compile successfully or not in the end and to count the errors.
     */
    public void declareCodeHasSemanticErrors()
    {
        codeHasSemanticErrors = true;
        numberOfSemanticErrors++;
    }

    /**
     * Getter for the attribute numberOfSemanticErrors.
     * @return The value of the attribute numberOfSemanticErrors.
     */
    public int getNumberOfSemanticErrors()
    {
        return numberOfSemanticErrors;
    }
}
