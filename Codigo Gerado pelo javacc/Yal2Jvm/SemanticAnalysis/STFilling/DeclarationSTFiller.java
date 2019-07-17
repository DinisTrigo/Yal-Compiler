package Yal2Jvm.SemanticAnalysis.STFilling;

import Yal2Jvm.*;

/**
 * This class implements the necessary actions to fill the ST for an Declaration node.
 */
public class DeclarationSTFiller
{
    /**
     * A class representing the Declaration node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTDeclaration ASTDeclaration;

    /**
     * Constructor for this class receives an ASTDeclaration objects as parameter that will be used to set the attribute.
     * @param ASTDeclaration A class representing the Declaration node on the AST. This is used to access information about the node.
     */
    public DeclarationSTFiller(Yal2Jvm.ASTDeclaration ASTDeclaration)
    {
        this.ASTDeclaration = ASTDeclaration;
    }

    /**
     * Method that fills the ST for an Declaration node.
     * On the first pass, only function definitions are filled in the symbol table. Function bodies will be filled in the second pass.
     * @param st An Object that represents the already filled symbol table.
     */
    public void fillStFirstPass(ST st)
    {
        if (ASTDeclaration.getChildren()[0] instanceof ASTScalarElement)
        {
            if (ASTDeclaration.getChildren().length == 1)
            {
                String scalarID = ((ASTScalarElement) ASTDeclaration.getChildren()[0]).scalarID;
                if (st.isVariableArray(scalarID) != null)
                    if (st.isVariableArray(scalarID) && ASTDeclaration.getAssignValue() == null) //display error if trying to redeclare variable (needs to ensure that it is not array initialization, because that is correct)
                    {
                        System.out.println("Redeclaring global variable " + scalarID + " on line " + ASTDeclaration.getLine());
                        st.declareCodeHasSemanticErrors();
                        return;
                    } else if (!st.isVariableArray(scalarID))
                    {
                        System.out.println("Redeclaring global variable " + scalarID + " on line " + ASTDeclaration.getLine());
                        st.declareCodeHasSemanticErrors();
                        return;
                    }

                ((SimpleNode) ASTDeclaration.getChildren()[0]).fillStFirstPass(st);
            } else
            {
                String scalarID = ((ASTScalarElement) ASTDeclaration.getChildren()[0]).scalarID;
                if (ASTDeclaration.getChildren()[1] instanceof ASTArraySize)
                {
                    if (st.isVariableArray(scalarID) != null)
                    {
                        System.out.println("Redeclaring global variable " + scalarID + " on line " + ASTDeclaration.getLine());
                        st.declareCodeHasSemanticErrors();
                        return;
                    }
                    st.addVariable(scalarID, true);
                } else
                {
                    if (st.isVariableArray(scalarID) != null)
                    {
                        System.out.println("Redeclaring global variable " + scalarID + " on line " + ASTDeclaration.getLine());
                        st.declareCodeHasSemanticErrors();
                        return;
                    }
                    st.addVariable(scalarID, false);
                }
            }
        } else if (ASTDeclaration.getChildren()[0] instanceof ASTArrayElement)
        {
            ((SimpleNode) ASTDeclaration.getChildren()[0]).fillStFirstPass(st);
        } else
            return;
    }
}