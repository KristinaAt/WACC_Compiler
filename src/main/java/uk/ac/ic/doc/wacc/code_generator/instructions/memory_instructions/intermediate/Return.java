package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of a return instruction. */
public class Return extends IntermediateInstruction {
  private final Operand returnValue;
  private final String functionExitLabel;

  public Return() {
    this.returnValue = null;
    this.functionExitLabel = null;
  }

  public Return(Operand returnValue,
      String functionExitLabel) {
    this.returnValue = returnValue;
    this.functionExitLabel = functionExitLabel;
  }

  public Operand getReturnValue() {
    return returnValue;
  }

  public String getFunctionExitLabel() {
    return functionExitLabel;
  }
}
