package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.*;
import Yal2Jvm.CodeGeneration.IR.ControlFlowGraph;
import Yal2Jvm.CodeGeneration.IR.IRContainer;

/**
 * This class implements the necessary actions to fill the IR for an Declaration node.
 */
public class DeclarationIRFiller
{
    /**
     * A class representing the Declaration node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTDeclaration ASTDeclaration;

    /**
     * Constructor for this class receives an ASTDeclaration objects as parameter that will be used to set the attribute.
     * @param ASTDeclaration A class representing the Declaration node on the AST. This is used to access information about the node.
     */
    public DeclarationIRFiller(Yal2Jvm.ASTDeclaration ASTDeclaration)
    {
        this.ASTDeclaration = ASTDeclaration;
    }

    /**
     * Method that fills the IR for the second pass for a Declaration node.
     * On the first pass, only function definitions are filled in the intermediate representation. Function bodies will be filled in the second pass.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     */
    public void fillIRFirstPass(ST st, IRContainer irContainer)
    {
        SimpleNode lhs = (SimpleNode) ASTDeclaration.getChildren()[0];
        String lhsID;
        boolean isArray;
        if (lhs instanceof ASTArrayElement)
        {
            lhsID = ((ASTArrayElement) lhs).arrayID;
            isArray = true;
        } else if (lhs instanceof ASTScalarElement)
        {
            lhsID = ((ASTScalarElement) lhs).scalarID;
            isArray = false;
        } else
            return;

        String defaultValueString;
        Integer arraySize = null;
        if (ASTDeclaration.getChildren().length > 1)
            arraySize = ((ASTArraySize) ASTDeclaration.getChildren()[1]).sizeArray;

        if (ASTDeclaration.getAssignValue() != null || arraySize != null)
        {
            if (ASTDeclaration.getAssignValue() != null)
            {
                defaultValueString = ASTDeclaration.getAssignValue().toString();
                ((ControlFlowGraph) irContainer).addGlobalVariable(lhsID, isArray, defaultValueString);
            } else //(assignValue != null)
                ((ControlFlowGraph) irContainer).addGlobalVariable(lhsID, true, arraySize.toString());
        } else
        {
            ((ControlFlowGraph) irContainer).addGlobalVariable(lhsID, isArray);
        }
    }
}