package uk.ac.ic.doc.wacc;

import org.jmock.Mockery;
import uk.ac.ic.doc.wacc.ast.ASTNodes.ASTNode;
import uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType;
import uk.ac.ic.doc.wacc.ast.ASTNodes.BodyNode;
import uk.ac.ic.doc.wacc.ast.ASTNodes.Type;
import uk.ac.ic.doc.wacc.ast.ASTVisitor;
import uk.ac.ic.doc.wacc.semantic_analyser.SymbolTable;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.BOOL;
import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.INT;
import static org.junit.Assert.*;

public class SymbolTableTest {

  private static final String VARIABLE_1 = "test123";
  private static final Type TYPE_1 = new BaseType(INT);

  private static final String VARIABLE_2 = "aBc_";
  private static final Type TYPE_2 = new BaseType(BOOL);

  private static final String NEW_VARIABLE = "new_var";
  private static final Type NEW_TYPE = new BaseType(INT);

  private Map<String, Type> testMap;

  private final Mockery context = new Mockery();
  private final ASTNode mockNode = context.mock(ASTNode.class);

  private final BodyNode pseudoBodyNode = new BodyNode() {
    @Override
    public <T> T visit(ASTVisitor<T> visitor) {
      return null;
    }
  };

  @Before
  public void prepareMap() {
    testMap = new HashMap<>();
    testMap.put(VARIABLE_1, TYPE_1);
    testMap.put(VARIABLE_2, TYPE_2);
  }

  private SymbolTable<Type> prepareSymbolTable() {
    return new SymbolTable<>(mockNode, testMap);
  }

  private void checkVariable(SymbolTable<Type> symbolTable,
      String variableName,
      Type expectedType) {
    Optional<Type> optionalType = symbolTable.get(variableName);

    if (expectedType == null) {
      assertTrue(optionalType.isEmpty());
    } else {
      assertTrue(optionalType.isPresent());
      assertEquals(expectedType, optionalType.get());
    }
  }

  @Test
  public void existsReturnsFalseForNonExistentVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();
    assertFalse(symbolTable.exists(NEW_VARIABLE));
  }

  @Test
  public void existsReturnsTrueForExistingVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();
    assertTrue(symbolTable.exists(VARIABLE_1));
    assertTrue(symbolTable.exists(VARIABLE_2));
  }

  @Test
  public void addWorksCorrectlyAndReturnsTrueForNewVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();
    assertTrue(symbolTable.add(NEW_VARIABLE, NEW_TYPE));

    checkVariable(symbolTable, NEW_VARIABLE, NEW_TYPE);
  }

  @Test
  public void addWorksCorrectlyAndReturnsFalseForExistingVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    assertFalse(symbolTable.add(VARIABLE_1, TYPE_2));
    checkVariable(symbolTable, VARIABLE_1, TYPE_1);

    assertFalse(symbolTable.add(VARIABLE_2, TYPE_1));
    checkVariable(symbolTable, VARIABLE_2, TYPE_2);
  }

  @Test
  public void getReturnsExistingVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    checkVariable(symbolTable, VARIABLE_1, TYPE_1);
    checkVariable(symbolTable, VARIABLE_2, TYPE_2);
  }

  @Test
  public void getReturnsEmptyOptionalForNonExistentVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    checkVariable(symbolTable, NEW_VARIABLE, null);
  }

  @Test
  public void removeWorksCorrectlyAndReturnsTrueForExistingVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    assertTrue(symbolTable.remove(VARIABLE_1));
    checkVariable(symbolTable, VARIABLE_1, null);

    assertTrue(symbolTable.remove(VARIABLE_2));
    checkVariable(symbolTable, VARIABLE_2, null);
  }

  @Test
  public void removeWorksCorrectlyAndReturnsFalseForNonExistentVariable() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    assertFalse(symbolTable.remove(NEW_VARIABLE));
  }

  @Test
  public void removeWorksCorrectlyAndReturnsTrueForExistingVariableInWiderScope() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();
    symbolTable.openScope(pseudoBodyNode);

    assertTrue(symbolTable.remove(VARIABLE_1));
    checkVariable(symbolTable, VARIABLE_1, null);

    assertTrue(symbolTable.remove(VARIABLE_2));
    checkVariable(symbolTable, VARIABLE_2, null);
  }

  @Test
  public void getReturnsExistingVariableInWiderScope() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();
    symbolTable.openScope(pseudoBodyNode);

    checkVariable(symbolTable, VARIABLE_1, TYPE_1);
    checkVariable(symbolTable, VARIABLE_2, TYPE_2);
  }

  @Test
  public void existsReturnsTrueForExistingVariableInWiderScope() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();
    symbolTable.openScope(pseudoBodyNode);
    assertTrue(symbolTable.exists(VARIABLE_1));
    assertTrue(symbolTable.exists(VARIABLE_2));
  }

  @Test
  public void addWorksCorrectlyAndReturnsTrueForRedeclaredVariableInNarrowerScope() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    checkVariable(symbolTable, VARIABLE_1, TYPE_1);
    symbolTable.openScope(pseudoBodyNode);

    assertTrue(symbolTable.add(VARIABLE_1, TYPE_2));
    checkVariable(symbolTable, VARIABLE_1, TYPE_2);
  }

  @Test
  public void closeScopeRemovesNewVariablesInScope() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    symbolTable.openScope(pseudoBodyNode);

    assertTrue(symbolTable.add(NEW_VARIABLE, NEW_TYPE));
    checkVariable(symbolTable, NEW_VARIABLE, NEW_TYPE);

    symbolTable.closeScope();

    checkVariable(symbolTable, NEW_VARIABLE, null);
  }

  @Test
  public void closeScopeRestoresVariablesRedeclaredInScope() {
    SymbolTable<Type> symbolTable = prepareSymbolTable();

    symbolTable.openScope(pseudoBodyNode);

    assertTrue(symbolTable.add(VARIABLE_1, TYPE_2));
    checkVariable(symbolTable, VARIABLE_1, TYPE_2);

    symbolTable.closeScope();

    checkVariable(symbolTable, VARIABLE_1, TYPE_1);
  }
}
