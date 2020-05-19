package uk.ac.ic.doc.wacc.code_generator.instructions;

import uk.ac.ic.doc.wacc.Triple;
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
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Pop;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Push;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Return;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Immediate;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.MemoryAtRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Register;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Shifted;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.MessageData;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.free_methods.FreeArray;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.free_methods.FreePair;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.CheckListBounds;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.FreeList;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.GetLastNode;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.GetListElement;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.ListContains;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.list_methods.RemoveFromList;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintChar;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintList;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckArrayBounds;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckDivideByZero;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckNullPointer;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.CheckOverflow;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.RuntimeError;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintBool;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintInteger;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintLn;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintReference;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.PrintString;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.ReadChar;
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.print_read_methods.ReadInteger;

public interface Translator {

  Instruction[] translateFrom(StartFunction startFunction);

  Instruction[] translateFrom(StartMain startMain);

  Instruction[] translateFrom(Branch branch);

  Instruction[] translateFrom(Label label);

  Instruction[] translateFrom(Add add);

  Instruction[] translateFrom(And and);

  Instruction[] translateFrom(Compare compare);

  Instruction[] translateFrom(Divide divide);

  Instruction[] translateFrom(Malloc malloc);

  Instruction[] translateFrom(Mod mod);

  Instruction[] translateFrom(Multiply multiply);

  Instruction[] translateFrom(Not not);

  Instruction[] translateFrom(Or or);

  Instruction[] translateFrom(Shift shift);

  Instruction[] translateFrom(Subtract subtract);

  Instruction[] translateFrom(Xor xor);

  Instruction[] translateFrom(Exit exit);

  Instruction[] translateFrom(GetReturn getReturn);

  Instruction[] translateFrom(Move move);

  Instruction[] translateFrom(Pop pop);

  Instruction[] translateFrom(Push push);

  Instruction[] translateFrom(Return returnInstruction);

  Instruction[] translateFrom(FreeArray freeArray);

  Instruction[] translateFrom(FreePair freePair);

  Instruction[] translateFrom(CheckListBounds checkListBounds);

  Instruction[] translateFrom(FreeList freeList);

  Instruction[] translateFrom(GetLastNode getLastNode);

  Instruction[] translateFrom(GetListElement getListElement);

  Instruction[] translateFrom(ListContains listContains);

  Instruction[] translateFrom(RemoveFromList removeFromList);

  Instruction[] translateFrom(PrintBool printBool);

  Instruction[] translateFrom(PrintChar printChar);

  Instruction[] translateFrom(PrintInteger printInteger);

  Instruction[] translateFrom(PrintList printList);

  Instruction[] translateFrom(PrintLn printLn);

  Instruction[] translateFrom(PrintReference printReference);

  Instruction[] translateFrom(PrintString printString);

  Instruction[] translateFrom(ReadChar readChar);

  Instruction[] translateFrom(ReadInteger readInteger);

  Instruction[] translateFrom(CheckArrayBounds checkArrayBounds);

  Instruction[] translateFrom(CheckDivideByZero checkDivideByZero);

  Instruction[] translateFrom(CheckNullPointer checkNullPointer);

  Instruction[] translateFrom(CheckOverflow checkOverflow);

  Instruction[] translateFrom(RuntimeError runtimeError);

  Instruction[] translateFrom(MessageData messageData);

