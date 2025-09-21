import java.io.*;
public class DeclObjNode {
    private String name;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.OBJECT) {
            throw new ParserException("expected object");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.ID) {
            throw new ParserException("expected ID after object");
        }
        name = scanner.getId();
        scanner.nextToken();
        
        if (scanner.currentToken() != Core.SEMICOLON) {
            throw new ParserException("expected ';' after declaration");
        }
        scanner.nextToken();
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        boolean ok = symbols.declare(name, Type.OBJECT);
        if (!ok) throw new SemanticException("duplicate declaration " + name);
    }

    public void print(int indent) {
        indent(indent);
        System.out.println("object " + name + ";");
    }

    private static void indent(int n) { for (int i = 0; i < n; i++) System.out.print(" "); }
}
