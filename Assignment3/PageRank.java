import javafx.util.Pair;

import java.util.*;

/**
 * Write a description of class PageRank here.
 *
 * @author Z'Arn Payne
 * @version 1
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;

    //edges fields FOR CHALLENGE
    private static Map<String, Edge> edges = new HashMap<>();

    //private static ArrayList<Edge> edges = new ArrayList<>();


    /**
     * build the fromLinks and toLinks 
     */
    //TODO: Build the data structure to support Page rank. For each edge in the graph add the corresponding cities to the fromLinks and toLinks
    public static void computeLinks(Graph graph){
        // TODO


        int ctr = 0;
        for (Edge e : graph.getOriginalEdges()){
            //edges.add(e);

            e.toCity().addFromLinks(e.fromCity()); // Computes from Link for toCity
            e.fromCity().addToLinks(e.toCity());   // Computes toLink for fromCity
            e.fromCity().addEdgeId(String.valueOf(ctr));
            edges.put(String.valueOf(ctr), e);
            ctr++;

        }

        printPageRankGraphData(graph);  ////may help in debugging
        // END TODO
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (City city : graph.getCities().values()){
            System.out.print("\nCity: "+city.toString());
            //for each city display the in edges 
            System.out.print("\nIn links to cities:");
            for(City c:city.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to cities:");
            //for each city display the out edges 
            for(City c: city.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }
    //TODO: Compute rank of all nodes in the network and display them at the console
    public static void computePageRank(Graph graph){
        // TODO

        Map<String, Double> pageRanks = new HashMap<>();    // Stores the pageRank of a city by using its ID
        Map<String, City>   cities    = graph.getCities();
        ArrayList<String>   sinks     = new ArrayList<>();  // Stores all the sinks when graph is initialised

        Map<String, Double> rankThroughEdge = new HashMap<>(); // Stores the sum of pageRanks travelling through an edges

        int n = graph.getCities().size();                   // Find size of graph
        int ctr = 0;

        for (String entry : graph.getCities().keySet()){      // Initializes pageRank
            pageRanks.put(entry, 1.0/n);                      // Stores the default pageRank of nodes
            if (cities.get(entry).getToLinks().size() == 0){
                sinks.add(entry);                             // Adds sinks to the sink arraylist
            }

            for(String edge : cities.get(entry).getEdgeIds()){// Initialises edges for CHALLENGE
                rankThroughEdge.put(edge, 0.0);
            }
        }

        ctr++;

        while(ctr <= iter){
            Map<String, Double> newPageRank = new HashMap<>();              // Stores temporary new pageRanks, before update
            printPageRanks(ctr, pageRanks);                                 // Prints page Ranks

            for(Map.Entry<String, Double> pr : pageRanks.entrySet()){       // Goes through each pagerank, finding node
                double nRank = 0;

                for(City bn : cities.get(pr.getKey()).getFromLinks()){                                  // Gets all the toLinks from the city, computes contribution
                    double neighbourShare = pageRanks.get(bn.getId()) / bn.getToLinks().size();         // Computes the contribution of this backLink
                    nRank += neighbourShare;
                }

                nRank = (1 - dampingFactor)/n + (dampingFactor * (nRank));
                newPageRank.put(pr.getKey(), nRank);                        // Updates to new rank

                double edgeContribution = dampingFactor * (pr.getValue() / cities.get(pr.getKey()).getToLinks().size());     // Finds accumulative pageRank transfer, then adds to total sum
                Collection<String> outEdges = cities.get(pr.getKey()).getEdgeIds();
                handleEdgeContribution(outEdges ,rankThroughEdge, edgeContribution);
            }

            handleSinks(n, dampingFactor, sinks, pageRanks, newPageRank);                  // Handles all sinks in method

            for (Map.Entry<String, Double> npr : newPageRank.entrySet()){   // Updates actual pageRanks
                pageRanks.put(npr.getKey(), npr.getValue());
            }

            ctr++;
        }

        findPrintInfluentialEdges(cities, rankThroughEdge);         // Prints total rank contribution.

        // END TODO

    }

    /** Method to print page ranks of each node as well as final sum.
     *
     * @param ctr        Counter
     * @param pageRanks  PageRanks of all nodes
     */
    private static void printPageRanks(int ctr, Map<String, Double> pageRanks){
        double currentSum = 0;

        System.out.printf("Page Ranks for Graph at iter %d\n", ctr );
        for(Map.Entry<String, Double> pr : pageRanks.entrySet()){
            System.out.printf("%s: %f\n", pr.getKey(), pr.getValue());
            currentSum += pr.getValue();
        }
        System.out.printf("current sum: %f\n\n", currentSum);
    }

    /** Method to distribute the page Rank of sinks evenly through the graph.
     *
     * @param n      number of pages
     * @param sinks  all sinks in the graph
     * @param oldPR  old Page Rank
     * @param newPR  new Page Rank
     */
    private static void handleSinks(int n, double d, ArrayList<String> sinks, Map<String, Double> oldPR , Map<String, Double> newPR){

        for (String s : sinks){                  // Distribution of sinks pagerank to all other pages
            double addPr = d*(oldPR.get(s)/n);

            for(Map.Entry<String, Double> spr : newPR.entrySet()){
                newPR.put(spr.getKey(), spr.getValue() + addPr);
            }
        }

    }

    /** Associates edges in the graph with total contribution. Updates the fields storing page rank 'transferred'
     *  for each edge.
     *
     * @param toEdges          edges node goes out of
     * @param edgeContribution The existing contributions
     * @param eContribute      additional contribution of edge
     */
    private static void handleEdgeContribution(Collection<String> toEdges, Map<String, Double> edgeContribution, Double eContribute){

        for(String e : toEdges){
            edgeContribution.put(e, edgeContribution.get(e) + eContribute);  // Adds the additional contribution of this iteration
        }
    }

    /** Calculates and prints the edges with the most page Rank contributed.
     *
     * @param rankContribution How much rank went through each edge
     */
    private static void findPrintInfluentialEdges(Map<String, City> cities, Map<String, Double> rankContribution){

        Map<City, Double> maxContribution = new HashMap<>();
        Map<City, Edge>   maxContEdge =     new HashMap<>();

        for (Map.Entry<String, City> c : cities.entrySet()){                    // Initialises all cities max edges as well
            maxContribution.put(c.getValue(), Double.NEGATIVE_INFINITY);
            maxContEdge.put(c.getValue(), null);
        }

        for (Map.Entry<String, Double> rankCont : rankContribution.entrySet()){ // Checks if the rank going through edges is the greatest one
            Edge curEdge = edges.get(rankCont.getKey());
            City toCity = curEdge.toCity();

            if (maxContribution.get(toCity) < rankCont.getValue()) {
                maxContribution.put(toCity, rankCont.getValue());
                maxContEdge.put(toCity, curEdge);
            }
        }

        System.out.printf("Most influential edges for each city:\n");                                   // Prints out influential nodes
        for (Map.Entry<City, Double> max : maxContribution.entrySet()){
            if (max.getValue().equals(Double.NEGATIVE_INFINITY)) System.out.printf("Node %s: No In Edges :(\n", max.getKey().getName());

            else {
                System.out.printf("Node %s: %s\n", max.getKey().getName(), maxContEdge.get(max.getKey()).fromCity().getName());
                //System.out.printf("%s: %s, with contribution %f\n", max.getKey().getId(), maxContEdge.get(max.getKey()), max.getValue());
            }
        }


        /*
        for(Map.Entry<String, City> c : cities.entrySet()){

            Double maxContribution = Double.NEGATIVE_INFINITY;

            for (String e : c.getValue().getEdgeIds())      // Finds max value from all out
                maxContribution = (rankContribution.get(e) > maxContribution) ? rankContribution.get(e) : maxContribution;

        */




    }


}
