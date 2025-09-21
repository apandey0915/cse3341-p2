import java.io.*;
public class DumpTokens {
  public static void main(String[] args) throws Exception {
    if (args.length != 1) { System.out.println("usage: DumpTokens <file>"); return; }
    CoreScanner sc = new CoreScanner(args[0]);
    int i = 0;
    while (true) {
      Core t = sc.currentToken();
      System.out.print((i++) + ": " + t.name());
      if (t == Core.ID) System.out.print(" id=" + sc.getId());
      if (t == Core.CONST) System.out.print(" const=" + sc.getConst());
      if (t == Core.STRING) System.out.print(" str=\"" + sc.getString() + "\"");
      System.out.println();
      if (t == Core.EOS || t == Core.ERROR) break;
      sc.nextToken();
    }
  }
}
