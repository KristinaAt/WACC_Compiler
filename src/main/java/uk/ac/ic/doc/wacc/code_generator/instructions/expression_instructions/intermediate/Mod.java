package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a mod instruction. */
public class Mod extends ThreeOperand {

  /* The boolean is set to true when we want to set the flags. */
  public Mod(
      Operand sourceOperand1,
      Operand sourceOperand2,
      Operand destinationOperand,
      boolean updateBooleanFlags) {
    super(sourceOperand1, sourceOperand2, destinationOperand,
        updateBooleanFlags);
  }
}
