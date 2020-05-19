package uk.ac.ic.doc.wacc.code_generator.instructions.utils.print_read_methods;

import uk.ac.ic.doc.wacc.ast.ASTNodes;
import uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.*;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Immediate;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.OffsetRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BL;
import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.BNE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.BYTE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
public class PrintList extends AbstractUtilMethod {
  public PrintList(UtilMethodLabel listLabel, Label firstLabel, Label secondLabel,
                     Label typeLabel, ASTNodes.Type type) {

    /* Generates the label for printing a list and pushes the
       return address onto the stack */
    this.instructions.add(new Label(listLabel.toString()));
    this.instructions.add(new PUSHInstruction(LR.getRegister()));

    /* Print '[' */
    this.instructions.add(new PUSHInstruction(r0.getRegister()));
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            new Immediate(91), BYTE));
    this.instructions.add(new BranchInstruction(BL, new Label("putchar")));
    this.instructions.add(new POPInstruction(r0.getRegister()));

    /* Generates the label for checking if the current node is null */
    this.instructions.add(new BranchInstruction(BL, firstLabel));

    /* Loads the address of the data of the current node in r0 and puts it
       on the stack */
    this.instructions.add(secondLabel);
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            OffsetRegister.preIndexedOffset(r0.getRegister(),
                    new ConstantOffset(4)), WORD));
    this.instructions.add(new PUSHInstruction(r0.getRegister()));

    /* Loads the data into r0 and branches to the specific print method
       based on the data type */
    ElementSize elementSize = type.getSize() == 4 ? WORD : BYTE;
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            OffsetRegister.noOffset(r0.getRegister()), elementSize));
    this.instructions.add(new BranchInstruction(BL, typeLabel));
    this.instructions.add(new POPInstruction(r0.getRegister()));

    /* Print a space */
    this.instructions.add(new PUSHInstruction(r0.getRegister()));
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            new Immediate(32), BYTE));
    this.instructions.add(new BranchInstruction(BL, new Label("putchar")));
    this.instructions.add(new POPInstruction(r0.getRegister()));

    /* Checks of the address of the current node is null */
    this.instructions.add(firstLabel);
    this.instructions.add(new LDRInstruction(r1.getRegister(),
            OffsetRegister.preIndexedOffset(r0.getRegister(),
                    new ConstantOffset(4)), WORD));
    this.instructions.add(new CMPInstruction(r1.getRegister(),
            new ConstantOffset(0)));
    this.instructions.add(new BranchInstruction(BNE, secondLabel));

    /* Print ']' */
    this.instructions.add(new PUSHInstruction(r0.getRegister()));
    this.instructions.add(new LDRInstruction(r0.getRegister(),
            new Immediate(93), BYTE));
    this.instructions.add(new BranchInstruction(BL, new Label("putchar")));
    this.instructions.add(new POPInstruction(r0.getRegister()));

    this.instructions.add(new POPInstruction(PC.getRegister()));
  }
}
