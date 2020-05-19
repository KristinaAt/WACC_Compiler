package uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.CustomOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class Call extends x86Instruction {

  public Call(Label label) {
    super("CALL", new CustomOperand(label.getName()));
  }
}
