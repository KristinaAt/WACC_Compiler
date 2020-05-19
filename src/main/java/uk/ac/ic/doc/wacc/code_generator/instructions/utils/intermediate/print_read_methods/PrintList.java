package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods;

import uk.ac.ic.doc.wacc.ast.ASTNodes.Type;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.ManyOperands;

public class PrintList extends ManyOperands {

  private final Type type;

  public PrintList(Type type, Operand operand) {
    super(operand);

    this.type = type;
  }

  public Type getType() {
    return type;
  }
}
