import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

//=============================================================================
//   TODO   Finding Components
//   Finds all the strongly connected subgraphs in the graph
//   Labels each stop with the number of the subgraph it is in and
//   sets the subGraphCount of the graph to the number of subgraphs.
//   Uses Kosaraju's_algorithm   (see lecture slides, based on
//   https://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)
//=============================================================================

public class Components{

    // Use Kosaraju's algorithm.
    // In the forward search, record which nodes are visited with a visited set.
    // In the backward search, use the setSubGraphId and getSubGraphID methods
    // on the stop to record the component (and whether the node has been visited
    // during the backward search).
    // Alternatively, during the backward pass, you could use a Map<Stop,Stop>
    // to record the "root" node of each component, following the original version
    // of Kosaraju's algorithm, but this is unnecessarily complex.

    
    public static void findComponents(Graph graph) {
        System.out.println("calling findComponents");
        graph.resetSubGraphIds();
        
        int componentNum = 0;
        List<Stop> stopList = new ArrayList<>();
        Set<Stop> visitedStops = new HashSet<>();
        
        for (Stop s : graph.getStops()){                     // Forward visits
            if (!visitedStops.contains(s)){
                forwardVisit(s, stopList, visitedStops);
            }
        }
        
        Collections.reverse(stopList);
        
        for (Stop s : stopList){                             // reverse Visits
            if (s.getSubGraphId() == -1){
                //graph.addRootNode(s);
                reverseVisit(s, componentNum);
                componentNum++;
            }
        }
        graph.setSubGraphCount(componentNum);
    }

    public static void forwardVisit(Stop stop, List<Stop> stopList, Set<Stop> visitedStops){
        if (!visitedStops.contains(stop)){                       // If stop not visited, check all neighbours and add stop in post-order
            visitedStops.add(stop);
            for (Stop neighbour : stop.getForwardNeighbours()){  // Goes through each outNeighbour
                forwardVisit(neighbour, stopList, visitedStops);
            }
            stopList.add(stop);
        }
    }
    
    public static void reverseVisit(Stop stop, int componentNum){
        if (stop.getSubGraphId() == -1){                          // If neighbour doesn't have a subGraphId, add it to root nodes id.
            stop.setSubGraphId(componentNum);
            for (Stop neighbour : stop.getBackwardNeighbours()){  // Goes through each backNeighbour
                reverseVisit(neighbour, componentNum);
            }
        }
    }
    
}
