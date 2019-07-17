package Yal2Jvm.SemanticAnalysis.InitializationVerifier;

import Yal2Jvm.ASTArrayElement;
import Yal2Jvm.ASTScalarElement;
import Yal2Jvm.ST;
import Yal2Jvm.SimpleNode;

import java.util.HashSet;

/**
 * This class implements the necessary actions to verify the initialization for a Declaration node.
 */
public class DeclarationInitializationVerifier
{
    /**
     * A class representing the Declaration node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTDeclaration ASTDeclaration;

    /**
     * Constructor for this class receives a ASTDeclaration objects as parameter that will be used to set the attribute.
     * @param ASTDeclaration A class representing the Declaration node on the AST. This is used to access information about the node.
     */
    public DeclarationInitializationVerifier(Yal2Jvm.ASTDeclaration ASTDeclaration)
    {
        this.ASTDeclaration = ASTDeclaration;
    }

    /**
     * Method that verifies the initialization for a Declaration node.
     * @param initializedVariables A set containing the currently initialized variables (in the current node of the AST). As new variables get initialized, they are added to the set.
     * @param st An Object that represents the already filled symbol table.
     * @return The set of initialized variables after the current AST node (new variables could eventually have been added to the set it received as parameter if they actually for initialized in the current AST node).
     */
    public HashSet verifyInitialization(HashSet<String> initializedVariables, ST st)
    {
        if (ASTDeclaration.getAssignValue() != null)
        {
            SimpleNode lhs = (SimpleNode) ASTDeclaration.getChildren()[0];
            String lhsID;
            if (lhs instanceof ASTArrayElement)
                lhsID = ((ASTArrayElement) lhs).arrayID;
            else if (lhs instanceof ASTScalarElement)
                lhsID = ((ASTScalarElement) lhs).scalarID;
            else
                return initializedVariables;

            initializedVariables.add(lhsID);
        }

        return initializedVariables;
    }
}