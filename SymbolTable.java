import java.util.*;
public class SymbolTable {
    private final Deque<Map<String, Type>> scopes = new ArrayDeque<>();

    public SymbolTable() {
        enterScope();
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        scopes.pop();
    }

    public boolean declare(String name, Type type) {
        Map<String, Type> current = scopes.peek();
        if (current.containsKey(name)) return false;
        current.put(name, type);
        return true;
    }

    public Type lookup(String name) {
        for (Map<String, Type> scope : scopes) {
            if (scope.containsKey(name)) return scope.get(name);
        }
        return null;
    }
}