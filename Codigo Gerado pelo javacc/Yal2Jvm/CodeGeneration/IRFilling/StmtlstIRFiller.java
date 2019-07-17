package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.ASTIf;
import Yal2Jvm.ASTWhile;
import Yal2Jvm.CodeGeneration.IR.*;
import Yal2Jvm.ST;
import Yal2Jvm.SimpleNode;

/**
 * This class implements the necessary actions to fill the IR for an Stmtlst node.
 */
public class StmtlstIRFiller
{
    /**
     * A class representing the Stmtlst node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTStmtlst ASTStmtlst;

    /**
     * Constructor for this class receives an Stmtlst objects as parameter that will be used to set the attribute.
     * @param ASTStmtlst A class representing the ASTStmtlst node on the AST. This is used to access information about the node.
     */
    public StmtlstIRFiller(Yal2Jvm.ASTStmtlst ASTStmtlst)
    {
        this.ASTStmtlst = ASTStmtlst;
    }

    /**
     * Method that fills the IR for the second pass for an Stmtlst node.
     * On the second pass, function bodies are filled in the intermediate representation.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @param currentBlock A class representing the current block on the IR of this node. This is used to perform branching on certain conditions.
     */
    public void fillIRSecondPass(ST st, IRContainer irContainer, BasicBlock currentBlock)
    {
        if (ASTStmtlst.getChildren() == null)
            return;
        Method currentMethod = (Method) irContainer;
        VariableTable varTable = currentMethod.localVariables;
        BasicBlock nextBlock = currentBlock;
        for (int i = 0; i < ASTStmtlst.getChildren().length; i++)
        {
            SimpleNode child = (SimpleNode) ASTStmtlst.getChildren()[i];
            child = (SimpleNode) child.getChildren()[0];
            child.fillIRSecondPass(st, irContainer, nextBlock);
            if (child instanceof ASTIf || child instanceof ASTWhile)
            {
                if (i == child.getChildren().length - 1) //if this is the last child, create an empty block as being the next one; if not, set the next child's block as the block
                {
                    BasicBlock nextBlockEmpty = new BasicBlock(varTable, currentMethod, Method.generateBlockID());

                    BasicBlock[] blocks = child.getBlocks();
                    for (BasicBlock block : blocks)
                    {
                        block.setNextBlock(nextBlockEmpty);
                    }
                    if (currentBlock instanceof WhileBlock)
                        ((WhileBlock) currentBlock).addBlock(nextBlockEmpty);
                    else if (currentBlock instanceof BranchBlock)
                        ((BranchBlock) currentBlock).addBlock(nextBlockEmpty);
                    else
                        currentMethod.addBlock(nextBlockEmpty);
                } else
                {
                    nextBlock = new BasicBlock(varTable, currentMethod, Method.generateBlockID());
                    BasicBlock[] blocks = child.getBlocks();
                    for (BasicBlock block : blocks)
                    {
                        block.setNextBlock(nextBlock);
                    }
                    if (currentBlock instanceof WhileBlock)
                        ((WhileBlock) currentBlock).addBlock(nextBlock);
                    else if (currentBlock instanceof BranchBlock)
                        ((BranchBlock) currentBlock).addBlock(nextBlock);
                    else
                        currentMethod.addBlock(nextBlock);
                }
            }
        }
    }
}