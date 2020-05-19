package uk.ac.ic.doc.wacc.code_generator.instructions.utils.list_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.SUBInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.*;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.OffsetRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BNE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_remove_from_list;

public class RemoveFromList extends AbstractUtilMethod {

    public RemoveFromList(Label firstLabel, Label secondLabel) {
        super();

        this.instructions.add(new Label(p_remove_from_list.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* While loop, dereference addresses until ... */
        this.instructions.add(new BranchInstruction(BL, secondLabel));
        this.instructions.add(firstLabel);
        this.instructions.add(new LDRInstruction(r0.getRegister(),
                OffsetRegister.preIndexedOffset(r0.getRegister(),
                        new ConstantOffset(4)), WORD));
        this.instructions.add(SUBInstruction.subWithNoFlags(r1.getRegister(),
                r1.getRegister(), new ConstantOffset(1)));

    /* We have reached the nth element. We are at the one before the one
       we wish to delete */
        this.instructions.add(secondLabel);
        this.instructions.add(new CMPInstruction(r1.getRegister(),
                new ConstantOffset(0)));
        this.instructions.add(new BranchInstruction(BNE, firstLabel));

    /* Finally, change the pointer at this node to be the same as the pointer
       of the next node */
        this.instructions.add(new LDRInstruction(r1.getRegister(),
                OffsetRegister.preIndexedOffset(r0.getRegister(),
                        new ConstantOffset(4)), WORD));
        /* This is to remember the address of the node for freeing later */
        this.instructions.add(new MOVInstruction(r2.getRegister(),
                r1.getRegister()));
        this.instructions.add(new LDRInstruction(r1.getRegister(),
                OffsetRegister.preIndexedOffset(r1.getRegister(),
                        new ConstantOffset(4)), WORD));
        this.instructions.add(new STRInstruction(r1.getRegister(),
                OffsetRegister.preIndexedOffset(r0.getRegister(),
                        new ConstantOffset(4)), WORD));

        /* Free the allocated memory for the node by calling the free method */
        this.instructions.add(new MOVInstruction(r0.getRegister(),
                r2.getRegister()));
        this.instructions.add(new BranchInstruction(BL, new Label("free")));

        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
