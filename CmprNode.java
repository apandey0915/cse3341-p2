import java.io.*;
public class CmprNode {
    private ExprNode left, right;
    private Core op; // LESS ('<'), EQUAL ('=='), or ASSIGN ('=')

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        left = new ExprNode();
        left.parse(scanner);

        Core t = scanner.currentToken();
        if (t != Core.LESS && t != Core.EQUAL && t != Core.ASSIGN) {
            throw new ParserException("expected '=', '==' or '<' in comparison");
        }
        op = t;
        scanner.nextToken();

        right = new ExprNode();
        right.parse(scanner);
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        left.checkSemantics(symbols);
        right.checkSemantics(symbols);
    }

    public void print(int indent) {
        left.print(indent);
        if (op == Core.LESS) {
            System.out.print(" < ");
        } else if (op == Core.EQUAL) {
            System.out.print(" == ");
        } else { 
            System.out.print(" = ");
        }
        right.print(0);
    }
}
