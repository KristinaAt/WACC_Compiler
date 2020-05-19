package uk.ac.ic.doc.wacc.ast;

/*  Every node in ASTNodes must implement ASTNode.
    ASTNode interface has a method called visit(). Visit takes a visitor
    as an argument.

    This allows the visit method to be overriden by any class which
    implements the ASTNode interface. As such, each sub node can specify
    which method to call. For example, let's suppose we have a ProgNode.
    Then ProgNode will override the visit method. It will use the
    passed in visitor to call the method visitProgNode with the ProgNode
    as the argument. As a result we can guarantee that visitProgNode will
    be called, as the overridden method is the method that is used.

    So for each node we call the relevant visitor method, passing `this`
    as an argument to pass the node for later use.

    `public abstract <T> T visit(ASTVisitor<T> visitor)`

    This is similar to the way ANTLR makes visitors.
    The <T> T visit specifies that the return type will be the same type
    as the arbitary type specified by the parameter. This means that if
    the visitor has type T (ASTVisitor<T> visitor) then the return type
    of the visit method is also T. This is required to ensure we get
    the correct return type of the visitor.

    We need to pass the visitor in order to call the valid visitNode method
    based on which node we are trying to visit. So if we call visit(visitor)
    on a progNode, then the progNode visit method is used, which specifies
    that we should call visitor.visitProgNode(this). The `this` ensures we
    call the method having passed the progNode so that we can use the fields
    and/or methods of the progNode.
 */

import uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category;
import org.antlr.v4.runtime.ParserRuleContext;
import uk.ac.ic.doc.wacc.semantic_analyser.FunctionEntry;
import uk.ac.ic.doc.wacc.semantic_analyser.VariableDeclaration;

import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.CHAR;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.STRING;

public class ASTNodes {

  public interface ASTNode {
    <T> T visit(ASTVisitor<T> visitor);
  }

  public interface LeftHandSideAssignment extends ASTNode {

  }

  public interface RightHandSideAssignment extends ASTNode {

  }

  public interface Type extends ASTNode, Comparable<Type> {
    int getSize();

    default int compareTo(Type type) {
      return getSize() - type.getSize();
    }
  }

  public abstract static class ASTBasicNode {

    private ParserRuleContext ctx;

    public int getLineNum(ParserRuleContext context) {
      return context.start.getLine();
    }

    public int getColumnNum(ParserRuleContext context) {
      return context.start.getCharPositionInLine();
    }

    public ParserRuleContext getCtx() {
      return ctx;
    }

    public void setCtx(ParserRuleContext ctx) {
      this.ctx = ctx;
    }
  }

  /* Body node class */
  public abstract static class BodyNode extends ASTBasicNode
      implements ASTNode {

  }

  public abstract static class ExpressionNode extends ASTBasicNode
      implements ASTNode, RightHandSideAssignment {

  }

  /* A collection of expressions used for array and function calls
     implementation */
  public abstract static class ExpressionCollection implements ASTNode {

    private ExpressionNode[] expressionNodes;

    public ExpressionCollection(ExpressionNode[] expressionNodes) {
      this.expressionNodes = expressionNodes;
    }

    public ExpressionNode[] getExpressionNodes() {
      return expressionNodes;
    }

    public void setExpressionNodes(ExpressionNode[] expressionNodes) {
      this.expressionNodes = expressionNodes;
    }
  }

  /* An abstraction of a binary expression node */
  public abstract static class BinExpressionNode extends ExpressionNode {

    private ExpressionNode left;
    private ExpressionNode right;
    private BinaryOper operator;

    public BinExpressionNode(BinaryOper operator) {
      this.operator = operator;
    }

    public ExpressionNode getLeft() {
      return left;
    }

    public ExpressionNode getRight() {
      return right;
    }

    public BinaryOper getOperator() {
      return operator;
    }

    public void setChildren(ExpressionNode left, ExpressionNode right) {
      this.left = left;
      this.right = right;
    }

    public void setLeft(ExpressionNode left) {
      this.left = left;
    }

    public void setRight(ExpressionNode right) {
      this.right = right;
    }

