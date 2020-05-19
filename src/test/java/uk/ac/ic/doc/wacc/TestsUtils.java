package uk.ac.ic.doc.wacc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestsUtils {

  public static Collection<String> findAllWACCFiles(File file) {
    assert (file != null);

    List<String> files = new ArrayList<>();
    if (file.isDirectory()) {
      File[] filesInside = file.listFiles();
      if (filesInside != null) {
        for (File subfile : filesInside) {
          files.addAll(findAllWACCFiles(subfile));
        }
      }
    } else {
      String filename = file.getAbsolutePath();
      if (filename.endsWith(".wacc")) {
        files.add(file.getAbsolutePath());
      }
    }
    return files;
  }
}
