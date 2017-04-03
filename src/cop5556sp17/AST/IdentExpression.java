package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public class IdentExpression extends Expression {
	Dec dec;

	public IdentExpression(Token firstToken) {
		super(firstToken);
	}

	public void setDec(Dec dec) {
		this.dec = dec;
	}

	public Dec getDec() {
		return this.dec;
	}

	@Override
	public String toString() {
		return "IdentExpression [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentExpression(this, arg);
	}

}
