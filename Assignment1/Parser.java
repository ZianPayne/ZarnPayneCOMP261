import javax.lang.model.element.ModuleElement;
import javax.sound.sampled.EnumControl;
import java.util.*;
import java.util.regex.*;

/**
 * See assignment handout for the grammar.
 * You need to implement the parse(..) method and all the rest of the parser.
 * There are several methods provided for you:
 * - several utility methods to help with the parsing
 * See also the TestParser class for testing your code.
 */
public class Parser {


    // Useful Patterns

    static final Pattern NUMPAT = Pattern.compile("-?[1-9][0-9]*|0");
    static final Pattern OPENPAREN = Pattern.compile("\\(");
    static final Pattern CLOSEPAREN = Pattern.compile("\\)");
    static final Pattern OPENBRACE = Pattern.compile("\\{");
    static final Pattern CLOSEBRACE = Pattern.compile("\\}");
    static final Pattern SEMICOLON = Pattern.compile("\\;");
    static final Pattern COMMA = Pattern.compile("\\,");


    /** Strings for Patterns */

    static final String ACT_STRING =  "move|turnL|turnR|turnAround|shieldOn|shieldOff|wait|takeFuel";
    static final String LOOP_STRING = "loop";
    static final String IF_STRING = "if";
    static final String WHILE_STRING = "while";
    static final String OP_STRING = "add|sub|mul|div";
    static final String RELOP_STRING = "lt|gt|eq";
    static final String SENS_STRING = "fuelLeft|oppLR|oppFB|numBarrels|barrelLR|barrelFB|wallDist";
    static final String COND_STRING = "and|or|not";
    static final String STMT_STRING = ACT_STRING+"|"+LOOP_STRING+"|"+IF_STRING+"|"+WHILE_STRING+"|"+OP_STRING;

    /** Patterns for Code */

    /* PROGRAM NODE PATTERNS*/

    static final Pattern STMT_PATTERN = Pattern.compile(STMT_STRING);

    static final Pattern ACT_PATTERN = Pattern.compile(ACT_STRING);
    static final Pattern MOVE_PAT = Pattern.compile("move");
    static final Pattern TURNL_PATTERN = Pattern.compile("turnL");
    static final Pattern TURNR_PATTERN = Pattern.compile("turnR");
    static final Pattern TURNAROUND_PATTERN = Pattern.compile("turnAround");
    static final Pattern SHIELDON_PATTERN = Pattern.compile("shieldOn");
    static final Pattern SHIELDOFF_PATTERN = Pattern.compile("shieldOff");
    static final Pattern WAIT_PATTERN = Pattern.compile("wait");
    static final Pattern TAKEFUEL_PATTERN = Pattern.compile("takeFuel");
    static final Pattern LOOP_PATTERN = Pattern.compile(LOOP_STRING);
    static final Pattern IF_PATTERN = Pattern.compile(IF_STRING);
    static final Pattern ELIF_PATTERN = Pattern.compile("elif");
    static final Pattern ELSE_PATTERN = Pattern.compile("else");
    static final Pattern WHILE_PATTERN = Pattern.compile(WHILE_STRING);

    /* Bool Patterns */

    static final Pattern RELOP_PATTERN = Pattern.compile(RELOP_STRING);

    static final Pattern LT_PATTERN = Pattern.compile("lt");
    static final Pattern GT_PATTERN = Pattern.compile("gt");
    static final Pattern EQ_PATTERN = Pattern.compile("eq");
    static final Pattern COND_PATTERN = Pattern.compile(COND_STRING);
    static final Pattern OR_PATTERN = Pattern.compile("or");
    static final Pattern AND_PATTERN = Pattern.compile("and");
    static final Pattern NOT_PATTERN = Pattern.compile("not");

    /* Int Patterns */

    static final Pattern SENS_PATTERN = Pattern.compile(SENS_STRING);

    static final Pattern FUEL_LEFT_PATTERN = Pattern.compile("fuelLeft");

    static final Pattern OPP_LR_PATTERN = Pattern.compile("oppLR");
    static final Pattern OPP_FB_PATTERN = Pattern.compile("oppFB");
    static final Pattern NUM_BARRELS_PATTERN = Pattern.compile("numBarrels");
    static final Pattern BARREL_LR_PATTERN = Pattern.compile("barrelLR");
    static final Pattern BARREL_FB_PATTERN = Pattern.compile("barrelFB");
    static final Pattern WALL_DIST_PATTERN = Pattern.compile("wallDist");

    /* EXPR and CALC */

