package uk.ac.ic.doc.wacc.code_generator.instructions.utils.runtime_error_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.OffsetRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BLCS;
import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BLLT;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_array_bounds;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_throw_runtime_error;

public class CheckArrayBounds extends AbstractUtilMethod {
    public CheckArrayBounds(String msg_label_1, String msg_label_2){
        super();
        /* Generates the label for checking if an array is out of bounds
           and pushes the return address onto the stack */
        this.instructions.add(new Label(p_check_array_bounds.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* Checks if we are trying to access a negative index and throws
           a runtime error if so */
        this.instructions.add(new CMPInstruction(r0.getRegister(),
                new ConstantOffset(0)));
        this.instructions.add(new LDRInstruction(LDRLT, r0.getRegister(),
                new LabelOperand(msg_label_1), WORD));
        this.instructions.add(new BranchInstruction(BLLT,
                new Label(p_throw_runtime_error.toString())));

        /* Checks if the index we try to access is beyond the size of the array
           and branches to the throw runtime error label */
        this.instructions.add(new LDRInstruction(LDR, r1.getRegister(),
                OffsetRegister.noOffset(r1.getRegister()) , WORD));
        this.instructions.add(new CMPInstruction(r0.getRegister(),
                r1.getRegister()));
        this.instructions.add(new LDRInstruction(LDRCS, r0.getRegister(),
                new LabelOperand(msg_label_2), WORD));
        this.instructions.add(new BranchInstruction(BLCS,
                new Label(p_throw_runtime_error.toString())));

        /* Pops the program counted off the stack */
        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
