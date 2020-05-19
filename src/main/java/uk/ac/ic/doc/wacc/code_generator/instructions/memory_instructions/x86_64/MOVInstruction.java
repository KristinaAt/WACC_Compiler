package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register.Identifier;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class MOVInstruction extends x86Instruction {

  public enum Condition {
    NONE("MOV"),
    GE("CMOVGE"),
    NE("CMOVNE"),
    LT("CMOVL"),
    C("CMOVC"),
    E("CMOVE"),
    G("CMOVG"),
    LE("CMOVLE"),
    AE("CMOVAE");

    private final String opcode;

    Condition(String opcode) {
      this.opcode = opcode;
    }

    @Override
    public String toString() {
      return opcode;
    }
  }

  private final Operand destinationOperand;
  private final Operand sourceOperand;
  private final Condition condition;
  private final boolean isByte;

  public MOVInstruction(Operand destinationOperand, Operand sourceOperand, Condition condition, boolean isByte) {
    this.destinationOperand = destinationOperand;
    this.sourceOperand = sourceOperand;
    this.condition = condition;
    this.isByte = isByte;
  }

  @Override
  public String toString() {
    String opcode = condition.toString() + (isByte ? "B" : "");

    if (condition == Condition.NONE) {
      return opcode + " " + destinationOperand + ", " + sourceOperand;
    } else {
      String instructions = "";

      String register =
          destinationOperand.equals(Identifier.eax.getRegister()) ? "ebx" :
              "eax";

      instructions += "SUB esp, 4\n";
      instructions += "MOV [esp], " + register + "\n";
      instructions += "MOV " + register + ", " + sourceOperand + "\n";
      instructions += opcode + " " + destinationOperand + ", " + register +
          "\n";
      instructions += "MOV " + register + ", [esp]\n";
      instructions += "ADD esp, 4";

      return instructions;
    }
  }
}