    static final Pattern OP_PATTERN = Pattern.compile(OP_STRING);
    static final Pattern ADD_PATTERN = Pattern.compile("add");
    static final Pattern SUB_PATTERN = Pattern.compile("sub");
    static final Pattern MUL_PATTERN = Pattern.compile("mul");
    static final Pattern DIV_PATTERN = Pattern.compile("div");

    //----------------------------------------------------------------
    /**
     * The top of the parser, which is handed a scanner containing
     * the text of the program to parse.
     * Returns the parse tree.
     */
    ProgramNode parse(Scanner s) {
        // Set the delimiter for the scanner.
        s.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");
        // THE PARSER GOES HERE
        // Call the parseProg method for the first grammar rule (PROG) and return the node
        ProgramNode tree = parseProg(s);
        if (s.hasNext()) parseProg(s);
        System.out.println(tree);
        return tree;
    }

    /** Parser for PROG Node */
    private ProgramNode parseProg(Scanner s){                      //Parser for Prog, goes through STMT pattern
        List<ProgramNode> nodes = new ArrayList<>();

        if (!s.hasNext()) {
            fail("Empty Expresssion", s);
        }
        else if (s.hasNext(STMT_PATTERN)) {                        // If there is a statement, perform a do while to capture all statements.
            do {
                nodes.add(parseStmt(s));
            } while (s.hasNext(STMT_PATTERN));
            return new ProgNode(nodes);
        }
        else fail("Not an expression", s);

        return null;
    }

    /** Parser for STMT Nodes */
    private ProgramNode parseStmt(Scanner s){                      // Checks the various statements
        ProgramNode n;
        if (s.hasNext(ACT_PATTERN)) {
            n = parseAct(s);
            require(SEMICOLON, "Expecting ';'", s);
            return new StmtNode(n);
        }
        if (s.hasNext(IF_PATTERN)) {
            n = parseIf(s);
            return new StmtNode(n);
        }
        if (s.hasNext(WHILE_PATTERN)) {
            n = parseWhile(s);
            return new StmtNode(n);
        }
        if (s.hasNext(OPENBRACE)) {
            n = parseBlock(s);
            return new StmtNode(n);
        }
        if (s.hasNext(LOOP_PATTERN)) { return new StmtNode(parseLoop(s));}
        fail("Unknown Command", s);
        return null;
    }

    /** Parser for ACT Nodes*/
    private ProgramNode parseAct(Scanner s){                     // Checks for Act Pattern
        if (s.hasNext(MOVE_PAT)) { return new ActNode(parseMove(s));}
        if (s.hasNext(TURNL_PATTERN)) { return new ActNode(parseTurnL(s));}
        if (s.hasNext(TURNR_PATTERN)) { return new ActNode(parseTurnR(s));}
        if (s.hasNext(TURNAROUND_PATTERN)) { return new ActNode(parseTurnAround(s));}
        if (s.hasNext(SHIELDON_PATTERN)) { return new ActNode(parseShieldOn(s));}
        if (s.hasNext(SHIELDOFF_PATTERN)) { return new ActNode(parseShieldOff(s));}
        if (s.hasNext(WAIT_PATTERN)) { return new ActNode(parseWait(s));}
        if (s.hasNext(TAKEFUEL_PATTERN)) { return new ActNode(parseTakeFuel(s));}
        fail("Unknown Command", s);
        return null;
    }

    /** Parser for MOVE nodes */
    private ProgramNode parseMove(Scanner s){

        require(MOVE_PAT, "Expecting 'move'", s);
        if (s.hasNext(OPENPAREN)){                          // Parentheses means greater numAct
            require(OPENPAREN, "Expecting '('", s);
            IntNode n = parseExpr(s);                       // Collects expr node, then returns in movenode object
            require(CLOSEPAREN, "Expecting ')'", s);
            return new MoveNode(n);
        }
        return new MoveNode(new NumNode(1));
    }

    /** Parser for TURN_L nodes */
    private ProgramNode parseTurnL(Scanner s){
        require(TURNL_PATTERN, "Expecting 'turnL'", s);
        return new TurnLNode();
    }

    /** Parser for TURN_R nodes */
    private ProgramNode parseTurnR(Scanner s){
        require(TURNR_PATTERN, "Expecting 'turnR'", s);
        return new TurnRNode();
    }

    /** Parser for TURN_AROUND nodes */
    private ProgramNode parseTurnAround(Scanner s){
        require(TURNAROUND_PATTERN, "Expecting 'turnAround'", s);
        return new TurnAroundNode();
    }

    /** Parser for SHIELD_ON nodes */
    private ProgramNode parseShieldOn(Scanner s){
        require(SHIELDON_PATTERN, "Expecting 'shieldOn'", s);
        return new ShieldOnNode();
    }

