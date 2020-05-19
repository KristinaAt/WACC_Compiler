package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.MOVInstruction.MovType.*;

/* MOV instruction with an enum for deciding the correct suffix. Uses the enum
   as the type, a register and operand. */

public class MOVInstruction extends ARMInstruction {
  public enum MovType{
    MOV,
    MOVGE,
    MOVLT,
    MOVGT,
    MOVLE,
    MOVEQ,
    MOVNE;
  }

  private final MovType movType;
  private final Register dest;
  private final Operand src;

  public MOVInstruction(Register dest, Operand src, MovType movType) {
    this.dest = dest;
    this.src = src;
    this.movType = movType;
  }

  public MOVInstruction(Register dest, Operand src) {
    this(dest, src, MOV);
  }

  @Override
  public String toString() {
    return movType + " " + dest + ", " + src;
  }
}
