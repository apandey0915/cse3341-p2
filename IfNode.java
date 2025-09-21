import java.io.*;
public class IfNode {
    private CondNode cond;
    private StmtSeqNode thenPart;
    private StmtSeqNode elsePart; // optional

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.IF) throw new ParserException("expected if");
        scanner.nextToken();

        // accept '[', '(', or bare condition
        Core look = scanner.currentToken();
        if (look == Core.LSQUARE || look == Core.LPAREN) {
            Core close = (look == Core.LSQUARE) ? Core.RSQUARE : Core.RPAREN;
            scanner.nextToken();

            cond = new CondNode();
            cond.parse(scanner);

            if (scanner.currentToken() != close) throw new ParserException("expected closing bracket/paren");
            scanner.nextToken();
        } else if (startsCondToken(look)) {
            cond = new CondNode();
            cond.parse(scanner);
        } else {
            throw new ParserException("expected '[' or '(' or condition after if");
        }

        if (scanner.currentToken() != Core.THEN) throw new ParserException("expected then");
        scanner.nextToken();

        if (!startsStmt(scanner.currentToken())) throw new ParserException("expected statement after then");
        thenPart = new StmtSeqNode();
        thenPart.parse(scanner);

        if (scanner.currentToken() == Core.ELSE) {
            scanner.nextToken();
            if (!startsStmt(scanner.currentToken())) throw new ParserException("expected statement after else");
            elsePart = new StmtSeqNode();
            elsePart.parse(scanner);
        }

        if (scanner.currentToken() != Core.END) throw new ParserException("expected end");
        scanner.nextToken();
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        cond.checkSemantics(symbols);

        symbols.enterScope();
        thenPart.checkSemantics(symbols);
        symbols.exitScope();

        if (elsePart != null) {
            symbols.enterScope();
            elsePart.checkSemantics(symbols);
            symbols.exitScope();
        }
    }

    public void print(int indent) {
        indent(indent); System.out.print("if [");
        cond.print(0);
        System.out.println("] then");
        thenPart.print(indent + 2);
        if (elsePart != null) {
            indent(indent); System.out.println("else");
            elsePart.print(indent + 2);
        }
        indent(indent); System.out.println("end");
    }

    private static boolean startsStmt(Core tok) {
        return tok == Core.ID || tok == Core.IF || tok == Core.FOR || tok == Core.PRINT
            || tok == Core.READ || tok == Core.INTEGER || tok == Core.OBJECT;
    }

    // tokens that can start a *condition* via <cmpr> or nested cond:
    private static boolean startsCondToken(Core tok) {
        // NOT <cond> | ( <cond> ... ) | <cmpr> which begins with an <expr>
        // <expr> can start with ID, CONST, LPAREN, ADD (unary +), SUBTRACT (unary -)
        return tok == Core.NOT || tok == Core.LPAREN || tok == Core.ID
            || tok == Core.CONST || tok == Core.ADD || tok == Core.SUBTRACT;
    }

    private static void indent(int n) { for (int i = 0; i < n; i++) System.out.print(" "); }
}
