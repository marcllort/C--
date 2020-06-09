package Semantic.SymbolTable;

import Utils.Constants;
import java.util.HashMap;

public class SymbolTable {

    public static HashMap<String, Symbol> scopeGlobal = new HashMap<String, Symbol>();
    public static HashMap<String, Symbol> scopeMain = new HashMap<String, Symbol>();

    public static boolean insertSymbol(Symbol symbol, int scope) {
        boolean state = false;

        if (getSymbol(symbol.getName(), scope) == null) {
            switch (scope) {
                case Constants.SCOPE_GLOBAL:
                    scopeGlobal.put(symbol.getName(), symbol);
                    break;
                case Constants.SCOPE_MAIN:
                    scopeMain.put(symbol.getName(), symbol);
                    break;
                default:
                    break;
            }
            state = true;
        }

        return state;
    }

    public static Symbol getSymbol(String key, int scope) {
        Symbol symbol = null;
        switch (scope) {
            case Constants.SCOPE_GLOBAL:
                symbol = scopeGlobal.get(key);
                break;
            case Constants.SCOPE_MAIN:
                symbol = scopeMain.get(key);
            default:
                break;
        }
        return symbol;
    }

    public static boolean updateSymbol(String key, String value, int scope) {
        boolean state = false;
        Symbol symbol = getSymbol(key, scope);
        if (symbol != null) {
            Symbol s = new Symbol();
            s.setName(symbol.getName());
            s.setType(symbol.getType());
            s.setValue(value);
            scopeGlobal.put(key, s);
        }
        return state;
    }

    public static HashMap<String, Symbol> getScope(int scope) {
        switch (scope) {
            case Constants.SCOPE_GLOBAL:
                return scopeGlobal;
            case Constants.SCOPE_MAIN:
                return scopeMain;
            default:
                return null;
        }
    }

    public static void printScope(int scope) {
        switch (scope) {
            case Constants.SCOPE_GLOBAL:
                if (scopeGlobal.size() > 0) {
                    System.out.println("\t### Symbol Table (Scope Global)" + " Size: " + scopeGlobal.size());
                    for (Symbol symbol : scopeGlobal.values()) System.out.println("\t\t" + symbol);
                }
                break;
            case Constants.SCOPE_MAIN:
                if (scopeMain.size() > 0) {
                    System.out.println("\t### Symbol Table (Scope Main)" + " Size: " + scopeMain.size());
                    for (Symbol symbol : scopeMain.values()) System.out.println("\t\t" + symbol);
                }
                break;
            default:
        }
    }

    public static void printSymbolTable() {
        System.out.println("\n### SYMBOL TABLE");
        printScope(Constants.SCOPE_GLOBAL);
        printScope(Constants.SCOPE_MAIN);
    }

}
