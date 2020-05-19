package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* RSBSInstruction class is used for creating the RSBS assembly instruction.
   The instruction subtracts the value stored in the source register from
   the operand and the result is stored in the first destination register. */
public class RSBSInstruction extends ARMInstruction {
    private final Register dst;
    private final Register srcreg;
    private final Operand operand;

    public RSBSInstruction(Register dst, Register srcreg, Operand operand) {
        this.dst = dst;
        this.srcreg = srcreg;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return "RSBS "+ dst + ", " + srcreg + ", " + operand;
    }
}
