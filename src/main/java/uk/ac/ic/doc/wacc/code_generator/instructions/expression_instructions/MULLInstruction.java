package uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register;

/* MULLInstruction class is used for creating the MULL assembly instruction.
   It consists of two operands, which we do the multiplication on. The
   result is then stored in the two destination registers. The class also
   holds a boolean variable flag, that is set to true whenever the
   instruction is SMULL. The ARM instruction can therefore be created
   using the corresponding constructor with or without setting the flag. */
public class MULLInstruction extends ARMInstruction {
    private final Operand destLeastSignificant;
    private final Operand destMostSignificant;
    private final Operand operand1;
    private final Operand operand2;
    private final boolean flag;


    public static MULLInstruction mullWithFlags(Operand destLeastSignificant,
             Operand destMostSignificant, Operand operand1, Operand operand2) {
        return new MULLInstruction(destLeastSignificant, destMostSignificant,
                operand1, operand2, true);
    }

    public static MULLInstruction mullWithNoFlags(Operand destLeastSignificant,
            Operand destMostSignificant, Operand operand1, Operand operand2) {
        return new MULLInstruction(destLeastSignificant, destMostSignificant,
                operand1, operand2, false);
    }

    private MULLInstruction(Operand destLeastSignificant,
                            Operand destMostSignificant, Operand operand1,
                            Operand operand2, boolean flag) {
        this.destLeastSignificant = destLeastSignificant;
        this.destMostSignificant = destMostSignificant;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.flag = flag;
    }
    @Override
    public String toString(){
        return (flag ? "SMULL " : "MULL ") + destLeastSignificant + ", " +
                destMostSignificant + ", " + operand1 + ", " + operand2;
    }
}
