package uk.ac.ic.doc.wacc.code_generator.instructions.utils.print_read_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.ADDInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.MOVInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDREQ;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDRNE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_bool;

public class PrintBool extends AbstractUtilMethod {

    public PrintBool(String msg_label_1, String msg_label_2) {
        super();
        /* Generates the label for printing a bool and pushes the
           return address onto the stack */
        this.instructions.add(new Label(p_print_bool.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* Checks whether false or true is being printed and puts
           a corresponding message into r0.getRegister() */
        this.instructions.add(new CMPInstruction(r0.getRegister(),
                new ConstantOffset(0)));
        this.instructions.add(new LDRInstruction(LDRNE, r0.getRegister(),
                new LabelOperand(msg_label_1), WORD));
        this.instructions.add(new LDRInstruction(LDREQ, r0.getRegister(),
                new LabelOperand(msg_label_2), WORD));

        /* Set-up for printing and branching to a utility function printf */
        this.instructions.add(ADDInstruction.addWithNoFlags(r0.getRegister(),
                r0.getRegister(),
                new ConstantOffset(4)));
        this.instructions.add(new BranchInstruction(BL, new Label("printf")));

        /* Resets the r0.getRegister() value and flushes the stream to stdout */
        this.instructions.add(new MOVInstruction(r0.getRegister(),
                new ConstantOffset(0)));
        this.instructions.add(new BranchInstruction(BL, new Label("fflush")));

        /* Pops the program counted off the stack */
        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
