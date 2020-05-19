package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.ManyOperands;

public class CheckOverflow extends ManyOperands {

  public enum Condition {
    OVERFLOW,
    NOT_EQUAL
  }

  private final Condition condition;

  public CheckOverflow(Condition condition) {
    super();

    this.condition = condition;
  }

  public Condition getCondition() {
    return condition;
  }
}
