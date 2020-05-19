package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class SHRDInstruction extends x86Instruction {

  public SHRDInstruction(Operand destinationOperand, Operand sourceOperand, Operand bits) {
    super("SHRD", destinationOperand, sourceOperand, bits);
  }
}
