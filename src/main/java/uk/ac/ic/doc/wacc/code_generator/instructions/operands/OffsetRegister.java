package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

/* Offset register has the ability to create pre or post indexed registers.
   Pre-Indexed = [reg, #offset]
   Post-Indexed = [reg], #offset

   Also has the ability for save back for pre-index offset registers. This
   is [reg, #offset], used for growing the stack along with the movement e.g.
   [sp, #-4]! will grow the stack by 4 at the same time as the encapsulating
   instruction.

   A non off-setted register can also be created
 */
public class OffsetRegister implements Operand {
  private final Register register;
  private final Offset offset;
  private final boolean preIndexed;
  private final boolean saveBack;

  public static OffsetRegister noOffset(Register register) {
    return new OffsetRegister(register);
  }

  public static OffsetRegister preIndexedOffset(Register register,
   Offset offset) {
    return preIndexedOffset(register, offset, false);
  }

  public static OffsetRegister preIndexedOffset(Register register, Offset offset,
                                                boolean saveBack) {
    return new OffsetRegister(register, offset, true, saveBack);
  }

  public static OffsetRegister postIndexedOffset(Register register,
   Offset offset) {
    return new OffsetRegister(register, offset, false, false);
  }

  private OffsetRegister(Register register) {
    this(register, null, true, false);
  }

  private OffsetRegister(Register register, Offset offset, boolean preIndexed,
                         boolean saveBack) {
    this.register = register;
    this.offset = offset;
    this.preIndexed = preIndexed;
    this.saveBack = saveBack;
  }

  @Override
  public String toString() {
    if (offset == null || offset.isNull()) {
      return "[" + register + "]";
    } else if (preIndexed) {
      return "[" + register + ", " + offset + "]" + (saveBack ? "!" : "");
    } else {
      return "[" + register + "], " + offset;
    }
  }
}
