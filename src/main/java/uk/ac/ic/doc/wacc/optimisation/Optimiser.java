package uk.ac.ic.doc.wacc.optimisation;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.ast.ASTNodes.*;
import uk.ac.ic.doc.wacc.semantic_analyser.VariableDeclaration;

import java.util.Optional;

public class Optimiser extends ASTBaseVisitor<ASTNode> {
    /* Get value visitors used to return the value of an int, bool or char
       node.  */
    private final GetIntValueVisitor getIntValueVisitor;
    private final GetBoolValueVisitor getBoolValueVisitor;
    private final GetCharValueVisitor getCharValueVisitor;

    /* If the remember is set to false we cannot use the variable declarations
       within an assignment and declaration node for optimisation. */
    private boolean remember;

    public Optimiser() {
        this.getIntValueVisitor = new GetIntValueVisitor();
        this.getBoolValueVisitor = new GetBoolValueVisitor();
        this.getCharValueVisitor = new GetCharValueVisitor();
        this.remember = true;
    }

    @Override
    public ASTNode visitProgNode(ProgNode node) {
        /* Recursively visits the body of the program and optimises it. */
        BodyNode bodyNode = (BodyNode) node.getBody().visit(this);

        /* Recursively visits all function bodies and optimises them. */
        for(FunctionNode functionNode : node.getFunctionNodes()) {
            BodyNode funcBody =
                    (BodyNode) functionNode.getBodyNode().visit(this);
            functionNode.setBodyNode(funcBody);
        }

        node.setBody(bodyNode);

        return node;
    }

    @Override
    public ASTNode visitExitNode(ExitNode node) {
        /* Recursively visits the exit expression and optimises it. */
        ExpressionNode expr = (ExpressionNode)
                node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitIdentifierNode(IdentifierNode node) {
        VariableDeclaration varDeclaration = node.getVariableDeclaration();

        /* If we are in a state where we do not remember the value of the
           identifier because of a possible change we set its variable
           declaration value to an empty optional, increment the number
           of its usages and return the node. */
        if(!remember) {
            varDeclaration.setValue(Optional.empty());
            varDeclaration.incrementCount();
            return node;
        }

        /* Gets the value of the var declaration of the identifier. */
        Optional<String> optionalValue =
                varDeclaration.getValue();

        /* If the value is an empty optional then its value has been changed
           and we increment its usages and return the node. */
        if(optionalValue.isEmpty()) {
            varDeclaration.incrementCount();
            return node;
        }

        /* We are in a remember state and we know the actual value of the
           identifier. */
        String value = optionalValue.get();

        /* If the value is of type int bool or char we create a new node
           of the corresponding type.

           int a = 5 ;       --->       int a = 5 ;
           int b = a + 3                int b = 5 + 3
         */
        switch(varDeclaration.getType().toString()) {
            case "INT":
                return new IntNode(Integer.parseInt(value));
            case "CHAR":
                return new CharNode(value.charAt(0));
            case "BOOL":
                return new BoolNode(Boolean.valueOf(value));
        }

        /* Increments the usage count of the variable declaration */
        varDeclaration.incrementCount();
        return node;
    }

    @Override
    public ASTNode visitCompoundNode(CompoundNode node) {
        /* Recursively visits the lhs and rhs and optimises them. */
        BodyNode lhs = (BodyNode) node.getLeftBody().visit(this);
        BodyNode rhs = (BodyNode) node.getRightBody().visit(this);

        /* Sets the lhs and rhs to the optimised versions */
        node.setLeftBody(lhs);
        node.setRightBody(rhs);

        return node;
    }

    @Override
    public ASTNode visitSkipNode(SkipNode node) {
        return node;
    }

    @Override
    public ASTNode visitDeclarationNode(DeclarationNode node) {
        /* Gets the rhs and the variable declaration of a node. */
        RightHandSideAssignment rhs =
                (RightHandSideAssignment) node.getRhs().visit(this);

        VariableDeclaration varDeclaration =
                node.getVariableDeclaration();

        /* We always increment if the rhs is a call statement. That happens
           because even if the only usage of a variable may be when we assign
           a call statement to it, we want to retain the called function output.

           int a = call f()
           If a is nowhere used we still want to keep that declaration as
           the call to a function may print something to stdout.*/
        if(node.getRhs() instanceof CallStatement) {
            varDeclaration.incrementCount();
        }

        /* If we are in a state where we remember, we update the
           variable declaration. Otherwise, we increment the usage count of
            the variable declaration.*/
        if(remember) {
            RememberValue rememberValue =
                    new RememberValue(varDeclaration, rhs);
            varDeclaration.getType().visit(rememberValue);
        } else {
            varDeclaration.incrementCount();
        }

        node.setRhs(rhs);

        return node;
    }

    @Override
    public ASTNode visitIntNode(IntNode node) {
        return node;
    }

    @Override
    public ASTNode visitPrintNode(PrintNode node) {
        /* Recursively visits the print expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitPrintLnNode(PrintLnNode node) {
        /* Recursively visits the print expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitBeginNode(BeginNode node) {
        /* Recursively visits the body and optimises it. */
        BodyNode bodyNode = (BodyNode) node.getBody().visit(this);

        node.setBody(bodyNode);

        return node;
    }

    @Override
    public ASTNode visitWhileNode(WhileNode node) {
        /* Sets remember to false because we do not know what changes will
           occur while executing the while statement. */
        remember = false;
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);
        BodyNode bodyNode = (BodyNode) node.getBody().visit(this);

        Boolean condition = expr.visit(getBoolValueVisitor);

        /* Further simplifies the node if we get the condition value.
           Sets remember to true because we will exit the while loop.*/
        if(condition != null) {
            remember = true;

            /* If the condition is false we return a skip node. */
            if(!condition) {
                return new SkipNode();
            }
        }

        node.setExpression(expr);
        node.setBody(bodyNode);

        /* Sets remember to true because we will exit the while loop. */
        remember = true;
        return node;
    }

