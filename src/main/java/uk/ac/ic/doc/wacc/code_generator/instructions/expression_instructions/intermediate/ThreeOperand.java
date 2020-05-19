package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Abstract intermediate representation of a three operand instruction. */
public abstract class ThreeOperand extends IntermediateInstruction {
  private final Operand destinationOperand;
  private final Operand sourceOperand1;
  private final Operand sourceOperand2;

  /* The boolean is set to true when we want to set the flags. */
  private final boolean updateBooleanFlags;

  public ThreeOperand(Operand sourceOperand1, Operand sourceOperand2,
                      Operand destinationOperand,
    boolean updateBooleanFlags) {
    this.destinationOperand = destinationOperand;
    this.sourceOperand1 = sourceOperand1;
    this.sourceOperand2 = sourceOperand2;
    this.updateBooleanFlags = updateBooleanFlags;
  }

  public Operand getFirstSourceOperand() {
    return sourceOperand1;
  }

  public Operand getSecondSourceOperand() {
    return sourceOperand2;
  }

  public Operand getDestinationOperand() {
    return destinationOperand;
  }

  public boolean shouldBooleanFlagsBeUpdated() {
    return updateBooleanFlags;
  }
}
