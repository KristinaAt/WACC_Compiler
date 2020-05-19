package uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of an operand. */
public class Immediate implements Operand {
  private final int number;

  public Immediate(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }
}
