package uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class Label extends x86Instruction {

  private final String name;

  public Label(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getName() + ":";
  }
}
