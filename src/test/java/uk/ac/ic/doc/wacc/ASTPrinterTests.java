package uk.ac.ic.doc.wacc;

import java.util.ArrayList;
import uk.ac.ic.doc.wacc.ast.ASTGenerator;
import uk.ac.ic.doc.wacc.ast.ASTNodes;
import uk.ac.ic.doc.wacc.ast.ASTPrinter;
import uk.ac.ic.doc.wacc.error.WACCErrorListener;
import uk.ac.ic.doc.wacc.antlr.WACCLexer;
import uk.ac.ic.doc.wacc.antlr.WACCParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ASTPrinterTests {

  WACCLexer lexer = new WACCLexer(null);
  CommonTokenStream tokens = new CommonTokenStream(lexer);
  WACCParser parser = new WACCParser(tokens);
  WACCErrorListener errorListener = new WACCErrorListener(new ArrayList<>());
  ASTGenerator astGenerator = new ASTGenerator();
  ASTPrinter astPrinter = new ASTPrinter();
  WACCParser.ProgContext tree;

  /*Current directory is wacc_29*/
  @Test
  public void checkWhilePrinter() throws IOException {
    ANTLRInputStream inputStream = new ANTLRFileStream(
        "src/test/java/testfiles/valid/while/loopCharCondition.wacc");

    lexer.setInputStream(inputStream);
    parser.addErrorListener(errorListener);
    tree = parser.prog();

    ASTNodes.ASTNode ASTree = astGenerator.visit(tree);
    astPrinter.visitProgNode((ASTNodes.ProgNode) ASTree);

    String content = new String(Files.readAllBytes(Paths.get(
        "src/test/java/testfiles/printer/loopCharCondition.txt")));
    System.out.println(content);

    assertEquals(content, astPrinter.getBaos());
  }

  @Test
  public void checkFunctionsPrinter() throws IOException {
    ANTLRInputStream inputStream =
        new ANTLRFileStream(
            "src/test/java/testfiles/valid/function/nested_functions"
                + "/printInputTriangle.wacc");

    lexer.setInputStream(inputStream);
    parser.addErrorListener(errorListener);
    tree = parser.prog();

    ASTNodes.ASTNode ASTree = astGenerator.visit(tree);
    astPrinter.visitProgNode((ASTNodes.ProgNode) ASTree);

    String content = new String(Files.readAllBytes(Paths.get(
        "src/test/java/testfiles/printer/printInputTriangle.txt")));
    System.out.println(content);

    assertEquals(content, astPrinter.getBaos());
  }

  @Test
  public void checkArraysPrinter() throws IOException {
    ANTLRInputStream inputStream = new ANTLRFileStream(
        "src/test/java/testfiles/valid/array/arrayNested.wacc");

    lexer.setInputStream(inputStream);
    parser.addErrorListener(errorListener);
    tree = parser.prog();

    ASTNodes.ASTNode ASTree = astGenerator.visit(tree);
    astPrinter.visitProgNode((ASTNodes.ProgNode) ASTree);

    String content = new String(Files.readAllBytes(Paths.get(
        "src/test/java/testfiles/printer/arrayNested.txt")));
    System.out.println(content);

    assertEquals(content, astPrinter.getBaos());
  }

  @Test
  public void checkAUnaryOpPrinter() throws IOException {
    ANTLRInputStream inputStream = new ANTLRFileStream(
        "src/test/java/testfiles/valid/expressions/ordAndchrExpr.wacc");

    lexer.setInputStream(inputStream);
    parser.addErrorListener(errorListener);
    tree = parser.prog();

    ASTNodes.ASTNode ASTree = astGenerator.visit(tree);
    astPrinter.visitProgNode((ASTNodes.ProgNode) ASTree);

    String content = new String(Files.readAllBytes(Paths.get(
        "src/test/java/testfiles/printer/unaryOperators.txt")));
    System.out.println(content);

    assertEquals(content, astPrinter.getBaos());
  }

  @Test
  public void checkPairPrinter() throws IOException {
    ANTLRInputStream inputStream = new ANTLRFileStream(
        "src/test/java/testfiles/valid/pairs/printPairOfNulls.wacc");

    lexer.setInputStream(inputStream);
    parser.addErrorListener(errorListener);
    tree = parser.prog();

    ASTNodes.ASTNode ASTree = astGenerator.visit(tree);
    astPrinter.visitProgNode((ASTNodes.ProgNode) ASTree);

    String content = new String(Files.readAllBytes(Paths.get(
        "src/test/java/testfiles/printer/pair.txt")));
    System.out.println(content);

    assertEquals(content, astPrinter.getBaos());
  }

}
