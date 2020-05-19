package uk.ac.ic.doc.wacc.code_generator.instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

public abstract class x86Instruction extends Instruction {
  protected final Operand[] operands;

  private final String opcode;

  public x86Instruction(Operand... operands) {
    this(null, operands);
  }

  public x86Instruction(String opcode, Operand... operands) {
    super(Architecture.X86_64);

    this.operands = operands;
    this.opcode = opcode;
  }

  @Override
  public String toString() {
    if (opcode == null) {
      throw new UnsupportedOperationException("Non-overridden toString() not supported for null opcode");
    }

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(opcode);

    if (operands.length > 0) {
      stringBuilder.append(" ");
    }

    for (int i = 0; i < operands.length; i++) {
      stringBuilder.append(operands[i]);

      if (i < operands.length - 1) {
        stringBuilder.append(", ");
      }
    }

    return stringBuilder.toString();
  }
}
