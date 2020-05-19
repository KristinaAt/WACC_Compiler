package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a return instruction. */
public class GetReturn extends IntermediateInstruction {

  private final Operand destinationOperand;

  public GetReturn(Operand destinationOperand) {
    this.destinationOperand = destinationOperand;
  }

  public Operand getDestinationOperand() {
    return destinationOperand;
  }
}