    @Override
    public ASTNode visitIfStatement(IfStatement node) {
        remember = true;

        /* Simplifies the condition. */
        ExpressionNode expr =
                (ExpressionNode) node.getCondition().visit(this);

        /* Sets remember to false because we do not know what changes will
           occur while executing the while statement. */
        remember = false;

        /* Simplifies the true and false bodies. */
        BodyNode trueBodyNode = (BodyNode) node.getTrueBody().visit(this);
        BodyNode falseBodyNode = (BodyNode) node.getFalseBody().visit(this);

        /* Tries to get the value of the condition. */
        Boolean condition = expr.visit(getBoolValueVisitor);

        /* Further simplifies the node if we get the condition value.
           Sets remember to true because we will exit the if statement.*/
        if(condition != null) {
            remember = true;

            if(condition) {
                return trueBodyNode;
            } else {
                return falseBodyNode;
            }
        }

        node.setCondition(expr);
        node.setTrueBody(trueBodyNode);
        node.setFalseBody(falseBodyNode);

        /* Sets remember to true because we will exit the if loop. */
        remember = true;
        return node;
    }

    @Override
    public ASTNode visitAssignmentNode(AssignmentNode node) {
        /* Simplifies the rhs. */
        RightHandSideAssignment rhs =
                (RightHandSideAssignment) node.getRhs().visit(this);

        node.getLhs().visit(this);

        /* If the lhs is not an identifier we update the optimised rhs and
           return the node. */
        if(!(node.getLhs() instanceof IdentifierNode)) {
            node.setRhs(rhs);
            return node;
        }

        IdentifierNode identifierNode = (IdentifierNode) node.getLhs();
        VariableDeclaration varDeclaration =
                identifierNode.getVariableDeclaration();

        /* We always increment if the rhs is a call statement. That happens
           because even if the only usage of a variable may be when we assign
           a call statement to it, we want to retain the called function output.
           int a = call f() ;
           If a is nowhere used we still want to keep that declaration as
           the call to a function may print something to stdout.*/
        if(node.getRhs() instanceof CallStatement) {
            varDeclaration.incrementCount();
        }

        /* If we are in a state where we remember, we update the
        variable declaration. Otherwise, we set the variable value to an
        empty optional and increment the usage count of the variable
        declaration. */
        if(remember) {
            RememberValue rememberValue =
                    new RememberValue(varDeclaration, rhs);
            varDeclaration.getType().visit(rememberValue);
        } else {
            varDeclaration.setValue(Optional.empty());
            varDeclaration.incrementCount();
        }

        node.setRhs(rhs);

        return node;
    }

    @Override
    public ASTNode visitReturnNode(ReturnNode node) {
        /* Recursively visits the return expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitFreeNode(FreeNode node) {
        /* Visits the expression so that we increment the number of usages, */
        node.getExpression().visit(this);

        return node;
    }

    @Override
    public ASTNode visitReadNode(ReadNode node) {
        /* Sets remembering to false so that it increments the usage count
           when visiting the lhs expression the we are freeing. */
        remember = false;
        node.getLhs().visit(this);
        remember = true;

        return node;
    }

