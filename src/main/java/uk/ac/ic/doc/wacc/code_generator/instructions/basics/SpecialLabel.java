package uk.ac.ic.doc.wacc.code_generator.instructions.basics;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;

/* SpecialLabel class holds the name of the label. It is used for the global
   main function and for the takedown instructions of the assembly code. */
public class SpecialLabel extends ARMInstruction {

    private final String label;

    public SpecialLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "." + label + "\n";
    }
}
