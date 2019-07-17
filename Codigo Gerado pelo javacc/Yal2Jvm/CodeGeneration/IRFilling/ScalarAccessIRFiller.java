package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.ASTScalarAccess;
import Yal2Jvm.FunctionST;
import Yal2Jvm.CodeGeneration.IR.Operand;
import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the IR for an ScalarAccess node.
 */
public class ScalarAccessIRFiller
{
    /**
     * Method to get the ScalarAccess node as Operand (part of the IR structure).
     * @param scalarAccess The ASTScalarAccess node of which to get the Operand.
     * @param st An Object that represents the already filled symbol table.
     * @return An object of the class Operand (part of the IR structure) representing ScalarAccess node.
     */
    public static Operand getOperand(ASTScalarAccess scalarAccess, ST st)
    {
        boolean isVariable = true;
        String value = null;
        boolean shouldNegate = false;
        Boolean isGlobal = false;
        boolean isArray = false;

        if (scalarAccess.size != null)
            isArray = true;

        isVariable = true;

        String scalarID = scalarAccess.id;
        if (st.isVariableArray(scalarID))
            isArray = true;
        value = scalarID;
        if (((FunctionST) st).locals.get(value) != null)
            isGlobal = false;
        else
            isGlobal = true;


        Operand arrayOperand = new Operand(value, isVariable, isArray, isGlobal);
        if (scalarAccess.size == null)
            return arrayOperand;
        else
        {
            return new Operand(arrayOperand, true);
        }
    }
}