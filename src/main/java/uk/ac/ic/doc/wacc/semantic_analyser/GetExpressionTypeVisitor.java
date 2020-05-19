package uk.ac.ic.doc.wacc.semantic_analyser;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.error.CompileError;
import uk.ac.ic.doc.wacc.error.SemanticErrors;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static uk.ac.ic.doc.wacc.ast.ASTNodes.*;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.*;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.EQ;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BinExpressionNode.BinaryOper.NOT_EQ;

/*  If it can get the type without a semantic error,
    it will return the type of the expression.
    If Semantic Error occurs during determination, will return null
 */
public class GetExpressionTypeVisitor extends ASTBaseVisitor<Type> {

  private final List<CompileError> errorList;

  private SymbolTable<VariableDeclaration> symbolTable;
  private FunctionSymbolTable<FunctionEntry> functionSymbolTable;

  public GetExpressionTypeVisitor(List<CompileError> errorList,
      SymbolTable<VariableDeclaration> symbolTable,
      FunctionSymbolTable<FunctionEntry>
          functionSymbolTable) {
    this.errorList = errorList;
    this.symbolTable = symbolTable;
    this.functionSymbolTable = functionSymbolTable;
  }

  @Override
  public Type visitAnyType(AnyType node) {
    return node;
  }

  @Override
  public Type visitPair(Pair node) {
    return node;
  }

  @Override
  public Type visitBaseType(BaseType node) {
    return node;
  }

  @Override
  public Type visitArrayType(ArrayType node) {
    return node;
  }

  @Override
  public Type visitListType(ListType node) {
    return node;
  }

  @Override
  public Type visitPairType(PairType node) {
    return node;
  }

  @Override
  public Type visitIntNode(IntNode node) {
    return new BaseType(INT);
  }

  @Override
  public Type visitBoolNode(BoolNode node) {
    return new BaseType(BOOL);
  }

  @Override
  public Type visitCharNode(CharNode node) {
    return new BaseType(CHAR);
  }

  @Override
  public Type visitStringNode(StringNode node) {
    return new BaseType(STRING);
  }

  @Override
  public Type visitPairLiterNode(PairLiterNode node) {
    return new Pair();
  }

  @Override
  public Type visitArrayElement(ArrayElement node) {
    /* Checks that the variable exists in the symbol table */
    Optional<VariableDeclaration> optionalVariableDeclaration =
        symbolTable.get(node.getIdentifierNode().getName());
    if (optionalVariableDeclaration.isEmpty()) {
      ParserRuleContext ctx = node.getIdentifierNode().getCtx();
      SemanticErrors.undeclaredVarError(errorList, node.getIdentifierNode(), ctx,
          node.getIdentifierNode().getName());
      return null;
    }

    Type type = optionalVariableDeclaration.get().getType();
    node.getIdentifierNode().setVariableDeclaration(optionalVariableDeclaration.get());

    if (type instanceof BaseType) {
      ParserRuleContext ctx = node.getIdentifierNode().getCtx();
      SemanticErrors.specialError(errorList, node.getIdentifierNode(), ctx,
          "Expected an array, but was actually a " + type);
      return null;
    }

    final BaseType expectedExpressionType = new BaseType(INT);

        /*  Get all the expression nodes of the array element
            their expected type is INT for it to valid.
         */
    ExpressionNode[] expressionNodes = node.getExpressionNodes();

    for (int i = 0; i < node.getExpressionNodes().length; i++) {
      /* For each expression node check that it's type is indeed INT */
      Type actualExpressionType = expressionNodes[i].visit(this);
      ParserRuleContext ctx = expressionNodes[i].getCtx();

      if (actualExpressionType != null
          && !actualExpressionType.equals(expectedExpressionType)) {
        SemanticErrors.typeMismatchError(errorList, expressionNodes[i],
            actualExpressionType, expectedExpressionType, ctx);
        return null;
      }

      /* Then check that this is still a valid array access */
      if (type instanceof ArrayType) {
                /* If it is then the return type will be the subtype of
                   the array */
        type = ((ArrayType) type).getType();
      } else {
        /* Otherwise we are trying to access a non-existent dimension */
        SemanticErrors.specialError(errorList, expressionNodes[i], ctx,
            "Access invalid dimension of array.");
        return null;
      }
    }

    /* We now have the correct type so we can return it */
    return type;
  }

