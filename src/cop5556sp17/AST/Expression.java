package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;

public abstract class Expression extends ASTNode {

	Type.TypeName typeName;

	public Type.TypeName getType() {
		return typeName;
	}

	public Type.TypeName getTypeName() {
		return typeName;
	}

	public void setTypeName(Type.TypeName typeName) {
		this.typeName = typeName;
	}

	protected Expression(Token firstToken) {
		super(firstToken);
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}
