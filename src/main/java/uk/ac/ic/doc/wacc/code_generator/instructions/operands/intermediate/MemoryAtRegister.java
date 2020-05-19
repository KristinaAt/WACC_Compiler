package uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a memory operation at a specific register. */
public class MemoryAtRegister implements Operand {
  /* Register populated with a memory address. */
  private final Register addressRegister;
  private final int offset;
  private final boolean saveBack;

  public MemoryAtRegister(Register addressRegister, int offset,
                          boolean saveBack) {
    this.addressRegister = addressRegister;
    this.offset = offset;
    this.saveBack = saveBack;
  }

  public Register getAddressRegister() {
    return addressRegister;
  }

  public int getOffset() {
    return offset;
  }

  public boolean shouldSaveBack() {
    return saveBack;
  }

}
