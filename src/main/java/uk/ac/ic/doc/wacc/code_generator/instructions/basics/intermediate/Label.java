package uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;

/* Intermediate representation of a label instruction. */
public class Label extends IntermediateInstruction {
  private final String labelName;

  public Label(String labelName) {
    this.labelName = labelName;
  }

  public String getName() {
    return labelName;
  }
}