    /** Parser for SHIELD_OFF nodes */
    private ProgramNode parseShieldOff(Scanner s){
        require(SHIELDOFF_PATTERN, "Expecting 'shieldOff'", s);
        return new ShieldOffNode();
    }

    /** Parser for WAIT nodes */
    private ProgramNode parseWait(Scanner s){

        require(WAIT_PATTERN, "Expecting 'wait'", s);

        if (s.hasNext(OPENPAREN)){                          // Parentheses means greater numAct
            require(OPENPAREN, "Expecting '('", s);
            IntNode e = parseExpr(s);
            require(CLOSEPAREN, "Expecting ')'", s);
            return new WaitNode(e);
        }
        return new WaitNode(new NumNode(1));
    }
    /** Parser for TAKE_FUEL nodes */
    private ProgramNode parseTakeFuel(Scanner s){
        require(TAKEFUEL_PATTERN, "Expecting 'takeFuel'", s);
        return new TakeFuelNode();
    }

    /** Parser for the LOOP node */
    private ProgramNode parseLoop(Scanner s){

        require(LOOP_PATTERN, "Expecting 'loop'", s);
        return new LoopNode(parseBlock(s));
    }

    /** Parser for BLOCK nodes */
    private BlockNode parseBlock(Scanner s){
        List<ProgramNode> operations = new ArrayList<>();

        require(OPENBRACE, "Expecting '{'", s);

        do {                                    // Do while operates while still available programs.
            operations.add(parseProg(s));
        } while (!s.hasNext(CLOSEBRACE));

        require(CLOSEBRACE, "Expecting '}'", s);

        return new BlockNode(operations);
    }

    /** Parser for IF nodes */
    private ProgramNode parseIf(Scanner s){
        ArrayList<BlockNode> blocks = new ArrayList<>();        // Arraylist for if statement blocks.
        ArrayList<BoolNode> conds = new ArrayList<>();          // Arraylist for if statement conds.


        if (s.hasNext(IF_PATTERN)) {
            require(IF_PATTERN, "Expecting 'if'", s);          // This loop will keep running as long as there is if/elif next in the pattern
            require(OPENPAREN, "Expecting '('", s);
            conds.add(parseCond(s));
            require(CLOSEPAREN, "Expecting ')'", s);
            blocks.add(parseBlock(s));

            while (s.hasNext(ELIF_PATTERN)){
                require(ELIF_PATTERN, "Expecting 'elif'", s);  // This loop will keep running as long as there is if/elif next in the pattern
                require(OPENPAREN, "Expecting '('", s);
                conds.add(parseCond(s));
                require(CLOSEPAREN, "Expecting ')'", s);
                blocks.add(parseBlock(s));
            };
        }
        if (s.hasNext(ELSE_PATTERN)){
            s.next();
            blocks.add(parseBlock(s));
            return new IfNode(blocks, conds, true);
        }
        return new IfNode(blocks, conds, false);
    }


    /** Parser for WHILE nodes */
    private ProgramNode parseWhile(Scanner s){
        BoolNode cond;
        BlockNode operations;

        require(WHILE_PATTERN, "Expecting 'while'", s);
        require(OPENPAREN, "Expecting '('", s);
        cond = parseCond(s);
        require(CLOSEPAREN, "Expecting ')'", s);
        operations = parseBlock(s);

        return new WhileNode(cond, operations);
    }


    //-----------BOOL NODES----------------


    /** Parser for Cond Nodes*/

    private BoolNode parseCond(Scanner s){
        String comp;          // The comparison for the condition
        BoolNode arg1;
        BoolNode arg2;

        if (s.hasNext(RELOP_PATTERN)){                      // RELOP Pattern
            comp = parseRelop(s);
            require(OPENPAREN, "Expecting '('", s);
            IntNode expr1 = parseExpr(s);
            require(COMMA, "Expecting ','", s);
            IntNode expr2 = parseExpr(s);
            require(CLOSEPAREN, "Expecting ')'", s);
            return new CondNode(comp, expr1, expr2, null, null);
        }
        if (s.hasNext(OR_PATTERN)){                         // OR PATTERN
            s.next();
            comp = "or";
            require(OPENPAREN, "Expecting '('", s);
            arg1 = parseCond(s);
            require(COMMA, "Expecting ','", s);
            arg2 = parseCond(s);
            require(CLOSEPAREN, "Expecting ')'", s);
            return new CondNode(comp, null, null, arg1, arg2);
        }
        if (s.hasNext(AND_PATTERN)){                         // AND PATTERN
            s.next();
            comp = "and";
            require(OPENPAREN, "Expecting '('", s);
            arg1 = parseCond(s);
            require(COMMA, "Expecting ','", s);
            arg2 = parseCond(s);
            require(CLOSEPAREN, "Expecting ')'", s);
            return new CondNode(comp, null, null, arg1, arg2);
        }
        if (s.hasNext(NOT_PATTERN)){                         // NOT PATTERN
            s.next();
            comp = "not";
            require(OPENPAREN, "Expecting '('", s);
            arg1 = parseCond(s);
            require(CLOSEPAREN, "Expecting ')'", s);
            return new CondNode(comp, null, null, arg1, null);
        }
        fail("No valid condition", s);
        return null;
    }


