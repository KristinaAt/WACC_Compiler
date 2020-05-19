package uk.ac.ic.doc.wacc.code_generator.instructions.utils.list_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Immediate;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.OffsetRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BNE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_list_contains;

public class ListContains extends AbstractUtilMethod {

    public ListContains(Label firstLabel, Label secondLabel,
                        Label thirdLabel) {
        super();
        this.instructions.add(new Label(p_list_contains.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        this.instructions.add(new BranchInstruction(BL, secondLabel));

        this.instructions.add(firstLabel);
        this.instructions.add(new LDRInstruction(r2.getRegister(),
                OffsetRegister.noOffset(r0.getRegister()), WORD));

        this.instructions.add(
                new CMPInstruction(r1.getRegister(), r2.getRegister()));
        this.instructions.add(new BranchInstruction(BNE, thirdLabel));
        this.instructions.add(new LDRInstruction(r0.getRegister(),
                new Immediate(1), WORD));
        this.instructions.add(new POPInstruction(PC.getRegister()));

        this.instructions.add(thirdLabel);
        this.instructions.add(secondLabel);

        this.instructions.add(new LDRInstruction(r0.getRegister(),
                OffsetRegister.preIndexedOffset(r0.getRegister(),
                        new ConstantOffset(4)),  WORD));
        this.instructions.add(new CMPInstruction(r0.getRegister(),
                new ConstantOffset(0)));
        this.instructions.add(new BranchInstruction(BNE, firstLabel));

        this.instructions.add(new LDRInstruction(r0.getRegister(),
                new Immediate(0), WORD));
        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
