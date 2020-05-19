package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a push instruction. */
public class Push extends IntermediateInstruction {
  private final Operand sourceOperand;

  public Push(Operand sourceOperand) {
    this.sourceOperand = sourceOperand;
  }

  public Operand getSourceOperand() {
    return sourceOperand;
  }
}
