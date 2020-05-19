package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of an or instruction. */
public class Shift extends ThreeOperand {

  private
  final uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift shift;

  /* The boolean is set to true when we want to set the flags. */
  public
  Shift(uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift shift,
      Operand sourceOperand1,
      Operand sourceOperand2,
      Operand destinationOperand,
      boolean updateBooleanFlags) {
    super(sourceOperand1, sourceOperand2, destinationOperand,
        updateBooleanFlags);
    this.shift = shift;
  }

  public
  uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift getShift() {
    return shift;
  }
}
