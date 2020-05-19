package uk.ac.ic.doc.wacc.code_generator.instructions.utils.free_methods;import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;import uk.ac.ic.doc.wacc.code_generator.instructions.operands.LabelOperand;import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BEQ;import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDREQ;import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.*;public class FreeArray extends AbstractUtilMethod  {    public FreeArray(String msg_label) {        super();    /* Generates the free array label followed by the assembly       code for freeing an array */        this.instructions.add(new Label(p_free_array.toString()));        this.instructions.add(new PUSHInstruction(LR.getRegister()));        /* Checks whether we try to free a null */        this.instructions.add(new CMPInstruction(r0.getRegister(),            new ConstantOffset(0)));        this.instructions.add(new LDRInstruction(LDREQ, r0.getRegister(),                new LabelOperand(msg_label), WORD));        this.instructions.add(new BranchInstruction(BEQ,                new Label(p_throw_runtime_error.toString())));       /* Frees the array reference */        this.instructions.add(new BranchInstruction(BL, new Label("free")));        /* Pops the program counted off the stack */        this.instructions.add(new POPInstruction(PC.getRegister()));    }}