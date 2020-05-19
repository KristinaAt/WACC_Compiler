package uk.ac.ic.doc.wacc.code_generator.instructions.utils.runtime_error_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.MOVInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.AbstractUtilMethod;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.*;

public class RuntimeError extends AbstractUtilMethod {

    public RuntimeError() {
        super();
        /* Generates the label for throwing a runtime error */
        this.instructions.add(new Label(p_throw_runtime_error.toString()));

        /* Branches to the code for printing a corresponting message
           that is previously stored in r0 */
        this.instructions.add(new BranchInstruction(BL,
                new Label(p_print_string.toString())));

        /* Exits with an exit code -1 (255) */
        this.instructions.add(new MOVInstruction(r0.getRegister(),
                new ConstantOffset(-1)));
        this.instructions.add(new BranchInstruction(BL, new Label("exit")));
    }

}
