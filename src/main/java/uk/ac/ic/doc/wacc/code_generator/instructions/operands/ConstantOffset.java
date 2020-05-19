package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

/* Class that holds the constant offset, that is an integer. If the offset
   is 0, then a nullOffset is created. */
public class ConstantOffset extends Offset {
  private final int offset;

  public ConstantOffset(int offset) {
    super(offset == 0);
    this.offset = offset;
  }

  @Override
  public String toString() {
    return "#" + offset;
  }
}
