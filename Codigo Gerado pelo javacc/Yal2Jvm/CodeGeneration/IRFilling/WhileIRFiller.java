package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.ASTExprtest;
import Yal2Jvm.ASTStmtlst;
import Yal2Jvm.CodeGeneration.IR.*;
import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the IR for a While node.
 */
public class WhileIRFiller
{
    /**
     * A class representing the While node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTWhile ASTWhile;

    /**
     * Constructor for this class receives a ASTWhile objects as parameter that will be used to set the attribute.
     * @param ASTWhile A class representing the While node on the AST. This is used to access information about the node.
     */
    public WhileIRFiller(Yal2Jvm.ASTWhile ASTWhile)
    {
        this.ASTWhile = ASTWhile;
    }

    /**
     * Method that fills the IR for the second pass for a While node.
     * On the second pass, function bodies are filled in the intermediate representation.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @param currentBlock A class representing the current block on the IR of this node. This is used to perform branching on certain conditions.
     */
    public void fillIRSecondPass(ST st, IRContainer irContainer, BasicBlock currentBlock)
    {
        Method currentMethod = (Method) irContainer;
        ASTExprtest exprtest = (ASTExprtest) ASTWhile.getChildren()[0];
        Operand lhsOperand = ExprtestIRFiller.getLhsOperand(exprtest, st, irContainer);
        Operand rhsOperand = exprtest.getRhsOperand(st, irContainer, currentBlock);
        String relaOp = exprtest.getRelaopStr(st, irContainer);
        ASTStmtlst body = (ASTStmtlst) ASTWhile.getChildren()[1];

        WhileBlock whileBlock = new WhileBlock(currentMethod.localVariables, currentMethod, Method.generateBlockID(), relaOp, lhsOperand, rhsOperand);
        BasicBlock bodyBlock = whileBlock.getWhileBody();
        ASTWhile.setBlocks(new BasicBlock[1]);
        ASTWhile.getBlocks()[0] = whileBlock;
        body.fillIRSecondPass(st, irContainer, bodyBlock);
        if (currentBlock instanceof WhileBlock)
            ((WhileBlock) currentBlock).addBlock(whileBlock);
        else if (currentBlock instanceof BranchBlock)
            ((BranchBlock) currentBlock).addBlock(whileBlock);
        else
            currentMethod.addBlock(whileBlock);
    }
}