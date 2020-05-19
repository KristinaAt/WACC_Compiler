package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class PUSHInstruction extends x86Instruction {

  public PUSHInstruction(Register register) {
    super("PUSH", register);
  }

  public PUSHInstruction(Immediate immediate) {
    super("PUSH", immediate);
  }
}
