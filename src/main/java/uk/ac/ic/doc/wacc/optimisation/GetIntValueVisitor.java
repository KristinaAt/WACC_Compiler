package uk.ac.ic.doc.wacc.optimisation;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.ast.ASTNodes.IntNode;

public class GetIntValueVisitor extends ASTBaseVisitor<Integer> {

    /* A GetIntValueVisitor used for returning the value of a
       int node and null when visiting the rest of the nodes  */
    @Override
    public Integer visitIntNode(IntNode node) {
        return node.getValue();
    }

}
