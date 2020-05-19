package uk.ac.ic.doc.wacc.code_generator.instructions;

import java.io.FileWriter;
import java.io.IOException;

public interface Program {
  void writeToFile(FileWriter fileWriter) throws IOException;
}
