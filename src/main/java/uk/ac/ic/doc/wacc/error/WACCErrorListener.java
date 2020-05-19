package uk.ac.ic.doc.wacc.error;

import java.util.List;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.BitSet;

import static uk.ac.ic.doc.wacc.error.CompileError.Type.SYNTAX;

public class WACCErrorListener implements ANTLRErrorListener {

  private final List<CompileError> errorList;

  public WACCErrorListener(List<CompileError> errorList) {
    this.errorList = errorList;
  }

  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object o, int lineNum,
      int columnNum, String errorMessage, RecognitionException e) {
    if (e instanceof FailedPredicateException) {
      FailedPredicateException failedPredicateException =
          (FailedPredicateException) e;
      String predicate = failedPredicateException.getPredicate();

      if (predicate != null && predicate.contains("isIntParsable")) {
        errorMessage = "Badly formatted (either it has a badly defined sign or "
            + "it is too large for a 32-bit signed integer)";
      }
    }

    new CompileError(errorList, SYNTAX, lineNum, columnNum, errorMessage);
  }

  @Override
  public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1,
      boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {

  }

  @Override
  public void reportAttemptingFullContext(Parser parser, DFA dfa, int i,
      int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {

  }

  @Override
  public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1,
      int i2, ATNConfigSet atnConfigSet) {

  }
}
