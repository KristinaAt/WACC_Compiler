package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class POPInstruction extends x86Instruction {

  public POPInstruction(Register register) {
    super("POP", register);
  }
}
