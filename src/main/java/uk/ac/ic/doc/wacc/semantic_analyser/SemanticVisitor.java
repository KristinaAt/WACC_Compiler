package uk.ac.ic.doc.wacc.semantic_analyser;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category;
import uk.ac.ic.doc.wacc.error.CompileError;
import uk.ac.ic.doc.wacc.error.SemanticErrors;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static uk.ac.ic.doc.wacc.ast.ASTNodes.*;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.INT;

public class SemanticVisitor extends ASTBaseVisitor<Void> {

  private final List<CompileError> errorList;

  private GetExpressionTypeVisitor getExpressionType;
  private SymbolTable<VariableDeclaration> symbolTable;
  private FunctionSymbolTable<FunctionEntry> functionSymbolTable;
  private Type returnType;

  public SemanticVisitor(List<CompileError> errorList,
      SymbolTable<VariableDeclaration> symbolTable,
      FunctionSymbolTable<FunctionEntry> functionSymbolTable) {
    this(errorList, symbolTable, functionSymbolTable, null);
  }

  public SemanticVisitor(List<CompileError> errorList,
      SymbolTable<VariableDeclaration> symbolTable,
      FunctionSymbolTable<FunctionEntry> functionSymbolTable,
      Type returnType) {
    this.errorList = errorList;
    this.symbolTable = symbolTable;
    this.functionSymbolTable = functionSymbolTable;
    this.getExpressionType = new GetExpressionTypeVisitor(errorList,
        symbolTable,
        functionSymbolTable);
    this.returnType = returnType;
  }

  @Override
  public Void visitProgNode(ProgNode node) {
    FunctionNode[] functionNodes = node.getFunctionNodes();
        /* Add each function to the function table with return types and
           argument type */
    for (FunctionNode functionNode : functionNodes) {
      addToFunctionTable(functionNode);
    }
    /* Semantically check the body of each function */
    for (FunctionNode functionNode : functionNodes) {
      semanticallyCheck(functionNode);
    }
    /* Semantically check the main body of the code */
    node.getBody().visit(this);
    return null;
  }

  @Override
  public Void visitCompoundNode(CompoundNode node) {
    /* Recursively check the two bodies of compound nodes */
    node.getLeftBody().visit(this);
    node.getRightBody().visit(this);
    return null;
  }

  @Override
  public Void visitSkipNode(SkipNode node) {
    return null;
  }

  @Override
  public Void visitIfStatement(IfStatement node) {
    Type type = node.getCondition().visit(getExpressionType);
    Type expectedType = new BaseType(Category.BOOL);

    /* The expression of the if statement must be a boolean expression */
    if (type != null) {
      if (!type.equals(expectedType)) {
        SemanticErrors.typeMismatchError(errorList, node.getCondition(), type,
            expectedType, null);
      }
    }

    /* Visit the true and false bodies with a new scope */
    symbolTable.openScope(node.getTrueBody());
    node.getTrueBody().visit(this);
    symbolTable.closeScope();

    symbolTable.openScope(node.getFalseBody());
    node.getFalseBody().visit(this);
    symbolTable.closeScope();

    return null;
  }

  @Override
  public Void visitBeginNode(BeginNode node) {
    /* Visit the body with a new scope */
    symbolTable.openScope(node.getBody());
    node.getBody().visit(this);
    symbolTable.closeScope();
    return null;
  }

  @Override
  public Void visitAddToListNode(AddToListNode node) {
    Type identType = node.getIdentifier().visit(getExpressionType);

    //* If the identifier is not a list, then cannot add to a non-list */
    if(!(identType instanceof ListType)) {
      SemanticErrors.typeMismatchError(errorList, node.getIdentifier(),
              identType, new ListType(new AnyType()),
              node.getIdentifier().getCtx());
      return null;
    }

    /* The expected type of the element we're trying to add must be the type
       of the list we're adding to*/
    ExpressionNode expression = node.getExpression();
    Type expectedType = ((ListType) identType).getType();
    Type actualType = expression.visit(getExpressionType);

    if(!expectedType.equals(actualType)) {
      SemanticErrors.typeMismatchError(errorList, expression,
              actualType, expectedType, expression.getCtx());
      return null;
    }

    return null;
  }

  @Override
  public Void visitRemoveFromListNode(RemoveFromListNode node) {
    Type identType = node.getIdentifier().visit(getExpressionType);

    //* If the identifier is not a list, then cannot remove from a non-list */
    if(!(identType instanceof ListType)) {
      SemanticErrors.typeMismatchError(errorList, node.getIdentifier(),
              identType, new ListType(new AnyType()),
              node.getIdentifier().getCtx());
      return null;
    }

    /* We want to remove an element at a specific position so must be an int */
    ExpressionNode expression = node.getExpression();
    Type expectedType = new BaseType(INT);
    Type actualType = expression.visit(getExpressionType);

    if(!expectedType.equals(actualType)) {
      SemanticErrors.typeMismatchError(errorList, expression,
              actualType, expectedType, expression.getCtx());
      return null;
    }

    return null;
  }

