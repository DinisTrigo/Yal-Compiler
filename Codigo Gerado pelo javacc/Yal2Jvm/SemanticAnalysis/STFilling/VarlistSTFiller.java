package Yal2Jvm.SemanticAnalysis.STFilling;

import Yal2Jvm.ASTArrayElement;
import Yal2Jvm.ASTScalarElement;
import Yal2Jvm.FunctionST;
import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the ST for an Varlist node.
 */
public class VarlistSTFiller
{
    /**
     * A class representing the Varlist node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTVarlist ASTVarlist;

    /**
     * Constructor for this class receives an ASTVarlist objects as parameter that will be used to set the attribute.
     * @param ASTVarlist A class representing the Varlist node on the AST. This is used to access information about the node.
     */
    public VarlistSTFiller(Yal2Jvm.ASTVarlist ASTVarlist)
    {
        this.ASTVarlist = ASTVarlist;
    }

    /**
     * Method that fills the ST for an Varlist node.
     * On the second pass, function bodies are filled in the symbol table.
     * @param st An Object that represents the already filled symbol table.
     */
    public void fillStFirstPass(ST st)
    {
        for (int i = 0; i < ASTVarlist.getChildren().length; i++)
        {
            boolean isArray = false;
            String childId;
            if (ASTVarlist.getChildren()[i] instanceof ASTArrayElement)
            {
                isArray = true;
                childId = ((ASTArrayElement) ASTVarlist.getChildren()[i]).arrayID;
            } else
                childId = ((ASTScalarElement) ASTVarlist.getChildren()[i]).scalarID;

            FunctionST functionST = (FunctionST) st;
            functionST.addParamType(isArray);
            functionST.addParamName(childId);
            functionST.addVariable(childId, isArray);
        }
    }
}