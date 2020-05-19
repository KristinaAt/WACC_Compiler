package uk.ac.ic.doc.wacc.ast;

import uk.ac.ic.doc.wacc.ast.ASTNodes.*;
import uk.ac.ic.doc.wacc.antlr.WACCParserBaseVisitor;

import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.VOID;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.AND;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.BIT_AND;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.BIT_EOR;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.BIT_OR;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.DIVIDE;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.EQ;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.GREATER_EQ;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.GREATER_THAN;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.LESS_EQ;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.LESS_THAN;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.MOD;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.MULTIPLY;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.NOT_EQ;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.OR;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.PLUS;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.*;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.SHIFT_L;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.SHIFT_R;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.PairElement.Type.FST;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.PairElement.Type.SND;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.UnaryExpressionNode.UnaryOper.CHR;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.UnaryExpressionNode.UnaryOper.LEN;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.UnaryExpressionNode.UnaryOper.MINUS;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.UnaryExpressionNode.UnaryOper.NOT;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.UnaryExpressionNode.UnaryOper.ORD;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.UnaryExpressionNode.UnaryOper.BIT_NOT;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.UnaryExpressionNode.UnaryOper.LIST_SIZE;

import static uk.ac.ic.doc.wacc.antlr.WACCParser.*;

public class ASTGenerator extends WACCParserBaseVisitor<ASTNode> {

  @Override
  public ASTNode visitProg(ProgContext ctx) {
    /* Can ignore the begin and end node as the scope currently does not
       matter */
    int numFuncs = ctx.func().size() + ctx.voidFunc().size();

    /* Now need to create function nodes for both normal functions and void
       functions. Done in two back to back for loops. */
    FunctionNode[] functionNodes = new FunctionNode[numFuncs];
    int i = 0;
    for (; i < ctx.func().size(); i++) {
      functionNodes[i] = (FunctionNode) visit(ctx.func(i));
    }
    for (; i < numFuncs; i++) {
      functionNodes[i] = (FunctionNode) visit(ctx.voidFunc(i));
    }
    ProgNode progNode = new ProgNode((BodyNode) visit(ctx.stat()),
        functionNodes);
    progNode.setCtx(ctx);
    return progNode;
  }

  @Override
  public ASTNode visitVoidFunc(VoidFuncContext ctx) {
    /* A void function is guaranteed to have return type void */
    FunctionNode functionNode;
    Type type = new BaseType(VOID);
    IdentifierNode identifier = (IdentifierNode) visit(ctx.ident());
    BodyNode body = (BodyNode) visit(ctx.stat());

    if (ctx.paramList() == null) {
      functionNode = new FunctionNode(type, identifier, body);
    } else {
      ParamList paramList = (ParamList) visit(ctx.paramList());
      functionNode = new FunctionNode(type, identifier, body, paramList);
    }

    functionNode.setCtx(ctx);
    return functionNode;
  }

  @Override
  public ASTNode visitFunc(FuncContext ctx) {
    FunctionNode functionNode;
    Type type = (Type) visit(ctx.type());
    IdentifierNode identifier = (IdentifierNode) visit(ctx.ident());
    BodyNode body = (BodyNode) visit(ctx.func_body());

    if (ctx.paramList() == null) {
      functionNode = new FunctionNode(type, identifier, body);
    } else {
      ParamList paramList = (ParamList) visit(ctx.paramList());
      functionNode = new FunctionNode(type, identifier, body, paramList);
    }

    functionNode.setCtx(ctx);
    return functionNode;
  }

  @Override
  public ASTNode visitCompoundFuncEnd(CompoundFuncEndContext ctx) {
    CompoundNode compoundNode = new CompoundNode(
        (BodyNode) visit(ctx.stat()), (BodyNode) visit(ctx.func_end()));
    compoundNode.setCtx(ctx);
    return compoundNode;
  }

