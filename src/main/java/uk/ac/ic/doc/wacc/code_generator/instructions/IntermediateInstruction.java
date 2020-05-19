package uk.ac.ic.doc.wacc.code_generator.instructions;

/* Abstract class used for reperesenting the Intermediate Instructions before
   architecture specific translation. */
public abstract class IntermediateInstruction extends Instruction {

  public IntermediateInstruction() {
    super(Architecture.INTERMEDIATE);
  }

  public final Instruction[] translate(Translator translator) {
    return translator.translateFrom(this);
  }

  @Override
  public String toString() {
    return getClass().getName();
  }
}
