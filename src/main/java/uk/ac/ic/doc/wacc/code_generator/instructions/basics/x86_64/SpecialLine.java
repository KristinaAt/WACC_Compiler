package uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class SpecialLine extends x86Instruction {

  private final String line;

  public SpecialLine(String line) {
    this.line = line;
  }

  @Override
  public String toString() {
    return line;
  }
}
