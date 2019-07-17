package Yal2Jvm;

/* Generated By:JJTree: Do not edit this line. Yal2Jvm.ASTArgumentList.java Version 4.3 */
 /* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
/**
 * ArgumentList expression generated by JJTree.
 * ASTArgumentList only needs to override the dump method, all other methods are inherited by the SimpleNode class.
 */
public class ASTArgumentList extends SimpleNode
{

	/**
	 * Constructor of the non-terminal expression 'ArgumentList'.
	 * @param id ID of the node.
	 */
    public ASTArgumentList(int id)
    {
        super(id);
    }
    /**
     * Constructor of the non-terminal expression 'ArgumentList'.
     * @param p Scanner object.
     * @param id ID of the node.
     */
    public ASTArgumentList(Scanner p, int id)
    {
        super(p, id);
    }

    /**
     * Displays information about this node. Correctly formats the information for more user-friendly reading on the console.
     * The method is overridden in the derived classes whenever special behaviour is necessary.
     * @param prefix The prefix (one or more spaces) to correctly format the information.
     */
    public void dump(String prefix)
    {
        System.out.println(toString(prefix));
        for (int i = 0; i < children.length; ++i)
        {
            SimpleNode n = (SimpleNode) children[i];
            if (n != null)
            {
                n.dump(prefix + " ");
            }
        }
    }

}
/*
 * JavaCC - OriginalChecksum=f2432c0d68dc057003b55adc967366ca (do not edit this
 * line)
 */