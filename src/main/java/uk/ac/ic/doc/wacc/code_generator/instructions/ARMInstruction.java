package uk.ac.ic.doc.wacc.code_generator.instructions;

/* Abstract class used for ARM Specific Instructions. */
public abstract class ARMInstruction extends Instruction {
  public ARMInstruction() {
    super(Architecture.ARM);
  }
}
