package uk.ac.ic.doc.wacc.semantic_analyser;

import uk.ac.ic.doc.wacc.ast.ASTNodes;
import uk.ac.ic.doc.wacc.ast.ASTNodes.Type;

import java.util.Optional;

public class VariableDeclaration implements Comparable<VariableDeclaration> {

    private Optional<String> value;
    private final Type type;
    private final boolean isParameter;
    private int stackPos;
    private int count;

    public VariableDeclaration(String variableName, Type type) {
        this(variableName, type, false);
    }

    public VariableDeclaration(String variableName, Type type,
        boolean isParameter) {
        this.type = type;
        this.isParameter = isParameter;
        this.value = Optional.empty();
    }

    public int getStackPos() {
        return stackPos;
    }

    public void setStackPos(int stackPos) {
        this.stackPos = stackPos;
    }

    public Type getType() {
        return type;
    }

    public boolean isParameter() {
        return isParameter;
    }

    @Override
    public int compareTo(VariableDeclaration variableDeclaration) {
        return -getType().compareTo(variableDeclaration.getType());
    }

    public Optional<String> getValue() {
        return value;
    }

    public void setValue(Optional<String> value) {
        this.value = value;
    }

    public void incrementCount() {
        this.count++;
    }

    public int getCount() {
        return count;
    }
}
