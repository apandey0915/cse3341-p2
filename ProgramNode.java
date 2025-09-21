import java.io.*;

public class ProgramNode {
    private ProcedureNode procedure;
    public void parse(CoreScanner scanner) throws ParserException, IOException {
        procedure = new ProcedureNode();
        procedure.parse(scanner);

        if (scanner.currentToken() == Core.PERIOD) {
            scanner.nextToken();
        }

        if (scanner.currentToken() != Core.EOS) {
            throw new ParserException("extra input after program end");
        }
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        if (procedure != null) {
            procedure.checkSemantics(symbols);
        }
    }

    public void print(int indent) {
        if (procedure != null) {
            procedure.print(indent);
        }
    }
}
