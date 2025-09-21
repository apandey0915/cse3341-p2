import java.io.*;
public class IfNode {
    private enum Delim { SQUARE, PAREN, NONE }
    private CondNode cond;
    private StmtSeqNode thenPart;
    private StmtSeqNode elsePart;
    private Delim delim = Delim.NONE;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.IF) throw new ParserException("expected if");
        scanner.nextToken();

        // detect delimiter style
        Core look = scanner.currentToken();
        if (look == Core.LSQUARE) {
            delim = Delim.SQUARE;
            scanner.nextToken();
            cond = new CondNode();
            cond.parse(scanner);
            if (scanner.currentToken() != Core.RSQUARE) throw new ParserException("expected ']'");
            scanner.nextToken();
        } else if (look == Core.LPAREN) {
            delim = Delim.PAREN;
            scanner.nextToken();
            cond = new CondNode();
            cond.parse(scanner);
            if (scanner.currentToken() != Core.RPAREN) throw new ParserException("expected ')'");
            scanner.nextToken();
        } else {
            delim = Delim.NONE;
            cond = new CondNode();
            cond.parse(scanner);
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
        indent(indent);
        System.out.print("if ");
        if (delim == Delim.SQUARE) System.out.print("[");
        if (delim == Delim.PAREN)  System.out.print("(");
        cond.print(0);
        if (delim == Delim.SQUARE) System.out.print("]");
        if (delim == Delim.PAREN)  System.out.print(")");
        System.out.println(" then");

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

    private static void indent(int n) { for (int i = 0; i < n; i++) System.out.print(" "); }
}
