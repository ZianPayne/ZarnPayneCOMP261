/**
 * AStar search (and Dijkstra search) uses a priority queue of partial paths
 * that the search is building.
 * Each partial path needs several pieces of information, to specify
 * the path to that point, its cost so far, and its estimated total cost
 */

public class PathItem implements Comparable<PathItem> {
    Stop nextPoint;
    Edge edge;
    Line lastLine;
    double currentCost;
    double estCost;
    
    
    // TODO
    /**
     * Stores path items for the A* algorithm's fringe.
     * 
     * ARGS:
     * Stop   nextPoint:    stores the next point in fringe.
     * Edge   edge:         stores the next edge that the item travels along..
     * Line   lastLine:     stores the line that it last travelled along
     * double currentCost:  stores the current cost.
     * double estCost:      stores the estimated total cost from the heuristic.
     */
    PathItem (Stop nxtPt, Edge e, Line l, double curCost, double estCost){
        nextPoint = nxtPt;
        edge = e;
        lastLine = l;
        currentCost = curCost;
        this.estCost = estCost;
    }
    
    public Stop getStop() {return nextPoint;}
    
    public Edge getEdge() {return edge;}
    
    /** returns last line*/
    public Line getLine() {return lastLine;}
    
    public double getCurrentCost() {return currentCost;}
    
    public Double returnTotalCost(){                                    // Returns total cost, needs to be a Class as it isued in compareTo
        return estCost;
    }
    
    public String toString(){
        return ("to: " + nextPoint.getName() + ", from: " + ((edge == null) ? " " : edge.fromStop().getName()) + ", current cost: " + currentCost + ", est cost: " + estCost);
    }
    
    public int compareTo(PathItem p){ /** Note this is an issue*/
        return this.returnTotalCost().compareTo(p.returnTotalCost());
    }

}
