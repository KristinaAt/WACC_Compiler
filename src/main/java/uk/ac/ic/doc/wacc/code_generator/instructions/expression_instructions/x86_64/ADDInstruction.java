package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class ADDInstruction extends x86Instruction {

  public ADDInstruction(Operand destinationOperand, Operand sourceOperand) {
    super("ADD", destinationOperand, sourceOperand);
  }
}
