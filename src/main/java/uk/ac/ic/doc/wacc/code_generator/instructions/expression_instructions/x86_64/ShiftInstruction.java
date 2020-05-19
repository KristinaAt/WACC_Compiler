package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class ShiftInstruction extends x86Instruction {
  public enum Type {
    SAR,
    SHL,
    SHR
  }

  public ShiftInstruction(Type type, Operand destinationOperand, Operand shiftOperand) {
    super(type.toString(), destinationOperand, shiftOperand);
  }
}
