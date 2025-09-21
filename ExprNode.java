import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class ExprNode {
    private static class OpTerm {
        Core op;
        TermNode term; 
        OpTerm(Core op, TermNode term) { this.op = op; this.term = term; }
    }

    private TermNode first;
    private final List<OpTerm> rest = new ArrayList<>();

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() == Core.RPAREN) {
            throw new ParserException("missing left parenthesis in expression");
        }

        first = new TermNode();
        parseTermOrParensError(first, scanner);

        while (scanner.currentToken() == Core.ADD || scanner.currentToken() == Core.SUBTRACT) {
            Core op = scanner.currentToken();
            scanner.nextToken();

            if (scanner.currentToken() == Core.RPAREN) {
                throw new ParserException("missing left parenthesis in expression");
            }

            TermNode t = new TermNode();
            parseTermOrParensError(t, scanner);
            rest.add(new OpTerm(op, t));
        }
    }
    
    private static void parseTermOrParensError(TermNode t, CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() == Core.RPAREN) {
            throw new ParserException("missing left parenthesis in expression");
        }
        t.parse(scanner);
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        first.checkSemantics(symbols);
        for (OpTerm ot : rest) ot.term.checkSemantics(symbols);
    }

    public void print(int indent) {
        first.print(indent);
        for (OpTerm ot : rest) {
            System.out.print(ot.op == Core.ADD ? " + " : " - ");
            ot.term.print(0);
        }
    }
}
