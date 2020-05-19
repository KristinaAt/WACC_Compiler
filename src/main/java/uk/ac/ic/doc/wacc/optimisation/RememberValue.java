package uk.ac.ic.doc.wacc.optimisation;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType;
import uk.ac.ic.doc.wacc.ast.ASTNodes.RightHandSideAssignment;
import uk.ac.ic.doc.wacc.semantic_analyser.VariableDeclaration;

import java.util.Optional;

/* A visitor used for updating a variable declaration based on the type of
   the rhs field if possible */
public class RememberValue extends ASTBaseVisitor<Void> {

  private VariableDeclaration varDeclaration;
  private RightHandSideAssignment rhs;

  public RememberValue(VariableDeclaration varDeclaration,
                       RightHandSideAssignment rhs) {
    this.varDeclaration = varDeclaration;
    this.rhs = rhs;
  }

  /* Based on the type of the lhs of a variable declaration or an assignment
     node we visit the rhs with the appropriate visitor type, get the value and
      update the lhs variable declaration. We only update if the visitors that
      get the value do not return null. */
  @Override
  public Void visitBaseType(BaseType node) {
    boolean forget = true;
    switch(node.getCategory()) {
      case INT:
        Integer intVal = rhs.visit(new GetIntValueVisitor());
        if(intVal != null) {
          varDeclaration.setValue(Optional.of(intVal.toString()));
          forget = false;
        }
        break;
      case CHAR:
        Character charVal = rhs.visit(new GetCharValueVisitor());
        if(charVal != null) {
          varDeclaration.setValue(Optional.of(charVal.toString()));
          forget = false;
        }
        break;
      case BOOL:
        Boolean boolVal = rhs.visit(new GetBoolValueVisitor());
        if(boolVal != null) {
          varDeclaration.setValue(Optional.of(boolVal.toString()));
          forget = false;
        }
        break;
    }

    /* If we have not managed to update the value then we "forget" it
       by assigning it an empty optional */
    if(forget) {
      varDeclaration.setValue(Optional.empty());
    }

    return null;
  }
}
