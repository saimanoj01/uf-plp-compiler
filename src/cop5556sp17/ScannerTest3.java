package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ScannerTest3 {

    @Rule
    public ExpectedException thrown = ExpectedException.none();



    @Test
    public void testEmpty() throws IllegalCharException, IllegalNumberException {
        String input = "";
        Scanner scanner = new Scanner(input);
        scanner.scan();
    }

    @Test
    public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ";;;";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(SEMI, token.kind);
        assertEquals(0, token.pos);
        String text = SEMI.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(SEMI, token1.kind);
        assertEquals(1, token1.pos);
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(SEMI, token2.kind);
        assertEquals(2, token2.pos);
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token3.kind);
    }


    /**
     * This test illustrates how to check that the Scanner detects errors properly.
     * In this test, the input contains an int literal with a value that exceeds the range of an int.
     * The scanner should detect this and throw and IllegalNumberException.
     *
     * @throws IllegalCharException
     * @throws IllegalNumberException
     */
    @Test
    public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
        String input = "99999999999999999";
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalNumberException.class);
        scanner.scan();
    }

    //TODO  more tests
    @Test
    public void testEquals() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "=";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token.kind);
    }

    @Test
    public void testIdentInt() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "00";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(INT_LIT, token.kind);
        assertEquals(0, token.pos);
        String text = "0";
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(INT_LIT, token1.kind);
        assertEquals(1, token1.pos);
        assertEquals(text.length(), token1.length);
        assertEquals("0", token1.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token2.kind);
    }

    @Test
    public void testOperators() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "!|->|<--->";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(NOT, token.kind);
        assertEquals(0, token.pos);
        String text = NOT.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(BARARROW, token1.kind);
        assertEquals(1, token1.pos);
        text = BARARROW.getText();
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(OR, token2.kind);
        assertEquals(4, token2.pos);
        text = OR.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(OR.getText(), token2.getText());
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(ASSIGN, token3.kind);
        assertEquals(5, token3.pos);
        text = ASSIGN.getText();
        assertEquals(text.length(), token3.length);
        assertEquals(ASSIGN.getText(), token3.getText());
        Scanner.Token token4 = scanner.nextToken();
        assertEquals(MINUS, token4.kind);
        assertEquals(7, token4.pos);
        text = MINUS.getText();
        assertEquals(text.length(), token4.length);
        assertEquals(MINUS.getText(), token4.getText());
        Scanner.Token token5 = scanner.nextToken();
        assertEquals(ARROW, token5.kind);
        assertEquals(8, token5.pos);
        text = ARROW.getText();
        assertEquals(text.length(), token5.length);
        assertEquals(ARROW.getText(), token5.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }

    @Test
    public void testOperators1() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "!====";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(NOTEQUAL, token.kind);
        assertEquals(0, token.pos);
        String text = NOTEQUAL.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EQUAL, token1.kind);
        assertEquals(2, token1.pos);
        text = EQUAL.getText();
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }

    @Test
    public void testOperators2() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "!=====";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(NOTEQUAL, token.kind);
        assertEquals(0, token.pos);
        String text = NOTEQUAL.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EQUAL, token1.kind);
        assertEquals(2, token1.pos);
        text = EQUAL.getText();
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(EQUAL, token2.kind);
        assertEquals(4, token2.pos);
        text = EQUAL.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }

    @Test
    public void testOperators3() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<==";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(LE, token.kind);
        assertEquals(0, token.pos);
        String text = LE.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }
    @Test
    public void testOperators4() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<=====";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(LE, token.kind);
        assertEquals(0, token.pos);
        String text = LE.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EQUAL, token1.kind);
        assertEquals(2, token1.pos);
        text = EQUAL.getText();
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(EQUAL, token2.kind);
        assertEquals(4, token2.pos);
        text = EQUAL.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }
    @Test
    public void testOperators5() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ">===>==";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(GE, token.kind);
        assertEquals(0, token.pos);
        String text = GE.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EQUAL, token1.kind);
        assertEquals(2, token1.pos);
        text = EQUAL.getText();
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(GE, token2.kind);
        assertEquals(4, token2.pos);
        text = GE.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }

    @Test
    public void keywords() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "0sleepa+";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(INT_LIT, token.kind);
        assertEquals(0, token.pos);
        String text = "0";
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(IDENT, token1.kind);
        assertEquals(1, token1.pos);
        text = "sleepa";
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(PLUS, token2.kind);
        assertEquals(7, token2.pos);
        text = PLUS.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }
    @Test
    public void keywords1() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "0sleep+";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(INT_LIT, token.kind);
        assertEquals(0, token.pos);
        String text = "0";
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(OP_SLEEP, token1.kind);
        assertEquals(1, token1.pos);
        text = OP_SLEEP.getText();
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(PLUS, token2.kind);
        assertEquals(6, token2.pos);
        text = PLUS.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }

    @Test
    public void comments() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ab/*8****ll/";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(IDENT, token.kind);
        assertEquals(0, token.pos);
        String text = "ab";
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }

    @Test
    public void comments1() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ab/*****absagdyaus*/110";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(IDENT, token.kind);
        assertEquals(0, token.pos);
        String text = "ab";
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the first token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(INT_LIT, token1.kind);
        assertEquals(20, token1.pos);
        text = "110";
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }

    @Test
    public void lineTest() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "\n\n0ab\n;110";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(INT_LIT, token.kind);
        assertEquals(2, token.pos);
        String str = "LinePos [line=2, posInLine=0]";
        String expstr = token.getLinePos().toString();
        assertEquals(str,expstr);
        String text = "0";
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the first token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(IDENT, token1.kind);
        assertEquals(3, token1.pos);
        text = "ab";
        str = "LinePos [line=2, posInLine=1]";
        expstr = token1.getLinePos().toString();
        assertEquals(str,expstr);
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        //get the first token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(SEMI, token2.kind);
        assertEquals(6, token2.pos);
        text = SEMI.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //get the first token and check its kind, position, and contents
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(INT_LIT, token3.kind);
        assertEquals(7, token3.pos);
        str = "LinePos [line=3, posInLine=1]";
        expstr = token3.getLinePos().toString();
        assertEquals(str,expstr);
        text = "110";
        assertEquals(text.length(), token3.length);
        assertEquals(text, token3.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }
    @Test
    public void lineTest1() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "\n/*\n0ab\n*/;110";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(SEMI, token2.kind);
        assertEquals(10, token2.pos);
        String text = SEMI.getText();
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //get the first token and check its kind, position, and contents
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(INT_LIT, token3.kind);
        assertEquals(11, token3.pos);
        String str = "LinePos [line=3, posInLine=3]";
        String expstr = token3.getLinePos().toString();
        assertEquals(str,expstr);
        text = "110";
        assertEquals(text.length(), token3.length);
        assertEquals(text, token3.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token6 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF,token6.kind);
    }
}