  @Override
  public ASTNode visitFuncEndReturn(FuncEndReturnContext ctx) {
    ReturnNode returnNode = new ReturnNode(
        (ExpressionNode) visit(ctx.expr()));
    returnNode.setCtx(ctx);
    return returnNode;
  }

  @Override
  public ASTNode visitFuncEndExit(FuncEndExitContext ctx) {
    ExitNode exitNode = new ExitNode((ExpressionNode) visit(ctx.expr()));
    exitNode.setCtx(ctx);
    return exitNode;
  }

  @Override
  public ASTNode visitFuncEnd(FuncEndContext ctx) {
    return visit(ctx.func_end());
  }

  @Override
  public ASTNode visitIfStatementFuncEnd(IfStatementFuncEndContext ctx) {
    IfStatement ifStatement = new IfStatement(
        (ExpressionNode) visit(ctx.expr()),
        (BodyNode) visit(ctx.func_body(0)),
        (BodyNode) visit(ctx.func_body(1)));
    ifStatement.setCtx(ctx);

    if (ctx.stat() != null) {
      CompoundNode compoundNode =
          new CompoundNode((BodyNode) visit(ctx.stat()), ifStatement);
      compoundNode.setCtx(ctx);

      return compoundNode;
    }

    return ifStatement;
  }

  @Override
  public ASTNode visitParam(ParamContext ctx) {
    ParamNode paramNode = new ParamNode((Type) visit(ctx.type()),
        (IdentifierNode) visit(ctx.ident()));
    paramNode.setCtx(ctx);
    return paramNode;
  }

  @Override
  public ASTNode visitParamList(ParamListContext ctx) {
    int numParams = ctx.param().size();
    ParamNode[] paramNodes = new ParamNode[numParams];
    for (int i = 0; i < numParams; i++) {
      paramNodes[i] = (ParamNode) visit(ctx.param(i));
    }
    return new ParamList(paramNodes);
  }


  @Override
  public ASTNode visitBegin(BeginContext ctx) {
    BeginNode beginNode = new BeginNode((BodyNode) visit(ctx.stat()));
    beginNode.setCtx(ctx);

    return beginNode;
  }

  @Override
  public ASTNode visitStatementCall(StatementCallContext ctx) {
    /* You can only call void function statements, not regular statements
       as these are the only non-return functions. */
    CallStatement callStatement;
    if (ctx.argList() != null) {
      callStatement = new CallStatement((
              IdentifierNode) visit(ctx.ident()),
              (ArgList) visit(ctx.argList()));
    } else {
      callStatement = new CallStatement(
              (IdentifierNode) visit(ctx.ident()));
    }
    callStatement.setExpectedReturnType(new BaseType(VOID));
    callStatement.setCtx(ctx);
    return callStatement;
  }

  @Override
  public ASTNode visitListStat(ListStatContext ctx) {
    return visit(ctx.genericListStat());
  }

  @Override
  public ASTNode visitAddToList(AddToListContext ctx) {
    IdentifierNode identifier = (IdentifierNode) visit(ctx.ident());
    ExpressionNode expression = (ExpressionNode) visit(ctx.expr());
    AddToListNode addToListNode = new AddToListNode(identifier, expression);

    addToListNode.setCtx(ctx);
    return addToListNode;
  }

  @Override
  public ASTNode visitRemoveFromList(RemoveFromListContext ctx) {
    IdentifierNode identifier = (IdentifierNode) visit(ctx.ident());
    ExpressionNode expression = (ExpressionNode) visit(ctx.expr());
    RemoveFromListNode removeFromListNode =
            new RemoveFromListNode(identifier, expression);

    removeFromListNode.setCtx(ctx);
    return removeFromListNode;
  }

  @Override
  public ASTNode visitGetFromList(GetFromListContext ctx) {
    IdentifierNode identifier = (IdentifierNode) visit(ctx.ident());
    ExpressionNode expression = (ExpressionNode) visit(ctx.expr());

    GetFromListNode getFromListNode =
            new GetFromListNode(identifier, expression);

    getFromListNode.setCtx(ctx);
    return getFromListNode;
  }

