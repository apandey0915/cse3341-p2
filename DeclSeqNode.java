import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class DeclSeqNode {
    private final List<DeclNode> decls = new ArrayList<>();

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        // first decl 
        DeclNode first = new DeclNode();
        first.parse(scanner);
        decls.add(first);

        // keep going while next token starts a decl
        while (scanner.currentToken() == Core.INTEGER || scanner.currentToken() == Core.OBJECT) {
            DeclNode more = new DeclNode();
            more.parse(scanner);
            decls.add(more);
        }
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        for (DeclNode d : decls) d.checkSemantics(symbols);
    }

    public void print(int indent) {
        for (DeclNode d : decls) d.print(indent);
    }
}