    public enum BinaryOper {
      DIVIDE,
      MOD,
      PLUS,
      BIN_MINUS,
      MULTIPLY,
      GREATER_THAN,
      GREATER_EQ,
      LESS_THAN,
      LESS_EQ,
      EQ,
      NOT_EQ,
      AND,
      OR,
      BIT_AND,
      BIT_OR,
      BIT_EOR,
      SHIFT_L,
      SHIFT_R;
    }
  }

  /* Prog node stores the main body and all function nodes within it */
  public static class ProgNode extends ASTBasicNode implements ASTNode {

    private BodyNode body;
    private FunctionNode[] functionNodes;

    public ProgNode(BodyNode body, FunctionNode[] functionNodes) {
      this.body = body;
      this.functionNodes = functionNodes;
    }

    public BodyNode getBody() {
      return body;
    }

    public FunctionNode[] getFunctionNodes() {
      return functionNodes;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitProgNode(this);
    }

    public void setBody(BodyNode body) {
      this.body = body;
    }

    public void setFunctionNodes(FunctionNode[] functionNodes) {
      this.functionNodes = functionNodes;
    }
  }

  /* Function node implementation */
  public static class FunctionNode extends ASTBasicNode implements ASTNode {

    private Type type;
    private IdentifierNode identifierNode;
    private ParamList paramList;
    private BodyNode bodyNode;
    private String label;

    public FunctionNode(Type type, IdentifierNode identifierNode,
        BodyNode bodyNode) {
      this(type, identifierNode, bodyNode, null);
    }

    public FunctionNode(Type type, IdentifierNode identifierNode,
        BodyNode bodyNode, ParamList paramList) {
      this.type = type;
      this.identifierNode = identifierNode;
      this.bodyNode = bodyNode;
      this.paramList = paramList;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }


    public Type getType() {
      return type;
    }

    public IdentifierNode getIdentifierNode() {
      return identifierNode;
    }

    public BodyNode getBodyNode() {
      return bodyNode;
    }

    public ParamList getParamList() {
      return paramList;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitFunctionNode(this);
    }

    public void setBodyNode(BodyNode bodyNode) {
      this.bodyNode = bodyNode;
    }
  }

  /* Parameter node implementation */
  public static class ParamNode extends ASTBasicNode implements ASTNode {

    private Type type;
    private IdentifierNode identifierNode;

    public ParamNode(Type type, IdentifierNode identifierNode) {
      this.type = type;
      this.identifierNode = identifierNode;
    }

    public Type getType() {
      return type;
    }

    public IdentifierNode getIdentifierNode() {
      return identifierNode;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitParamNode(this);
    }
  }

  /* A parameter list implementation */
  public static class ParamList implements ASTNode {

    private ParamNode[] paramList;

    public ParamList(ParamNode[] paramList) {
      this.paramList = paramList;
    }

    public ParamNode[] getParamList() {
      return paramList;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitParamList(this);
    }
  }

  /* Arguments list node used in call statements */
  public static class ArgList extends ExpressionCollection {

    public ArgList(ExpressionNode[] expressionNodes) {
      super(expressionNodes);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitArgList(this);
    }
  }

  /* Begin-end node implementation */
  public static class BeginNode extends BodyNode {

    private BodyNode body;

    public BeginNode(BodyNode body) {
      this.body = body;
    }

    public BodyNode getBody() {
      return body;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitBeginNode(this);
    }

    public void setBody(BodyNode body) {
      this.body = body;
    }
  }

  /* Compound node implementation */
  public static class CompoundNode extends BodyNode {

    private BodyNode leftBody;
    private BodyNode rightBody;

    public CompoundNode(BodyNode leftBody, BodyNode rightBody) {
      this.leftBody = leftBody;
      this.rightBody = rightBody;
    }

    public BodyNode getLeftBody() {
      return leftBody;
    }

    public BodyNode getRightBody() {
      return rightBody;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitCompoundNode(this);
    }

    public void setLeftBody(BodyNode leftBody) {
      this.leftBody = leftBody;
    }

    public void setRightBody(BodyNode rightBody) {
      this.rightBody = rightBody;
    }
  }

  /* The while body node implementation */
  public static class WhileNode extends BodyNode {

