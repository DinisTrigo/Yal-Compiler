package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.ASTExprtest;
import Yal2Jvm.ASTStmtlst;
import Yal2Jvm.CodeGeneration.IR.*;
import Yal2Jvm.ST;

/**
 * This class implements the necessary actions to fill the IR for an If node.
 */
public class IfIRFiller
{
    /**
     * A class representing the If node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTIf ASTIf;

    /**
     * Constructor for this class receives an ASTIf objects as parameter that will be used to set the attribute.
     * @param ASTIf A class representing the If node on the AST. This is used to access information about the node.
     */
    public IfIRFiller(Yal2Jvm.ASTIf ASTIf)
    {
        this.ASTIf = ASTIf;
    }

    /**
     * Method that fills the IR for the second pass for an If node.
     * On the second pass, function bodies are filled in the intermediate representation.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @param currentBlock A class representing the current block on the IR of this node. This is used to perform branching on certain conditions.
     */
    public void fillIRSecondPass(ST st, IRContainer irContainer, BasicBlock currentBlock)
    {
        Method currentMethod = (Method) irContainer;
        ASTExprtest exprtest = (ASTExprtest) ASTIf.getChildren()[0];
        Operand lhsOperand = ExprtestIRFiller.getLhsOperand(exprtest, st, irContainer);
        Operand rhsOperand = exprtest.getRhsOperand(st, irContainer, currentBlock);
        String relaOp = exprtest.getRelaopStr(st, irContainer);
        ASTStmtlst thenStmt = (ASTStmtlst) ASTIf.getChildren()[1];
        ASTStmtlst elseStmt = null;
        if (ASTIf.getChildren().length > 2) //has else statement list
            elseStmt = (ASTStmtlst) ASTIf.getChildren()[2];

        boolean hasElseStmt = (elseStmt != null);

        IfBlock ifBlock = new IfBlock(currentMethod.localVariables, currentMethod, Method.generateBlockID(), relaOp, lhsOperand, rhsOperand, hasElseStmt);
        BasicBlock thenBlock = ifBlock.getTrueBranch();
        thenStmt.fillIRSecondPass(st, irContainer, thenBlock);
        if (elseStmt != null)
        {
            BasicBlock elseBlock = ifBlock.getFalseBranch();
            elseStmt.fillIRSecondPass(st, irContainer, elseBlock);
        }
        ASTIf.setBlocks(new BasicBlock[1]);
        ASTIf.getBlocks()[0] = ifBlock;
        ASTIf.setBlocks(ASTIf.getBlocks());
        if (currentBlock instanceof WhileBlock)
            ((WhileBlock) currentBlock).addBlock(ifBlock);
        else if (currentBlock instanceof BranchBlock)
            ((BranchBlock) currentBlock).addBlock(ifBlock);
        else
            currentMethod.addBlock(ifBlock);
    }
}