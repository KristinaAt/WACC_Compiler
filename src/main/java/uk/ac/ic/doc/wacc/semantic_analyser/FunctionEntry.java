package uk.ac.ic.doc.wacc.semantic_analyser;

import uk.ac.ic.doc.wacc.ast.ASTNodes.Type;

import java.util.ArrayList;
import java.util.List;

public class FunctionEntry {

  private SymbolTable<VariableDeclaration> symbolTable;
  private List<Type> types;
  private Type returnType;
  private String label;

  public FunctionEntry(Type returnType) {
    this.types = new ArrayList<>();
    this.returnType = returnType;
  }

  public FunctionEntry(Type returnType, List<Type> types) {
    this.returnType = returnType;
    this.types = types;
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof FunctionEntry)) {
      return false;
    }

    FunctionEntry functionEntry = (FunctionEntry) obj;
    List<Type> argTypes = functionEntry.getTypes();

    /* Checks if the return types are the same. */
    if(!(returnType.equals(functionEntry.getReturnType()))) {
      return false;
    }

    /* Checks the argument number match */
    if(functionEntry.getTypes().size() != getTypes().size()) {
      return false;
    }

    /* Checks the ordering and types of the arguments match for both */
    for(int i = 0; i < argTypes.size(); i++) {
      if(!(argTypes.get(i).equals(types.get(i)))){
        return false;
      }
    }

    return true;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Type getReturnType() {
    return returnType;
  }

  public void addType(Type type) {
    types.add(type);
  }

  public List<Type> getTypes() {
    return types;
  }

  public SymbolTable<VariableDeclaration> getSymbolTable() {
    return symbolTable;
  }

  public void setSymbolTable(SymbolTable<VariableDeclaration> symbolTable) {
    this.symbolTable = symbolTable;
  }

  /* Used for printing a specific semantic error. Needs to be passed the
     function name and will return the function declaration that was
     expected */
  public String getFunctionDeclaration(String name) {
    StringBuilder stringBuilder = new StringBuilder();

    /* Prints type funcName(type1, type2, type3) */

    stringBuilder.append(returnType + " ");
    stringBuilder.append(name);
    stringBuilder.append("(");
    for(int i = 0; i < types.size() - 1; i++) {
      stringBuilder.append(types.get(i) + ", ");
    }
    if(!types.isEmpty()) {
      stringBuilder.append(types.get(types.size() - 1));
    }
    stringBuilder.append(")");

    return stringBuilder.toString();
  }

}
