package uk.ac.ic.doc.wacc.code_generator.instructions.utils.list_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BGE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDRGE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_list_bounds;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_throw_runtime_error;

public class CheckListBounds extends AbstractUtilMethod {

    public CheckListBounds(String msg_label) {
        super();
        this.instructions.add(new Label(p_check_list_bounds.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* r0 contains list access, r1 contains list size */
        this.instructions.add(new CMPInstruction(r0.getRegister(),
                r1.getRegister()));

        /* If r1 >= r0 then runtime error */
        this.instructions.add(new LDRInstruction(LDRGE, r0.getRegister(),
                new LabelOperand(msg_label), WORD));
        this.instructions.add(new BranchInstruction(BGE,
                new Label(p_throw_runtime_error.toString())));

        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
