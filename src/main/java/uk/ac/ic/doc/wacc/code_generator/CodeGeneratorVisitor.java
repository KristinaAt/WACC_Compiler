package uk.ac.ic.doc.wacc.code_generator;

import uk.ac.ic.doc.wacc.ast.ASTBaseVisitor;
import uk.ac.ic.doc.wacc.ast.ASTNodes.*;
import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateProgram;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Branch;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.StartFunction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.StartMain;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Add;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.And;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Compare;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Divide;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Malloc;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Mod;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Multiply;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Not;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Or;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Shift;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Subtract;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Xor;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Exit;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.GetReturn;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Move;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Move.Condition;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Pop;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Push;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Return;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Immediate;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.MemoryAtRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Register;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Shifted;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.MessageData;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.CheckListBounds;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.GetListElement;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.ListContains;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.RemoveFromList;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintBool;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintChar;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintInteger;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintList;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintLn;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintReference;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintString;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.ReadChar;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.ReadInteger;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckArrayBounds;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckDivideByZero;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckNullPointer;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckOverflow;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.GetLastNode;
import uk.ac.ic.doc.wacc.semantic_analyser.FunctionEntry;
import uk.ac.ic.doc.wacc.semantic_analyser.FunctionSymbolTable;
import uk.ac.ic.doc.wacc.semantic_analyser.SymbolTable;
import uk.ac.ic.doc.wacc.semantic_analyser.VariableDeclaration;


import java.util.*;

import static uk.ac.ic.doc.wacc.ast.ASTNodes.BaseType.Category.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.*;

