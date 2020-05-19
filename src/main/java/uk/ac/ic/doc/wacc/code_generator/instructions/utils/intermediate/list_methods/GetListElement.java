package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.ManyOperandsWithDestination;

public class GetListElement extends ManyOperandsWithDestination {

  public GetListElement(
      Operand destinationOperand,
      Operand... operands) {
    super(destinationOperand, operands);
  }
}
