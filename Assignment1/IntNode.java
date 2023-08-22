import java.util.List;
import java.util.function.IntToDoubleFunction;

/**
 * Interface for all boolean nodes that can be executed,
 */

interface IntNode {

    public Integer evaluate(Robot robot);

    String toString();
}

/** SENS node Class */

class SensNode implements IntNode{
    IntNode node;

    public SensNode(IntNode n){
        node = n;
    }

    public Integer evaluate(Robot robot) {
        return node.evaluate(robot);
    }

    public String toString(){
        return node.toString();
    }
}

class FuelLeftNode implements IntNode{
    public Integer evaluate(Robot robot){
        return robot.getFuel();
    }

    public String toString(){
        return "fuelLeft";
    }

}

class OppLRNode implements IntNode{

    public OppLRNode(){}
    public Integer evaluate(Robot robot){
        return robot.getOpponentLR();
    }

    public String toString(){
        return "oppLR";
    }
}

class OppFBNode implements IntNode{

    public OppFBNode(){}
    public Integer evaluate(Robot robot){
        return robot.getOpponentFB();
    }

    public String toString(){
        return "oppFB";
    }
}

class NumBarrelsNode implements IntNode{

    public NumBarrelsNode(){}
    public Integer evaluate(Robot robot){
        return robot.numBarrels();
    }

    public String toString(){
        return "numBarrels";
    }
}

class BarrelLRNode implements IntNode{
    IntNode expr;                   // Expr stores any additional args

    public BarrelLRNode(IntNode e){
        expr = e;
    }
    public Integer evaluate(Robot robot){
        return robot.getBarrelLR(expr.evaluate(robot));
    }   // Uses new getBarrel with expr

    public String toString(){
        return "barrelLR";
    }
}

class BarrelFBNode implements IntNode{

    IntNode expr;

    public BarrelFBNode(IntNode e){
        expr = e;
    }
    public Integer evaluate(Robot robot){

        return robot.getBarrelFB(expr.evaluate(robot));
    }

    public String toString(){
        return "barrelFB";
    }
}

/** Returns int for walldist*/
class WallDistNode implements IntNode{

    public WallDistNode(){}
    public Integer evaluate(Robot robot){
        return robot.getDistanceToWall();
    }

    public String toString(){
        return "wallDist";
    }
}

/** EXPR/Calculation node Classes */

/** Overarching EXPR node implementation
 *
 * Please note that SENS and Num Nodes will use their own class deifinitions.
 *
 * */
class ExprNode implements IntNode{
    IntNode operator;
    IntNode expr1; IntNode expr2;

    public ExprNode(IntNode op, IntNode e1, IntNode e2){
        operator = op;
        expr1 = e1;
        expr2 = e2;
    }

    public Integer evaluate(Robot robot) {  // Breaks down fields to find which node it is, does relevant operator from there.
        if (operator != null) {                 // Operator Function
            switch (operator.toString()) {
                case ("add"): return expr1.evaluate(robot) + expr2.evaluate(robot);
                case ("sub"): return expr1.evaluate(robot) - expr2.evaluate(robot);
                case ("mul"): return expr1.evaluate(robot) * expr2.evaluate(robot);
                case ("div"): return expr1.evaluate(robot) / expr2.evaluate(robot);
            }
        }
        return expr1.evaluate(robot);           // Single value, nothing special NUM node
    }

    public String toString(){
        if (operator != null) {                 // Operator Function
            return (operator.toString() + " (" + expr1.toString() + ", " + expr2.toString() + ")");
        }
        return expr1.toString();
    }
}

/** Node storing operator*/
class OpNode implements IntNode{
    String operator;

    public OpNode(String op){
        operator = op;
    }
    public Integer evaluate(Robot robot) {
        return null;
    }

    public String toString(){
        return operator.toString();
    }
}

/** NUM node Class */

class NumNode implements IntNode{
    Integer num;

    public NumNode(Integer n){
        num = n;
    }

    public Integer evaluate(Robot robot){
        return num;
    }

    public String toString(){
        return num.toString();
    }
}

