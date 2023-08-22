/**
 * Interface for all boolean nodes that can be executed,
 */

interface BoolNode {

    public boolean evaluate(Robot robot);

    String toString();
}


/** RELOP node Classes */

class RelopNode implements BoolNode{
    BoolNode operator;

    public RelopNode(BoolNode op){
        operator = op;
    }

    public boolean evaluate(Robot robot){
        return false;
    }

    public String toString(){
        return operator.toString();
    }
}

class LTNode implements BoolNode{

    public boolean evaluate(Robot robot){
        return false;
    }

    public String toString() {return "lt";}
}

class GTNode implements BoolNode{

    public boolean evaluate(Robot robot){
        return false;
    }
    public String toString() {return "gt";}
}

class EQNode implements BoolNode{

    public boolean evaluate(Robot robot){
        return false;
    }
    public String toString() {return "eq";}
}

/** COND node Classes */

class CondNode implements BoolNode{
    String comp;
    IntNode expr1;
    IntNode expr2;
    BoolNode bool1;
    BoolNode bool2;
    public CondNode(String c, IntNode e1, IntNode e2, BoolNode b1, BoolNode b2){
        comp = c;
        expr1 = e1;
        expr2 = e2;
        bool1 = b1;
        bool2 = b2;
    }

    public boolean evaluate(Robot robot){

        if (comp.equals("eq")) {                                  // Goes through every operator to see if it is fine then works it out.
            if (expr1.evaluate(robot) == expr2.evaluate(robot)) return true;
        }
        else if (comp.equals("lt")) {
            if (expr1.evaluate(robot) < expr2.evaluate(robot)) return true;
        }
        else if (comp.equals("gt")) {
            if (expr1.evaluate(robot) > expr2.evaluate(robot)) return true;
        }
        else if (comp.equals("or")) {
            if (bool1.evaluate(robot) || bool2.evaluate(robot)) return true;
        }
        else if (comp.equals("and")) {
            if (bool1.evaluate(robot) && bool2.evaluate(robot)) return true;
        }
        else if (comp.equals("not")) {
            if (!bool1.evaluate(robot) ) return true;
        }
        return false;
    }

    public String toString(){

        if (comp.equals("gt")||comp.equals("lt")||comp.equals("eq")) return comp + "(" + expr1.toString() + "," + expr2.toString() + ")" ;
        else if (!comp.equals("not")) return comp + "(" + bool1.toString() + "," + bool2.toString() + ")" ;
        else return (comp + "(" + bool1.toString() + ")");
    }
}