import java.io.*;
public class DeclIntegerNode {
    private String name;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.INTEGER) {
            throw new ParserException("expected integer");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.ID) {
            throw new ParserException("expected ID after integer");
        }
        name = scanner.getId();
        scanner.nextToken();

        // Expect ';' — adjust constant name if your Core.java uses a different token for semicolon
        if (scanner.currentToken() != Core.SEMICOLON) {
            throw new ParserException("expected ';' after declaration");
        }
        scanner.nextToken();
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        // duplicate in same scope?
        boolean ok = symbols.declare(name, Type.INTEGER);
        if (!ok) throw new SemanticException("duplicate declaration " + name);
    }

    public void print(int indent) {
        indent(indent);
        System.out.println("integer " + name + ";");
    }

    private static void indent(int n) { for (int i = 0; i < n; i++) System.out.print(" "); }
}
