package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class NEGInstruction extends x86Instruction {

  public NEGInstruction(Register sourceRegister) {
    super("NEG", sourceRegister);
  }
}
