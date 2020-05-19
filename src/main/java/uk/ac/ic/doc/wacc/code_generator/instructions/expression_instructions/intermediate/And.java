package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of an and instruction. */
public class And extends ThreeOperand {

  /* The boolean is set to true when we want to set the flags. */
  public And(Operand sourceOperand1,
      Operand sourceOperand2,
      Operand destinationOperand) {
    super(sourceOperand1, sourceOperand2, destinationOperand, false);
  }
}
