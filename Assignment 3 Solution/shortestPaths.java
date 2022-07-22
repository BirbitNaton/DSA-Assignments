/*
https://codeforces.com/group/C71Rz4W66e/contest/325457/submission/114358044
Codeforces submission number 114358044

Tolmachev Anton BS20-02
 */

import java.util.*;

public class shortestPaths {
    /**
     * Reads input and responses correspondingly
     *
     * @param args
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        AdjacencyMatrixGraph myGraph = new AdjacencyMatrixGraph();

        Integer N = scanner.nextInt();
        Integer M = scanner.nextInt();
        myGraph.addVertex(0);
        for (int i = 1; i < N + 1; i++) {
            myGraph.addVertex(java.lang.Integer.MAX_VALUE);
        }
        for (int i = 0; i < M; i++) {
            myGraph.addEdge(myGraph.vertices.get(scanner.nextInt()), myGraph.vertices.get(scanner.nextInt()), new Pair(scanner.nextInt(), scanner.nextInt()));
        }

        Vertex source = myGraph.vertices.get(scanner.nextInt());
        Vertex target = myGraph.vertices.get(scanner.nextInt());
        Integer W = scanner.nextInt();

        myGraph.Dijkstra(source, target, W);
    }
}

/**
 * Generic interface for the graphs operated on
 * reflects the methods mentioned in SRS
 */
interface Graph_ADT {
    void addVertex(Integer value);

    void removeVertex(Vertex v);

    Edge addEdge(Vertex from, Vertex to, Pair weight);

    void removeEdge(Edge e);

    Collection<Edge> edgesFrom(Vertex v);

    Collection<Edge> edgesTo(Vertex v);

    Vertex findVertex(Integer value);

    Edge findEdge(Integer from_value, Integer to_value);

    boolean hasEdge(Vertex v, Vertex u);
}

class Pair {
    public Pair(Integer length, Integer bandwidth) {
        this.length = length;
        this.bandwidth = bandwidth;
    }

    Integer length;
    Integer bandwidth;
}

/**
 * class for Vertices, that contain indices and values of Integer
 */
class Vertex implements Comparator<Vertex> {
    public Vertex(Integer value) {
        this.value = value;
    }

    int idx;
    Integer value;
    Vertex parent;

    @Override
    public int compare(Vertex vertex, Vertex t1) {
        return Integer.compare(vertex.value, t1.value);
    }
}

/**
 * class for directed Edges from from vertex to to vertex, that contain weights of specified E and from and to vertices with values Integer
 */
class Edge {
    public Edge(Vertex v, Vertex u, Pair weight) {
        this.from = v;
        this.to = u;
        this.weight = weight;
    }

    Vertex from;
    Vertex to;
    Pair weight;
}

/**
 * the implementation of the Graph_ADT by the method of Adjacency Matrix
 * saves vertices to the ArrayList and edges to the two-dimentional ArrayList called adjM
 */
class AdjacencyMatrixGraph implements Graph_ADT {

    /**
     * applies Dijkstra's algorithm, saving previous node for each one along each path so it was possible to restore the shortest path to a particular node
     *
     * @param source
     * @param target
     * @param bandwidth
     */
    void Dijkstra(Vertex source, Vertex target, Integer bandwidth) {
        PriorityQueue<Vertex> SPT = new PriorityQueue<>(vertices.size(), source);

        source.value = 0;
        SPT.add(source);

        while (!SPT.isEmpty()) {
            Vertex v = SPT.poll();
            if (v.value < Integer.MAX_VALUE) {
                for (Edge e : edgesFrom(v)) {
                    if (e.to.value > v.value + e.weight.length && e.weight.bandwidth >= bandwidth) {
                        SPT.remove(e.to);
                        e.to.value = e.weight.length + v.value;
                        SPT.add(e.to);
                        e.to.parent = v;
                    }
                }
            }
        }
        if (target.value == Integer.MAX_VALUE) {
            System.out.println("IMPOSSIBLE");
            return;
        }
        ArrayList<Vertex> path = new ArrayList<>();
        Integer minW = Integer.MAX_VALUE;
        Vertex currentNode = target;
        path.add(currentNode);
        while (currentNode != source) {

            if (minW > adjM.get(currentNode.parent.idx).get(currentNode.idx).weight.bandwidth)
                minW = adjM.get(currentNode.parent.idx).get(currentNode.idx).weight.bandwidth;
            path.add(currentNode.parent);
            currentNode = currentNode.parent;
        }

        Collections.reverse(path);
        System.out.println(String.valueOf(path.size()) + " " + target.value + " " + minW);
        String output = String.valueOf(path.get(0).idx);
        for (int i = 1; i < path.size(); i++) {
            output += " " + path.get(i).idx;
        }
        System.out.println(output);
    }

