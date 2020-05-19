package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class NOTInstruction extends x86Instruction {

  public NOTInstruction(Operand sourceOperand) {
    super("NOT", sourceOperand);
  }
}