  @Override
  public ASTNode visitListContains(ListContainsContext ctx) {
    IdentifierNode identifier = (IdentifierNode) visit(ctx.ident());
    ExpressionNode expression = (ExpressionNode) visit(ctx.expr());

    ListContainsNode listContainsNode =
            new ListContainsNode(identifier, expression);

    listContainsNode.setCtx(ctx);
    return listContainsNode;
  }

  @Override
  public ASTNode visitCompound(CompoundContext ctx) {
    CompoundNode compoundNode = new CompoundNode(
        (BodyNode) visit(ctx.stat(0)), (BodyNode) visit(ctx.stat(1)));
    compoundNode.setCtx(ctx);
    return compoundNode;
  }

  @Override
  public ASTNode visitWhile(WhileContext ctx) {
    WhileNode whileNode = new WhileNode(
        (ExpressionNode) visit(ctx.expr()), (BodyNode) visit(ctx.stat()));
    whileNode.setCtx(ctx);
    return whileNode;
  }

  @Override
  public ASTNode visitExit(ExitContext ctx) {
    ExitNode exitNode = new ExitNode((ExpressionNode) visit(ctx.expr()));
    exitNode.setCtx(ctx);
    return exitNode;
  }

  @Override
  public ASTNode visitSkip(SkipContext ctx) {
    return new SkipNode();
  }

  @Override
  public ASTNode visitIf(IfContext ctx) {
    IfStatement ifStatement = new IfStatement(
        (ExpressionNode) visit(ctx.expr()),
        (BodyNode) visit(ctx.stat(0)),
        (BodyNode) visit(ctx.stat(1)));
    ifStatement.setCtx(ctx);
    return ifStatement;
  }

  @Override
  public ASTNode visitAssignVar(AssignVarContext ctx) {
    AssignmentNode assignmentNode = new AssignmentNode(
            (LeftHandSideAssignment) visit(ctx.assignLhs()),
            (RightHandSideAssignment) visit(ctx.assignRhs()));
    assignmentNode.setCtx(ctx);
    return assignmentNode;
  }

  @Override
  public ASTNode visitAssignNewVar(AssignNewVarContext ctx) {
    RightHandSideAssignment rhs =
            (RightHandSideAssignment) visit(ctx.assignRhs());
    Type type = (Type) visit(ctx.type());
    IdentifierNode identifier = (IdentifierNode) visit(ctx.ident());
    DeclarationNode declarationNode = new DeclarationNode(
        type, identifier, rhs);
    declarationNode.setCtx(ctx);

    if(rhs instanceof CallStatement) {
      ((CallStatement) rhs).setExpectedReturnType(type);
    }

    return declarationNode;
  }

  @Override
  public ASTNode visitAssignLhs(AssignLhsContext ctx) {
    return visit(ctx.children.get(0));
  }

  @Override
  public ASTNode visitAssignExpr(AssignExprContext ctx) {
    return super.visitAssignExpr(ctx);
  }

  @Override
  public ASTNode visitAssignArrayLiter(AssignArrayLiterContext ctx) {
    return super.visitAssignArrayLiter(ctx);
  }

  @Override
  public ASTNode visitAssignNewPair(AssignNewPairContext ctx) {
    return new NewPairStatement((ExpressionNode) visit(ctx.expr(0)),
        (ExpressionNode) visit(ctx.expr(1)));
  }

  @Override
  public ASTNode visitAssignNewList(AssignNewListContext ctx) {
    return new NewListStatement();
  }

  @Override
  public ASTNode visitAssignPairElem(AssignPairElemContext ctx) {
    return super.visitAssignPairElem(ctx);
  }

  @Override
  public ASTNode visitAssignCall(AssignCallContext ctx) {
    CallStatement callStatement;
    if (ctx.argList() != null) {
      callStatement = new CallStatement((
          IdentifierNode) visit(ctx.ident()),
          (ArgList) visit(ctx.argList()));
    } else {
      callStatement = new CallStatement(
          (IdentifierNode) visit(ctx.ident()));
    }
    callStatement.setCtx(ctx);
    return callStatement;
  }

