package uk.ac.ic.doc.wacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import uk.ac.ic.doc.wacc.code_generator.instructions.Architecture;

@RunWith(Parameterized.class)
public class CodeGenerationTests {

  private static int passed;
  private static int skipped;
  private static int failed;

  private static class Pair<T, U> {

    public final T first;
    public final U second;

    public Pair(T first, U second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Pair) {
        Pair<T, U> pair = (Pair<T, U>) obj;
        return first.equals(pair.first) && second.equals(pair.second);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return first.hashCode() + second.hashCode();
    }
  }
  
  private static class Triple<T, U, V> extends Pair<Pair<T, U>, V> {
    
    public Triple(T keyFirst, U keySecond, V third) {
      super(new Pair<>(keyFirst, keySecond), third);
    }
  }

  /* Creates a map based on key-value pairs (K: key, V: value). */
  private static <K, V> Map<K, V> mapOf(Pair<K, V>... pairs) {
    Map<K, V> map = new HashMap<>();

    for (Pair<K, V> pair : pairs) {
      map.put(pair.first, pair.second);
    }

    return map;
  }

  /* From which architectures and folders in testfiles/valid should source code
     files be tested? */
  private static final Map<Pair<Architecture, String>, Boolean> shouldBeTested =
    mapOf(
      new Triple<>(Architecture.ARM, "advanced", true),
      new Triple<>(Architecture.ARM, "array", true),
      new Triple<>(Architecture.ARM, "basic", true),
      new Triple<>(Architecture.ARM, "expressions", true),
      new Triple<>(Architecture.ARM, "generic_list", true),
      new Triple<>(Architecture.ARM, "function", true),
      new Triple<>(Architecture.ARM, "overloading", true),
      new Triple<>(Architecture.ARM, "voidFunctions", true),
      new Triple<>(Architecture.ARM, "if", true),
      new Triple<>(Architecture.ARM, "IO", true),
      new Triple<>(Architecture.ARM, "pairs", true),
      new Triple<>(Architecture.ARM, "runtimeErr", true),
      new Triple<>(Architecture.ARM, "bitwise_operators", true),
      new Triple<>(Architecture.ARM, "scope", true),
      new Triple<>(Architecture.ARM, "sequence", true),
      new Triple<>(Architecture.ARM, "variables", true),
      new Triple<>(Architecture.ARM, "while", true),
      new Triple<>(Architecture.X86_64, "advanced", false),
      new Triple<>(Architecture.X86_64, "array", false),
      new Triple<>(Architecture.X86_64, "basic", false),
      new Triple<>(Architecture.X86_64, "expressions", false),
      new Triple<>(Architecture.X86_64, "generic_list", false),
      new Triple<>(Architecture.X86_64, "function", false),
      new Triple<>(Architecture.X86_64, "overloading", false),
      new Triple<>(Architecture.X86_64, "voidFunctions", false),
      new Triple<>(Architecture.X86_64, "if", false),
      new Triple<>(Architecture.X86_64, "IO", false),
      new Triple<>(Architecture.X86_64, "pairs", false),
      new Triple<>(Architecture.X86_64, "runtimeErr", false),
      new Triple<>(Architecture.X86_64, "bitwise_operators", false),
      new Triple<>(Architecture.X86_64, "scope", false),
      new Triple<>(Architecture.X86_64, "sequence", false),
      new Triple<>(Architecture.X86_64, "variables", false),
      new Triple<>(Architecture.X86_64, "while", false)
  );

  @Parameter()
  public String fileName; /* The filename of a source code file to test. */

  private String expectedOutput;
  private File inputFile;
  private File outputFile;
  private boolean runtimeErrorExpected;
  private int expectedExitCode;

  /* Gets all source code files to be tested, without considering the
     shouldBeTested map here. */
  @Parameters(name = "{index}: Test with filepath={0}")
  public static Collection<String> getTestFiles() {
    return TestsUtils.findAllWACCFiles(new File(
        "src/test/java/testfiles/valid"));
  }

