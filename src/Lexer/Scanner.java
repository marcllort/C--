package Lexer;

import Utils.Constants;
import Utils.ErrorHandler;

import java.util.ArrayList;
import java.util.Arrays;

import static Utils.Constants.EOF_PATTERN;
import static Utils.Constants.EOF_TYPE;


public class Scanner {

    private String code;
    private int arrayIndex = 0;
    private ArrayList<Token> arrayTokens = new ArrayList<>();
    private ErrorHandler errorHandler = new ErrorHandler();
    private int line, pos;

    public Scanner(String source) {
        line = 1;
        this.code = preProcessor(source);                                       // Clean the code
        System.out.println("Code: \n" + code + "\n");
    }

    private String preProcessor(String source) {

        String[] split = source.split("\\r?\\n");                         //Delete comments
        source = "";
        for (String s : split) {
            int offset = s.indexOf("//");
            if (-1 != offset) {
                s = s.substring(0, offset);
            }
            source += s + '\n';
        }
        source = source.replaceAll("/  +/g", " ");

        return source;
    }

    public void saveTokens() {
        String[] splited = code.split(" ");                             // Split code by spaces
        for (String s : splited) {
            if (!containsOperators(s) && s.length() > 0) {                                        // If contains operators, treat it differently
                Token t = Keywords.checkKeyword(s, line, pos);                  // Add Token to array of tokens
                arrayTokens.add(t);
                pos++;
            }
        }
    }

    private boolean containsOperators(String s) {

        if (stringContainsItemFromList(s, Constants.OPERATORS) && s.length() > 1) {
            String[] lines = s.split("\n");
            int index = 1;
            for (String lineString : lines) {
                if (lineString.length() > 0) {
                    lineString = lineString.replaceAll("==", " == ");
                    lineString = lineString.replaceAll("(?<![><!+-=])[=](?![=])", " = ");
                    lineString = lineString.replaceAll("!=", " != ");
                    lineString = lineString.replaceAll("\\<", " < ");
                    lineString = lineString.replaceAll("\\>", " > ");
                    lineString = lineString.replaceAll("\\;", " ; ");
                    lineString = lineString.replaceAll("-", " - ");
                    lineString = lineString.replaceAll("\\+", " + ");
                    lineString = lineString.replaceAll("\\(", " ( ");
                    lineString = lineString.replaceAll("\\)", " ) ");
                    lineString = lineString.replaceAll("\\{", " { ");
                    lineString = lineString.replaceAll("\\}", " } ");
                    //TODO: Add missing operators, try to find a better way
                    String[] splited = lineString.split(" ");                        // Split code by spaces
                    for (String string : splited) {
                        if (string.length() > 0) {
                            Token t = Keywords.checkKeyword(string, line, pos);                 // Add Token to array of tokens
                            arrayTokens.add(t);
                            pos++;
                        }
                    }
                }
                    if (!(index == lines.length && !s.substring(s.length() - 1).equals("\n"))) {
                        pos = 0;
                        line++;
                        index++;
                    }


            }
            return true;
        }
        return false;
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }

    public Token getToken() {
        if (arrayIndex < arrayTokens.size()) {
            Token token = arrayTokens.get(arrayIndex++);

            if (token.getType() == 400) {                                                       // If token type is unknown
                errorHandler.printError("Undefined type!");
            }
            return token;
        }
        Token t2 = Keywords.checkKeyword(EOF_PATTERN, line, pos);
        arrayIndex = 0;

        return t2;
    }

    public void printTokens() {
        Token tok = new Token();
        System.out.println("\t\t\t\tTOKENS:\n");
        while (tok.getType() != EOF_TYPE) {
            tok = this.getToken();
            if (tok != null) {
                System.out.println(tok.getData() + "\t\tTYPE: " + tok.getType() + "\t\tLINE: " + tok.getLine() + "\t\tPOS: " + tok.getPos());
                System.out.println("------------------------------------------");
            }
        }
    }

}