  @Override
  public ASTNode visitPrint(PrintContext ctx) {
    PrintNode printNode = new PrintNode((ExpressionNode) visit(ctx.expr()));
    printNode.setCtx(ctx);
    return printNode;
  }

  @Override
  public ASTNode visitPrintLn(PrintLnContext ctx) {
    PrintLnNode printlnNode = new PrintLnNode(
        (ExpressionNode) visit(ctx.expr()));
    printlnNode.setCtx(ctx);
    return printlnNode;
  }

  @Override
  public ASTNode visitReturn(ReturnContext ctx) {
    ReturnNode returnNode = new ReturnNode(
        (ExpressionNode) visit(ctx.expr()));
    returnNode.setCtx(ctx);
    return returnNode;
  }

  @Override
  public ASTNode visitFree(FreeContext ctx) {
    FreeNode freeNode = new FreeNode((ExpressionNode) visit(ctx.expr()));
    freeNode.setCtx(ctx);
    return freeNode;
  }

  @Override
  public ASTNode visitRead(ReadContext ctx) {
    ReadNode readNode = new ReadNode(
        (LeftHandSideAssignment) visit(ctx.assignRhs()));
    readNode.setCtx(ctx);
    return readNode;
  }

  @Override
  public ASTNode visitPair(PairContext ctx) {
    return new PairLiterNode();
  }

  @Override
  public ASTNode visitTypeBase(TypeBaseContext ctx) {
    return visitBaseType(ctx.baseType());
  }

  @Override
  public ASTNode visitBaseType(BaseTypeContext ctx) {
    BaseType baseType = null;
    String type = ctx.getText();
    switch (type) {
      case ("int"):
        baseType = new BaseType(BaseType.Category.INT);
        break;
      case ("bool"):
        baseType = new BaseType(BaseType.Category.BOOL);
        break;
      case ("char"):
        baseType = new BaseType(BaseType.Category.CHAR);
        break;
      case ("string"):
        baseType = new BaseType(BaseType.Category.STRING);
        break;
    }

    return baseType;
  }

  @Override
  public ASTNode visitArrayType(ArrayTypeContext ctx) {
    return new ArrayType((Type) visit(ctx.type()));
  }

  @Override
  public ASTNode visitTypeArray(TypeArrayContext ctx) {
    return new ArrayType((Type) visit(ctx.type()));
  }

  @Override
  public ASTNode visitTypeList(TypeListContext ctx) {
    Type type = (Type) visit(ctx.genericType());
    return new ListType(type);
  }

  @Override
  public ASTNode visitGenericType(GenericTypeContext ctx) {
    return visit(ctx.type());
  }

  @Override
  public ASTNode visitPairType(PairTypeContext ctx) {
    return new PairType((Type) visit(ctx.pairElemType(0)),
        (Type) visit(ctx.pairElemType(1)));
  }

  @Override
  public ASTNode visitTypePair(TypePairContext ctx) {
    return visit(ctx.pairType());
  }

  @Override
  public ASTNode visitPairElemType(PairElemTypeContext ctx) {
    if (ctx.children.get(0).getText().equals("pair")) {
      return new Pair();
    }
    return visit(ctx.children.get(0));
  }


  @Override
  public ASTNode visitInteger(IntegerContext ctx) {
    String value;
    if (ctx.children.size() > 1) {
      String sign = ctx.children.get(0).getText();
      value = sign + ctx.children.get(1).getText();
    } else {
      value = ctx.children.get(0).getText();
    }
    IntNode intNode = new IntNode(Integer.parseInt(value));
    intNode.setCtx(ctx);
    return intNode;
  }

  @Override
  public ASTNode visitBoolean(BooleanContext ctx) {
    String value = ctx.boolLiter().getText();
    BoolNode boolNode = new BoolNode(value);
    boolNode.setCtx(ctx);

    return boolNode;
  }

