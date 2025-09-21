import java.io.*;

public class ProcedureNode {
    private String name;
    private DeclSeqNode decls;
    private StmtSeqNode stmts;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.PROCEDURE) {
            throw new ParserException("expected procedure");
        }
        scanner.nextToken();

        if (scanner.currentToken() != Core.ID) {
            throw new ParserException("expected ID after procedure");
        }
        name = scanner.getId();
        scanner.nextToken();

        if (scanner.currentToken() != Core.IS) {
            throw new ParserException("expected is");
        }
        scanner.nextToken();

        // check for decl-seq or directly begin
        if (scanner.currentToken() == Core.INTEGER || scanner.currentToken() == Core.OBJECT) {
            decls = new DeclSeqNode();
            decls.parse(scanner);
        }

        if (scanner.currentToken() != Core.BEGIN) {
            throw new ParserException("expected begin");
        }
        scanner.nextToken();

        Core la = scanner.currentToken();
        boolean starts =
            la == Core.ID || la == Core.IF || la == Core.FOR || la == Core.PRINT ||
            la == Core.READ || la == Core.INTEGER || la == Core.OBJECT;

        if (starts) {
            stmts = new StmtSeqNode();
            stmts.parse(scanner);
        } else {
            // empty block: stmts stays null
        }

        if (scanner.currentToken() != Core.END) {
            throw new ParserException("expected end");
        }
        scanner.nextToken();
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        symbols.enterScope();
        if (decls != null) decls.checkSemantics(symbols);
        if (stmts != null) stmts.checkSemantics(symbols);
        symbols.exitScope();
    }

    public void print(int indent) {
        indent(indent); 
        System.out.println("procedure " + name + " is");
        if (decls != null) decls.print(indent + 2);
        indent(indent); 
        System.out.println("begin");
        if (stmts != null) stmts.print(indent + 2);
        indent(indent); 
        System.out.println("end");
    }

    private void indent(int n) {
        for (int i = 0; i < n; i++) System.out.print(" ");
    }
}
