package uk.ac.ic.doc.wacc.code_generator.instructions.utils.print_read_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.ADDInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.MOVInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.OffsetRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_string;

public class PrintString extends AbstractUtilMethod {

    public PrintString(String msg_label) {
        super();
        /* Generates the label for printing a string and pushes the
           return address onto the stack */
        this.instructions.add(new Label(p_print_string.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* Set-up for printing and branching to a utility function printf */
        this.instructions.add(new LDRInstruction(r1.getRegister(),
                OffsetRegister.noOffset(r0.getRegister()), WORD));
        this.instructions.add(ADDInstruction.addWithNoFlags(r2.getRegister(),
            r0.getRegister(),
                new ConstantOffset(4)));
        this.instructions.add(new LDRInstruction(r0.getRegister(),
                new LabelOperand(msg_label), WORD));
        this.instructions.add(ADDInstruction.addWithNoFlags(r0.getRegister(),
                r0.getRegister(),
                new ConstantOffset(4)));
        this.instructions.add(new BranchInstruction(BL, new Label("printf")));

        /* Resets the r0 value and flushes the stream to stdout */
        this.instructions.add(new MOVInstruction(r0.getRegister(),
                new ConstantOffset(0)));
        this.instructions.add(new BranchInstruction(BL, new Label("fflush")));

        /* Pops the program counted off the stack */
        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
