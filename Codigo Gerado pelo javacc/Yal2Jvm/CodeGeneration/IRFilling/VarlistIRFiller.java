package Yal2Jvm.CodeGeneration.IRFilling;

import Yal2Jvm.ASTArrayElement;
import Yal2Jvm.ASTScalarElement;
import Yal2Jvm.CodeGeneration.IR.IRContainer;
import Yal2Jvm.CodeGeneration.IR.Method;
import Yal2Jvm.ST;

import java.util.ArrayList;

/**
 * This class implements the necessary actions to fill the IR for a Varlist node.
 */
public class VarlistIRFiller
{
    /**
     * A class representing the Varlist node on the AST. This is used to access information about the node.
     */
    private final Yal2Jvm.ASTVarlist ASTVarlist;

    /**
     * Constructor for this class receives an ASTVarlist objects as parameter that will be used to set the attribute.
     * @param ASTVarlist A class representing the Varlist node on the AST. This is used to access information about the node.
     */
    public VarlistIRFiller(Yal2Jvm.ASTVarlist ASTVarlist)
    {
        this.ASTVarlist = ASTVarlist;
    }

    /**
     * Method that fills the IR for the second pass for a Varlist node.
     * On the first pass, only function definitions are filled in the intermediate representation. Function bodies will be filled in the second pass.
     * @param st An Object that represents the already filled symbol table.
     * @param irContainer A class representing the intermediate representation to be filled.
     */
    public void fillIRFirstPass(ST st, IRContainer irContainer)
    {
        for (int i = 0; i < ASTVarlist.getChildren().length; i++)
        {
            if (ASTVarlist.getChildren()[i] instanceof ASTArrayElement)
            {
                String arrayID = ((ASTArrayElement) ASTVarlist.getChildren()[i]).arrayID;
                ((Method) irContainer).addLocalVariable(arrayID);
            } else if (ASTVarlist.getChildren()[i] instanceof ASTScalarElement)
            {
                String scalarID = ((ASTScalarElement) ASTVarlist.getChildren()[i]).scalarID;
                ((Method) irContainer).addLocalVariable(scalarID);
            }
        }
    }

    /**
     * Function that allows to get the argument types in JVM convention for this ASTVarlist node.
     * @return an ArrayList that contains all the arguments as String in the JVM convention for this ASTVarlist node.
     */
    public static ArrayList<String> getArgumentsTypesInJVMConvention(Yal2Jvm.ASTVarlist ASTVarlist)
    {
        ArrayList<String> params = new ArrayList<String>();
        for (int i = 0; i < ASTVarlist.getChildren().length; i++)
        {
            if (ASTVarlist.getChildren()[i] instanceof ASTArrayElement)
            {
                params.add("[I");
            } else if (ASTVarlist.getChildren()[i] instanceof ASTScalarElement)
            {
                params.add("I");
            }
        }
        return params;
    }
}