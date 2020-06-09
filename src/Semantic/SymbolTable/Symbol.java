package Semantic.SymbolTable;

import Lexer.Token;

public class Symbol {

    private int type = -1;
    private String name;
    private String value;
    private int scope = -1;

    public Symbol(){
    }

    public Symbol(Token token){
        this.type = token.getType();
        this.name = token.getData();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getScope() {
        return scope;
    }

    public void setScope(int scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", scope=" + scope +
                '}';
    }
}
