package uk.ac.ic.doc.wacc.ast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static uk.ac.ic.doc.wacc.ast.ASTNodes.*;

public class ASTPrinter extends ASTBaseVisitor<Void> {

    int tabs = 0;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public void printTabs() {
        for (int i = 0; i < tabs; i++) {
            System.out.print("\t");
        }
    }

    public String getBaos() {
        return baos.toString();
    }

    @Override
    public Void visitProgNode(ProgNode node) {
        PrintStream ps = new PrintStream(baos);
        //Save the old System.out
        PrintStream old = System.out;
        // Tell Java to use your special stream
        System.setOut(ps);

        printTabs();
        System.out.println("Prog");
        tabs++;
        for (FunctionNode functionNode : node.getFunctionNodes()) {
            functionNode.visit(this);
        }
        printTabs();
        System.out.println("Body");
        node.getBody().visit(this);

        System.out.flush();
        System.setOut(old);

        System.out.println(getBaos());

        return null;
    }

    @Override
    public Void visitFunctionNode(FunctionNode node) {
        printTabs();
        System.out.println("Function");
        tabs++;
        node.getType().visit(this);
        node.getIdentifierNode().visit(this);
        node.getParamList().visit(this);
        node.getBodyNode().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitParamNode(ParamNode node) {
        printTabs();
        System.out.println("Param");
        tabs++;
        node.getType().visit(this);
        node.getIdentifierNode().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitParamList(ParamList node) {
        printTabs();
        System.out.println("Param List");
        tabs++;
        ParamNode[] paramls = node.getParamList();
        for (ParamNode paraml : paramls) {
            paraml.visit(this);
        }
        tabs--;
        return null;
    }

    @Override
    public Void visitArgList(ArgList node) {
        printTabs();
        System.out.println("Argument List");
        ExpressionNode[] expnodes = node.getExpressionNodes();
        tabs++;
        for (ExpressionNode expnode : expnodes) {
            expnode.visit(this);
        }
        tabs--;
        return null;
    }

    @Override
    public Void visitBeginNode(BeginNode node) {
        printTabs();
        System.out.println("Begin");
        tabs++;
        node.getBody().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitCompoundNode(CompoundNode node) {
        printTabs();
        System.out.println("Compound Statement");
        tabs++;
        node.getLeftBody().visit(this);
        node.getRightBody().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitWhileNode(WhileNode node) {
        printTabs();
        System.out.println("while");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        printTabs();
        System.out.println("do");
        tabs++;
        node.getBody().visit(this);
        tabs--;
        printTabs();
        System.out.println("done");
        return null;
    }

    @Override
    public Void visitExitNode(ExitNode node) {
        printTabs();
        System.out.println("exit");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitSkipNode(SkipNode node) {
        printTabs();
        System.out.println("Skip");
        return null;
    }

    @Override
    public Void visitIfStatement(IfStatement node) {
        printTabs();
        System.out.println("If");
        tabs++;
        node.getCondition().visit(this);
        tabs--;

        printTabs();
        System.out.println("then");
        tabs++;
        node.getTrueBody().visit(this);
        tabs--;

        printTabs();
        System.out.println("else");
        tabs++;
        node.getFalseBody().visit(this);
        tabs--;

        return null;
    }

    @Override
    public Void visitAssignmentNode(AssignmentNode node) {
        printTabs();
        System.out.println("Assign");
        tabs++;
        node.getLhs().visit(this);
        node.getRhs().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitDeclarationNode(DeclarationNode node) {
        printTabs();
        System.out.println("Declare");
        tabs++;
        node.getType().visit(this);
        node.getIdentifierNode().visit(this);
        node.getRhs().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitPrintNode(PrintNode node) {
        printTabs();
        System.out.println("print");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitPrintLnNode(PrintLnNode node) {
        printTabs();
        System.out.println("println");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitReturnNode(ReturnNode node) {
        printTabs();
        System.out.println("return");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitFreeNode(FreeNode node) {
        printTabs();
        System.out.println("free");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitReadNode(ReadNode node) {
        printTabs();
        System.out.println("read");
        tabs++;
        node.getLhs().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitAnyType(AnyType node) {
        return null;
    }

    @Override
    public Void visitPair(Pair node) {
        printTabs();
        System.out.println(node);
        return null;
    }

    @Override
    public Void visitBaseType(BaseType node) {
        printTabs();
        System.out.println(node.toString());
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType node) {
        printTabs();
        System.out.println(node.toString());
        return null;
    }

    @Override
    public Void visitPairType(PairType node) {
        printTabs();
        System.out.println("pair type");
        tabs++;
        node.getFirstElemType().visit(this);
        node.getSecondElemType().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitIntNode(IntNode node) {
        printTabs();
        System.out.println(node.getValue());
        return null;
    }

    @Override
    public Void visitBoolNode(BoolNode node) {
        printTabs();
        System.out.println(node.isTrue());
        return null;
    }

    @Override
    public Void visitCharNode(CharNode node) {
        printTabs();
        Character character = node.getValue();
        String toPrint = "";
        switch (character) {
            case '\0':
                toPrint = "\\0";
                break;
            case '\b':
                toPrint = "\\b";
                break;
            case '\t':
                toPrint = "\\t";
                break;
            case '\n':
                toPrint = "\\n";
                break;
            case '\f':
                toPrint = "\\f";
                break;
            case '\r':
                toPrint = "\\r";
                break;
            case '\"':
                toPrint = "\\\"";
                break;
            case '\'':
                toPrint = "\\\'";
                break;
            case '\\':
                toPrint = "\\\\";
                break;
            default:
                toPrint = character.toString();
                break;
        }
        System.out.println(toPrint);
        return null;
    }

    @Override
    public Void visitStringNode(StringNode node) {
        printTabs();
        System.out.println(node.getValue());
        return null;
    }

    @Override
    public Void visitPairLiterNode(PairLiterNode node) {
        printTabs();
        System.out.println(node.toString());
        return null;
    }

    @Override
    public Void visitArrayElement(ArrayElement node) {
        System.out.println("Array Element");
        ExpressionNode[] expressionNodes = node.getExpressionNodes();
        tabs++;
        for (ExpressionNode expressionNode : expressionNodes) {
            expressionNode.visit(this);
        }
        tabs--;
        return null;
    }

    @Override
    public Void visitArithmeticExpressionNode(ArithmeticExpressionNode node) {
        printTabs();
        System.out.println("Arithmetic expression");
        tabs++;
        node.getLeft().visit(this);
        printTabs();
        System.out.println(node.getOperator());
        node.getRight().visit(this);
        tabs--;
        return null;
    }


    @Override
    public Void visitCompareExpressionNode(CompareExpressionNode node) {
        printTabs();
        System.out.println("Compare expression");
        tabs++;
        node.getLeft().visit(this);
        printTabs();
        System.out.println(node.getOperator());
        node.getRight().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitBoolExpressionNode(BoolExpressionNode node) {
        printTabs();
        System.out.println("Bool expression");
        tabs++;
        node.getLeft().visit(this);
        printTabs();
        System.out.println(node.getOperator());
        node.getRight().visit(this);
        tabs--;
        return null;
    }


    @Override
    public Void visitIdentifierNode(IdentifierNode node) {
        printTabs();
        System.out.println(node.getName());
        return null;
    }

    @Override
    public Void visitNotUnaryExpressionNode(NotUnaryExpressionNode node) {
        printTabs();
        System.out.println("not");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitNegUnaryExpressionNode(NegUnaryExpressionNode node) {
        printTabs();
        System.out.println("neg");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitLenUnaryExpressionNode(LenUnaryExpressionNode node) {
        printTabs();
        System.out.println("len");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitOrdUnaryExpressionNode(OrdUnaryExpressionNode node) {
        printTabs();
        System.out.println("Ord");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitChrUnaryExpressionNode(ChrUnaryExpressionNode node) {
        printTabs();
        System.out.println("Chr");
        tabs++;
        node.getExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitBitNotUnaryExpressionNode(BitNotUnaryExpressionNode node) {
        return null;
    }


    @Override
    public Void visitPairElement(PairElement node) {
        printTabs();
        System.out.println(node.getType().toString());
        tabs++;
        node.getPairExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitArrayLiteral(ArrayLiteral node) {
        printTabs();
        System.out.println("Array Literal");
        ExpressionNode[] expressionNodes = node.getExpressionNodes();
        tabs++;
        for (ExpressionNode expressionNode : expressionNodes) {
            expressionNode.visit(this);
        }
        tabs--;
        return null;
    }

    @Override
    public Void visitNewPairStatement(NewPairStatement node) {
        printTabs();
        System.out.println("New pair stat");
        tabs++;
        node.getFirstExpression().visit(this);
        node.getSecondExpression().visit(this);
        tabs--;
        return null;
    }

    @Override
    public Void visitCallStatement(CallStatement node) {
        printTabs();
        System.out.println("Call");
        tabs++;
        node.getFunctionIdentifier().visit(this);
        node.getArgumentList().visit(this);
        tabs--;
        return null;
    }
}
