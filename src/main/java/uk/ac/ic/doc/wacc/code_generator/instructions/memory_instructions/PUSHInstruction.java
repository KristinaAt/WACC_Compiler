package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* Holds a register whose contents will be pushed onto the stack. */
public class PUSHInstruction extends ARMInstruction {

    private final Register reg;

    public PUSHInstruction(Register reg) {
        this.reg = reg;
    }

    @Override
    public String toString() {
        return "PUSH {" + reg + "}";
    }
}
