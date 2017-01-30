package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;

public class ScannerTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();



	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
	}

	@Test
	public void testOperators() throws IllegalCharException, IllegalNumberException {
		String input = "|&==!=<><=>=+-*/%!->|-><-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(OR, token.kind);
		token = scanner.nextToken();
		assertEquals(AND,token.kind);
		token = scanner.nextToken();
		assertEquals(EQUAL,token.kind);
		token = scanner.nextToken();
		assertEquals(NOTEQUAL,token.kind);
		token = scanner.nextToken();
		assertEquals(LT,token.kind);
		token = scanner.nextToken();
		assertEquals(GT,token.kind);
		token = scanner.nextToken();
		assertEquals(LE,token.kind);
		token = scanner.nextToken();
		assertEquals(GE,token.kind);
		token = scanner.nextToken();
		assertEquals(PLUS,token.kind);
		token = scanner.nextToken();
		assertEquals(MINUS,token.kind);
		token = scanner.nextToken();
		assertEquals(TIMES,token.kind);
		token = scanner.nextToken();
		assertEquals(DIV, token.kind);
		token = scanner.nextToken();
		assertEquals(MOD,token.kind);
		token = scanner.nextToken();
		assertEquals(NOT,token.kind);
		token = scanner.nextToken();
		assertEquals(ARROW,token.kind);
		token = scanner.nextToken();
		assertEquals(BARARROW,token.kind);
		token = scanner.nextToken();
		assertEquals(ASSIGN,token.kind);
		token = scanner.nextToken();
		assertEquals(EOF,token.kind);
	}

	@Test
	public void testAfterOr() throws IllegalCharException, IllegalNumberException {
		String input = "|";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(OR, token.kind);

		input = "|->";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(BARARROW, token.kind);

		input = "|*";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(OR, token.kind);
		token = scanner.nextToken();
		assertEquals(TIMES, token.kind);
	}


	@Test
	public void testAfterNotNegative() throws IllegalCharException, IllegalNumberException {
		String input = "|->=";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}

	@Test
	public void testAfterEqualNegative() throws IllegalCharException, IllegalNumberException {
		String input = "=";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		scanner.scan();
	}

	@Test
	public void testAfterEqual() throws IllegalCharException, IllegalNumberException {
		String input = "==";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(EQUAL, token.kind);

		input = "==*";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EQUAL, token.kind);
		token = scanner.nextToken();
		assertEquals(TIMES, token.kind);
	}

	@Test
	public void testAfterNot() throws IllegalCharException, IllegalNumberException {
		String input = "!";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(NOT, token.kind);

		input = "!=";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(NOTEQUAL, token.kind);

		input = "!!";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(NOT, token.kind);
		token = scanner.nextToken();
		assertEquals(NOT, token.kind);

		input = "!=!";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(NOTEQUAL, token.kind);
		token = scanner.nextToken();
		assertEquals(NOT, token.kind);
	}

	@Test
	public void testAfterGreaterThan() throws IllegalCharException, IllegalNumberException {
		String input = ">";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(GT, token.kind);

		input = ">=";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(GE, token.kind);

		input = ">>=";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(GT, token.kind);
		token = scanner.nextToken();
		assertEquals(GE, token.kind);
	}

	@Test
	public void testAfterLessThan() throws IllegalCharException, IllegalNumberException {
		String input = "<";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(LT, token.kind);

		input = "<=";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(LE, token.kind);

		input = "<-";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(ASSIGN, token.kind);


		input = "<-|";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(ASSIGN, token.kind);
		token = scanner.nextToken();
		assertEquals(OR, token.kind);
	}

	@Test
	public void testAfterMinus() throws IllegalCharException, IllegalNumberException {
		String input = "-";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(MINUS, token.kind);

		input = "->";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(ARROW, token.kind);

		input = "->--";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(ARROW, token.kind);
		token = scanner.nextToken();
		assertEquals(MINUS, token.kind);
		token = scanner.nextToken();
		assertEquals(MINUS, token.kind);
	}

	@Test
	public void testSeperator() throws IllegalCharException, IllegalNumberException {
		String input = ";,(}){";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		token = scanner.nextToken();
		assertEquals(COMMA, token.kind);
		token = scanner.nextToken();
		assertEquals(LPAREN, token.kind);
		token = scanner.nextToken();
		assertEquals(RBRACE, token.kind);
		token = scanner.nextToken();
		assertEquals(RPAREN, token.kind);
		token = scanner.nextToken();
		assertEquals(LBRACE, token.kind);
	}

	@Test
	public void testIntLit() throws IllegalCharException, IllegalNumberException {
		String input = "1203";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "00";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "01230";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);
	}

	@Test
	public void testIdentifier() throws IllegalCharException, IllegalNumberException {
		String input = "sai123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "123sai123";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "$123";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "_123";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "A123";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);
	}

	@Test
	public void testWhiteSpace() throws IllegalCharException, IllegalNumberException {
		String input = "integer x;\n" + "x <- 5 + 5;\n" + "/* Hello World */" + "return x";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(KW_INTEGER, token.kind);
		assertEquals(token.pos, 0);
		assertEquals(token.length, 7);

		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(token.pos, 8);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(token.pos, 9);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(token.pos, 11);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(ASSIGN, token.kind);
		assertEquals(token.pos, 13);
		assertEquals(token.length, 2);

		token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(token.pos, 16);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(PLUS, token.kind);
		assertEquals(token.pos, 18);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(INT_LIT, token.kind);
		assertEquals(token.pos, 20);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(token.pos, 21);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(token.pos, 40);
		assertEquals(token.length, 6);

		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		assertEquals(token.pos, 47);
		assertEquals(token.length, 1);

		token = scanner.nextToken();
		assertEquals(EOF, token.kind);
		assertEquals(token.pos, 48);
		assertEquals(token.length, 0);

	}

	@Test
	public void testKeywords() throws IllegalCharException, IllegalNumberException {
		String[] keywords = {"integer", "boolean", "image", "url", "file", "frame", "while", "if", "sleep", "screenheight", "screenwidth",
				"gray", "convolve", "blur", "scale", "width", "height", "xloc", "yloc", "hide", "show", "move", "true", "false"};
		for(String keyword : keywords) {
			String input = keyword;
			Scanner scanner = new Scanner(input);
			scanner.scan();
			Scanner.Token token = scanner.nextToken();
			assertEquals(Scanner.Kind.fromString(keyword), token.kind);
			token = scanner.nextToken();
			assertEquals(EOF, token.kind);
		}
	}

	@Test
	public void testAfterDivAndComments() throws IllegalCharException, IllegalNumberException {
		String input = "/";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(DIV, token.kind);
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "/*";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "/**/";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "/*saimanoj nadnjsndkbsaibf saknfsan";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "/abc";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(DIV, token.kind);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(IDENT, token.kind);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "/*abc*/";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "/*abc*";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);

		input = "/**";
		scanner = new Scanner(input);
		scanner.scan();
		token = scanner.nextToken();
		assertEquals(EOF, token.kind);
	}

	@Test
	public void testGetTextMethod() throws IllegalCharException, IllegalNumberException {
		String input = "integer 555";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(token.getText(), "integer");
		token = scanner.nextToken();
		assertEquals(token.getText(), "555");
		token = scanner.nextToken();
		assertEquals(token.getText(), "");
		thrown.expect(NumberFormatException.class);
		assertEquals(token.intVal(), 12345);
	}

	@Test
	public void testGetValueMethod() throws IllegalCharException, IllegalNumberException {
		String input = "integer 555";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token1 = scanner.nextToken();
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(token2.intVal(), 555);
		thrown.expect(NumberFormatException.class);
		assertEquals(token1.intVal(), 12345);
	}

	@Test
	public void testGetLinePosMethod() throws IllegalCharException, IllegalNumberException {
		String input = "abcd\n" + "efghij klmnop\n" + "sai manoj";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(token.getLinePos().line, 0);
		assertEquals(token.getLinePos().posInLine, 0);

		token = scanner.nextToken();
		assertEquals(token.getLinePos().line, 1);
		assertEquals(token.getLinePos().posInLine, 0);

		token = scanner.nextToken();
		assertEquals(token.getLinePos().line, 1);
		assertEquals(token.getLinePos().posInLine, 7);

		token = scanner.nextToken();
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 0);

		token = scanner.nextToken();
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 4);

		token = scanner.nextToken();
		assertEquals(token.getLinePos().line, 2);
		assertEquals(token.getLinePos().posInLine, 9);
	}

	@Test
	public void testGetLinePosMethod2() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Scanner.Token token = scanner.nextToken();
		assertEquals(token.getLinePos().line, 0);
		assertEquals(token.getLinePos().posInLine, 0);
	}

	@Test
	public void testIllegalCharacter() throws IllegalCharException, IllegalNumberException {
		String input = "sai#manoj";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
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

}
