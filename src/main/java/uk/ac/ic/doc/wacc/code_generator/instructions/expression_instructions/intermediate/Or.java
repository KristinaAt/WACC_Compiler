package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of an or instruction. */
public class Or extends ThreeOperand {

  public Or(Operand sourceOperand1,
      Operand sourceOperand2,
      Operand destinationOperand) {
    super(sourceOperand1, sourceOperand2, destinationOperand, false);
  }
}
