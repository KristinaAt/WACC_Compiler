package uk.ac.ic.doc.wacc.error;

import java.util.List;
import uk.ac.ic.doc.wacc.ast.ASTNodes.ASTBasicNode;
import uk.ac.ic.doc.wacc.ast.ASTNodes.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import uk.ac.ic.doc.wacc.semantic_analyser.FunctionEntry;

import static uk.ac.ic.doc.wacc.error.CompileError.Type.SEMANTIC;

public class SemanticErrors {

  public static void typeMismatchError(List<CompileError> errorList,
      ASTBasicNode node, Type actualType,
      Type expectedType, ParserRuleContext ctx) {
    if (ctx != null) {
      new CompileError(errorList, SEMANTIC, node.getLineNum(ctx),
          node.getColumnNum(ctx), ("Expected expression of type "
          + expectedType + " but was actually " + actualType));
    } else {
      new CompileError(errorList, SEMANTIC, node.getLineNum(node.getCtx()),
          node.getColumnNum(node.getCtx()),
          "Expected expression of "
              + "type " + expectedType + " but was "
              + actualType);
    }
  }

  public static void undeclaredVarError(List<CompileError> errorList,
      ASTBasicNode node,
      ParserRuleContext ctx, String name) {
    new CompileError(errorList, SEMANTIC, node.getLineNum(ctx),
        node.getColumnNum(ctx),
        "Undeclared variable " + name);
  }

  public static void undeclaredFuncError(List<CompileError> errorList,
      ASTBasicNode node,
      ParserRuleContext ctx, String name) {
    new CompileError(errorList, SEMANTIC, node.getLineNum(ctx),
        node.getColumnNum(ctx),
        "Undeclared variable " + name);
  }

  public static void declaredVarError(List<CompileError> errorList,
      ASTBasicNode node,
      ParserRuleContext ctx, String name) {
    new CompileError(errorList, SEMANTIC, node.getLineNum(ctx),
        node.getColumnNum(ctx),
        "Already declared variable " + name);
  }

  public static void declaredFuncError(List<CompileError> errorList,
      ASTBasicNode node,
      ParserRuleContext ctx, String name, FunctionEntry functionEntry) {
    new CompileError(errorList, SEMANTIC, node.getLineNum(ctx),
        node.getColumnNum(ctx),
        "Already declared function " +
                functionEntry.getFunctionDeclaration(name));
  }

  public static void specialError(List<CompileError> errorList,
      ASTBasicNode node,
      ParserRuleContext ctx, String message) {
    new CompileError(errorList, SEMANTIC, node.getLineNum(ctx),
        node.getColumnNum(ctx),
        message);
  }

}
