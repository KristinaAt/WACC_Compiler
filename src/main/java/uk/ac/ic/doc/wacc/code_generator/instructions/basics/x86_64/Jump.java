package uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.CustomOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class Jump extends x86Instruction {

  public enum Type {
    JMP,
    JE,
    JO,
    JL,
    JAE,
    JNE,
    JGE
  }

  public Jump(Label label, Type type) {
    super(type.toString(), new CustomOperand(label.getName()));
  }
}
