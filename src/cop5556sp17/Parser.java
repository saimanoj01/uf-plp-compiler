package cop5556sp17;

/**
 * Created by saima_000 on 2/12/2017.
 */
import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;

public class Parser {

    /**
     * Exception to be thrown if a syntax error is detected in the input.
     * You will want to provide a useful error message.
     *
     */
    @SuppressWarnings("serial")
    public static class SyntaxException extends Exception {
        public SyntaxException(String message) {
            super(message);
        }
    }

    /**
     * Useful during development to ensure unimplemented routines are
     * not accidentally called during development.  Delete it when
     * the Parser is finished.
     *
     */
    @SuppressWarnings("serial")
    public static class UnimplementedFeatureException extends RuntimeException {
        public UnimplementedFeatureException() {
            super();
        }
    }

    Scanner scanner;
    Token t;

    Parser(Scanner scanner) {
        this.scanner = scanner;
        t = scanner.nextToken();
    }

    /**
     * parse the input using tokens from the scanner.
     * Check for EOF (i.e. no trailing junk) when finished
     *
     * @throws SyntaxException
     */
    void parse() throws SyntaxException {
        program();
        matchEOF();
        return;
    }


    /**
     * program ::=  IDENT block
     * program ::=  IDENT param_dec ( , param_dec )*   block
     * @throws SyntaxException
     */
    void program() throws SyntaxException {
        match(IDENT);

        Kind kind = t.kind;
        if(kind == KW_URL || kind == KW_FILE || kind == KW_INTEGER || kind == KW_BOOLEAN) {
            paramDec();
            while(t.kind == COMMA) {
                consume();
                paramDec();
            }
        }
        block();
    }


