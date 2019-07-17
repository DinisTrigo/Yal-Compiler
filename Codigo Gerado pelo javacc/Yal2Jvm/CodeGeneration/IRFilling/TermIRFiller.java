package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.*;
import Yal2Jvm.CodeGeneration.IR.BasicBlock;
import Yal2Jvm.CodeGeneration.IR.ControlFlowGraph;
import Yal2Jvm.CodeGeneration.IR.Method;
import Yal2Jvm.CodeGeneration.IR.Operand;

import java.util.ArrayList;

/**
 * This class implements the necessary actions to fill the IR for an Term node.
 */
public class TermIRFiller
{
    /**
     * Method to get the Term node as Operand (part of the IR structure).
     * @param ASTTerm The ASTTerm node of which to get the Operand.
     * @param st An Object that represents the already filled symbol table.
     * @param methodIR A class representing the intermediate representation of the Method where the Term is contained.
     * @param currentBlock A class representing the current block on the IR of this node. This is used to perform branching on certain conditions.
     * @return An object of the class Operand (part of the IR structure) representing Term node.
     */
    public static Operand getOperand(ASTTerm ASTTerm, ST st, Method methodIR, BasicBlock currentBlock)
    {
        boolean isVariable = true;
        String value = null;
        boolean shouldNegate = false;
        Boolean isGlobal = false;
        boolean isArray = false;
        if (ASTTerm.getTermInt() != null)
        {
            value = ASTTerm.getTermInt().toString();
            if (ASTTerm.getTermInt() < 0)
                value = "-" + value;
            if (ASTTerm.getAddSub_Op() != null)
                if (ASTTerm.getAddSub_Op().equals("-"))
                    value = "-" + value;

            isVariable = false;
        } else if (ASTTerm.getChildren()[0] instanceof ASTCall)
        {
            ASTCall astcall = (ASTCall) ASTTerm.getChildren()[0];

            ArrayList<Operand> args = CallIRFiller.getArgsAsIROperands(astcall, (FunctionST) st);
            ControlFlowGraph cfg = methodIR.parentCFG;
            Method calledMethod = cfg.methodRegistry.get(astcall.outerCalledID);
            Operand operand;
            if (calledMethod == null)
            {
                String outerCalledID = ((ASTCall) ASTTerm.getChildren()[0]).outerCalledID;
                String innerCalledID = ((ASTCall) ASTTerm.getChildren()[0]).innerCalledID;

                if (outerCalledID.equals("io"))
                    operand = CallIRFiller.getMethodOperandInCaseOfExternalCallForIOModule(args, methodIR,
                            innerCalledID, ASTTerm.getLine(), currentBlock);
                else
                {
                    operand = CallIRFiller.getMethodOperandInCaseOfExternalCall(args, methodIR,
                            outerCalledID, innerCalledID, ASTTerm, st);
                    if (operand == null) return null;
                }
            } else
                operand = new Operand(calledMethod, args);
            return operand;
        } else if (ASTTerm.getChildren()[0] instanceof ASTArrayAccess)
        {
            ASTArrayAccess arrayAccess = (ASTArrayAccess) ASTTerm.getChildren()[0];
            ASTIndex index = (ASTIndex) arrayAccess.getChildren()[0];
            Operand indexOperand = IndexIRFiller.getOperand(index, st, methodIR);

            value = ((ASTArrayAccess) ASTTerm.getChildren()[0]).arrayID;

            if (((FunctionST) st).locals.get(value) != null)
                isGlobal = false;
            else
                isGlobal = true;

            Operand operand = new Operand(value, indexOperand, isGlobal);
            if (shouldNegate)
                operand.shouldNegate = true;
            return operand;
        } else if (ASTTerm.getChildren()[0] instanceof ASTScalarAccess)
        {
            ASTScalarAccess scalarAccess = (ASTScalarAccess) ASTTerm.getChildren()[0];

            Operand operand = ScalarAccessIRFiller.getOperand(scalarAccess, st);
            if (ASTTerm.getAddSub_Op() != null)
                if (ASTTerm.getAddSub_Op().equals("-"))
                    operand.shouldNegate = true;
            return operand;
        }
        Operand operand = new Operand(value, isVariable, isArray, isGlobal);
        if (shouldNegate)
            operand.shouldNegate = true;
        return operand;
    }
}