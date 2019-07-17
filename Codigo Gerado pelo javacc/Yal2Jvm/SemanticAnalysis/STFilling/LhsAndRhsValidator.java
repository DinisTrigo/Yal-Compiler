package Yal2Jvm.SemanticAnalysis.STFilling;

import Yal2Jvm.*;

/**
 * This class implements the validation of the left hand side and the right hand side operands in an expression.
 */
public class LhsAndRhsValidator
{
    /**
     * A boolean indicating the result of the execution of the validation.
     */
    private boolean myResult;
    /**
     * An Object that represents the already filled symbol table.
     */
    private ST st;
    /**
     * A String representing the Id of the variable that is on the left hand side of the expression.
     */
    private String lhsID;
    /**
     * A boolean value indicating weather the left hand side of the expression is an array of not.
     */
    private Boolean lhsIsArray;
    /**
     * A boolean value indicating weather the right hand side of the expression is an array of not.
     */
    private Boolean rhsIsArray;
    /**
     * A boolean value indicating weather the expression contains an external module call or not.
     */
    private boolean externalModuleCall;
    /**
     * An array of Node objects that represents the children of the expression node.
     */
    Node[] children;
    /**
     * The line of code where the expression is at. This is used to display more user-friendly errors.
     */
    private int line;

    /**
     * Constructor for this class receives an ASTArrayElement objects as parameter that will be used to set the attribute.
     * @param st An Object that represents the already filled symbol table.
     * @param children
     * @param line
     */
    public LhsAndRhsValidator(ST st, Node[] children, int line)
    {
        this.st = st;
        this.children = children;
        this.line = line;
    }

    /**
     * Getter for the attribute myResult.
     * @return A boolean value representing the attribute myResult.
     */
    public boolean is()
    {
        return myResult;
    }

    /**
     * Getter for the attribute lhsID.
     * @return A String value representing the attribute lhsID.
     */
    public String getLhsID()
    {
        return lhsID;
    }

    /**
     * Getter for the attribute lhsIsArray.
     * @return A Boolean value representing the attribute lhsIsArray.
     */
    public Boolean getLhsIsArray()
    {
        return lhsIsArray;
    }

    /**
     * Getter for the attribute rhsIsArray.
     * @return A Boolean value representing the attribute rhsIsArray.
     */
    public Boolean getRhsIsArray()
    {
        return rhsIsArray;
    }

    /**
     * Getter for the attribute externalModuleCall.
     * @return A boolean value representing the attribute externalModuleCall.
     */
    public boolean isExternalModuleCall()
    {
        return externalModuleCall;
    }

    /**
     * Method that performs the actual left hand side and right hand side operands validation. It fills in the attributes according to the results of the validation which should then be accessed using getter methods.
     * @return An instance of the class LhsAndRhsValidator which is always the class that is performing the left hand side and right hand side operand validations.
     */
    public LhsAndRhsValidator invoke()
    {
        Node lhs = ((SimpleNode) children[0]).getChildren()[0];
        if (lhs instanceof ASTArrayAccess)
        {
            try
            {
                if (((ASTRhs) children[1]).returnIsArray(st)) //Cannot assign array to array element, throw error
                {
                    return handleSemanticError("Trying to assign array to array element (int) on line ");
                }
            } catch (ExternalModuleCall externalModuleCall1)
            {
                externalModuleCall = true;
            }
            return this;
        }
        //if it is not array access it can be either int or array, need to check for type of rhs

        lhsID = ((ASTScalarAccess) lhs).id;
        if (((ASTScalarAccess) lhs).size != null)
        {
            System.out.println("Trying to resize array (arrays cannot be resized) on line: " + line);
            st.declareCodeHasSemanticErrors();
            myResult = true;
            return this;
        }
        lhsIsArray = ((FunctionST) st).isVariableArray(lhsID);
        rhsIsArray = null;
        externalModuleCall = false;
        try
        {
            rhsIsArray = ((ASTRhs) children[1]).returnIsArray(st);
            //check we aren't trying to assign the value of a null function
            if (rhsIsArray == null)
            {
                return handleSemanticError("Cannot use return value of void function on line: ");
            }
        } catch (ExternalModuleCall e)
        {
            externalModuleCall = true;
        }
        if (rhsIsArray == null && !externalModuleCall)
        {
            return handleSemanticError("Undeclared variable on line: ");
        }

        if (externalModuleCall) //the variable results from external module call
        {
            //if the variable is already declared assume that the types match

            //if the variable is not already declared assume that the external module function returns int
            if (lhsIsArray == null)
                rhsIsArray = false;
        }
        myResult = false;
        return this;
    }

    /**
     * Displays a semantic error, according to the String received as parameter, adding to it the line of code where the error occurred.
     * This method is called by the invoke method of this class to display multiple semantic errors, avoiding code duplication.
     * @param s A String value containing information about the semantic error that will be displayed to the screen.
     * @return An instance of the class LhsAndRhsValidator which is always the class that is performing the left hand side and right hand side operand validations.
     */
    public LhsAndRhsValidator handleSemanticError(String s)
    {
        System.out.println(s + line);
        st.declareCodeHasSemanticErrors();
        myResult = true;
        return this;
    }
}