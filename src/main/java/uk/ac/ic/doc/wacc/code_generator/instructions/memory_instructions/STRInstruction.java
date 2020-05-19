package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* STR instruction. Holds a register, operand and element size for determining
   whether to append with B or not */
public class STRInstruction extends ARMInstruction {
  private final Register src;
  private final Operand dest;
  private final ElementSize size;

  public STRInstruction(Register src, Operand dest, ElementSize size) {
    this.src = src;
    this.dest = dest;
    this.size = size;
  }

  @Override
  public String toString() {
    /* Returns STRb if size is one byte, else STR */
    return "STR" + size + " " + src + ", " + dest;
  }
}
