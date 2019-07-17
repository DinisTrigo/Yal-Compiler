package Yal2Jvm.SemanticAnalysis.InitializationVerifier;

import Yal2Jvm.*;

import java.util.HashSet;

/**
 * This class implements the necessary actions to verify the initialization for an Function node.
 */
public class FunctionInitializationVerifier
{
    /**
     * A class representing the Function node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTFunction ASTFunction;

    /**
     * Constructor for this class receives a ASTFunction objects as parameter that will be used to set the attribute.
     * @param ASTFunction A class representing the Function node on the AST. This is used to access information about the node.
     */
    public FunctionInitializationVerifier(Yal2Jvm.ASTFunction ASTFunction)
    {
        this.ASTFunction = ASTFunction;
    }

    /**
     * Method that verifies the initialization for a Function node.
     * @param initializedVariables A set containing the currently initialized variables (in the current node of the AST). As new variables get initialized, they are added to the set.
     * @param st An Object that represents the already filled symbol table.
     * @return The set of initialized variables after the current AST node (new variables could eventually have been added to the set it received as parameter if they actually for initialized in the current AST node).
     */
    public HashSet verifyInitialization(HashSet<String> initializedVariables, ST st)
    {
        FunctionST thisFunctionST = ((ModuleST) st).getFunctionST(ASTFunction.getFunctionId());
        SimpleNode parametersVarList = null;
        for (Node node : ASTFunction.getChildren())
        {
            if (node instanceof ASTVarlist)
            {
                parametersVarList = (SimpleNode) node;
                break;
            }
        }
        if (parametersVarList != null)
        {
            addParametersToInitializedVariablesSet(initializedVariables, parametersVarList);
        }

        initializedVariables.addAll(ASTFunction.verifyInitializationSuper(new HashSet<String>(initializedVariables), thisFunctionST));

        String returnVarID = ASTFunction.getReturnVariableID();
        if (returnVarID == null) //return type is void, perform no verification on return variable initialization
            return initializedVariables;

        if (!initializedVariables.contains(returnVarID)) //if return is not void, the variable being returned must be initialized; throws exception if is not
        {
            System.out.println("return variable " + returnVarID + " may not have been initialized for function " + ASTFunction.getFunctionId());
            st.declareCodeHasSemanticErrors();
            return initializedVariables;
        }

        return initializedVariables;
    }

    /**
     * Auxiliary method for the verifyInitialization method of this class. Adds the newly declared variables to the set of initialized variables.
     * @param initializedVariables A set containing the currently initialized variables (in the current node of the AST). As new variables get initialized, they are added to the set.
     * @param varListNode The Varlist node which contains the variables to be added to the set.
     */
    private void addParametersToInitializedVariablesSet(HashSet<String> initializedVariables, SimpleNode varListNode)
    {
        Node[] varListNodeChildren = varListNode.getChildren();
        for (Node node : varListNodeChildren)
        {
            String nodeID;
            if (node instanceof ASTArrayElement)
                nodeID = ((ASTArrayElement) node).arrayID;
            else if (node instanceof ASTScalarElement)
                nodeID = ((ASTScalarElement) node).scalarID;
            else
                continue;

            initializedVariables.add(nodeID);
        }
    }

}