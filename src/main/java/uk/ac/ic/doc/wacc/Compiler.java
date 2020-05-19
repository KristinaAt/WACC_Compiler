package uk.ac.ic.doc.wacc;

import java.util.Arrays;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import uk.ac.ic.doc.wacc.antlr.WACCLexer;
import uk.ac.ic.doc.wacc.antlr.WACCParser;
import uk.ac.ic.doc.wacc.ast.ASTGenerator;
import uk.ac.ic.doc.wacc.ast.ASTNodes.ASTNode;
import uk.ac.ic.doc.wacc.code_generator.CodeGeneratorVisitor;
import uk.ac.ic.doc.wacc.code_generator.instructions.Architecture;
import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateProgram;
import uk.ac.ic.doc.wacc.code_generator.instructions.Program;
import uk.ac.ic.doc.wacc.error.CompileError;
import uk.ac.ic.doc.wacc.error.WACCErrorListener;
import uk.ac.ic.doc.wacc.optimisation.Optimiser;
import uk.ac.ic.doc.wacc.semantic_analyser.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Compiler {

  private static WACCLexer lexer;
  private static WACCParser parser;
  private static List<CompileError> errorList;

  public static void main(String[] args) throws Exception {
    Architecture architecture = getArchitecture(args);
    String filepath = args[0];
    ANTLRInputStream inputStream = new ANTLRFileStream(filepath);
    errorList = new ArrayList<>();

    /* Do the parsing of the file being compiled with error detection */
    setUpLexer(inputStream);
    setUpParser();
    addErrorDetectors(lexer, parser);
    ParseTree tree = parseProgram();

    /* Generate an abstract syntax tree for semantic checking and code gen */
    ASTNode ASTree =  generateAbstractSyntaxTree(tree);

    /* Setup symbol tables to be used for semantic checking and code gen */
    FunctionSymbolTable<FunctionEntry> functionSymbolTable =
            new FunctionSymbolTable<>();
    SymbolTable<VariableDeclaration> basicSymbolTable =
            new SymbolTable<>(ASTree);

    /* Check semantics of program */
    checkSemantics(ASTree, functionSymbolTable, basicSymbolTable);

    /* Creates an optimised ASTree if optimise flag is set */
    boolean optimiseFlag = checkForOptimiseFlag(args);
    ASTree = createOptimisedASTree(optimiseFlag, ASTree);

    /* Generate code and output code to a .s file of the same name */
    IntermediateProgram program = new IntermediateProgram(architecture);
    generateIntermediateCode(program, ASTree, functionSymbolTable,
        basicSymbolTable, optimiseFlag);

    Program targetProgram = translateIntermediateCode(program);
    generateAssembly(filepath, targetProgram);
  }

  private static void setUpLexer(ANTLRInputStream inputStream) {
    lexer = new WACCLexer(inputStream);
  }

  private static void setUpParser() {
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    parser = new WACCParser(tokens);
  }

  private static void addErrorDetectors(WACCLexer lexer, WACCParser parser) {
    /* Create error listener */
    WACCErrorListener errorListener = new WACCErrorListener(errorList);

    /* Remove ANTLR's default error listeners */
    lexer.removeErrorListeners();
    parser.removeErrorListeners();

    /* Add our error listener to lexer and parser */
    lexer.addErrorListener(errorListener);
    parser.addErrorListener(errorListener);
  }

  private static ParseTree parseProgram() {
    /* Use the ANTLR tree to parse check the program */
    ParseTree tree = parser.prog();

    /* If an error is detected during parsing, then exit with code 100 */
    if (!errorList.isEmpty()) {
      System.exit(100);
    }

    return tree;
  }

  private static ASTNode generateAbstractSyntaxTree(ParseTree tree) {
    /* Use an ASTGenerator visitor to generate an AST */
    ASTGenerator astGenerator = new ASTGenerator();
    return astGenerator.visit(tree);
  }

  private static void checkSemantics(
          ASTNode ASTree,
          FunctionSymbolTable<FunctionEntry> functionSymbolTable,
          SymbolTable<VariableDeclaration> basicSymbolTable) {

    /* Set up a semantic visitor to perform semantic checks */
    SemanticVisitor checkSemantics = new SemanticVisitor(errorList,
            basicSymbolTable, functionSymbolTable);

    /* Visit the abstract syntax tree whilst performing semantic checking */
    ASTree.visit(checkSemantics);

    /* If an error was detected exit with exit code 200 */
    if (!errorList.isEmpty()) {
      System.exit(200);
    }
  }

  private static boolean checkForOptimiseFlag(String[] args) {
    /* Tells us if -o flag is set */
    List<String> argList = Arrays.asList(args);

    return argList.contains("-o");
  }

  private static Architecture getArchitecture(String[] args) {
    List<String> argList = Arrays.asList(args);

    if (argList.contains("-x86")) {
      return Architecture.X86_64;
    } else {
      return Architecture.ARM;
    }
  }

  private
  static ASTNode createOptimisedASTree(boolean optimise, ASTNode ASTree) {
    /* Creates an optimised ASTree if optimise flag is set */
    if(optimise) {
      Optimiser preCodeGenOptimiser = new Optimiser();
      ASTree = ASTree.visit(preCodeGenOptimiser);
    }
    return ASTree;
  }

  private static void generateAssembly(String arg, Program program)
          throws IOException {

    /* Create an assembly file with the same name but a .s extension */
    Path path = Path.of(arg).getFileName();
    String filenamestring =
            path.toString().substring(0, path.toString().lastIndexOf("."));
    FileWriter file = new FileWriter(filenamestring + ".s");

    /* Write the program to the .s file */
    program.writeToFile(file);

    file.close();
  }

  private static void generateIntermediateCode(
          IntermediateProgram program,
          ASTNode ASTree,
          FunctionSymbolTable<FunctionEntry> functionSymbolTable,
          SymbolTable<VariableDeclaration> basicSymbolTable,
          boolean optimiseFlag) {

    CodeGeneratorVisitor codeGeneratorVisitor =
            new CodeGeneratorVisitor(program, basicSymbolTable,
                    functionSymbolTable, optimiseFlag);

    ASTree.visit(codeGeneratorVisitor);
  }

  private
  static Program translateIntermediateCode(IntermediateProgram program) {
    return program.translate();
  }
}
