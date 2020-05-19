package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

public class MVNInstruction extends ARMInstruction {
    private final Register dest;
    private final Register src;


    public MVNInstruction(Register dest, Register src) {
        this.dest = dest;
        this.src = src;
    }

    @Override
    public String toString() {
        return "MVN " + dest + ", " + src;
    }
}

