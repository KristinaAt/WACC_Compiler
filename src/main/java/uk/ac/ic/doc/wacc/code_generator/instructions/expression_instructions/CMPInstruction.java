package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Offset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;

/* CMPInstruction class is used for creating the CMP assembly instruction.
   The instruction subtracts one operand from the other for comparing
   whether the operands are equal or not. */
public class CMPInstruction extends ARMInstruction {
    private final Operand fstOp;
    private final Operand sndOp;
    private final Offset offset;

    public CMPInstruction(Operand fstOp, Operand sndOp) {
        this(fstOp, sndOp, null);
    }

    public CMPInstruction(Operand fstOp, Operand sndOp, Offset offset) {
        this.fstOp = fstOp;
        this.sndOp = sndOp;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "CMP " + fstOp + ", " + sndOp + (offset != null ? offset : "");
    }

}
