package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;

import static cop5556sp17.Scanner.Kind.*;

public class Scanner {
    /**
     * All Keywords supported by the language.
     */
    private final Kind[] keywords = {KW_INTEGER, KW_BOOLEAN, KW_IMAGE, KW_URL, KW_FILE, KW_FRAME, KW_WHILE, KW_IF, OP_SLEEP, KW_SCREENHEIGHT, KW_SCREENWIDTH};
    private final Kind[] filter_op_keywords = {OP_GRAY, OP_CONVOLVE, OP_BLUR, KW_SCALE};
    private final Kind[] image_op_keywords = {OP_WIDTH, OP_HEIGHT};
    private final Kind[] frame_op_keywords = {KW_XLOC, KW_YLOC, KW_HIDE, KW_SHOW, KW_MOVE};
    private final Kind[] boolean_literals = {KW_TRUE, KW_FALSE};


    /**
     * Kind enum
     */

    public static enum Kind {
        IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"),
        KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"),
        KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"),
        SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"),
        RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"),
        EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="),
        PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"),
        ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"),
        KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"),
        OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"),
        KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"),
        KW_SCALE("scale"), EOF("eof");

        Kind(String text) {
            this.text = text;
        }

        final String text;

        String getText() {
            return text;
        }

        public static Kind fromString(String text) {
            if (text != null) {
                for (Kind b : Kind.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Enumeration to remember the states in DFA.
     */
    public static enum State {
        START("start"), AFTER_OR("|"), AFTER_EQUAL("="), AFTER_NOT("!"), AFTER_LESS_THAN("<"), AFTER_GREATER_THAN(">"), AFTER_MINUS("-"),
        INT_LIT("int_lit"), IDENTIFIER("identifier"), AFTER_DIV("/"), COMMENT("comment");

        final String text;

        State(String text) {
            this.text = text;
        }

        String getText() {
            return text;
        }
    }

    /**
     * Thrown by Scanner when an illegal character is encountered
     */
    @SuppressWarnings("serial")
    public static class IllegalCharException extends Exception {
        public IllegalCharException(String message) {
            super(message);
        }
    }

    /**
     * Thrown by Scanner when an int literal is not a value that can be represented by an int.
     */
    @SuppressWarnings("serial")
    public static class IllegalNumberException extends Exception {
        public IllegalNumberException(String message) {
            super(message);
        }
    }


    /**
     * Holds the line and position in the line of a token.
     */
    static class LinePos {
        public final int line;
        public final int posInLine;

        public LinePos(int line, int posInLine) {
            super();
            this.line = line;
            this.posInLine = posInLine;
        }

        @Override
        public String toString() {
            return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
        }
    }


    public class Token {

        public final Kind kind;
        public final int pos;  //position in input array
        public final int length;

        //returns the text of this Token
        public String getText() {
            return chars.substring(pos, pos + length);
        }

        //returns a LinePos object representing the line and column of this Token
        LinePos getLinePos() {
            if (chars.length() == 0) {
                return new LinePos(0, 0);
            }
            int column = 0;
            int temp = pos;
            if (pos == chars.length())
                temp--;
            while (temp >= 0 && (chars.charAt(temp) != '\n')) {
                column++;
                temp--;
            }
            int lineNo = 0;
            while (temp >= 0) {
                if (chars.charAt(temp) == '\n')
                    lineNo++;
                temp--;
            }
            if (pos == chars.length()) {
                return new LinePos(lineNo, column);
            }
            return new LinePos(lineNo, column - 1);
        }

        Token(Kind kind, int pos, int length) {
            this.kind = kind;
            this.pos = pos;
            this.length = length;
        }

        /**
         * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
         * Note that the validity of the input should have been checked when the Token was created.
         * So the exception should never be thrown.
         *
         * @return int value of this token, which should represent an INT_LIT
         * @throws NumberFormatException
         */
        public int intVal() throws NumberFormatException {
            if (kind == INT_LIT) {
                return Integer.parseInt(getText());
            } else {
                throw new NumberFormatException("Tried to convert a non integer '" + getText() + "' at pos " + pos + " to integer");
            }
        }

    }


    Scanner(String chars) {
        this.chars = chars;
        tokens = new ArrayList<Token>();

        this.keyword_map = new HashMap<String, Kind>();

        // Insert Keywords into map.
        for (Kind keyword : keywords) {
            keyword_map.put(keyword.getText(), keyword);
        }

        // Insert filter_op_keywords into map
        for (Kind filter_op_keyword : filter_op_keywords) {
            keyword_map.put(filter_op_keyword.getText(), filter_op_keyword);
        }

        // Insert image_op_keywords into map
        for (Kind image_op_keyword : image_op_keywords) {
            keyword_map.put(image_op_keyword.getText(), image_op_keyword);
        }

        // Insert frame_op_keywords into map
        for (Kind frame_op_keyword : frame_op_keywords) {
            keyword_map.put(frame_op_keyword.getText(), frame_op_keyword);
        }

        // Insert boolean_literals into map
        for (Kind boolean_literal : boolean_literals) {
            keyword_map.put(boolean_literal.getText(), boolean_literal);
        }

    }


    /**
     * Initializes Scanner object by traversing chars and adding tokens to tokens list.
     *
     * @return this scanner
     * @throws IllegalCharException
     * @throws IllegalNumberException
     */
    public Scanner scan() throws IllegalCharException, IllegalNumberException {
        int pos = 0;
        int length = chars.length();
        State state = State.START;
        int startPos = 0;
        int ch;
        while (pos <= length) {
            ch = pos < length ? chars.charAt(pos) : -1;
            switch (state) {
                case START: {
                    int[] result = start(pos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case AFTER_OR: {
                    int[] result = after_or(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case AFTER_EQUAL: {
                    int[] result = after_equal(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case AFTER_NOT: {
                    int[] result = after_not(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case AFTER_LESS_THAN: {
                    int[] result = after_less_than(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case AFTER_GREATER_THAN: {
                    int[] result = after_greater_than(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case AFTER_MINUS: {
                    int[] result = after_minus(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case INT_LIT: {
                    int[] result = int_lit(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case IDENTIFIER: {
                    int[] result = identifier(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case AFTER_DIV: {
                    int[] result = after_div(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
                case COMMENT: {
                    int[] result = comment(pos, startPos);
                    pos = result[0];
                    startPos = result[1];
                    state = State.values()[result[2]];
                    break;
                }
            }
        }
        return this;
    }


    final HashMap<String, Kind> keyword_map;
    final ArrayList<Token> tokens;
    final String chars;
    int tokenNum;

    /*
     * Return the next token in the token list and update the state so that
     * the next call will return the Token..
     */
    public Token nextToken() {
        if (tokenNum >= tokens.size())
            return null;
        return tokens.get(tokenNum++);
    }

    /*
     * Return the next token in the token list without updating the state.
     * (So the following call to next will return the same token.)
     */
    public Token peek() {
        if (tokenNum >= tokens.size())
            return null;
        return tokens.get(tokenNum + 1);
    }


    /**
     * Returns a LinePos object containing the line and position in line of the
     * given token.
     * <p>
     * Line numbers start counting at 0
     *
     * @param t
     * @return
     */
    public LinePos getLinePos(Token t) {
        return t.getLinePos();
    }


    /**
     * Skip white spaces if any
     */
    private int skipWhiteSpace(int pos) {
        while (true) {
            int ch = pos < chars.length() ? chars.charAt(pos) : -1;
            if (Character.isWhitespace(ch)) {
                pos++;
            } else {
                break;
            }
        }
        return pos;
    }


    /**
     * Start state of the DFA.
     */
    private int[] start(int pos) throws IllegalCharException {
        pos = skipWhiteSpace(pos);
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        int startPos = pos;
        State state = State.START;
        switch (ch) {
            case -1: {
                tokens.add(new Token(Kind.EOF, pos, 0));
                pos++;
                break;
            }
            case '|': {
                state = State.AFTER_OR;
                pos++;
                break;
            }
            case '&': {
                tokens.add(new Token(Kind.AND, startPos, 1));
                pos++;
                break;
            }
            case '=': {
                state = State.AFTER_EQUAL;
                pos++;
                break;
            }
            case '!': {
                state = State.AFTER_NOT;
                pos++;
                break;
            }
            case '<': {
                state = State.AFTER_LESS_THAN;
                pos++;
                break;
            }
            case '>': {
                state = State.AFTER_GREATER_THAN;
                pos++;
                break;
            }
            case '+': {
                tokens.add(new Token(Kind.PLUS, startPos, 1));
                pos++;
                break;
            }
            case '-': {
                state = State.AFTER_MINUS;
                pos++;
                break;
            }
            case '*': {
                tokens.add(new Token(Kind.TIMES, startPos, 1));
                pos++;
                break;
            }
            case '/': {
                state = State.AFTER_DIV;
                pos++;
                break;
            }
            case '%': {
                tokens.add(new Token(Kind.MOD, startPos, 1));
                pos++;
                break;
            }
            case ';': {
                tokens.add(new Token(Kind.SEMI, startPos, 1));
                pos++;
                break;
            }
            case ',': {
                tokens.add(new Token(Kind.COMMA, startPos, 1));
                pos++;
                break;
            }
            case '(': {
                tokens.add(new Token(Kind.LPAREN, startPos, 1));
                pos++;
                break;
            }
            case ')': {
                tokens.add(new Token(Kind.RPAREN, startPos, 1));
                pos++;
                break;
            }
            case '{': {
                tokens.add(new Token(Kind.LBRACE, startPos, 1));
                pos++;
                break;
            }
            case '}': {
                tokens.add(new Token(Kind.RBRACE, startPos, 1));
                pos++;
                break;
            }
            default: {
                if (Character.isDigit(ch)) {
                    if (ch == '0') {
                        tokens.add(new Token(Kind.INT_LIT, startPos, 1));
                        pos++;
                    } else {
                        state = State.INT_LIT;
                        pos++;
                    }
                } else if (Character.isJavaIdentifierStart(ch)) {
                    state = State.IDENTIFIER;
                    pos++;
                } else {
                    throw new IllegalCharException("Illegal character " + (char)ch + " at position " + pos);
                }
            }
        } // end of switch
        return new int[]{pos, startPos, state.ordinal()};
    }

    /**
     * AFTER_OR state of the DFA.
     */
    private int[] after_or(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        switch (ch) {
            case '-': {
                if (pos + 1 < chars.length() && (chars.charAt(pos + 1) == '>')) {
                    tokens.add(new Token(Kind.BARARROW, startPos, 3));
                    pos++;
                    pos++;
                } else {
                    tokens.add(new Token(Kind.OR, startPos, 1));
                }
                break;
            }
            default: {
                tokens.add(new Token(Kind.OR, startPos, 1));
            }
        }
        return new int[]{pos, startPos, State.START.ordinal()};
    }

    /**
     * AFTER_EQUAL state of the DFA.
     */
    private int[] after_equal(int pos, int startPos) throws IllegalCharException {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        switch (ch) {
            case '=': {
                tokens.add(new Token(Kind.EQUAL, startPos, 2));
                pos++;
                break;
            }
            default: {
                throw new IllegalCharException("Illegal operator '=' at pos " + (pos - 1));
            }
        }
        return new int[]{pos, startPos, State.START.ordinal()};
    }

    /**
     * AFTER_NOT state of the DFA.
     */
    private int[] after_not(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        switch (ch) {
            case '=': {
                tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
                pos++;
                break;
            }
            default: {
                tokens.add(new Token(Kind.NOT, startPos, 1));
            }
        }
        return new int[]{pos, startPos, State.START.ordinal()};
    }


    /**
     * AFTER_LESS_THAN state of the DFA.
     */
    private int[] after_less_than(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        switch (ch) {
            case '=': {
                tokens.add(new Token(Kind.LE, startPos, 2));
                pos++;
                break;
            }
            case '-': {
                tokens.add(new Token(Kind.ASSIGN, startPos, 2));
                pos++;
                break;
            }
            default: {
                tokens.add(new Token(Kind.LT, startPos, 1));
            }
        }
        return new int[]{pos, startPos, State.START.ordinal()};
    }


    /**
     * AFTER_GREATER_THAN state of the DFA.
     */
    private int[] after_greater_than(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        switch (ch) {
            case '=': {
                tokens.add(new Token(Kind.GE, startPos, 2));
                pos++;
                break;
            }
            default: {
                tokens.add(new Token(Kind.GT, startPos, 1));
                break;
            }
        }
        return new int[]{pos, startPos, State.START.ordinal()};
    }

    /**
     * AFTER_MINUS state of the DFA.
     */
    private int[] after_minus(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        switch (ch) {
            case '>': {
                tokens.add(new Token(Kind.ARROW, startPos, 2));
                pos++;
                break;
            }
            default: {
                tokens.add(new Token(Kind.MINUS, startPos, 1));
                break;
            }
        }
        return new int[]{pos, startPos, State.START.ordinal()};
    }


    /**
     * INT_LIT state of the DFA.
     */
    private int[] int_lit(int pos, int startPos) throws IllegalNumberException {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        State state = State.START;
        if (Character.isDigit(ch)) {
            pos++;
            state = State.INT_LIT;
        } else {
            String integer = chars.substring(startPos, pos);
            try {
                int int_value = Integer.parseInt(integer);
            } catch (NumberFormatException e) {
                throw new IllegalNumberException("Illegal number " + integer + " at pos " + startPos);
            }
            tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
        }
        return new int[]{pos, startPos, state.ordinal()};
    }

    /**
     * IDENTIFIER state of the DFA.
     */
    private int[] identifier(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        State state = State.START;
        if (Character.isJavaIdentifierStart(ch) || Character.isDigit(ch)) {
            pos++;
            state = State.IDENTIFIER;
        } else {
            String identifier = chars.substring(startPos, pos);
            if (keyword_map.containsKey(identifier)) {
                tokens.add(new Token(keyword_map.get(identifier), startPos, pos - startPos));
            } else {
                tokens.add(new Token(IDENT, startPos, pos - startPos));
            }
        }
        return new int[]{pos, startPos, state.ordinal()};
    }

    /**
     * AFTER_DIV state of DFA
     */
    private int[] after_div(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        State state = State.START;
        if (ch == '*') {
            state = State.COMMENT;
            pos++;
        } else {
            tokens.add(new Token(DIV, startPos, 1));
        }
        return new int[]{pos, startPos, state.ordinal()};
    }

    /**
     * COMMENT state of DFA
     */
    private int[] comment(int pos, int startPos) {
        int ch = pos < chars.length() ? chars.charAt(pos) : -1;
        State state = State.COMMENT;
        if (ch == '*') {
            if (pos + 1 < chars.length() && (chars.charAt(pos + 1) == '/')) {
                state = State.START;
                pos++;
                pos++;
            } else {
                pos++;
            }
        } else if (ch == -1) {
            state = State.START;
        } else {
            pos++;
        }
        return new int[]{pos, startPos, state.ordinal()};
    }
}
