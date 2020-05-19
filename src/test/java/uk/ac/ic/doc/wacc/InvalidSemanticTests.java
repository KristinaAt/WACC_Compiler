package uk.ac.ic.doc.wacc;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ic.doc.wacc.ast.ASTGenerator;
import uk.ac.ic.doc.wacc.ast.ASTNodes.ASTNode;
import uk.ac.ic.doc.wacc.ast.ASTNodes.ProgNode;
import uk.ac.ic.doc.wacc.error.CompileError;
import uk.ac.ic.doc.wacc.error.WACCErrorListener;
import uk.ac.ic.doc.wacc.semantic_analyser.FunctionSymbolTable;
import uk.ac.ic.doc.wacc.semantic_analyser.SemanticVisitor;
import uk.ac.ic.doc.wacc.antlr.WACCLexer;
import uk.ac.ic.doc.wacc.antlr.WACCParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import uk.ac.ic.doc.wacc.semantic_analyser.SymbolTable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class InvalidSemanticTests {

  private final List<CompileError> errorList = new ArrayList<>();

  @Parameterized.Parameter()
  public String filepath;
  WACCLexer lexer = new WACCLexer(null);
  CommonTokenStream tokens = new CommonTokenStream(lexer);
  WACCParser parser = new WACCParser(tokens);
  WACCErrorListener errorListener = new WACCErrorListener(errorList);
  ASTGenerator astGenerator = new ASTGenerator();
  SemanticVisitor semanticVisitor;

  @Parameterized.Parameters(name = "{index}: Test with filepath={0}")
  public static Collection<String> data() {
    Collection<String> files = TestsUtils.findAllWACCFiles(new File(
        "src/test/java/testfiles/invalid/semanticErr"));
    return files;
  }

  @Test
  public synchronized void isAnInvalidProgram() throws IOException {
    System.out.println("Running " + filepath + " program test.");

    ANTLRInputStream inputStream = new ANTLRFileStream(filepath);
    lexer.setInputStream(inputStream);
    parser.addErrorListener(errorListener);
    ParseTree tree = parser.prog();

    assertTrue(errorList.isEmpty());

    ASTNode ASTree = astGenerator.visit(tree);

    semanticVisitor = new SemanticVisitor(errorList, new SymbolTable<>(ASTree),
        new FunctionSymbolTable<>());
    semanticVisitor.visitProgNode((ProgNode) ASTree);

    assertFalse(errorList.isEmpty());
  }
}
