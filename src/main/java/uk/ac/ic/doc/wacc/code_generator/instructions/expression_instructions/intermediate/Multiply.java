package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a multiply instruction. */
public class Multiply extends IntermediateInstruction {

  private final Operand sourceOperand1;
  private final Operand sourceOperand2;
  private final Operand destinationOperandMostSignificant;
  private final Operand destinationOperandLeastSignificant;

  /* The boolean is set to true when we want to set the flags. */
  private final boolean updateBooleanFlags;

  public Multiply(Operand sourceOperand1,
      Operand sourceOperand2,
      Operand destinationOperandMostSignificant,
      Operand destinationOperandLeastSignificant, boolean updateBooleanFlags) {
    this.sourceOperand1 = sourceOperand1;
    this.sourceOperand2 = sourceOperand2;
    this.destinationOperandMostSignificant =
            destinationOperandMostSignificant;
    this.destinationOperandLeastSignificant =
            destinationOperandLeastSignificant;
    this.updateBooleanFlags = updateBooleanFlags;
  }

  public Operand getFirstSourceOperand() {
    return sourceOperand1;
  }

  public Operand getSecondSourceOperand() {
    return sourceOperand2;
  }

  public Operand getDestinationOperandMostSignificant() {
    return destinationOperandMostSignificant;
  }

  public Operand getDestinationOperandLeastSignificant() {
    return destinationOperandLeastSignificant;
  }

  public boolean shouldBooleanFlagsBeUpdated() {
    return updateBooleanFlags;
  }
}
