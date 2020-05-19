package uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

public class CustomOperand implements Operand {

  private final String operand;

  public CustomOperand(String operand) {
    this.operand = operand;
  }

  @Override
  public String toString() {
    return operand;
  }
}
