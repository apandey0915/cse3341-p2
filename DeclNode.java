import java.io.*;
public class DeclNode {
    private DeclIntegerNode intDecl;
    private DeclObjNode objDecl;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        Core t = scanner.currentToken();
        if (t == Core.INTEGER) {
            intDecl = new DeclIntegerNode();
            intDecl.parse(scanner);
        } else if (t == Core.OBJECT) {
            objDecl = new DeclObjNode();
            objDecl.parse(scanner);
        } else {
            throw new ParserException("expected declaration");
        }
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        if (intDecl != null) intDecl.checkSemantics(symbols);
        if (objDecl != null) objDecl.checkSemantics(symbols);
    }

    public void print(int indent) {
        if (intDecl != null) intDecl.print(indent);
        if (objDecl != null) objDecl.print(indent);
    }
}
