package uk.ac.ic.doc.wacc.code_generator.instructions;

public abstract class Instruction {
  private final Architecture architecture;

  public Instruction(Architecture architecture) {
    this.architecture = architecture;
  }

  public final Architecture getArchitecture() {
    return architecture;
  }

  @Override
  public abstract String toString();
}
