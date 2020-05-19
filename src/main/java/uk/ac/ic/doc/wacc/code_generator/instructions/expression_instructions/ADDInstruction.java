package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* ADDInstruction class is used for creating the ADD assembly instruction.
   It consists of two operands, which we do the addition on. The result is
   then stored in the destination register. The class also holds a boolean
   variable flag, that is set to true whenever the instruction is ADDS.
   The ARM instruction can therefore be created using the corresponding
   constructor with or without setting flag. */
public class ADDInstruction extends ARMInstruction {

    private final Register dest;
    private final Operand operand1;
    private final Operand operand2;
    private final boolean flag;

    public static ADDInstruction addWithFlags(Register dest, Operand operand1,
                                              Operand operand2) {
        return new ADDInstruction(dest, operand1, operand2, true);
    }

    public static ADDInstruction addWithNoFlags(Register dest, Operand operand1,
                                                Operand operand2) {
        return new ADDInstruction(dest, operand1, operand2, false);
    }

    private ADDInstruction(Register dest, Operand operand1, Operand operand2,
                           boolean flag) {
        this.dest = dest;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.flag = flag;
    }

    @Override
    public String toString() {
        return (flag ? "ADDS " : "ADD ") + dest + ", " + operand1 + ", " +
                operand2;
    }
}
