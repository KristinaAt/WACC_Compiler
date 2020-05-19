package uk.ac.ic.doc.wacc.optimisation;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.ast.ASTNodes.BoolNode;

public class GetBoolValueVisitor extends ASTBaseVisitor<Boolean> {
  /* A GetBoolValueVisitor used for returning the boolean value of a
     bool node and null when visiting the rest of the nodes  */
  @Override
  public Boolean visitBoolNode(BoolNode node) {
    return node.isTrue();
  }
}
