package uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;

/* Intermediate representation of a start function label. */
public class StartFunction extends IntermediateInstruction {
  private final Label functionLabel;

  public StartFunction(Label functionLabel) {
    this.functionLabel = functionLabel;
  }

  public Label getFunctionLabel() {
    return functionLabel;
  }
}