  @Override
  public Void visitWhileNode(WhileNode node) {
    Type type = node.getExpression().visit(getExpressionType);
    Type expectedType = new BaseType(Category.BOOL);

    /* The expression of the while statement must be a boolean expression */
    if (type != null) {
      if (!type.equals(expectedType)) {
        SemanticErrors.typeMismatchError(errorList, node.getExpression(), type,
            expectedType, null);
      }
    }

    /* Visit the body with a new scope */
    symbolTable.openScope(node.getBody());
    node.getBody().visit(this);
    symbolTable.closeScope();

    return null;
  }

  @Override
  public Void visitExitNode(ExitNode node) {
    Type expectedType = new BaseType(INT);
    ExpressionNode expr = node.getExpression();
    Type actualType = expr.visit(getExpressionType);
    ParserRuleContext ctx = expr.getCtx();

    /* We must exit with an integer expression */
    if (actualType != null && !actualType.equals(expectedType)) {
      SemanticErrors.typeMismatchError(errorList, expr, actualType,
          expectedType,
          ctx);
    }
    return null;
  }

  @Override
  public Void visitAssignmentNode(AssignmentNode node) {
    Type expectedType = node.getLhs().visit(getExpressionType);
    RightHandSideAssignment rhs = node.getRhs();
    if(rhs instanceof CallStatement) {
      ((CallStatement) rhs).setExpectedReturnType(expectedType);
    }
    Type actualType = node.getRhs().visit(getExpressionType);

    /* Checks the types of both sides of an assigment match */
    if (expectedType != null && actualType != null
        && !actualType.equals(expectedType)) {
      ParserRuleContext ctx = node.getCtx();
      SemanticErrors.typeMismatchError(errorList, node, expectedType, actualType,
          ctx);
    }
    return null;
  }

  @Override
  public Void visitDeclarationNode(DeclarationNode node) {
    Type expectedType = node.getType();
    Type actualType = node.getRhs().visit(getExpressionType);
    IdentifierNode identifier = node.getIdentifierNode();

        /* Checks it is possible to add to the symbol table by checking if
           it is already declared */
    VariableDeclaration variableDeclaration =
            new VariableDeclaration(node.getIdentifierNode().getName(),
                    node.getType());
    if (!symbolTable.add(identifier.getName(), variableDeclaration)) {
      ParserRuleContext ctx = identifier.getCtx();
      SemanticErrors.declaredVarError(errorList, identifier, ctx,
          identifier.getName());
      return null;
    }

    /* Checks that the type of both sides of the declaration match */
    if (actualType != null && !expectedType.equals(actualType)) {
      ParserRuleContext ctx = node.getCtx();
      SemanticErrors.typeMismatchError(errorList, node, actualType, expectedType,
          ctx);
    }
    symbolTable.addVariableDeclaration(variableDeclaration);
    node.setVariableDeclaration(variableDeclaration);
    return null;
  }

  @Override
  public Void visitPrintNode(PrintNode node) {
    Type type = node.getExpression().visit(getExpressionType);
    node.setType(type);
    return null;
  }

  @Override
  public Void visitPrintLnNode(PrintLnNode node) {
    Type type = node.getExpression().visit(getExpressionType);
    node.setType(type);
    return null;
  }

  @Override
  public Void visitReturnNode(ReturnNode node) {
        /* Makes sure that we are returning from a function
           not the main method */
    if (returnType == null) {
      ParserRuleContext ctx = node.getCtx();
      SemanticErrors.specialError(errorList, node, ctx,
          "Trying to return from main function.");
      return null;
    }

    Type actualType = node.getExpression().visit(getExpressionType);
    /* Checks the return type matches the return type of the function */
    if (actualType != null && !actualType.equals(returnType)) {
      ParserRuleContext ctx = node.getExpression().getCtx();
      SemanticErrors.typeMismatchError(errorList, node, returnType, actualType, ctx);
    }
    return null;
  }

  @Override
  public Void visitFreeNode(FreeNode node) {
    Type type = node.getExpression().visit(getExpressionType);
    List<Type> expectedTypes = Arrays.asList(
        new PairType(new AnyType(), new AnyType()),
        new ArrayType(new AnyType()),
        new ListType(new AnyType())
    );

    if (type != null) {
      if (!expectedTypes.contains(type)) {
        SemanticErrors.specialError(errorList, node, node.getCtx(),
            "Expected expression of type pair or array but was "
                + type);
      }
    }

    return null;
  }

