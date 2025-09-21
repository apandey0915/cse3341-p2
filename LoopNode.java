import java.io.*;
public class LoopNode {
    private AssignNode init;
    private CondNode  cond;
    private ExprNode  update;  

    private StmtSeqNode body;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.FOR) throw new ParserException("expected for");
        scanner.nextToken();

        Core look = scanner.currentToken();


        if (look == Core.LPAREN) {
            scanner.nextToken(); // consume '('

            if (scanner.currentToken() == Core.SEMICOLON) {
                scanner.nextToken();
            } else if (scanner.currentToken() == Core.ID) {
                init = new AssignNode();
                init.parse(scanner); 
            } else {
                throw new ParserException("expected assignment or ';' in for-init");
            }

            if (scanner.currentToken() == Core.SEMICOLON) {
                scanner.nextToken();
            } else {
                cond = new CondNode();
                cond.parse(scanner);
                if (scanner.currentToken() != Core.SEMICOLON) {
                    throw new ParserException("expected ';' after for-condition");
                }
                scanner.nextToken();
            }

            if (scanner.currentToken() == Core.RPAREN) {
                scanner.nextToken();
            } else {
                update = new ExprNode();
                update.parse(scanner);
                if (scanner.currentToken() != Core.RPAREN) {
                    throw new ParserException("expected ')' to close for-header");
                }
                scanner.nextToken();
            }

            if (scanner.currentToken() != Core.DO) throw new ParserException("expected do");
            scanner.nextToken();

            requireStmtAndParseBody(scanner);
            return;
        }

        if (look == Core.LSQUARE) {
            scanner.nextToken();
            cond = new CondNode();
            cond.parse(scanner);
            if (scanner.currentToken() != Core.RSQUARE) throw new ParserException("expected ']'");
            scanner.nextToken();
            if (scanner.currentToken() != Core.DO) throw new ParserException("expected do");
            scanner.nextToken();
            requireStmtAndParseBody(scanner);
            return;
        }

        if (startsCondToken(look)) {
            cond = new CondNode();
            cond.parse(scanner);
            if (scanner.currentToken() != Core.DO) throw new ParserException("expected do");
            scanner.nextToken();
            requireStmtAndParseBody(scanner);
            return;
        }

        throw new ParserException("expected '(' or '[' or condition after for");
    }

    private void requireStmtAndParseBody(CoreScanner scanner) throws ParserException, IOException {
        if (!startsStmt(scanner.currentToken())) {
            throw new ParserException("expected statement after do");
        }
        body = new StmtSeqNode();
        body.parse(scanner);
        if (scanner.currentToken() != Core.END) throw new ParserException("expected end");
        scanner.nextToken();
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        if (init != null)   init.checkSemantics(symbols);
        if (cond != null)   cond.checkSemantics(symbols);
        if (update != null) update.checkSemantics(symbols);
        symbols.enterScope();
        body.checkSemantics(symbols);
        symbols.exitScope();
    }

    public void print(int indent) {
        if (init != null || update != null) {
            // 3-part header form
            indent(indent); System.out.print("for (");
            if (init != null) init.printInline();
            System.out.print("; ");
            if (cond != null) cond.print(0);
            System.out.print("; ");
            if (update != null) update.print(0);
            System.out.println(") do");
        } else if (cond != null) {
            // single-cond form (normalize to brackets)
            indent(indent); System.out.print("for [");
            cond.print(0);
            System.out.println("] do");
        } else {
            indent(indent); System.out.println("for [] do");
        }

        body.print(indent + 2);
        indent(indent); System.out.println("end");
    }

    private static boolean startsStmt(Core tok) {
        return tok == Core.ID || tok == Core.IF || tok == Core.FOR || tok == Core.PRINT
            || tok == Core.READ || tok == Core.INTEGER || tok == Core.OBJECT;
    }

    private static boolean startsCondToken(Core tok) {
        return tok == Core.NOT || tok == Core.LPAREN || tok == Core.ID
            || tok == Core.CONST || tok == Core.ADD || tok == Core.SUBTRACT;
    }

    private static void indent(int n) { for (int i = 0; i < n; i++) System.out.print(" "); }
}