    /** Parser for RELOP nodes
     *
     * Returns a string based on what pattern it matches
     * */
    private String parseRelop(Scanner s){
        if(s.hasNext(LT_PATTERN)) { s.next(); return "lt"; }
        if(s.hasNext(GT_PATTERN)) { s.next(); return "gt"; }
        if(s.hasNext(EQ_PATTERN)) { s.next(); return "eq"; }
        fail("No valid relative operator", s);
        return null;
    }

    /** Parser for LT nodes */
    private BoolNode parseLT(Scanner s){
        require(LT_PATTERN, "Expecting 'lt'", s);
        return new LTNode();
    }

    /** Parser for GT nodes */
    private BoolNode parseGT(Scanner s){
        require(GT_PATTERN, "Expecting 'gt'", s);
        return new GTNode();
    }

    /** Parser for EQ nodes */
    private BoolNode parseEQ(Scanner s){
        require(EQ_PATTERN, "Expecting 'eq'", s);
        return new EQNode();
    }

    //----------------INT NODES----------------


    /** Parser for SENS nodes */

    private SensNode parseSensor(Scanner s){
        SensNode sens;

        //static final String SENS_STRING = "fuelLeft|oppLR|offFB|numBarrels|barrelLR|barrelFB|wallDist";
        if (s.hasNext(FUEL_LEFT_PATTERN)){return new SensNode(parseFuelLeft(s));}
        if (s.hasNext(OPP_LR_PATTERN)){return new SensNode(parseOppLR(s));}
        if (s.hasNext(OPP_FB_PATTERN)){return new SensNode(parseOppFB(s));}
        if (s.hasNext(NUM_BARRELS_PATTERN)){ return new SensNode(parseNumBarrels(s));}
        if (s.hasNext(BARREL_LR_PATTERN)){return new SensNode(parseBarrelLR(s));}
        if (s.hasNext(BARREL_FB_PATTERN)){return new SensNode(parseBarrelFB(s));}
        if (s.hasNext(WALL_DIST_PATTERN)){return new SensNode(parseWallDist(s));}

        fail("No valid Sensor", s);
        return null;
    }

    /** Parser for fuelLeft nodes */
    private IntNode parseFuelLeft(Scanner s){
        require(FUEL_LEFT_PATTERN, "Expecting 'fuelLeft'", s);
        return new FuelLeftNode();
    }

    /** Parser for OPP_LR nodes */
    private IntNode parseOppLR(Scanner s){
        require(OPP_LR_PATTERN, "Expecting 'oppLR'", s);
        return new OppLRNode();
    }

    /** Parser for OPP_FB nodes */
    private IntNode parseOppFB(Scanner s){
        require(OPP_FB_PATTERN, "Expecting 'oppFB'", s);
        return new OppFBNode();
    }

    /** Parser for OPP_NUM_BARRELS nodes */
    private IntNode parseNumBarrels(Scanner s){
        require(NUM_BARRELS_PATTERN, "Expecting 'numBarrels'", s);
        return new NumBarrelsNode();
    }

    /** Parser for BARREL_LR nodes */
    private IntNode parseBarrelLR(Scanner s){
        require(BARREL_LR_PATTERN, "Expecting 'barrelLR'", s);

        if (s.hasNext(OPENPAREN)){                                        // Parentheses means start code for n'th barrel
            require(OPENPAREN, "Expecting '('", s);
            IntNode e = parseExpr(s);
            require(CLOSEPAREN, "Expecting ')'", s);
            return new BarrelLRNode(e);
        }

        return new BarrelLRNode(new NumNode(0));
    }

    /** Parser for BARREL_FB nodes */
    private IntNode parseBarrelFB(Scanner s){
        require(BARREL_FB_PATTERN, "Expecting 'barrelFB'", s);

        if (s.hasNext(OPENPAREN)){                                        // Parentheses means start code for n'th barrel
            require(OPENPAREN, "Expecting '('", s);
            IntNode e = parseExpr(s);
            require(CLOSEPAREN, "Expecting ')'", s);
            return new BarrelFBNode(e);
        }

        return new BarrelFBNode(new NumNode(0));
    }

