package uk.ac.ic.doc.wacc;

import uk.ac.ic.doc.wacc.error.CompileError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import uk.ac.ic.doc.wacc.error.CompileError.Type;

import static org.junit.Assert.assertEquals;

public class CompileErrorTest {

  private final List<CompileError> errorList = new ArrayList<>();

  private CompileError createSemanticError(int lineNumber, int columnNumber,
      String message) {
    return new CompileError(errorList, CompileError.Type.SEMANTIC, lineNumber,
        columnNumber, message);
  }

  private CompileError createSemanticError(int lineNumber, int columnNumber,
      String message,
      List<CompileError> errorList) {
    return new CompileError(errorList, CompileError.Type.SEMANTIC, lineNumber,
        columnNumber, message);
  }

  @Test
  public void getsCorrectLineNumber() {
    int lineNumber = 23871;
    CompileError error = createSemanticError(lineNumber, 2912, "test");
    assertEquals(lineNumber, error.getLineNumber());
  }

  @Test
  public void getsCorrectColumnNumber() {
    int columnNumber = 99999;
    CompileError error = createSemanticError(1, columnNumber, "test2");
    assertEquals(columnNumber, error.getColumnNumber());
  }

  @Test
  public void getsCorrectMessage() {
    String message = "Hello! This is a sweet test.";
    int lineNumber = 99;
    int columnNumber = 1213;
    CompileError error = createSemanticError(lineNumber, columnNumber,
        message);
    assertEquals(Type.SEMANTIC.toString() + " error on line " + lineNumber +
        ", col " + columnNumber + ": " + message, error.getMessage());
  }

  private List<CompileError> addErrors(int howMany,
      List<CompileError> errorList) {
    List<CompileError> listToReturn = new ArrayList<>();
    Random random = new Random();

    for (int i = 0; i < howMany; i++) {
      int lineNumber = random.nextInt();
      int columnNumber = random.nextInt();
      String message = "this_is_a_test";
      listToReturn.add(createSemanticError(lineNumber, columnNumber,
          message, errorList));
    }

    return listToReturn;
  }

  @Test
  public void updatesErrorsThrownSoFarList() {
    List<CompileError> errorList = new ArrayList<>();
    List<CompileError> newErrors = addErrors(10000, errorList);

    assertEquals(newErrors, errorList);
  }

  @Test
  public void incrementsErrorCount() {
    List<CompileError> errorList = new ArrayList<>();

    int howManyNewErrors = 21479;
    addErrors(howManyNewErrors, errorList);

    assertEquals(howManyNewErrors, errorList.size());
  }
}
