package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a malloc instruction. We specify the number
   memory bytes we need and the destination register for the address of
   the allocated space. */
public class Malloc extends IntermediateInstruction {

  private final int bytes;
  private final Operand destinationOperand;

  public Malloc(int bytes, Operand destinationOperand) {
    this.bytes = bytes;
    this.destinationOperand = destinationOperand;
  }

  public int getBytes() {
    return bytes;
  }

  public Operand getDestinationOperand() {
    return destinationOperand;
  }
}
