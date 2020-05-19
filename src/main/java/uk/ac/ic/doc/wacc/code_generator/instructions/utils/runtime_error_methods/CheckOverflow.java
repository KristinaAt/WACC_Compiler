package uk.ac.ic.doc.wacc.code_generator.instructions.utils.runtime_error_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.*;

public class CheckOverflow extends AbstractUtilMethod {

    public CheckOverflow(String msg_label) {
        super();
        /* Loads a corresponding message if we get an overflow and branches to
           the assembly code for throwing a runtime error */
        this.instructions.add(new Label(p_throw_overflow_error.toString()));
        this.instructions.add(new LDRInstruction(r0.getRegister(),
                new LabelOperand(msg_label), WORD));
        this.instructions.add(new BranchInstruction(BL,
                new Label(p_throw_runtime_error.toString())));

    }
}