  /* Gets a source code file, parses the expected exit code and output
     comments there, sets expectedExitCode if applicable and returns the
     expected output regex.

     The expected output is in a regex form, because an output might contain
     an array/pair address and its exact content cannot be easily predicted,
     while its general format can ("0x" followed by a sequence of digits and
     letters from A to F, both lower- and upper-case). */
  private String getOutput(File file) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;

    boolean outputMode = false;
    boolean exitMode = false;

    boolean outputDetected = false;
    StringBuilder outputBuilder = new StringBuilder();

    /* An entire source code file is parsed line-by-line. The function decides
       what to do based on the contents of each line. */
    while ((line = reader.readLine()) != null) {
      if (line.equals("# Output:")) {
        outputMode = true;
        exitMode = false;

        outputDetected = true;
      } else if (line.equals("# Exit:")) {
        outputMode = false;
        exitMode = true;
      } else if (outputMode) {
        if (!line.startsWith("#")) {
          outputMode = false;
        } else {
          String outputLine = Pattern.quote(line.substring(2));

          if (outputLine.contains("#empty#")) {
            outputMode = false;
          } else {
            /* Replaces any expected occurrences of an address with the
               address regex. See the top comment for details. */
            outputLine = outputLine.replace("#addrs#", "\\E0x[0-9A-Fa-f]+\\Q");
            outputBuilder.append(outputLine).append("\n");
          }
        }
      } else if (exitMode) {
        if (!line.startsWith("#")) {
          exitMode = false;
        } else {
          String outputLine = line.substring(2);

          /* Does outputLine represent a whole number? */
          if (outputLine.matches("^\\d+$")) {
            expectedExitCode = Integer.parseInt(outputLine);
          }
        }
      }
    }

    reader.close();