    @Override
    public ASTNode visitAddToListNode(AddToListNode node) {
        /* Visits the identifier and expression so that we increment the usage
           count if necessary. */
        node.getIdentifier().visit(this);
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitRemoveFromListNode(RemoveFromListNode node) {
        /* Visits the identifier and expression so that we increment the usage
           count if necessary. */
        node.getIdentifier().visit(this);
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitGetFromListNode(GetFromListNode node) {
        /* Visits the identifier and expression so that we increment the usage
           count if necessary. */
        node.getIdentifier().visit(this);
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitListContainsNode(ListContainsNode node) {
        /* Visits the identifier and expression so that we increment the usage
           count if necessary. */
        node.getIdentifier().visit(this);
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        node.setExpression(expr);

        return node;
    }

    /* The most basic nodes cannot be simplified further */
    @Override
    public ASTNode visitBoolNode(BoolNode node) {
        return node;
    }

    @Override
    public ASTNode visitCharNode(CharNode node) {
        return node;
    }

    @Override
    public ASTNode visitStringNode(StringNode node) {
        return node;
    }

    @Override
    public ASTNode visitPairLiterNode(PairLiterNode node) {
        return node;
    }

    @Override
    public ASTNode visitArrayElement(ArrayElement node) {
        /* Visits the identifier so that we increment the usage count
           if necessary. */
        node.getIdentifierNode().visit(this);

        /* Returns the node if the expression nodes array is empty. */
        ExpressionNode[] expressionNodes = node.getExpressionNodes();
        if(expressionNodes == null) {
            return node;
        }

        /* Creates an array of expression nodes that will be populated with
           the optimised version. */
        ExpressionNode[] newExpressionNodes =
                new ExpressionNode[expressionNodes.length];

        /* Recursively visits each expression node and populates the new
           array. */
        int i = 0;
        for(ExpressionNode expressionNode : expressionNodes) {
            ExpressionNode expr = (ExpressionNode) expressionNode.visit(this);
            newExpressionNodes[i] = expr;
            i++;
        }

        node.setExpressionNodes(newExpressionNodes);

        return node;
    }

    @Override
    public ASTNode
    visitArithmeticExpressionNode(ArithmeticExpressionNode node) {
        /* Recursively visits the lhs and rhs and optimises them. */
        ExpressionNode lhs = (ExpressionNode) node.getLeft().visit(this);
        ExpressionNode rhs = (ExpressionNode) node.getRight().visit(this);

        node.setLeft(lhs);
        node.setRight(rhs);

        /* Tries to get the values of the lhs and rhs. */
        Integer leftVal = lhs.visit(getIntValueVisitor);
        Integer rightVal = rhs.visit(getIntValueVisitor);

        /* If we managed to get the actual values of the lhs ad rhs, we create
           a new int node.
           int a = 3 + 4    --->   int a = 7 */
        try {
            if (leftVal != null && rightVal != null) {
                switch (node.getOperator()) {
                    case PLUS:
                        return new IntNode(Math.addExact(leftVal, rightVal));
                    case MULTIPLY:
                        return new IntNode(Math.multiplyExact(leftVal,
                                rightVal));
                    case BIN_MINUS:
                        return new IntNode(Math.subtractExact(leftVal,
                                rightVal));
                    case MOD:
                        return new IntNode(leftVal % rightVal);
                    case DIVIDE:
                        return new IntNode(leftVal / rightVal);
                    case SHIFT_L:
                        return new IntNode(leftVal << rightVal);
                    case SHIFT_R:
                        return new IntNode(leftVal >> rightVal);
                    case BIT_OR:
                        return new IntNode(leftVal | rightVal);
                    case BIT_AND:
                        return new IntNode(leftVal & rightVal);
                    case BIT_EOR:
                        return new IntNode(leftVal ^ rightVal);
                }
            }
            /* If we catch an error we return the node so that we can produce
               a runtime error code during code generation. */
        } catch (ArithmeticException e) {
            return node;
        }

        return node;
    }

    @Override
    public ASTNode visitCompareExpressionNode(CompareExpressionNode node) {
        /* Recursively visits the lhs and rhs and optimises them. */
        ExpressionNode lhs = (ExpressionNode) node.getLeft().visit(this);
        ExpressionNode rhs = (ExpressionNode) node.getRight().visit(this);

        node.setLeft(lhs);
        node.setRight(rhs);

        BoolNode boolNode = null;

        /* If the type of the lhs and rhs are int, bool or char we branch
           and further simplify. */
        switch(node.getType().toString()) {
            case "INT":
                boolNode = simplifyIntExpression(node);
                break;
            case "CHAR":
                boolNode = simplifyCharExpression(node);
                break;
            case "BOOL":
                boolNode = simplifyBoolExpression(node);
                break;
        }

        /* If we managed to simplify we return the new node. */
        if(boolNode != null) {
            return boolNode;
        }

        return node;
    }

    BoolNode simplifyIntExpression(CompareExpressionNode node) {
        /* Gets the int values of the lhs and rhs if possible. */
        Integer lhs = node.getLeft().visit(getIntValueVisitor);
        Integer rhs = node.getRight().visit(getIntValueVisitor);

        /* If we managed to get the values, we return a simplified
           bool node based on the operator. */
        if(lhs != null && rhs != null) {
            switch (node.getOperator()) {
                case EQ:
                    return new BoolNode(lhs.equals(rhs));
                case NOT_EQ:
                    return new BoolNode(!lhs.equals(rhs));
                case GREATER_THAN:
                    return new BoolNode(lhs > rhs);
                case GREATER_EQ:
                    return new BoolNode(lhs >= rhs);
                case LESS_THAN:
                    return new BoolNode(lhs < rhs);
                case LESS_EQ:
                    return new BoolNode(lhs <= rhs);
            }
        }

        return null;
    }

    BoolNode simplifyCharExpression(CompareExpressionNode node) {
        /* Gets the char values of the lhs and rhs if possible. */
        Character lhs = node.getLeft().visit(getCharValueVisitor);
        Character rhs = node.getRight().visit(getCharValueVisitor);

        /* If we managed to get the values, we return a simplified
           bool node based on the operator. */
        if(lhs != null && rhs != null) {
            switch (node.getOperator()) {
                case EQ:
                    return new BoolNode(lhs.equals(rhs));
                case NOT_EQ:
                    return new BoolNode(!lhs.equals(rhs));
                case GREATER_THAN:
                    return new BoolNode(lhs > rhs);
                case GREATER_EQ:
                    return new BoolNode(lhs >= rhs);
                case LESS_THAN:
                    return new BoolNode(lhs < rhs);
                case LESS_EQ:
                    return new BoolNode(lhs <= rhs);
            }
        }

        return null;
    }

    BoolNode simplifyBoolExpression(CompareExpressionNode node) {
        /* Gets the bool values of the lhs and rhs if possible. */
        Boolean lhs = node.getLeft().visit(getBoolValueVisitor);
        Boolean rhs = node.getRight().visit(getBoolValueVisitor);

        /* If we managed to get the values, we return a simplified
           bool node based on the operator. */
        if(lhs != null && rhs != null) {
            switch (node.getOperator()) {
                case EQ:
                    return new BoolNode(lhs.equals(rhs));
                case NOT_EQ:
                    return new BoolNode(!lhs.equals(rhs));
            }
        }

        return null;
    }

    @Override
    public ASTNode visitBoolExpressionNode(BoolExpressionNode node) {
        /* Recursively visits the lhs and rhs and optimises them. */
        ExpressionNode lhs = (ExpressionNode) node.getLeft().visit(this);
        ExpressionNode rhs = (ExpressionNode) node.getRight().visit(this);

        node.setLeft(lhs);
        node.setRight(rhs);

        /* Tries to get the values of the lhs and rhs. */
        Boolean leftVal = lhs.visit(getBoolValueVisitor);
        Boolean rightVal = rhs.visit(getBoolValueVisitor);

        /* If we managed to get the values, we return a simplified
           bool node based on the operator. */
        if (leftVal != null && rightVal != null) {
            switch (node.getOperator()) {
                case AND:
                    return new BoolNode(leftVal && rightVal);
                case OR:
                    return new BoolNode(leftVal || rightVal);
            }
        }

        return node;
    }

    @Override
    public ASTNode visitNotUnaryExpressionNode(NotUnaryExpressionNode node) {
        /* Recursively visits the expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        /* Tries to get the boolean value of the expression. */
        Boolean val = expr.visit(getBoolValueVisitor);

        /* If we managed to get the value we return a simplifies node. */
        if(val != null) {
            return new BoolNode(!val);
        }

        node.setExpression(expr);

        return node;
    }

    @Override
    public
    ASTNode visitBitNotUnaryExpressionNode(BitNotUnaryExpressionNode node) {
        /* Recursively visits the expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        /* Tries to get the int value of the expression. */
        Integer val = expr.visit(getIntValueVisitor);

        /* If we managed to get the value we return a simplifies node. */
        if (val != null) {
            return new IntNode(~val);
        }

        node.setExpression(expr);
        return node;
    }

    @Override
    public ASTNode visitNegUnaryExpressionNode(NegUnaryExpressionNode node) {
        /* Recursively visits the expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        /* Tries to get the int value of the expression. */
        Integer val = expr.visit(getIntValueVisitor);

        try {
            /* If we managed to get the value we return a simplifies node. */
            if (val != null) {
                return new IntNode(Math.negateExact(val));
            }
            /* If we catch an error we return the node so that we can produce
            a runtime error code during code generation. */
        } catch (ArithmeticException e){
            node.setExpression(expr);
            return node;
        }

        node.setExpression(expr);
        return node;
    }

    @Override
    public ASTNode visitLenUnaryExpressionNode(LenUnaryExpressionNode node) {
        /* Visits the identifier so that we increment the usage count
           if necessary. */
        node.getIdentifierNode().visit(this);

        return node;
    }

    @Override
    public ASTNode visitOrdUnaryExpressionNode(OrdUnaryExpressionNode node) {
        /* Recursively visits the expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        /* Tries to get the char value of the expression. */
        Character val = expr.visit(getCharValueVisitor);

        /* If we managed to get the value we return a simplifies node. */
        if(val != null) {
            return new IntNode(Integer.valueOf(val));
        }

        node.setExpression(expr);
        return node;
    }

    @Override
    public ASTNode visitChrUnaryExpressionNode(ChrUnaryExpressionNode node) {
        /* Recursively visits the expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getExpression().visit(this);

        /* Tries to get the int value of the expression. */
        Integer val = expr.visit(getIntValueVisitor);

        /* If we managed to get the value we return a simplifies node. */
        if(val != null) {
            int value = val;
            return new CharNode((char) value);
        }

        node.setExpression(expr);
        return node;
    }

    @Override
    public ASTNode visitListSizeExpressionNode(ListSizeExpressionNode node) {
        /* Recursively visits the expression and optimises it. */
        node.getExpression().visit(this);

        return node;
    }

    @Override
    public ASTNode visitPairElement(PairElement node) {
        /* Recursively visits the expression and optimises it. */
        ExpressionNode expr =
                (ExpressionNode) node.getPairExpression().visit(this);

        node.setPairExpression(expr);

        return node;
    }

    @Override
    public ASTNode visitArrayLiteral(ArrayLiteral node) {
        ExpressionNode[] expressionNodes = node.getExpressionNodes();

        if(expressionNodes == null) {
            return node;
        }

        /* Creates an array of expression nodes that will be populated with
           the optimised version. */
        ExpressionNode[] newExpressionNodes =
                new ExpressionNode[expressionNodes.length];

        /* Recursively visits each expression node and populates the new
           array. */
        int i = 0;
        for(ExpressionNode expressionNode : expressionNodes) {
            ExpressionNode expr = (ExpressionNode) expressionNode.visit(this);
            newExpressionNodes[i] = expr;
            i++;
        }

        node.setExpressionNodes(newExpressionNodes);

        return node;
    }

    @Override
    public ASTNode visitNewListStatement(NewListStatement node) {
        return node;
    }

    @Override
    public ASTNode visitNewPairStatement(NewPairStatement node) {
        /* Recursively visits the first and second expression and
           optimises them. */
        ExpressionNode firstExpr =
                (ExpressionNode) node.getFirstExpression().visit(this);
        ExpressionNode secondExpr =
                (ExpressionNode) node.getSecondExpression().visit(this);

        node.setFirstExpression(firstExpr);
        node.setSecondExpression(secondExpr);

        return node;
    }

    @Override
    public ASTNode visitCallStatement(CallStatement node) {
        /* Returns the node if we do not have arg list to optimise. */
        ArgList argList = node.getArgumentList();
        if(argList == null) {
            return node;
        }

        ExpressionNode[] expressionNodes = argList.getExpressionNodes();

        if(expressionNodes == null) {
            return node;
        }

        /* Creates an array of expression nodes that will be populated with
           the optimised version. */
        ExpressionNode[] newExpressionNodes =
                new ExpressionNode[expressionNodes.length];

        /* Recursively visits each expression node and populates the new
           array. */
        int i = 0;
        for(ExpressionNode expressionNode : expressionNodes) {
            ExpressionNode expr = (ExpressionNode) expressionNode.visit(this);
            newExpressionNodes[i] = expr;
            i++;
        }

        node.getArgumentList().setExpressionNodes(newExpressionNodes);

        return node;
    }
}
