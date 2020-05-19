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
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_read_char;

public class ReadChar extends AbstractUtilMethod {

    public ReadChar(String msg_label) {
        super();
        /* Generated the label for printing on a new line and pushes the
           return address onto the stack */
        this.instructions.add(new Label(p_read_char.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* General set-up for reading a character from stdin */
        this.instructions.add(new MOVInstruction(r1.getRegister(),
                r0.getRegister()));
        this.instructions.add(new LDRInstruction(r0.getRegister(),
                new LabelOperand(msg_label), WORD));
        this.instructions.add(ADDInstruction.addWithNoFlags(r0.getRegister(),
                r0.getRegister(),
                new ConstantOffset(4)));
        this.instructions.add(new BranchInstruction(BL, new Label("scanf")));

        /* Pops the program counted off the stack */
        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
