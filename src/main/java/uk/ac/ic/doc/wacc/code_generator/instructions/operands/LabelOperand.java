package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

/* Label operand, which contains the name of the assembly label */
public class LabelOperand implements Operand{
  private final String label;

  public LabelOperand(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return "=" + label;
  }
}
