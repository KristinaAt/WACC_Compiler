package uk.ac.ic.doc.wacc.code_generator.instructions.utils.x86_64;

import uk.ac.ic.doc.wacc.code_generator.instructions.basics.x86_64.Label;
import uk.ac.ic.doc.wacc.code_generator.instructions.x86Instruction;

public class MessageData extends x86Instruction {
  private final Label label;
  private final String message;
  private final int size;

  public MessageData(Label label, String message) {
    this.label = label;
    this.message = message;
    this.size = getSize(message);
  }

  private int getSize(String message) {
    int size = 0;
    /* Removes the quotation marks */
    for(int i = 1; i < message.length() - 1; i++) {
      size++;
      if(message.charAt(i) == '\\') {
        i++;
      }
    }
    return size;
  }

  @Override
  public String toString() {
    return label + " .string " + message;
  }
}
