package uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* LDR instruction with an enum for deciding the correct suffix. Uses the enum
   as the type, a register, operand and element size for determining whether
   to append with B or not. */
public class LDRInstruction extends ARMInstruction {
    public enum LDRType {
        LDR,
        LDRGE,
        LDRNE,
        LDRLT,
        LDRCS,
        LDREQ;
    }

    private final LDRType ldrType;
    private final Register dest;
    private final Operand src;
    private final ElementSize size;

    public LDRInstruction(LDRType ldrType, Register dest, Operand src, ElementSize size) {
        this.ldrType = ldrType;
        this.dest = dest;
        this.src = src;
        this.size = size;
    }

    public LDRInstruction(Register dest, Operand src, ElementSize size) {
        this(LDRType.LDR, dest, src, size);
    }

    @Override
    public String toString() {
        /* Returns LDRb if size is one byte, else LDR */
        return ldrType.toString() + size + " " + dest + ", " + src;
    }
}
