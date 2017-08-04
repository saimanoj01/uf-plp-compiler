package cop5556sp17;

import cop5556sp17.AST.*;
import cop5556sp17.AST.Type.TypeName;

import java.util.ArrayList;
import java.util.List;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import cop5556sp17.SymbolTable;

import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		Chain chain = binaryChain.getE0();
		chain.visit(this, null);
		ChainElem chainElem = binaryChain.getE1();
		chainElem.visit(this, null);
		Token op = binaryChain.getArrow();
		if(chain.getTypeName().isType(URL) && chainElem.getTypeName().isType(IMAGE) && op.isKind(ARROW)) {
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain.getTypeName().isType(FILE) && chainElem.getTypeName().isType(IMAGE) && op.isKind(ARROW)) {
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain.getTypeName().isType(FRAME) && (op.isKind(ARROW)) && (chainElem instanceof FrameOpChain) && (binaryChain.getE1().getFirstToken().getText().contentEquals(KW_XLOC.getText()) || binaryChain.getE1().getFirstToken().getText().contentEquals(KW_YLOC.getText()))) {
			binaryChain.setTypeName(INTEGER);
		}
		else if(chain.getTypeName().isType(FRAME) && (op.isKind(ARROW)) && (chainElem instanceof FrameOpChain) && (binaryChain.getE1().getFirstToken().getText().contentEquals(KW_SHOW.getText()) || binaryChain.getE1().getFirstToken().getText().contentEquals(KW_MOVE.getText()) || binaryChain.getE1().getFirstToken().getText().contentEquals(KW_HIDE.getText()))) {
			binaryChain.setTypeName(FRAME);
		}
		else if(chain.getTypeName().isType(IMAGE) && (op.isKind(ARROW)) && (chainElem instanceof ImageOpChain) && (binaryChain.getE1().getFirstToken().getText().contentEquals(OP_WIDTH.getText()) || binaryChain.getE1().getFirstToken().getText().contentEquals(OP_HEIGHT.getText()))) {
			binaryChain.setTypeName(INTEGER);
		}
		else if(chain.getTypeName().isType(IMAGE) && (op.isKind(ARROW)) && chainElem.getTypeName().isType(FRAME)) {
			binaryChain.setTypeName(FRAME);
		}
		else if(chain.getTypeName().isType(IMAGE) && (op.isKind(ARROW)) && chainElem.getTypeName().isType(FILE)) {
			binaryChain.setTypeName(NONE);
		}
		else if(chain.getTypeName().isType(IMAGE) && (op.isKind(ARROW) || op.isKind(BARARROW)) && (chainElem instanceof FilterOpChain) && (binaryChain.getE1().getFirstToken().getText().contentEquals(OP_GRAY.getText()) || binaryChain.getE1().getFirstToken().getText().contentEquals(OP_BLUR.getText()) || binaryChain.getE1().getFirstToken().getText().contentEquals(OP_CONVOLVE.getText()))) {
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain.getTypeName().isType(IMAGE) && (op.isKind(ARROW)) && (chainElem instanceof ImageOpChain) && (binaryChain.getE1().getFirstToken().getText().contentEquals(KW_SCALE.getText()))) {
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain.getTypeName().isType(IMAGE) && (op.isKind(ARROW)) && (chainElem instanceof IdentChain) && (chainElem.getTypeName().isType(IMAGE))) {
			binaryChain.setTypeName(IMAGE);
		}
		else if(chain.getTypeName().isType(INTEGER) && (op.isKind(ARROW)) && (chainElem instanceof IdentChain) && (chainElem.getTypeName().isType(INTEGER))) {
			binaryChain.setTypeName(INTEGER);
		}
		else {
			throw new TypeCheckException("Invalid type - BinaryChain");
		}
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		Expression expression0 = binaryExpression.getE0();
		expression0.visit(this, null);
		Expression expression1 = binaryExpression.getE1();
		expression1.visit(this, null);
		Token op = binaryExpression.getOp();

		if(expression0.getTypeName() == TypeName.INTEGER && expression1.getTypeName() == TypeName.INTEGER && (op.isKind(PLUS) || op.isKind(MINUS))) {
			binaryExpression.setTypeName(TypeName.INTEGER);
		}
		else if(expression0.getTypeName() == TypeName.IMAGE && expression1.getTypeName() == TypeName.IMAGE && (op.isKind(PLUS) || op.isKind(MINUS))) {
			binaryExpression.setTypeName(TypeName.IMAGE);
		}
		else if(expression0.getTypeName() == TypeName.INTEGER && expression1.getTypeName() == TypeName.INTEGER && (op.isKind(TIMES) || op.isKind(DIV) || op.isKind(MOD))) {
			binaryExpression.setTypeName(TypeName.INTEGER);
		}
		else if(expression0.getTypeName() == TypeName.INTEGER && expression1.getTypeName() == TypeName.IMAGE && (TIMES.getText().contentEquals(op.getText()))) {
			binaryExpression.setTypeName(TypeName.IMAGE);
		}
		else if(expression0.getTypeName() == TypeName.IMAGE && expression1.getTypeName() == TypeName.INTEGER && (op.isKind(TIMES))) {
			binaryExpression.setTypeName(TypeName.IMAGE);
		}
		else if(expression0.getTypeName() == TypeName.IMAGE && expression1.getTypeName() == TypeName.INTEGER && (op.isKind(Kind.DIV) || op.isKind(Kind.MOD))) {
			binaryExpression.setTypeName(TypeName.IMAGE);
		}
		else if(expression0.getTypeName() == TypeName.INTEGER && expression1.getTypeName() == TypeName.INTEGER && (LE.getText().contentEquals(op.getText()) || LT.getText().contentEquals(op.getText()) || GE.getText().contentEquals(op.getText()) || GT.getText().contentEquals(op.getText()))) {
			binaryExpression.setTypeName(TypeName.BOOLEAN);
		}
		else if(expression0.getTypeName() == TypeName.BOOLEAN && expression1.getTypeName() == TypeName.BOOLEAN && (op.isKind(AND) || op.isKind(OR))) {
			binaryExpression.setTypeName(TypeName.BOOLEAN);
		}
		else if(expression0.getTypeName() == TypeName.BOOLEAN && expression1.getTypeName() == TypeName.BOOLEAN && (LE.getText().contentEquals(op.getText()) || LT.getText().contentEquals(op.getText()) || GE.getText().contentEquals(op.getText()) || GT.getText().contentEquals(op.getText()))) {
			binaryExpression.setTypeName(TypeName.BOOLEAN);
		}
		else if((op.isKind(EQUAL) || op.isKind(NOTEQUAL)) && expression0.getTypeName() == expression1.getTypeName()) {
			binaryExpression.setTypeName(TypeName.BOOLEAN);
		}
		else {
			throw new TypeCheckException("Invalid type assignment to BinaryExpression");
		}
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		symtab.enterScope();
		List<Dec> decs = block.getDecs();
		List<Statement> statements = block.getStatements();
		for(Dec dec : decs) {
			dec.visit(this, null);
		}
		for(Statement statement : statements) {
			statement.visit(this, null);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		booleanLitExpression.setTypeName(TypeName.BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		Tuple tuple = filterOpChain.getArg();
		tuple.visit(this, null);
		List<Expression> expressionList = tuple.getExprList();
		if(expressionList.size() != 0) {
			throw new TypeCheckException("Arg length is expected to be 0 but it is " + expressionList.size());
		}
		filterOpChain.setTypeName(IMAGE);
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		if(frameOpChain.getFirstToken().isKind(KW_SHOW) || frameOpChain.getFirstToken().isKind(KW_HIDE)) {
			Tuple tuple = frameOpChain.getArg();
			tuple.visit(this, null);
			List<Expression> expressionList = tuple.getExprList();
			if(expressionList.size() != 0) {
				throw new TypeCheckException("Arg length is expected to be 0 but it is " + expressionList.size());
			}
			frameOpChain.setTypeName(NONE);
		}
		else if(frameOpChain.getFirstToken().isKind(KW_XLOC) || frameOpChain.getFirstToken().isKind(KW_YLOC)) {
			Tuple tuple = frameOpChain.getArg();
			tuple.visit(this, null);
			List<Expression> expressionList = tuple.getExprList();
			if(expressionList.size() != 0) {
				throw new TypeCheckException("Arg length is expected to be 0 but it is " + expressionList.size());
			}
			frameOpChain.setTypeName(INTEGER);
		}
		else if(frameOpChain.getFirstToken().isKind(KW_MOVE)) {
			Tuple tuple = frameOpChain.getArg();
			tuple.visit(this, null);
			List<Expression> expressionList = tuple.getExprList();
			if(expressionList.size() != 2) {
				throw new TypeCheckException("Arg length is expected to be 2 but it is " + expressionList.size());
			}
			frameOpChain.setTypeName(NONE);
		}
		else {
			throw new TypeCheckException("Invalid FrameOp!");
		}
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Dec dec = symtab.lookup(identChain.getFirstToken().getText());
		if(dec != null) {
			identChain.setTypeName(dec.getTypeName());
			identChain.setDec(dec);
		}
		else {
			throw new TypeCheckException("Identifier " + identChain.getFirstToken().getText() + " not declared or out of scope");
		}
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		Dec dec = symtab.lookup(identExpression.getFirstToken().getText());
		if(dec != null) {
			identExpression.setTypeName(dec.getTypeName());
			identExpression.setDec(dec);
		}
		else {
			throw new TypeCheckException("Indentifier " + identExpression.getFirstToken().getText() + " not declared or is not in scope");
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		Expression expression = ifStatement.getE();
		expression.visit(this, null);
		if(expression.getTypeName() != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected " + TypeName.INTEGER + " but got " + expression.getTypeName());
		}
		ifStatement.getB().visit(this, null);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		intLitExpression.setTypeName(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		Expression expression = sleepStatement.getE();
		expression.visit(this, null);
		if(expression.getTypeName() != TypeName.INTEGER) {
			throw new TypeCheckException("Expected " + TypeName.INTEGER + " but got " + expression.getTypeName());
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		Expression expression = whileStatement.getE();
		expression.visit(this, null);
		if(expression.getTypeName() != TypeName.BOOLEAN) {
			throw new TypeCheckException("Expected " + TypeName.BOOLEAN + " but got " + expression.getTypeName());
		}
		whileStatement.getB().visit(this, null);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		declaration.setTypeName(Type.getTypeName(declaration.getType()));
		if(!symtab.insert(declaration.getIdent().getText(), declaration)) {
			throw new TypeCheckException("Identifier " + declaration.getIdent().getText() + " already in scope");
		}
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		List<ParamDec> list = program.getParams();
		for(ParamDec paramDec : list) {
			paramDec.visit(this, arg);
		}
		program.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		IdentLValue identLValue = assignStatement.getVar();
		identLValue.visit(this, null);
		TypeName identLValueTypeName = identLValue.getDec().getTypeName();
		Expression expression = assignStatement.getE();
		expression.visit(this, null);
		if(identLValueTypeName != expression.getTypeName()) {
			throw new TypeCheckException("Invalid assignment\nExpected " + identLValueTypeName + " but got " + expression.getTypeName());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		Dec dec = symtab.lookup(identX.getFirstToken().getText());
		if(dec != null) {
			identX.setDec(dec);
		}
		else {
			throw new TypeCheckException("Symbol " + identX.getFirstToken().getText() + " not declared or not in scope");
		}
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		paramDec.setTypeName(Type.getTypeName(paramDec.getType()));
		if(!symtab.insert(paramDec.getIdent().getText(), paramDec)) {
			throw new TypeCheckException("Identifier " + paramDec.getIdent().getText() + " already in scope");
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		constantExpression.setTypeName(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		if(imageOpChain.getFirstToken().getText().contentEquals(OP_WIDTH.getText()) || imageOpChain.getFirstToken().getText().contentEquals(OP_HEIGHT.getText())) {
			Tuple tuple = imageOpChain.getArg();
			tuple.visit(this, null);
			List<Expression> expressionList = tuple.getExprList();
			if(expressionList.size() != 0) {
				throw new TypeCheckException("Arg length is expected to be 0 but it is " + expressionList.size());
			}
			imageOpChain.setTypeName(INTEGER);
		}
		else if(imageOpChain.getFirstToken().getText().contentEquals(KW_SCALE.getText())) {
			Tuple tuple = imageOpChain.getArg();
			tuple.visit(this, null);
			List<Expression> expressionList = tuple.getExprList();
			if(expressionList.size() != 1) {
				throw new TypeCheckException("Arg length is expected to be 0 but it is " + expressionList.size());
			}
			imageOpChain.setTypeName(IMAGE);
		}
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		List<Expression> expressionList = tuple.getExprList();
		for(Expression expression : expressionList) {
			expression.visit(this, null);
			if(expression.getTypeName() != TypeName.INTEGER) {
				throw new TypeCheckException("Expected type " + TypeName.INTEGER + "but got " + expression.getTypeName());
			}
		}
		return null;
	}


}
