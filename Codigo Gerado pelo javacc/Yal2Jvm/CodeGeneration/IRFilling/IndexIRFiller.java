package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.FunctionST;
import Yal2Jvm.CodeGeneration.IR.Method;
import Yal2Jvm.CodeGeneration.IR.Operand;
import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the IR for an Index node.
 */
public class IndexIRFiller
{
    /**
     * Method to get the Index node as Operand (part of the IR structure).
     * @param ASTIndex The ASTIndex node of which to get the Operand.
     * @param st An Object that represents the already filled symbol table.
     * @param methodIR A class representing the intermediate representation of the Method where the Index is contained.
     * @return An object of the class Operand (part of the IR structure) representing Index node.
     */
    public static Operand getOperand(Yal2Jvm.ASTIndex ASTIndex, ST st, Method methodIR)
    {
        boolean isGlobal = false;
        boolean isArray = false;
        boolean isVariable = false;
        Operand operand;
        if (ASTIndex.getVarId() == null)
        {
            String value = ASTIndex.getIndex() + "";
            operand = new Operand(value, isVariable, isArray, isGlobal);
        } else
        {
            isVariable = true;
            String id = ASTIndex.getVarId();
            if (((FunctionST) st).locals.get(id) != null)
                isGlobal = false;
            else
                isGlobal = true;
            operand = new Operand(ASTIndex.getVarId(), isVariable, isArray, isGlobal);
        }
        return operand;
    }
}