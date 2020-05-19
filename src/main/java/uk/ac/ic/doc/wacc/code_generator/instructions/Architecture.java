package uk.ac.ic.doc.wacc.code_generator.instructions;

/* ENum for the different architectures our compiler supports.
   Intermediate is the step before translation into an actual assembly
   code representation. Each architecture needs to specify a translator class
   and the number of registers which can be used. */
public enum Architecture {
  INTERMEDIATE(Integer.MAX_VALUE, null),
  ARM(8, new ARMTranslator()),
  X86_64(15, new x86Translator());

  private final int availableRegisters;
  private final Translator translator;

  Architecture(int availableRegisters, Translator translator) {
    this.availableRegisters = availableRegisters;
    this.translator = translator;
  }

  public int getAvailableRegisterNumber() {
    return availableRegisters;
  }

  public Translator getTranslator() {
    return translator;
  }
}