public class CodeGeneratorVisitor
        extends ASTBaseVisitor<List<IntermediateInstruction>> {

  static final int WORD = 4;
  static final int BYTE = 1;

  /* A pair class to associate variable declarations with arguments */
  private static class Pair<T, U> {

    public final T first;
    public final U second;

    public Pair(T first, U second) {
      this.first = first;
      this.second = second;
    }
  }

  /* A list that stores which types will only use one byte of data. Useful
     for determining whether to use LDRB or LDR etc. */
  private static final List<Type> BYTE_TYPES =
      Arrays.asList(new BaseType(BOOL), new BaseType(CHAR));

  /* Message data contains all the strings that will be defined at the
     beginning of the assembly file */
  private List<IntermediateInstruction> messageData;

  /* The list of instructions for the main method */
  private List<IntermediateInstruction> bodyInstructions;

  /* The list of instructions for user defined functions */
  private List<IntermediateInstruction> userFunctions;

  /* Symbol tables from semantic analysis stage */
  private SymbolTable<VariableDeclaration> symbolTable;
  private final FunctionSymbolTable<FunctionEntry> functionEntrySymbolTable;

  /* Util methods for default assembly library code such as reading an
     integer etc. */
  private final HashMap<UtilMethodLabel, String[]> utilMethods;

  /* A global list of the available registers */
  private List<Register> usableRegisters;

  /* Variables to be able to generate unique labelling */
  private static int labelNumber;
  private static int messageNumber;
  private String functionExitLabel;

  /* Variable for keeping track of stack growth */
  private int stackSize;
  private int argStackSize;
  private int argStackSizeForCall;

  /* List used for functions and call statements */
  private List<VariableDeclaration> argDeclarationsForCall;

  private boolean optimise;
  private int registerCount;

  public CodeGeneratorVisitor(
      IntermediateProgram intermediateProgram,
      SymbolTable<VariableDeclaration> symbolTable,
      FunctionSymbolTable<FunctionEntry> functionEntrySymbolTable,
      boolean optimise) {
    this.messageData = intermediateProgram.getMessageData();
    this.bodyInstructions = intermediateProgram.getBodyInstructions();
    this.userFunctions = intermediateProgram.getUserFunctions();
    this.utilMethods = intermediateProgram.getUtilMethods();

    this.registerCount = 0;

    this.usableRegisters = new ArrayList<>();
    for (int i = 0;
        i < intermediateProgram.getArchitecture().getAvailableRegisterNumber();
        i++) {
      this.usableRegisters.add(new Register(this.registerCount++));
    }

    this.symbolTable = symbolTable;
    this.functionEntrySymbolTable = functionEntrySymbolTable;

    labelNumber = 0;
    messageNumber = 0;
    this.optimise = optimise;

    setStackPositions(symbolTable);
  }

  public static String getNextLabel() {
    String label = "label" + labelNumber;
    labelNumber++;
    return label;
  }

  public static String getNextMessageLabel() {
    String messageLabel = "msg_" + messageNumber;
    messageNumber++;
    return messageLabel;
  }

  /* Determines the offset for an LDR instruction when loading a variable
     based off of the variable declaration */
  static int getOffset(VariableDeclaration varDeclaration,
                       int stackSize) {
    return stackSize - varDeclaration.getStackPos() +
            (varDeclaration.isParameter() ? 4 : 0);
  }

  /* Returns instructions for growing a stack if it needs to be grown */
  private List<IntermediateInstruction> growStack(int amount) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    if(amount > 0) {
      Register firstRegister = usableRegisters.get(0);
      instructions.add(new Move(new Immediate(amount), firstRegister,
          Condition.NONE, 4));
      instructions.add(new Subtract(Register.STACK_POINTER, firstRegister,
          Register.STACK_POINTER, false));
    }
    return instructions;
  }

  /* Returns instructions for shrinking a stack if it needs to be shrunk */
  private List<IntermediateInstruction> shrinkStack(int amount) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    if(amount > 0) {
      Register firstRegister = usableRegisters.get(0);
      instructions.add(new Move(new Immediate(amount), firstRegister,
          Condition.NONE, WORD));
      instructions.add(new Add(Register.STACK_POINTER, firstRegister,
          Register.STACK_POINTER, false));
    }
    return instructions;
  }

  /* Returns instructions for loading a variable from the stack to a register */
  static List<IntermediateInstruction>
    loadVariable(Register register, VariableDeclaration variableDeclaration,
    int bytes, int stackSize, boolean optimise){
      if (optimise && variableDeclaration.getCount() == 0) {
        return new ArrayList<>();
      }

      int offset = getOffset(variableDeclaration, stackSize);

      return Collections.singletonList(new Move(
              new MemoryAtRegister(Register.STACK_POINTER, offset, false),
              register, Condition.NONE, bytes));
    }

  /* Returns instructions for saving a variable from a register to the stack */
  private static List<IntermediateInstruction> saveVariable(Register register,
      VariableDeclaration variableDeclaration, int bytes, int stackSize) {
    int offset = getOffset(variableDeclaration, stackSize);

    return Collections.singletonList(new Move(register,
        new MemoryAtRegister(Register.STACK_POINTER, offset, false),
        Condition.NONE, bytes));
  }

  /*
        -------------
        |   ARG1    |
        ------------
        |   ARG2    |
        -------------
        |RETURN ADDR|
        -------------
        |   VAR1    |
        -------------

     Iterates through the variable declaration assinging them positions on
     the stack. The stack will be setup similarly as above (the number of
     arguments and variables may differ). */
  public void setStackPositions(SymbolTable<VariableDeclaration> symbolTable) {
    int stackSize = 0;
    int argStackSize = 0;

    /* Tracks all the variables declarations - both arguments and local vars */
    List<VariableDeclaration> allDeclarations =
            new ArrayList<>(symbolTable.getVariableDeclarations());

    /* Seperates the variable declarations into argument declarations and
       local variable declarations */
    List<VariableDeclaration> argDeclarations = new ArrayList<>();
    List<VariableDeclaration> varDeclarations = new ArrayList<>();
    for (VariableDeclaration variableDeclaration : allDeclarations) {
      if(variableDeclaration.isParameter()) {
        variableDeclaration.incrementCount();
        argDeclarations.add(variableDeclaration);
      } else {
        varDeclarations.add(variableDeclaration);
      }
    }

    /* Sort the local variable declarations so that BYTE declarations are after
       WORD size declarations */
    Collections.sort(varDeclarations);

    /* Concatenates the sorted lists */
    allDeclarations = new ArrayList<>();
    allDeclarations.addAll(argDeclarations);
    allDeclarations.addAll(varDeclarations);

    /* Iterates through the sorted list, assigning stack positions to
       each variable, as shown in diagram above */
    for (VariableDeclaration variableDeclaration : allDeclarations) {
      if(optimise && variableDeclaration.getCount() == 0) {
        continue;
      }
      if (variableDeclaration.isParameter()) {
        int sizeOffset =
                BYTE_TYPES.contains(variableDeclaration.getType()) ? 1 : 4;
        argStackSize += sizeOffset;
        stackSize += sizeOffset;
      } else {
        stackSize =
                BYTE_TYPES.contains(variableDeclaration.getType()) ?
                        stackSize + 1 :
                        stackSize + 4;
      }
      variableDeclaration.setStackPos(stackSize);
    }

    /* Sets the new stack and argument stack sizes */
    this.stackSize = stackSize;
    this.argStackSize = argStackSize;
  }

  @Override
  public List<IntermediateInstruction> visitProgNode(ProgNode node) {
    /* Recursively generate and add assembly code for all
       user-defined functions */
    for (FunctionNode functionNode : node.getFunctionNodes()) {
      functionNode.visit(this);
    }

    /* Defines the main function */
    bodyInstructions.add(new StartMain());

    /* Increases the stack size in anticipation of variables being declared */
    bodyInstructions.addAll(growStack(stackSize));

    /* Recurse into the main body of the program and adds the
       instructions returned */
    bodyInstructions.addAll(node.getBody().visit(this));

    /* Decreases the stack size once complete */
    bodyInstructions.addAll(shrinkStack(stackSize));

    /* Performs the takedown instructions of the assembly code */
    bodyInstructions.add(new Return(new Immediate(0), null));

    return bodyInstructions;
  }

  @Override
  public List<IntermediateInstruction> visitFunctionNode(FunctionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();

    /* Sets up the labels that will be used by the function. _funcName to
      avoid clashes with other labels. */
    String name = node.getIdentifierNode().getName();
    String funcLabel = "_" + node.getLabel();
    functionExitLabel = "_exit_" + node.getLabel();

    /* Created a duplicate function entry of the node */
    List<Type> argsTypes = new ArrayList<>();

    if(node.getParamList() != null) {
      for (ParamNode paramNode : node.getParamList().getParamList()) {
        argsTypes.add(paramNode.getType());
      }
    }
    FunctionEntry functionEntryCheck =
            new FunctionEntry(node.getType(), argsTypes);

    /* Finds the symbol table entry for the function, throwing an error if
       function is not found. It is expected the function should be found */
    Optional<FunctionEntry> possibleFuncEntry =
            functionEntrySymbolTable.retrieveEntry(name, functionEntryCheck);
    SymbolTable<VariableDeclaration> functionSymbolTable;

    if (possibleFuncEntry.isPresent()) {
      functionSymbolTable = possibleFuncEntry.get().getSymbolTable();
    } else {
      throw new RuntimeException(
              "Could not get the function entry for "
                      + name
                      + ". Something must have gone terribly wrong.");
    }


    /* Keep track of the old stack and symbol table before recursively
       generating assembly for the user defined function. */
    int oldStackSize = stackSize;
    int oldArgStackSize = argStackSize;
    SymbolTable<VariableDeclaration> oldSymbolTable = symbolTable;

    /* Change the symbol table to the function's scope symbol table */
    symbolTable = functionSymbolTable;

    setStackPositions(functionSymbolTable);

    /* Add setup assembly instructions */
    instructions.add(new StartFunction(new Label(funcLabel)));

    instructions.addAll(growStack(stackSize - argStackSize));

    /* Recursively generates assembly code for the function body */
    instructions.addAll(node.getBodyNode().visit(this));

    /* Defines the exit assembly */
    instructions.add(new Label(functionExitLabel));

    instructions.addAll(shrinkStack(stackSize - argStackSize));

    /* Performs takedown of the function */
    instructions.add(new Return());

    /* Restores the old stack and symbol tables */
    symbolTable = oldSymbolTable;
    stackSize = oldStackSize;
    argStackSize = oldArgStackSize;

    userFunctions.addAll(instructions);
    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitArgList(ArgList node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Populates the list of pairs of variables and their corresponding
       expression */
    List<Pair<VariableDeclaration, ExpressionNode>> expressionNodes =
            new ArrayList<>();

    for (int i = 0; i < node.getExpressionNodes().length; i++) {
      VariableDeclaration argDeclaration = argDeclarationsForCall.get(i);
      ExpressionNode expressionNode = node.getExpressionNodes()[i];
      expressionNodes.add(new Pair<>(argDeclaration, expressionNode));
    }

    /* Sorts the pairs to match the stack positions assigned to them */
    expressionNodes.sort(Comparator.comparingInt(t -> t.first.getStackPos()));

    /* Iterates through the expressions, placing them onto the correct
       stack position. */
    for (Pair<VariableDeclaration, ExpressionNode> expressionNodePair :
            expressionNodes) {

      /* Put on stack based on the type of the argument */
      int elementSize =
          BYTE_TYPES.contains(expressionNodePair.first.getType()) ? BYTE : WORD;
      int sizeOffset = elementSize == BYTE ? 1 : 4;

      argStackSizeForCall += sizeOffset;

      /* Recursively visit the expression and generate assembly code. The
         result is in the first usable register */
      instructions.addAll(expressionNodePair.second.visit(this));

      /* Creates a STR instruction which simultaneously grows the stack */
      instructions.add(new Move(firstRegister,
          new MemoryAtRegister(Register.STACK_POINTER,
          -sizeOffset, true), Condition.NONE, elementSize));

      /* Ensures we take into account that the stack has grown */
      stackSize += sizeOffset;
    }

    /* Ensures we remember the old stack size for after the call
       statement finishes */
    stackSize -= argStackSizeForCall;

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitBeginNode(BeginNode node) {
    return node.getBody().visit(this);
  }

  @Override
  public List<IntermediateInstruction> visitCompoundNode(CompoundNode node) {
    /* Recursively visits the two bodies and returns their instructions */
    List<IntermediateInstruction> instructions = node.getLeftBody().visit(this);
    instructions.addAll(node.getRightBody().visit(this));
    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitWhileNode(WhileNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Generate two branching labels for the true and false bodies */
    String label1 = getNextLabel();
    String label2 = getNextLabel();

    instructions.add(new Branch(label1, Branch.Condition.NONE, false));

    /* Recursively generate assembly code for the body and
       corresponding label */
    instructions.add(new Label(label2));
    instructions.addAll(node.getBody().visit(this));

    /* Recursively generate assembly code for the condition and corresponding
       label */
    instructions.add(new Label(label1));
    instructions.addAll(node.getExpression().visit(this));

    /* Check if the condition was true or false. Branch to body if true */
    instructions.add(new Compare(firstRegister, new Immediate(1)));
    instructions.add(new Branch(label2, Branch.Condition.EQUAL, false));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitExitNode(ExitNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Recursively generate code for the expression, putting result in the
       first available register */
    instructions.addAll(node.getExpression().visit(this));

    /* Moves the exit code into r0 to perform an exit */
    instructions.add(new Exit(firstRegister));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitSkipNode(SkipNode node) {
    return new ArrayList<>();
  }

  @Override
  public List<IntermediateInstruction> visitIfStatement(IfStatement node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Generate assembly code for the condition, result in first register */
    instructions.addAll(node.getCondition().visit(this));

    /* Check if the result was true or false */
    instructions.add(new Compare(firstRegister, new Immediate(0)));

    /* Generate two branching labels for the true and false bodies */
    String label1 = getNextLabel();
    String label2 = getNextLabel();

    /* Generate branching for the two labels */
    instructions.add(new Branch(label1, Branch.Condition.EQUAL, false));

    /* Recursively generate assembly code for the true body */
    instructions.addAll(node.getTrueBody().visit(this));
    instructions.add(new Branch(label2, Branch.Condition.NONE, false));

    /* Recursively generate assembly code for the false body and
       corresponding label */
    instructions.add(new Label(label1));
    instructions.addAll(node.getFalseBody().visit(this));

    /* Label that we branch to to skip the false body */
    instructions.add(new Label(label2));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitAssignmentNode(AssignmentNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register srcRegister = usableRegisters.get(0);
    int elementSize;
    LeftHandSideAssignment lhs = node.getLhs();

    if(node.getLhs() instanceof IdentifierNode) {
      VariableDeclaration varDeclaration =
              ((IdentifierNode) node.getLhs()).getVariableDeclaration();
      if(optimise && varDeclaration.getCount() == 0) {
        return instructions;
      }
    }

    /* Recursively visits and hence receives the instructions of the RHS
       whose result will be in src register (see below) */
    instructions.addAll(node.getRhs().visit(this));

    /* Remove first register to avoid overwriting the result */
    usableRegisters.remove(0);
    Register firstRegister = usableRegisters.get(0);

    /* Based on the type of the LHS, we determine what assembly to generate */
    if (lhs instanceof PairElement) {
      PairElement pairElement = (PairElement) lhs;

      /* Dereference the pair placing the result in the correct register */
      instructions.addAll(dereferencePairElement(pairElement, firstRegister));
      instructions.add(new Move(srcRegister,
          new MemoryAtRegister(firstRegister, 0, false),
              Condition.NONE, WORD));

    } else if (lhs instanceof ArrayElement) {
      ArrayElement arrayElement = (ArrayElement) lhs;

      VariableDeclaration varDeclaration =
              arrayElement.getIdentifierNode().getVariableDeclaration();

      /* Dereference the array element placing the result in the correct
         register */
      instructions.addAll(dereferenceArray(arrayElement));

      /* If it is a char[] then the element size is BYTE - any other case,
         the element size is WORD. */
      if (varDeclaration.getType().equals(new ArrayType(new BaseType(CHAR)))) {
        elementSize = BYTE;
      } else {
        elementSize = WORD;
      }

      /* Store the result in the correct position based on element size */
      instructions.add(new Move(srcRegister,
          new MemoryAtRegister(firstRegister, 0, false), Condition.NONE,
          elementSize));

    } else {
      VariableDeclaration varDeclaration =
          ((IdentifierNode) lhs).getVariableDeclaration();

      /* Based on the type, we determine whether to use STRB or STR */
      if (BYTE_TYPES.contains(varDeclaration.getType())) {
        elementSize = BYTE;
      } else {
        elementSize = WORD;
      }

      /* Creates a STR instruction to store the value onto the stack
      The offset is determined by the stack position determined
      inside the variable declaration. */
      instructions.addAll(saveVariable(srcRegister, varDeclaration,
              elementSize, stackSize));
    }

    /* Restores the removed register */
    usableRegisters.add(0, srcRegister);
    return instructions;
  }

  @Override
  public
  List<IntermediateInstruction> visitDeclarationNode(DeclarationNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();

    if(optimise && node.getVariableDeclaration().getCount() == 0) {
      return instructions;
    }

    /* Recursively visits and hence receives the instructions of the RHS
    whose result will be in src register (see below)*/
    instructions = node.getRhs().visit(this);

    Register srcRegister = usableRegisters.get(0);
    int elementSize;

    /* Based on the type, we determine whether to use STRB or STR */
    if (BYTE_TYPES.contains(node.getVariableDeclaration().getType())) {
      elementSize = BYTE;
    } else {
      elementSize = WORD;
    }

    /* Creates a STR instruction to store the value onto the stack
    The offset is determined by the stack position determined
    inside the variable declaration. */
    instructions.addAll(saveVariable(srcRegister,
            node.getVariableDeclaration(), elementSize, stackSize));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitPrintNode(PrintNode node) {
    /* Generate instructions for expression by recursively visiting
       the expression */
    List<IntermediateInstruction> instructions =
            node.getExpression().visit(this);
    Register firstRegister = usableRegisters.get(0);

    /* Create a print method for the instruction based on the type */
    instructions.addAll(generatePrintMethod(firstRegister, node.getType()));
    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitPrintLnNode(PrintLnNode node) {
    /* Generate instructions for expression by recursively visiting the
       expression */
    List<IntermediateInstruction> instructions =
            node.getExpression().visit(this);
    Register firstRegister = usableRegisters.get(0);

    /* Create a print method for the instruction based on the type */
    instructions.addAll(generatePrintMethod(firstRegister, node.getType()));

    instructions.add(new PrintLn());

    /* Generate a print ln method if not already generated */
    if (!utilMethods.containsKey(p_print_ln)) {
      String msg_label = getNextMessageLabel();
      messageData.add(new MessageData(new Label(msg_label), "\"\\0\""));
      utilMethods.put(p_print_ln, new String[] {msg_label});
    }

    return instructions;
  }

  private List<IntermediateInstruction> generatePrintMethod(Register register,
      Type type) {
    List<IntermediateInstruction> instructions = new ArrayList<>();

    /* Creates a print method based on the type we are printing */
    /* Based on the type, create a message data for the type's format
       and put a corresponding printing method in the hash map. Also create a
       branch instruction to the newly defined method. */

    if(type instanceof ListType) {
      ListType listType = (ListType) type;
      generatePrintListMethod(register, listType, instructions);
      return instructions;
    }

    switch (type.toString()) {
      case ("INT"):
        instructions.add(new PrintInteger(register));
        if (!utilMethods.containsKey(p_print_int)) {
          String msg_label = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label), "\"%d\\0\""));
          utilMethods.put(p_print_int, new String[] {msg_label});
        }
        break;
      case ("STRING"):
        instructions.add(new PrintString(register));
        if (!utilMethods.containsKey(p_print_string)) {
          String msg_label = getNextMessageLabel();
          messageData.add(
                  new MessageData(new Label(msg_label), "\"%.*s\\0\""));
          utilMethods.put(p_print_string, new String[] {msg_label});
        }
        break;
      case ("BOOL"):
        instructions.add(new PrintBool(register));
        if (!utilMethods.containsKey(p_print_bool)) {
          String msg_label_1 = getNextMessageLabel();
          String msg_label_2 = getNextMessageLabel();
          messageData.add(
                  new MessageData(new Label(msg_label_1), "\"true\\0\""));
          messageData.add(
                  new MessageData(new Label(msg_label_2), "\"false\\0\""));
          utilMethods.put(p_print_bool,
                  new String[] {msg_label_1, msg_label_2});
        }
        break;
      case ("CHAR"):
        instructions.add(new PrintChar(register));
        break;
      default:
        instructions.add(new PrintReference(register));
        if (!utilMethods.containsKey(p_print_reference)) {
          String msg_label = getNextMessageLabel();
          messageData.add(
                  new MessageData(new Label(msg_label), "\"%p\\0\""));
          utilMethods.put(p_print_reference, new String[] {msg_label});
        }
        break;
    }
    return instructions;
  }

  private void generatePrintListMethod(Register register, ListType listType,
                          List<IntermediateInstruction> instructions) {
    Type type = listType.getType();
    generatePrintMethod(register, type);

    String firstLabel = getNextLabel();
    String secondLabel = getNextLabel();

    instructions.add(new PrintList(type, register));

    UtilMethodLabel methodLabel;

    switch(type.toString()) {
      case "INT":
        methodLabel = p_print_list_int;
        break;
      case "STRING":
        methodLabel = p_print_list_string;
        break;
      case "CHAR":
        methodLabel = p_print_list_char;
        break;
      case "BOOL":
        methodLabel = p_print_list_bool;
        break;
      default:
        methodLabel = p_print_list_reference;
        break;
    }

    if(!utilMethods.containsKey(methodLabel)) {
      utilMethods.put(methodLabel, new String[] {firstLabel, secondLabel});
    }

  }

  static void addRuntimeErrorMethod(
          HashMap<UtilMethodLabel, String[]> utilMethods,
          List<IntermediateInstruction> messageData, UtilMethodLabel label) {
    /* Add a new method for each potential label to the list of defined
       methods */
    switch (label) {
      case p_check_divide_by_zero:
        if (!utilMethods.containsKey(p_check_divide_by_zero)) {
          String msg_label = getNextMessageLabel();
          messageData.add(
                  new MessageData(new Label(msg_label),
                          "\"DivideByZeroError: divide or modulo\""));
          utilMethods.put(p_check_divide_by_zero,
                  new String[] {msg_label});
        }
        break;
      case p_throw_overflow_error:
        if (!utilMethods.containsKey(p_throw_overflow_error)) {
          String msg_label = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label),
                  "\"OverflowError: the result is too small/large " +
                          "to store in a 4-byte signed-integer.\\n\""));
          utilMethods.put(p_throw_overflow_error,
                  new String[] {msg_label});
        }
        break;
      case p_check_array_bounds:
        if (!utilMethods.containsKey(p_check_array_bounds)){
          String msg_label_1 = getNextMessageLabel();
          String msg_label_2 = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label_1),
                  "\"ArrayIndexOutOfBoundsError: negative index\\n\\0\""));
          messageData.add(new MessageData(new Label(msg_label_2),
                  "\"ArrayIndexOutOfBoundsError: index too large\\n\\0\""));
          utilMethods.put(p_check_array_bounds,
                  new String[] {msg_label_1, msg_label_2});
          break;
        }
      case p_check_null_pointer:
        if (!utilMethods.containsKey(p_check_null_pointer)) {
          String msg_label = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label),
                  "\"NullReferenceError: " +
                          "dereference a null reference\\n\\0\""));
          utilMethods.put(p_check_null_pointer,
                  new String[] {msg_label});
          break;
        }
      case p_free_pair:
        if (!utilMethods.containsKey(p_free_pair)) {
          String msg_label = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label),
                  "\"NullReferenceError: " +
                          "dereference a null reference\\n\\0\""));
          utilMethods.put(p_free_pair, new String[] {msg_label});
        }
        break;
      case p_free_array:
        if (!utilMethods.containsKey(p_free_array)) {
          String msg_label = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label),
                  "\"NullReferenceError: " +
                          "dereference a null reference\\n\\0\""));
          utilMethods.put(p_free_array, new String[] {msg_label});
        }
        break;
      case p_free_list:
        if (!utilMethods.containsKey(p_free_list)) {
          String firstLabel = getNextLabel();
          String secondLabel = getNextLabel();
          utilMethods.put(p_free_list, new String[] {firstLabel, secondLabel});
        }
        break;
      case p_check_list_bounds:
        if(!utilMethods.containsKey(p_check_list_bounds)) {
          /* Message label to report error */
          String messageLabel = getNextMessageLabel();
          messageData.add(new MessageData(new Label(messageLabel),
                  "\"Out of bounds access to list.\\n\""));
          utilMethods.put(p_check_list_bounds,
                  new String[] {messageLabel});
        }
        break;
    }

    /* Add printing of runtime errors if not already defined */
    if (!utilMethods.containsKey(p_throw_runtime_error)) {
      utilMethods.put(p_throw_runtime_error, new String[0]);
    }

    /* Add printing of strings if not already defined */
    if (!utilMethods.containsKey(p_print_string)) {
      String msg_label = getNextMessageLabel();
      messageData.add(new MessageData(new Label(msg_label), "\"%.*s\\0\""));
      utilMethods.put(p_print_string, new String[] {msg_label});
    }
  }

  @Override
  public List<IntermediateInstruction> visitReturnNode(ReturnNode node) {
    List<IntermediateInstruction> instructions =
            node.getExpression().visit(this);

    /* Branches to a function's corresponding exit label */
    instructions.add(new Return(usableRegisters.get(0), functionExitLabel));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitFreeNode(FreeNode node) {

    FreeCodeGeneratorVisitor freeCodeGeneratorVisitor =
            new FreeCodeGeneratorVisitor(usableRegisters, utilMethods,
                    messageData, this, stackSize, optimise);

    return node.getExpression().visit(freeCodeGeneratorVisitor);
  }

  @Override
  public List<IntermediateInstruction> visitReadNode(ReadNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    LeftHandSideAssignment lhs = node.getLhs();

    /* Based on the type of the variable being read into, dereferences it
       placing the result in the first available register */
    if(lhs instanceof PairElement) {
      PairElement pairElement = (PairElement) lhs;
      instructions.addAll(dereferencePairElement(pairElement, firstRegister));
    } else if (lhs instanceof ArrayElement) {
      ArrayElement arrayElement = (ArrayElement) lhs;
      instructions.addAll(dereferenceArray(arrayElement));
    } else if (lhs instanceof IdentifierNode) {
      IdentifierNode identifierNode = (IdentifierNode) lhs;
      VariableDeclaration varDeclaration =
              identifierNode.getVariableDeclaration();
      int offset = getOffset(varDeclaration, stackSize);
      instructions.add(new Add(Register.STACK_POINTER, new Immediate(offset),
          firstRegister, false));
    }

    /* Branches to the correct read method, adding the read method if it
       doesn't already exist */
    instructions.addAll(generateReadMethod(firstRegister, node.getType()));

    return instructions;
  }

  private List<IntermediateInstruction> generateReadMethod(Register register,
      Type type) {
    List<IntermediateInstruction> instructions = new ArrayList<>();

    /* Based on the type, create a message data for the type's format
       and put a corresponding read method in the hashmap. Also create a branch
       instruction to the newly defined method. */
    switch (type.toString()) {
      case ("INT"):
        instructions.add(new ReadInteger(register));
        if (!utilMethods.containsKey(p_read_int)) {
          String msg_label = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label), "\"%d\\0\""));
          utilMethods.put(p_read_int, new String[] {msg_label});
        }
        break;
      case ("CHAR"):
        instructions.add(new ReadChar(register));
        if (!utilMethods.containsKey(p_read_char)) {
          String msg_label = getNextMessageLabel();
          messageData.add(new MessageData(new Label(msg_label), "\" %c\\0\""));
          utilMethods.put(p_read_char, new String[] {msg_label});
        }
        break;
    }
    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitAddToListNode(AddToListNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);

    /* Loads the list address into first register */
    VariableDeclaration varDeclaration =
            node.getIdentifier().getVariableDeclaration();
    instructions.addAll(loadVariable(firstRegister, varDeclaration, WORD,
            stackSize, optimise));

    /* Increments the size of the list */
    instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
        secondRegister, Condition.NONE, WORD));
    instructions.add(new Add(secondRegister, new Immediate(1),
            secondRegister, false));
    instructions.add(new Move(secondRegister,
        new MemoryAtRegister(firstRegister, 0, false), Condition.NONE, WORD));


    /* Get address of the last node by branching to the util method */

    instructions.add(new GetLastNode(firstRegister, firstRegister));
    if(!utilMethods.containsKey(p_get_last_node)) {
      /* Labels used for traversing the list up to the last element */
      String firstLabel = getNextLabel();
      String secondLabel = getNextLabel();
      utilMethods.put(p_get_last_node,
              new String[] {firstLabel, secondLabel});
    }

    // instructions.add(new MOVInstruction(firstRegister, r0));

    /* Allocates space for the new node that will be added */
    instructions.add(new Malloc(8, secondRegister));

    /* Puts address of the newly allocated space in the new list node */
    instructions.add(new Move(secondRegister,
        new MemoryAtRegister(firstRegister, 4, false), Condition.NONE, WORD));

    /* Removes the register that stores the address of the new element
       from the list of usable registers and recursively visit the
       expression to be added in the list */
    usableRegisters.remove(1);
    instructions.addAll(node.getExpression().visit(this));
    usableRegisters.add(1, secondRegister);

    /* Sets the next node address to null and also the data */
    instructions.add(new Move(firstRegister,
        new MemoryAtRegister(secondRegister, 0, false), Condition.NONE, WORD));
    instructions.add(new Move(new Immediate(0), firstRegister,
            Condition.NONE, WORD));
    instructions.add(new Move(firstRegister,
        new MemoryAtRegister(secondRegister, 4, false), Condition.NONE, WORD));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction>
  visitRemoveFromListNode(RemoveFromListNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);
    Register thirdRegister = usableRegisters.get(2);

    /* Loads the list address into first register */
    VariableDeclaration varDeclaration =
            node.getIdentifier().getVariableDeclaration();
    instructions.addAll(loadVariable(firstRegister, varDeclaration, WORD,
            stackSize, optimise));

    /* Recursively visits and gets n, the number of the element being
       removed */
    usableRegisters.remove(0);
    instructions.addAll(node.getExpression().visit(this));
    usableRegisters.add(firstRegister);

    /* Gets the size of the list */
    instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
        thirdRegister, Condition.NONE, WORD));

    instructions.add(new CheckListBounds(secondRegister, thirdRegister));
    addRuntimeErrorMethod(utilMethods, messageData, p_check_list_bounds);

    /* Decremements the size of the list */
    instructions.add(new Subtract(thirdRegister, new Immediate(1),
        thirdRegister, false));
    instructions.add(new Move(thirdRegister,
        new MemoryAtRegister(firstRegister, 0, false), Condition.NONE, WORD));
    /* Second register contains n, first register contains list address */

    instructions.add(new RemoveFromList(firstRegister, secondRegister));

    if(!utilMethods.containsKey(p_remove_from_list)) {
      /* Labels used for traversing the list */
      String firstLabel = getNextLabel();
      String secondLabel = getNextLabel();
      utilMethods.put(p_remove_from_list,
              new String[] {firstLabel, secondLabel});
    }

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitIntNode(IntNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Creates an instruction to load an immediate number into
       the first available register */
    instructions.add(new Move(new Immediate(node.getValue()), firstRegister,
        Condition.NONE, WORD));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitBoolNode(BoolNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Creates an instruction to load 1 or 0 into the first available register
       based on whether the boolean is true or false */
    int value = node.isTrue() ? 1 : 0;
    instructions.add(new Move(new Immediate(value), firstRegister,
        Condition.NONE, WORD));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitCharNode(CharNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Creates an instruction to load an immediate number into the first
       available register which is the ordinal value of the character */
    instructions.add(new Move(new Immediate(node.getValue()), firstRegister,
        Condition.NONE, WORD));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitStringNode(StringNode node) {
    Register firstRegister = usableRegisters.get(0);
    List<IntermediateInstruction> instructions = new ArrayList<>();

    /* Generates the next label message and adds a MessageData instruction
       to the strings list*/
    String nextMessageLabel = getNextMessageLabel();
    messageData.add(
            new MessageData(new Label(nextMessageLabel), node.getValue()));
    instructions.add(new Move(new LabelOperand(new Label(nextMessageLabel)),
        firstRegister, Condition.NONE, WORD));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitPairLiterNode(PairLiterNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Load 0 into first register - 0 means null */
    instructions.add(new Move(new Immediate(0), firstRegister,
        Condition.NONE, WORD));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitArrayElement(ArrayElement node) {
    Register firstRegister = usableRegisters.get(0);

    /* Dereferences the array placing result in first usable register */
    List<IntermediateInstruction> instructions =
            new ArrayList<>(dereferenceArray(node));

    /* Loads the result onto the stack */
    instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
        firstRegister, Condition.NONE, WORD));

    return instructions;
  }

  List<IntermediateInstruction> dereferenceArray(ArrayElement node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();

    VariableDeclaration varDeclaration =
            node.getIdentifierNode().getVariableDeclaration();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);

    ExpressionNode[] expressionNodes = node.getExpressionNodes();

    /* Puts the address stored on the stack for this array into the first
       usable register*/
    int offset = getOffset(varDeclaration, stackSize);
    instructions.add(new Add(Register.STACK_POINTER, new Immediate(offset),
        firstRegister, false));

    /* char[] store elements one byte per element, so have shifting equivalent
       to 0 */
    int shiftBits = 2;
    if(varDeclaration.getType().equals(new ArrayType(new BaseType(CHAR)))) {
      shiftBits = 0;
    }

    /* Remove the first usable register to avoid overwriting our address */
    usableRegisters.remove(0);

    for (ExpressionNode expressionNode : expressionNodes) {
      /* Recursively generate assembly code for the expression, result in
         second register */
      instructions.addAll(expressionNode.visit(this));

      /* Dereferences the address of the array on the heap */
      instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
          firstRegister, Condition.NONE, WORD));

      /* Runtime check to make sure the array access is valid */
      instructions.add(new CheckArrayBounds(secondRegister, firstRegister));
      addRuntimeErrorMethod(utilMethods, messageData, p_check_array_bounds);

      /*Skips the size of the array that is stored at the beginning*/
      instructions.add(new Add(firstRegister, new Immediate(4), firstRegister,
          false));

      /* Get the address of the nth element of the array, where n is stored
         in second register. This is done with LSL assembly instruction */
      instructions.add(new Add(firstRegister, new Shifted(secondRegister, LSL,
          shiftBits), firstRegister, false));
    }

    /* Restores the register that was removed */
    usableRegisters.add(0, firstRegister);

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitArithmeticExpressionNode(
          ArithmeticExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);

    /* Generates assembly code for both sides of the arithmetic expression, and
       puts the results in the first two registers */
    instructions.addAll(
            transSubExpressions(firstRegister, secondRegister,
                    node.getLeft(), node.getRight()));

    /* Based on the operator, generate assembly code and a corresponding
       runtime error check */
    switch (node.getOperator()) {
      case PLUS:
        instructions.add(new Add(firstRegister, secondRegister, firstRegister,
            true));
        instructions.add(new CheckOverflow(CheckOverflow.Condition.OVERFLOW));
        addRuntimeErrorMethod(utilMethods, messageData,
                p_throw_overflow_error);
        break;
      case BIN_MINUS:
        instructions.add(new Subtract(firstRegister, secondRegister,
            firstRegister, true));
        instructions.add(new CheckOverflow(CheckOverflow.Condition.OVERFLOW));
        addRuntimeErrorMethod(utilMethods, messageData,
                p_throw_overflow_error);
        break;
      case MULTIPLY:
        instructions.add(new Multiply(firstRegister, secondRegister,
            secondRegister, firstRegister, true));
        instructions.add(new Compare(secondRegister,
            new Shifted(firstRegister, ASR, 31)));
        instructions.add(new CheckOverflow(CheckOverflow.Condition.NOT_EQUAL));
        addRuntimeErrorMethod(utilMethods, messageData,
                p_throw_overflow_error);
        break;
      case DIVIDE:
        instructions.add(new CheckDivideByZero(firstRegister, secondRegister));
        addRuntimeErrorMethod(utilMethods, messageData,
                p_check_divide_by_zero);
        instructions.add(new Divide(firstRegister, secondRegister,
            firstRegister, false));
        break;
      case MOD:
        instructions.add(new CheckDivideByZero(firstRegister, secondRegister));
        addRuntimeErrorMethod(utilMethods, messageData,
                p_check_divide_by_zero);
        instructions.add(new Mod(firstRegister, secondRegister, firstRegister,
            false));
        break;
      case SHIFT_L:
        instructions.add(new Shift(LSL, firstRegister, secondRegister,
            firstRegister, false));
        break;
      case SHIFT_R:
        instructions.add(new Shift(LSR, firstRegister, secondRegister,
            firstRegister, false));
        break;
      case BIT_AND:
        instructions.add(new And(firstRegister, secondRegister, firstRegister));
        break;
      case BIT_EOR:
        instructions.add(new Xor(firstRegister, secondRegister, firstRegister));
        break;
      case BIT_OR:
        instructions.add(new Or(firstRegister, secondRegister, firstRegister));
        break;
    }

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitCompareExpressionNode(
          CompareExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);
    Immediate offsetOne = new Immediate(1);
    Immediate offsetZero = new Immediate(0);

     /* Generates assembly code for both sides of the compare expression, and
        puts the results in the first two registers */
    instructions.addAll(
            transSubExpressions(firstRegister, secondRegister,
                    node.getLeft(), node.getRight()));

    /* Compares the two results */
    instructions.add(new Compare(firstRegister, secondRegister));

    /* Based on the operator, generate assembly code */
    switch (node.getOperator()) {
      case LESS_THAN:
        instructions.add(new Move(offsetOne, firstRegister,
            Condition.LESS_THAN, WORD));
        instructions.add(new Move(offsetZero, firstRegister,
            Condition.GREATER_EQUAL, WORD));
        break;
      case GREATER_THAN:
        instructions.add(new Move(offsetOne, firstRegister,
            Condition.GREATER_THAN, WORD));
        instructions.add(new Move(offsetZero, firstRegister,
            Condition.LESS_EQUAL, WORD));
        break;
      case GREATER_EQ:
        instructions.add(new Move(offsetOne, firstRegister,
            Condition.GREATER_EQUAL, WORD));
        instructions.add(new Move(offsetZero, firstRegister,
            Condition.LESS_THAN, WORD));
        break;
      case LESS_EQ:
        instructions.add(new Move(offsetOne, firstRegister,
            Condition.LESS_EQUAL, WORD));
        instructions.add(new Move(offsetZero, firstRegister,
            Condition.GREATER_THAN, WORD));
        break;
      case EQ:
        instructions.add(new Move(offsetOne, firstRegister, Condition.EQUAL,
                WORD));
        instructions.add(new Move(offsetZero, firstRegister,
            Condition.NOT_EQUAL, WORD));
        break;
      case NOT_EQ:
        instructions.add(new Move(offsetOne, firstRegister,
            Condition.NOT_EQUAL, WORD));
        instructions.add(new Move(offsetZero, firstRegister, Condition.EQUAL,
            WORD));
        break;
    }
    return instructions;
  }

  @Override
  public List<IntermediateInstruction>
  visitBoolExpressionNode(BoolExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);

    /* Generates assembly code for both sides of the compare expression, and
       puts the results in the first two registers */
    instructions.addAll(
            transSubExpressions(firstRegister, secondRegister,
                    node.getLeft(), node.getRight()));

    /* Based on the operator, generate assembly code */
    switch (node.getOperator()) {
      case OR:
        instructions.add(new Or(firstRegister, secondRegister, firstRegister));
        break;
      case AND:
        instructions.add(new And(firstRegister, secondRegister, firstRegister));
        break;
    }
    return instructions;
  }

  private List<IntermediateInstruction> transSubExpressions(
          Register firstRegister, Register secondRegister,
          ExpressionNode left, ExpressionNode right) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    if (usableRegisters.size() > 2) {
      /* Recursively translate the two expressions using up registers */
      instructions.addAll(left.visit(this));
      usableRegisters.remove(0);
      instructions.addAll(right.visit(this));
      usableRegisters.add(0, firstRegister);
    } else {
      /* Use accumulator strategy */
      instructions.addAll(right.visit(this));

      /* Push result onto the stack */
      instructions.add(new Push(firstRegister));
      stackSize += 4;

      /* Visit left with result in first register */
      instructions.addAll(left.visit(this));

      /* Put result of right hand side into second register */
      instructions.add(new Pop(secondRegister));
      stackSize -= 4;
    }
    return instructions;
  }

  @Override
  public
  List<IntermediateInstruction> visitIdentifierNode(IdentifierNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    int elementSize;

    /* Based on the type, we determine the element size */
    if (BYTE_TYPES.contains(node.getVariableDeclaration().getType())) {
      elementSize = BYTE;
    } else {
      elementSize = WORD;
    }

    /* Dereferences the variable based on its stack position */
    instructions.addAll(loadVariable(firstRegister,
        node.getVariableDeclaration(), elementSize, stackSize, optimise));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitNotUnaryExpressionNode(
          NotUnaryExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Produces the assembly code for evaluating the base expression */
    instructions.addAll(node.getExpression().visit(this));

    /* Negates the result and stores it in the first usable register */
    Immediate offsetZero = new Immediate(1);
    instructions.add(new Xor(firstRegister, offsetZero, firstRegister));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitBitNotUnaryExpressionNode(
          BitNotUnaryExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    instructions.addAll(node.getExpression().visit(this));

    /* Performs bitwise negation of the result and stores it in the first
       usable register */
    instructions.add(new Not(firstRegister, firstRegister));
    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitNegUnaryExpressionNode(
          NegUnaryExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Produces the assembly code for evaluating the base expression */
    instructions.addAll(node.getExpression().visit(this));

    /* Negates the result, checks for potential overflow and stores the result
       in the first usable register */
    Immediate offsetZero = new Immediate(0);
    instructions.add(new Subtract(offsetZero, firstRegister, firstRegister,
        true));
    instructions.add(new CheckOverflow(CheckOverflow.Condition.OVERFLOW));
    addRuntimeErrorMethod(utilMethods, messageData, p_throw_overflow_error);

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitLenUnaryExpressionNode(
          LenUnaryExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    VariableDeclaration varDeclaration =
            node.getIdentifierNode().getVariableDeclaration();

    /* Dereferences the variable based on its stack position */
    instructions.addAll(loadVariable(firstRegister, varDeclaration, WORD,
            stackSize, optimise));

    /* Stores the length of the underlying array in the
       first usable register */
    instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
        firstRegister, Condition.NONE, WORD));
    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitOrdUnaryExpressionNode(
          OrdUnaryExpressionNode node) {
    return node.getExpression().visit(this);
  }

  @Override
  public List<IntermediateInstruction> visitChrUnaryExpressionNode(
          ChrUnaryExpressionNode node) {
    return node.getExpression().visit(this);
  }

  public List<IntermediateInstruction> visitArrayLiteral(ArrayLiteral node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    ExpressionNode[] expressionNodes = node.getExpressionNodes();
    ArrayType arrayType = node.getType();

    int arraySize;

    /* Determines the array size */
    if (expressionNodes == null) {
      arraySize = 0;
    } else {
      arraySize = expressionNodes.length;
    }

    /* Stores the element size based on the array type */
    int elementSize = 4;
    if(arrayType != null && arrayType.equals(new ArrayType(
            new BaseType(CHAR)))) {
      elementSize = 1;
    }

    /* Malloc space of 4 Bytes (for the integer size) + n * elementSize
       where n is the number of items and elementSize is the size based
       on whether we have char or something else */
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);

    instructions.add(new Malloc(4 + (arraySize * elementSize), firstRegister));

    /* Remove register to prevent overwriting */
    usableRegisters.remove(0);

    /* Iterate through the expressions and individually store them
       on the correct heap position */
    for (int i = 0; i < arraySize; i++) {
      instructions.addAll(expressionNodes[i].visit(this));
      instructions.add(new Move(secondRegister,
          new MemoryAtRegister(firstRegister, 4 + elementSize * i, false),
          Condition.NONE, WORD));
    }

    /* Store the size of the array in the first position on the allocated
       heap memory */
    instructions.add(new Move(new Immediate(arraySize), secondRegister,
        Condition.NONE, WORD));
    instructions.add(new Move(secondRegister,
            new MemoryAtRegister(firstRegister, 0, false), Condition.NONE,
        WORD));

    /* Restore the registers */
    usableRegisters.add(0, firstRegister);

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitPairElement(PairElement node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Dereferences a pair element based on FST or SND, placing result in
       the first usable register */
    instructions.addAll(dereferencePairElement(node, firstRegister));
    instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
        firstRegister, Condition.NONE, WORD));

    return instructions;
  }

  private List<IntermediateInstruction> dereferencePairElement(PairElement node,
                                                      Register firstRegister) {
    List<IntermediateInstruction> instructions = new ArrayList<>();

    /* Get the pair from the symbol table that is being referred to */
    VariableDeclaration variableDeclaration =
            node.getPairIdentifier().getVariableDeclaration();

    instructions.addAll(loadVariable(usableRegisters.get(0),
        variableDeclaration, WORD, stackSize, optimise));

    /* Adds runtime check for null pointers, so we don't dereference null */
    instructions.add(new CheckNullPointer(firstRegister));
    addRuntimeErrorMethod(utilMethods, messageData, p_check_null_pointer);

    /* Based on FST or SND, we do a dereference of the heap address by shifting
       the corresponding number of bytes */
    if (node.getType() == PairElement.Type.FST) {
      instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
          firstRegister, Condition.NONE, WORD));
    } else {
      instructions.add(new Move(new MemoryAtRegister(firstRegister, 4, false),
          firstRegister, Condition.NONE, WORD));
    }


    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitNewListStatement(NewListStatement node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);

    /* Allocate 8 bytes of space for a new list - this is the head node.
       First 4 bytes for the size of the list, second 4 bytes for the address
       of the next node, which is initially null. */
    instructions.add(new Malloc(8, firstRegister));

    /* Store initial size of 0 and null pointer */
    instructions.add(new Move(new Immediate(0), secondRegister,
        Condition.NONE, WORD));
    instructions.add(new Move(secondRegister,
            new MemoryAtRegister(firstRegister, 0, false), Condition.NONE,
        WORD));
    instructions.add(new Move(secondRegister,
            new MemoryAtRegister(firstRegister, 4, false), Condition.NONE,
        WORD));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitNewPairStatement(NewPairStatement node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);
    Register thirdRegister = usableRegisters.get(2);

    /* Malloc space for the pair and put the address into the first
       usable register */
    instructions.add(new Malloc(8, firstRegister));

    /* Remove the first usable register to avoid overwriting the address*/
    usableRegisters.remove(0);

    /* Recursively visit the first expression */
    instructions.addAll(node.getFirstExpression().visit(this));

    /* Allocate space for the first expression based on the type */
    int elementSize = BYTE_TYPES.contains(node.getFirstExpressionType()) ?
        BYTE : WORD;

    instructions.add(new Malloc(elementSize, thirdRegister));

    /* Place the address in the correct position relative to the pair */
    instructions.add(new Move(secondRegister,
        new MemoryAtRegister(thirdRegister, 0, false), Condition.NONE, WORD));
    instructions.add(new Move(thirdRegister,
        new MemoryAtRegister(firstRegister, 0, false), Condition.NONE, WORD));

    /* Recursively visit the second expression */
    instructions.addAll(node.getSecondExpression().visit(this));

    /* Allocate space for the second expression based on the type */
    elementSize = BYTE_TYPES.contains(node.getSecondExpressionType()) ? BYTE
        : WORD;

    instructions.add(new Malloc(elementSize, thirdRegister));

    /* Place the address in the correct position relative to the pair */
    instructions.add(new Move(secondRegister,
        new MemoryAtRegister(thirdRegister, 0, false), Condition.NONE, WORD));
    instructions.add(new Move(thirdRegister,
        new MemoryAtRegister(firstRegister, 4, false), Condition.NONE, WORD));

    /* Restore the first register */
    usableRegisters.add(0, firstRegister);
    return instructions;
  }

  @Override
  public List<IntermediateInstruction> visitCallStatement(CallStatement node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    String functionLabel = node.getFunctionEntry().getLabel();

    /* Get the function entry from the function symbol table to be used */
    FunctionEntry functionEntry = node.getFunctionEntry();

    SymbolTable<VariableDeclaration> functionSymbolTable;

    if (functionEntry != null) {
      functionSymbolTable = functionEntry.getSymbolTable();
    } else {
      throw new RuntimeException("Could not find the function "
          + functionLabel + ". Something must have gone terribly wrong.");
    }


    /* Setup and prepare the argument declarations that will need to be made
       for the call */
    argDeclarationsForCall = new ArrayList<>();
    for (VariableDeclaration declaration :
        functionSymbolTable.getVariableDeclarations()) {
      if (declaration.isParameter()) {
        argDeclarationsForCall.add(declaration);
      }
    }

    argStackSizeForCall = 0;

    /* Recursively visit and generate assembly code for each of the arguments,
       placing them on the correct position on the stack and growing it */
    ArgList argList = node.getArgumentList();
    if (argList != null) {
      instructions.addAll(argList.visit(this));
    }

    /* Add a branch to the correct function */
    instructions.add(new Branch("_" + functionLabel, Branch.Condition.NONE,
        true));


    /* Shrink the stack by the amount the stack was grown by to place
       arguments */
    instructions.addAll(shrinkStack(argStackSizeForCall));

    /* Place the result in the first usable register */
    instructions.add(new GetReturn(usableRegisters.get(0)));

    return instructions;
  }

  @Override
  public List<IntermediateInstruction>
  visitListSizeExpressionNode(ListSizeExpressionNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);

    /* Gets the address of the list */
    instructions.addAll(loadVariable(firstRegister,
        node.getVarDeclaration(), WORD, stackSize, optimise));

    /* Dereferences first byte of the address where we store the size of
       the list. */
    instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
        firstRegister, Condition.NONE, WORD));

    return instructions;
  }

  @Override
  public
  List<IntermediateInstruction> visitListContainsNode(ListContainsNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);
    VariableDeclaration varDeclaration =
            node.getIdentifier().getVariableDeclaration();

    instructions.addAll(loadVariable(firstRegister, varDeclaration,
        WORD, stackSize, optimise));

    usableRegisters.remove(0);
    instructions.addAll(node.getExpression().visit(this));
    usableRegisters.add(0, firstRegister);

    instructions.add(new ListContains(firstRegister, firstRegister,
        secondRegister));

    if(!utilMethods.containsKey(p_list_contains)) {
      String firstLabel = getNextLabel();
      String secondLabel = getNextLabel();
      String thirdLabel = getNextLabel();
      utilMethods.put(p_list_contains,
          new String[] {firstLabel, secondLabel, thirdLabel});
    }

    return instructions;
  }

  @Override
  public
  List<IntermediateInstruction> visitGetFromListNode(GetFromListNode node) {
    List<IntermediateInstruction> instructions = new ArrayList<>();
    Register firstRegister = usableRegisters.get(0);
    Register secondRegister = usableRegisters.get(1);
    VariableDeclaration varDeclaration =
        node.getIdentifier().getVariableDeclaration();

    instructions.addAll(loadVariable(firstRegister, varDeclaration, WORD,
        stackSize, optimise));

    usableRegisters.remove(0);
    instructions.addAll(node.getExpression().visit(this));
    usableRegisters.add(0, firstRegister);

    instructions.add(new CheckListBounds(secondRegister,
        new MemoryAtRegister(firstRegister, 0, false)));

    addRuntimeErrorMethod(utilMethods, messageData, p_check_list_bounds);

    instructions.add(new GetListElement(firstRegister, firstRegister,
        secondRegister));

    if(!utilMethods.containsKey(p_get_list_element)) {
      String firstLabel = getNextLabel();
      String secondLabel = getNextLabel();
      utilMethods.put(p_get_list_element,
              new String[] {firstLabel, secondLabel});
    }

    int elementSize = node.getType().getSize();

    instructions.add(new Move(new MemoryAtRegister(firstRegister, 0, false),
        firstRegister, Condition.NONE, elementSize));

    return instructions;
  }

}