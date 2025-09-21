import java.io.*;
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            // All messages to go to stdout
            System.out.println("ERROR: missing input filename");
            System.exit(1);
        }

        String filename = args[0];
        try {
            // Scanner
            CoreScanner scanner = new CoreScanner(filename);

            // Parser builds parse tree (AST)
            Parser parser = new Parser(scanner);
            ProgramNode root = parser.parseProgram();

            // Semantic checks
            SymbolTable symbols = new SymbolTable();
            root.checkSemantics(symbols);

            // Pretty print
            root.print(0);

        } catch (ParserException e) {
            System.out.println("ERROR: " + e.getMessage());
            System.exit(1);
        } catch (SemanticException e) {
            System.out.println("ERROR: " + e.getMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: file not found");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("ERROR: I/O error");
            System.exit(1);
        } catch (RuntimeException e) { // safety net
            System.out.println("ERROR: unexpected failure");
            System.exit(1);
        }
    }
}
