package cop5556sp17;

/**
 * Created by saima_000 on 2/12/2017.
 */


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (3,5) ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        System.out.println(scanner);
        Parser parser = new Parser(scanner);
        parser.arg();
    }

    @Test
    public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (3,) ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.arg();
    }


    @Test
    public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
        String input = "prog0 {}";
        Parser parser = new Parser(new Scanner(input).scan());
        parser.parse();
    }

    @Test
    public void testProgram() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai integer manoj {}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai integer manoj, integer bandi, integer plp {}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }


    @Test
    public void testProgramNegative1() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testProgramNegative3() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai integer bandi integer{}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai url manoj {}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai file manoj {}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai integer manoj {}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai boolean manoj {}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai womt_work manoj {}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testParamDecNegative() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai url {}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai url manoj {sleep plp ; integer sai boolean bandi sleep plp ; image saibandi frame sai}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai url manoj {sleep plp ;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai url manoj {integer sai}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai url manoj {integer sai";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }


    @Test
    public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {integer sai}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai file manoj {boolean sai image sai frame sai}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai womt_work manoj {boolean sai image sai frame sai hello sai}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testStatement() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {sleep plp ;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while (sai) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {if (sai) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {sai -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {sai <- manoj;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {sai -> sai; sai <- manoj; if (sai) {} while (sai) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {sai <- manoj;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testChain() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {sai -> sai -> sai -> sai;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testWhile() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {while(sai) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testIf() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {if(sai) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testArrowOp() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {sai -> sai |-> bandi;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testChainElement() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {gray (sai) -> sai;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {move (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {width (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {sai -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testFilterOp() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {gray (sai) -> sai;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {blur (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {convolve (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {invalid (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testFrameOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {show (sai) -> sai;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {hide (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {move (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {xloc (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {yloc (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {yzloc (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testImageOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {width (sai) -> sai;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {height (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {scale (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {inv (sai) -> sai;}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        thrown.expect(SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testArg() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {width -> sai;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {while(sai < manoj) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai < manoj < bandi) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }


    @Test
    public void testTerm() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {while(sai + manoj) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai + manoj + bandi) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai + manoj + bandi < saibandi > plp) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testElem() throws SyntaxException, IllegalCharException, IllegalNumberException {
        String input = "sai {while(sai * manoj) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai * manoj / hello + bandi - sai > plp < uf) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }


    @Test
    public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {while(sai * sai) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai * manoj * 5 * true * false * screenwidth * screenheight * (sai)) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testRelOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {while(sai < sai) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai < sai <= sai > sai >= sai == sai != sai) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }


    @Test
    public void testWeakOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {while(sai + sai) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai - sai + sai | sai) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testStrongOp() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sai {while(sai * sai) {}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();

        input = "sai {while(sai * sai / sai & sai % sai) {}}";
        scanner = new Scanner(input);
        scanner.scan();
        parser = new Parser(scanner);
        parser.parse();
    }
}

