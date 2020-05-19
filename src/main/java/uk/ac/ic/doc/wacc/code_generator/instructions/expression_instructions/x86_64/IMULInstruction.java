package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class IMULInstruction extends x86Instruction {

  public IMULInstruction(Operand destinationOperand, Operand sourceOperand) {
    super("IMUL", destinationOperand, sourceOperand);
  }
}
