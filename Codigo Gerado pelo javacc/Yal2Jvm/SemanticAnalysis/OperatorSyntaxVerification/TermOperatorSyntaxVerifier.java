package Yal2Jvm.SemanticAnalysis.OperatorSyntaxVerification;

import Yal2Jvm.*;

/**
 * This class implements the necessary actions to verify the operator syntax for an Term node.
 */
public class TermOperatorSyntaxVerifier
{
    /**
     * A class representing the Term node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTTerm ASTTerm;

    /**
     * Constructor for this class receives an ASTTerm objects as parameter that will be used to set the attribute.
     * @param ASTTerm A class representing the Term node on the AST. This is used to access information about the node.
     */
    public TermOperatorSyntaxVerifier(Yal2Jvm.ASTTerm ASTTerm)
    {
        this.ASTTerm = ASTTerm;
    }

    /**
     * Method that verifies the operator syntax for a Term node.
     * @param st An Object that represents the already filled symbol table.
     * @param operatorHasToBeInt A boolean indicating weather the next operator found has to be an int or not.
     */
    public void verifyOperatorSyntax(ST st, boolean operatorHasToBeInt)
    {
        Boolean termIsArray = null;
        String ID = null;
        if (ASTTerm.getTermInt() != null)
            termIsArray = false;
        else if (ASTTerm.getChildren()[0] instanceof ASTCall)
        {
            String functionID = ((ASTCall) ASTTerm.getChildren()[0]).outerCalledID;
            int functionReturnType = ((FunctionST) st).globalST.getFunctionReturnType(functionID);
            switch (functionReturnType)
            {
                case 0:
                    termIsArray = false;
                    break;
                case 1:
                    termIsArray = true;
                    break;
                case 2:
                    System.out.println("Cannot use void function " + functionID + " return type as term on line: " + ASTTerm.getLine());
                    st.declareCodeHasSemanticErrors();
                    return;
                case 3:
                    return;
                default:
                    return;
            }
        } else if (ASTTerm.getChildren()[0] instanceof ASTArrayAccess)
        {
            termIsArray = false;
            ID = ((ASTArrayAccess) ASTTerm.getChildren()[0]).arrayID;
        } else if (ASTTerm.getChildren()[0] instanceof ASTScalarAccess)
        {
            ASTScalarAccess scalarAccess = (ASTScalarAccess) ASTTerm.getChildren()[0];
            ID = scalarAccess.id;
            if (scalarAccess.size != null)
                termIsArray = false;
            else
                termIsArray = ((FunctionST) st).isVariableArray(ID);
        }
        if (termIsArray == null) //if the function is an external call, assume it returns the type that matches the variable
            return;

        if (termIsArray && ASTTerm.getAddSub_Op() != null)
        {
            System.out.println("cannot use add or sub operator for array type variable: " + ID + " on line: " + ASTTerm.getLine());
            st.declareCodeHasSemanticErrors();
            return;
        } else if (termIsArray && operatorHasToBeInt)
        {
            System.out.println("cannot use array element: " + ID + " as term for expression." + " on line: " + ASTTerm.getLine());
            st.declareCodeHasSemanticErrors();
            return;
        }
    }
}