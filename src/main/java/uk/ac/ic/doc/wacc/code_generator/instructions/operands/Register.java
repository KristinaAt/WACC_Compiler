package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

import java.util.ArrayList;
import java.util.List;

/* Enum for the general purpose registers. Registers r4-r11 are the usable
   registers. LR holds the return address, PC is the program counter and
   SP holds the stack pointer. */
public class Register extends BaseRegister {

    public enum Identifier {
        r0,
        r1,
        r2,
        r3,
        r4,
        r5,
        r6,
        r7,
        r8,
        r9,
        r10,
        r11,
        LR,
        PC,
        SP;

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
        List<Register> registers = List.of(
            Identifier.r4.getRegister(), Identifier.r5.getRegister(),
            Identifier.r6.getRegister(), Identifier.r7.getRegister(),
            Identifier.r8.getRegister(), Identifier.r9.getRegister(),
            Identifier.r10.getRegister(), Identifier.r11.getRegister()
        );

        return new ArrayList<>(registers);
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
