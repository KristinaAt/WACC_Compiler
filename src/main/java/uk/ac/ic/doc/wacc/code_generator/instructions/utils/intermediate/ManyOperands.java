package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

public abstract class ManyOperands extends IntermediateInstruction
        implements Util {
  private final Operand[] operands;

  public ManyOperands(Operand... operands) {
    this.operands = operands;
  }

  public Operand[] getOperands() {
    return operands;
  }
}
