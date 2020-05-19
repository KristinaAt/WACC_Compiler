package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* Holds a register which will hold the data popped off the stack */
public class POPInstruction extends ARMInstruction {
    private final Register reg;

    public POPInstruction(Register reg) {
        this.reg = reg;
    }

    @Override
    public String toString() {
        return "POP {" + reg + "}";
    }
}
