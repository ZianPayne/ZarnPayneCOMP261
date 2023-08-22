import java.util.ArrayList;
import java.util.List;
import java.util.List;


/**
 * Interface for all nodes that can be executed,
 * including the top level program node
 */

interface ProgramNode {

    public void execute(Robot robot);

    String toString();
}


/** ProgramNode Classes */

class ProgNode implements ProgramNode{

    List<ProgramNode> nodes;

    public ProgNode (List<ProgramNode> n){
        nodes = n;
    }

    public void addNode(ProgramNode p){
        nodes.add(p);
    }
    public void execute(Robot robot){
        for (ProgramNode p : nodes){
            p.execute(robot);
        }
    }

    public String toString(){
        StringBuilder ans = new StringBuilder("");
        for (ProgramNode p : nodes){
            ans.append(p.toString());
            ans.append(" ");
        }
        return ans.toString();
    }
}

class StmtNode implements ProgramNode{  // Statement Node class
    ProgramNode node;

    public StmtNode (ProgramNode p){
        node = p;
    }
    public void execute(Robot robot) {
        node.execute(robot);
    }

    public String toString(){
        return (node.toString());
    }
}


class ActNode implements ProgramNode{   // Act Node Class
    ProgramNode node;
    public ActNode (ProgramNode p){
        node = p;
    }
    public void execute(Robot robot ){
        node.execute(robot);
    }
    public String toString(){
        return(node.toString());
    }
}


/** Act Node Classes */

class MoveNode implements ProgramNode{   // Move Node Class
    IntNode expr;

    public MoveNode(IntNode e) {
        expr = e;
    }
    public void execute(Robot robot) {
        for (int i = 0; i < expr.evaluate(robot); i++) robot.move(); // Completes in number of times rep occurs
    }

    public String toString(){ return ("move(" + expr.toString() + ")");}

}

class TurnLNode implements ProgramNode{   // Turn Left Node Class
    public TurnLNode() {}
    public void execute(Robot robot){ robot.turnLeft();}

    public String toString() { return "turnLeft";}
}

class TurnRNode implements ProgramNode{   // Turn Right Node Class
    public TurnRNode() {}
    public void execute(Robot robot ){ robot.turnRight();}

    public String toString() { return "turnRight";}
}

class TurnAroundNode implements ProgramNode{   // Turn Around Node Class
    public TurnAroundNode() {}
    public void execute(Robot robot ){ robot.turnAround();}

    public String toString() { return "turnAround";}
}

class ShieldOnNode implements ProgramNode{   // Shield On Node Class
    public ShieldOnNode() {}
    public void execute(Robot robot ){ robot.setShield(true);}

    public String toString() { return "shieldOn";}
}

class ShieldOffNode implements ProgramNode{   // Shield Off Node Class
    public ShieldOffNode() {}
    public void execute(Robot robot ){ robot.setShield(false);}

    public String toString() { return "shieldOff";}
}

class WaitNode implements ProgramNode{   // Wait Node Class default one without EXPR Node
    IntNode expr;
    public WaitNode(IntNode e) {
        expr = e;
    }
    public void execute(Robot robot ) {
        for (int i = 0; i < expr.evaluate(robot); i++) robot.idleWait(); // Completes in number of times rep occurs
    }

    public String toString() { return "wait (" + expr.toString() + ")";}
}

class TakeFuelNode implements ProgramNode{   // Take Fuel Node Class
    public TakeFuelNode() {}
    public void execute(Robot robot ){ robot.takeFuel();}

    public String toString() { return "takeFuel";}
}

/** LOOP Node Classes */
class LoopNode implements ProgramNode{  // Loop Node Class
    BlockNode block;

    public LoopNode(BlockNode ops){
        block = ops;
    }

    public void execute(Robot robot){
        while (true) {
            block.execute(robot);            // indefinitely executes the block
        }
    }

    public String toString(){

        return "loop" + this.block.toString();
    }
}

/** IF, WHIlE node Classes */

class IfNode implements ProgramNode{

    List<BlockNode> blocks; // An arraylist of blocknodes storing them
    List<BoolNode> cond;        // An arrayList of BoolNodes, storing them
    boolean executed = false;
    boolean isElse = false;

    public IfNode(List<BlockNode> b, List<BoolNode> c, boolean is){  // Here, the list of conditions and block nodes are sent to be initialised
        blocks = b;
        cond = c;
        isElse = is;
    }

    public void execute(Robot robot){
        for (int i = 0; i < cond.size(); i++){                   // Goes through each condition, if one is true, executes block and breaks.
            if(cond.get(i).evaluate(robot)) {
                blocks.get(i).execute(robot);
                executed = true;
                break;
            }
        }
        if (!executed && isElse) blocks.get(blocks.size()-1).execute(robot); // If no conditions were met, execute last (else) block.
        executed = false;   // This came up as an issue with nested else cases. I don't know why

    }

    public String toString(){

        StringBuilder ans = new StringBuilder("if (" + cond.get(0).toString() + ")" + blocks.get(0).toString());        //Stringbuilder going through each string.
        for (int i = 1; i < cond.size(); i++){
            ans.append(" elif (" + cond.get(i).toString() + ") " + blocks.get(i).toString());
        }
        if (isElse) ans.append(" else " + blocks.get(blocks.size()-1));

        return ans.toString();
        /*
        if (block2 != null) return "if (" + cond.toString() + ")" + block.toString() + "else" + block2.toString();
        return ("if (" + cond.toString() + ")" + block.toString());
        */

    }
}

/** Implementation for WHILE nodes */
class WhileNode implements ProgramNode{
    BlockNode block;
    BoolNode cond;

    public WhileNode(BoolNode c, BlockNode b){
        block = b;
        cond = c;
    };

    public void execute(Robot robot){           // Checks to see if cond is true, then executes block once.
        while(cond.evaluate(robot)){
            block.execute(robot);
        }
    }

    public String toString(){
        return ("while (" + cond.toString() + ")" + block.toString() );
    }
}

/** BLOCK node Class */

class BlockNode implements ProgramNode {     // Block Node Class

    List<ProgramNode> operations;     // List of operations

    public BlockNode(List<ProgramNode> ops) { // Initialiser of Loop Node
        operations = ops;
    }

    public void addNode(ProgramNode op) {
        operations.add(op);
    }

    public void execute(Robot robot) {
        for (int i = 0; i < operations.size(); i++) {
            this.operations.get(i).execute(robot);    // Iterator for each operation in the block
        }
    }

    public String toString() {
        StringBuilder ans = new StringBuilder("");  //Uses a stringBuilder to form toString stmt.
        ans.append(operations.get(0));
        for (int i = 1; i < operations.size(); i++) {
            ans.append(", ").append(operations.get(i));
        }
        return ("{" + ans.toString() + "}");

    }
}