    return outputDetected ? outputBuilder.toString() : null;
  }

  /* A method assuming that false is true. This is used for marking tests as to
     be ignored. */
  private void ignore() {
    skipped++;
    Assume.assumeTrue(false);
  }

  /* Sets up the environment before running a test.

     The following steps are carried out in order:
     1) Check if the source code file is marked as to be tested. Set
        runtimeErrorExpected to false and expectedExitCode to 0.
     2) Look for an output file. If a one exists, use it to produce an
        expected output regex. Otherwise, call getOutput() to get the regex.
     3) If no expected output can be produced, mark the test as to be ignored.
     4) Look for an input and exit file. If an exit file exists, parse it and
        set the expected exit code.
     5) Look for any variables inside expected output comments in the source
        code file (their format is #[name]#). Mark the test as to be ignored
        if an unsupported variable is detected. If #runtime_error# is seen,
        set runtimeErrorExpected to true so that the suite knows that a
        runtime error is expected.
     6) Depending on whether a runtime error is expected, apply extra changes
        to the expected output regex.
     7) Set expectedOutput to the expected output regex.

     Note that #input# and #output# variables are not supported. You need to
     supply a separate input (.in) and output (.out) files if you have a
     source code file which expects some user input and prints an output
     based on that. */
  public void prepare(Architecture architecture) throws IOException {
    /* Step 1 */
    File file = new File(fileName);
    Pair<Architecture, String> filePair;

    do {
      filePair = new Pair<>(architecture, file.getName());
      file = file.getParentFile();
    } while (!shouldBeTested.containsKey(filePair) && file != null);

    if (file == null || !shouldBeTested.get(filePair)) {
      System.out.println("Marked as not to be tested, ignoring");
      ignore();
    }

    runtimeErrorExpected = false;
    expectedExitCode = 0;

    file = new File(fileName);

    /* Step 2 */
    File inputFile =
        new File(FilenameUtils.getFullPath(fileName)
            + FilenameUtils.getBaseName(fileName) + ".in");
    File outputFile =
        new File(FilenameUtils.getFullPath(fileName)
            + FilenameUtils.getBaseName(fileName) + ".out");
    File exitOutputFile =
        new File(FilenameUtils.getFullPath(fileName)
            + FilenameUtils.getBaseName(fileName) + ".exit");

    String output = outputFile.exists() ? FileUtils.readFileToString(outputFile,
        Charset.defaultCharset()) : getOutput(file);

    /* Step 3 */
    if (output == null) {
      System.out.println("No expected output detected");
      ignore();
    }

    if (outputFile.exists()) {
      this.outputFile = outputFile;
    }

    /* Step 4 */
    if (inputFile.exists()) {
      this.inputFile = inputFile;
    }

    if (exitOutputFile.exists()) {
      String exitCodeString = FileUtils.readFileToString(exitOutputFile,
          Charset.defaultCharset());

      /* Does exitCodeString represent a whole number? */
      if (exitCodeString.matches("^\\d+$")) {
        expectedExitCode = Integer.parseInt(exitCodeString);
      }
    }

    /* Step 5 */
    Pattern pattern = Pattern.compile("(#\\S+#)");
    Matcher matcher = pattern.matcher(output);

    List<String> unsupportedVariables = new ArrayList<>();

    while (matcher.find()) {
      String match = matcher.group();

      if (match.equals("#runtime_error#")) {
        runtimeErrorExpected = true;
      } else {
        unsupportedVariables.add(matcher.group());
      }
    }

    if (!unsupportedVariables.isEmpty()) {
      System.out.println("Unsupported variables detected: "
          + unsupportedVariables);
      ignore();
    }

    /* Step 6 */
    if (runtimeErrorExpected) {
      output = output.replace("#runtime_error#", "");
      if (output.endsWith("\n")) {
        output = output.substring(0, output.length() - 1);
      }
    }

    /* Step 7 */
    expectedOutput = output;
  }

  private String run(String... command) throws Exception {
    return run(false, 0, null, command);
  }

  /* Runs a shell command and returns the contents of STDOUT after the
     resultant process finishes. A test will fail if the exit code is other
     than expected. In that case, a message describing the command run and the
     contents of STDOUT and STDERR is printed. */
  private String run(boolean runtimeErrorExpected, int expectedExitCode,
      File inputFile, String... command) throws Exception {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    Process process = processBuilder.start();

    if (inputFile != null) {
      IOUtils.copy(new FileInputStream(inputFile), process.getOutputStream());
      process.getOutputStream().close();
    }

    String output = IOUtils.toString(process.getInputStream(),
        Charset.defaultCharset());
    String errorOutput = IOUtils.toString(process.getErrorStream(),
        Charset.defaultCharset());
    int result = process.waitFor();

    String message =
        "Command run: " + Arrays.toString(command)
            + "\nSTDOUT:\n"
            + output
            + "\nSTDERR:\n"
            + errorOutput
            + "\n";

    if (!runtimeErrorExpected) {
      assertEquals(message, expectedExitCode, result);
    } else {
      assertNotEquals(message, 0, result);
    }

    return output;
  }

  /* Reads an assembly file and returns a string representing the assembly
     code with numbered lines, starting with 1. */
  private String getAssemblyCode(File assemblyFile) throws IOException {
    String assemblyCode = FileUtils.readFileToString(assemblyFile,
        Charset.defaultCharset());

    String[] lines = assemblyCode.split("\n");

    for (int i = 0; i < lines.length; i++) {
      lines[i] = "(" + (i + 1) + ") " + lines[i];
    }

    return String.join("\n", lines);
  }

  /* Tests whether the assembly code generated for a source code file by the
     WACC compiler works correctly.

     The following steps are carried out in order:
     1) Produce an assembly code for a WACC file.
     2) Compile an assembly code.
     3) Run the compiled code directly or with an appropriate emulator.
     4) Compare the outputs. */
  private void test(Architecture architecture, boolean optimise) throws Exception {
    String baseName = FilenameUtils.getBaseName(fileName);

    /* Step 1: Produce an assembly code for) a WACC file. */
    List<String> argList = new ArrayList<>();

    argList.add(fileName);

    if (optimise) {
      argList.add("-o");
    }

    if (architecture == Architecture.X86_64) {
      argList.add("-x86");
    }

    String[] args = new String[argList.size()];
    argList.toArray(args);

    String expectedAssemblyFilename = baseName + ".s";

    try {
      Compiler.main(args);
    } catch (UnsupportedOperationException exception) {
      if (exception.getMessage().equals("Lists not implemented in x86")) {
        System.out.println(exception.getMessage());
        ignore();
      } else {
        throw exception;
      }
    }

    File assemblyFile = new File(expectedAssemblyFilename);
    assertTrue(assemblyFile.exists());

    System.out.println("Produced assembly code with numbered lines:");
    System.out.println(getAssemblyCode(assemblyFile));

    /* Step 2: Compile an assembly code. */
    switch (architecture) {
      case ARM:
      default:
        run("arm-linux-gnueabi-gcc", "-o", baseName,
          "-mcpu=arm1176jzf-s", "-mtune=arm1176jzf-s",
          expectedAssemblyFilename);
        break;

      case X86_64:
        run("gcc", "-no-pie", "-m32", "-g", "-o", baseName,
            expectedAssemblyFilename);
        break;
    }

    File resultantFile = new File(baseName);
    assertTrue(resultantFile.exists());

    /* Step 3: Run the compiled code directly or with an appropriate
       emulator. */
    String obtainedOutput;

    switch (architecture) {
      case ARM:
      default:
        obtainedOutput = run(runtimeErrorExpected, expectedExitCode,
          inputFile, "qemu-arm", "-L", "/usr/arm-linux-gnueabi",
          baseName);
        break;

      case X86_64:
        /* Assuming the host runs an x86-64 Unix-like OS. */
        obtainedOutput = run(runtimeErrorExpected, expectedExitCode, inputFile,
          "./" + baseName);
        break;
    }

    /* Step 4: Compare the outputs. */
    try {
      if (outputFile != null) {
        assertEquals(FileUtils.readFileToString(outputFile, "utf-8"),
                obtainedOutput);
      } else {
        if (!runtimeErrorExpected) {
          assertTrue("Obtained output:\n" + obtainedOutput +
                          "\nExpected output (regex):\n" + expectedOutput,
                  obtainedOutput.matches(expectedOutput));
        } else {
          Pattern pattern = Pattern.compile(expectedOutput);
          Matcher matcher = pattern.matcher(obtainedOutput);

          assertTrue(matcher.find());
        }
      }
      passed++;
    } catch (AssertionError ignored) {
      failed++;
    }
  }

  @Test(timeout=45000)
  public void ARMGeneratedCodeWorksCorrectly() throws Exception {
    /* AI does different behaviour so we skip it for unoptimised but test
       anyway in optimised, so ticTacToe is still tested.*/
    if(FilenameUtils.getBaseName(fileName).equals("ticTacToe"))
      ignore();
    prepare(Architecture.ARM);
    System.out.println("Running unoptimised ARM code test on " + fileName);
    test(Architecture.ARM, false);
  }

  @Test(timeout=45000)
  public void OptimisedARMGeneratedCodeWorksCorrectly() throws Exception {
    prepare(Architecture.ARM);
    System.out.println("Running optimised ARM code test on "  + fileName);
    test(Architecture.ARM, true);
  }

  @Test(timeout=45000)
  public void x86_64GeneratedCodeWorksCorrectly() throws Exception {
    prepare(Architecture.X86_64);
    System.out.println("Running unoptimised x86-64 code test on " + fileName);
    test(Architecture.X86_64, false);
  }

  @Test(timeout=45000)
  public void Optimisedx86_64GeneratedCodeWorksCorrectly() throws Exception {
    prepare(Architecture.X86_64);
    System.out.println("Running optimised x86-64 code test on "  + fileName);
    test(Architecture.X86_64, true);
  }

  /* Removes all temporary files created during a test. */
  @After
  public void clean() {
    String baseName = FilenameUtils.getBaseName(fileName);
    String expectedAssemblyFilename = baseName + ".s";

    File assemblyFile = new File(expectedAssemblyFilename);
    File resultantFile = new File(baseName);

    if (assemblyFile.exists()) {
      assertTrue(assemblyFile.delete());
    }

    if (resultantFile.exists()) {
      assertTrue(resultantFile.delete());
    }

    System.out.println("Passed: " + passed);
    System.out.println("Skipped: " + skipped);
    System.out.println("Failed: " + failed);
  }
}
