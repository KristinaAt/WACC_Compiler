package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

/* Adds ability to create a shifted register instruction. Contains enum for
   the types of shifts possible in ARM Assembly with static methods
   for generating valid shifted register operands.
   Can also create generic shift instructions without specifying a register.
   Used for de-referencing arrays.*/
public class ShiftOffset extends Offset {

  private final boolean minus;
  private final Register register;
  private final Shift shift;
  private final int shiftBits;

  public static ShiftOffset offsetWithRRX(boolean negate, Register register) {
    return new ShiftOffset(negate, register, Shift.RRX, 0);
  }

  public static ShiftOffset offset(boolean negate, Register register,
   Shift shift, int shiftBits) {
    return new ShiftOffset(negate, register, shift, shiftBits);
  }

  public static ShiftOffset offsetWithNoRegister(boolean negate, Shift shift,
                                                 int shiftBits) {
    return new ShiftOffset(negate, null, shift, shiftBits);
  }

  public static ShiftOffset offsetWithNoShift(boolean negate,
   Register register) {
    return new ShiftOffset(negate, register, null, 0);
  }

  private ShiftOffset(boolean minus, Register register, Shift shift,
   int shiftBits) {
    super(false);
    this.minus = minus;
    this.register = register;
    this.shift = shift;
    this.shiftBits = shiftBits;
  }

  @Override
  public String toString() {
    String shiftString = "";

    if (shift != null) {
      shiftString = ", " + shift.toString();
      if (shift != Shift.RRX) {
        shiftString += " #" + shiftBits;
      }
    }

    return (minus ? "-" : "")
            + (register != null ? register : "") + shiftString;
  }
}
