package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.ManyOperands;

public class CheckNullPointer extends ManyOperands {

  public CheckNullPointer(Operand... operands) {
    super(operands);
  }
}
