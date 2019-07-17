package Yal2Jvm.SemanticAnalysis.STFilling;

import Yal2Jvm.FunctionST;
import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the ST for an Assign node.
 */
public class AssignSTFiller
{
    /**
     * A class representing the Assign node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTAssign ASTAssign;

    /**
     * Constructor for this class receives an ASTAssign objects as parameter that will be used to set the attribute.
     * @param ASTAssign A class representing the Assign node on the AST. This is used to access information about the node.
     */
    public AssignSTFiller(Yal2Jvm.ASTAssign ASTAssign)
    {
        this.ASTAssign = ASTAssign;
    }

    /**
     * Method that fills the ST for an Assign node.
     * On the second pass, function bodies are filled in the symbol table.
     * @param st An Object that represents the already filled symbol table.
     */
    public void fillStSecondPass(ST st)
    {
        LhsAndRhsValidator lhsAndRhsValidator = new LhsAndRhsValidator(st, ASTAssign.getChildren(), ASTAssign.getLine()).invoke();
        if (lhsAndRhsValidator.is()) return;
        String lhsID = lhsAndRhsValidator.getLhsID();
        Boolean lhsIsArray = lhsAndRhsValidator.getLhsIsArray();
        Boolean rhsIsArray = lhsAndRhsValidator.getRhsIsArray();
        boolean externalModuleCall = lhsAndRhsValidator.isExternalModuleCall();
        if (lhsIsArray == null)
            ((FunctionST) st).addVariable(lhsID, rhsIsArray);
        else if (rhsIsArray == null && externalModuleCall)
            return;
        else
        {
            String exceptMsg = new String();
            if (lhsIsArray != rhsIsArray)
            {
                if (lhsIsArray && !rhsIsArray) //if left hand side is array and right hand side is not, consider it is an initialization of all values to the value of the right hand side and dont display an error
                    return;
                exceptMsg += "Type mismatch for " + lhsID + " expecting: ";
                String expectedVarType;
                if (lhsIsArray)
                {
                    exceptMsg += "array";
                    expectedVarType = "int";
                } else
                {
                    exceptMsg += "int";
                    expectedVarType = "array";
                }
                System.out.println(exceptMsg + ", but found " + expectedVarType + " on line: " + ASTAssign.getLine());
                st.declareCodeHasSemanticErrors();
                return;
            }
        }
    }
}