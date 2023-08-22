import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;


/**
 * Graph is the data structure that stores the collection of stops, lines and connections. 
 * The Graph constructor is passed a Map of the stops, indexed by stopId and
 *  a Map of the Lines, indexed by lineId.
 * The Stops in the map have their id, name and GIS location.
 * The Lines in the map have their id, and lists of the stopIDs an times (in order)
 *
 * To build the actual graph structure, it is necessary to
 *  build the list of Edges out of each stop and the list of Edges into each stop
 * Each pair of adjacent stops in a Line is an edge.
 * We also need to create walking edges between every pair of stops in the whole
 *  network that are closer than walkingDistance.
 */
public class Graph {

    private Collection<Stop> stops;
    private Collection<Line> lines;
    private Collection<Edge> edges = new HashSet<Edge>();      // edges between Stops

    private int numComponents = 0;     // number of connected subgraphs (graph components)

    /**
     * Construct a new graph given a collection of stops and a collection of lines.
     */
    public Graph(Collection<Stop> stops, Collection<Line> lines) {
        this.stops = new TreeSet<Stop>(stops);
        this.lines = lines;

        // These are two of the key methods you must complete:
        createAndConnectEdges();
        computeNeighbours();

        // printGraphData();   // you could uncomment this to help in debugging your code
    }


    /** Print out the lines and stops in the graph to System.out */
    public void printGraphData(){
        System.out.println("============================\nLines:");
        for (Line line : lines){
            System.out.println(line.getId()+ "("+line.getStops().size()+" stops)");
        }
        System.out.println("\n=============================\nStops:");
        for (Stop stop : stops){
            System.out.println(stop+((stop.getSubGraphId()<0)?"":" subG:"+stop.getSubGraphId()));
            System.out.println("  "+stop.getForwardEdges().size()+" out edges; "+
                               stop.getBackwardEdges().size() +" in edges; " +
                               stop.getNeighbours().size() +" neighbours");
        }
        System.out.println("===============");
    }


    //============================================
    // Methods to build the graph structure. 
    //============================================

    /** 
     * From the loaded Line and Stop information,
     *  identify all the edges that connect stops along a Line.
     * - Construct the collection of all Edges in the graph  and
     * - Construct the forward and backward neighbour edges of each Stop.
     */
    private void createAndConnectEdges() {
        // TODO
        for (Line curLine : this.lines){
            List<Stop> lineStops = curLine.getStops();
            String transpType = curLine.getType();
            
            lineStops.get(0).addLine(curLine);            // Just adding the first stop to the line.
            for (int i = 1; i < lineStops.size(); i++){   // Starts from destination vertice setting the edges from there.
                Stop curStop = lineStops.get(i);                                                        // Current and Previous stop
                Stop prevStop = lineStops.get(i-1);
                double distance = curStop.distanceTo(prevStop);                                         // Gets the distance from current to prev stop
                
                double prevTime = curLine.getTimes().get(i-1);                                          // Gets time for the prev stop
                double curTime = curLine.getTimes().get(i);                                             // Gets time for the current stop
                double time = curTime - prevTime;                                                       // Computes time between
                
                Edge newEdge = new Edge(prevStop, curStop, transpType, curLine, time, distance);     // Takes the above info and adds to edge hashset and forward and back edges.
                edges.add(newEdge);
                
                prevStop.addForwardEdge(newEdge);
                curStop.addBackwardEdge(newEdge);
                curStop.addLine(curLine);
            }
        }


    }

    /** 
     * Construct the undirected graph of neighbours for each Stop:
     * For each Stop, construct a set of the stops that are its neighbours
     * from the forward and backward neighbour edges.
     * It may assume that there are no walking edges at this point.
     */
    public void computeNeighbours(){
        // TODO
        for (Line curLine : this.lines){    // Goes through each line, adding adjacent stops as neighbours into each stop's set.
            List<Stop> lineStops = curLine.getStops();
            
            for (int i = 1; i < lineStops.size(); i++){     // Starts at 1 to avoid nullPointer errors.
                Stop curStop = lineStops.get(i);            // Gets current and prev stops
                Stop prevStop = lineStops.get(i-1);
                
                prevStop.addNeighbour(curStop);             // Adds them to each others hashset.
                curStop.addNeighbour(prevStop);
            }
        }

    }

