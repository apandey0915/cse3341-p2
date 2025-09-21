import java.io.*;
public class StmtNode {
    private ReadNode readStmt;
    private DeclNode declStmt;
    private PrintNode printStmt;
    private AssignNode assignStmt;
    private IfNode ifStmt;
    private LoopNode loopStmt;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        Core t = scanner.currentToken();

        if (t == Core.READ) {
            readStmt = new ReadNode();
            readStmt.parse(scanner);
            return;
        }

        if (t == Core.INTEGER || t == Core.OBJECT) {
            declStmt = new DeclNode();
            declStmt.parse(scanner);
            return;
        }

        if (t == Core.PRINT) {
            printStmt = new PrintNode();
            printStmt.parse(scanner);
            return;
        }

        if (t == Core.ID) {
            assignStmt = new AssignNode();
            assignStmt.parse(scanner);
            return;
        }

        if (t == Core.IF) {
            ifStmt = new IfNode();
            ifStmt.parse(scanner);
            return;
        }

        if (t == Core.FOR) {
            loopStmt = new LoopNode();
            loopStmt.parse(scanner);
            return;
        }

        throw new ParserException("unsupported statement: " + t.name());
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        if (readStmt != null) readStmt.checkSemantics(symbols);
        if (declStmt != null) declStmt.checkSemantics(symbols);
        if (printStmt != null) printStmt.checkSemantics(symbols);
        if (assignStmt != null) assignStmt.checkSemantics(symbols);
        if (ifStmt != null)   ifStmt.checkSemantics(symbols);
        if (loopStmt != null) loopStmt.checkSemantics(symbols);
    }

    public void print(int indent) {
        if (readStmt != null) { readStmt.print(indent); return; }
        if (declStmt != null) { declStmt.print(indent); return; }
        if (printStmt != null) { printStmt.print(indent); return; }
        if (assignStmt != null) { assignStmt.print(indent); return; }
        if (ifStmt != null)   { ifStmt.print(indent);   return; }
        if (loopStmt != null) { loopStmt.print(indent); return; }
    }
}