package cop5556sp17;

/**
 * Created by saima_000 on 2/12/2017.
 */
import cop5556sp17.AST.*;
import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.Scanner.Token;

import java.util.ArrayList;

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
    Program parse() throws SyntaxException {
        Program program = program();
        matchEOF();
        return program;
    }


    /**
     * program ::=  IDENT block
     * program ::=  IDENT param_dec ( , param_dec )*   block
     * @throws SyntaxException
     */
    Program program() throws SyntaxException {
        Token firstToken = t;
        ArrayList<ParamDec> paramDecs = new ArrayList<ParamDec>();
        match(IDENT);
        Kind kind = t.kind;
        if(kind == KW_URL || kind == KW_FILE || kind == KW_INTEGER || kind == KW_BOOLEAN) {
            paramDecs.add(paramDec());
            while(t.kind == COMMA) {
                consume();
                paramDecs.add(paramDec());
            }
        }
        return new Program(firstToken, paramDecs, block());
    }


    /**
     * paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN ) IDENT
     * @throws SyntaxException
     */
    ParamDec paramDec() throws SyntaxException {
        Token firstToken = t;
        Kind kind = t.kind;
        if (kind == KW_URL || kind == KW_FILE || kind == KW_INTEGER || kind == KW_BOOLEAN) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'paramDec'");
        }
        Token identToken = t;
        match(IDENT);
        return new ParamDec(firstToken, identToken);
    }


    /**
     * block ::= { ( dec | statement) * }
     * @throws SyntaxException
     */
    Block block() throws SyntaxException {
        Token firstToken = t;
        ArrayList<Dec> decList = new ArrayList<Dec>();
        ArrayList<Statement> stmtList = new ArrayList<Statement>();
        match(LBRACE);
        while(!t.isKind(EOF) && !t.isKind(RBRACE)) {
            if(t.kind == KW_INTEGER || t.kind == KW_BOOLEAN || t.kind == KW_IMAGE || t.kind == KW_FRAME) {
                decList.add(dec());
            }
            else {
                stmtList.add(statement());
            }
        }
        match(RBRACE);
        return new Block(firstToken, decList, stmtList);
    }


    /**
     * dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
     * @throws SyntaxException
     */
    Dec dec() throws SyntaxException {
        Token firstToken = t;
        Kind kind = t.kind;
        if(kind == KW_INTEGER || kind == KW_BOOLEAN || kind == KW_IMAGE || kind == KW_FRAME) {
            consume();
        }
        else {
            throw new SyntaxException("Expected 'dec'");
        }
        Token identToken = t;
        match(IDENT);
        return new Dec(firstToken, identToken);
    }


    /**
     * statement ::= OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
     * @throws SyntaxException
     */
    Statement statement() throws SyntaxException {
        if(t.kind == OP_SLEEP) {
            Token firstToken = t;
            match(OP_SLEEP);
            Expression expr = expression();
            match(SEMI);
            return new SleepStatement(firstToken, expr);
        }
        else if(t.kind == KW_WHILE) {
            return whileProduction();
        }
        else if(t.kind == KW_IF) {
            return ifProduction();
        }
        else if(t.kind == IDENT && scanner.peek().kind == ASSIGN) {
            AssignmentStatement stmt = assign();
            match(SEMI);
            return stmt;
        }
        else {
            Chain chain = chain();
            match(SEMI);
            return chain;
        }
    }


    /**
     * assign ::= IDENT ASSIGN expression
     * @throws SyntaxException
     */
    AssignmentStatement assign() throws SyntaxException {
        Token ident = t;
        match(IDENT);
        match(ASSIGN);
        Expression expr = expression();
        return new AssignmentStatement(ident, new IdentLValue(ident), expr);
    }


    /**
     * chain ::= chainElem arrowOp chainElem ( arrowOp chainElem)*
     * @throws SyntaxException
     *
     * TODO - Find about the firstToken.
     * TODO - Find about the difference between chainElement/binaryChainElement.
     */
    Chain chain() throws SyntaxException {
        Token firstToken = t;
        Chain chain = chainElem();
        Token arrayOp = t;
        arrowOp();
        ChainElem chainElem = chainElem();
        BinaryChain binaryChain = new BinaryChain(firstToken, chain, arrayOp, chainElem);
        while(t.kind == ARROW || t.kind == BARARROW ) {
            arrayOp = t;
            arrowOp();
            chainElem = chainElem();
            binaryChain = new BinaryChain(firstToken, binaryChain, arrayOp, chainElem);
        }
        return binaryChain;
    }


    /**
     * whileStatement ::= KW_WHILE ( expression ) block
     * @throws SyntaxException
     */
    WhileStatement whileProduction() throws SyntaxException {
        Token firstToken = t;
        match(KW_WHILE);
        match(LPAREN);
        Expression expr = expression();
        match(RPAREN);
        Block block = block();
        return new WhileStatement(firstToken, expr, block);
    }


    /**
     * ifStatement ::= KW_IF ( expression ) block
     * @throws SyntaxException
     */
    IfStatement ifProduction() throws SyntaxException {
        Token firstToken = t;
        match(KW_IF);
        match(LPAREN);
        Expression expr = expression();
        match(RPAREN);
        Block block = block();
        return new IfStatement(firstToken, expr, block);
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
    ChainElem chainElem() throws SyntaxException {
        if(t.kind == IDENT) {
            Token firstToken = t;
            match(IDENT);
            return new IdentChain(firstToken);
        }
        else if(t.kind == OP_BLUR || t.kind == OP_GRAY || t.kind == OP_CONVOLVE) {
            Token firstToken = t;
            filterOp();
            Tuple tuple = arg();
            return new FilterOpChain(firstToken, tuple);
        }
        else if(t.kind == KW_SHOW || t.kind == KW_HIDE || t.kind == KW_MOVE || t.kind == KW_XLOC || t.kind == KW_YLOC) {
            Token firstToken = t;
            frameOp();
            Tuple tuple = arg();
            return new FrameOpChain(firstToken, tuple);
        }
        else if(t.kind == OP_WIDTH || t.kind == OP_HEIGHT || t.kind == KW_SCALE) {
            Token firstToken = t;
            imageOp();
            Tuple tuple = arg();
            return new ImageOpChain(firstToken, tuple);
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
    Tuple arg() throws SyntaxException {
        Token firstToken = t;
        ArrayList<Expression> list = new ArrayList<Expression>();
        if(t.kind == LPAREN) {
            match(LPAREN);
            list.add(expression());
            while(t.kind == COMMA) {
                match(COMMA);
                list.add(expression());
            }
            match(RPAREN);
        }
        return new Tuple(firstToken, list);
    }


    /**
     * expression ∷= term ( relOp term)*
     * @throws SyntaxException
     */
    Expression expression() throws SyntaxException {
        Token firstToken = t;
        Expression expr = term();
        while(t.kind == LT || t.kind == LE || t.kind == GT || t.kind == GE || t.kind == EQUAL || t.kind == NOTEQUAL) {
            Token op = t;
            relOp();
            expr = new BinaryExpression(firstToken, expr, op, term());
        }
        return expr;
    }


    /**
     * term ∷= elem ( weakOp elem)*
     * @throws SyntaxException
     */
    Expression term() throws SyntaxException {
        Token firstToken = t;
        Expression expr = elem();
        while(t.kind == PLUS || t.kind == MINUS || t.kind == OR) {
            Token op = t;
            weakOP();
            expr = new BinaryExpression(firstToken, expr, op, elem());
        }
        return expr;
    }


    /**
     * elem ∷= factor ( strongOp factor)*
     * @throws SyntaxException
     */
    Expression elem() throws SyntaxException {
        Token firstToken = t;
        Expression expr = factor();
        while(t.kind == TIMES || t.kind == DIV || t.kind == AND || t.kind == MOD) {
            Token op = t;
            strongOp();
            expr = new BinaryExpression(firstToken, expr, op, factor());
        }
        return expr;
    }


    /**
     * factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE
     | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
     * @throws SyntaxException
     */
    Expression factor() throws SyntaxException {
        Kind kind = t.kind;
        Token firstToken;
        switch (kind) {
            case IDENT:
                firstToken = t;
                consume();
                return new IdentExpression(firstToken);
            case INT_LIT:
                firstToken = t;
                consume();
                return new IntLitExpression(firstToken);
            case KW_TRUE: case KW_FALSE:
                firstToken = t;
                consume();
                return new BooleanLitExpression(firstToken);
            case KW_SCREENWIDTH: case KW_SCREENHEIGHT:
                firstToken = t;
                consume();
                return new ConstantExpression(firstToken);
            case LPAREN:
                consume();
                Expression expression = expression();
                match(RPAREN);
                return expression;
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