  @Override
  public ASTNode visitCharacter(CharacterContext ctx) {
    String value = ctx.charLiter().getText();
    Character character = 0;
    if(value.length() == 3) {
      character = value.charAt(1);
    } else {
      switch (value.charAt(2)) {
        case '0':
          character = 0;
          break;
        case 'b':
          character = 8;
          break;
        case 't':
          character = 9;
          break;
        case 'n':
          character = 10;
          break;
        case 'f':
          character = 12;
          break;
        case 'r':
          character = 13;
          break;
        case '\"':
          character = 34;
          break;
        case '\'':
          character = 39;
          break;
        case '\\':
          character = 92;
          break;
      }
    }

    CharNode charNode = new CharNode(character);
    charNode.setCtx(ctx);
    return charNode;
  }

  @Override
  public ASTNode visitString(StringContext ctx) {
    StringNode stringNode = new StringNode(ctx.strLiter().getText());
    stringNode.setCtx(ctx);
    return stringNode;
  }

  @Override
  public ASTNode visitBracketedExpression(BracketedExpressionContext ctx) {
    return (visit(ctx.expr()));
  }

  @Override
  public ASTNode visitPairLiter(PairLiterContext ctx) {
    return new PairLiterNode();
  }

  @Override
  public ASTNode visitArrayElem(ArrayElemContext ctx) {
    int size = ctx.expr().size();
    ExpressionNode[] expressionNodes = new ExpressionNode[size];

    for (int i = 0; i < size; i++) {
      ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr(i));
      expressionNodes[i] = expressionNode;
    }

