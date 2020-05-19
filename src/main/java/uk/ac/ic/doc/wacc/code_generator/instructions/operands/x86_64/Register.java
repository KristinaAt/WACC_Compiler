package uk.ac.ic.doc.wacc.code_generator.instructions.operands.x86_64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.ac.ic.doc.wacc.code_generator.instructions.operands.BaseRegister;

public class Register extends BaseRegister {

  public enum Identifier {
    eax,
    ebx,
    ecx,
    edx,
    esp,
    ebp,
    esi,
    edi,
    r8,
    r9,
    r10,
    r11,
    r12,
    r13,
    r14,
    r15;

    private final Register register;

    Identifier() {
      this.register = new Register(this);
    }

    public Register getRegister() {
      return register;
    }
  };

  private final Identifier identifier;

  private Register(Identifier identifier) {
    this.identifier = identifier;
  }

  public static List<Register> getUsableRegisters() {
    List<Register> registers = new ArrayList<>(Arrays.asList(
       Identifier.ecx.getRegister(),
       Identifier.edx.getRegister(),
       Identifier.ebp.getRegister(),
       Identifier.esi.getRegister(),
       Identifier.edi.getRegister(),
       Identifier.r8.getRegister(),
       Identifier.r9.getRegister(),
       Identifier.r10.getRegister(),
       Identifier.r11.getRegister(),
       Identifier.r12.getRegister(),
       Identifier.r13.getRegister(),
       Identifier.r14.getRegister(),
       Identifier.r15.getRegister()
    ));

    return registers;
  }

  @Override
  public int getIdentifier() {
    return identifier.ordinal();
  }

  @Override
  public String toString() {
    return identifier.toString();
  }
}
