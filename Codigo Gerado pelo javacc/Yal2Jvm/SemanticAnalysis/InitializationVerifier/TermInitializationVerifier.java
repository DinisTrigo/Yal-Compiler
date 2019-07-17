package Yal2Jvm.SemanticAnalysis.InitializationVerifier;

import Yal2Jvm.*;

import java.util.HashSet;

/**
 * This class implements the necessary actions to verify the initialization for an Term node.
 */
public class TermInitializationVerifier
{
    public TermInitializationVerifier()
    {
    }

    /**
     * Method that verifies the initialization for an Term node.
     * @param children The children of the Term node of which to verify the initialization.
     * @param termInt The termInt of the node, in case the Term is a literal.
     * @param initializedVariables A set containing the currently initialized variables (in the current node of the AST). As new variables get initialized, they are added to the set.
     * @param st An Object that represents the already filled symbol table.
     * @return The set of initialized variables after the current AST node (new variables could eventually have been added to the set it received as parameter if they actually for initialized in the current AST node).
     */
    public static HashSet verifyInitialization(Node[] children, Integer termInt, HashSet<String> initializedVariables, ST st)
    {
        if (children == null)
            return initializedVariables;

        if (termInt != null)
            return initializedVariables;

        if (children[0] instanceof ASTCall)
        {
            return initializedVariables;
        } else if (children[0] instanceof ASTArrayAccess)
        {
            String arrayID = ((ASTArrayAccess) children[0]).arrayID;
            return verifyInitializationForVariableID(initializedVariables, st, arrayID);
        } else if (children[0] instanceof ASTScalarAccess)
        {
            String scalarID = ((ASTScalarAccess) children[0]).id;
            return verifyInitializationForVariableID(initializedVariables, st, scalarID);
        } else
            return initializedVariables;
    }

    /**
     * Auxiliary method for the method verifyInitialization of this class. Performs verification of initialization in the case the Term is a variable and not a literal neither a Call.
     * @param initializedVariables A set containing the currently initialized variables (in the current node of the AST). As new variables get initialized, they are added to the set.
     * @param st An Object that represents the already filled symbol table.
     * @param variableID A String representing the name of the variable of which to verify the initialization of.
     * @return The set of initialized variables after the current AST node (new variables could eventually have been added to the set it received as parameter if they actually for initialized in the current AST node).
     */
    public static HashSet verifyInitializationForVariableID(HashSet<String> initializedVariables, ST st, String variableID)
    {
        if (((FunctionST) st).globalST.getGlobals().containsKey(variableID))
            return initializedVariables;
        else if (initializedVariables.contains(variableID))
            return initializedVariables;
        else
        {
            System.out.println("Using not initialized variable: " + variableID);
            st.declareCodeHasSemanticErrors();
            return initializedVariables;
        }
    }
}