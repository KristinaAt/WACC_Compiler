package uk.ac.ic.doc.wacc.code_generator.instructions;

import static uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction.Branch.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.BYTE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize.WORD;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDR;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDRCS;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDREQ;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDRGE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDRLT;
import static uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType.LDRNE;
import static uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.Identifier.*;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_array_bounds;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_divide_by_zero;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_list_bounds;
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_check_null_pointer;
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
import static uk.ac.ic.doc.wacc.code_generator.instructions.utils.UtilMethodLabel.p_throw_runtime_error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.ac.ic.doc.wacc.Triple;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.BranchInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.SpecialLabel;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Branch;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.StartFunction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.StartMain;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.ADDInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.ANDInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.CMPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.EORInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.LSInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.MULLInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.MVNInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.ORRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.RSBSInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.expression_instructions.SUBInstruction;
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
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.ElementSize;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.LDRInstruction.LDRType;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.MOVInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.MOVInstruction.MovType;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.POPInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.PUSHInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.STRInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Exit;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.GetReturn;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Move;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Pop;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Push;
import uk.ac.ic.doc.wacc.code_generator.instructions.memory_instructions.intermediate.Return;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ConstantOffset;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.OffsetRegister;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.ShiftOffset;
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

/* This class is used to translate the intermediate representation into ARM
   Assembly code, by making use of the relevant mnemonics and instructions.
   The compiler's default behaviour is to generate ARM Assembly. */
public class ARMTranslator implements Translator {

  @Override
  public Instruction[] translateFrom(StartFunction startFunction) {
    /* A function declared in ARM needs a label and begins by pushing the link
     register to the stack {LR} */
    List<Instruction> instructionList = new ArrayList<>(
        Arrays.asList(translateFrom(startFunction.getFunctionLabel())));
    instructionList.add(new PUSHInstruction(LR.getRegister()));

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(StartMain startMain) {
    /* Every ARM Assembly file needs a .global main and a function called
       main which begins by pushing the link register. */
    return new Instruction[] {
        new SpecialLabel("global main"),
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label("main"),
        new PUSHInstruction(LR.getRegister())
    };
  }

  @Override
  public Instruction[] translateFrom(Branch branch) {
    /* We generate the correct branch label and instruction with a
       switch statement. The cases are self explanatory. */
    BranchInstruction.Branch branchType;

    switch (branch.getCondition()) {
      case NONE:
      default:
        branchType =
            branch.shouldNextInstructionBeSaved()
                ? BranchInstruction.Branch.BL
                : B;
        break;

      case EQUAL:
        branchType =
            branch.shouldNextInstructionBeSaved()
                ? BranchInstruction.Branch.BLEQ
                : BranchInstruction.Branch.BEQ;
        break;

      case OVERFLOW:
        branchType =
            branch.shouldNextInstructionBeSaved()
                ? BranchInstruction.Branch.BLVS
                : BranchInstruction.Branch.BVS;
        break;

      case SIGNED_LESS_THAN:
        branchType =
            branch.shouldNextInstructionBeSaved()
                ? BranchInstruction.Branch.BLLT
                : BranchInstruction.Branch.BLT;
        break;

      case UNSIGNED_GREATER_THAN_OR_EQUAL:
        branchType =
            branch.shouldNextInstructionBeSaved()
                ? BranchInstruction.Branch.BLCS
                : BranchInstruction.Branch.BCS;
        break;

      case NOT_EQUAL:
        branchType =
            branch.shouldNextInstructionBeSaved()
                ? BLNE
                : BNE;
        break;
    }

    /* The last thing to do is to generate an ARM Branch Instruction with
       the Branching type and the label to branch to. Specifically an ARM
       Label, not a Intermediate Label, hence the long uk.ac.ic expression */
    return new Instruction[] {
      new BranchInstruction(
          branchType,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              branch.getTargetLabelName()))
    };
  }

