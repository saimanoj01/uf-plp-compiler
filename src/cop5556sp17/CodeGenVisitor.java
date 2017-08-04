package cop5556sp17;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import cop5556sp17.AST.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.AST.Type.TypeName.INTEGER;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction
    FieldVisitor fv;
    int index, slotCount = 0;

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
        index = 0;
		for (ParamDec dec : params)
			dec.visit(this, mv);
        index = 0;
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
        //TODO  visit the local variables
        for(Dec dec : program.getB().getDecs()) {
            if(dec.getTypeName() == TypeName.INTEGER) {
                mv.visitLocalVariable(dec.getIdent().getText(), "I", null, startRun, endRun, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.BOOLEAN) {
                mv.visitLocalVariable(dec.getIdent().getText(), "Z", null, startRun, endRun, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.FRAME) {
                mv.visitLocalVariable(dec.getIdent().getText(), PLPRuntimeFrame.JVMDesc, null, startRun, endRun, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.IMAGE) {
                mv.visitLocalVariable(dec.getIdent().getText(), "Ljava/awt/image/BufferedImage;", null, startRun, endRun, dec.getSlotNo());
            }
        }
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}

    @Override
    public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
        //For assignment 5, only needs to handle integers and booleans
        if(paramDec.getTypeName() == TypeName.INTEGER) {
            fv = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), "I", null, new Integer(0));
        }
        else if(paramDec.getTypeName() == TypeName.BOOLEAN) {
            fv = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), "Z", null, new Boolean(false));
        }
        else if(paramDec.getTypeName() == FILE) {
            fv = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), "Ljava/io/File;", null, null);
        }
        else if(paramDec.getTypeName() == URL) {
            fv = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), "Ljava/net/URL;", null, null);
        }

        fv.visitEnd();

        if(paramDec.getTypeName() == TypeName.INTEGER) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, index++);
            mv.visitInsn(AALOAD);

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
            mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");
        }
        else if(paramDec.getTypeName() == TypeName.BOOLEAN) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, index++);
            mv.visitInsn(AALOAD);

            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
            mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
        }
        else if(paramDec.getTypeName() == TypeName.FILE) {
            mv.visitVarInsn(ALOAD, 0);

            mv.visitTypeInsn(NEW, "java/io/File");
            mv.visitInsn(DUP);

            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, index++);
            mv.visitInsn(AALOAD);

            mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Ljava/io/File;");
        }
        else if(paramDec.getTypeName() == TypeName.URL) {
//            mv.visitTypeInsn(NEW, "java/net/URL");
//            mv.visitInsn(DUP);

//            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitIntInsn(SIPUSH, index++);
//            mv.visitInsn(AALOAD);

//            mv.visitMethodInsn(INVOKESPECIAL, "java/net/URL", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
            mv.visitVarInsn(ALOAD, 0);

            mv.visitInsn(SWAP);
            mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Ljava/net/URL;");
        }
        return null;
    }


    @Override
    public Object visitDec(Dec declaration, Object arg) throws Exception {
        declaration.setSlotNo(++slotCount);
        if(declaration.getTypeName().isType(IMAGE)) {
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, declaration.getSlotNo());
        }
        else if(declaration.getTypeName().isType(FRAME)) {
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, declaration.getSlotNo());
        }
        else {
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, declaration.getSlotNo());
        }
        return null;
    }

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType());
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		Chain chain = binaryChain.getE0();
        ChainElem chainElem = binaryChain.getE1();
        chain.visit(this, "left");
        if(chain.getTypeName().isType(URL)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
        }
        else if(chain.getTypeName().isType(FILE)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
        }
        else if(chain.getTypeName().isType(NONE)) {
            mv.visitInsn(POP);
        }
        if(chainElem instanceof FilterOpChain) {
            if(binaryChain.getArrow().isKind(ARROW)) {
                mv.visitInsn(ACONST_NULL);
            }
            else {
                mv.visitInsn(DUP);
            }
        }
        chainElem.visit(this, "right");
		return null;
	}

    @Override
    public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
        Token filterToken = filterOpChain.getFirstToken();
        filterOpChain.getArg().visit(this, null);
        String op = (String) arg;
        if(filterToken.isKind(OP_BLUR)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);
        }
        else if(filterToken.isKind(OP_GRAY)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
        }
        else if(filterToken.isKind(OP_CONVOLVE)){
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig, false);
        }
        return null;
    }

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
        Label next = new Label();
        Expression e0 = binaryExpression.getE0();
        e0.visit(this, arg);
        Expression e1 = binaryExpression.getE1();
        e1.visit(this, arg);
        switch(binaryExpression.getOp().kind) {
            case TIMES:
                if(e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(IMAGE)) {
                    mv.visitInsn(SWAP);
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
                }
                else if(e0.getType().isType(IMAGE) && e1.getType().isType(TypeName.INTEGER)) {
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
                }
                else {
                    mv.visitInsn(IMUL);
                }
                break;
            case DIV:
                if(e0.getType().isType(IMAGE) && e1.getType().isType(TypeName.INTEGER)) {
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
                }
                else {
                    mv.visitInsn(IDIV);
                }
                break;
            case AND:
                mv.visitInsn(IAND);
                break;
            case MOD:
                if(e0.getType().isType(IMAGE) && e1.getType().isType(TypeName.INTEGER)) {
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
                }
                else {
                    mv.visitInsn(IREM);
                }
                break;
            case PLUS:
                if(e0.getType().isType(IMAGE) && e1.getType().isType(IMAGE)) {
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
                }
                else {
                    mv.visitInsn(IADD);
                }
                break;
            case MINUS:
                if(e0.getType().isType(IMAGE) && e1.getType().isType(IMAGE)) {
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
                }
                else {
                    mv.visitInsn(ISUB);
                }
                break;
            case OR:
                mv.visitInsn(IOR);
                break;
            case LT: {
                Label l1 = new Label();
                mv.visitJumpInsn(IF_ICMPGE, l1);
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, next);
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, next);
                break;
            }
            case LE: {
                Label l1 = new Label();
                mv.visitJumpInsn(IF_ICMPGT, l1);
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, next);
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, next);
                break;
            }
            case GT: {
                Label l1 = new Label();
                mv.visitJumpInsn(IF_ICMPLE, l1);
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, next);
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, next);
                break;
            }
            case GE: {
                Label l1 = new Label();
                mv.visitJumpInsn(IF_ICMPLT, l1);
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, next);
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, next);
                break;
            }
            case EQUAL: {
                Label l1 = new Label();
                mv.visitJumpInsn(IF_ICMPNE, l1);
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, next);
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, next);
                break;
            }
            case NOTEQUAL: {
                Label l1 = new Label();
                mv.visitJumpInsn(IF_ICMPEQ, l1);
                mv.visitInsn(ICONST_1);
                mv.visitJumpInsn(GOTO, next);
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0);
                mv.visitJumpInsn(GOTO, next);
                break;
            }
        }
        mv.visitLabel(next);
        return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
        List<Dec> decs = block.getDecs();
        List<Statement> statements = block.getStatements();
        for(Dec dec : decs) {
            dec.visit(this, null);
        }
        for(Statement statement : statements) {
            statement.visit(this, null);
            if(statement instanceof BinaryChain)
                mv.visitInsn(POP);
        }
		return null;
	}


	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
        if(booleanLitExpression.getFirstToken().getText().contentEquals("true")) {
            mv.visitInsn(ICONST_1);
        }
        else {
            mv.visitInsn(ICONST_0);
        }
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
        if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENWIDTH)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
        }
        else if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENHEIGHT)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
        }
		return null;
	}


	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		Token frameToken = frameOpChain.getFirstToken();
        frameOpChain.getArg().visit(this, null);
        if(frameToken.isKind(KW_SHOW)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc, false);
        }
        else if(frameToken.isKind(KW_HIDE)){
            mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
        }
        else if(frameToken.isKind(KW_MOVE)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
        }
        else if(frameToken.isKind(KW_XLOC)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
        }
        else if(frameToken.isKind(KW_YLOC)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
        }
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		String side = (String)arg;
        Dec dec = identChain.getDec();
        if(side.contentEquals("left")) {
            if(dec instanceof ParamDec) {
                mv.visitVarInsn(ALOAD, 0);
                if(dec.getTypeName() == TypeName.INTEGER)
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "I");
                else if(dec.getTypeName() == TypeName.BOOLEAN)
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Z");
                else if(dec.getTypeName() == TypeName.FILE) {
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Ljava/io/File;");
                }
                else if(dec.getTypeName() == TypeName.URL) {
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Ljava/net/URL;");
                }
            }
            else {
                if(dec.getTypeName() == TypeName.INTEGER || dec.getTypeName() == TypeName.BOOLEAN)
                    mv.visitVarInsn(ILOAD, dec.getSlotNo());
                else
                    mv.visitVarInsn(ALOAD, dec.getSlotNo());
            }
        }
        else if(side.contentEquals("right")) {
            if(dec instanceof ParamDec) {
                if(dec.getTypeName().isType(TypeName.INTEGER)) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitInsn(SWAP);
                    mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "I");
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "I");
                }
                else if(dec.getTypeName().isType(TypeName.FILE)) {
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Ljava/io/File;");
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write", PLPRuntimeImageIO.writeImageDesc, false);
                    mv.visitInsn(POP);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Ljava/io/File;");
                }
            }
            else {
                if(dec.getTypeName().isType(TypeName.INTEGER)) {
                    mv.visitVarInsn(ISTORE, dec.getSlotNo());
                    mv.visitVarInsn(ILOAD, dec.getSlotNo());
                }
                else if(dec.getTypeName().isType(TypeName.IMAGE)) {
                    mv.visitVarInsn(ASTORE, dec.getSlotNo());
                    mv.visitVarInsn(ALOAD, dec.getSlotNo());
                }
                else if(dec.getTypeName().isType(TypeName.FRAME)) {
                    mv.visitVarInsn(ALOAD, dec.getSlotNo());
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
                    mv.visitVarInsn(ASTORE, dec.getSlotNo());
                    mv.visitVarInsn(ALOAD, dec.getSlotNo());
                }
            }
        }
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
        Dec dec = identExpression.getDec();
        if(dec instanceof ParamDec) {
            mv.visitVarInsn(ALOAD, 0);
            if(dec.getTypeName() == TypeName.INTEGER)
                mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "I");
            else if(dec.getTypeName() == TypeName.BOOLEAN)
                mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Z");
            else if(dec.getTypeName() == TypeName.FILE) {
                mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Ljava/io/File;");
            }
            else if(dec.getTypeName() == TypeName.URL) {
                mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Ljava/net/URL;");
            }
        }
        else {
            if(dec.getTypeName() == TypeName.INTEGER || dec.getTypeName() == TypeName.BOOLEAN)
                mv.visitVarInsn(ILOAD, dec.getSlotNo());
            else
                mv.visitVarInsn(ALOAD, dec.getSlotNo());
        }
        return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
	    Dec dec = identX.getDec();
        if(dec instanceof ParamDec) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(SWAP);
            if(dec.getTypeName().isType(TypeName.INTEGER))
                mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "I");
            else if(dec.getTypeName().isType(TypeName.BOOLEAN))
                mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "Z");
            else if(dec.getTypeName().isType(TypeName.FILE)) {
                mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), TypeName.FILE.toString());
            }
            else if(dec.getTypeName().isType(TypeName.URL)) {
                mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), TypeName.URL.toString());
            }
        }
        else {
            if(dec.getTypeName().isType(TypeName.IMAGE)) {
                mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
                mv.visitVarInsn(ASTORE, dec.getSlotNo());
            }
            else if(dec.getTypeName().isType(TypeName.FRAME)) {
                mv.visitVarInsn(ASTORE, dec.getSlotNo());
            }
            else {
                mv.visitVarInsn(ISTORE, dec.getSlotNo());
            }
        }
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
        Label start = new Label();
        Label end = new Label();
        mv.visitLabel(start);
        Expression ex = ifStatement.getE();
        Block bl = ifStatement.getB();
        ex.visit(this, null);
        mv.visitJumpInsn(IFEQ, end);
        bl.visit(this, arg);
        mv.visitLabel(end);

        for(Dec dec : ifStatement.getB().getDecs()) {
            if(dec.getTypeName() == TypeName.INTEGER) {
                mv.visitLocalVariable(dec.getIdent().getText(), "I", null, start, end, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.BOOLEAN) {
                mv.visitLocalVariable(dec.getIdent().getText(), "Z", null, start, end, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.FRAME) {
                mv.visitLocalVariable(dec.getIdent().getText(), PLPRuntimeFrame.JVMDesc, null, start, end, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.IMAGE) {
                mv.visitLocalVariable(dec.getIdent().getText(), "Ljava/awt/image/BufferedImage;", null, start, end, dec.getSlotNo());
            }
        }

        return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		Token imageToken = imageOpChain.getFirstToken();
        imageOpChain.getArg().visit(this, null);
        if(imageToken.isKind(OP_WIDTH)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getWidth", PLPRuntimeImageOps.getWidthSig, false);
        }
        else if(imageToken.isKind(OP_HEIGHT)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage", "getHeight", PLPRuntimeImageOps.getHeightSig, false);
        }
        else if(imageToken.isKind(KW_SCALE)) {
            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
        }
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
        mv.visitIntInsn(SIPUSH, intLitExpression.getFirstToken().intVal());
		return null;
	}


	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
	    sleepStatement.getE().visit(this, arg);
        mv.visitInsn(I2L);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
	    for(Expression expression : tuple.getExprList()) {
	        expression.visit(this, arg);
        }
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
        Label start = new Label();
        Label end = new Label();
        mv.visitLabel(start);
        Expression ex = whileStatement.getE();
        Block bl = whileStatement.getB();
        ex.visit(this, null);
        mv.visitJumpInsn(IFEQ, end);
        bl.visit(this, arg);
        mv.visitJumpInsn(GOTO, start);
        mv.visitLabel(end);

        for(Dec dec : whileStatement.getB().getDecs()) {
            if(dec.getTypeName() == TypeName.INTEGER) {
                mv.visitLocalVariable(dec.getIdent().getText(), "I", null, start, end, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.BOOLEAN) {
                mv.visitLocalVariable(dec.getIdent().getText(), "Z", null, start, end, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.FRAME) {
                mv.visitLocalVariable(dec.getIdent().getText(), PLPRuntimeFrame.JVMDesc, null, start, end, dec.getSlotNo());
            }
            else if(dec.getTypeName() == TypeName.IMAGE) {
                mv.visitLocalVariable(dec.getIdent().getText(), "Ljava/awt/image/BufferedImage;", null, start, end, dec.getSlotNo());
            }
        }
		return null;
	}

}
