package Yal2Jvm.SemanticAnalysis.InitializationVerifier;

import Yal2Jvm.ASTStmtlst;
import Yal2Jvm.ST;
import Yal2Jvm.SimpleNode;

import java.util.HashSet;

/**
 * This class implements the necessary actions to verify the initialization for an If node.
 */
public class IfInitializationVerifier
{
    /**
     * A class representing the If node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTIf ASTIf;

    /**
     * Constructor for this class receives an ASTIf objects as parameter that will be used to set the attribute.
     * @param ASTIf A class representing the If node on the AST. This is used to access information about the node.
     */
    public IfInitializationVerifier(Yal2Jvm.ASTIf ASTIf)
    {
        this.ASTIf = ASTIf;
    }

    /**
     * Method that verifies the initialization for an If node.
     * @param initializedVariables A set containing the currently initialized variables (in the current node of the AST). As new variables get initialized, they are added to the set.
     * @param st An Object that represents the already filled symbol table.
     * @return The set of initialized variables after the current AST node (new variables could eventually have been added to the set it received as parameter if they actually for initialized in the current AST node).
     */
    public HashSet verifyInitialization(HashSet<String> initializedVariables, ST st)
    {
        HashSet<String> newInitedVars = ((SimpleNode) ASTIf.getChildren()[0]).verifyInitialization(new HashSet<String>(initializedVariables), st);
        if (ASTIf.getChildren().length == 2)
            return newInitedVars;

        ASTStmtlst stmtlstThen = (ASTStmtlst) ASTIf.getChildren()[1];
        ASTStmtlst stmtlstElse = (ASTStmtlst) ASTIf.getChildren()[2];
        HashSet<String> stmtlstThenInitedVars = stmtlstThen.verifyInitialization(new HashSet<String>(newInitedVars), st);
        HashSet<String> stmtlstElseInitedVars = stmtlstElse.verifyInitialization(new HashSet<String>(newInitedVars), st);
        //intersect the then and else sets to create a new set hat only contains the vars that are initialized in both of the sets; assign the new set to the stmtlstThenInitedVars
        stmtlstThenInitedVars.retainAll(stmtlstElseInitedVars);
        //add the variables that are common to the Then and Else to the newInitedVars set
        newInitedVars.addAll(stmtlstThenInitedVars);

        return newInitedVars;
    }
}