    ArrayList<ArrayList<Edge>> adjM = new ArrayList<>();
    ArrayList<Vertex> vertices = new ArrayList<>();

    /**
     * adds 1 to both dimentions of adjM and fills all the new cells with nulls
     * adds new vertex to vertices ArrayList
     *
     * @param value
     */
    @Override
    public void addVertex(Integer value) {
        Vertex vertex = new Vertex(value);
        vertex.idx = adjM.size();

        adjM.add(new ArrayList<>());

        for (int i = 0; i < adjM.size() - 1; i++) {
            adjM.get(adjM.size() - 1).add(null);
        }
        for (ArrayList<Edge> edges : adjM) {
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
    public void removeVertex(Vertex v) {
        adjM.remove(v.idx);
        for (ArrayList<Edge> edges : adjM) {
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
    public Edge addEdge(Vertex from, Vertex to, Pair weight) {
        Edge edge = new Edge(from, to, weight);
        adjM.get(edge.from.idx).set(edge.to.idx, edge);
        return edge;
    }

    /**
     * removes (sets to null) the given edge
     *
     * @param e
     */
    @Override
    public void removeEdge(Edge e) {
        adjM.get(e.from.idx).set(e.to.idx, null);
    }

    /**
     * goes all the way along the array with the index of from vertex and adds all non-null edges to the output collection
     *
     * @param v
     * @return all the edges leading out of the given vertex
     */
    @Override
    public Collection<Edge> edgesFrom(Vertex v) {
        ArrayList<Edge> edges = new ArrayList<>();
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
    public Collection<Edge> edgesTo(Vertex v) { //
        ArrayList<Edge> edges = new ArrayList<>();
        for (ArrayList<Edge> edgeArrayList : adjM) {
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
    public Vertex findVertex(Integer value) {
        for (Vertex vertex : vertices) {
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
    public Edge findEdge(Integer from_value, Integer to_value) {
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
    public boolean hasEdge(Vertex v, Vertex u) {
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
    private Boolean visit(ArrayList<Vertex> stack, Vertex v) {
        if (stack.contains(v)) {
            while (stack.get(0) != v)
                stack.remove(0);
            return true;
        }

        stack.add(v);
        for (Edge e : edgesFrom(v))
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
    private ArrayList<Vertex> ifAcyclicFalse() {
        ArrayList<Vertex> output = new ArrayList<>();
        for (Vertex v : vertices) {
            ArrayList<Vertex> stack = new ArrayList<>();
            stack.add(v); //add v into stack

            ArrayList<Vertex> verticesFrom = new ArrayList<>();
            for (Edge e : edgesFrom(v))
                verticesFrom.add(e.to);

            for (Vertex vertexFrom : verticesFrom)
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
        Edge tempE;
        for (int i = 0; i < adjM.size(); i++) {
            for (int j = i + 1; j < adjM.size(); j++) {
                tempE = adjM.get(i).get(j);
                adjM.get(i).set(j, adjM.get(j).get(i));
                adjM.get(j).set(i, tempE);

                if (adjM.get(i).get(j) != null) {
                    Vertex tempV = adjM.get(i).get(j).to;
                    adjM.get(i).get(j).to = adjM.get(i).get(j).from;
                    adjM.get(i).get(j).from = tempV;
                }
                if (adjM.get(j).get(i) != null) {
                    Vertex tempV = adjM.get(j).get(i).to;
                    adjM.get(j).get(i).to = adjM.get(j).get(i).from;
                    adjM.get(j).get(i).from = tempV;
                }
            }
        }
    }
}