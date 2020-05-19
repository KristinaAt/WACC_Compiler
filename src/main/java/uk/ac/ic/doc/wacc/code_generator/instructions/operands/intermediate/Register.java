package uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.BaseRegister;

/* Intermediate representation of a register. */
public class Register extends BaseRegister {

  public static final Register STACK_POINTER = new Register(-1);

  private final int identifier;

  public Register(int identifier) {
    this.identifier = identifier;
  }

  @Override
  public int getIdentifier() {
    return identifier;
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException("toString() not supported for "
        + "intermediate registers");
  }
}
