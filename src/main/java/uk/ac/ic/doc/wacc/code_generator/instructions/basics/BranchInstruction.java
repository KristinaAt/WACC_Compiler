package uk.ac.ic.doc.wacc.code_generator.instructions.basics;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;

/* BranchInstruction class is used for creating branch assembly instructions.
   It holds the label we branch to and an enum for the type of the
   instruction corresponding to the branching condition. */
public class BranchInstruction extends ARMInstruction {
    private final Branch branch;
    private final Label label;

    public BranchInstruction(Branch branch, Label label) {
        this.branch = branch;
        this.label = label;
    }

    @Override
    public String toString() {
        return branch + " " + label.getLabelName();
    }

    public enum Branch {
        BL,
        B,
        BEQ,
        BNE,
        BGE,
        BLVS,
        BLEQ,
        BLLT,
        BLCS,
        BLNE,
        BVS,
        BCS,
        BLT
    }
}
