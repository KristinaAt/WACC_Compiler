package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* SUBInstruction class is used for creating the SUB assembly instruction.
   The second operand is subtracted from the first. The result is then
   stored in the destination register. The class also holds a boolean
   variable flag, that is set to true whenever the instruction is SUBS.
   The ARM instruction can therefore be created using the corresponding
   constructor with or without setting the flag. */
public class SUBInstruction extends ARMInstruction {

    private final Register dest;
    private final Operand operand1;
    private final Operand operand2;
    private final boolean flag;

    public static SUBInstruction subWithFlags(Register dest, Operand operand1,
                                              Operand operand2) {
        return new SUBInstruction(dest, operand1, operand2, true);
    }

    public static SUBInstruction subWithNoFlags(Register dest, Operand operand1,
                                                Operand operand2) {
        return new SUBInstruction(dest, operand1, operand2, false);
    }

    public SUBInstruction(Register dest, Operand operand1, Operand operand2,
                          boolean flag) {
        this.dest = dest;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.flag = flag;
    }

    @Override
    public String toString(){
        return (flag ? "SUBS " : "SUB ") + dest + ", " + operand1 + ", "
                + operand2;
    }
}