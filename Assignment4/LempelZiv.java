
import java.lang.reflect.Array;
import java.util.*;
import java.nio.charset.*;

public class LempelZiv {
    private static final int WINDOW_SIZE = 100;

    /**
     * Take uncompressed input as a text string, compress it, and return it as a
     * text string.
     */
    public static String compress(String input) {
        // TODO fill this in.
        int cursor = 0;
        int windowLength = WINDOW_SIZE;
        int buffersize = 8;

        String window = "";
        boolean foundPrefix = false;

        StringBuilder output = new StringBuilder();

        while (cursor < input.length()){
            foundPrefix = false;
            window = input.substring((cursor-WINDOW_SIZE<1?0:cursor-WINDOW_SIZE),cursor); // sets the window size, uses tuple to ensure no null pointer errors.
            windowLength = window.length();                                               // Then finds current size based on substrings

            for (int i = (buffersize+cursor < input.length() ? buffersize : input.length()-cursor); i >= 1; i--) {       // Looks for the largest buffer possible
                String prefixSearch = input.substring(cursor, cursor+i);

                int indexFind = window.indexOf(prefixSearch);

                if (indexFind != -1){
                    foundPrefix = true;
                    if (cursor+i+1 < input.length()) output.append(String.format("[%s|%s|%s]", windowLength - indexFind, i, input.substring(cursor+i,cursor+i+1)));
                    else {                      // If end of a string
                        output.append(String.format("[%s|%s|%s]", windowLength - indexFind, i, ""));
                    }
                    cursor += i+1;
                    break;
                }
            }

            if(!foundPrefix) {
                String singleChar = input.substring(cursor, cursor+1);
                if (singleChar.length() != 0) output.append(String.format("[0|0|%s]", singleChar));   // In the case no prefix is found
                cursor++;
            }
        }


        return output.toString();
    }

    /**
     * Take compressed input as a text string, decompress it, and return it as a
     * text string.
     */
    public static String decompress(String compressed) {
        // TODO fill this in.
        StringBuilder output = new StringBuilder();

        int cursor = 0;                           // Current point while going through tuples


        while (cursor < compressed.length() && cursor != -1){

            ArrayList<String> curTuple = createTuple(compressed, cursor);

           // String curTuple = compressed.substring(cursor, cursor+7);

           if ( Integer.parseInt((curTuple.get(0))) == 0){               // In the case of an empty tuple
               output.append(curTuple.get(2));
           }
           else {
               output.append(output.substring(output.length() - Integer.parseInt(curTuple.get(0)), output.length() - Integer.parseInt(curTuple.get(0)) + Integer.parseInt(curTuple.get(1))));       // Adds substring to output
               output.append(curTuple.get(2));
            }
           cursor = findNextTuple(cursor, compressed);
        }


        return output.toString();
    }

    private static ArrayList<String> createTuple(String text, int cursor){
        String current = text.substring(cursor, text.indexOf("]", cursor)+1);
        ArrayList<String> tuple = new ArrayList<>();

        int firstPipe = current.indexOf("|");                                               // Hideous bit of code to find positions of tuple
        try {
            tuple.add(current.substring(1, firstPipe));
        } catch (Exception e){
            System.out.println(current);
        }

        int secondPipe = current.indexOf("|", firstPipe+1);
        tuple.add(current.substring(firstPipe+1,secondPipe));

        tuple.add(current.substring(secondPipe+1, current.length()-1));

        return tuple;
    }

    /** Returns the next tuples index. Used to move cursor correct amount.
     *
     * @param startingIndex:    starting index of previous tuple
     * @param text:             text to be searched in
     * @return index of next '[' character
     */
    private static int findNextTuple(int startingIndex, String text){
        int next;

        next = text.indexOf('[', startingIndex+1);


        return next;
    }

    /**
     * The getInformation method is here for your convenience, you don't need to
     * fill it in if you don't want to. It is called on every run and its return
     * value is displayed on-screen. You can use this to print out any relevant
     * information from your compression.
     */
    public String getInformation() {
        return "";
    }
}
