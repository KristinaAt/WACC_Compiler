package uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift;

/* Intermediate representation of a shift operand. */
public class Shifted implements Operand {

  private final Register register;
  private final Shift shift;
  private final int howManyBits;

  public Shifted(Register register, Shift shift, int howManyBits) {
    this.register = register;
    this.shift = shift;
    this.howManyBits = howManyBits;
  }

  public Register getRegister() {
    return register;
  }

  public Shift getShift() {
    return shift;
  }

  public int getHowManyBits() {
    return howManyBits;
  }
}
