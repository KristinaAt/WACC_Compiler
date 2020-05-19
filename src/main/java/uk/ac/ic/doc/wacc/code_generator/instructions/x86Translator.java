package uk.ac.ic.doc.wacc.code_generator.instructions;

import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register.Identifier.eax;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register.Identifier.ebx;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register.Identifier.edx;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register.Identifier.esp;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_array_bounds;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_divide_by_zero;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_list_bounds;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_null_pointer;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_free_array;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_free_list;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_free_pair;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_get_last_node;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_get_list_element;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_list_contains;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_bool;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_int;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_list_bool;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_list_char;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_list_int;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_list_reference;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_list_string;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_ln;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_reference;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_print_string;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_read_char;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_read_int;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_remove_from_list;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_throw_overflow_error;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_throw_runtime_error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.ac.ic.doc.wacc.Triple;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Branch;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Branch.Condition;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.StartFunction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.StartMain;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Call;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Jump;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Jump.Type;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.SpecialLine;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CDQInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CMPInstruction;
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
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.ThreeOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.intermediate.Xor;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.ADDInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.ANDInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.IDIVInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.IMULInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.NOTInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.ORInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.SHRDInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.SUBInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.ShiftInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.XORInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Exit;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.GetReturn;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Move;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Pop;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Push;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Return;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.x86_64.MOVInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.x86_64.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.x86_64.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.Operand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Immediate;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.LabelOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.MemoryAtRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Register;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.intermediate.Shifted;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.CustomOperand;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.OffsetRegister;
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
import uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate.runtime_error_methods.RuntimeError;

public class x86Translator implements Translator {

