package uk.ac.ic.doc.wacc.code_generator.instructions.utils.runtime_error_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BLEQ;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDREQ;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_null_pointer;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_throw_runtime_error;

public class CheckNullPointer extends AbstractUtilMethod {
    public CheckNullPointer(String msg_label){
        super();
        /* Generates the label for checking a null pointer reference
           and pushes the return address onto the stack */
        this.instructions.add(new Label(p_check_null_pointer.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* Checks if the pointer is null by comparing it to 0 */
        this.instructions.add(new CMPInstruction(r0.getRegister(),
                new ConstantOffset(0)));

        /* Loads a corresponding message if the pointer is null and branches to
           the assembly code for throwing a runtime error */
        this.instructions.add(new LDRInstruction(LDREQ, r0.getRegister(),
                new LabelOperand(msg_label), WORD));
        this.instructions.add(new BranchInstruction(BLEQ,
                new Label(p_throw_runtime_error.toString())));

        /* Pops the program counted off the stack */
        this.instructions.add(new POPInstruction(PC.getRegister()));

    }
}