  @Override
  public Type visitArithmeticExpressionNode(ArithmeticExpressionNode node) {
    ExpressionNode left = node.getLeft();
    Type leftType = left.visit(this);
    ExpressionNode right = node.getRight();
    Type rightType = right.visit(this);
    final Type expectedType = new BaseType(INT);

    /* Both sides of an arithmetic expression should be of type integer */
    if (leftType == null || rightType == null) {
      return null;
    }

    if (!leftType.equals(expectedType)) {
      ParserRuleContext ctx = left.getCtx();
      SemanticErrors.typeMismatchError(errorList, left, leftType, expectedType, ctx);
      return null;
    }
    if (!rightType.equals(expectedType)) {
      ParserRuleContext ctx = right.getCtx();
      SemanticErrors.typeMismatchError(errorList, right, rightType, expectedType,
          ctx);
      return null;
    }

    return expectedType;
  }

  @Override
  public Type visitCompareExpressionNode(CompareExpressionNode node) {
    ExpressionNode left = node.getLeft();
    Type leftType = left.visit(this);
    ExpressionNode right = node.getRight();
    Type rightType = right.visit(this);

    if (leftType == null || rightType == null) {
      return null;
    }

    /* Both sides of the expression must be of the same type to compare */
    if (!leftType.equals(rightType)) {
      ParserRuleContext ctx = left.getCtx();
      SemanticErrors.typeMismatchError(errorList, left, leftType, rightType, ctx);
      return null;
    }

        /* EQ and NOT_EQ can be done on any type. Any other comparison can only
           be done on Base Types */
    if (!((node.getOperator() == EQ) || (node.getOperator() == NOT_EQ))) {
      if (!(leftType instanceof BaseType)) {
        ParserRuleContext ctx = left.getCtx();
        SemanticErrors.specialError(errorList, left, ctx,
            "Cannot compare addresses.");
        return null;
      }
      if (!(rightType instanceof BaseType)) {
        ParserRuleContext ctx = right.getCtx();
        SemanticErrors.specialError(errorList, right, ctx,
            "Cannot compare addresses.");
        return null;
      }
    }

    node.setType(rightType);

    return new BaseType(BOOL);
  }

  @Override
  public Type visitBoolExpressionNode(BoolExpressionNode node) {
    ExpressionNode left = node.getLeft();
    Type leftType = left.visit(this);
    ExpressionNode right = node.getRight();
    Type rightType = right.visit(this);
    final Type expectedType = new BaseType(BOOL);

    /* Both sides of the expression must be of type boolean */
    if (leftType == null || rightType == null) {
      return null;
    }

    if (!leftType.equals(expectedType)) {
      ParserRuleContext ctx = left.getCtx();
      SemanticErrors.typeMismatchError(errorList, left, leftType, expectedType, ctx);
      return null;
    }
    if (!rightType.equals(expectedType)) {
      ParserRuleContext ctx = right.getCtx();
      SemanticErrors.typeMismatchError(errorList, right, rightType, expectedType,
          ctx);
      return null;
    }
    return expectedType;
  }

  @Override
  public Type visitGetFromListNode(GetFromListNode node) {

    /* Checks that the variable is a list */
    Type listActualType = node.getIdentifier().visit(this);
    Type listExpectedType = new ListType(new AnyType());

    if(!listActualType.equals(listExpectedType)) {
      ParserRuleContext ctx = node.getIdentifier().getCtx();
      SemanticErrors.typeMismatchError(errorList, node.getIdentifier(),
              listActualType, listExpectedType, ctx);
      return null;
    }

    /* Checks that the expression is an integer */
    Type expressionActualType = node.getExpression().visit(this);
    Type expressionExpectedType = new BaseType(INT);

    if(!expressionActualType.equals(expressionExpectedType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node.getExpression(),
              expressionActualType, expressionExpectedType, ctx);
      return null;
    }

    /* The type of this will be the type of the list */
    Type actualType = ((ListType) listActualType).getType();
    node.setType(actualType);
    return actualType;
  }

  @Override
  public Type visitListContainsNode(ListContainsNode node) {
    /* Checks that the variable is a list */
    Type listActualType = node.getIdentifier().visit(this);
    Type listExpectedType = new ListType(new AnyType());

    if(!listActualType.equals(listExpectedType)) {
      ParserRuleContext ctx = node.getIdentifier().getCtx();
      SemanticErrors.typeMismatchError(errorList, node.getIdentifier(),
              listActualType, listExpectedType, ctx);
      return null;
    }

    /* Checks that the expression matches the type of the list */
    Type expressionActualType = node.getExpression().visit(this);
    Type expressionExpectedType = ((ListType) listActualType).getType();

    if(!expressionActualType.equals(expressionExpectedType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node.getExpression(),
              expressionActualType, expressionExpectedType, ctx);
      return null;
    }

    return new BaseType(BOOL);
  }