    private ExpressionNode expression;
    private BodyNode body;

    public WhileNode(ExpressionNode expression, BodyNode body) {
      this.expression = expression;
      this.body = body;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    public BodyNode getBody() {
      return body;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitWhileNode(this);
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }

    public void setBody(BodyNode body) {
      this.body = body;
    }
  }

  /* Exit body node that stores the exit expression node */
  public static class ExitNode extends BodyNode {

    private ExpressionNode expression;

    public ExitNode(ExpressionNode expression) {
      this.expression = expression;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitExitNode(this);
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  public static class SkipNode extends BodyNode {

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitSkipNode(this);
    }
  }

  /* If statement node implementation */
  public static class IfStatement extends BodyNode {

    private ExpressionNode condition;
    private BodyNode trueBody;
    private BodyNode falseBody;

    public IfStatement(ExpressionNode condition, BodyNode trueBody,
        BodyNode falseBody) {
      this.condition = condition;
      this.trueBody = trueBody;
      this.falseBody = falseBody;
    }

    public ExpressionNode getCondition() {
      return condition;
    }

    public BodyNode getTrueBody() {
      return trueBody;
    }

    public BodyNode getFalseBody() {
      return falseBody;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitIfStatement(this);
    }

    public void setCondition(ExpressionNode condition) {
      this.condition = condition;
    }

    public void setTrueBody(BodyNode trueBody) {
      this.trueBody = trueBody;
    }

    public void setFalseBody(BodyNode falseBody) {
      this.falseBody = falseBody;
    }
  }

  /* Assignment node implementation */
  public static class AssignmentNode extends BodyNode {

    private LeftHandSideAssignment lhs;
    private RightHandSideAssignment rhs;

    public AssignmentNode(LeftHandSideAssignment lhs,
        RightHandSideAssignment rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
    }

    public LeftHandSideAssignment getLhs() {
      return lhs;
    }

    public RightHandSideAssignment getRhs() {
      return rhs;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitAssignmentNode(this);
    }

    public void setRhs(RightHandSideAssignment rhs) {
      this.rhs = rhs;
    }
  }

  /* Declaration node implementation. The variable declaration
     save the stack position of the identifier which is
     used during code generation */
  public static class DeclarationNode extends BodyNode {

    private Type type;
    private IdentifierNode identifier;
    private RightHandSideAssignment rhs;
    private VariableDeclaration variableDeclaration;

    public DeclarationNode(Type type, IdentifierNode identifier,
        RightHandSideAssignment rhs) {
      this.type = type;
      this.identifier = identifier;
      this.rhs = rhs;
    }

    public Type getType() {
      return type;
    }

    public IdentifierNode getIdentifierNode() {
      return identifier;
    }

    public RightHandSideAssignment getRhs() {
      return rhs;
    }

    public void setRhs(RightHandSideAssignment rhs) {
      this.rhs = rhs;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitDeclarationNode(this);
    }

    public VariableDeclaration getVariableDeclaration() {
      return variableDeclaration;
    }

    public void setVariableDeclaration(VariableDeclaration variableDeclaration) {
      this.variableDeclaration = variableDeclaration;
    }
  }

  /* Print node implementation. Stores the type of the expression
     which will be printed for the code generation phase */
  public static class PrintNode extends BodyNode {

    private ExpressionNode expression;
    private Type type;

    public PrintNode(ExpressionNode expression) {
      this.expression = expression;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitPrintNode(this);
    }

    public Type getType() {
      return type;
    }

