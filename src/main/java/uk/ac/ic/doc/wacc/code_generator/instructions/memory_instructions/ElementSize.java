package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions;

/* ENUM for determining whether to use LDR or LDRB etc. */
public enum ElementSize {
  BYTE("B"),
  WORD;

  private final String suffix;

  ElementSize() {
    this("");
  }

  ElementSize(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public String toString() {
    return suffix;
  }
}
