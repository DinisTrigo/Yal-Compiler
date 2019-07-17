package Yal2Jvm.SemanticAnalysis.InitializationVerifier;

import Yal2Jvm.ASTArrayAccess;
import Yal2Jvm.ASTScalarAccess;
import Yal2Jvm.ST;
import Yal2Jvm.SimpleNode;

import java.util.HashSet;

/**
 * This class implements the necessary actions to verify the initialization for an Assign node.
 */
public class AssignInitializationVerifier
{
    /**
     * A class representing the Assign node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTAssign ASTAssign;

    /**
     * Constructor for this class receives an ASTAssign objects as parameter that will be used to set the attribute.
     * @param ASTAssign A class representing the Assign node on the AST. This is used to access information about the node.
     */
    public AssignInitializationVerifier(Yal2Jvm.ASTAssign ASTAssign)
    {
        this.ASTAssign = ASTAssign;
    }

    /**
     * Method that verifies the initialization for an Assign node.
     * @param initializedVariables A set containing the currently initialized variables (in the current node of the AST). As new variables get initialized, they are added to the set.
     * @param st An Object that represents the already filled symbol table.
     * @return The set of initialized variables after the current AST node (new variables could eventually have been added to the set it received as parameter if they actually for initialized in the current AST node).
     */
    public HashSet verifyInitialization(HashSet<String> initializedVariables, ST st)
    {
        if (ASTAssign.getChildren() == null)
            return initializedVariables;
        for (int i = 0; i < ASTAssign.getChildren().length; i++)
            ((SimpleNode) ASTAssign.getChildren()[i]).verifyInitialization(new HashSet<String>(initializedVariables), st);

        SimpleNode lhs = (SimpleNode) ASTAssign.getChildren()[0];
        SimpleNode lhsChild = (SimpleNode) lhs.getChildren()[0];
        String lhsChildID;
        if (lhsChild instanceof ASTScalarAccess)
            lhsChildID = ((ASTScalarAccess) lhsChild).id;
        else if (lhsChild instanceof ASTArrayAccess)
            lhsChildID = ((ASTArrayAccess) lhsChild).arrayID;
        else
            return initializedVariables;

        initializedVariables.add(lhsChildID);
        return initializedVariables;
    }
}