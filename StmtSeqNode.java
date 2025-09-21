import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class StmtSeqNode {
    private final List<StmtNode> stmts = new ArrayList<>();

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        StmtNode first = new StmtNode();
        first.parse(scanner);
        stmts.add(first);

        while (startsStmt(scanner.currentToken())) {
            StmtNode more = new StmtNode();
            more.parse(scanner);
            stmts.add(more);
        }
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        for (StmtNode s : stmts) {
            s.checkSemantics(symbols);
        }
    }

    public void print(int indent) {
        for (StmtNode s : stmts) {
            s.print(indent);
        }
    }

    // Recognize tokens that can *begin* a statement.
    private static boolean startsStmt(Core tok) {
        return tok == Core.ID
            || tok == Core.IF
            || tok == Core.FOR
            || tok == Core.PRINT
            || tok == Core.READ
            || tok == Core.INTEGER
            || tok == Core.OBJECT;
    }
}