package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods;

import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.ManyOperands;

public class CheckListBounds extends ManyOperands {

    public CheckListBounds(Operand... operands) {
        super(operands);
    }
}
