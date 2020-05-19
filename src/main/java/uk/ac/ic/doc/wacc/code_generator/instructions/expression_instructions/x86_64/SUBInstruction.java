package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class SUBInstruction extends x86Instruction {

  public SUBInstruction(Operand destinationOperand, Operand sourceOperand) {
    super("SUB", destinationOperand, sourceOperand);
  }
}
