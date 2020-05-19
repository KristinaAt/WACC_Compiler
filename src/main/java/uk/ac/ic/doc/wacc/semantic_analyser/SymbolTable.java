package uk.ac.ic.doc.wacc.semantic_analyser;

import uk.ac.ic.doc.wacc.ast.ASTNodes.ASTNode;
import java.util.*;

public class SymbolTable<T> {

  private final List<Map<String, T>> scopeHistory;
  private final List<VariableDeclaration> variableDeclarations;

  public SymbolTable(ASTNode mainNode) {
    this(mainNode, new HashMap<>());
  }

  public SymbolTable(ASTNode mainNode, Map<String, T> map) {
    this.scopeHistory = new ArrayList<>();
    this.scopeHistory.add(map);
    this.variableDeclarations = new ArrayList<>();
  }

  private Map<String, T> getScopeWithName(String name) {
    for (int i = scopeHistory.size() - 1; i >= 0; i--) {
      Map<String, T> scope = scopeHistory.get(i);
      if (scope.containsKey(name)) {
        return scope;
      }
    }
    return null;
  }

  public boolean exists(String name) {
    return getScopeWithName(name) != null;
  }

  public boolean add(String name, T type) {
    Map<String, T> newestScope = scopeHistory.get(scopeHistory.size() - 1);
    if (newestScope.containsKey(name)) {
      return false;
    }
    newestScope.put(name, type);
    return true;
  }

  public Optional<T> get(String name) {
    Map<String, T> scope = getScopeWithName(name);
    if (scope == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(scope.getOrDefault(name, null));
  }

  public boolean remove(String name) {
    Map<String, T> scope = getScopeWithName(name);
    if (scope == null) {
      return false;
    }

    return scope.remove(name) != null;
  }

  public void openScope(ASTNode node) {
    Map<String, T> scope = new HashMap<>();
    scopeHistory.add(scope);
  }

  public void closeScope() {
    scopeHistory.remove(scopeHistory.size() - 1);
  }

  public void addVariableDeclaration(VariableDeclaration variableDeclaration) {
      variableDeclarations.add(variableDeclaration);
  }

  public List<VariableDeclaration> getVariableDeclarations() {
    return variableDeclarations;
  }
}
