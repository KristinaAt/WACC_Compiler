package uk.ac.ic.doc.wacc;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ic.doc.wacc.ast.ASTNodes.*;
import uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category;
import uk.ac.ic.doc.wacc.error.CompileError;
import uk.ac.ic.doc.wacc.semantic_analyser.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class GetExpressionTypeVisitorTest {

  private final List<CompileError> errorList = new ArrayList<>();

  private final Mockery context = new Mockery();
  private final ASTNode mockNode = context.mock(ASTNode.class);
  private final SymbolTable<VariableDeclaration> basicSymbolTable =
      new SymbolTable<>(mockNode);
  private final FunctionSymbolTable<FunctionEntry> functionSymbolTable =
      new FunctionSymbolTable<>();
  private final GetExpressionTypeVisitor getExpressionTypeVisitor =
      new GetExpressionTypeVisitor(errorList, basicSymbolTable,
          functionSymbolTable);
  private final Type mockType = context.mock(Type.class);
  private final ParserRuleContext parserRuleContext = new ParserRuleContext();
  private final Token pseudoToken = new Token() {
    @Override
    public String getText() {
      return null;
    }

    @Override
    public int getType() {
      return 0;
    }

    @Override
    public int getLine() {
      return 0;
    }

    @Override
    public int getCharPositionInLine() {
      return 0;
    }

    @Override
    public int getChannel() {
      return 0;
    }

    @Override
    public int getTokenIndex() {
      return 0;
    }

    @Override
    public int getStartIndex() {
      return 0;
    }

    @Override
    public int getStopIndex() {
      return 0;
    }

    @Override
    public TokenSource getTokenSource() {
      return null;
    }

    @Override
    public CharStream getInputStream() {
      return null;
    }
  };

  private void setContextForNodes(ASTBasicNode... nodes) {
    for (ASTBasicNode node : nodes) {
      node.setCtx(parserRuleContext);
    }
  }

  @Before
  public void setupParserRuleContext() {
    parserRuleContext.start = pseudoToken;
  }

  @Test
  public void visitAnyTypeReturnsCorrectType() {
    AnyType anyType = new AnyType();
    assertEquals(anyType, getExpressionTypeVisitor.visitAnyType(anyType));
  }

  @Test
  public void visitPairReturnsCorrectType() {
    Pair pair = new Pair();
    assertEquals(pair, getExpressionTypeVisitor.visitPair(pair));
  }

  @Test
  public void visitBaseTypeReturnsCorrectType() {
    for (BaseType.Category category : BaseType.Category.values()) {
      BaseType baseType = new BaseType(category);

      assertEquals(baseType,
          getExpressionTypeVisitor.visitBaseType(baseType));
    }
  }

  @Test
  public void visitArrayTypeReturnsCorrectType() {
    ArrayType arrayType = new ArrayType(mockType);
    assertEquals(arrayType,
        getExpressionTypeVisitor.visitArrayType(arrayType));
  }

  @Test
  public void visitPairTypeReturnsCorrectType() {
    PairType pairType = new PairType(mockType, mockType);
    assertEquals(pairType,
        getExpressionTypeVisitor.visitPairType(pairType));
  }

  @Test
  public void visitIntNodeReturnsCorrectType() {
    IntNode intNode = new IntNode(999);
    assertEquals(new BaseType(Category.INT),
        getExpressionTypeVisitor.visitIntNode(intNode));
  }

  @Test
  public void visitBoolNodeReturnsCorrectType() {
    BoolNode boolNode = new BoolNode("true");
    assertEquals(new BaseType(Category.BOOL),
        getExpressionTypeVisitor.visitBoolNode(boolNode));
  }

  @Test
  public void visitCharNodeReturnsCorrectType() {
    CharNode charNode = new CharNode('a');
    assertEquals(new BaseType(Category.CHAR),
        getExpressionTypeVisitor.visitCharNode(charNode));
  }

  @Test
  public void visitStringNodeReturnsCorrectType() {
    StringNode stringNode = new StringNode("test");
    assertEquals(new BaseType(Category.STRING),
        getExpressionTypeVisitor.visitStringNode(stringNode));
  }

  @Test
  public void visitPairLiterNodeReturnsCorrectType() {
    PairLiterNode pairLiterNode = new PairLiterNode();
    assertEquals(new Pair(),
        getExpressionTypeVisitor.visitPairLiterNode(pairLiterNode));
  }

  @Test
  public void visitArrayElementReturnsCorrectType() {
    ArrayElement arrayElement = new ArrayElement(
        new IdentifierNode("test"),
        new ExpressionNode[]{new IntNode(1)});
    Type type = new ArrayType(mockType);
    VariableDeclaration variableDeclaration = new VariableDeclaration(arrayElement.getIdentifierNode().getName(), type);
    basicSymbolTable.add("test", variableDeclaration);

    assertEquals(mockType,
        getExpressionTypeVisitor.visitArrayElement(arrayElement));
  }

  @Test
  public void visitArrayElementFailsOnUndeclaredVariable() {
    IdentifierNode identifierNode = new IdentifierNode("test1");
    IntNode intNode = new IntNode(3);
    ArrayElement arrayElement = new ArrayElement(identifierNode,
        new ExpressionNode[]{intNode});
    setContextForNodes(identifierNode, intNode, arrayElement);

    assertNull(getExpressionTypeVisitor.visitArrayElement(arrayElement));
  }

  @Test
  public void visitArrayElementFailsOnInvalidIndex() {
    IdentifierNode identifierNode = new IdentifierNode("t");
    CharNode charNode = new CharNode('a');
    ArrayElement arrayElement = new ArrayElement(identifierNode,
        new ExpressionNode[]{charNode});
    setContextForNodes(identifierNode, charNode, arrayElement);

    Type type = new ArrayType(mockType);
    VariableDeclaration variableDeclaration = new VariableDeclaration(arrayElement.getIdentifierNode().getName(), type);
    basicSymbolTable.add("t", variableDeclaration);

    assertNull(getExpressionTypeVisitor.visitArrayElement(arrayElement));
  }

  @Test
  public void visitArrayElementFailsOnInvalidDimension() {
    IdentifierNode identifierNode = new IdentifierNode("test123");
    IntNode intNode = new IntNode(3);
    IntNode intNode1 = new IntNode(0);
    ArrayElement arrayElement = new ArrayElement(identifierNode,
        new ExpressionNode[]{intNode, intNode1});
    setContextForNodes(identifierNode, intNode, intNode1, arrayElement);

    Type type = new ArrayType(mockType);
    VariableDeclaration variableDeclaration = new VariableDeclaration(arrayElement.getIdentifierNode().getName(), type);
    basicSymbolTable.add("test123", variableDeclaration);

    assertNull(getExpressionTypeVisitor.visitArrayElement(arrayElement));
  }

  @Test
  public void visitArithmeticExpressionNodeReturnsCorrectType() {
    ArithmeticExpressionNode expressionNode =
        new ArithmeticExpressionNode(
            BinExpressionNode.BinaryOper.PLUS);
    expressionNode.setChildren(new IntNode(5), new IntNode(-2));

    assertEquals(new BaseType(Category.INT),
        getExpressionTypeVisitor.visitArithmeticExpressionNode(
            expressionNode));
  }

  @Test
  public void visitArithmeticExpressionNodeFailsOnWrongLeftSide() {
    ArithmeticExpressionNode expressionNode =
        new ArithmeticExpressionNode(BinExpressionNode.BinaryOper.PLUS);
    BoolNode boolNode = new BoolNode("true");
    IntNode intNode = new IntNode(0);
    expressionNode.setChildren(boolNode, intNode);
    setContextForNodes(expressionNode, boolNode, intNode);

    assertNull(
        getExpressionTypeVisitor.visitArithmeticExpressionNode(
            expressionNode));
  }

  @Test
  public void visitArithmeticExpressionNodeFailsOnWrongRightSide() {
    ArithmeticExpressionNode expressionNode =
        new ArithmeticExpressionNode(BinExpressionNode.BinaryOper.PLUS);
    IntNode intNode = new IntNode(11);
    CharNode charNode = new CharNode('z');
    expressionNode.setChildren(intNode, charNode);
    setContextForNodes(expressionNode, intNode, charNode);

    assertNull(
        getExpressionTypeVisitor.visitArithmeticExpressionNode(
            expressionNode));
  }

  @Test
  public void visitCompareExpressionNodeReturnsCorrectType() {
    CompareExpressionNode expressionNode =
        new CompareExpressionNode(BinExpressionNode.BinaryOper.EQ);
    expressionNode.setChildren(new BoolNode("true"),
        new BoolNode("false"));

    assertEquals(new BaseType(Category.BOOL),
        getExpressionTypeVisitor.visitCompareExpressionNode(
            expressionNode));
  }

  @Test
  public void visitCompareExpressionNodeFailsOnNonBaseType() {
    CompareExpressionNode expressionNode =
        new CompareExpressionNode(
            BinExpressionNode.BinaryOper.GREATER_EQ);
    IdentifierNode identifierNode = new IdentifierNode("arr");
    IdentifierNode identifierNode1 = new IdentifierNode("arr2");
    expressionNode.setChildren(identifierNode, identifierNode1);
    basicSymbolTable.add("arr", new VariableDeclaration("arr", mockType));
    basicSymbolTable.add("arr2", new VariableDeclaration("arr2", mockType));
    setContextForNodes(expressionNode, identifierNode, identifierNode1);

    assertNull(
        getExpressionTypeVisitor.visitCompareExpressionNode(
            expressionNode));
  }

  @Test
  public void visitCompareExpressionNodeFailsOnTypeMismatch() {
    CompareExpressionNode expressionNode =
        new CompareExpressionNode(BinExpressionNode.BinaryOper.EQ);
    BoolNode boolNode = new BoolNode("true");
    IntNode intNode = new IntNode(999);
    expressionNode.setChildren(boolNode, intNode);
    setContextForNodes(expressionNode, boolNode, intNode);

    assertNull(
        getExpressionTypeVisitor.visitCompareExpressionNode(
            expressionNode));
  }

  @Test
  public void visitBoolExpressionNodeReturnsCorrectType() {
    BoolExpressionNode expressionNode = new BoolExpressionNode(
        BinExpressionNode.BinaryOper.AND);
    expressionNode.setChildren(new BoolNode("false"),
        new BoolNode("true"));

    assertEquals(new BaseType(Category.BOOL),
        getExpressionTypeVisitor.visitBoolExpressionNode(
            expressionNode));
  }

  @Test
  public void visitBoolExpressionNodeFailsOnWrongLeftSide() {
    BoolExpressionNode expressionNode = new BoolExpressionNode(
        BinExpressionNode.BinaryOper.AND);
    StringNode stringNode = new StringNode("test");
    BoolNode boolNode = new BoolNode("true");
    expressionNode.setChildren(stringNode, boolNode);
    setContextForNodes(expressionNode, stringNode, boolNode);

    assertNull(
        getExpressionTypeVisitor.visitBoolExpressionNode(
            expressionNode));
  }

  @Test
  public void visitBoolExpressionNodeFailsOnWrongRightSide() {
    BoolExpressionNode expressionNode = new BoolExpressionNode(
        BinExpressionNode.BinaryOper.AND);
    BoolNode boolNode = new BoolNode("false");
    IntNode intNode = new IntNode(-1);
    expressionNode.setChildren(boolNode, intNode);
    setContextForNodes(expressionNode, boolNode, intNode);

    assertNull(
        getExpressionTypeVisitor.visitBoolExpressionNode(
            expressionNode));
  }

  @Test
  public void visitIdentifierNodeReturnsCorrectType() {
    IdentifierNode identifierNode = new IdentifierNode("this_is");
    basicSymbolTable.add("this_is", new VariableDeclaration("this_is", mockType));

    assertEquals(mockType,
        getExpressionTypeVisitor.visitIdentifierNode(identifierNode));
  }

  @Test
  public void visitIdentifierNodeFailsOnUndeclaredVariable() {
    IdentifierNode identifierNode = new IdentifierNode("yeah");
    setContextForNodes(identifierNode);

    assertNull(getExpressionTypeVisitor.visitIdentifierNode(
        identifierNode));
  }

  @Test
  public void visitNotUnaryExpressionNodeReturnsCorrectType() {
    NotUnaryExpressionNode expressionNode =
        new NotUnaryExpressionNode(UnaryExpressionNode.UnaryOper.NOT);
    expressionNode.setExpression(new BoolNode("true"));

    assertEquals(new BaseType(Category.BOOL),
        getExpressionTypeVisitor.visitNotUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitNotUnaryExpressionNodeFailsOnWrongType() {
    NotUnaryExpressionNode expressionNode =
        new NotUnaryExpressionNode(UnaryExpressionNode.UnaryOper.NOT);
    IntNode intNode = new IntNode(1);
    expressionNode.setExpression(intNode);
    setContextForNodes(expressionNode, intNode);

    assertNull(
        getExpressionTypeVisitor.visitNotUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitNegUnaryExpressionNodeReturnsCorrectType() {
    NegUnaryExpressionNode expressionNode =
        new NegUnaryExpressionNode(UnaryExpressionNode.UnaryOper.MINUS);
    expressionNode.setExpression(new IntNode(55));

    assertEquals(new BaseType(Category.INT),
        getExpressionTypeVisitor.visitNegUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitNegUnaryExpressionNodeFailsOnWrongType() {
    NegUnaryExpressionNode expressionNode =
        new NegUnaryExpressionNode(UnaryExpressionNode.UnaryOper.MINUS);
    StringNode stringNode = new StringNode("no");
    expressionNode.setExpression(stringNode);
    setContextForNodes(expressionNode, stringNode);

    assertNull(
        getExpressionTypeVisitor.visitNegUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitLenUnaryExpressionNodeReturnsCorrectType() {
    LenUnaryExpressionNode expressionNode =
        new LenUnaryExpressionNode(UnaryExpressionNode.UnaryOper.LEN);
    expressionNode.setExpression(new IdentifierNode("array"));
    basicSymbolTable.add("array", new VariableDeclaration("array", new ArrayType(mockType)));

    assertEquals(new BaseType(Category.INT),
        getExpressionTypeVisitor.visitLenUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitLenUnaryExpressionNodeFailsOnWrongType() {
    LenUnaryExpressionNode expressionNode =
        new LenUnaryExpressionNode(UnaryExpressionNode.UnaryOper.LEN);
    IntNode intNode = new IntNode(0);
    expressionNode.setExpression(intNode);
    setContextForNodes(expressionNode, intNode);

    assertNull(
        getExpressionTypeVisitor.visitLenUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitOrdUnaryExpressionNodeReturnsCorrectType() {
    OrdUnaryExpressionNode expressionNode =
        new OrdUnaryExpressionNode(UnaryExpressionNode.UnaryOper.ORD);
    expressionNode.setExpression(new CharNode('x'));

    assertEquals(new BaseType(Category.INT),
        getExpressionTypeVisitor.visitOrdUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitOrdUnaryExpressionNodeFailsOnWrongType() {
    OrdUnaryExpressionNode expressionNode =
        new OrdUnaryExpressionNode(UnaryExpressionNode.UnaryOper.ORD);
    IntNode intNode = new IntNode(-222);
    expressionNode.setExpression(intNode);
    setContextForNodes(expressionNode, intNode);

    assertNull(
        getExpressionTypeVisitor.visitOrdUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitChrUnaryExpressionNodeReturnsCorrectType() {
    ChrUnaryExpressionNode expressionNode =
        new ChrUnaryExpressionNode(UnaryExpressionNode.UnaryOper.CHR);
    expressionNode.setExpression(new IntNode(65));

    assertEquals(new BaseType(Category.CHAR),
        getExpressionTypeVisitor.visitChrUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitChrUnaryExpressionNodeFailsOnWrongType() {
    ChrUnaryExpressionNode expressionNode =
        new ChrUnaryExpressionNode(UnaryExpressionNode.UnaryOper.CHR);
    CharNode charNode = new CharNode('i');
    expressionNode.setExpression(charNode);
    setContextForNodes(expressionNode, charNode);

    assertNull(
        getExpressionTypeVisitor.visitChrUnaryExpressionNode(
            expressionNode));
  }

  @Test
  public void visitPairElementFstReturnsCorrectType() {
    PairElement pairElement =
        new PairElement(new IdentifierNode("pr"),
            PairElement.Type.FST);
    basicSymbolTable.add("pr", new VariableDeclaration("pr", new PairType(mockType,
        new BaseType(Category.BOOL))));

    assertEquals(mockType,
        getExpressionTypeVisitor.visitPairElement(pairElement));
  }

  @Test
  public void visitPairElementSndReturnsCorrectType() {
    PairElement pairElement =
        new PairElement(new IdentifierNode("hey"),
            PairElement.Type.SND);
    basicSymbolTable.add("hey", new VariableDeclaration("hey",  new PairType(mockType,
        new BaseType(Category.INT))));

    assertEquals(new BaseType(Category.INT),
        getExpressionTypeVisitor.visitPairElement(pairElement));
  }

  @Test
  public void visitPairElementFailsOnNonIdentifierType() {
    IntNode intNode = new IntNode(0);
    PairElement pairElement =
        new PairElement(intNode, PairElement.Type.FST);
    setContextForNodes(intNode, pairElement);

    assertNull(getExpressionTypeVisitor.visitPairElement(pairElement));
  }

  @Test
  public void visitPairElementFailsOnUndeclaredVariable() {
    IdentifierNode identifierNode = new IdentifierNode("decl");
    PairElement pairElement =
        new PairElement(identifierNode,
            PairElement.Type.SND);
    setContextForNodes(identifierNode, pairElement);

    assertNull(getExpressionTypeVisitor.visitPairElement(pairElement));
  }

  @Test
  public void visitPairElementFailsOnNonPair() {
    IdentifierNode identifierNode = new IdentifierNode("not_pair");
    PairElement pairElement =
        new PairElement(identifierNode,
            PairElement.Type.SND);
    basicSymbolTable.add("not_pair", new VariableDeclaration("not_pair", mockType));
    setContextForNodes(identifierNode, pairElement);

    assertNull(getExpressionTypeVisitor.visitPairElement(pairElement));
  }

  @Test
  public void visitArrayLiteralReturnsCorrectType() {
    ArrayLiteral arrayLiteral =
        new ArrayLiteral(new ExpressionNode[]{new IntNode(-222),
            new IntNode(2438)});

    assertEquals(new ArrayType(new BaseType(Category.INT)),
        getExpressionTypeVisitor.visitArrayLiteral(arrayLiteral));
  }

  @Test
  public void visitArrayLiteralFailsOnDifferentTypesWithinLiteral() {
    IntNode intNode = new IntNode(9128);
    StringNode stringNode = new StringNode("enemy");
    ArrayLiteral arrayLiteral =
        new ArrayLiteral(new ExpressionNode[]{intNode,
            stringNode});
    setContextForNodes(intNode, stringNode);

    assertNull(getExpressionTypeVisitor.visitArrayLiteral(arrayLiteral));
  }

  @Test
  public void visitNewPairStatementReturnsCorrectType() {
    NewPairStatement newPairStatement =
        new NewPairStatement(new IntNode(11), new CharNode('x'));

    assertEquals(new PairType(new BaseType(Category.INT),
            new BaseType(Category.CHAR)),
        getExpressionTypeVisitor.visitNewPairStatement(
            newPairStatement));
  }

  @Test
  public void visitCallStatementReturnsCorrectType() {
    CallStatement callStatement =
        new CallStatement(new IdentifierNode("func"));
    callStatement.setExpectedReturnType(mockType);
    functionSymbolTable.add("func", new FunctionEntry(mockType));

    assertEquals(mockType,
        getExpressionTypeVisitor.visitCallStatement(callStatement));
  }

  @Test
  public void visitCallStatementFailsOnNonExistentFunction() {
    IdentifierNode identifierNode =
        new IdentifierNode("catch_me_if_you_can");
    CallStatement callStatement =
        new CallStatement(identifierNode);
    setContextForNodes(identifierNode, callStatement);

    assertNull(getExpressionTypeVisitor.visitCallStatement(callStatement));
  }

  @Test
  public void visitCallStatementFailsOnWrongArgNum() {
    IdentifierNode identifierNode = new IdentifierNode("func2");
    StringNode stringNode = new StringNode("haha");
    CallStatement callStatement =
        new CallStatement(identifierNode,
            new ArgList(new ExpressionNode[]{stringNode}));
    callStatement.setExpectedReturnType(mockType);
    functionSymbolTable.add("func2", new FunctionEntry(mockType));
    setContextForNodes(identifierNode, stringNode, callStatement);

    assertNull(getExpressionTypeVisitor.visitCallStatement(callStatement));
  }

  @Test
  public void visitCallStatementFailsOnArgTypeMismatch() {
    IdentifierNode identifierNode = new IdentifierNode("func3");
    StringNode stringNode = new StringNode("Oh, baby!");
    CallStatement callStatement =
        new CallStatement(identifierNode,
            new ArgList(new ExpressionNode[]{stringNode}));

    FunctionEntry functionEntry = new FunctionEntry(mockType);
    functionEntry.addType(mockType);
    functionSymbolTable.add("func2", functionEntry);

    setContextForNodes(identifierNode, stringNode, callStatement);

    assertNull(getExpressionTypeVisitor.visitCallStatement(callStatement));
  }
}
