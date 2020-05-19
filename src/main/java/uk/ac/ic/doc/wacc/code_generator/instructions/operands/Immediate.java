package uk.ac.ic.doc.wacc.code_generator.instructions.operands;

/* Immediate class which holds an integer value */
public class Immediate implements Operand {
    private final int num;

    public Immediate(int num) {
        this.num = num;
    }

    public int getNumber() {
        return num;
    }

    @Override
    public String toString() {
        return "=" + num;
    }
}
