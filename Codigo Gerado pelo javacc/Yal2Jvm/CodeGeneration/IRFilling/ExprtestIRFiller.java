package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.*;
import Yal2Jvm.CodeGeneration.IR.IRContainer;
import Yal2Jvm.CodeGeneration.IR.Operand;

/**
 * This class implements the necessary actions to fill the IR for an Exprtest node.
 */
public class ExprtestIRFiller
{
    /**
     * Method to get the lhs of an Exprtest node as Operand (part of the IR structure).
     * @param ASTExprtest the Exprtest node of which to get the lhs operand.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @return An object of the class Operand (part of the IR structure) representing the left hand side operand of this node.
     */
    public static Operand getLhsOperand(Yal2Jvm.ASTExprtest ASTExprtest, ST st, IRContainer irContainer)
    {
        SimpleNode child = (SimpleNode) ASTExprtest.getChildren()[0];
        String variableID;
        Boolean variableIsArray;
        Boolean isVariable;
        boolean isLocal;
        if (child.getChildren()[0] instanceof ASTArrayAccess)
        {
            ASTArrayAccess arrayAccess = (ASTArrayAccess) child.getChildren()[0];
            variableID = arrayAccess.arrayID;
            isLocal = ((FunctionST) st).locals.containsKey(variableID);
            variableIsArray = false;
            isVariable = true;
        } else if (child.getChildren()[0] instanceof ASTScalarAccess)
        {
            variableID = ((ASTScalarAccess) child.getChildren()[0]).id;
            variableIsArray = st.isVariableArray(variableID);
            if (variableIsArray == null)
            {
                isVariable = false;
                variableIsArray = false;
                isLocal = true;
            } else
            {
                isVariable = true;
                isLocal = ((FunctionST) st).locals.containsKey(variableID);
            }
        } else
            return null;

        return new Operand(variableID, isVariable, variableIsArray, !isLocal);
    }
}