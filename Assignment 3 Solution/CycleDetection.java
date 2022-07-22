/*
https://codeforces.com/group/C71Rz4W66e/contest/325457/submission/114137490
Codeforces submission number 114137490

Tolmachev Anton BS20-02
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class buildingGraphs {
    /**
     * Reads input and responses correspondingly
     *
     * @param args
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        AdjacencyMatrixGraph<Integer, String> myGraph = new AdjacencyMatrixGraph<>();

        while (scanner.hasNextLine()) {
            String[] input = scanner.nextLine().split(" ");

            switch (input[0]) {
                case "ADD_VERTEX":
                    myGraph.addVertex(input[1]);
                    break;
                case "REMOVE_VERTEX":
                    myGraph.removeVertex(myGraph.findVertex(input[1]));
                    break;
                case "ADD_EDGE":
                    myGraph.addEdge(myGraph.findVertex(input[1]), myGraph.findVertex(input[2]), Integer.valueOf(input[3]));
                    break;
                case "REMOVE_EDGE":
                    myGraph.removeEdge(myGraph.findEdge(input[1], input[2]));
                    break;
                case "HAS_EDGE":
                    if (myGraph.hasEdge(myGraph.findVertex(input[1]), myGraph.findVertex(input[2])))
                        System.out.println("TRUE");
                    else
                        System.out.println("FALSE");
                    break;
                case "IS_ACYCLIC":  //determines whether myGraph is acyclic
                    if (myGraph.isAcyclic()) {      //if isAcyclic returns True, than myGraph is Acyclic
                        System.out.println("ACYCLIC");
                        break;
                    }
                    //if isAcyclic returns False, ifAcyclicFalse has non-empty output of an ArrayList of Vertices which is a cycle
                    //This piece handles the output of ifAcyclicFalse and computes and prints first cycle the method spots
                    int weight = 0;
                    ArrayList<Vertex<String>> outV = myGraph.ifAcyclicFalse();
                    Vertex<String> root = outV.get(outV.size() - 1);
                    int idx = outV.size() - 1;
                    for (int i = 0; i < outV.size() - 1; i++)
                        weight += myGraph.adjM.get(outV.get(i).idx).get(outV.get(i + 1).idx).weight;
                    weight += myGraph.adjM.get(outV.get(outV.size() - 1).idx).get(outV.get(0).idx).weight;
                    StringBuilder output = new StringBuilder(String.valueOf(weight));
                    for (Vertex<String> vertex : outV)
                        output.append(" ").append(vertex.value);
                    System.out.println(output);
                    break;
                case "TRANSPOSE":
                    myGraph.transpose();
                    break;
            }
        }
    }
}

/**
 * Generic interface for the graphs operated on
 * reflects the methods mentioned in SRS
 *
 * @param <E>
 * @param <V>
 */
interface Graph_ADT<E, V> {
    void addVertex(V value);

    void removeVertex(Vertex<V> v);

    Edge<E, V> addEdge(Vertex<V> from, Vertex<V> to, E weight);

    void removeEdge(Edge<E, V> e);

    Collection<Edge<E, V>> edgesFrom(Vertex<V> v);

    Collection<Edge<E, V>> edgesTo(Vertex<V> v);

    Vertex<V> findVertex(V value);

    Edge<E, V> findEdge(V from_value, V to_value);

    boolean hasEdge(Vertex<V> v, Vertex<V> u);
}

/**
 * class for Vertices, that contain indices and values of specified V
 *
 * @param <V>
 */
class Vertex<V> {
    public Vertex(V value) {
        this.value = value;
    }

    int idx;
    V value;
}

/**
 * class for directed Edges from from vertex to to vertex, that contain weights of specified E and from and to vertices with values of specified V
 *
 * @param <E>
 * @param <V>
 */
class Edge<E, V> {
    public Edge(Vertex<V> v, Vertex<V> u, E weight) {
        this.from = v;
        this.to = u;
        this.weight = weight;
    }

    Vertex<V> from;
    Vertex<V> to;
    E weight;
}

/**
 * the implementation of the Graph_ADT by the method of Adjacency Matrix
 * saves vertices to the ArrayList and edges to the two-dimentional ArrayList called adjM
 *
 * @param <E>
 * @param <V>
 */
class AdjacencyMatrixGraph<E, V> implements Graph_ADT<E, V> {


    ArrayList<ArrayList<Edge<E, V>>> adjM = new ArrayList<>();
    ArrayList<Vertex<V>> vertices = new ArrayList<>();

    /**
     * adds 1 to both dimentions of adjM and fillesall the new cells with nulls
     * adds new vertex to vertices ArrayList
     *
     * @param value
     */
    @Override
    public void addVertex(V value) {
        Vertex<V> vertex = new Vertex<>(value);
        vertex.idx = adjM.size();

        adjM.add(new ArrayList<>());

        for (int i = 0; i < adjM.size() - 1; i++) {
            adjM.get(adjM.size() - 1).add(null);
        }
        for (ArrayList<Edge<E, V>> edges : adjM) {
            edges.add(null);
        }
        vertices.add(vertex);
    }

