/**
 * Implements the A* search algorithm to find the shortest path
 * in a graph between a start node and a goal node.
 * It returns a Path consisting of a list of Edges that will
 * connect the start node to the goal node.
 */

import java.util.Collections;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;


public class AStar {


    private static String timeOrDistance = "distance";    // way of calculating cost: "time" or "distance"



    /** find the shortest path between two stops
     * 
     *  CHALLENGE SECTION:
     *  
     *  Here the path will be determined by time rather than distance in the other version.
     *  
     *  This wil be done by adding an additional cost to transferring a line.
     *  This will discourage the path search from swapping bus lines.
     *  
     *  We will do this by modifying the distanceToNeighbour Variable to account for the line change.
     *  
     *  We will store the previous line in pathitem
     * 
    */
    public static List<Edge> findShortestPath(Stop start, Stop goal, String timeOrDistance) {
        if (start == null || goal == null) {return null;}
        AStar.timeOrDistance= (timeOrDistance.equals("time"))?"time":"distance";
        
        PriorityQueue<PathItem> fringe = new PriorityQueue<PathItem>();                                 // Priority Queue of PathItem, sorting them by distance.
        Map<Stop, Edge> backpointers = new HashMap<Stop, Edge>();
        Set<Stop> visited = new HashSet<>();
        
        fringe.offer(new PathItem(start, null, null, 0, heuristic(start, goal, 0)));                       // Starting node.
        
        while (!fringe.isEmpty()){  
            PathItem current = fringe.poll();
            Stop node = current.getStop();
            Edge edge = current.getEdge();
            
            
            if (!visited.contains(node)){
                visited.add(node);
                backpointers.put(node, edge);
                
                System.out.println(current.toString());
                
                if (node ==  goal) {                                                                    // If destination reached
                    System.out.println("DESTINATION REACHED");
                    return reconstructPath(start, goal, backpointers);
                }
                
                for (Edge outEdge : node.getForwardEdges()){                                            // Goes through all forward edges of node, adding ones not visited with new PathItem
                    Stop neighbour = outEdge.toStop();
                    
                    if (!visited.contains(neighbour)){                                                  // If node has never been visited
                        double swapCost = 0;
                        
                        /* This is needed as the test has line's as null
                        *  Here the swap cost is only 10 seconds, which means that the heuristic will remain admissable.
                        *
                        */
                        if (current.getLine() != null && outEdge.line() != null ) {
                            swapCost = current.getLine().equals(outEdge.line()) ? 0:10;                 // Sees if the neighbouring edge is the part of same line, if not adds swap cost of 10 seconds 
                        }
                        
                        double lengthToNeighbour = current.getCurrentCost() + swapCost + edgeCost(outEdge);
                        double estTotalPath = lengthToNeighbour + heuristic(neighbour, goal, Transport.getSpeedMPS(outEdge.transpType()));
                        fringe.offer(new PathItem(neighbour, outEdge, outEdge.line(), lengthToNeighbour, estTotalPath));
                    }
                }
            }
        }


        return null;   // fix this!!!
    }

    /**
     * Short method reconstructing the path of the fastest route.
     * Takes the backpointers and returns the list of edges required for the route.
     * 
     * Method starts from the goal, going to the start.
     */
    public static List<Edge> reconstructPath (Stop start, Stop goal, Map<Stop, Edge> backpointers){
        Stop currentStop = goal;
        Edge currentEdge = null;
        List<Edge> directions = new ArrayList<>();
        
        while (!currentStop.equals(start)){                 // Keeps iterating until path created going from goal to start.
            currentEdge = backpointers.get(currentStop);
            
            directions.add(currentEdge);
            currentStop = currentEdge.fromStop();
        }
        
        Collections.reverse(directions);                    // Reverses directions so it goes: start -> goal
        return directions;
    }




    /** Return the heuristic estimate of the cost to get from a stop to the goal
     * 
     * CHALLENGE: Modified the time method to improve consistency.
     * Here, it will incorporate the speed of a method of transport,
     * rather than rely on the same speed.
     * 
     */
    public static double heuristic(Stop current, Stop goal, double transpSpeed) {
        if (timeOrDistance=="distance"){ return current.distanceTo(goal);}
        else if (timeOrDistance=="time"){return current.distanceTo(goal) / transpSpeed;}
        else {return 0;}
    }

    /** Return the cost of traversing an edge in the graph */
    public static double edgeCost(Edge edge){
        if (timeOrDistance=="distance"){ return edge.distance();}
        else if (timeOrDistance=="time"){return edge.time();}
        else {return 1;}
    }




}

