package Yal2Jvm.CodeGeneration.RegisterAllocation;

import Yal2Jvm.CodeGeneration.IR.Method;
import Yal2Jvm.CodeGeneration.IR.VariableTable;

import java.util.*;

/**
 * Class that implements the Method Register Allocation, i.e., performs register allocation for one single method of the module which has the liveness analysis passed to the constructor.
 */
public class MethodRegisterAllocator
{
    /**
     * An int variable representing the number of available registers to be used.
     */
    int numOfAvailableRegisters;
    /**
     * The result of the liveness analysis. It maps the name of the variable (String) to a set of lines where the variable is used.
     */
    HashMap<String, HashSet<Integer>> linesWhereVariableIsUsed;
    /**
     * The variable table (object of class VariableTable, which is part of the IR structure) that resulted from register allocation. This is used for code generation.
     */
    VariableTable resultingVarTable;
    /**
     * The IR structure representing the Method for which the registers are being allocated.
     */
    Method methodIR;
    /**
     * A boolean value indicating weather the method to which the registers are being allocated is the main method or not.
     */
    boolean thisIsMainMethod;
    /**
     * A set that retains order containing the variable names (Strings) that are being allocated to registers.
     */
    LinkedHashSet<String> orderedVariableSet;

    /**
     * Getter for the variable table (object of class VariableTable, which is part of the IR structure) that resulted from register allocation. This is used for code generation.
     * @return The variable table (object of class VariableTable, which is part of the IR structure) that resulted from register allocation.
     */
    public VariableTable getResultingVarTable()
    {
        return resultingVarTable;
    }

    /**
     * Constructor of the class that received the number of available registers to be allocated to variables, a hash map containing the association between the names of the variables to be allocated to registers and a set containing the lines where they are used.
     * Also received the Method which is a IR structure that represents the method which is being register allocated.
     * @param numOfAvailableRegisters An int representing the number of registers available to be allocated.
     * @param linesWhereVariableIsUsed A hash map containing the association between the names of the variables to be allocated to registers and a set containing the lines where they are used.
     * @param methodIR An IR structure that represents the Method that is being register allocated. Is used to access some information about the method necessary to perform the register allocation.
     */
    public MethodRegisterAllocator(int numOfAvailableRegisters, HashMap<String, HashSet<Integer>> linesWhereVariableIsUsed, Method methodIR)
    {
        this.numOfAvailableRegisters = numOfAvailableRegisters;
        this.linesWhereVariableIsUsed = linesWhereVariableIsUsed;
        this.methodIR = methodIR;
        HashMap<String, Integer> varTable = methodIR.localVariables.varTable;
        Iterator it1 = varTable.entrySet().iterator();
        while (it1.hasNext())
        {
            Map.Entry pair1 = (Map.Entry)it1.next();
            String varName = (String) pair1.getKey();
            if (this.linesWhereVariableIsUsed.get(varName) == null)
            {
                this.linesWhereVariableIsUsed.put(varName, new HashSet<Integer>());
            }
        }
    }

    /**
     * Method that performs the register allocation for the method represented by liveness analysis passed to the constructor. This method implements all the class' functionality.
     * It returns a boolean value indicating weather the register allocation was successful or not. It may fail if the register allocation could not be performed in the given number of registers.
     * @return A boolean value indicating weather the register allocation was successful or not.
     */
    public boolean performRegisterAllocation()
    {
        int numberOfVars = linesWhereVariableIsUsed.size();
        int[][] graph = new int[numberOfVars][numberOfVars];
        int[] restrictions = new int[numberOfVars];

        fillGraph(graph, restrictions);
        GraphColoring graphColoring = new GraphColoring();
        boolean success = graphColoring.graphColor(graph, numOfAvailableRegisters, restrictions);
        boolean successAtFirstTry = success;
        int increasedNumberOfAvailableRegisters = numOfAvailableRegisters;
        while (!success)
        {
            increasedNumberOfAvailableRegisters++;
            success = graphColoring.graphColor(graph, increasedNumberOfAvailableRegisters, restrictions);
        }
        if (!successAtFirstTry)
        {
            System.out.println("Could not perform register allocation with the desired " + numOfAvailableRegisters + " registers.");
            System.out.println("Register allocation for this program would only be possible with " + increasedNumberOfAvailableRegisters + ".");
            return false;
        }
        else
        {
            fillVarTableWithGraphColoringResults(graphColoring);
            return true;
        }
    }