  default Instruction[] translateFrom(IntermediateInstruction instruction) {
    if (instruction instanceof StartFunction) {
      return translateFrom((StartFunction) instruction);
    } else if (instruction instanceof StartMain) {
      return translateFrom((StartMain) instruction);
    } else if (instruction instanceof Branch) {
      return translateFrom((Branch) instruction);
    } else if (instruction instanceof Label) {
      return translateFrom((Label) instruction);
    } else if (instruction instanceof Add) {
      return translateFrom((Add) instruction);
    } else if (instruction instanceof And) {
      return translateFrom((And) instruction);
    } else if (instruction instanceof Compare) {
      return translateFrom((Compare) instruction);
    } else if (instruction instanceof Divide) {
      return translateFrom((Divide) instruction);
    } else if (instruction instanceof Malloc) {
      return translateFrom((Malloc) instruction);
    } else if (instruction instanceof Mod) {
      return translateFrom((Mod) instruction);
    } else if (instruction instanceof Multiply) {
      return translateFrom((Multiply) instruction);
    } else if (instruction instanceof Not) {
      return translateFrom((Not) instruction);
    } else if (instruction instanceof Or) {
      return translateFrom((Or) instruction);
    } else if (instruction instanceof Shift) {
      return translateFrom((Shift) instruction);
    } else if (instruction instanceof Subtract) {
      return translateFrom((Subtract) instruction);
    } else if (instruction instanceof Xor) {
      return translateFrom((Xor) instruction);
    } else if (instruction instanceof Exit) {
      return translateFrom((Exit) instruction);
    } else if (instruction instanceof GetReturn) {
      return translateFrom((GetReturn) instruction);
    } else if (instruction instanceof Move) {
      return translateFrom((Move) instruction);
    } else if (instruction instanceof Pop) {
      return translateFrom((Pop) instruction);
    } else if (instruction instanceof Push) {
      return translateFrom((Push) instruction);
    } else if (instruction instanceof Return) {
      return translateFrom((Return) instruction);
    } else if (instruction instanceof FreeArray) {
      return translateFrom((FreeArray) instruction);
    } else if (instruction instanceof FreePair) {
      return translateFrom((FreePair) instruction);
    } else if (instruction instanceof CheckListBounds) {
      return translateFrom((CheckListBounds) instruction);
    } else if (instruction instanceof FreeList) {
      return translateFrom((FreeList) instruction);
    } else if (instruction instanceof GetLastNode) {
      return translateFrom((GetLastNode) instruction);
    } else if (instruction instanceof GetListElement) {
      return translateFrom((GetListElement) instruction);
    } else if (instruction instanceof ListContains) {
      return translateFrom((ListContains) instruction);
    } else if (instruction instanceof RemoveFromList) {
      return translateFrom((RemoveFromList) instruction);
    } else if (instruction instanceof PrintBool) {
      return translateFrom((PrintBool) instruction);
    } else if (instruction instanceof PrintChar) {
      return translateFrom((PrintChar) instruction);
    } else if (instruction instanceof PrintInteger) {
      return translateFrom((PrintInteger) instruction);
    } else if (instruction instanceof PrintList) {
      return translateFrom((PrintList) instruction);
    } else if (instruction instanceof PrintLn) {
      return translateFrom((PrintLn) instruction);
    } else if (instruction instanceof PrintReference) {
      return translateFrom((PrintReference) instruction);
    } else if (instruction instanceof PrintString) {
      return translateFrom((PrintString) instruction);
    } else if (instruction instanceof ReadChar) {
      return translateFrom((ReadChar) instruction);
    } else if (instruction instanceof ReadInteger) {
      return translateFrom((ReadInteger) instruction);
    } else if (instruction instanceof CheckArrayBounds) {
      return translateFrom((CheckArrayBounds) instruction);
    } else if (instruction instanceof CheckDivideByZero) {
      return translateFrom((CheckDivideByZero) instruction);
    } else if (instruction instanceof CheckNullPointer) {
      return translateFrom((CheckNullPointer) instruction);
    } else if (instruction instanceof CheckOverflow) {
      return translateFrom((CheckOverflow) instruction);
    } else if (instruction instanceof RuntimeError) {
      return translateFrom((RuntimeError) instruction);
    } else if (instruction instanceof MessageData) {
      return translateFrom((MessageData) instruction);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  Instruction[] getCode(UtilMethodLabel utilMethodLabel, String... msgLabels);

  /* Triple<instructions immediately before an instruction with the operand, the operand itself,
   instructions immediately after the instruction with the operand>

   It's not strictly required to have instructions immediately before or after an instruction with
   the operand. For example, Triple<null, Operand, null> is acceptable. */
  Triple<Instruction[], Operand, Instruction[]> translateFrom(
      Immediate immediate);

  Triple<Instruction[], Operand, Instruction[]> translateFrom(
      LabelOperand labelOperand);

  Triple<Instruction[], Operand, Instruction[]> translateFrom(
      MemoryAtRegister memoryAtRegister);

  Triple<Instruction[], Operand, Instruction[]> translateFrom(
      Register register);

  Triple<Instruction[], Operand, Instruction[]> translateFrom(Shifted shifted);

  default Triple<Instruction[], Operand, Instruction[]> translateFrom(
      Operand operand) {
    if (operand instanceof Immediate) {
      return translateFrom((Immediate) operand);
    } else if (operand instanceof LabelOperand) {
      return translateFrom((LabelOperand) operand);
    } else if (operand instanceof MemoryAtRegister) {
      return translateFrom((MemoryAtRegister) operand);
    } else if (operand instanceof Register) {
      return translateFrom((Register) operand);
    } else if (operand instanceof Shifted) {
      return translateFrom((Shifted) operand);
    } else {
      return null;
    }
  }

}
