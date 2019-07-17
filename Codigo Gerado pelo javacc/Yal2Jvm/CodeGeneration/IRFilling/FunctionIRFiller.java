package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.*;
import Yal2Jvm.CodeGeneration.IR.*;

import java.util.ArrayList;

/**
 * This class implements the necessary actions to fill the IR for an Function node.
 */
public class FunctionIRFiller
{
    /**
     * A class representing the Function node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTFunction ASTFunction;

    /**
     * Constructor for this class receives an ASTFunction objects as parameter that will be used to set the attribute.
     * @param ASTFunction A class representing the Function node on the AST. This is used to access information about the node.
     */
    public FunctionIRFiller(Yal2Jvm.ASTFunction ASTFunction)
    {
        this.ASTFunction = ASTFunction;
    }

    /**
     * Method that fills the IR for the first pass for a Function node.
     * On the first pass, only function definitions are filled in the intermediate representation. Function bodies will be filled in the second pass.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     */
    public void fillIRFirstPass(ST st, IRContainer irContainer)
    {
        FunctionST thisFunctionST = ((ModuleST) st).getFunctionST(ASTFunction.getFunctionId());
        ControlFlowGraph globalIR = (ControlFlowGraph) irContainer;
        VariableTable globalVarTable = globalIR.globalVariables;

        Method thisFunctionIR = new Method(globalVarTable, ASTFunction.getFunctionId(),
                getArgumentsTypesInJVMConvention(), thisFunctionST.returnIsArray, globalIR);
        String returnVarName = ASTFunction.getReturnVariableID();
        Operand returnVarOperand = null;
        Boolean returnVarIsArray = thisFunctionST.isVariableArray(returnVarName);
        Boolean returnIsGlobal;
        if (returnVarIsArray != null)
        {
            if (((ModuleST) st).isVariableArray(returnVarName) == null)
                returnIsGlobal = false;
            else
                returnIsGlobal = true;

            returnVarOperand = new Operand(returnVarName, true, returnVarIsArray, returnIsGlobal);
        }
        thisFunctionIR.setReturnVar(returnVarOperand);
        globalIR.addMethod(thisFunctionIR);
        ASTFunction.fillIRFirstPassSuper(thisFunctionST, thisFunctionIR);
    }

    /**
     * Function that allows to get the argument types in JVM convention for this ASTFunction node.
     * @return an ArrayList that contains all the arguments as String in the JVM convention for this ASTFunction node.
     */
    private ArrayList<String> getArgumentsTypesInJVMConvention()
    {
        ASTVarlist varlist = null;
        Node[] children = ASTFunction.getChildren();
        for (Node node : children)
        {
            if (node instanceof ASTVarlist)
            {
                varlist = (ASTVarlist) node;
                break;
            }
        }
        if (varlist == null)
            return new ArrayList<String>();

        return VarlistIRFiller.getArgumentsTypesInJVMConvention(varlist);
    }

    /**
     * Method that fills the IR for the second pass for an Function node.
     * On the second pass, function bodies are filled in the intermediate representation.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     * @param currentBlock A class representing the current block on the IR of this node. This is used to perform branching on certain conditions.
     */
    public void fillIRSecondPass(ST st, IRContainer irContainer, BasicBlock currentBlock)
    {
        FunctionST thisFunctionST = ((ModuleST) st).getFunctionST(ASTFunction.getFunctionId());
        ControlFlowGraph cfg = (ControlFlowGraph) irContainer;
        Method thisMethodIR = cfg.methodRegistry.get(ASTFunction.getFunctionId());
        currentBlock = thisMethodIR.blocks.get(0);
        if (ASTFunction.getChildren() == null)
            return;
        for (int i = 0; i < ASTFunction.getChildren().length; i++)
            ((SimpleNode) ASTFunction.getChildren()[i]).fillIRSecondPass(thisFunctionST, thisMethodIR, currentBlock);
    }
}