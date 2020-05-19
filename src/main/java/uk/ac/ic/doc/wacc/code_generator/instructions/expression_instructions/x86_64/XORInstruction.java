package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class XORInstruction extends x86Instruction {

  public XORInstruction(Operand operand1, Operand operand2) {
    super("XOR", operand1, operand2);
  }
}
