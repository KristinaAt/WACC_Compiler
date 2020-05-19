package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

public abstract class ManyOperandsWithDestination extends ManyOperands {
  private final Operand destinationOperand;

  public ManyOperandsWithDestination(Operand destinationOperand,
      Operand... operands) {
    super(operands);

    this.destinationOperand = destinationOperand;
  }

  public Operand getDestinationOperand() {
    return destinationOperand;
  }
}
