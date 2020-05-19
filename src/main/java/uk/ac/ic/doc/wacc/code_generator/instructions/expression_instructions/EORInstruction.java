package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* EORInstruction class is used for creating an EOR assembly instruction,
   used for boolean expressions. It consists of two registers that hold a
   boolean value, which we do the exclusive or operation on. The result is
   then stored in the destination register. */
public class EORInstruction extends ARMInstruction {
    private final Register dst;
    private final Register srcreg;
    private final Operand sndOp;

    public EORInstruction(Register dst, Register srcreg, Operand sndOp) {
        this.dst = dst;
        this.srcreg = srcreg;
        this.sndOp = sndOp;
    }
    @Override
    public String toString() {
        return "EOR "+ dst + ", " + srcreg + ", " + sndOp;
    }
}
