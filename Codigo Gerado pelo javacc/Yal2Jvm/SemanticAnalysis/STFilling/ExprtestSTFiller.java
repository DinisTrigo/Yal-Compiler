package Yal2Jvm.SemanticAnalysis.STFilling;

import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the ST for an Exprtest node.
 */
public class ExprtestSTFiller
{
    /**
     * Method that fills the ST for an Exprtest node.
     * On the first pass, only function definitions are filled in the symbol table. Function bodies will be filled in the second pass.
     * @param ASTExprtest  A class representing the Exprtest node on the AST. This is used to access information about the node.
     * @param st An Object that represents the already filled symbol table.
     */
    public static void fillStSecondPass(Yal2Jvm.ASTExprtest ASTExprtest, ST st)
    {
        LhsAndRhsValidator lhsAndRhsValidator = new LhsAndRhsValidator(st, ASTExprtest.getChildren(), ASTExprtest.getLine()).invoke();
        if (lhsAndRhsValidator.is()) return;
        String lhsID = lhsAndRhsValidator.getLhsID();
        Boolean lhsIsArray = lhsAndRhsValidator.getLhsIsArray();
        Boolean rhsIsArray = lhsAndRhsValidator.getRhsIsArray();
        boolean externalModuleCall = lhsAndRhsValidator.isExternalModuleCall();

        if (lhsIsArray == null && rhsIsArray == null)
        {
            throwUndeclaredVariableSemanticErrorIfNotExternalModuleCall(lhsAndRhsValidator, externalModuleCall);
            return;
        }
        if (rhsIsArray == null) //type of rhs is unknown because it is an external module call, assume best case for the comparison
        {
            throwUndeclaredVariableSemanticErrorIfNotExternalModuleCall(lhsAndRhsValidator, externalModuleCall);
            //lhsIsArray is not null, because if it was, last if condition would have been true
            //which means that we know the type of rhs
            //assume the best case for the comparison, i.e., that the lhs is the same type as the rhs
            isValidComparisonWithAtLeastOneArrayOperand(ASTExprtest, st, lhsIsArray, lhsIsArray);
            return;
        } else if (lhsIsArray == null)
        {
            boolean errorThrown = throwUndeclaredVariableSemanticErrorIfNotExternalModuleCall(lhsAndRhsValidator, externalModuleCall);
            if (errorThrown)
                return;
            //same opposite of the above but same result
            isValidComparisonWithAtLeastOneArrayOperand(ASTExprtest, st, lhsIsArray, lhsIsArray);
            return;
        } else if (lhsIsArray || rhsIsArray) //at least one of the operands is an array
        {
            isValidComparisonWithAtLeastOneArrayOperand(ASTExprtest, st, lhsIsArray, rhsIsArray);
        }
        if (rhsIsArray == null && externalModuleCall)
            return;
        if (!lhsIsArray && !rhsIsArray) //if both operands are int, the comparison is correct for certain
            return;

    }

    /**
     * This method displays the error in case it is not an external module call. It is used to avoid code duplication since is it is needed in multiple places.
     * @param lhsAndRhsValidator An object os the class LhsAndRhsValidator which is used to validate the correct semantic for left hand side and right hand side of an expression.
     * @param externalModuleCall A boolean value indicating weather it is anm external module call or not.
     * @return A boolean value indicating weather a semantic error occurred or not.
     */
    private static boolean throwUndeclaredVariableSemanticErrorIfNotExternalModuleCall(LhsAndRhsValidator lhsAndRhsValidator, boolean externalModuleCall)
    {
        if (!externalModuleCall)
        {
            lhsAndRhsValidator.handleSemanticError("Undeclared variable on line ");
            return true;
        }
        else
            return false;
    }

    /**
     * This method displays checks if the semantic is valid with at least one array operand. It is used to avoid code duplication since is it is needed used im multiple places.
     * @param ASTExprtest  A class representing the Exprtest node on the AST. This is used to access information about the node.
     * @param st An Object that represents the already filled symbol table.
     * @param lhsIsArray A boolean value indicating weather the left hand side of the expression is an array or not.
     * @param rhsIsArray A boolean value indicating weather the right hand side of the expression is an array or not.
     * @return A boolean value indicating the comparison is valid or not.
     */
    private static boolean isValidComparisonWithAtLeastOneArrayOperand(Yal2Jvm.ASTExprtest ASTExprtest, ST st, Boolean lhsIsArray, Boolean rhsIsArray)
    {
        if (lhsIsArray != rhsIsArray) //if they are not both arrays, display error immediately
        {
            System.out.println("Comparison between array and int is not allowed on line " + ASTExprtest.getLine());
            st.declareCodeHasSemanticErrors();
            return false;
        }

        if (!ASTExprtest.rela_Op.equals("==") && !ASTExprtest.rela_Op.equals("!="))
        {
            System.out.println("Comparison between two arrays not allowed unless with operators == or != on line " + ASTExprtest.getLine());
            st.declareCodeHasSemanticErrors();
            return false;
        }
        return true;
    }
}