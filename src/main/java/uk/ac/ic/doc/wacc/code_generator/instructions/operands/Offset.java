package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

/* Offset superclass */
public abstract class Offset implements Operand {
  private final boolean nullOffset;

  public Offset(boolean nullOffset) {
    this.nullOffset = nullOffset;
  }

  public final boolean isNull() {
    return nullOffset;
  }
}
