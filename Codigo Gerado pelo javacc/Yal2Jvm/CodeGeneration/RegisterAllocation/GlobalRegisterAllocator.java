package Yal2Jvm.CodeGeneration.RegisterAllocation;

import Yal2Jvm.CodeGeneration.IR.ControlFlowGraph;
import Yal2Jvm.CodeGeneration.IR.Method;
import Yal2Jvm.CodeGeneration.IR.VariableTable;
import Yal2Jvm.Utils.globals;

import java.util.*;

/**
 * Class that implements the Global Register Allocation, i.e., performs register allocation for each method of the module that is on the CFG is received as parameter.
 */
public class GlobalRegisterAllocator
{
    /**
     * An object of the class ControlFlowGraph which contains information regarding the entire IR.
     */
    ControlFlowGraph cfg;
    /**
     * An int variable representing the number of available registers to be used.
     */
    int numberOfAvailableRegisters;

    /**
     * Constructor for this class receives the ControlFlowGraph of the program being compiled and the available number of registers to be used. This values will be used to set the attributes.
     * @param cfg An object of the class ControlFlowGraph which contains information regarding the entire IR.
     * @param numberOfAvailableRegisters An int variable representing the number of available registers to be used.
     */
    public GlobalRegisterAllocator(ControlFlowGraph cfg, int numberOfAvailableRegisters)
    {
        this.cfg = cfg;
        this.numberOfAvailableRegisters = numberOfAvailableRegisters;
    }

    /**
     * Method that performs the register allocation for all methods of the module represented by the CFG passed to the constructor. This method implements all the class' functionality.
     * It returns a boolean value indicating weather the register allocation was successful or not. It may fail if the register allocation could not be performed in the given number of registers.
     * @return A boolean value indicating weather the register allocation was successful or not.
     */
    public boolean performRegisterAllocation()
    {
        Iterator it = cfg.methodRegistry.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            Method method = (Method) pair.getValue();
            HashMap<String, HashSet<Integer>> livenessAnalysisResult = method.livenessAnalysis();
            MethodRegisterAllocator methodRegisterAllocator = new MethodRegisterAllocator(numberOfAvailableRegisters, livenessAnalysisResult, method);
            if (!methodRegisterAllocator.performRegisterAllocation())
                return false;

            VariableTable variableTable = methodRegisterAllocator.getResultingVarTable();
            method.setVarTable(variableTable);
        }

        return true;
    }
}
