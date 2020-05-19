package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a pop instruction. */
public class Pop extends IntermediateInstruction {
  private final Operand destinationOperand;

  public Pop(Operand destinationOperand) {
    this.destinationOperand = destinationOperand;
  }

  public Operand getDestinationOperand() {
    return destinationOperand;
  }
}
