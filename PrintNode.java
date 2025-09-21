import java.io.*;
public class PrintNode {
    private ExprNode expr;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.PRINT) {
            throw new ParserException("expected print");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.LPAREN) {
            throw new ParserException("expected '(' after print");
        }
        scanner.nextToken();

        expr = new ExprNode();
        expr.parse(scanner);

        if (scanner.currentToken() != Core.RPAREN) {
            throw new ParserException("expected ')'");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.SEMICOLON) {
            throw new ParserException("expected ';' after print(...)");
        }
        scanner.nextToken();
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        expr.checkSemantics(symbols);
    }

    public void print(int indent) {
        indent(indent);
        System.out.print("print(");
        expr.print(0);
        System.out.println(");");
    }

    private static void indent(int n) { 
        for (int i = 0; i < n; i++) System.out.print(" "); 
    }
}
