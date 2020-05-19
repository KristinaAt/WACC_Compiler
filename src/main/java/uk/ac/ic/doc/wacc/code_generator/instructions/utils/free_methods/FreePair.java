package uk.ac.ic.doc.wacc.code_generator.instructions.utils.free_methods;

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

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BEQ;
import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDREQ;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_free_pair;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_throw_runtime_error;

public class FreePair extends AbstractUtilMethod {

  /* The constructor generated the assembly code for freeing a pair */
  public FreePair(String msg_label) {
    super();
    /* Generates the free pair label followed by the assembly
       code for freeing a pair */
    this.instructions.add(new Label(p_free_pair.toString()));
    this.instructions.add(new PUSHInstruction(LR.getRegister()));

    /* Checks whether we try to free a null */
    this.instructions.add(new CMPInstruction(r0.getRegister(),  new ConstantOffset(0)));
    this.instructions.add(new LDRInstruction(LDREQ, r0.getRegister(),
            new LabelOperand(msg_label), WORD));
    this.instructions.add(new BranchInstruction(BEQ,
            new Label(p_throw_runtime_error.toString())));

    /* Pushes the address of the pair onto the stack */
    this.instructions.add(new PUSHInstruction(r0.getRegister()));

    /* Dereferences the first element of a pair and frees it */
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            OffsetRegister.noOffset(r0.getRegister()), WORD));
    this.instructions.add(new BranchInstruction(BL, new Label("free")));

    /* Dereferences the second element of a pair and frees it */
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            OffsetRegister.noOffset(SP.getRegister()), WORD));
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            OffsetRegister.preIndexedOffset(r0.getRegister(), new ConstantOffset(4)), WORD));
    this.instructions.add(new BranchInstruction(BL, new Label("free")));

    /* Pops the pair address off the stack and frees the reference
       to the pair itself */
    this.instructions.add(new POPInstruction(r0.getRegister()));
    this.instructions.add(new BranchInstruction(BL, new Label("free")));

    /* Pops the program counted off the stack */
    this.instructions.add(new POPInstruction(PC.getRegister()));
  }
}