    /**
     * removes all the vertices leading to and out of the given edge from the matrix adjM
     * recomputes indices for each vertex, so the were consequent and smooth
     *
     * @param v
     */
    @Override
    public void removeVertex(Vertex<V> v) {
        adjM.remove(v.idx);
        for (ArrayList<Edge<E, V>> edges : adjM) {
            edges.remove(v.idx);
        }
        vertices.remove(v);
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).idx != i) {
                vertices.get(i).idx = i;
            }
        }
    }

    /**
     * adds an edge to the matrix at the cell set by indices of from and to vertices
     *
     * @param from
     * @param to
     * @param weight
     * @return the added edge
     */
    @Override
    public Edge<E, V> addEdge(Vertex<V> from, Vertex<V> to, E weight) {
        Edge<E, V> edge = new Edge<>(from, to, weight);
        adjM.get(edge.from.idx).set(edge.to.idx, edge);
        return edge;
    }

    /**
     * removes (sets to null) the given edge
     *
     * @param e
     */
    @Override
    public void removeEdge(Edge<E, V> e) {
        adjM.get(e.from.idx).set(e.to.idx, null);
    }

    /**
     * goes all the way along the array with the index of from vertex and adds all non-null edges to the output collection
     *
     * @param v
     * @return all the edges leading out of the given vertex
     */
    @Override
    public Collection<Edge<E, V>> edgesFrom(Vertex<V> v) {
        ArrayList<Edge<E, V>> edges = new ArrayList<>();
        for (int i = 0; i < adjM.size(); i++) {
            if (adjM.get(v.idx).get(i) != null)
                edges.add(adjM.get(v.idx).get(i));
        }
        return edges;
    }

    /**
     * goes through all the arrays in matrix and picks edges with the index of to vertex and adds all of non-null of them to the output collection
     *
     * @param v
     * @return all the edges leading to the given vertex
     */
    @Override
    public Collection<Edge<E, V>> edgesTo(Vertex<V> v) { //
        ArrayList<Edge<E, V>> edges = new ArrayList<>();
        for (ArrayList<Edge<E, V>> edgeArrayList : adjM) {
            if (edgeArrayList.get(v.idx) != null)
                edges.add(edgeArrayList.get(v.idx));
        }
        return edges;
    }

    /**
     * seeks for a vertex with given value, if ther're none, returns null
     *
     * @param value
     * @return vertex with given value first met during traverse of the vertices array/null
     */
    @Override
    public Vertex<V> findVertex(V value) {
        for (Vertex<V> vertex : vertices) {
            if (vertex.value.equals(value))
                return vertex;
        }
        return null;
    }

    /**
     * traverses the matrrix adjM and seeks for an edge with given from and to vertices, if there're none, returns null
     *
     * @param from_value
     * @param to_value
     * @return the edge with given from and to vertices/null
     */
    @Override
    public Edge<E, V> findEdge(V from_value, V to_value) {
        for (int i = 0; i < adjM.size(); i++) {
            for (int j = 0; j < adjM.size(); j++) {
                if (vertices.get(i).value.equals(from_value) && vertices.get(j).value.equals(to_value) && adjM.get(i).get(j) != null)
                    return adjM.get(i).get(j);
            }
        }
        return null;
    }

    /**
     * check whether there's an edge between given vertices
     *
     * @param v
     * @param u
     * @return True/False
     */
    @Override
    public boolean hasEdge(Vertex<V> v, Vertex<V> u) {
        return adjM.get(v.idx).get(u.idx) != null;
    }

    /**
     * says whether there's a cycle within some branch
     * is not supposed to be used betond ifAcyclicFalse method, thus private
     *
     * @param stack
     * @param v
     * @return True/False
     */
    private Boolean visit(ArrayList<Vertex<V>> stack, Vertex<V> v) {
        if (stack.contains(v)) {
            while (stack.get(0) != v)
                stack.remove(0);
            return true;
        }

        stack.add(v);
        for (Edge<E, V> e : edgesFrom(v))
            if (visit(stack, e.to))
                return true;
            else
                stack.remove(e.to);
        stack.remove(v);
        return false;

    }

    /**
     * method called to construct a cycle if there's one, else returns an emty array
     * is not supposed to be used betond isAcyclic method, thus private
     *
     * @return a very trail of the cycle
     */
    private ArrayList<Vertex<V>> ifAcyclicFalse() {
        ArrayList<Vertex<V>> output = new ArrayList<>();
        for (Vertex<V> v : vertices) {
            ArrayList<Vertex<V>> stack = new ArrayList<>();
            stack.add(v); //add v into stack

            ArrayList<Vertex<V>> verticesFrom = new ArrayList<>();
            for (Edge<E, V> e : edgesFrom(v))
                verticesFrom.add(e.to);

            for (Vertex<V> vertexFrom : verticesFrom)
                if (visit(stack, vertexFrom)) {
                    for (int i = 0; i < stack.size(); i++) {
                        output.add(stack.get(i));
                    }
                    return output;
                }
        }
        return output;
    }

    /**
     * determines whether there's a cycle in the graph by trial to construct it using ifAcyclicFalse
     *
     * @return True/False
     */
    boolean isAcyclic() {
        return ifAcyclicFalse().size() == 0;
    }

    /**
     * transoses adjM by simple rotation (exchanging axes) and changes from and to vertices for every ege in the graph
     */
    void transpose() {
        Edge<E, V> tempE;
        for (int i = 0; i < adjM.size(); i++) {
            for (int j = i + 1; j < adjM.size(); j++) {
                tempE = adjM.get(i).get(j);
                adjM.get(i).set(j, adjM.get(j).get(i));
                adjM.get(j).set(i, tempE);

                if (adjM.get(i).get(j) != null) {
                    Vertex<V> tempV = adjM.get(i).get(j).to;
                    adjM.get(i).get(j).to = adjM.get(i).get(j).from;
                    adjM.get(i).get(j).from = tempV;
                }
                if (adjM.get(j).get(i) != null) {
                    Vertex<V> tempV = adjM.get(j).get(i).to;
                    adjM.get(j).get(i).to = adjM.get(j).get(i).from;
                    adjM.get(j).get(i).from = tempV;
                }
            }
        }
    }
}