  @Override
  public Instruction[] translateFrom(Label label) {
    /* An ARM Label instruction is generated, label: */
    return new Instruction[] {
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              label.getName())
    };
  }

  private
  Triple<Operand, Operand, Operand>
  getOperands(ThreeOperand threeOperandInstruction) {
    /* Generates a triple which can be used by instructions which require
       three operands, for standardisation */
    return new Triple<>(threeOperandInstruction.getDestinationOperand(),
        threeOperandInstruction.getFirstSourceOperand(),
        threeOperandInstruction.getSecondSourceOperand());
  }

  @Override
  public Instruction[] translateFrom(Add add) {
    /* Creates an ADD Arm Instruction */
    Triple<Operand, Operand, Operand> operands = getOperands(add);

    /* Ensures we store the result in a register, as other types are not
       supported in ARM */
    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Casts the destination to a register, as we it must be a register to be
       supported by ARM */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
            (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                    translatedTriple1.second;

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    /* Determines whether to create ADDS or ADD for ARM Assembly */
    if (add.shouldBooleanFlagsBeUpdated()) {
      instructionList.add(
          ADDInstruction.addWithFlags(
              destinationRegister, translatedTriple2.second,
                  translatedTriple3.second));
    } else {
      instructionList.add(
          ADDInstruction.addWithNoFlags(
              destinationRegister, translatedTriple2.second,
                  translatedTriple3.second));
    }

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(And and) {
    Triple<Operand, Operand, Operand> operands = getOperands(and);

    /* AND operations can only be carried out on registers, so checks to ensure
       garbage ARM Code is not generated */
    if (!(operands.first instanceof Register)
        || !(operands.second instanceof Register)
        || !(operands.third instanceof Register)) {
      throw new UnsupportedOperationException(
              "Operands other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Casts the operands to registers, as they cannot be anything else */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple1.second;

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister1 =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple2.second;

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister2 =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple3.second;

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    /* Performs AND on the two specified registers, placing result in
       destination register */
    instructionList.add(new ANDInstruction(destinationRegister,
            sourceRegister1, sourceRegister2));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Adds the ARM Assembly to the instruction list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Compare compare) {
    Operand firstOperand = compare.getFirstOperand();
    Operand secondOperand = compare.getSecondOperand();

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(firstOperand);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
            translateFrom(secondOperand);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    /* Performs CMP instruction on the two specified operands */
    instructionList.add(new CMPInstruction(translatedTriple1.second,
            translatedTriple2.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    /* Creates a CMP instruction to the ARM List of instructions for
       printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Divide divide) {
    Triple<Operand, Operand, Operand> operands = getOperands(divide);

    /* Ensures the destination operand is a register, as ARM does not support
       other operands */
    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Casts the destination operand to a register, as we know it must be a
       register */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple1.second;

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    /* Places the two values involved in the division into r0 and r1, ready
       for branching to the division library method */
    instructionList.add(new MOVInstruction(r0.getRegister(),
        translatedTriple2.second));
    instructionList.add(new MOVInstruction(r1.getRegister(),
        translatedTriple3.second));

    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Branch Instruction created to the library method which will help do
       division */
    instructionList.add(new BranchInstruction(BL,
            new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                    "__aeabi_idiv")));

    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.add(new MOVInstruction(destinationRegister,
        r0.getRegister()));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));

    /* Adds ARM Instruction to the list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Malloc malloc) {
    Operand destinationOperand = malloc.getDestinationOperand();

    /* Ensures the destination operand is a register as ARM will not support
       anything else */
    if (!(destinationOperand instanceof Register)) {
      throw new UnsupportedOperationException("Destination operand other than"
          + " a register not supported");
    }

    /* Determines the operands used for this instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(destinationOperand);

    List<Instruction> instructionList = new ArrayList<>();

    /* Loads the malloc size into r0 and branches to the library method
       for malloc */
    instructionList.add(new LDRInstruction(r0.getRegister(),
        new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                Immediate(malloc.getBytes()), WORD));
    instructionList.add(new BranchInstruction(BL,
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
            "malloc")));

    instructionList.addAll(Arrays.asList(translatedTriple.first));
    instructionList.add(
            new MOVInstruction((uk.ac.ic.doc.wacc.code_generator.
                    instructions.operands.Register)
                    translatedTriple.second, r0.getRegister()));
    instructionList.addAll(Arrays.asList(translatedTriple.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Mod mod) {
    Triple<Operand, Operand, Operand> operands = getOperands(mod);

    /* Ensures the destination operand is a register as ARM will not support
       anything else */
    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Casts the destination operand to a register as it cannot be anything
       else */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple1.second;

    List<Instruction> instructionList = new ArrayList<>();

    /* Puts the two values being divided into the r0 and r1 ready for
       the branch call */
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    instructionList.add(new MOVInstruction(r0.getRegister(),
        translatedTriple2.second));
    instructionList.add(new MOVInstruction(r1.getRegister(),
        translatedTriple3.second));

    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Creates a branch instruction to library method used for calculating
       the % mod operator */
    instructionList.add(new BranchInstruction(BL,
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
            "__aeabi_idivmod")));

    /* Puts result into the register given as an operand */
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.add(new MOVInstruction(destinationRegister,
        r1.getRegister()));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Multiply multiply) {

    /* Determines the operands involved in the multiply method. There are four
       operands involved. Two source operands, most significant and least
       significant */
    Operand sourceOperand1 = multiply.getFirstSourceOperand();
    Operand sourceOperand2 = multiply.getSecondSourceOperand();
    Operand destinationOperandMostSignificant =
            multiply.getDestinationOperandMostSignificant();
    Operand destinationOperandLeastSignificant =
            multiply.getDestinationOperandLeastSignificant();

    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(sourceOperand1);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
            translateFrom(sourceOperand2);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
        translateFrom(destinationOperandMostSignificant);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple4 =
        translateFrom(destinationOperandLeastSignificant);

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));
    instructionList.addAll(Arrays.asList(translatedTriple4.first));

    /* Generates a MULL or SMULL Instruction based on if flags should be
       updated, using the four specified operands */
    if (multiply.shouldBooleanFlagsBeUpdated()) {
      instructionList.add(
          MULLInstruction.mullWithFlags(
              translatedTriple4.second,
              translatedTriple3.second,
              translatedTriple1.second,
              translatedTriple2.second));
    } else {
      instructionList.add(
          MULLInstruction.mullWithNoFlags(
              translatedTriple4.second,
              translatedTriple3.second,
              translatedTriple1.second,
              translatedTriple2.second));
    }

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));
    instructionList.addAll(Arrays.asList(translatedTriple4.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Not not) {
    Operand sourceOperand = not.getSourceOperand();
    Operand destinationOperand = not.getDestinationOperand();

    /* Ensures the source and destination operands are both registers,
       as NOT can only be done on registers in ARM */
    if (!(sourceOperand instanceof Register) ||
            !(destinationOperand instanceof Register)) {
      throw new UnsupportedOperationException("Operands other than a register"
          + " not supported");
    }

    /* Determines the operands involved in the operation */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
        translateFrom(sourceOperand);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(destinationOperand);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));

    /* Casts the operands to Register Operands, as we have determined they
       cannot be anything else */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister =
            (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                    translatedTriple1.second;
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
            (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                    translatedTriple2.second;

    /* Creates a MVN instruction for performing Boolean NOT using the source
       and dest registers */
    instructionList.add(new MVNInstruction(destinationRegister,
        sourceRegister));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Or or) {
    Triple<Operand, Operand, Operand> operands = getOperands(or);

    /* Ensures the destination and two source operands are registers as ARM
       supports OR on registers only */
    if (!(operands.first instanceof Register)
        || !(operands.second instanceof Register)
        || !(operands.third instanceof Register)) {
      throw new UnsupportedOperationException(
              "Operands other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Casts the operands to registers as we have determined they cannot be
       anything else */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple1.second;

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister1 =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple2.second;

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister2 =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple3.second;

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    /* Creates an ORR instruction using the destination register and two
       source registers */
    instructionList.add(new ORRInstruction(destinationRegister,
            sourceRegister1, sourceRegister2));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Shift shift) {
    Triple<Operand, Operand, Operand> operands = getOperands(shift);

    /* Ensures the destination and two source operands are registers as
       shifting in ARM is supported for registers only */
    if (!(operands.first instanceof Register)
        || !(operands.second instanceof Register)
        || !(operands.third instanceof Register)) {
      throw new UnsupportedOperationException(
              "Operands other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Casts the operands to registers as we have determined they cannot be of
       any other type */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple1.second;

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister1 =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple2.second;

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister2 =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple3.second;

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    /* Determines which ARM shifting Operand to use based on the shift type */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift shiftOperand;

    switch (shift.getShift()) {
      default:
      case ASR:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.ASR;
        break;

      case LSL:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.LSL;
        break;

      case LSR:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.LSR;
        break;

      case ROR:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.ROR;
        break;

      case RRX:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.RRX;
        break;
    }

    /* Creates a new SHIFT instruction with the correct shift type,
       destination register and the two source registers. */
    instructionList.add(new LSInstruction(shiftOperand, destinationRegister,
            sourceRegister1, sourceRegister2));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Subtract subtract) {
    Triple<Operand, Operand, Operand> operands = getOperands(subtract);

    /* Ensures the destination operand is a register as ARM does not support
       anything else. */
    if (!(operands.first instanceof Register)) {
      throw new UnsupportedOperationException(
          "Destination operand other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Used to determine RSBS or SUB instruction to be used */
    boolean firstOperandNotRegister = !(operands.second instanceof Register);

    /* Casts the destination operand to a register as we have determined it
       should not be anything else.*/
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple1.second;

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    /* If we have a memory subtraction, we must use RSBS */
    if (firstOperandNotRegister) {
      instructionList.add(new RSBSInstruction(destinationRegister,
          (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                  translatedTriple3.second, translatedTriple2.second));
    } else {
      /* Otherwise we need to create a SUB or SUBS instruction based on
         whether or not we want the flags to be set. */
      if (subtract.shouldBooleanFlagsBeUpdated()) {
        instructionList.add(
            SUBInstruction.subWithFlags(
                destinationRegister, translatedTriple2.second,
                translatedTriple3.second));
      } else {
        instructionList.add(
            SUBInstruction.subWithNoFlags(
                destinationRegister, translatedTriple2.second,
                translatedTriple3.second));
      }
    }

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Xor xor) {
    Triple<Operand, Operand, Operand> operands = getOperands(xor);

    /* Ensures that XOR is being done on registers as anything else is not
       supported in ARM. */
    if (!(operands.first instanceof Register) ||
            !(operands.second instanceof Register)) {
      throw new UnsupportedOperationException(
              "Operands other than a register not supported");
    }

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple1 =
            translateFrom(operands.first);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple2 =
        translateFrom(operands.second);
    Triple<Instruction[], Operand, Instruction[]> translatedTriple3 =
            translateFrom(operands.third);

    /* Casts the destination and source to registers as we know they must be
       register operands and nothing else */
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            destinationRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple1.second;

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
            sourceRegister =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                translatedTriple2.second;

    List<Instruction> instructionList = new ArrayList<>();
    instructionList.addAll(Arrays.asList(translatedTriple1.first));
    instructionList.addAll(Arrays.asList(translatedTriple2.first));
    instructionList.addAll(Arrays.asList(translatedTriple3.first));

    /* Creates an ARM EOR Instruction using the destination register and the
       two source operands (where one must be a register). */
    instructionList.add(
        new EORInstruction(destinationRegister, sourceRegister,
                translatedTriple3.second));

    instructionList.addAll(Arrays.asList(translatedTriple1.third));
    instructionList.addAll(Arrays.asList(translatedTriple2.third));
    instructionList.addAll(Arrays.asList(translatedTriple3.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(Move move) {
    Operand sourceOperand = move.getSourceOperand();
    Operand destinationOperand = move.getDestinationOperand();

    /* Determines the operands involved in the instruction */
    Triple<Instruction[], Operand, Instruction[]> translatedTripleSource =
        translateFrom(sourceOperand);
    Triple<Instruction[], Operand, Instruction[]> translatedTripleDestination =
        translateFrom(destinationOperand);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTripleSource.first));
    instructionList.addAll(Arrays.asList(translatedTripleDestination.first));

    /* Move Instructions differ strongly based on what kind of move is done.
       The first case is if we have ldr r0, =1 or mov r0, r1*/
    if ((sourceOperand instanceof Register
            || sourceOperand instanceof Immediate)
        && destinationOperand instanceof Register) {
      uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
              destinationRegister =
          (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
              translatedTripleDestination.second;

      /* If we have immediate operands then we need to use LDR, load register,
         with immediate values as the operand */
      if (translatedTripleSource.second instanceof
              uk.ac.ic.doc.wacc.code_generator.instructions.operands.Immediate)
      {
        instructionList.add(
            new LDRInstruction(destinationRegister,
                translatedTripleSource.second, WORD));
      } else {
        /* If we have mov r0, r1, we need to create a mov instruction with
           the correct MOV Type based on suffix */
        MovType movType;

        switch (move.getCondition()) {
          case NONE:
          default:
            movType = MovType.MOV;
            break;

          case EQUAL:
            movType = MovType.MOVEQ;
            break;

          case NOT_EQUAL:
            movType = MovType.MOVNE;
            break;

          case GREATER_EQUAL:
            movType = MovType.MOVGE;
            break;

          case GREATER_THAN:
            movType = MovType.MOVGT;
            break;

          case LESS_EQUAL:
            movType = MovType.MOVLE;
            break;

          case LESS_THAN:
            movType = MovType.MOVLT;
            break;
        }

        /* We create a MOV Instruction with the correct mov type using the
           destination register and source operand */
        instructionList.add(
            new MOVInstruction(destinationRegister,
                translatedTripleSource.second,
                movType));
      }
    } else if ((sourceOperand instanceof MemoryAtRegister ||
            sourceOperand instanceof LabelOperand)
        && destinationOperand instanceof Register) {
      /* In this case, we are loading from memory, and need to use
         off-setted registers. We cannot load more than 4 bytes */
      if (move.getSizeInBytes() > 4) {
        throw new UnsupportedOperationException(
                "The element size is too large (> 4 bytes)");
      }

      /* Based on the size we are loading, we either use LDR or LDRB */
      ElementSize size = move.getSizeInBytes() == 1 ? ElementSize.BYTE : WORD;

      /* We also need to determine the LDR type based on if we want to load
         only if condition flags are set */
      LDRType ldrType;

      switch (move.getCondition()) {
        case NONE:
        default:
          ldrType = LDRType.LDR;
          break;

        case EQUAL:
          ldrType = LDRType.LDREQ;
          break;

        case NOT_EQUAL:
          ldrType = LDRType.LDRNE;
          break;

        case UNSIGNED_GREATER_EQUAL:
          ldrType = LDRType.LDRCS;
          break;

        case LESS_THAN:
          ldrType = LDRType.LDRLT;
          break;
      }

      /* Cast the destination operand to a register as it must be a register */
      uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
              destinationRegister =
          (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
              translatedTripleDestination.second;

      /* We create an LDR or LDRB instruction with the correct type using the
      destination register and source operand */
      instructionList.add(
          new LDRInstruction(ldrType, destinationRegister,
                  translatedTripleSource.second, size));
    } else if ((sourceOperand instanceof Register ||
            sourceOperand instanceof Immediate)
        && destinationOperand instanceof MemoryAtRegister) {
      /* In this case, we are storing values into memory, and need to use
         an STR instruction. E.G. STR [r0], #4.
         We cannot store more than 4 bytes at a time. */
      if (move.getSizeInBytes() > 4) {
        throw new UnsupportedOperationException("The element size is too " +
                "large (> 4 bytes)");
      }

      /* We will either have STR or STRB depending on whether we want to store
         4 bytes or a single byte */
      ElementSize size = move.getSizeInBytes() == 1 ? ElementSize.BYTE : WORD;

      /* We cast the soure operand to a register as it must be a register */
      uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
              sourceRegister =
          (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
              translatedTripleSource.second;

      /* Create a new STR or STRB instruction with the destination memory
         address and source operand */
      instructionList.add(new STRInstruction(sourceRegister,
          translatedTripleDestination.second, size));
    } else {
      /* Anything else is not supported, e.g. memory to memory moves. */
      throw new UnsupportedOperationException("Unsupported operand types");
    }

    instructionList.addAll(Arrays.asList(translatedTripleSource.third));
    instructionList.addAll(Arrays.asList(translatedTripleDestination.third));

    /* Adds the instruction to the instructions list for printing */
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
      /* Creates a POP instruction with the register as an operand */
      return new Instruction[] {
        new POPInstruction(
            (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                    triple.second)
      };
    } else {
      throw new UnsupportedOperationException(
              /* Cannot POP to anything other than a register */
          "Destination operand other than a register not supported");
    }
  }

  @Override
  public Instruction[] translateFrom(Push push) {
    Operand sourceOperand = push.getSourceOperand();

    if (sourceOperand instanceof Register) {
      Triple<Instruction[], Operand, Instruction[]> triple =
          translateFrom((Register) sourceOperand);
      /* Creates a PUSH instruction with the register as an operand */
      return new Instruction[] {
        new PUSHInstruction(
            (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                    triple.second)
      };
    } else {
      throw new UnsupportedOperationException(
              /* Cannot PUSH anything other than a register */
          "Destination operand other than a register not supported");
    }
  }

  @Override
  public Instruction[] translateFrom(Return returnInstruction) {
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(returnInstruction.getReturnValue());

    List<Instruction> instructionList = new ArrayList<>();

    /* To return from a function, the result is expected to have been placed
       into r0, so mov the result to r0 */
    if (translatedTriple != null) {
      instructionList.addAll(Arrays.asList(translatedTriple.first));
      instructionList.add(new MOVInstruction(r0.getRegister(),
          translatedTriple.second));
      instructionList.addAll(Arrays.asList(translatedTriple.third));
    }

    /* Next, branch to the exit label which performs the ARM takedown of the
       stack*/
    if (returnInstruction.getFunctionExitLabel() != null) {
      instructionList.add(new BranchInstruction(B,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                  returnInstruction.getFunctionExitLabel())));
    }

    /* Finally, pop the stack to the program counter to return to the caller */
    instructionList.add(new POPInstruction(PC.getRegister()));

    /* .ltorg to ensure we don't create a segmentation fault by reading data */
    instructionList.add(new SpecialLabel("ltorg"));

    /* Adds the instruction to the instructions list for printing */
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

    /* Moves the exit code into r0 */
    instructionList.add(new MOVInstruction(r0.getRegister(),
            translatedTriple.second));

    instructionList.addAll(Arrays.asList(translatedTriple.third));

    /* Branches to the library code used for performing a system exit */
    instructionList.add(new BranchInstruction(BL,
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                "exit")));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Instruction[] translateFrom(GetReturn getReturn) {
    Operand destinationOperand = getReturn.getDestinationOperand();

    /* Result from a call must be placed into a register in ARM. */
    if (!(destinationOperand instanceof Register)) {
      throw new UnsupportedOperationException("Destination operand other than"
          + " a register not supported");
    }

    /* Get the operand involved in getting the return value */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(destinationOperand);

    List<Instruction> instructionList = new ArrayList<>();

    instructionList.addAll(Arrays.asList(translatedTriple.first));

    /* Create a MOV instruction moving the result from r0 into the destination
       operand, casting the operand to a register */
    instructionList.add(new MOVInstruction((uk.ac.ic.doc.wacc.code_generator.
            instructions.operands.Register) translatedTriple.second,
            r0.getRegister()));

    instructionList.addAll(Arrays.asList(translatedTriple.third));

    /* Adds the instruction to the instructions list for printing */
    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]>
  translateFrom(Immediate immediate) {
    /* Creates a triple operand to be used by the translator, creating a
       Constant Offset for small numbers (#4) or immediate constant for larger
       numbers (=12345) */
    return new Triple<>(
        new Instruction[0],
        immediate.getNumber() > 255 || immediate.getNumber() < -255 ?
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                    Immediate(immediate.getNumber()) :
            new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                    ConstantOffset(immediate.getNumber()),
        new Instruction[0]);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]>
  translateFrom(LabelOperand labelOperand) {
    /* Creates a triple operand to be used by the translator, creating a
       label operand of the form =label_name */
    return new Triple<>(
        new Instruction[0],
        new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                LabelOperand(labelOperand.getTargetLabel().getName()),
            new Instruction[0]);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]> translateFrom(
      MemoryAtRegister memoryAtRegister) {

    /* Recursively determines the register operand triple. */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(memoryAtRegister.getAddressRegister());

    Operand result;

    /* We either have a no offsetted register [r0] or a pre-indexed offset
       register [r0, #4] in ARM that we are loading data from. Based on the
       type, we create the correct operand type.*/
    if (memoryAtRegister.getOffset() == 0) {
      result =
          OffsetRegister.noOffset(
              (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                  translatedTriple.second);
    } else {
      result =
          OffsetRegister.preIndexedOffset(
              (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                  translatedTriple.second,
              new ConstantOffset(memoryAtRegister.getOffset()),
              memoryAtRegister.shouldSaveBack());
    }

    /* Creates a triple operand of an offsetted register */
    return new Triple<>(translatedTriple.first, result,
            translatedTriple.third);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]>
  translateFrom(Register register) {
    List<uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register>
            usableRegisters =
        uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register.
                getUsableRegisters();

    /* Creates a triple operand of a register that can be used by using an
       index and determing the correct usable ARM register e.g. r4-13.
       An index of -1 represents the SP register. */
    return new Triple<>(new Instruction[0],
        register.getIdentifier() == -1 ? SP.getRegister() :
            usableRegisters.get(register.getIdentifier()),
        new Instruction[0]);
  }

  @Override
  public Triple<Instruction[], Operand, Instruction[]> translateFrom(
      Shifted shifted) {
    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift shiftOperand;

    /* Creates the correct ARM shift type based on which shifting we require */
    switch (shifted.getShift()) {
      default:
      case ASR:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.ASR;
        break;

      case LSL:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.LSL;
        break;

      case LSR:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.LSR;
        break;

      case ROR:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.ROR;
        break;

      case RRX:
        shiftOperand =
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Shift.RRX;
        break;
    }

    /* Recursively determines the register operand */
    Triple<Instruction[], Operand, Instruction[]> translatedTriple =
        translateFrom(shifted.getRegister());

    uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register register =
        (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
            translatedTriple.second;

    /* Creates a triple operand of a shifted register to be used by
       the translator with the correct register and correct shift type. */
    return new Triple<>(translatedTriple.first,
        ShiftOffset.offset(false, register, shiftOperand,
            shifted.getHowManyBits()), translatedTriple.third);
  }

  private Instruction[] utilJumpTo(Operand[] operands,
      UtilMethodLabel methodLabel) {
    return utilJumpTo(operands, methodLabel, null);
  }

  /* Method used for translating util methods such as free array */
  private Instruction[] utilJumpTo(Operand[] operands,
      UtilMethodLabel methodLabel, Operand destinationOperand) {
    List<Triple<Instruction[], Operand, Instruction[]>> translatedTriples =
        new ArrayList<>();

    /* Determines any operands such as msg_labels which are needed to define
       the util method.*/
    if (operands != null) {
      for (Operand operand : operands) {
        translatedTriples.add(translateFrom(operand));
      }
    }

    List<Instruction> instructionList = new ArrayList<>();

    int i = 0;

    /* Determines the sub operands of the triple determined in the earlier
       for loop.*/
    for (Triple<Instruction[], Operand, Instruction[]> triple :
        translatedTriples) {
      instructionList.addAll(Arrays.asList(triple.first));
      if (operands[i] instanceof MemoryAtRegister) {
        instructionList.add(new LDRInstruction(
                uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
                        .Identifier.values()[i].getRegister(),
                triple.second, WORD));
      } else {
        instructionList.add(new MOVInstruction(
            uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register
                .Identifier.values()[i].getRegister(), triple.second));
      }
      instructionList.addAll(Arrays.asList(triple.third));

      i++;
    }

    /* Creates a branch instruction BL to the util method label */
    instructionList.add(
        new BranchInstruction(
            BranchInstruction.Branch.BL,
            new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label
                    (methodLabel.name())));

    /* Ensures that if the result needs to be placed in an operand,
       the operand must be a register*/
    if (destinationOperand != null) {
      if (!(destinationOperand instanceof Register)) {
        throw new UnsupportedOperationException("Destination operand other "
            + "than a register not supported");
      }

      /* Determines the destination operand */
      Triple<Instruction[], Operand, Instruction[]> translatedDestinationTriple
              = translateFrom(destinationOperand);

      /* Places the result of a util method inside r0 */
      instructionList.addAll(Arrays.asList(translatedDestinationTriple.first));
      instructionList.add(new MOVInstruction(
              (uk.ac.ic.doc.wacc.code_generator.instructions.operands.Register)
                      translatedDestinationTriple.second, r0.getRegister()));
      instructionList.addAll(Arrays.asList(translatedDestinationTriple.third));
    }

    Instruction[] instructions = new Instruction[instructionList.size()];
    instructionList.toArray(instructions);

    return instructions;
  }

  /* When translating a util method, we need to create branches to the method
     and generate ARM Assembly code. The following methods call utilJumpTo
     which will perform this.*/
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

    /* Printing characters is special, as we simply create a branch instruction
       to the library code "putchar" */
    instructionList.addAll(Arrays.asList(translatedTriple.first));
    instructionList.add(new MOVInstruction(r0.getRegister(),
        translatedTriple.second));
    instructionList.addAll(Arrays.asList(translatedTriple.third));
    instructionList.add(
        new BranchInstruction(
            BranchInstruction.Branch.BL,
            new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                "putchar")));

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
    /* We create a print list util method based on the type we are trying to
       print to the console, and so recursively call utilJumpTo with the
       correct util method label.*/
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
    return utilJumpTo(printReference.getOperands(),
            UtilMethodLabel.p_print_reference);
  }

  @Override
  public Instruction[] translateFrom(PrintString printString) {
    return utilJumpTo(printString.getOperands(),
            UtilMethodLabel.p_print_string);
  }

  @Override
  public Instruction[] translateFrom(ReadChar readChar) {
    return utilJumpTo(readChar.getOperands(),
            UtilMethodLabel.p_read_char);
  }

  @Override
  public Instruction[] translateFrom(ReadInteger readInteger) {
    return utilJumpTo(readInteger.getOperands(), UtilMethodLabel.p_read_int);
  }

  @Override
  public Instruction[] translateFrom(CheckArrayBounds checkArrayBounds) {
    return utilJumpTo(checkArrayBounds.getOperands(),
            UtilMethodLabel.p_check_array_bounds);
  }

  @Override
  public Instruction[] translateFrom(CheckDivideByZero checkDivideByZero) {
    return utilJumpTo(checkDivideByZero.getOperands(),
            UtilMethodLabel.p_check_divide_by_zero);
  }

  @Override
  public Instruction[] translateFrom(CheckNullPointer checkNullPointer) {
    return utilJumpTo(checkNullPointer.getOperands(),
            UtilMethodLabel.p_check_null_pointer);
  }

  @Override
  public Instruction[] translateFrom(CheckOverflow checkOverflow) {
    BranchInstruction.Branch branchType;

    /* The Check Overflow method requires two possible branching labels.
       One for if Overflow does occur and one if it doesn't. */
    switch (checkOverflow.getCondition()) {
      case OVERFLOW:
      default:
        branchType = BLVS;
        break;

      case NOT_EQUAL:
        branchType = BLNE;
        break;
    }

    /* We simply need to branch to p_throw_overflow_error util method */
    return new Instruction[] {
        new BranchInstruction(branchType,
            new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                UtilMethodLabel.p_throw_overflow_error.toString()))
    };
  }

  @Override
  public Instruction[] translateFrom(RuntimeError runtimeError) {
    return utilJumpTo(null, UtilMethodLabel.p_throw_runtime_error);
  }

  @Override
  public Instruction[] translateFrom(MessageData messageData) {
    /* Creates a message data which uses the given msg_label as its
       definition */
    return new Instruction[] {
      new uk.ac.ic.doc.wacc.code_generator.instructions.utils.MessageData(
          (uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label)
                  translateFrom(messageData.getLabel())[0],
          messageData.getMessage())
    };
  }

  private Instruction[] getPrintIntCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the label for printing an integer and pushes the
         return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_print_int.toString()),
      new PUSHInstruction(LR.getRegister()),
      new MOVInstruction(r1.getRegister(), r0.getRegister()),
      new LDRInstruction(
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel), WORD),

      /* Set-up for printing and branching to a utility function printf */
      ADDInstruction.addWithNoFlags(r0.getRegister(), r0.getRegister(),
          new ConstantOffset(4)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("printf")),

      /* Resets the r0 value and flushes the stream to stdout */
      new MOVInstruction(r0.getRegister(), new ConstantOffset(0)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("fflush")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getPrintStringCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the label for printing a string and pushes the
         return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label
              (p_print_string.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Set-up for printing and branching to a utility function printf */
      new LDRInstruction(r1.getRegister(), OffsetRegister.noOffset(
              r0.getRegister()),
          WORD),
      ADDInstruction.addWithNoFlags(r2.getRegister(), r0.getRegister(),
          new ConstantOffset(4)),
      new LDRInstruction(
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      ADDInstruction.addWithNoFlags(r0.getRegister(), r0.getRegister(),
          new ConstantOffset(4)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("printf")),

      /* Resets the r0 value and flushes the stream to stdout */
      new MOVInstruction(r0.getRegister(), new ConstantOffset(0)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("fflush")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getPrintBoolCode(String msgLabel1, String msgLabel2) {
    return new Instruction[] {
      /* Generates the label for printing a bool and pushes the
         return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_print_bool.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Checks whether false or true is being printed and puts
         a corresponding message into r0 */
      new CMPInstruction(r0.getRegister(), new ConstantOffset(0)),
      new LDRInstruction(
          LDRNE,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel1),
          WORD),
      new LDRInstruction(
          LDREQ,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel2),
          WORD),

      /* Set-up for printing and branching to a utility function printf */
      ADDInstruction.addWithNoFlags(r0.getRegister(), r0.getRegister(),
          new ConstantOffset(4)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("printf")),

      /* Resets the r0 value and flushes the stream to stdout */
      new MOVInstruction(r0.getRegister(), new ConstantOffset(0)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("fflush")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getPrintLnCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the label for printing on a new line and pushes the
         return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_print_ln.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* General set-up for printing on a new line */
      new LDRInstruction(
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      ADDInstruction.addWithNoFlags(r0.getRegister(), r0.getRegister(),
              new ConstantOffset(4)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("puts")),

      /* Resets the r0.getRegister() value and flushes the stream to stdout */
      new MOVInstruction(r0.getRegister(), new ConstantOffset(0)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("fflush")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getReadIntCode(String msgLabel) {
    return new Instruction[] {
      /* Generated the label for printing on a new line and pushes the
         return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_read_int.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* General set-up for reading an integer from stdin */
      new MOVInstruction(r1.getRegister(), r0.getRegister()),
      new LDRInstruction(
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      ADDInstruction.addWithNoFlags(r0.getRegister(), r0.getRegister(),
              new ConstantOffset(4)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("scanf")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister()),
    };
  }

  private Instruction[] getReadCharCode(String msgLabel) {
    return new Instruction[] {
      /* Generated the label for printing on a new line and pushes the
         return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_read_char.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* General set-up for reading a character from stdin */
      new MOVInstruction(r1.getRegister(), r0.getRegister()),
      new LDRInstruction(
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      ADDInstruction.addWithNoFlags(r0.getRegister(), r0.getRegister(),
              new ConstantOffset(4)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("scanf")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getPrintReferenceCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the label for printing a reference and pushes the
         return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_print_reference.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Set-up for printing and branching to a utility function printf */
      new MOVInstruction(r1.getRegister(), r0.getRegister()),
      new LDRInstruction(
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      ADDInstruction.addWithNoFlags(r0.getRegister(), r0.getRegister(),
              new ConstantOffset(4)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("printf")),

      /* Resets the r0.getRegister() value and flushes the stream to stdout */
      new MOVInstruction(r0.getRegister(), new ConstantOffset(0)),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("fflush")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getFreePairCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the free pair label followed by the assembly
         code for freeing a pair */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_free_pair.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Checks whether we try to free a null */
      new CMPInstruction(r0.getRegister(), new ConstantOffset(0)),
      new LDRInstruction(
          LDREQ,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      new BranchInstruction(
          BEQ,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_throw_runtime_error.toString())),

      /* Pushes the address of the pair onto the stack */
      new PUSHInstruction(r0.getRegister()),

      /* Dereferences the first element of a pair and frees it */
      new LDRInstruction(r0.getRegister(), OffsetRegister.noOffset(
              r0.getRegister()), WORD),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("free")),

      /* Dereferences the second element of a pair and frees it */
      new LDRInstruction(r0.getRegister(), OffsetRegister.noOffset(
              SP.getRegister()),
          WORD),
      new LDRInstruction(r0.getRegister(), OffsetRegister.preIndexedOffset(
              r0.getRegister(), new ConstantOffset(4)), WORD),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("free")),

      /* Pops the pair address off the stack and frees the reference
         to the pair itself */
      new POPInstruction(r0.getRegister()),
      new BranchInstruction(
          BL, new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
              Label("free")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getFreeArrayCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the free array label followed by the assembly
         code for freeing an array */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
          UtilMethodLabel.p_free_array.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Checks whether we try to free a null */
      new CMPInstruction(r0.getRegister(), new ConstantOffset(0)),
      new LDRInstruction(
          LDRType.LDREQ,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      new BranchInstruction(
          BranchInstruction.Branch.BEQ,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              UtilMethodLabel.p_throw_runtime_error.toString())),

      /* Frees the array reference */
      new BranchInstruction(
          BranchInstruction.Branch.BL,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
                  Label("free")),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getCheckDivideByZeroCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the label for checking division by zero and pushes
         the return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
          p_check_divide_by_zero.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Checks if the number we divide by is equal to zero and if that is
         the case we put the corresponding message data into r0.getRegister()
         and branch to the assembly code for throwing a runtime error */
      new CMPInstruction(r1.getRegister(), new ConstantOffset(0)),
      new LDRInstruction(
          LDREQ,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      new BranchInstruction(
          BLEQ,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_throw_runtime_error.toString())),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[]
  getCheckArrayBoundsCode(String msgLabel1, String msgLabel2) {
    return new Instruction[] {
      /* Generates the label for checking if an array is out of bounds
         and pushes the return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
          p_check_array_bounds.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Checks if we are trying to access a negative index and throws
         a runtime error if so */
      new CMPInstruction(r0.getRegister(), new ConstantOffset(0)),
      new LDRInstruction(
          LDRLT,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel1),
          WORD),
      new BranchInstruction(
          BLLT,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_throw_runtime_error.toString())),

      /* Checks if the index we try to access is beyond the size of the array
         and branches to the throw runtime error label */
      new LDRInstruction(LDR, r1.getRegister(), OffsetRegister.noOffset(
              r1.getRegister()), WORD),
      new CMPInstruction(r0.getRegister(), r1.getRegister()),
      new LDRInstruction(
          LDRCS,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel2),
          WORD),
      new BranchInstruction(
          BLCS,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_throw_runtime_error.toString())),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getCheckNullPointerCode(String msgLabel) {
    return new Instruction[] {
      /* Generates the label for checking a null pointer reference
         and pushes the return address onto the stack */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
          p_check_null_pointer.toString()),
      new PUSHInstruction(LR.getRegister()),

      /* Checks if the pointer is null by comparing it to 0 */
      new CMPInstruction(r0.getRegister(), new ConstantOffset(0)),

      /* Loads a corresponding message if the pointer is null and branches to
         the assembly code for throwing a runtime error */
      new LDRInstruction(
          LDREQ,
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      new BranchInstruction(
          BLEQ,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              p_throw_runtime_error.toString())),

      /* Pops the program counted off the stack */
      new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getThrowOverflowErrorCode(String msgLabel) {
    return new Instruction[] {
      /* Loads a corresponding message if we get an overflow and branches to
         the assembly code for throwing a runtime error */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
          UtilMethodLabel.p_throw_overflow_error.toString()),
      new LDRInstruction(
          r0.getRegister(),
          new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                  LabelOperand(msgLabel),
          WORD),
      new BranchInstruction(
          BranchInstruction.Branch.BL,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              UtilMethodLabel.p_throw_runtime_error.toString()))
    };
  }

  private Instruction[] getThrowRuntimeErrorCode() {
    return new Instruction[] {
      /* Generates the label for throwing a runtime error */
      new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
          UtilMethodLabel.p_throw_runtime_error.toString()),

      /* Branches to the code for printing a corresponting message
         that is previously stored in r0.getRegister() */
      new BranchInstruction(
          BranchInstruction.Branch.BL,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
              UtilMethodLabel.p_print_string.toString())),

      /* Exits with an exit code -1 (255) */
      new MOVInstruction(r0.getRegister(), new ConstantOffset(-1)),
      new BranchInstruction(
          BranchInstruction.Branch.BL,
          new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
                  Label("exit"))
    };
  }

  private Instruction[] getPrintListCode(UtilMethodLabel printTypeLabel,
      String label1, String label2) {
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label firstLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label1);
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label secondLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label2);

    ElementSize elementSize;
    UtilMethodLabel utilPrintLabel;

    /* Determine which print method to generate code for based on the label */
    switch (printTypeLabel) {
      case p_print_list_int:
        utilPrintLabel = p_print_int;
        elementSize = WORD;
        break;

      case p_print_list_bool:
        utilPrintLabel = p_print_bool;
        elementSize = BYTE;
        break;

      case p_print_list_char:
        utilPrintLabel = null;
        elementSize = BYTE;
        break;

      case p_print_list_string:
        utilPrintLabel = p_print_string;
        elementSize = WORD;
        break;

      case p_print_list_reference:
        utilPrintLabel = p_print_reference;
        elementSize = WORD;
        break;

      default:
        throw new UnsupportedOperationException("Incorrect printTypeLabel");
    }

    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label printLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
            utilPrintLabel == null ? "putchar" : utilPrintLabel.toString());

    return new Instruction[] {
        /* Generates the label for printing a list and pushes the
       return address onto the stack */
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.
                Label(printTypeLabel.toString()),
    new PUSHInstruction(LR.getRegister()),

    /* Print '[' */
    new PUSHInstruction(r0.getRegister()),
    new LDRInstruction(r0.getRegister(), new uk.ac.ic.doc.wacc.code_generator.
            instructions.operands.Immediate(91), BYTE),
    new BranchInstruction(BL, new uk.ac.ic.doc.wacc.code_generator.
            instructions.basics.Label("putchar")),
    new POPInstruction(r0.getRegister()),

    /* Generates the label for checking if the current node is null */
    new BranchInstruction(BL, firstLabel),

    /* Loads the address of the data of the current node in r0 and puts it
       on the stack */
    secondLabel,
    new LDRInstruction(r0.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
                new ConstantOffset(4)), WORD),
    new PUSHInstruction(r0.getRegister()),

    /* Loads the data into r0 and branches to the specific print method
       based on the data type */
    new LDRInstruction(r0.getRegister(),
        OffsetRegister.noOffset(r0.getRegister()), elementSize),
    new BranchInstruction(BL, printLabel),
    new POPInstruction(r0.getRegister()),

    /* Print a space */
    new PUSHInstruction(r0.getRegister()),
    new LDRInstruction(r0.getRegister(),
        new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                Immediate(32), BYTE),
    new BranchInstruction(BL, new uk.ac.ic.doc.wacc.code_generator.
            instructions.basics.Label("putchar")),
    new POPInstruction(r0.getRegister()),

    /* Checks of the address of the current node is null */
    firstLabel,
    new LDRInstruction(r1.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
                new ConstantOffset(4)), WORD),
    new CMPInstruction(r1.getRegister(), new ConstantOffset(0)),
    new BranchInstruction(BNE, secondLabel),

    /* Print ']' */
    new PUSHInstruction(r0.getRegister()),
    new LDRInstruction(r0.getRegister(), new uk.ac.ic.doc.wacc.code_generator.
            instructions.operands.Immediate(93), BYTE),
    new BranchInstruction(BL, new uk.ac.ic.doc.wacc.code_generator.
            instructions.basics.Label("putchar")),
    new POPInstruction(r0.getRegister()),

    new POPInstruction(PC.getRegister()),
    };
  }

  private Instruction[] getRemoveFromListCode(String label1,
      String label2) {
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label firstLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label1);
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label secondLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label2);

    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                p_remove_from_list.toString()),
    new PUSHInstruction(LR.getRegister()),

    /* While loop, dereference addresses until ... */
    new BranchInstruction(BL, secondLabel),
    firstLabel,
    new LDRInstruction(r0.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)), WORD),
    SUBInstruction.subWithNoFlags(r1.getRegister(),
        r1.getRegister(), new ConstantOffset(1)),

    /* We have reached the nth element. We are at the one before the one
       we wish to delete */
    secondLabel,
    new CMPInstruction(r1.getRegister(), new ConstantOffset(0)),
    new BranchInstruction(BNE, firstLabel),

    /* Finally, change the pointer at this node to be the same as the pointer
       of the next node */
    new LDRInstruction(r1.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)), WORD),
    /* This is to remember the address of the node for freeing later */
    new MOVInstruction(r2.getRegister(), r1.getRegister()),
    new LDRInstruction(r1.getRegister(),
        OffsetRegister.preIndexedOffset(r1.getRegister(),
            new ConstantOffset(4)), WORD),
    new STRInstruction(r1.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)), WORD),

    /* Free the allocated memory for the node by calling the free method */
    new MOVInstruction(r0.getRegister(), r2.getRegister()),
    new BranchInstruction(BL, new uk.ac.ic.doc.wacc.code_generator.instructions.
            basics.Label("free")),

    new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getGetLastNodeCode(String label1, String label2) {
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label firstLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label1);
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label secondLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label2);
    
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                p_get_last_node.toString()),
    new PUSHInstruction(LR.getRegister()),

        /* Branch label for checking if the current address stores a
           null pointer */
    new BranchInstruction(BL, firstLabel),

    /* Gets the next list element address */
    secondLabel,
    new LDRInstruction(r0.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)), WORD),

    /* Checks if the current element address is null. Loops if not */
    firstLabel,
    new LDRInstruction(r1.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)), WORD),
    new CMPInstruction(r1.getRegister(), new ConstantOffset(0)),
    new BranchInstruction(BNE, secondLabel),

    new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getCheckListBoundsCode(String label) {
    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                p_check_list_bounds.toString()),
    new PUSHInstruction(LR.getRegister()),

    /* r0 contains list access, r1 contains list size */
    new CMPInstruction(r0.getRegister(), r1.getRegister()),

    /* If r1 >= r0 then runtime error */
    new LDRInstruction(LDRGE, r0.getRegister(),
        new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                LabelOperand(label), WORD),
    new BranchInstruction(BGE,
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                p_throw_runtime_error.toString())),

    new POPInstruction(PC.getRegister())
    };
  }

  private Instruction[] getFreeListCode(String label1, String label2) {
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label firstLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label1);
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label secondLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label2);

    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label
                (p_free_list.toString()),
    new PUSHInstruction(LR.getRegister()),

        /* Remembers the next node after the head node, before freeing the
           head node. PUSH and POP to prevent register corruption. */
    new LDRInstruction(r1.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)), WORD),
    new PUSHInstruction(r1.getRegister()),
    new BranchInstruction(BL, new uk.ac.ic.doc.wacc.code_generator.
            instructions.basics.Label("free")),
    new POPInstruction(r1.getRegister()),


    /* While loop branching. Jumps to condition */
    new BranchInstruction(BL, secondLabel),

        /* Body of while loop - Remember the next node and then free the
           current node stored inside of r1. */
    firstLabel,
    new MOVInstruction(r0.getRegister(), r1.getRegister()),
    new LDRInstruction(r1.getRegister(),
        OffsetRegister.preIndexedOffset(r1.getRegister(),
            new ConstantOffset(4)), WORD),
    new PUSHInstruction(r1.getRegister()),
    new BranchInstruction(BL, new uk.ac.ic.doc.wacc.code_generator.
            instructions.basics.Label("free")),
    new POPInstruction(r1.getRegister()),

        /* Condition: If the next node is null then we are complete, otherwise
           we loop. */
    secondLabel,
    new CMPInstruction(r1.getRegister(), new ConstantOffset(0)),
    new BranchInstruction(BNE, firstLabel),

    /* Pops the program counted off the stack */
    new POPInstruction(PC.getRegister())
    };
  }
  
  private Instruction[] getListContainsCode(String label1, String label2,
      String label3) {
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label firstLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label1);
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label secondLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label2);
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label thirdLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label3);

    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                p_list_contains.toString()),
    new PUSHInstruction(LR.getRegister()),

    /* r0 = address of list, r1 = data we want to check for */
    /* Standard while loop. Start by branching to condition*/
    new BranchInstruction(BL, secondLabel),

    /* Main Body Code */
    firstLabel,

    /* Load data stored at node into r2.*/
    new LDRInstruction(r2.getRegister(),
        OffsetRegister.noOffset(r0.getRegister()), WORD),

    /* Compare data to check it is the data we were looking for */
        new CMPInstruction(r1.getRegister(), r2.getRegister()),

    /* If it isn't then we want to continue */
    new BranchInstruction(BNE, thirdLabel),

    /* Else load true into r0 and return from the function */
    new LDRInstruction(r0.getRegister(),
        new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                Immediate(1), WORD),
    new POPInstruction(PC.getRegister()),

    thirdLabel,
    secondLabel,

    /* Condition for while loop. Keep getting next node until next node is
       null */
    new LDRInstruction(r0.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)),  WORD),
    new CMPInstruction(r0.getRegister(), new ConstantOffset(0)),
    new BranchInstruction(BNE, firstLabel),

   /* If we reach the end of the loop, we failed to find the value so return
      false */
    new LDRInstruction(r0.getRegister(),
        new uk.ac.ic.doc.wacc.code_generator.instructions.operands.
                Immediate(0), WORD),
    new POPInstruction(PC.getRegister())
    };
  }
  
  private Instruction[] getGetListElementCode(String label1, String label2) {
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label firstLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label1);
    uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label secondLabel =
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(label2);

    return new Instruction[] {
        new uk.ac.ic.doc.wacc.code_generator.instructions.basics.Label(
                p_get_list_element.toString()),
    new PUSHInstruction(LR.getRegister()),

    /* r0 is list address, r1 is (n - 1) (starting from 0) so we want nth
       node, hence add 1*/
    ADDInstruction.addWithNoFlags(r1.getRegister(),
        r1.getRegister(), new ConstantOffset(1)),

    /* Branch to condition of while loop */
    new BranchInstruction(BL, secondLabel),

    /* Main Body Code */
    firstLabel,

    /* Get the next node and decrement n by 1 to remember we have
       looped once */
    new LDRInstruction(r0.getRegister(),
        OffsetRegister.preIndexedOffset(r0.getRegister(),
            new ConstantOffset(4)), WORD),
    SUBInstruction.subWithNoFlags(r1.getRegister(),
        r1.getRegister(), new ConstantOffset(1)),

    /* Condition for loop, keep looping until we have looped n times */
    secondLabel,
    new CMPInstruction(r1.getRegister(),
        new ConstantOffset(0)),
    new BranchInstruction(BNE, firstLabel),

    /* At the end of the loop, r0 contains the address of the nth node's
       data section.*/
    new POPInstruction(PC.getRegister()),  
    };
  }

  @Override
  public Instruction[] getCode(UtilMethodLabel utilMethodLabel,
                               String... msgLabels) {
    /* Used to call the correct get method for generating ARM code for
       util methods */
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
}
