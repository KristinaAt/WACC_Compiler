package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Register;

public abstract class BaseRegister implements Operand {
  public abstract int getIdentifier();

  @Override
  public abstract String toString();

  @Override
  public boolean equals(Object object) {
    if (object instanceof BaseRegister) {
      return ((BaseRegister) object).getIdentifier() == getIdentifier();
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(getIdentifier());
  }
}
