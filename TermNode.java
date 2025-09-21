import java.io.*;
public class TermNode {
    private FactorNode factor;
    private Core op;
    private TermNode rhs;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        factor = new FactorNode();
        factor.parse(scanner);

        Core t = scanner.currentToken();
        if (t == Core.MULTIPLY || t == Core.DIVIDE) {
            op = t;
            scanner.nextToken();
            rhs = new TermNode();
            rhs.parse(scanner);
        }
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        factor.checkSemantics(symbols);
        if (rhs != null) rhs.checkSemantics(symbols);
    }

    public void print(int indent) {
        factor.print(indent);
        if (rhs != null) {
            System.out.print(op == Core.MULTIPLY ? " * " : " / ");
            rhs.print(0);
        }
    }
}
