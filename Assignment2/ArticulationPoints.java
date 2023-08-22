import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

//=============================================================================
//   TODO   Finding Articulation Points
//   Finds and returns a collection of all the articulation points in the undirected
//   graph, without walking edges
//=============================================================================

public class ArticulationPoints{

    // Use the algorithm from the lectures, but you will need a loop to check through
    // all the Stops in the graph to find any Stops which were not connected to the
    // previous Stops, and apply the lecture slide algorithm starting at each such stop.
    
    public static Collection<Stop> findArticulationPoints(Graph graph) {
        System.out.println("calling findArticulationPoints");
        graph.computeNeighbours();   // To ensure that all stops have a set of (undirected) neighbour stops
        
        Set<Stop> rootNodes = findRootNodes(graph);
        
        Collection<Stop> articulationPoints = new HashSet<Stop>();
        Map<Stop, Integer> stopDepths = new HashMap<Stop, Integer>();    // Hashmap storing all the stops and their respective depths.
        
        /*
        for (Stop s : graph.getStops()){                                // Creates a map with all stops, storing the unvisited value of -1
            stopDepths.put(s, -1);
        }
        
        for (Stop s : rootNodes){
            Stop start = s;                                                 // Start is root node of new subgraph
            stopDepths.put(start, 0);                                       // Sets depth to 0 of the new subgraph
            int numSubTrees = 0;
            
            for (Stop neighbour : start.getNeighbours()){                   // Goes through neighbours, identifying subtrees
                if (stopDepths.get(neighbour) == -1) {
                    recArtPts(neighbour, start, 1, articulationPoints, stopDepths);
                    numSubTrees++;
                }
            }
                        
            if (numSubTrees > 1) {                                          // Adds starting point if articulation point.
                articulationPoints.add(start);
            }     
        }
        */
        
        graph.resetStopDepth();
         
        for (Stop s : rootNodes){
            Stop start = s;                                                 // Start is root node of new subgraph
            s.setStopDepth(0);                                              // Sets depth to 0 of the new subgraph
            int numSubTrees = 0;
            int numNeighbours = s.getNeighbours().size();
            
            for (Stop neighbour : start.getNeighbours()){                   // Goes through neighbours, identifying subtrees
                if (neighbour.getStopDepth() == -1) {
                    recArtPts(neighbour, start, 1, articulationPoints);
                    numSubTrees++;
                }
            }
                        
            if (numSubTrees > 1) {                                          // Adds starting point if articulation point.
                articulationPoints.add(start);
            }     
        }
           
        System.out.println(articulationPoints);
        return articulationPoints;
        //return rootNodes;
    }

    /** Recursive Articulation Point finder
       * Identifies articulation points based on comparing itself with their neighbours or max depth of any child node it finds.
       *
    private static Integer recArtPts(Stop s, Stop fromStop, int depth, Collection<Stop> aPoints, Map<Stop, Integer> sDepths){
        sDepths.put(s, depth);                          // Sets depth of node
        int reachBack = depth;                          // Sets max reachback
        
        for (Stop neighbour : s.getNeighbours()){       // Goes through all neighbours, finding potential reachbacks and spreading search to other nodes.
            int nDepth = sDepths.get(neighbour);
            
            if (neighbour.equals(fromStop)) continue;
            
            else if (nDepth != -1){            // Checks visited nodes, finding reachbacks
                reachBack = Math.min(nDepth,depth);  // Finds lowest depth value.
            }
            
            else {                                                   // If not visited, goes through and finds reach of child. 
                int childReach = recArtPts(neighbour, s, depth + 1, aPoints, sDepths);
                
                if (childReach >= depth) {                      // Determines if the depth of child is greater or equal to the parent node of subtree
                    aPoints.add(s);
                }
                reachBack = Math.min(childReach,reachBack);
            }
        }
        
        return reachBack;
    }
    
    */
    
    /** Recursive Articulation Point finder
       * Identifies articulation points based on comparing itself with their neighbours or max depth of any child node it finds.
       */
    private static Integer recArtPts(Stop s, Stop fromStop, int depth, Collection<Stop> aPoints){
        s.setStopDepth(depth);                          // Sets depth of node
        int reachBack = depth;                          // Sets max reachback
        
        for (Stop neighbour : s.getNeighbours()){       // Goes through all neighbours, finding potential reachbacks and spreading search to other nodes.
            int nDepth = neighbour.getStopDepth();
            
            if (neighbour.equals(fromStop)) continue;
            
            else if (nDepth > -1){            // Checks visited nodes, finding reachbacks
                reachBack = Math.min(nDepth,reachBack);  // Finds lowest depth value.
            }
            
            else {                                                   // If not visited, goes through and finds reach of child. 
                int childReach = recArtPts(neighbour, s, depth + 1, aPoints);
                
                if (childReach >= depth) {                      // Determines if the depth of child is greater or equal to the parent node of subtree
                    aPoints.add(s);
                }
                reachBack = Math.min(childReach,reachBack);
            }
        }
        
        return reachBack;
    }
    
    /** Returns a set of all root nodes in the graph */
    private static Set<Stop> findRootNodes(Graph graph){
        Set<Stop> roots = new HashSet<>();
        graph.resetSubGraphIds();
        int group = 0;
        
        for (Stop s : graph.getStops()){            // Goes through all nodes, finding what group they belong to. Add new groups first node as a root
            if (s.getSubGraphId() == -1){
                findUndirectedNeighbours(s, group);
                group++;
                roots.add(s);    
            }
        }
        
        return roots;
    }
    
    /** Finds undirected neighbours of nodes, assigning them to a subgroup with a recursive call.*/ 
    private static void findUndirectedNeighbours(Stop s, int group){
        if (s.getSubGraphId() == -1){
            s.setSubGraphId(group);
            
            for (Stop n : s.getNeighbours()){           // Goes throguh all neighbours, adding them to subgraphs group.
                findUndirectedNeighbours(n, group);
            }
        }
    }
}
