package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.*;
import Yal2Jvm.CodeGeneration.IR.*;

/**
 * This class implements the necessary actions to fill the IR for an Assign node.
 */
public class AssignIRFiller
{
    /**
     * A class representing the Assign node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTAssign ASTAssign;

    /**
     * Constructor for this class receives an ASTAssign objects as parameter that will be used to set the attribute.
     * @param ASTAssign A class representing the Assign node on the AST. This is used to access information about the node.
     */
    public AssignIRFiller(Yal2Jvm.ASTAssign ASTAssign)
    {
        this.ASTAssign = ASTAssign;
    }

    /**
     * Method that fills the IR for the second pass for an Assign node.
     * On the second pass, function bodies are filled in the intermediate representation.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @param currentBlock A class representing the current block on the IR of this node. This is used to perform branching on certain conditions.
     */
    public void fillIRSecondPass(ST st, IRContainer irContainer, BasicBlock currentBlock)
    {
        SimpleNode lhs = (SimpleNode) ASTAssign.getChildren()[0];
        SimpleNode lhsChild = (SimpleNode) (((SimpleNode) lhs).getChildren())[0];
        String lhsID;
        if (lhsChild instanceof ASTArrayAccess)
            lhsID = ((ASTArrayAccess) lhsChild).arrayID;
        else
        {
            lhsID = ((ASTScalarAccess) lhsChild).id;
        }
        Operand lhsOperand = getLhsOperand(st, lhsChild, lhsID, irContainer);
        ASTRhs rhs = (ASTRhs) ASTAssign.getChildren()[1];
        Method currentMethodIR = (Method) irContainer;
        IRNode IRNode = RhsIRFiller.getIRNode(rhs, st, currentMethodIR, lhsOperand, currentBlock);
        if (currentBlock != null)
            currentBlock.addNode(IRNode);
        else
            currentMethodIR.addNode(IRNode);
    }

    /**
     * Method to get the lhs of an assign node as Operand (part of the IR structure).
     * @param st An Object that represents the already filled symbol table.
     * @param lhsChild The first left hand side child of the node.
     * @param lhsID The id of first the left hand side child of the node.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @return An object of the class Operand (part of the IR structure) representing the left hand side operand of this node.
     */
    private Operand getLhsOperand(ST st, SimpleNode lhsChild, String lhsID, IRContainer irContainer)
    {
        Operand lhsOperand = null;
        if (lhsChild instanceof ASTArrayAccess)
        {
            ASTIndex index = (ASTIndex) lhsChild.getChildren()[0];
            Operand indexOperand = IndexIRFiller.getOperand(index, st, (Method) irContainer);

            boolean isGlobal;
            if (((FunctionST) st).locals.get(lhsID) != null)
                isGlobal = false;
            else
                isGlobal = true;

            lhsOperand = new Operand(lhsID, indexOperand, isGlobal);
        } else if (lhsChild instanceof ASTScalarAccess)
        {
            ASTScalarAccess scalarAccess = (ASTScalarAccess) lhsChild;
            String scalarID = scalarAccess.id;
            boolean isGlobal = !((FunctionST) st).isVariableLocal(scalarID);
            boolean isArray;
            if (st.isVariableArray(scalarID))
                isArray = true;
            else
                isArray = false;
            lhsOperand = new Operand(scalarID, true, isArray, isGlobal);
        }

        return lhsOperand;
    }
}