    /** Parser for WALL_DIST nodes */
    private IntNode parseWallDist(Scanner s){
        require(WALL_DIST_PATTERN, "Expecting 'wallDist'", s);
        return new WallDistNode();
    }

    /** Parser for Numbers, returns a num */
    private NumNode parseNum(Scanner s){
        if(s.hasNext(NUMPAT)) return new NumNode(s.nextInt());
        fail("Expecting an Int", s);
        return null;
    }

    /** Parser for EXPR and all its functions*/
    private IntNode parseExpr(Scanner s){
        if(s.hasNext(OP_PATTERN)){                                          // If node is a calculation, stores both exprs and operator
            IntNode op = parseOp(s);
            require(OPENPAREN, "Expecting '('", s);
            IntNode expr1 = parseExpr(s);
            require(COMMA, "Expecting ','", s);
            IntNode expr2 = parseExpr(s);
            require(CLOSEPAREN, "Expecting ')'" , s);
            return new ExprNode( op, expr1, expr2);
        }
        else if (s.hasNext(SENS_PATTERN)) return parseSensor(s);            // If node is a sensor
        else if (s.hasNext(NUMPAT)) return parseNum(s);        // If node is a num
        fail("Invalid EXPR", s);
        return null;
    }

    /** Parser for operations */
    private IntNode parseOp(Scanner s){                 // Gets an operator node with correct operation attached.
        if (s.hasNext(ADD_PATTERN)) return parseAdd(s);
        if (s.hasNext(SUB_PATTERN)) return parseSub(s);
        if (s.hasNext(MUL_PATTERN)) return parseMul(s);
        if (s.hasNext(DIV_PATTERN)) return parseDiv(s);
        return null;
    }

    /** Parser for addition */
    private IntNode parseAdd(Scanner s){
        require(ADD_PATTERN, "Expected 'add'", s);
        return new OpNode("add");
    }

    /** Parser for subtraction */
    private IntNode parseSub(Scanner s){
        require(SUB_PATTERN, "Expected 'sub'", s);
        return new OpNode("sub");
    }

    /** Parser for multiplications */
    private IntNode parseMul(Scanner s){
        require(MUL_PATTERN, "Expected 'mul'", s);
        return new OpNode("mul");
    }

    /** Parser for division */
    private IntNode parseDiv(Scanner s){
        require(DIV_PATTERN, "Expected 'div'", s);
        return new OpNode("div");
    }
    //----------------------------------------------------------------
    // utility methods for the parser
    // - fail(..) reports a failure and throws exception
    // - require(..) consumes and returns the next token as long as it matches the pattern
    // - requireInt(..) consumes and returns the next token as an int as long as it matches the pattern
    // - checkFor(..) peeks at the next token and only consumes it if it matches the pattern

    /**
     * Report a failure in the parser.
     */
    static void fail(String message, Scanner s) {
        String msg = message + "\n   @ ...";
        for (int i = 0; i < 5 && s.hasNext(); i++) {
            msg += " " + s.next();
        }
        throw new ParserFailureException(msg + "...");
    }

    /**
     * Requires that the next token matches a pattern if it matches, it consumes
     * and returns the token, if not, it throws an exception with an error
     * message
     */
    static String require(String p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    static String require(Pattern p, String message, Scanner s) {
        if (s.hasNext(p)) {return s.next();}
        fail(message, s);
        return null;
    }

    /**
     * Requires that the next token matches a pattern (which should only match a
     * number) if it matches, it consumes and returns the token as an integer
     * if not, it throws an exception with an error message
     */
    static int requireInt(String p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    static int requireInt(Pattern p, String message, Scanner s) {
        if (s.hasNext(p) && s.hasNextInt()) {return s.nextInt();}
        fail(message, s);
        return -1;
    }

    /**
     * Checks whether the next token in the scanner matches the specified
     * pattern, if so, consumes the token and return true. Otherwise returns
     * false without consuming anything.
     */
    static boolean checkFor(String p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;}
        return false;
    }

    static boolean checkFor(Pattern p, Scanner s) {
        if (s.hasNext(p)) {s.next(); return true;}
        return false;
    }

}

// You could add the node classes here or as separate java files.
// (if added here, they must not be declared public or private)
// For example:
//  class BlockNode implements ProgramNode {.....
//     with fields, a toString() method and an execute() method
//





