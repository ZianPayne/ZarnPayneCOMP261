
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.*;
import java.util.Map;
import javafx.util.Pair;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges = new HashMap<String, Edge>(); 

    // Augmentation path and the corresponding flow  E.G { {SG, GT, TB, BA}, 8 } 
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths = new ArrayList<>();

    
    //TODO:Build the residual graph that includes original and reverse edges 
    public static void computeResidualGraph(Graph graph){
        // TODO
        int ctr = 0;       // For adding ID's to the Graph                 
        
        for (Edge e : graph.getOriginalEdges()) {
            e.setFlow(0);                                                           // Resets flow

            City fromCity = e.fromCity();
            City toCity = e.toCity();
            
            edges.put(Integer.toString(ctr), e);                                    // Adds the forward edge
            fromCity.addEdgeId(Integer.toString(ctr));                              // Adds edges to city

            Edge backEdge = new Edge(toCity, fromCity, e.transpType(), 0, 0);
            edges.put(Integer.toString(ctr + 1), backEdge);                         // Adding the reverse edge
            toCity.addEdgeId(Integer.toString(ctr+1));
            ctr += 2;
            
            //fromCity.addEdgeId(Integer.toString(ctr));                              // Adds edges to city
            //toCity.addEdgeId(Integer.toString(ctr));
            
            fromCity.addFromLinks(toCity);
            toCity.addToLinks(fromCity);
        }
                
        printResidualGraphData(graph);  //may help in debugging
        // END TODO
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());

            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
            
        edges.forEach((eId, edge)-> System.out.println("["+eId+"] " + edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }

    /** find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        //TODO
        augmentationPaths = new ArrayList<>();   // Resets augmentation paths
        computeResidualGraph(graph);             // Creates the residual graph in the assignment, resets values

        while (true){
            Pair<ArrayList<String>, Integer> newPair = bfs(graph, from, to);                    // Finds the next pair with dfs

            if (newPair.getKey() == null) {
                break;
            }
            augmentationPaths.add(newPair);
        }
        
        
        // END TODO
        return augmentationPaths;
    }

    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow
    public static Pair<ArrayList<String>, Integer>  bfs(Graph graph, City s, City t) {

        ArrayList<String> augmentationPath = new ArrayList<String>();
        HashMap<String, String> backPointer = new HashMap<String, String>();        // Uses a toCityID ad  EdgeId as the backpointer.
        // TODO
        Queue<City> queue = new LinkedList<City>();
        queue.offer(s);
        
        while (!queue.isEmpty()){
            City current = queue.poll();
            
            for (String edgeId : current.getEdgeIds()){             // For each edge in a city
                Edge e = edges.get(String.valueOf(edgeId));         // Finds specific edge
                
                if (e.toCity() != s && backPointer.get(e.toCity().getId()) == null && e.capacity() - e.flow() != 0){
                    backPointer.put(e.toCity().getId(), edgeId);
                    
                    if (backPointer.get(t.getId()) != null) {       // If target city successfully found
                        Pair<ArrayList<String>, Integer> pair = returnPathBottleneck(backPointer, s, t); //returns a pair of the augmentation paths and flows
                        updateFlows(pair.getKey(), pair.getValue());

                        return pair;
                    }

                    queue.offer(e.toCity());                        // Adds neighbours to queue
                }
            }
        }
        
        
        // END TODO
        return new Pair(null,0);
    }
    
    /**
     * Takes the hashmap of backpointers and returns a path created with edges.
     */
    public static Pair<ArrayList<String>,Integer> returnPathBottleneck (HashMap<String, String> backPointer, City start, City target){
        ArrayList<String> pathEdge = new ArrayList<>();
        int bottleneck = (int)Double.POSITIVE_INFINITY;

        String curCity = target.getId();
        Edge curEdge = edges.get(backPointer.get(curCity));// Gets the first edge
        //pathEdge.add(backPointer.get(curCity));

        while(curCity != start.getId()){

            //if(curCity == start.getId()) break;

            pathEdge.add(backPointer.get(curCity));
            curEdge = edges.get(backPointer.get(curCity));
            curCity = curEdge.fromCity().getId();

            int flowLeft = curEdge.capacity() - curEdge.flow();
            bottleneck = Math.min(flowLeft, bottleneck);                // Finds if new bottleneck
        }

        Collections.reverse(pathEdge);
        
        return new Pair(pathEdge,bottleneck);
    }

    /** Updates the flows of paths in the augmentation path.
     *
     * @param pathEdges All paths in the augmentation path, receive updated flows.
     * @param addFlow   Value by which additional flow is added.
     */
    private static void updateFlows(ArrayList<String> pathEdges, int addFlow){

        for (String e : pathEdges){
            Edge e1 = edges.get(e);


            if (Integer.parseInt(e) % 2 == 0 ){                 // If an Edge is a forward one
                Edge e2 = edges.get(Integer.toString(Integer.parseInt(e) + 1));

                e1.setFlow(e1.flow() + addFlow);
                e2.setFlow(e2.flow() - addFlow);                // This is the back edge
            }
            else{                                               // If an edge is  a back one
                Edge e2 = edges.get(Integer.toString(Integer.parseInt(e) - 1));

                e1.setFlow(e1.flow() + addFlow);                // Back edge this time
                e2.setFlow(e2.flow() - addFlow);
            }

        }

    }

    /**Update Flows: Updates the flow after an augmentation path passes through.
     *
     * THIS CLASS IS NO LONGER USED, I HATED IT
     *
     * @param backPointers Stores the backPointers, iterates through all edges.
     * @param addFlow      Stores the additional flow, added to all edges flow field
     */
   private static void updateFlows(HashMap<String, String> backPointers, Integer addFlow, City start, City target){

        String curCityId = target.getId();

        while(curCityId != start.getId()) {
            String curEdgeId = backPointers.get(curCityId);
            Edge e = edges.get(curEdgeId);
            curCityId = e.fromCity().getId();

            if (Float.valueOf(curEdgeId) % 2 == 0) {                           // If an edge is a forward edge.
                Edge backE = edges.get(Integer.toString(Integer.parseInt(curEdgeId) + 1));    // Gets the backlink

                e.setFlow(e.flow() + addFlow);
                backE.setFlow(backE.flow() - addFlow);
            }
            else {                                                                              // If an edge is a backward edge.
                Edge frontE = edges.get(Integer.toString(Integer.parseInt(curEdgeId) - 1));  // Gets forward edge

                frontE.setFlow(frontE.flow() - addFlow);
                e.setFlow(e.flow() + addFlow);
            }

        }
   }


}


