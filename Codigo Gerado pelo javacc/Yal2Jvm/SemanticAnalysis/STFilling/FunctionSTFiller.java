package Yal2Jvm.SemanticAnalysis.STFilling;

import Yal2Jvm.*;

/**
 * This class implements the necessary actions to fill the ST for a Function node.
 */
public class FunctionSTFiller
{
    /**
     * A class representing the Function node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTFunction ASTFunction;

    /**
     * Constructor for this class receives a ASTFunction objects as parameter that will be used to set the attribute.
     * @param ASTFunction A class representing the Function node on the AST. This is used to access information about the node.
     */
    public FunctionSTFiller(Yal2Jvm.ASTFunction ASTFunction)
    {
        this.ASTFunction = ASTFunction;
    }

    /**
     * Method that fills the ST for a Function node.
     * On the second pass, function bodies are filled in the symbol table.
     * @param st An Object that represents the already filled symbol table.
     */
    public void fillStFirstPass(ST st)
    {
        FunctionST functionST = new FunctionST();
        functionST.globalST = (ModuleST) st;
        ((ModuleST) st).addFunction(ASTFunction.getFunctionId(), functionST);
        Boolean returnIsArray;
        String returnVarId;
        if (ASTFunction.getChildren()[0] instanceof ASTArrayElement)
        {
            returnIsArray = true;
            returnVarId = ((ASTArrayElement) ASTFunction.getChildren()[0]).arrayID;
            functionST.setReturnIsArray(returnIsArray);
            functionST.setReturnId(returnVarId);
            functionST.addVariable(returnVarId, returnIsArray);
        } else if (ASTFunction.getChildren()[0] instanceof ASTScalarElement)
        {
            returnIsArray = false;
            returnVarId = ((ASTScalarElement) ASTFunction.getChildren()[0]).scalarID;
            functionST.setReturnIsArray(returnIsArray);
            functionST.setReturnId(returnVarId);
            functionST.addVariable(returnVarId, returnIsArray);
        }
        ASTVarlist varList = null;
        for (Node node : ASTFunction.getChildren())
        {
            if (node instanceof ASTVarlist)
            {
                varList = (ASTVarlist) node;
                break;
            }
        }
        if (varList == null)
            return;
        varList.fillStFirstPass(functionST);
    }
}