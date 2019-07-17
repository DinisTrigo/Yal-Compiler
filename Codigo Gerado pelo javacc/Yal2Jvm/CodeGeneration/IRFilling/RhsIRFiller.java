package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.ASTArraySize;
import Yal2Jvm.ASTTerm;
import Yal2Jvm.CodeGeneration.IR.*;
import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the IR for an Rhs node.
 */
public class RhsIRFiller
{

    /**
     * Method to get the IRNode (part of the IR structure) that represents a Rhs node.
     * @param ASTRhs The ASTRhs of which to get the IRNode.
     * @param st An Object that represents the already filled symbol table.
     * @param methodIR A class representing the intermediate representation of the Method where the Index is contained.
     * @param storeOperand An object of class Operand (part of the IR structure) that represents the operand where the result of Rhs will be stored.
     * @param currentBlock A class representing the current block on the IR of this node. This is used to perform branching on certain conditions.
     * @return The IRNode (part of the IR structure) that represents the Rhs node it receives as parameter.
     */
    public static IRNode getIRNode(Yal2Jvm.ASTRhs ASTRhs, ST st, Method methodIR, Operand storeOperand, BasicBlock currentBlock)
    {
        if (ASTRhs.getChildren().length == 1)
        {
            if (ASTRhs.getChildren()[0] instanceof ASTArraySize)
            {
                Operand arraySizeOperand = ((ASTArraySize) ASTRhs.getChildren()[0]).getOperand(st);
                Operand arrayCreationOperand = new Operand(arraySizeOperand);
                AssignNode assignNode = new AssignNode(storeOperand, arrayCreationOperand,
                        methodIR.localVariables, null);
                return assignNode;
            } else if (ASTRhs.getChildren()[0] instanceof ASTTerm)
            {
                Operand operand = TermIRFiller.getOperand(((ASTTerm) ASTRhs.getChildren()[0]), st, methodIR, currentBlock);
                AssignNode assignNode = new AssignNode(storeOperand, operand,
                        methodIR.localVariables, null);
                return assignNode;
            } else
                return null;
        } else  //second children is another term, and this Rhs consists of an arithmetic operation
        {
            ASTTerm firstTerm = (ASTTerm) ASTRhs.getChildren()[0];
            ASTTerm secondTerm = (ASTTerm) ASTRhs.getChildren()[1];
            String operator = ASTRhs.getOp();
            Operand firstOperand = TermIRFiller.getOperand(firstTerm, st, methodIR, currentBlock);
            Operand secondOperand = TermIRFiller.getOperand(secondTerm, st, methodIR, currentBlock);
            ArithNode arithNode = new ArithNode(operator, firstOperand,
                    secondOperand, methodIR.localVariables, null, storeOperand);
            return arithNode;
        }
    }
}