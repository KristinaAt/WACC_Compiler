package uk.ac.ic.doc.wacc.optimisation;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.ast.ASTNodes.CharNode;

public class GetCharValueVisitor extends ASTBaseVisitor<Character> {

  /* A GetCharValueVisitor used for returning the char value of a
     char node and null when visiting the rest of the nodes  */
  @Override
  public Character visitCharNode(CharNode node) {
    return node.getValue();
  }
}