  @Override
  public Void visitReadNode(ReadNode node) {
    Type type = node.getLhs().visit(getExpressionType);
    List<Type> expectedTypes = Arrays.asList(
        new BaseType(Category.INT),
        new BaseType(Category.CHAR)
    );

    if (type != null) {
      if (!expectedTypes.contains(type)) {
        ASTBasicNode basicNode = (ASTBasicNode) node.getLhs();
        SemanticErrors.specialError(errorList, basicNode, basicNode.getCtx(),
            "Expected expression of type one of "
                + expectedTypes + " but was " + type);
      }
    }

    /* Type is stored for code generation to determine which method to use for reading */
    node.setType(type);
    return null;
  }

  @Override
  public Void visitCallStatement(CallStatement node) {
    getExpressionType.visitCallStatement(node);
    return null;
  }

  public void addToFunctionTable(FunctionNode node) {
    FunctionEntry functionEntry = new FunctionEntry(node.getType());

        /*  Creates an array of size corresponding to the number of
            arguments - if the param list is null then we have no
            arguments - the array is empty.
        */
    ParamNode[] paramNodes;
    if (node.getParamList() == null) {
      paramNodes = new ParamNode[0];
    } else {
      paramNodes = node.getParamList().getParamList();
    }

    /* We add the type of each parameter to the function entry */
    for (ParamNode paramNode : paramNodes) {
      functionEntry.addType(paramNode.getType());
    }

    /* Try adding the function to the symbol table to say this function
       now exists */

    String nodeName = node.getIdentifierNode().getName();
    if (!functionSymbolTable.add(nodeName, functionEntry)) {
      ParserRuleContext ctx = node.getCtx();
      SemanticErrors.declaredFuncError(errorList, node, ctx,
          node.getIdentifierNode().getName(), functionEntry);
    }

    /* Set a unique label based on how many functions of the same name have
       already been created, in order to deal with function overloading */
    int functionsWithSameName = functionSymbolTable.
            getFuncListWithName(nodeName).size();
    String functionLabel = nodeName + "_" + functionsWithSameName;

    /* Set the unique label in both the node and the function entry */
    node.setLabel(functionLabel);
    functionEntry.setLabel(functionLabel);
  }

  /* Semantically checks a function node */
  public void semanticallyCheck(FunctionNode node) {

    /* Checks if the function is declared */
    List<Type> paramTypes = new ArrayList<>();
    if(node.getParamList() != null) {
      for (ParamNode parameter : node.getParamList().getParamList()) {
        paramTypes.add(parameter.getType());
      }
    }

    FunctionEntry funcEntryForCheck = new FunctionEntry(node.getType(),
            paramTypes);
    Optional<FunctionEntry>  possibleFuncEntry =
            functionSymbolTable.retrieveEntry(
                    node.getIdentifierNode().getName(), funcEntryForCheck);

    if (possibleFuncEntry.isEmpty()) {
      return;
    }

    FunctionEntry functionEntry = possibleFuncEntry.get();

        /*  Creates an array of size corresponding to
            the number of arguments - if the param list is null then we have
            no arguments - the array is empty.
         */
    ParamNode[] paramNodes;
    if (node.getParamList() == null) {
      paramNodes = new ParamNode[0];
    } else {
      paramNodes = node.getParamList().getParamList();
    }

        /*  Creates a symbol table specifically for checking the semantics of
            the body of the function. It contains
            no declared variables, but all declared functions.*/
    SymbolTable<VariableDeclaration> functionBasicSymbolTable =
        new SymbolTable<>(node.getBodyNode());

        /*  If the parameters contain the same name, then there is no point
            checking the body. This boolean keeps track of this. */
    boolean checkFunction = true;

        /*  Add the type of each parameter to a list so later when we call this
            function, we can check the types match. At the same time, check
            if the naming of the arguments is unique and add it to a specific
            symbol table just for the function, if so. If the naming is not
            unique, then we don't check the body and so set the boolean to false
         */
    for (ParamNode paramNode : paramNodes) {
      VariableDeclaration variableDeclaration =
          new VariableDeclaration(paramNode.getIdentifierNode().getName(),
              paramNode.getType(), true);

      if (!functionBasicSymbolTable.add(
          paramNode.getIdentifierNode().getName(), variableDeclaration)) {
        ParserRuleContext ctx = paramNode.getCtx();
        SemanticErrors.declaredVarError(errorList, paramNode, ctx,
            paramNode.getIdentifierNode().getName());
        checkFunction = false;
      } else {
        functionBasicSymbolTable.addVariableDeclaration(variableDeclaration);
      }
    }

    functionEntry.setSymbolTable(functionBasicSymbolTable);

        /* If we're going to check the function, then we need to create a new
            visitor to visit the body of that function */
    if (checkFunction) {
      SemanticVisitor functionVisitor =
          new SemanticVisitor(errorList, functionBasicSymbolTable,
              functionSymbolTable, node.getType());
      node.getBodyNode().visit(functionVisitor);
    }

  }
}