  @Override
  public Instruction[] translateFrom(StartFunction startFunction) {
    List<Instruction> instructionList = new ArrayList<>(
        Arrays.asList(translateFrom(startFunction.getFunctionLabel())));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(StartMain startMain) {
    return new Instruction[] {
      new SpecialLine(".text"),
      new SpecialLine(".globl main"),
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("main")
    };
  }

  @Override
  public Instruction[] translateFrom(Branch branch) {
    if (branch.shouldNextInstructionBeSaved()) {
      if (branch.getCondition() != Condition.NONE) {
        throw new UnsupportedOperationException();
      }

      return new Instruction[] {
          new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(branch.getTargetLabelName()))
      };
    }

    Jump.Type jumpType;

    switch (branch.getCondition()) {
      case NONE:
      default:
        jumpType = Type.JMP;
        break;

      case EQUAL:
        jumpType = Type.JE;
        break;

      case OVERFLOW:
        jumpType = Type.JO;
        break;

      case SIGNED_LESS_THAN:
        jumpType = Type.JL;
        break;

      case UNSIGNED_GREATER_THAN_OR_EQUAL:
        jumpType = Type.JAE;
        break;

      case NOT_EQUAL:
        jumpType = Type.JNE;
        break;
    }

    return new Instruction[] {
        new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(branch.getTargetLabelName()), jumpType)
    };
  }

  @Override
  public Instruction[] translateFrom(Label label) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(label.getName())
    };
  }

  private Triple<Operand, Operand, Operand> getOperands(ThreeOperand threeOperandInstruction) {
    return new Triple<>(threeOperandInstruction.getDestinationOperand(),
        threeOperandInstruction.getFirstSourceOperand(),
        threeOperandInstruction.getSecondSourceOperand());
  }

  @Override
  public Instruction[] translateFrom(Add add) {
    Triple<Operand, Operand, Operand> operands = getOperands(add);

    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 = translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 = translateFrom(operands.third);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    instructionList.add(new MOVInstruction(translatedTriple1.second, translatedTriple2.second, MOVInstruction.Condition.NONE, false));
    instructionList.add(new ADDInstruction(translatedTriple1.second, translatedTriple3.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(And and) {
    Triple<Operand, Operand, Operand> operands = getOperands(and);

    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    if (!operands.first.equals(operands.second)) {
      throw new UnsupportedOperationException("Destination operand must be the same as the first source operand");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 = translateFrom(operands.third);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    instructionList.add(new ANDInstruction(translatedTriple1.second, translatedTriple2.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Compare compare) {
    Operand operand1 = compare.getFirstOperand();
    Operand operand2 = compare.getSecondOperand();

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operand1);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 = translateFrom(operand2);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    instructionList.add(new CMPInstruction(translatedTriple1.second, translatedTriple2.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Divide divide) {
    Triple<Operand, Operand, Operand> operands = getOperands(divide);

    if (!(operands.first instanceof Register) || !(operands.second instanceof Register) || !(operands.third instanceof Register)) {
      throw new UnsupportedOperationException(
          "All operands must be registers");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 = translateFrom(operands.third);

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register) translatedTriple1.second;

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    instructionList.add(new MOVInstruction(eax.getRegister(), translatedTriple2.second, MOVInstruction.Condition.NONE, false));

    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    instructionList.add(new CDQInstruction());

    instructionList.addAll(Arrays.asList(translatedTriple3.first));
    instructionList.add(new IDIVInstruction((uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register) translatedTriple3.second));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.add(new MOVInstruction(destinationRegister, eax.getRegister(), MOVInstruction.Condition.NONE, false));
    instructionList.addAll(Arrays.asList(translatedTriple1.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Malloc malloc) {
    Operand destinationOperand = malloc.getDestinationOperand();

    if (!(destinationOperand instanceof Register)) {
      throw new UnsupportedOperationException("Destination operand other than"
          + " a register not supported");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(destinationOperand);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.add(new PUSHInstruction(new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(malloc.getBytes())));
    instructionList.add(new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("malloc")));

    instructionList.addAll(Arrays.asList(translatedTriple.first));
    instructionList.add(new MOVInstruction(translatedTriple.second, eax.getRegister(), MOVInstruction.Condition.NONE, false));
    instructionList.addAll(Arrays.asList(translatedTriple.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Mod mod) {
    Triple<Operand, Operand, Operand> operands = getOperands(mod);

    if (!(operands.first instanceof Register) || !(operands.second instanceof Register) || !(operands.third instanceof Register)) {
      throw new UnsupportedOperationException(
          "All operands must be registers");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 = translateFrom(operands.third);

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register) translatedTriple1.second;

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    instructionList.add(new MOVInstruction(eax.getRegister(), translatedTriple2.second, MOVInstruction.Condition.NONE, false));

    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    instructionList.addAll(Arrays.asList(translatedTriple3.first));
    instructionList.add(new IDIVInstruction((uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register) translatedTriple3.second));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.add(new MOVInstruction(destinationRegister, edx.getRegister(), MOVInstruction.Condition.NONE, false));
    instructionList.addAll(Arrays.asList(translatedTriple1.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Multiply multiply) {
    Operand sourceOperand1 = multiply.getFirstSourceOperand();
    Operand sourceOperand2 = multiply.getSecondSourceOperand();
    Operand destinationOperandMostSignificant = multiply.getDestinationOperandMostSignificant();
    Operand destinationOperandLeastSignificant = multiply.getDestinationOperandLeastSignificant();

    if (!destinationOperandMostSignificant.equals(destinationOperandLeastSignificant)) {
      throw new UnsupportedOperationException("Both destination operands must be the same for x86");
    }

    if (!sourceOperand1.equals(destinationOperandMostSignificant)) {
      throw new UnsupportedOperationException("Destination operand must be the same as the first source operand");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(sourceOperand2);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(destinationOperandMostSignificant);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    instructionList.add(new IMULInstruction(translatedTriple2.second, translatedTriple1.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Not not) {
    Operand sourceOperand = not.getSourceOperand();
    Operand destinationOperand = not.getDestinationOperand();

    if (!sourceOperand.equals(destinationOperand)) {
      throw new UnsupportedOperationException("Destination and source operands must be the same");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple = translateFrom(sourceOperand);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple.first));
    instructionList.add(new NOTInstruction(translatedTriple.second));
    instructionList.addAll(Arrays.asList(translatedTriple.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Or or) {
    Triple<Operand, Operand, Operand> operands = getOperands(or);

    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    if (!operands.first.equals(operands.second)) {
      throw new UnsupportedOperationException("Destination operand must be the same as the first source operand");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 = translateFrom(operands.third);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    instructionList.add(new ORInstruction(translatedTriple1.second, translatedTriple2.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Shift shift) {
    Triple<Operand, Operand, Operand> operands = getOperands(shift);

    if (!(operands.first instanceof Register)
        || !(operands.second instanceof Register)
        || !(operands.third instanceof Register)) {
      throw new UnsupportedOperationException("Operands other than a register not supported");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 = translateFrom(operands.third);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    ShiftInstruction.Type shiftOperand;

    switch (shift.getShift()) {
      default:
      case ASR:
        shiftOperand = ShiftInstruction.Type.SAR;
        break;

      case LSL:
        shiftOperand = ShiftInstruction.Type.SHL;
        break;

      case LSR:
        shiftOperand = ShiftInstruction.Type.SHR;
        break;

      case ROR:
        throw new UnsupportedOperationException("ROR not supported");

      case RRX:
        shiftOperand = null;
        break;
    }

    if (shiftOperand == null) {
      instructionList.add(new SHRDInstruction(translatedTriple1.second, translatedTriple2.second, translatedTriple3.second));
    } else {
      instructionList.add(new ShiftInstruction(shiftOperand, translatedTriple1.second, translatedTriple3.second));
    }

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Subtract subtract) {
    Triple<Operand, Operand, Operand> operands = getOperands(subtract);

    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 = translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 = translateFrom(operands.third);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    instructionList.add(new MOVInstruction(translatedTriple1.second, translatedTriple2.second, MOVInstruction.Condition.NONE, false));
    instructionList.add(new SUBInstruction(translatedTriple1.second, translatedTriple3.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Xor xor) {
    Triple<Operand, Operand, Operand> operands = getOperands(xor);

    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    if (!operands.first.equals(operands.second)) {
      throw new UnsupportedOperationException("Destination operand must be the same as the first source operand");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 = translateFrom(operands.third);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    instructionList.add(new XORInstruction(translatedTriple1.second, translatedTriple2.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Exit exit) {
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(exit.getExitCode());

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple.first));
    instructionList.add(new MOVInstruction(eax.getRegister(), translatedTriple.second, MOVInstruction.Condition.NONE, false));
    instructionList.addAll(Arrays.asList(translatedTriple.third));

    instructionList.add(new PUSHInstruction(eax.getRegister()));
    instructionList.add(new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("exit")));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(GetReturn getReturn) {
    Operand destinationOperand = getReturn.getDestinationOperand();

    if (!(destinationOperand instanceof Register)) {
      throw new UnsupportedOperationException("Destination operand other than"
          + " a register not supported");
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(destinationOperand);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple.first));

    instructionList.add(new MOVInstruction(translatedTriple.second, eax.getRegister(), MOVInstruction.Condition.NONE, false));

    instructionList.addAll(Arrays.asList(translatedTriple.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Move move) {
    Operand sourceOperand = move.getSourceOperand();
    Operand destinationOperand = move.getDestinationOperand();

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 = translateFrom(sourceOperand);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 = translateFrom(destinationOperand);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    MOVInstruction.Condition condition;

    switch (move.getCondition()) {
      case NONE:
      default:
        condition = MOVInstruction.Condition.NONE;
        break;

      case EQUAL:
        condition = MOVInstruction.Condition.E;
        break;

      case NOT_EQUAL:
        condition = MOVInstruction.Condition.NE;
        break;

      case GREATER_EQUAL:
        condition = MOVInstruction.Condition.GE;
        break;

      case GREATER_THAN:
        condition = MOVInstruction.Condition.G;
        break;

      case LESS_EQUAL:
        condition = MOVInstruction.Condition.LE;
        break;

      case LESS_THAN:
        condition = MOVInstruction.Condition.LT;
        break;

      case UNSIGNED_GREATER_EQUAL:
        condition = MOVInstruction.Condition.AE;
        break;
    }

    instructionList.add(new MOVInstruction(translatedTriple2.second, translatedTriple1.second, condition, move.getSizeInBytes() == 1));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Pop pop) {
    Operand destinationOperand = pop.getDestinationOperand();

    if (destinationOperand instanceof Register) {
      Triple<Instruction[], Operand, Instruction[]> triple =
          translateFrom((Register) destinationOperand);
      return new Instruction[] {
          new POPInstruction((uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register) triple.second)
      };
    } else {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }
  }

  @Override
  public Instruction[] translateFrom(Push push) {
    Operand sourceOperand = push.getSourceOperand();

    if (sourceOperand instanceof Register) {
      Triple<Instruction[], Operand, Instruction[]> triple =
          translateFrom((Register) sourceOperand);
      return new Instruction[] {
          new PUSHInstruction((uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register) triple.second)
      };
    } else {
      throw new UnsupportedOperationException(
          "Source operand other than a register not supported");
    }
  }

  @Override
  public Instruction[] translateFrom(Return returnInstruction) {
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(returnInstruction.getReturnValue());

    List<Instruction> instructionList = new ArrayList<>();

    if (translatedTriple != null) {
      instructionList.addAll(Arrays.asList(translatedTriple.first));
      instructionList.add(new MOVInstruction(eax.getRegister(), translatedTriple.second, MOVInstruction.Condition.NONE, false));
      instructionList.addAll(Arrays.asList(translatedTriple.third));
    }

    if (returnInstruction.getFunctionExitLabel() != null) {
      instructionList.add(new Jump(
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(returnInstruction.getFunctionExitLabel()), Type.JMP));
    }

    instructionList.add(new SpecialLine("ret"));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  private Instruction[] utilJumpTo(Operand[] operands,
      UtilMethodLabel methodLabel) {
    return utilJumpTo(operands, methodLabel, null);
  }

  private Instruction[] utilJumpTo(Operand[] operands,
      UtilMethodLabel methodLabel, Operand destinationOperand) {
    List<Triple<Instruction[], Operand, Instruction[]>> translatedTriples =
        new ArrayList<>();

    if (operands != null) {
      for (Operand operand : operands) {
        translatedTriples.add(translateFrom(operand));
      }
    }

    List<Instruction> instructionList = new ArrayList<>();

    int i = 0;

    for (Triple<Instruction[], Operand, Instruction[]> triple :
        translatedTriples) {
      instructionList.addAll(Arrays.asList(triple.first));
      instructionList.add(new MOVInstruction(
          uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register
          .Identifier.values()[i].getRegister(), triple.second, MOVInstruction.Condition.NONE, false));
      instructionList.addAll(Arrays.asList(triple.third));

      i++;
    }

    instructionList.add(
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(methodLabel.name())));

    if (destinationOperand != null) {
      if (!(destinationOperand instanceof Register)) {
        throw new UnsupportedOperationException("Destination operand other "
            + "than a register not supported");
      }

      Triple<Instruction[], Operand, Instruction[]> translatedDestinationTriple = translateFrom(destinationOperand);

      instructionList.addAll(Arrays.asList(translatedDestinationTriple.first));
      instructionList.add(new MOVInstruction(translatedDestinationTriple.second, eax
          .getRegister(), MOVInstruction.Condition.NONE, false));
      instructionList.addAll(Arrays.asList(translatedDestinationTriple.third));
    }

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(FreeArray freeArray) {
    return utilJumpTo(freeArray.getOperands(), UtilMethodLabel.p_free_array);
  }

  @Override
  public Instruction[] translateFrom(FreePair freePair) {
    return utilJumpTo(freePair.getOperands(), UtilMethodLabel.p_free_pair);
  }

  @Override
  public Instruction[] translateFrom(CheckListBounds checkListBounds) {
    return utilJumpTo(checkListBounds.getOperands(), p_check_list_bounds);
  }

  @Override
  public Instruction[] translateFrom(FreeList freeList) {
    return utilJumpTo(freeList.getOperands(), p_free_list);
  }

  @Override
  public Instruction[] translateFrom(GetLastNode getLastNode) {
    return utilJumpTo(getLastNode.getOperands(), p_get_last_node,
        getLastNode.getDestinationOperand());
  }

  @Override
  public Instruction[] translateFrom(GetListElement getListElement) {
    return utilJumpTo(getListElement.getOperands(), p_get_list_element,
        getListElement.getDestinationOperand());
  }

  @Override
  public Instruction[] translateFrom(ListContains listContains) {
    return utilJumpTo(listContains.getOperands(), p_list_contains,
        listContains.getDestinationOperand());
  }

  @Override
  public Instruction[] translateFrom(RemoveFromList removeFromList) {
    return utilJumpTo(removeFromList.getOperands(), p_remove_from_list);
  }

  @Override
  public Instruction[] translateFrom(PrintBool printBool) {
    return utilJumpTo(printBool.getOperands(), UtilMethodLabel.p_print_bool);
  }

  @Override
  public Instruction[] translateFrom(PrintChar printChar) {
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(printChar.getOperands()[0]);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple.first));
    instructionList.add(new MOVInstruction(eax.getRegister(),
        translatedTriple.second, MOVInstruction.Condition.NONE, false));
    instructionList.addAll(Arrays.asList(translatedTriple.third));

    instructionList.add(new PUSHInstruction(eax.getRegister()));
    instructionList.add(new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("putchar")));
    instructionList.add(new ADDInstruction(esp.getRegister(),
        new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(PrintInteger printInteger) {
    return utilJumpTo(printInteger.getOperands(), UtilMethodLabel.p_print_int);
  }

  @Override
  public Instruction[] translateFrom(PrintList printList) {
    switch (printList.getType().toString()) {
      case "INT":
        return utilJumpTo(printList.getOperands(), p_print_list_int);

      case "STRING":
        return utilJumpTo(printList.getOperands(), p_print_list_string);

      case "CHAR":
        return utilJumpTo(printList.getOperands(), p_print_list_char);

      case "BOOL":
        return utilJumpTo(printList.getOperands(), p_print_list_bool);

      default:
        return utilJumpTo(printList.getOperands(), p_print_list_reference);
    }
  }

  @Override
  public Instruction[] translateFrom(PrintLn printLn) {
    return utilJumpTo(printLn.getOperands(), UtilMethodLabel.p_print_ln);
  }

  @Override
  public Instruction[] translateFrom(PrintReference printReference) {
    return utilJumpTo(printReference.getOperands(), UtilMethodLabel.p_print_reference);
  }

  @Override
  public Instruction[] translateFrom(PrintString printString) {
    return utilJumpTo(printString.getOperands(), UtilMethodLabel.p_print_string);
  }

  @Override
  public Instruction[] translateFrom(ReadChar readChar) {
    return utilJumpTo(readChar.getOperands(), UtilMethodLabel.p_read_char);
  }

  @Override
  public Instruction[] translateFrom(ReadInteger readInteger) {
    return utilJumpTo(readInteger.getOperands(), p_read_int);
  }

  @Override
  public Instruction[] translateFrom(CheckArrayBounds checkArrayBounds) {
    return utilJumpTo(checkArrayBounds.getOperands(), UtilMethodLabel.p_check_array_bounds);
  }

  @Override
  public Instruction[] translateFrom(CheckDivideByZero checkDivideByZero) {
    return utilJumpTo(checkDivideByZero.getOperands(), UtilMethodLabel.p_check_divide_by_zero);
  }

  @Override
  public Instruction[] translateFrom(CheckNullPointer checkNullPointer) {
    return utilJumpTo(checkNullPointer.getOperands(), UtilMethodLabel.p_check_null_pointer);
  }

  @Override
  public Instruction[] translateFrom(CheckOverflow checkOverflow) {
    Jump.Type branchType;

    switch (checkOverflow.getCondition()) {
      case OVERFLOW:
      default:
        branchType = Type.JO;
        break;

      case NOT_EQUAL:
        branchType = Type.JNE;
        break;
    }

    return new Instruction[] {
        new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_overflow_error.toString()), branchType)
    };
  }

  @Override
  public Instruction[] translateFrom(RuntimeError runtimeError) {
    return utilJumpTo(null, UtilMethodLabel.p_throw_runtime_error);
  }

  @Override
  public Instruction[] translateFrom(MessageData messageData) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.utils.x86_64.MessageData(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(messageData.getLabel().getName()), messageData.getMessage())
    };
  }

  @Override
  public Instruction[] getCode(UtilMethodLabel utilMethodLabel, String... msgLabels) {
    switch (utilMethodLabel) {
      case p_print_int:
        return getPrintIntCode(msgLabels[0]);

      case p_print_string:
        return getPrintStringCode(msgLabels[0]);

      case p_print_bool:
        return getPrintBoolCode(msgLabels[0], msgLabels[1]);

      case p_print_ln:
        return getPrintLnCode(msgLabels[0]);

      case p_print_list_int:
      case p_print_list_bool:
      case p_print_list_char:
      case p_print_list_string:
      case p_print_list_reference:
        return getPrintListCode(utilMethodLabel, msgLabels[0], msgLabels[1]);

      case p_remove_from_list:
        return getRemoveFromListCode(msgLabels[0], msgLabels[1]);

      case p_get_last_node:
        return getGetLastNodeCode(msgLabels[0], msgLabels[1]);

      case p_get_list_element:
        return getGetListElementCode(msgLabels[0], msgLabels[1]);

      case p_list_contains:
        return getListContainsCode(msgLabels[0], msgLabels[1], msgLabels[2]);

      case p_check_list_bounds:
        return getCheckListBoundsCode(msgLabels[0]);

      case p_free_list:
        return getFreeListCode(msgLabels[0], msgLabels[1]);

      case p_read_int:
        return getReadIntCode(msgLabels[0]);

      case p_read_char:
        return getReadCharCode(msgLabels[0]);

      case p_print_reference:
        return getPrintReferenceCode(msgLabels[0]);

      case p_free_pair:
        return getFreePairCode(msgLabels[0]);

      case p_free_array:
        return getFreeArrayCode(msgLabels[0]);

      case p_check_divide_by_zero:
        return getCheckDivideByZeroCode(msgLabels[0]);

      case p_check_array_bounds:
        return getCheckArrayBoundsCode(msgLabels[0], msgLabels[1]);

      case p_check_null_pointer:
        return getCheckNullPointerCode(msgLabels[0]);

      case p_throw_overflow_error:
        return getThrowOverflowErrorCode(msgLabels[0]);

      case p_throw_runtime_error:
        return getThrowRuntimeErrorCode();
    }

    return new Instruction[0];
  }

  private Instruction[] getThrowRuntimeErrorCode() {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_print_string.toString())),
        new PUSHInstruction(new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(-1)),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("exit"))
    };
  }

  private Instruction[] getThrowOverflowErrorCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_overflow_error.toString()),
        new MOVInstruction(eax.getRegister(), new CustomOperand(msgLabel), MOVInstruction.Condition.NONE, false),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()))
    };
  }

  private Instruction[] getCheckNullPointerCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_check_null_pointer.toString()),
        new uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CMPInstruction(
            eax.getRegister(), new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(0)),
        new MOVInstruction(eax.getRegister(), new CustomOperand(msgLabel), MOVInstruction.Condition.E, false),
        new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()), Type.JE),
    };
  }

  private Instruction[] getCheckArrayBoundsCode(String msgLabel, String msgLabel1) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_check_array_bounds.toString()),
        new uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CMPInstruction(
            eax.getRegister(), new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(0)),
        new MOVInstruction(eax.getRegister(), new CustomOperand(msgLabel), MOVInstruction.Condition.LT, false),
        new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()), Type.JL),
        new MOVInstruction(ebx.getRegister(), new OffsetRegister(ebx.getRegister()), MOVInstruction.Condition.NONE, false),
        new uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CMPInstruction(
            eax.getRegister(), ebx.getRegister()),
        new MOVInstruction(eax.getRegister(), new CustomOperand(msgLabel1), MOVInstruction.Condition.NONE, false),
        new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()), Type.JO),
    };
  }

  private Instruction[] getCheckDivideByZeroCode(String msgLabel) {
    return new Instruction[] {
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_check_divide_by_zero.toString()),
      new uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CMPInstruction(
          ebx.getRegister(), new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(0)),
      new MOVInstruction(eax.getRegister(), new CustomOperand(msgLabel), MOVInstruction.Condition.E, false),
      new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()), Type.JE),
    };
  }

  private Instruction[] getFreeArrayCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_free_array.toString()),
        new uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CMPInstruction(
            eax.getRegister(), new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(0)),
        new MOVInstruction(eax.getRegister(), new CustomOperand(msgLabel), MOVInstruction.Condition.E, false),
        new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()), Type.JE),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("free")),
        new ADDInstruction(esp.getRegister(), new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)),
    };
  }

  private Instruction[] getFreePairCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(
            p_free_pair.toString()),
        new CMPInstruction(eax.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(0)),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel),
            MOVInstruction.Condition.E, false),
        new Jump(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_throw_runtime_error.toString()),
            Type.JE),
        new PUSHInstruction(eax.getRegister()),
        new MOVInstruction(eax.getRegister(),
            new OffsetRegister(eax.getRegister()),
            MOVInstruction.Condition.NONE, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("free")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)),
        new MOVInstruction(eax.getRegister(),
            new OffsetRegister(esp.getRegister()),
            MOVInstruction.Condition.NONE, false),
        new MOVInstruction(eax.getRegister(),
            new OffsetRegister(eax.getRegister(), 4),
            MOVInstruction.Condition.NONE, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("free")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("free")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)),
        new SpecialLine("ret")
    };
  }

  private Instruction[] getPrintReferenceCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_print_reference.toString()),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel),
            MOVInstruction.Condition.NONE, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("printf")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)),
        new SpecialLine("ret")
    };
  }

  private Instruction[] getReadCharCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_read_char.toString()),
        new PUSHInstruction(eax.getRegister()),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel),
            MOVInstruction.Condition.NONE, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("scanf")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(8)),
        new SpecialLine("ret")
    };
  }

  private Instruction[] getReadIntCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_read_int.toString()),
        new PUSHInstruction(eax.getRegister()),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel),
            MOVInstruction.Condition.NONE, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("scanf")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(8)),
        new SpecialLine("ret")
    };
  }

  private Instruction[] getFreeListCode(String msgLabel, String msgLabel1) {
    throw new UnsupportedOperationException("Lists not implemented in x86");
  }

  private Instruction[] getCheckListBoundsCode(String msgLabel) {
    throw new UnsupportedOperationException("Lists not implemented in x86");
  }

  private Instruction[] getListContainsCode(String msgLabel, String msgLabel1, String msgLabel2) {
    throw new UnsupportedOperationException("Lists not implemented in x86");
  }

  private Instruction[] getGetListElementCode(String msgLabel, String msgLabel1) {
    throw new UnsupportedOperationException("Lists not implemented in x86");
  }

  private Instruction[] getGetLastNodeCode(String msgLabel, String msgLabel1) {
    throw new UnsupportedOperationException("Lists not implemented in x86");
  }

  private Instruction[] getRemoveFromListCode(String msgLabel, String msgLabel1) {
    throw new UnsupportedOperationException("Lists not implemented in x86");
  }

  private Instruction[] getPrintListCode(UtilMethodLabel utilMethodLabel,
      String label1, String label2) {
    throw new UnsupportedOperationException("Lists not implemented in x86");
  }

  private Instruction[] getPrintLnCode(String msgLabel) {
    return new Instruction[] {
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(p_print_ln.toString()),
      new MOVInstruction(eax.getRegister(),
          new CustomOperand("offset " + msgLabel), MOVInstruction.Condition.NONE, false),
      new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("puts")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)),
        new SpecialLine("ret")
    };
  }

  private Instruction[] getPrintBoolCode(String msgLabel, String msgLabel1) {
    return new Instruction[] {
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(
          p_print_bool.toString()),
        new uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.x86_64.CMPInstruction(eax.getRegister(), new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(0)),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel),
            MOVInstruction.Condition.NE, false),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel1),
            MOVInstruction.Condition.E, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("printf")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(4)),
        new SpecialLine("ret")
    };
  }

  private Instruction[] getPrintStringCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(
            p_print_string.toString()),
        new PUSHInstruction(eax.getRegister()),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel), MOVInstruction.Condition.NONE, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("printf")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(8)),
        new SpecialLine("ret")
    };
  }

  private Instruction[] getPrintIntCode(String msgLabel) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label(
            p_print_int.toString()),
        new PUSHInstruction(eax.getRegister()),
        new MOVInstruction(eax.getRegister(),
            new CustomOperand("offset " + msgLabel), MOVInstruction.Condition.NONE, false),
        new PUSHInstruction(eax.getRegister()),
        new Call(new uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label("printf")),
        new ADDInstruction(esp.getRegister(),
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(8)),
        new SpecialLine("ret")
    };
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]> translateFrom(Immediate immediate) {
    return new Triple<>(new Instruction[0], new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(immediate.getNumber()), new Instruction[0]);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]> translateFrom(LabelOperand labelOperand) {
    return new Triple<>(new Instruction[0], new CustomOperand(labelOperand.getTargetLabel().getName()),
      new Instruction[0]);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]> translateFrom(
      MemoryAtRegister memoryAtRegister) {
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(memoryAtRegister.getAddressRegister());

    Operand result = new OffsetRegister((uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register) translatedTriple.second, memoryAtRegister.getOffset());

    Instruction[] postInstructions = memoryAtRegister.shouldSaveBack() ? new Instruction[] {
      new ADDInstruction(translatedTriple.second, new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(memoryAtRegister.getOffset()))
    } : new Instruction[0];

    List<Instruction> postInstructionList = new ArrayList<>();
    postInstructionList.addAll(Arrays.asList(postInstructions));
    postInstructionList.addAll(Arrays.asList(translatedTriple.third));

    Instruction[] postInstructionsFinal = new Instruction[postInstructionList.size()];
    postInstructionList.toArray(postInstructionsFinal);

    return new Triple<>(translatedTriple.first, result, postInstructionsFinal);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]> translateFrom(Register register) {
    List<uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register> usableRegisters
        = uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register.getUsableRegisters();

    return new Triple<>(
        new Instruction[0],
        register.getIdentifier() == -1
            ? esp.getRegister()
            : usableRegisters.get(register.getIdentifier()),
        new Instruction[0]);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]> translateFrom(Shifted shifted) {
    ShiftInstruction.Type shiftOperand;

    switch (shifted.getShift()) {
      default:
      case ASR:
        shiftOperand = ShiftInstruction.Type.SAR;
        break;

      case LSL:
        shiftOperand = ShiftInstruction.Type.SHL;
        break;

      case LSR:
        shiftOperand = ShiftInstruction.Type.SHR;
        break;

      case ROR:
        throw new UnsupportedOperationException("ROR not supported");

      case RRX:
        shiftOperand = null;
        break;
    }

    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(shifted.getRegister());

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register register =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Register)
            translatedTriple.second;

    Instruction[] preInstructions;

    if (shiftOperand == null) {
      preInstructions = new Instruction[] {
        new SHRDInstruction(register, register, new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(shifted.getHowManyBits()))
      };
    } else {
      preInstructions =
          new Instruction[] {
            new ShiftInstruction(
                shiftOperand,
                register,
                new uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64.Immediate(
                    shifted.getHowManyBits()))
          };
    }

    List<Instruction> preInstructionList = new ArrayList<>();

    preInstructionList.addAll(Arrays.asList(translatedTriple.first));
    preInstructionList.addAll(Arrays.asList(preInstructions));

    Instruction[] preInstructionsFinal = new Instruction[preInstructionList.size()];
    preInstructionList.toArray(preInstructionsFinal);

    return new Triple<>(preInstructionsFinal, translatedTriple.second, translatedTriple.third);
  }
}
