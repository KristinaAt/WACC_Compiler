package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* Intermediate representation of an exit instruction. */
public class Exit extends IntermediateInstruction {

  private final Operand exitCode;

  public Exit(Operand exitCode) {
    this.exitCode = exitCode;
  }

  public Operand getExitCode() {
    return exitCode;
  }
}
