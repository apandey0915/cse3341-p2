import java.io.*;
public class ExprNode {
    private TermNode term;
    private Core op;
    private ExprNode rhs;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        term = new TermNode();
        term.parse(scanner);

        Core t = scanner.currentToken();
        if (t == Core.ADD || t == Core.SUBTRACT) {
            op = t;
            scanner.nextToken();
            rhs = new ExprNode();
            rhs.parse(scanner);
        }
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        term.checkSemantics(symbols);
        if (rhs != null) rhs.checkSemantics(symbols);
    }

    public void print(int indent) {
        term.print(indent);
        if (rhs != null) {
            System.out.print(op == Core.ADD ? " + " : " - ");
            rhs.print(0);
        }
    }
}