    /**
     * paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN ) IDENT
     * @throws SyntaxException
     */
    void paramDec() throws SyntaxException {
        Kind kind = t.kind;
        if (kind == KW_URL || kind == KW_FILE || kind == KW_INTEGER || kind == KW_BOOLEAN) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'paramDec'");
        }
        match(IDENT);
    }


    /**
     * block ::= { ( dec | statement) * }
     * @throws SyntaxException
     */
    void block() throws SyntaxException {
        match(LBRACE);
        while(!t.isKind(EOF) && !t.isKind(RBRACE)) {
            if(t.kind == KW_INTEGER || t.kind == KW_BOOLEAN || t.kind == KW_IMAGE || t.kind == KW_FRAME) {
                dec();
            }
            else {
                statement();
            }
        }
        match(RBRACE);
    }


    /**
     * dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
     * @throws SyntaxException
     */
    void dec() throws SyntaxException {
        Kind kind = t.kind;
        if(kind == KW_INTEGER || kind == KW_BOOLEAN || kind == KW_IMAGE || kind == KW_FRAME) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'dec'");
        }
        match(IDENT);
    }


    /**
     * statement ::= OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
     * @throws SyntaxException
     */
    void statement() throws SyntaxException {
        if(t.kind == OP_SLEEP) {
            match(OP_SLEEP);
            expression();
            match(SEMI);
        }
        else if(t.kind == KW_WHILE) {
            whileProduction();
        }
        else if(t.kind == KW_IF) {
            ifProduction();
        }
        else if(t.kind == IDENT && scanner.peek().kind == ASSIGN) {
            assign();
            match(SEMI);
        }
        else {
            chain();
            match(SEMI);
        }
    }


    /**
     * assign ::= IDENT ASSIGN expression
     * @throws SyntaxException
     */
    void assign() throws SyntaxException {
        match(IDENT);
        match(ASSIGN);
        expression();
    }


    /**
     * chain ::= chainElem arrowOp chainElem ( arrowOp chainElem)*
     * @throws SyntaxException
     */
    void chain() throws SyntaxException {
        chainElem();
        arrowOp();
        chainElem();
        while(t.kind == ARROW || t.kind == BARARROW ) {
            arrowOp();
            chainElem();
        }
    }


    /**
     * whileStatement ::= KW_WHILE ( expression ) block
     * @throws SyntaxException
     */
    void whileProduction() throws SyntaxException {
        match(KW_WHILE);
        match(LPAREN);
        expression();
        match(RPAREN);
        block();
    }


    /**
     * ifStatement ::= KW_IF ( expression ) block
     * @throws SyntaxException
     */
    void ifProduction() throws SyntaxException {
        match(KW_IF);
        match(LPAREN);
        expression();
        match(RPAREN);
        block();
    }


    /**
     * arrowOp ∷= ARROW | BARARROW
     * @throws SyntaxException
     */
    void arrowOp() throws SyntaxException {
        if(t.kind == ARROW || t.kind == BARARROW) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'arrowOp'");
        }
    }


    /**
     * chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
     * @throws SyntaxException
     */
    void chainElem() throws SyntaxException {
        if(t.kind == IDENT) {
            match(IDENT);
        }
        else if(t.kind == OP_BLUR || t.kind == OP_GRAY || t.kind == OP_CONVOLVE) {
            filterOp();
            arg();
        }
        else if(t.kind == KW_SHOW || t.kind == KW_HIDE || t.kind == KW_MOVE || t.kind == KW_XLOC || t.kind == KW_YLOC) {
            frameOp();
            arg();
        }
        else if(t.kind == OP_WIDTH || t.kind == OP_HEIGHT || t.kind == KW_SCALE) {
            imageOp();
            arg();
        }
        else {
            throw new SyntaxException("Expected 'chainElem'");
        }
    }


    /**
     * filterOp ::= OP_BLUR | OP_GRAY | OP_CONVOLVE
     * @throws SyntaxException
     */
    void filterOp() throws SyntaxException {
        if(t.kind == OP_BLUR || t.kind == OP_GRAY || t.kind == OP_CONVOLVE) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'filterOp'");
        }
    }


    /**
     * frameOp ::= KW_SHOW | KW_HIDE | KW_MOVE | KW_XLOC | KW_YLOC
     * @throws SyntaxException
     */
    void frameOp() throws SyntaxException {
        if(t.kind == KW_SHOW || t.kind == KW_HIDE || t.kind == KW_MOVE || t.kind == KW_XLOC || t.kind == KW_YLOC) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'frameOp'");
        }
    }

    /**
     * imageOp ::= OP_WIDTH | OP_HEIGHT | KW_SCALE
     * @throws SyntaxException
     */
    void imageOp() throws SyntaxException {
        if(t.kind == OP_WIDTH || t.kind == OP_HEIGHT || t.kind == KW_SCALE) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'imageOp'");
        }
    }


    /**
     * arg ::= ε | ( expression ( , expression)* )
     * @throws SyntaxException
     */
    void arg() throws SyntaxException {
        if(t.kind == LPAREN) {
            match(LPAREN);
            expression();
            while(t.kind == COMMA) {
                match(COMMA);
                expression();
            }
            match(RPAREN);
        }
    }


    /**
     * expression ∷= term ( relOp term)*
     * @throws SyntaxException
     */
    void expression() throws SyntaxException {
        term();
        while(t.kind == LT || t.kind == LE || t.kind == GT || t.kind == GE || t.kind == EQUAL || t.kind == NOTEQUAL) {
            relOp();
            term();
        }
    }


    /**
     * term ∷= elem ( weakOp elem)*
     * @throws SyntaxException
     */
    void term() throws SyntaxException {
        elem();
        while(t.kind == PLUS || t.kind == MINUS || t.kind == OR) {
            weakOP();
            elem();
        }
    }


    /**
     * elem ∷= factor ( strongOp factor)*
     * @throws SyntaxException
     */
    void elem() throws SyntaxException {
        factor();
        while(t.kind == TIMES || t.kind == DIV || t.kind == AND || t.kind == MOD) {
            strongOp();
            factor();
        }
    }


    /**
     * factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE
     | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
     * @throws SyntaxException
     */
    void factor() throws SyntaxException {
        Kind kind = t.kind;
        switch (kind) {
            case IDENT: case INT_LIT: case KW_TRUE: case KW_FALSE: case KW_SCREENWIDTH: case KW_SCREENHEIGHT:
                consume();
                break;
            case LPAREN:
                consume();
                expression();
                match(RPAREN);
                break;
            default:
                //you will want to provide a more useful error message
                throw new SyntaxException("illegal factor");
        }
    }


    /**
     * relOp ∷= LT | LE | GT | GE | EQUAL | NOTEQUAL
     * @throws SyntaxException
     */
    void relOp() throws SyntaxException {
        if(t.kind == LT || t.kind == LE || t.kind == GT || t.kind == GE || t.kind == EQUAL || t.kind == NOTEQUAL) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'relOp'");
        }
    }


    /**
     * weakOp ∷= PLUS | MINUS | OR
     * @throws SyntaxException
     */
    void weakOP() throws SyntaxException {
        if(t.kind == PLUS || t.kind == MINUS || t.kind == OR) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'weakOp'");
        }
    }



    /**
     * strongOp ∷= TIMES | DIV | AND | MOD
     * @throws SyntaxException
     */
    void strongOp() throws SyntaxException {
        if(t.kind == TIMES || t.kind == DIV || t.kind == AND || t.kind == MOD) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'strongOp'");
        }
    }


    /**
     * Checks whether the current token is the EOF token. If not, a
     * SyntaxException is thrown.
     *
     * @return
     * @throws SyntaxException
     */
    private Token matchEOF() throws SyntaxException {
        if (t.isKind(EOF)) {
            return t;
        }
        throw new SyntaxException("expected EOF");
    }

    /**
     * Checks if the current token has the given kind. If so, the current token
     * is consumed and returned. If not, a SyntaxException is thrown.
     *
     * Precondition: kind != EOF
     *
     * @param kind
     * @return
     * @throws SyntaxException
     */
    private Token match(Kind kind) throws SyntaxException {
        if (t.isKind(kind)) {
            return consume();
        }
        throw new SyntaxException("saw " + t.kind + " expected " + kind + " at " + t.getLinePos());
    }

    /**
     * Checks if the current token has one of the given kinds. If so, the
     * current token is consumed and returned. If not, a SyntaxException is
     * thrown.
     *
     * * Precondition: for all given kinds, kind != EOF
     *
     * @param kinds
     *            list of kinds, matches any one
     * @return
     * @throws SyntaxException
     */
    private Token match(Kind... kinds) throws SyntaxException {
        // TODO. Optional but handy
        return null; //replace this statement
    }

    /**
     * Gets the next token and returns the consumed token.
     *
     * Precondition: t.kind != EOF
     *
     * @return
     *
     */
    private Token consume() throws SyntaxException {
        Token tmp = t;
        t = scanner.nextToken();
        return tmp;
    }

}

