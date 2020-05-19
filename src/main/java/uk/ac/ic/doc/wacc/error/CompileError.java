package uk.ac.ic.doc.wacc.error;

import java.util.List;

public class CompileError {

  private final Type type;
  private final int lineNumber;
  private final int columnNumber;
  private final String message;

  public CompileError(List<CompileError> errorsEncounteredSoFar,
      Type type,
      int lineNumber,
      int columnNumber,
      String message) {
    this.message = generateMessage(type, lineNumber, columnNumber,
        message);
    this.type = type;
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;

    System.out.println(this.message);
    errorsEncounteredSoFar.add(this);
  }

  private static String generateMessage(Type type, int lineNumber,
      int columnNumber, String message) {
    return type + " error on line " + lineNumber + ", col " + columnNumber
        + ": " + message;
  }

  public String getMessage() {
    return message;
  }

  public Type getType() {
    return type;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public int getColumnNumber() {
    return columnNumber;
  }

  public enum Type {
    SYNTAX,
    SEMANTIC
  }
}