  @Override
  public Type visitIdentifierNode(IdentifierNode node) {
    /* The ident must exist in the symbol table */
    Optional<VariableDeclaration> variableDeclaration = symbolTable.get(node.getName());
    if (variableDeclaration.isEmpty()) {
      ParserRuleContext ctx = node.getCtx();
      SemanticErrors.undeclaredVarError(errorList, node, ctx, node.getName());
      return null;
    }
    node.setVariableDeclaration(variableDeclaration.get());
    return variableDeclaration.get().getType();
  }

  @Override
  public Type visitNotUnaryExpressionNode(NotUnaryExpressionNode node) {
    Type actualType = node.getExpression().visit(this);
    final Type expectedType = new BaseType(BOOL);

    /* Checks that the expression is a boolean */
    if (actualType == null) {
      return null;
    }

    if (!actualType.equals(expectedType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node, actualType, expectedType,
          ctx);
      return null;
    }
    return expectedType;
  }

  @Override
  public Type visitBitNotUnaryExpressionNode(BitNotUnaryExpressionNode node) {
    Type actualType = node.getExpression().visit(this);
    final Type expectedType = new BaseType(INT);
    if (actualType == null) {
      return null;
    }
    /* Check if the expression is an integer */
    if (!actualType.equals(expectedType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node, actualType, expectedType,
              ctx);
      return null;
    }
    return expectedType;

  }

  @Override
  public Type visitNegUnaryExpressionNode(NegUnaryExpressionNode node) {
    Type actualType = node.getExpression().visit(this);
    final Type expectedType = new BaseType(INT);

    /* Checks that the expression is an integer */
    if (actualType == null) {
      return null;
    }

    if (!actualType.equals(expectedType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node, actualType, expectedType,
          ctx);
      return null;
    }
    return expectedType;
  }

