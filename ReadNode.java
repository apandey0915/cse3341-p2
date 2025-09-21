import java.io.*;
public class ReadNode {
    private String name;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.READ) {
            throw new ParserException("expected read");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.LPAREN) {
            throw new ParserException("expected '(' after read");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.ID) {
            throw new ParserException("expected ID in read()");
        }
        name = scanner.getId();
        scanner.nextToken();

        if (scanner.currentToken() != Core.RPAREN) {
            throw new ParserException("expected ')'");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.SEMICOLON) {
            throw new ParserException("expected ';' after read(...)");
        }
        scanner.nextToken();
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        Type t = symbols.lookup(name);
        if (t == null) throw new SemanticException("undeclared id " + name);
    }

    public void print(int indent) {
        indent(indent);
        System.out.println("read(" + name + ");");
    }

    private static void indent(int n) {
        for (int i = 0; i < n; i++) System.out.print(" ");
    }
}