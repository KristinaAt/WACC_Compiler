package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a not instruction. */
public class Not extends IntermediateInstruction {

  private final Operand sourceOperand;
  private final Operand destinationOperand;

  public Not(Operand sourceOperand, Operand destinationOperand) {
    this.sourceOperand = sourceOperand;
    this.destinationOperand = destinationOperand;
  }

  public Operand getSourceOperand() {
    return sourceOperand;
  }

  public Operand getDestinationOperand() {
    return destinationOperand;
  }
}
