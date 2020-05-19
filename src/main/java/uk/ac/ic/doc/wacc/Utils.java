package uk.ac.ic.doc.wacc;

public class Utils {
  public static boolean isIntParsable(String sign, String integer) {
    if(sign == null) {
      sign = "";
    }
    /* Attempts to parse the integer */
    try {
      Integer.valueOf(sign + integer);
      return true;
    } catch (NumberFormatException e) {
      /* If unable to parse, then we have an overflow */
      return false;
    }
  }
}
