package Yal2Jvm.SemanticAnalysis.STFilling;

import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the ST for an ArrayElement node.
 */
public class ArrayElementSTFiller
{
    /**
     * A class representing the ArrayElement node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTArrayElement ASTArrayElement;

    /**
     * Constructor for this class receives an ASTArrayElement objects as parameter that will be used to set the attribute.
     * @param ASTArrayElement A class representing the ArrayElement node on the AST. This is used to access information about the node.
     */
    public ArrayElementSTFiller(Yal2Jvm.ASTArrayElement ASTArrayElement)
    {
        this.ASTArrayElement = ASTArrayElement;
    }

    /**
     * Method that fills the ST for an ArrayElement node.
     * On the first pass, only function definitions are filled in the symbol table. Function bodies will be filled in the second pass.
     * @param st An Object that represents the already filled symbol table.
     */
    public void fillStFirstPass(ST st)
    {
        if (st.isVariableArray(ASTArrayElement.getArrayID()) != null)
        {
            System.out.println("Redeclaring global variable " + ASTArrayElement.getArrayID() + " on line " + ASTArrayElement.getLine());
            st.declareCodeHasSemanticErrors();
            return;
        }
        st.addVariable(ASTArrayElement.getArrayID(), true);
    }
}