package uk.ac.ic.doc.wacc.code_generator.instructions.utils.list_methods;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BNE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.LR;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.PC;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.r0;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.r1;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_get_last_node;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.OffsetRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

public class GetLastNode extends AbstractUtilMethod {

    public GetLastNode(Label firstLabel, Label secondLabel) {
        super();

        this.instructions.add(new Label(p_get_last_node.toString()));
        this.instructions.add(new PUSHInstruction(LR.getRegister()));

        /* Branch label for checking if the current address stores a
           null pointer */
        this.instructions.add(new BranchInstruction(BL, firstLabel));

        /* Gets the next list element address */
        this.instructions.add(secondLabel);
        this.instructions.add(new LDRInstruction(r0.getRegister(),
            OffsetRegister.preIndexedOffset(r0.getRegister(),
                new ConstantOffset(4)), WORD));

        /* Checks if the current element address is null. Loops if not */
        this.instructions.add(firstLabel);
        this.instructions.add(new LDRInstruction(r1.getRegister(),
            OffsetRegister.preIndexedOffset(r0.getRegister(),
                new ConstantOffset(4)), WORD));
        this.instructions.add(new CMPInstruction(r1.getRegister(), new ConstantOffset(0)));
        this.instructions.add(new BranchInstruction(BNE, secondLabel));

        this.instructions.add(new POPInstruction(PC.getRegister()));
    }
}
