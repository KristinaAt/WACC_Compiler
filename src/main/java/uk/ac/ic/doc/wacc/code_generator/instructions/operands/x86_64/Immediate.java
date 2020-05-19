package uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

public class Immediate implements Operand {
  private final int number;

  public Immediate(int number) {
    this.number = number;
  }

  @Override
  public String toString() {
    return Integer.toString(number);
  }
}
