package Yal2Jvm.CodeGeneration.IR;

import java.util.HashMap;

public class VariableTable
{

    public HashMap<String, Integer> varTable;

    //these two only matter for global vars i think
    public HashMap<String, Boolean> types;
    public HashMap<String, String> defaultValues;

    /**
     * create a varible table
     */
    public VariableTable()
    {
        this.varTable = new HashMap<>();
        this.types = new HashMap<>();
        this.defaultValues = new HashMap<>();
    }

    /**
     * add local variable
     * @param id identifier
     */
    public void addLocalVariable(String id)
    {
        this.varTable.put(id, this.varTable.size());
    }

    /**
     * add global variable
     * @param id identifier
     * @param isArray if array
     */
    public void addGlobalVariable(String id, Boolean isArray)
    {
        this.varTable.put(id, this.varTable.size());
        this.types.put(id, isArray);
    }

    /**
     * add a global variable
     * @param id the indetifier
     * @param isArray is array
     * @param defaultVal the size or initial value
     */
    public void addGlobalVariable(String id, Boolean isArray, String defaultVal)
    {
        this.varTable.put(id, this.varTable.size());
        this.types.put(id, isArray);
        this.defaultValues.put(id, defaultVal);
    }

    /**
     * create a varible table for hash map
     * @param table the hash map for the table
     */
    public VariableTable(HashMap<String, Integer> table)
    {
        this.varTable = table;
        this.types = new HashMap<>();
        this.defaultValues = new HashMap<>();
    }
}
