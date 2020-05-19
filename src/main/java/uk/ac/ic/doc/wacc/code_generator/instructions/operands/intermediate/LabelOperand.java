package uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a label operand. */
public class LabelOperand implements Operand {
  private final Label targetLabel;

  public LabelOperand(Label targetLabel) {
    this.targetLabel = targetLabel;
  }

  public Label getTargetLabel() {
    return targetLabel;
  }
}
