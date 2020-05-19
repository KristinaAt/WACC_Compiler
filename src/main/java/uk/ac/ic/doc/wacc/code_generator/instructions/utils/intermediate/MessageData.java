package uk.ac.ic.doc.wacc.code_generator.instructions.utils.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;
import uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate.Label;

/* Message data class stores the information about each message
   at the beginning of the assembly program */
public class MessageData extends IntermediateInstruction {
  private final Label label;
  private final String message;
  private final int size;

  public MessageData(Label label, String message) {
    this.label = label;
    this.message = message;
    this.size = getSize(message);
  }

  public Label getLabel() {
    return label;
  }

  public String getMessage() {
    return message;
  }

  private static int getSize(String message) {
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
}
