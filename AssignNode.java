import java.io.*;
public class AssignNode {
    private enum Kind { ID_EQ_EXPR, INDEXED_EQ_EXPR, NEW_OBJECT, COLON_COPY }
    private Kind kind;

    // common
    private String leftId;
    private ExprNode rhsExpr;

    // indexed form
    private String indexKey;

    // new object form
    private String ctorKey;
    private ExprNode ctorExpr;

    // colon copy form
    private String rightId;

    public void parse(CoreScanner scanner) throws ParserException, IOException {
        if (scanner.currentToken() != Core.ID) {
            throw new ParserException("expected ID at start of assignment");
        }
        leftId = scanner.getId();
        scanner.nextToken();

        Core t = scanner.currentToken();

        if (t == Core.LSQUARE) {
            scanner.nextToken();

            if (scanner.currentToken() != Core.STRING) {
                throw new ParserException("expected string inside []");
            }
            indexKey = scanner.getString();
            scanner.nextToken();

            if (scanner.currentToken() != Core.RSQUARE) {
                throw new ParserException("expected ']'");
            }
            scanner.nextToken();

            if (scanner.currentToken() != Core.ASSIGN) {
                throw new ParserException("expected '=' after id[string]");
            }
            scanner.nextToken();

            rhsExpr = new ExprNode();
            rhsExpr.parse(scanner);

            if (scanner.currentToken() != Core.SEMICOLON) {
                throw new ParserException("expected ';' after assignment");
            }
            scanner.nextToken();

            kind = Kind.INDEXED_EQ_EXPR;
            return;
        }

        if (t == Core.COLON) {
            scanner.nextToken();

            if (scanner.currentToken() != Core.ID) {
                throw new ParserException("expected ID after ':'");
            }
            rightId = scanner.getId();
            scanner.nextToken();

            if (scanner.currentToken() != Core.SEMICOLON) {
                throw new ParserException("expected ';' after id : id");
            }
            scanner.nextToken();

            kind = Kind.COLON_COPY;
            return;
        }

        if (t != Core.ASSIGN) {
            throw new ParserException("expected '=' or ':' in assignment");
        }
        scanner.nextToken();

        if (scanner.currentToken() == Core.NEW) {
            scanner.nextToken();

            if (scanner.currentToken() != Core.OBJECT) {
                throw new ParserException("expected object after new");
            }
            scanner.nextToken();

            if (scanner.currentToken() != Core.LPAREN) {
                throw new ParserException("expected '(' after new object");
            }
            scanner.nextToken();

            if (scanner.currentToken() != Core.STRING) {
                throw new ParserException("expected string in new object");
            }
            ctorKey = scanner.getString();
            scanner.nextToken();

            if (scanner.currentToken() != Core.COMMA) {
                throw new ParserException("expected ',' in new object");
            }
            scanner.nextToken();

            ctorExpr = new ExprNode();
            ctorExpr.parse(scanner);

            if (scanner.currentToken() != Core.RPAREN) {
                throw new ParserException("expected ')'");
            }
            scanner.nextToken();

            if (scanner.currentToken() != Core.SEMICOLON) {
                throw new ParserException("expected ';' after new object(...)");
            }
            scanner.nextToken();

            kind = Kind.NEW_OBJECT;
            return;
        }

        rhsExpr = new ExprNode();
        rhsExpr.parse(scanner);

        if (scanner.currentToken() != Core.SEMICOLON) {
            throw new ParserException("expected ';' after assignment");
        }
        scanner.nextToken();

        kind = Kind.ID_EQ_EXPR;
    }

    public void checkSemantics(SymbolTable symbols) throws SemanticException {
        Type leftType = symbols.lookup(leftId);
        if (leftType == null) throw new SemanticException("undeclared id " + leftId);

        switch (kind) {
            case ID_EQ_EXPR:
                rhsExpr.checkSemantics(symbols);
                break;

            case INDEXED_EQ_EXPR:
                if (leftType != Type.OBJECT) throw new SemanticException(leftId + " must be object");
                rhsExpr.checkSemantics(symbols);
                break;

            case NEW_OBJECT:
                if (leftType != Type.OBJECT) throw new SemanticException(leftId + " must be object");
                ctorExpr.checkSemantics(symbols);
                break;

            case COLON_COPY:
                Type rightType = symbols.lookup(rightId);
                if (rightType == null) throw new SemanticException("undeclared id " + rightId);
                if (leftType != Type.OBJECT || rightType != Type.OBJECT) {
                    throw new SemanticException("id : id requires object types");
                }
                break;
        }
    }

    public void print(int indent) {
        indent(indent);
        switch (kind) {
            case ID_EQ_EXPR:
                System.out.print(leftId + " = ");
                rhsExpr.print(0);
                System.out.println(";");
                break;
            case INDEXED_EQ_EXPR:
                System.out.print(leftId + "['" + indexKey + "'] = ");
                rhsExpr.print(0);
                System.out.println(";");
                break;
            case NEW_OBJECT:
                System.out.print(leftId + " = new object('" + ctorKey + "', ");
                ctorExpr.print(0);
                System.out.println(");");
                break;
            case COLON_COPY:
                System.out.println(leftId + " : " + rightId + ";");
                break;
        }
    }

    // Print the assignment on one line with NO trailing semicolon/newline (for for-loop headers)
    public void printInline() {
        switch (kind) {
            case ID_EQ_EXPR:
                System.out.print(leftId + " = ");
                rhsExpr.print(0);
                break;
            case INDEXED_EQ_EXPR:
                System.out.print(leftId + "['" + indexKey + "'] = ");
                rhsExpr.print(0);
                break;
            case NEW_OBJECT:
                System.out.print(leftId + " = new object('" + ctorKey + "', ");
                ctorExpr.print(0);
                System.out.print(")");
                break;
            case COLON_COPY:
                System.out.print(leftId + " : " + rightId);
                break;
        }
    }


    private static void indent(int n) { for (int i = 0; i < n; i++) System.out.print(" "); }
}