    public void setType(Type type) {
      this.type = type;
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  /* PrintLn node implementation similar to print node */
  public static class PrintLnNode extends BodyNode {

    private ExpressionNode expression;
    private Type type;

    public PrintLnNode(ExpressionNode expression) {
      this.expression = expression;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitPrintLnNode(this);
    }

    public Type getType() {
      return type;
    }

    public void setType(Type type) {
      this.type = type;
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  /* Return body node that stores a return expression */
  public static class ReturnNode extends BodyNode {

    private ExpressionNode expression;

    public ReturnNode(ExpressionNode expression) {
      this.expression = expression;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitReturnNode(this);
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  /* Free body node that stores the expression node to be freed. */
  public static class FreeNode extends BodyNode {

    private ExpressionNode expression;

    public FreeNode(ExpressionNode expression) {
      this.expression = expression;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitFreeNode(this);
    }
  }

  /* Read node implementation. Stores the type of the expression
     which will be read for the code generation phase */
  public static class ReadNode extends BodyNode
      implements LeftHandSideAssignment {

    private LeftHandSideAssignment lhs;
    private Type type;

    public ReadNode(LeftHandSideAssignment lhs) {
      this.lhs = lhs;
    }

    public LeftHandSideAssignment getLhs() {
      return lhs;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitReadNode(this);
    }

    public Type getType() {
      return type;
    }

    public void setType(Type type) {
      this.type = type;
    }
  }

  /* AddToList node implementation. Stores the list that we want to add to
     and the value expression */
  public static class AddToListNode extends BodyNode {

    private final IdentifierNode identifier;
    private ExpressionNode expression;

    public AddToListNode(IdentifierNode identifier,
                         ExpressionNode expression) {
      this.identifier = identifier;
      this.expression = expression;
    }


    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitAddToListNode(this);
    }

    public IdentifierNode getIdentifier() {
      return identifier;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  /* RemoveFromList node implementation. Stores the list that we want to remove
     from and the value expression */
  public static class RemoveFromListNode extends BodyNode {

    private final IdentifierNode identifier;
    private ExpressionNode expression;

    public RemoveFromListNode(IdentifierNode identifer,
                              ExpressionNode expression) {
      this.identifier = identifer;
      this.expression = expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitRemoveFromListNode(this);
    }

    public IdentifierNode getIdentifier() {
      return identifier;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  public static class GetFromListNode extends ExpressionNode
          implements RightHandSideAssignment {

    private final IdentifierNode identifier;
    private ExpressionNode expression;
    private Type type;

    public GetFromListNode(IdentifierNode identifier,
                           ExpressionNode expression) {
      this.identifier = identifier;
      this.expression = expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
     return visitor.visitGetFromListNode(this);
    }

    public IdentifierNode getIdentifier() {
      return identifier;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    public Type getType() {
      return type;
    }

    public void setType(Type type) {
      this.type = type;
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  /* ListContains node implementation. Checks if the list contains the passed
     expression */
  public static class ListContainsNode extends ExpressionNode
          implements RightHandSideAssignment {
    private final IdentifierNode identifier;
    private ExpressionNode expression;

    public ListContainsNode(IdentifierNode identifier, ExpressionNode expression) {
      this.identifier = identifier;
      this.expression = expression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitListContainsNode(this);
    }

    public IdentifierNode getIdentifier() {
      return identifier;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }
  }

  /* Any type node used for empty array declarations. It is used during
     semantic analysis */
  public static class AnyType implements Type {

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitAnyType(this);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      return obj instanceof Type;
    }

    @Override
    public int getSize() {
      throw new IllegalArgumentException("AnyType does not support getSize()");
    }
  }

  /* A pair type implementation */
  public static class Pair implements Type {

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitPair(this);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj || (obj instanceof AnyType)
          || (obj instanceof PairType)) {
        return true;
      }
      return (obj instanceof Pair);
    }

    @Override
    public String toString() {
      return "PAIR";
    }

    @Override
    public int getSize() {
      return 4;
    }
  }

  /* Base type implementation (int, char, bool, string) */
  public static class BaseType implements Type {

    private Category category;

    public BaseType(Category category) {
      this.category = category;
    }

    public Category getCategory() {
      return category;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitBaseType(this);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj || (obj instanceof AnyType)) {
        return true;
      }
      if (!(obj instanceof BaseType)) {
        return false;
      }
      return this.getCategory() == ((BaseType) obj).getCategory();
    }

    @Override
    public String toString() {
      return this.category.toString();
    }

    @Override
    public int getSize() {
      switch (category) {
        case INT:
        case STRING:
        default:
          return 4;

        case BOOL:
        case CHAR:
          return 1;
      }
    }

    public enum Category {
      VOID,
      INT,
      BOOL,
      CHAR,
      STRING
    }
  }
  /* Array type implementation */
  public static class ArrayType implements Type {

    private Type type;

    public ArrayType(Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj || (obj instanceof AnyType)) {
        return true;
      }
      if (type.equals(new BaseType(CHAR))
          && (obj instanceof BaseType)) {
        return ((BaseType) obj).category == Category.STRING;
      }
      if (!(obj instanceof ArrayType)) {
        return false;
      }
      return type.equals(((ArrayType) obj).type);
    }

    @Override
    public String toString() {
      if (type.equals(new BaseType(CHAR))) {
        return STRING.toString();
      }
      return type.toString() + "[]";
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitArrayType(this);
    }

    @Override
    public int getSize() {
      return 4;
    }
  }

  /* List type implementation that stores the type of the list */
  public static class ListType implements Type {

    private final Type type;

    public ListType(Type type) {
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj || (obj instanceof AnyType)) {
        return true;
      }
      if (!(obj instanceof ListType)) {
        return false;
      }
      return type.equals(((ListType) obj).type);
    }

    @Override
    public String toString() {
      return "LIST<" + type + ">";
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitListType(this);
    }

    @Override
    public int getSize() {
      return 4;
    }
  }

  /* Pair type implementation that stores the types of the fst
     and snd of the pair*/
  public static class PairType implements Type {

    private Type firstElemType;
    private Type secondElemType;

    public PairType(Type firstElemType, Type secondElemType) {
      this.firstElemType = firstElemType;
      this.secondElemType = secondElemType;
    }

    public Type getFirstElemType() {
      return firstElemType;
    }

    public Type getSecondElemType() {
      return secondElemType;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj || (obj instanceof AnyType)
          || (obj instanceof Pair)) {
        return true;
      }
      if (!(obj instanceof PairType)) {
        return false;
      }
      return (firstElemType.equals(((PairType) obj).firstElemType))
          && (secondElemType.equals(((PairType) obj).secondElemType));
    }

    @Override
    public String toString() {
      return "pair(" + firstElemType.toString() + ","
          + secondElemType.toString() + ")";
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitPairType(this);
    }

    @Override
    public int getSize() {
      return 4;
    }
  }

  /* Int node implementation */
  public static class IntNode extends ExpressionNode {

    private int value;

    public IntNode(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitIntNode(this);
    }
  }

  /* Bool node implementation */
  public static class BoolNode extends ExpressionNode {

    private boolean value;

    public BoolNode(String value) {
      this.value = Boolean.parseBoolean(value);
    }

    public BoolNode(Boolean value) {
      this.value = value;
    }

    public boolean isTrue() {
      return value;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitBoolNode(this);
    }

  }

  /* Character node implementation */
  public static class CharNode extends ExpressionNode {

    private Character value;

    public CharNode(Character value) {
      this.value = value;
    }

    public Character getValue() {
      return value;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitCharNode(this);
    }
  }

  /* String node implementation */
  public static class StringNode extends ExpressionNode {

    private String value;

    public StringNode(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitStringNode(this);
    }
  }

  /* A null pair literal node implementation */
  public static class PairLiterNode extends ExpressionNode {

    @Override
    public String toString() {
      return "Null";
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitPairLiterNode(this);
    }
  }

  /* An array element implementation */
  public static class ArrayElement extends ExpressionNode
      implements LeftHandSideAssignment {

    private IdentifierNode identifier;
    private ExpressionNode[] expressionNodes;

    public ArrayElement(IdentifierNode identifier,
        ExpressionNode[] expressionNodes) {
      this.identifier = identifier;
      this.expressionNodes = expressionNodes;
    }

    public IdentifierNode getIdentifierNode() {
      return identifier;
    }

    public ExpressionNode[] getExpressionNodes() {
      return expressionNodes;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitArrayElement(this);
    }

    public void setExpressionNodes(ExpressionNode[] expressionNodes) {
      this.expressionNodes = expressionNodes;
    }
  }

  /* Arithmetic expression node implementation */
  public static class ArithmeticExpressionNode extends BinExpressionNode {

    public ArithmeticExpressionNode(BinaryOper binaryOper) {
      super(binaryOper);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitArithmeticExpressionNode(this);
    }
  }

  /* Compare expression node implementation */
  public static class CompareExpressionNode extends BinExpressionNode {

    private Type type;

    public CompareExpressionNode(BinaryOper binaryOper) {
      super(binaryOper);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitCompareExpressionNode(this);
    }

    public Type getType() {
      return type;
    }

    public void setType(Type type) {
      this.type = type;
    }
  }

  /* Bool expression node implementation */
  public static class BoolExpressionNode extends BinExpressionNode {

    public BoolExpressionNode(BinaryOper binaryOper) {
      super(binaryOper);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitBoolExpressionNode(this);
    }
  }

  /* An identifier node implementation. It stores a variable declaration
     which contains its stack position used during code generation */
  public static class IdentifierNode extends ExpressionNode
      implements LeftHandSideAssignment {

    private String identifierString;
    private VariableDeclaration variableDeclaration;

    public IdentifierNode(String identifierString) {
      this.identifierString = identifierString;
    }

    public String getName() {
      return identifierString;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitIdentifierNode(this);
    }

    public VariableDeclaration getVariableDeclaration() {
      return variableDeclaration;
    }

    public void setVariableDeclaration(VariableDeclaration variableDeclaration) {
      this.variableDeclaration = variableDeclaration;
    }
  }

  /* Unary expression node implementation */
  public abstract static class UnaryExpressionNode extends ExpressionNode {

    private ExpressionNode expression;
    private UnaryOper operator;

    public UnaryExpressionNode(UnaryOper operator) {
      this.operator = operator;
    }

    public ExpressionNode getExpression() {
      return expression;
    }

    public void setExpression(ExpressionNode expression) {
      this.expression = expression;
    }

    public enum UnaryOper {
      NOT,
      MINUS,
      LEN,
      ORD,
      CHR,
      BIT_NOT,
      LIST_SIZE;
    }
  }

  /* Not unary expression node implementation */
  public static class NotUnaryExpressionNode extends UnaryExpressionNode {

    public NotUnaryExpressionNode(UnaryOper operator) {
      super(operator);
    }
    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitNotUnaryExpressionNode(this);
    }
  }

  /* Bitwise not unary expression node implementation */
  public static class BitNotUnaryExpressionNode extends UnaryExpressionNode {

    public BitNotUnaryExpressionNode(UnaryOper operator) {
      super(operator);
    }
    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitBitNotUnaryExpressionNode(this);
    }
  }

  /* Negated unary expression node implementation */
  public static class NegUnaryExpressionNode extends UnaryExpressionNode {

    public NegUnaryExpressionNode(UnaryOper operator) {
      super(operator);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitNegUnaryExpressionNode(this);
    }
  }

  /* Length(len) unary expression node implementation */
  public static class LenUnaryExpressionNode extends UnaryExpressionNode {
    private IdentifierNode identifierNode;

    public LenUnaryExpressionNode(UnaryOper operator) {
      super(operator);
    }

    public IdentifierNode getIdentifierNode() {
      return identifierNode;
    }

    public void setIdentifierNode(IdentifierNode identifierNode) {
      this.identifierNode = identifierNode;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitLenUnaryExpressionNode(this);
    }
  }

  /* Ord unary expression node implementation */
  public static class OrdUnaryExpressionNode extends UnaryExpressionNode {

    public OrdUnaryExpressionNode(UnaryOper operator) {
      super(operator);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitOrdUnaryExpressionNode(this);
    }
  }

  /* Chr unary expression node implementation */

  public static class ChrUnaryExpressionNode extends UnaryExpressionNode {

    public ChrUnaryExpressionNode(UnaryOper operator) {
      super(operator);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitChrUnaryExpressionNode(this);
    }
  }

  /* List size unary expression node implementation */
  public static class ListSizeExpressionNode extends UnaryExpressionNode {

    private VariableDeclaration varDeclaration;

    public ListSizeExpressionNode(UnaryOper operator) {
      super(operator);
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitListSizeExpressionNode(this);
    }

    public VariableDeclaration getVarDeclaration() {
      return varDeclaration;
    }

    public void setVarDeclaration(VariableDeclaration varDeclaration) {
      this.varDeclaration = varDeclaration;
    }
  }

  /* Pair element node implementation (fst a, snd b, etc.). The type is used
     during semantic analysis and code generation */
  public static class PairElement extends ASTBasicNode
      implements LeftHandSideAssignment, RightHandSideAssignment {

    private Type type;
    private ExpressionNode pairExpression;
    private IdentifierNode pairIdentifier;

    public PairElement(ExpressionNode pairExpression, Type type) {
      this.pairExpression = pairExpression;
      this.type = type;
    }

    public Type getType() {
      return type;
    }

    public ExpressionNode getPairExpression() {
      return pairExpression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitPairElement(this);
    }

    public IdentifierNode getPairIdentifier() {
      return pairIdentifier;
    }

    public void setPairIdentifier(IdentifierNode pairIdentifier) {
      this.pairIdentifier = pairIdentifier;
    }

    public void setPairExpression(ExpressionNode pairExpression) {
      this.pairExpression = pairExpression;
    }

    public enum Type {
      FST,
      SND
    }
  }

  /* ArrayLiteral node implementation. The type is used during code
     generation */
  public static class ArrayLiteral extends ExpressionCollection
      implements RightHandSideAssignment {

    private ArrayType type;

    public ArrayLiteral(ExpressionNode[] expressionNodes) {
      super(expressionNodes);
    }

    public ArrayType getType() {
      return type;
    }

    public void setType(ArrayType type) {
      this.type = type;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitArrayLiteral(this);
    }
  }

  /* New list implementation. Just there to create a brand new list. */
  public static class NewListStatement implements RightHandSideAssignment {

    public NewListStatement() {}

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitNewListStatement(this);
    }
  }

  /* New pair node implementation. The types of the fst and snd of the pair
     are used during code generation */
  public static class NewPairStatement implements RightHandSideAssignment {

    private ExpressionNode firstExpression;
    private ExpressionNode secondExpression;
    private Type firstExpressionType;
    private Type secondExpressionType;

    public NewPairStatement(ExpressionNode firstExpression,
        ExpressionNode secondExpression) {
      this.firstExpression = firstExpression;
      this.secondExpression = secondExpression;
    }

    public ExpressionNode getFirstExpression() {
      return firstExpression;
    }

    public ExpressionNode getSecondExpression() {
      return secondExpression;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitNewPairStatement(this);
    }

    public void setTypes(Type firstExpressionType, Type secondExpressionType) {
      this.firstExpressionType = firstExpressionType;
      this.secondExpressionType = secondExpressionType;
    }

    public Type getFirstExpressionType() {
      return firstExpressionType;
    }

    public Type getSecondExpressionType() {
      return secondExpressionType;
    }

    public void setFirstExpression(ExpressionNode firstExpression) {
      this.firstExpression = firstExpression;
    }

    public void setSecondExpression(ExpressionNode secondExpression) {
      this.secondExpression = secondExpression;
    }
  }

  /* The CallStatement node implementation. Stores the function name and
     the arguments passes to the function */
  public static class CallStatement extends BodyNode
      implements RightHandSideAssignment {

    private IdentifierNode functionIdentifier;
    private ArgList argumentList;
    /* Used for function overloading */
    private Type expectedReturnType;
    private FunctionEntry functionEntry;

    public CallStatement(IdentifierNode functionIdentifier) {
      this.functionIdentifier = functionIdentifier;
    }

    public CallStatement(IdentifierNode functionIdentifier,
        ArgList argumentList) {
      this.functionIdentifier = functionIdentifier;
      this.argumentList = argumentList;
    }

    public IdentifierNode getFunctionIdentifier() {
      return functionIdentifier;
    }

    public ArgList getArgumentList() {
      return argumentList;
    }

    public FunctionEntry getFunctionEntry() {
      return functionEntry;
    }

    public void setFunctionEntry(FunctionEntry functionEntry) {
      this.functionEntry = functionEntry;
    }

    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return visitor.visitCallStatement(this);
    }

    /* Used for function overloading */
    public Type getExpectedReturnType() {
      return expectedReturnType;
    }

    public void setExpectedReturnType(Type expectedReturnType) {
      this.expectedReturnType = expectedReturnType;
    }
  }
}