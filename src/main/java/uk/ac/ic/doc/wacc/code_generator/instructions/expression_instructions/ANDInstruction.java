package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* ANDInstruction class is used for creating an AND assembly instruction, used
   for boolean expressions. It consists of two registers that hold a boolean
   value, which we do the operation on. The result is then stored in the
   destination register. */
public class ANDInstruction extends ARMInstruction {
    private final Register dest;
    private final Register srcOne;
    private final Register srcTwo;

    public ANDInstruction(Register dest, Register srcOne, Register srcTwo) {
        this.dest = dest;
        this.srcOne = srcOne;
        this.srcTwo = srcTwo;
    }
    @Override
    public String toString() {
        return "AND "+ dest + ", " + srcOne + ", " + srcTwo;
    }
}
