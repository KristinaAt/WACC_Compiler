package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a move instruction. */
public class Move extends IntermediateInstruction {

  /* Enum that accounts for a conditional move. */
  public enum Condition {
    NONE,
    EQUAL,
    NOT_EQUAL,
    GREATER_EQUAL,
    GREATER_THAN,
    LESS_EQUAL,
    LESS_THAN,
    UNSIGNED_GREATER_EQUAL
  }

  private final Operand sourceOperand;
  private final Operand destinationOperand;
  private final Condition condition;

  /* Number of bytes we move based on the type of the data. */
  private final int bytes;

  public Move(Operand sourceOperand, Operand destinationOperand,
              Condition condition, int bytes) {
    this.sourceOperand = sourceOperand;
    this.destinationOperand = destinationOperand;
    this.condition = condition;
    this.bytes = bytes;
  }

  public Operand getSourceOperand() {
    return sourceOperand;
  }

  public Operand getDestinationOperand() {
    return destinationOperand;
  }

  public Condition getCondition() {
    return condition;
  }

  public int getSizeInBytes() {
    return bytes;
  }
}