  @Override
  public Type visitLenUnaryExpressionNode(LenUnaryExpressionNode node) {
    Type actualType = node.getExpression().visit(this);

    /* Checks that the expression is an Array */
    if (actualType == null) {
      return null;
    }

    if (!(actualType instanceof ArrayType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.specialError(errorList, node, ctx,
          "Expected an array, but was actually " + actualType);
      return null;
    }

    node.setIdentifierNode((IdentifierNode) node.getExpression());
    return new BaseType(INT);
  }

  @Override
  public Type visitOrdUnaryExpressionNode(OrdUnaryExpressionNode node) {
    Type actualType = node.getExpression().visit(this);
    final Type expectedType = new BaseType(CHAR);

    /* Checks that the expression is a character */
    if (actualType == null) {
      return null;
    }

    if (!actualType.equals(expectedType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node, actualType, expectedType,
          ctx);
      return null;
    }
    return new BaseType(INT);
  }

  @Override
  public Type visitChrUnaryExpressionNode(ChrUnaryExpressionNode node) {
    Type actualType = node.getExpression().visit(this);
    final Type expectedType = new BaseType(INT);

    /* Checks that the expression is an integer */
    if (actualType == null) {
      return null;
    }

    if (!actualType.equals(expectedType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node, actualType, expectedType,
          ctx);
      return null;
    }
    return new BaseType(CHAR);
  }

  @Override
  public Type visitListSizeExpressionNode(ListSizeExpressionNode node) {
    ExpressionNode expression = node.getExpression();
    Type actualType = expression.visit(this);
    Type expectedType = new ListType(new AnyType());

    if(!(expression instanceof IdentifierNode)) {
      SemanticErrors.specialError(errorList, node, node.getCtx(),
              "Expected a variable.");
      return null;
    }

    /* Checks that the expression is a list variable */
    if (actualType == null) {
      return null;
    }

    if(!actualType.equals(expectedType)) {
      ParserRuleContext ctx = expression.getCtx();
      SemanticErrors.typeMismatchError(errorList, node, actualType, expectedType,
              ctx);
      return null;
    }

    node.setVarDeclaration(((IdentifierNode) expression).
            getVariableDeclaration());

    return new BaseType(INT);
  }

  @Override
  public Type visitPairElement(PairElement node) {
    ExpressionNode expression = node.getPairExpression();
    ParserRuleContext ctx = node.getCtx();

    /* Checks that the expression is a variable */
    if (!(expression instanceof IdentifierNode)) {
      Type expressionType = expression.visit(this);
      if (expressionType instanceof Pair) {
        SemanticErrors.specialError(errorList, expression, ctx,
            "Expected a variable, instead got null");
      } else {
        SemanticErrors.specialError(errorList, expression, ctx,
            "Expected a variable, instead got " + expressionType);
      }
      return null;
    }
    IdentifierNode var = (IdentifierNode) expression;
    /* Checks the variable is declared */
    Optional<VariableDeclaration> optionalVariableDeclaration = symbolTable.get(var.getName());
    if (optionalVariableDeclaration.isEmpty()) {
      SemanticErrors.undeclaredVarError(errorList, var, ctx, var.getName());
      return null;
    }

    /* Checks that we do indeed have a pair */
    final Pair expectedType = new Pair();
    Type varType = optionalVariableDeclaration.get().getType();
    if (!varType.equals(expectedType)) {
      SemanticErrors.typeMismatchError(errorList, expression, varType, expectedType,
          ctx);
      return null;
    }

    var.setVariableDeclaration(optionalVariableDeclaration.get());
    node.setPairIdentifier(var);

        /* If we have a pair, we get the correct element depending on
           FST or SND enum */
    if (node.getType() == PairElement.Type.FST) {
      return ((PairType) varType).getFirstElemType();
    } else {
      return ((PairType) varType).getSecondElemType();
    }

  }

  @Override
  public Type visitArrayLiteral(ArrayLiteral node) {
    ExpressionNode[] expressionNodes = node.getExpressionNodes();

    /* An empty array literal can match with any type of array */
    if (expressionNodes.length == 0) {
      return new ArrayType(new AnyType());
    }

        /* All the elements of the array literal must match. If they do, that
           is the type of the array */
    Type type = expressionNodes[0].visit(this);
    for (int i = 1; i < expressionNodes.length; i++) {
      Type nextType = expressionNodes[i].visit(this);
      if (nextType == null) {
        return null;
      }
      if (!type.equals(nextType)) {
        ParserRuleContext ctx = expressionNodes[i].getCtx();
        SemanticErrors.specialError(errorList, expressionNodes[i], ctx,
            "Different types used in array declaration");
        return null;
      }
    }
    ArrayType arrayType = new ArrayType(type);
    node.setType(arrayType);

    return arrayType;
  }

  @Override
  public Type visitNewPairStatement(NewPairStatement node) {
    Type firstExpType = node.getFirstExpression().visit(this);
    Type secondExpType = node.getSecondExpression().visit(this);

    node.setTypes(firstExpType, secondExpType);

    if (firstExpType == null || secondExpType == null) {
      return null;
    }

    return new PairType(firstExpType, secondExpType);
  }

  @Override
  public Type visitNewListStatement(NewListStatement node) {
    return new ListType(new AnyType());
  }

  @Override
  public Type visitCallStatement(CallStatement node) {
    /* Construct a list of the argument types */
    List<Type> argsTypes = new ArrayList<>();
    if(node.getArgumentList() != null) {
      for (ExpressionNode expressionNode :
              node.getArgumentList().getExpressionNodes()) {
        argsTypes.add(expressionNode.visit(this));
      }
    }

    /* Gets the function entries associated with this function name */
    List<FunctionEntry> entries = functionSymbolTable.getFuncListWithName(
            node.getFunctionIdentifier().getName());
    if(entries == null) {
      ParserRuleContext ctx = node.getCtx();
      SemanticErrors.undeclaredFuncError(errorList, node, ctx,
              node.getFunctionIdentifier().getName());
      return null;
    }

    /* Creates a dummy function entry of the call node and checks
       if there is a matching one with the same function name.  */
    FunctionEntry functionEntryCheck =
            new FunctionEntry(node.getExpectedReturnType(), argsTypes);
    boolean match = false;
    for(FunctionEntry entry : entries) {
      if(functionEntryCheck.equals(entry)) {
        match = true;
        node.setFunctionEntry(entry);
        break;
      }
    }

    if(!match) {
      ParserRuleContext ctx = node.getCtx();
      SemanticErrors.specialError(errorList, node, ctx,
              "Wrong type signature received!");
      return null;
    }

    return node.getExpectedReturnType();
  }
}
