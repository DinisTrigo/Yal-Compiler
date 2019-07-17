package Yal2Jvm.CodeGeneration.RegisterAllocation;

import java.util.Scanner;

/**
 * Class with the graph coloring algorithm.
 * This class is used for check if a graph can be colored with K number of colors.
 * The main function is used for testing purposes.
 */
public class GraphColoring
{
	/**
	 * The number of nodes in the graph.
	 */
    private int V;
    /**
     * The K value, or number of colors.
     */
    private int numOfColors;
    /**
     * The array with the colors for each node.
     */
    private int[] color;
    /**
     * The graph represented as a matrix where in each cell if the value is '1' then there's a connection between the indexes(or node IDs) accessing that cell. 
     */
    private int[][] graph;
    /**
     * Array of the restrictions of nodes.
     */
    private int[] restrictions;

    /**
     * Returns the array with the colors stored in the indexes that represent the nodes.
     * @return Returns the colors of all nodes.
     */
    public int[] getAssignedColors()
    {
        return color;
    }
    /**
     * Main function to test the coloring functions in this class.
     * @param args Arguments of main.
     */
    public static void main (String[] args)
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Graph Coloring Algorithm Test\n");
        /** Make an object of GraphColoring class **/
        GraphColoring gc = new GraphColoring();

        /** Accept number of vertices **/
        System.out.println("Enter number of verticesz\n");
        int V = scan.nextInt();

        /** get graph **/
        System.out.println("\nEnter matrix\n");
        int[][] graph = new int[V][V];
        for (int i = 0; i < V; i++)
            for (int j = 0; j < V; j++)
                graph[i][j] = scan.nextInt();

        System.out.println("\nEnter number of colors");
        int c = scan.nextInt();
        gc.graphColor(graph, c, new int[V]);
    }

    /**
     * Initializes global variables, sets up restrictions and calls the solve function.
     * @param g Graph matrix, each position indicates a directed connection between two nodes.
     * @param noc Number of colors or the K value as shown on the slides.
     * @param restrictions Restricts some nodes. 
     * @return Returns false if the graph can't be colored with the K value given in the noc argument.
     */
    public boolean graphColor(int[][] g, int noc, int[] restrictions)
    {
        V = g.length;
        numOfColors = noc;
        color = new int[V];
        graph = g;
        this.restrictions = restrictions;
        int currColor = 1;
        for (int i = 0; i < restrictions.length; i++)
        {
            if (restrictions[i] == 1)
            {
                color[i] = currColor;
                currColor++;
            }
        }

        try
        {
            solve(currColor - 1);
            return false;
        }
        catch (Exception e)
        {
            return true;
        }
    }

    /**
     * Tries finding a solution with recursion and backtracking.
     * If it can reach the last node in the graph, then all nodes were colored successfully without connecting two nodes with the same color. 
     * @param v The nodes number.
     * @throws Exception Throws an exception if it successfully iterated over all nodes.
     */
    public void solve(int v) throws Exception
    {
        if (v == V)
            throw new Exception("Solution found");

        /** try all colours **/
        for (int c = 1; c <= numOfColors; c++)
        {
            if (isPossible(v, c))
            {
                /** assign and proceed with next vertex **/
                color[v] = c;
                solve(v + 1);
                /** wrong assignement **/
                color[v] = 0;
            }
        }
    }

	/**
	 * Checks if the node can be colored with 'c' by verifying that the node isn't connected with another node with that 'c' color. 
	 * If the node is restricted then it returns false.
	 * @param v Index of the node which identifies the node.
	 * @param c The color which the function checks to see if node can be colored with it.
	 * @return Returns true if the node 'v' can be colored with the color 'c'. False otherwise.
	 */
    public boolean isPossible(int v, int c)
    {
        if (restrictions[v] == 1)
            return false;
        for (int i = 0; i < V; i++)
            if (graph[v][i] == 1 && c == color[i])
                return false;
        return true;
    }

}