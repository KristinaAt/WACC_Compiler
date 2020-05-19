package uk.ac.ic.doc.wacc.code_generator.instructions.utils;

import uk.ac.ic.doc.wacc.code_generator.instructions.ARMInstruction;

import java.util.ArrayList;
import java.util.List;

/* Abstraction of the utils methods for printing, reading and
   checking for runtime errors */
public abstract class AbstractUtilMethod {

    protected final List<ARMInstruction> instructions;

    public AbstractUtilMethod() {
        this.instructions = new ArrayList<>();
    }

    @Override
    public String toString() {
        String result = instructions.get(0).toString() + "\n";
        for(int i = 1; i < instructions.size(); i++) {
            result += "\t" + instructions.get(i).toString() + "\n";
        }
        return result;
    }
}