    //=============================================================================
    //    Recompute Walking edges and add to the graph
    //=============================================================================
    //
    /** 
     * Reconstruct all the current walking edges in the graph,
     * based on the specified walkingDistance:
     * identify all pairs of stops * that are at most walkingDistance apart,
     * and construct edges (both ways) between the stops
     * add the edges to the forward and backward neighbouars of the Stops
     * add the edges to the walking edges of the graph.
     * Assume that all the previous walking edges have been removed
     */
    public void recomputeWalkingEdges(double walkingDistance) {
        int count = 0;
        // TODO

        for (Stop stop : stops){
            
            for (Stop curStop : stops){                             // Goes through each stop to see if they are walking distance.
                double distApart = stop.distanceTo(curStop);
                
                if (stop.equals(curStop))continue;                  // Checks if they are the same stop
                
                if (distApart <= walkingDistance){                  // Finds out if they are within walking distance
                    double timeTravel = distApart / Transport.getSpeedMPS(Transport.WALKING);               // Computes time to complete.
                    
                    Edge edge1 = new Edge (stop, curStop, Transport.WALKING, null, timeTravel, distApart);  // Edges constructed both ways
                    Edge edge2 = new Edge (curStop, stop, Transport.WALKING, null, timeTravel, distApart);
                    
                    edges.add(edge1);               // Adds edge out to Edge set, edge in added by hother node
                    //edges.add(edge2);
                    
                    stop.addForwardEdge(edge1);     // Adds forward and bacwkard edges to each stop
                    stop.addBackwardEdge(edge2);
                    //stop.addNeighbour(curStop);
                    
                    count ++;
                    //stop.addForwardEdge(edge2);
                    //stop.addBackwardEdge(edge1);
                    //stop.addNeighbour(stop);
                }
                
            }
            
        }

        System.out.println("Number of walking edges added: " + count);
    }

    /** 
     * Remove all the current walking edges in the graph
     * - from the edges field (the collection of all the edges in the graph)
     * - from the forward and backward neighbours of each Stop.
     * - Resets the number of components back to 0 by
     *   calling  resetSubGraphIds()
     */
    public void removeWalkingEdges() {
        resetSubGraphIds();
        for (Stop stop : stops) {
            stop.deleteEdgesOfType(Transport.WALKING);// remove all edges of type walking
        }
        edges.removeIf((Edge e)->Transport.WALKING.equals(e.transpType()));
        
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return a collection of all the stops in the network
     */        
    public Collection<Stop> getStops() {
        return Collections.unmodifiableCollection(stops);
    }
    /**
     * Return a collection of all the edges in the network
     */        
    public Collection<Edge> getEdges() {
        return Collections.unmodifiableCollection(edges);
    }

    /**
     * Return the first stop that starts with the specified prefix
     * (first by alphabetic order of name)
     */
    public Stop getFirstMatchingStop(String prefix) {
        for (Stop stop : stops) {
            if (stop.getName().startsWith(prefix)) {
                return stop;
            }
        }
        return null;
    }

    /** 
     * Return all the stops that start with the specified prefix
     * in alphabetic order.
     */
    public List<Stop> getAllMatchingStops(String prefix) {
        List<Stop> ans = new ArrayList<Stop>();
        for (Stop stop : stops) {
            if (stop.getName().startsWith(prefix)) {
                ans.add(stop);
            }
        }
        return ans;
    }

    public int getSubGraphCount() {
        return numComponents;
    }
    public void setSubGraphCount(int num) {
        numComponents = num;
        if (num==0){ resetSubGraphIds(); }
    }

    /**
     * reset the subgraph ID of all stops
     */
    public void resetSubGraphIds() {
        for (Stop stop : stops) {
            stop.setSubGraphId(-1);
        }
        numComponents = 0;
    }
    
    /**
     * reset the depth of all stops
     */
    public void resetStopDepth() {
        for (Stop stop : stops) {
            stop.setStopDepth(-1);
        }
    }
    
    /** In the components Class, adds a root node if new component is found */
    public void addRootNode() {
        
    }




}
