package uk.ac.ic.doc.wacc.ast;

import uk.ac.ic.doc.wacc.ast.ASTNodes.*;

/*  The following interface defines an ASTVisitor and what is required.

    Please read the comment in ASTNodes.java first.

    For each visitor method, we now have the node available to use.

    To specify a visit, we need to call the visit method.

    Now as each kind of node implements visit(Visitor visitor), we can
    call node.visit(some visitor). For example, writing node.visit(this)
    will continue using the current visitor. Writing node.visit(other) will
    visit the node using the visitor named "other".

    Let's take, for example, the idea that we are currently defining the
    method visitProgNode(ProgNode node). A ProgNode contains a BodyNode,
    which can be accessed by writing node.getBody().

    Now if we wish to visit the body node, we need to call the visit method
    from the body node. The visit method requires a visitor to use to
    continue a visitor. This means we write node.getBody().visit(this) if
    we wish to continue using the same visitor. Or if we want to use
    another visitor (e.g. GetExpressionTypeVisitor) then we write
    node.getBody().visit(getExpressionType).

    This will go to ASTNodes, and find the overridden method. Let's suppose
    the body node was a declaration node. Then inside declaration node,
    we find that the method tells us we need to call is visitDeclarationNode.
    As a result, we return the result from doing visitor.visitDeclarationNode
    passing the declaration node as an argument.

    The flow is of control is as follows:
    visitProgNode(ProgNode node) ->
        node.getBodyNode().visit(this) (this refers to the visitor) ->
            (Assume the body node is a compound node)
            Inside overridden method of compound node:
            visitCompoundNode(this) (this refers to the compound node) ->
                visitCompoundNode(Compound node)
                We are now in the correct function call.
 */

public interface ASTVisitor<T> {

  // Top Level Node visitor methods
  T visitProgNode(ProgNode node);

  T visitFunctionNode(FunctionNode node);

  T visitParamNode(ParamNode node);

  T visitParamList(ParamList node);

  T visitArgList(ArgList node);

  // Body Node visitor methods
  T visitBeginNode(BeginNode node);

  T visitCompoundNode(CompoundNode node);

  T visitWhileNode(WhileNode node);

  T visitExitNode(ExitNode node);

  T visitSkipNode(SkipNode node);

  T visitIfStatement(IfStatement node);

  T visitAssignmentNode(AssignmentNode node);

  T visitDeclarationNode(DeclarationNode node);

  T visitPrintNode(PrintNode node);

  T visitPrintLnNode(PrintLnNode node);

  T visitReturnNode(ReturnNode node);

  T visitFreeNode(FreeNode node);

  T visitReadNode(ReadNode node);

  T visitAddToListNode(AddToListNode node);

  T visitRemoveFromListNode(RemoveFromListNode node);

  T visitGetFromListNode(GetFromListNode node);

  T visitListContainsNode(ListContainsNode node);

  // Type Node Visitor Methods
  T visitAnyType(AnyType node);

  T visitPair(Pair node);

  T visitBaseType(BaseType node);

  T visitArrayType(ArrayType node);

  T visitPairType(PairType node);

  T visitListType(ListType node);

  // Expression Node Visitor Methods
  T visitIntNode(IntNode node);

  T visitBoolNode(BoolNode node);

  T visitCharNode(CharNode node);

  T visitStringNode(StringNode node);

  T visitPairLiterNode(PairLiterNode node);

  T visitArrayElement(ArrayElement node);

  // Binary Expression Node Visitor Methods
  T visitArithmeticExpressionNode(ArithmeticExpressionNode node);

  T visitCompareExpressionNode(CompareExpressionNode node);

  T visitBoolExpressionNode(BoolExpressionNode node);

  T visitIdentifierNode(IdentifierNode node);

  // Unary Expression Node Visitor Methods
  T visitNotUnaryExpressionNode(NotUnaryExpressionNode node);

  T visitNegUnaryExpressionNode(NegUnaryExpressionNode node);

  T visitLenUnaryExpressionNode(LenUnaryExpressionNode node);

  T visitOrdUnaryExpressionNode(OrdUnaryExpressionNode node);

  T visitChrUnaryExpressionNode(ChrUnaryExpressionNode node);

  T visitBitNotUnaryExpressionNode(BitNotUnaryExpressionNode node);

  T visitListSizeExpressionNode(ListSizeExpressionNode node);

  // Right Hand Side Assignment Visitor Methods
  T visitPairElement(PairElement node);

  T visitArrayLiteral(ArrayLiteral node);

  T visitNewListStatement(NewListStatement node);

  T visitNewPairStatement(NewPairStatement node);

  T visitCallStatement(CallStatement node);
}
