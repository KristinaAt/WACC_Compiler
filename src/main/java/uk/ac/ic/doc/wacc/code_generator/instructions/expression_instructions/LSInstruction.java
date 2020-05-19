package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift;

import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.*;

public class LSInstruction extends ARMInstruction {
    private final Register dest;
    private final Register src;
    private final Register shiftVal;
    private final Shift shift;

    public LSInstruction(Shift shift, Register dest, Register src,
                         Register shiftVal) {
        this.shift = shift;
        this.dest = dest;
        this.src = src;
        this.shiftVal = shiftVal;
    }
    @Override
    public String toString() {
        return shift.toString() + " " + dest + ", " + src + ", "
                + shiftVal;
    }
}

