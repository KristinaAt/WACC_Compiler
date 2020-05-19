package uk.ac.ic.doc.wacc;

import uk.ac.ic.doc.wacc.antlr.WACCLexer;
import uk.ac.ic.doc.wacc.antlr.WACCParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LexerTests {

  WACCLexer lexer = new WACCLexer(null);
  CommonTokenStream tokens = new CommonTokenStream(lexer);
  WACCParser parser = new WACCParser(tokens);


  private List<Token> getTokens(String s) throws IOException {
    ANTLRInputStream inputStream = new ANTLRFileStream(s);
    lexer.setInputStream(inputStream);
    parser.prog();

    return tokens.getTokens();
  }

  @Test
  public void beginAndEndTokensAreGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/SkipLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.SKIP_TOK);
  }

  @Test
  public void skipTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/BeginEndLexerTest.txt");

    assertEquals(tokensList.get(0).getType(), WACCLexer.BEGIN);
    assertEquals(tokensList.get(1).getType(), WACCLexer.END);
  }

  @Test
  public void intTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/IntBaseTypeLexerTest.txt");

    assertEquals(tokensList.get(0).getType(), WACCLexer.BEGIN);
    assertEquals(tokensList.get(1).getType(), WACCLexer.INT);
  }

  @Test
  public void boolTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/BoolTypeLexerTest.txt");

    assertEquals(tokensList.get(0).getType(), WACCLexer.BEGIN);
    assertEquals(tokensList.get(1).getType(), WACCLexer.BOOL);
  }

  @Test
  public void trueTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/BoolTypeLexerTest.txt");

    assertEquals(tokensList.get(0).getType(), WACCLexer.BEGIN);
    assertEquals(tokensList.get(4).getType(), WACCLexer.TRUE);
  }

  @Test
  public void falseTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/BoolTypeLexerTest.txt");

    assertEquals(tokensList.get(0).getType(), WACCLexer.BEGIN);
    assertEquals(tokensList.get(9).getType(), WACCLexer.FALSE);
  }


  @Test
  public void additionTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/AdditionLexerTest.txt");

    assertEquals(tokensList.get(0).getType(), WACCLexer.INTEGER);
    assertEquals(tokensList.get(1).getType(), WACCLexer.PLUS);
    assertEquals(tokensList.get(2).getType(), WACCLexer.INTEGER);
  }

  @Test
  public void ReadAndPrintLexerTest() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/BuildInFunctionsLexerTest.txt");

    assertEquals(tokensList.get(6).getType(), WACCLexer.READ);
    assertEquals(tokensList.get(9).getType(), WACCLexer.PRINTLN);
    assertEquals(tokensList.get(12).getType(), WACCLexer.PRINT);
  }

  @Test
  public void IfStatementTokensLexerTest() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/IfStatementLexerTest.txt");

    assertEquals(tokensList.get(6).getType(), WACCLexer.IF);
    assertEquals(tokensList.get(8).getType(), WACCLexer.THEN);
    assertEquals(tokensList.get(11).getType(), WACCLexer.ELSE);
    assertEquals(tokensList.get(14).getType(), WACCLexer.FI);
  }

  @Test
  public void WhileStatementTokensLexerTest() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/WhileStatementLexerTest.txt");

    assertEquals(tokensList.get(6).getType(), WACCLexer.WHILE);
    assertEquals(tokensList.get(8).getType(), WACCLexer.DO);
    assertEquals(tokensList.get(11).getType(), WACCLexer.DONE);
  }

  @Test
  public void andTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/AndLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.AND);
  }

  @Test
  public void divideTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/DivideLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.DIVIDE);
  }

  @Test
  public void eqTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/EqLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.EQ);
  }

  @Test
  public void greaterEqTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/GreaterEqLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.GREATER_EQ);
  }

  @Test
  public void greaterThanTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/GreaterThanLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.GREATER_THAN);
  }

  @Test
  public void lessEqTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/LessEqLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.LESS_EQ);
  }

  @Test
  public void lessThanTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/LessThanLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.LESS_THAN);
  }

  @Test
  public void modTokenIsGenereated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/ModLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.MOD);
  }

  @Test
  public void notEqTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/NotEqLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.NOT_EQ);
  }

  @Test
  public void orTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/OrLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.OR);
  }

  @Test
  public void minusTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/MinusLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.MINUS);
  }

  @Test
  public void PairTypeTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/PairTypeLexerTest.txt");

    assertEquals(tokensList.get(1).getType(), WACCLexer.PAIR);
    assertEquals(tokensList.get(9).getType(), WACCLexer.NEWPAIR);
    assertEquals(tokensList.get(25).getType(), WACCLexer.FST);
    assertEquals(tokensList.get(37).getType(), WACCLexer.SND);

  }

  @Test
  public void SqBracketsTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/SqBracketLexerTest.txt");

    assertEquals(tokensList.get(2).getType(), WACCLexer.OPEN_SQUARE);
    assertEquals(tokensList.get(3).getType(), WACCLexer.CLOSE_SQUARE);
  }

  @Test
  public void CallTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/CallLexerTest.txt");

    assertEquals(tokensList.get(12).getType(), WACCLexer.CALL);
  }

  @Test
  public void UnaryOpTokenIsGenerated() throws IOException {
    List<Token> tokensList = getTokens(
        "src/test/java/testfiles/lexer/UnaryOperatorLexerTest.txt");

    assertEquals(tokensList.get(12).getType(), WACCLexer.NOT);
  }


}