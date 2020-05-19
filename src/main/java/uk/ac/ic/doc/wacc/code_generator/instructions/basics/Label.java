package uk.ac.ic.doc.wacc.code_generator.instructions.basics;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;

/* Label class that holds a String for the name of the label */
public class Label extends ARMInstruction {
    private final String label;

    public Label(String label) {
        this.label = label;
    }

    public String getLabelName() {
        return label;
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
