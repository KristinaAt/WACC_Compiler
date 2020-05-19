package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a compare instruction. */
public class Compare extends IntermediateInstruction {
  private final Operand operand1;
  private final Operand operand2;

  public Compare(Operand operand1, Operand operand2) {
    this.operand1 = operand1;
    this.operand2 = operand2;
  }

  public Operand getFirstOperand() {
    return operand1;
  }

  public Operand getSecondOperand() {
    return operand2;
  }
}