    ArrayElement arrayElement = new ArrayElement(
        (IdentifierNode) visit(ctx.ident()), expressionNodes);
    arrayElement.setCtx(ctx);
    return arrayElement;
  }


  @Override
  public ASTNode visitUnaryExpression(UnaryExpressionContext ctx) {
    String sign = ctx.unaryOper().getText();
    UnaryExpressionNode node = null;

    switch (sign) {
      case ("!"):
        node = new NotUnaryExpressionNode(NOT);
        break;
      case ("-"):
        node = new NegUnaryExpressionNode(MINUS);
        break;
      case ("len"):
        node = new LenUnaryExpressionNode(LEN);
        break;
      case ("ord"):
        node = new OrdUnaryExpressionNode(ORD);
        break;
      case ("chr"):
        node = new ChrUnaryExpressionNode(CHR);
        break;
      case("~"):
        node = new BitNotUnaryExpressionNode(BIT_NOT);
        break;
      case("listsize"):
        node = new ListSizeExpressionNode(LIST_SIZE);
        break;
      default:
    }

    assert (node != null);

    node.setExpression((ExpressionNode) visit(ctx.expr()));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode
  visitArithmeticFstPrecedence(ArithmeticFstPrecedenceContext ctx) {
    String sign = ctx.fstPrecedenceOp().getText();
    ArithmeticExpressionNode node = null;
    switch (sign) {
      case ("/"):
        node = new ArithmeticExpressionNode(DIVIDE);
        break;
      case ("*"):
        node = new ArithmeticExpressionNode(MULTIPLY);
        break;
      case ("%"):
        node = new ArithmeticExpressionNode(MOD);
        break;
    }

    assert (node != null);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
        (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }



  @Override
  public ASTNode
  visitArithmeticSndPrecedence(ArithmeticSndPrecedenceContext ctx) {
    String sign = ctx.sndPrecedenceOp().getText();
    ArithmeticExpressionNode node = null;
    switch (sign) {
      case ("+"):
        node = new ArithmeticExpressionNode(PLUS);
        break;
      case ("-"):
        node = new ArithmeticExpressionNode(BIN_MINUS);
        break;
    }

    assert (node != null);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
            (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode
  visitArithmeticThirdPrecedence(ArithmeticThirdPrecedenceContext ctx) {
    String sign = ctx.thirdPrecedenceOp().getText();
    ArithmeticExpressionNode node = null;
    switch (sign) {
      case ("<<"):
        node = new ArithmeticExpressionNode(SHIFT_L);
        break;
      case (">>"):
        node = new ArithmeticExpressionNode(SHIFT_R);
        break;
    }

    assert (node != null);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
            (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode visitComparisonExpression(ComparisonExpressionContext ctx) {
    String sign = ctx.compareOper().getText();
    CompareExpressionNode node = null;
    switch (sign) {
      case (">"):
        node = new CompareExpressionNode(GREATER_THAN);
        break;
      case (">="):
        node = new CompareExpressionNode(GREATER_EQ);
        break;
      case ("<"):
        node = new CompareExpressionNode(LESS_THAN);
        break;
      case ("<="):
        node = new CompareExpressionNode(LESS_EQ);
        break;
      case ("=="):
        node = new CompareExpressionNode(EQ);
        break;
      case ("!="):
        node = new CompareExpressionNode(NOT_EQ);
        break;
    }

    assert (node != null);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
        (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode visitBooleanFstPrecedence(BooleanFstPrecedenceContext ctx) {
    BoolExpressionNode node = new BoolExpressionNode(AND);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
        (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode visitBooleanSndPrecedence(BooleanSndPrecedenceContext ctx) {
    BoolExpressionNode node = new BoolExpressionNode(OR);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
            (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode visitBitwiseFstPrecedence(BitwiseFstPrecedenceContext ctx) {
    ArithmeticExpressionNode node = new ArithmeticExpressionNode(BIT_AND);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
            (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode visitBitwiseSndPrecedence(BitwiseSndPrecedenceContext ctx) {
    ArithmeticExpressionNode node = new ArithmeticExpressionNode(BIT_OR);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
            (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;
  }

  @Override
  public ASTNode visitBitwiseThirdPrecedence(BitwiseThirdPrecedenceContext ctx) {
    ArithmeticExpressionNode node = new ArithmeticExpressionNode(BIT_EOR);

    node.setChildren((ExpressionNode) visit(ctx.expr(0)),
            (ExpressionNode) visit(ctx.expr(1)));
    node.setCtx(ctx);
    return node;  }

  @Override
  public ASTNode visitIdentifier(IdentifierContext ctx) {
    return visitIdent(ctx.ident());
  }

  @Override
  public ASTNode visitIdent(IdentContext ctx) {
    IdentifierNode identifierNode = new IdentifierNode(ctx.getText());
    identifierNode.setCtx(ctx);
    return identifierNode;
  }


  @Override
  public ASTNode visitArgList(ArgListContext ctx) {
    int size = ctx.expr().size();
    ExpressionNode[] expressionNodes = new ExpressionNode[size];
    for (int i = 0; i < size; i++) {
      expressionNodes[i] = (ExpressionNode) visit(ctx.expr(i));
    }
    return new ArgList(expressionNodes);
  }

  @Override
  public ASTNode visitArrayLiter(ArrayLiterContext ctx) {
    int size = ctx.expr().size();
    ExpressionNode[] expressionNodes = new ExpressionNode[size];
    for (int i = 0; i < size; i++) {
      ExpressionNode expressionNode = (ExpressionNode) visit(ctx.expr(i));
      expressionNodes[i] = expressionNode;
    }
    return new ArrayLiteral(expressionNodes);
  }

  @Override
  public ASTNode visitPairElem(PairElemContext ctx) {
    PairElement pairElement;
    ExpressionNode expression = (ExpressionNode) visit(ctx.expr());
    String type = ctx.children.get(0).getText();
    switch (type) {
      case ("fst"):
        pairElement = new PairElement(expression, FST);
        break;
      case ("snd"):
        pairElement = new PairElement(expression, SND);
        break;
      default:
        return null;
    }
    pairElement.setCtx(ctx);
    return pairElement;
  }

  @Override
  public ASTNode visitArray(ArrayContext ctx) {
    return (visit(ctx.arrayElem()));
  }
}