    /**
     * Auxiliary method for the performRegisterAllocation method of this class. It fills the variable table with the results of the graph coloring.
     * @param graphColoring The class that performs the graph coloring (used to retrieve the results from).
     */
    void fillVarTableWithGraphColoringResults(GraphColoring graphColoring)
    {
        int[] assignedColors = graphColoring.getAssignedColors();
        HashMap<String, Integer> varTable = new HashMap<>();
        int currVarIdx = 0;
        int offset;
        if (thisIsMainMethod)
            offset = 0;
        else
            offset = -1;
        for (String varID : orderedVariableSet)
        {
            varTable.put(varID, assignedColors[currVarIdx] + offset);
            currVarIdx++;
        }
        resultingVarTable = new VariableTable(varTable);
    }

    /**
     * Auxiliary method for the performRegisterAllocation method of this class. It creates a graph from the liveness analysis (received in the constructor) that will then be colored to perform the actual register allocation.
     * @param graph A bi-dimensional array representing the graph that will be filled in with the liveness analysis received in the constructor.
     * @param restrictions An array of int representing the restrictions. If restrictions[i] is set, it means that the register represented by the i(th) node in the graph cannot be colored, meaning that the variable corresponding to it cannot be register allocated.
     */
    private void fillGraph(int[][] graph, int[] restrictions)
    {
        orderedVariableSet = fillColoringRestrictions(restrictions);

        Iterator it1 = linesWhereVariableIsUsed.entrySet().iterator();
        while (it1.hasNext())
        {
            Map.Entry pair1 = (Map.Entry)it1.next();
            orderedVariableSet.add((String) pair1.getKey());
        }

        int currVar1 = 0;
        for (String variable1 : orderedVariableSet)
        {
            int currVar2 = 0;
            for(String variable2 : orderedVariableSet)
            {
                HashSet<Integer> lines1 = linesWhereVariableIsUsed.get(variable1);
                HashSet<Integer> lines2 = linesWhereVariableIsUsed.get(variable2);
                if (lines1 == null || lines2 == null)
                {
                    currVar2++;
                    continue;
                }
                if (lines1.size() == 0)
                {
                    graph[currVar1][currVar2] = 1;
                    graph[currVar2][currVar1] = 1;
                    currVar2++;
                    continue;
                }
                lines1 = new HashSet<>(lines1);
                lines2 = new HashSet<>(lines2);

                lines1.retainAll(lines2);
                if (lines1.size() != 0)
                {
                    graph[currVar1][currVar2] = 1;
                    graph[currVar2][currVar1] = 1;
                }
                currVar2++;
            }
            currVar1++;

        }
    }

    /**
     * Auxiliary method for the fillGraph method of this class. It creates an array representing the restrictions of colors to be assigned to the graph, representing the register allocation.
     * This is used to ensure that the parameters of the function are always in the same registers, and cannot be allocated (they are pre-allocated).
     * @param restrictions An array of int representing the restrictions. If restrictions[i] is set, it means that the register represented by the i(th) node in the graph cannot be colored, meaning that the variable corresponding to it cannot be register allocated.
     * @return A LinkedHashSet of String containing the names of the variables that correspond to function parameters, and therefore are pre-allocated to registers, and cannot be allocated with graph coloring.
     */
    private LinkedHashSet<String> fillColoringRestrictions(int[] restrictions)
    {
        ArrayList<String> args = methodIR.methodArgs;
        HashMap<String, Integer> varTable = methodIR.localVariables.varTable;
        LinkedHashSet<String> orderedVariableSet = new LinkedHashSet<>();
        int currRestrictionIdx = 0;
        int initIdx;
        if (methodIR.methodName.equals("main"))
        {
            initIdx = 1;
            thisIsMainMethod = true;
        }
        else
        {
            initIdx = 0;
            thisIsMainMethod = false;
        }
        for (Integer i = initIdx; i < args.size(); i++)
        {
            Iterator it1 = varTable.entrySet().iterator();
            while (it1.hasNext())
            {
                Map.Entry pair1 = (Map.Entry)it1.next();
                Integer register = (Integer) pair1.getValue();
                if (register.equals(i))
                {
                    restrictions[currRestrictionIdx] = 1;
                    currRestrictionIdx++;
                    orderedVariableSet.add((String) pair1.getKey());
                }
            }
        }

        Iterator it1 = linesWhereVariableIsUsed.entrySet().iterator();
        while (it1.hasNext())
        {
            Map.Entry pair1 = (Map.Entry)it1.next();
            String varName = (String) pair1.getKey();
            HashSet<Integer> lines = (HashSet<Integer>) pair1.getValue();
            if (varName == "")
                continue;
            if (methodIR.returnVar != null)
                if (varName.equals(methodIR.returnVar.value))
                    continue;
            if (lines.size() == 0)
            {
                restrictions[currRestrictionIdx] = 1;
                currRestrictionIdx++;
                orderedVariableSet.add((String) pair1.getKey());
            }
        }

        return orderedVariableSet;
    }
}
