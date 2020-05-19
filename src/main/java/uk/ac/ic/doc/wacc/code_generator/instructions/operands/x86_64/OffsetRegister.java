package uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

public class OffsetRegister implements Operand {

  private final Register register;
  private final int offset;

  public OffsetRegister(Register register) {
    this(register, 0);
  }

  public OffsetRegister(Register register, int offset) {
    this.register = register;
    this.offset = offset;
  }

  @Override
  public String toString() {
    String sign = offset < 0 ? "" : "+";

    return "[" + register + (offset == 0 ? "" : sign + offset) + "]";
  }
}
