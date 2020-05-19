package uk.ac.ic.doc.wacc.code_generator.instructions.basics.intermediate;

import uk.ac.ic.doc.wacc.code_generator.instructions.IntermediateInstruction;

/* Intermediate representation of a branch instruction. */
public class Branch extends IntermediateInstruction {

  /* Specifies under what condition we branch. */
  public enum Condition {
    NONE,
    EQUAL,
    OVERFLOW,
    SIGNED_LESS_THAN,
    UNSIGNED_GREATER_THAN_OR_EQUAL,
    NOT_EQUAL
  }

  private final String targetLabelName;
  private final Condition condition;
  private final boolean saveNextInstruction;

  public Branch(String targetLabelName, Condition condition,
                boolean saveNextInstruction) {
    this.targetLabelName = targetLabelName;
    this.condition = condition;
    this.saveNextInstruction = saveNextInstruction;
  }

  public String getTargetLabelName() {
    return targetLabelName;
  }

  public Condition getCondition() {
    return condition;
  }

  public boolean shouldNextInstructionBeSaved() {
    return saveNextInstruction;
  }
}
