package Lexer;

public class Token {

    private int id;
    private int type;
    private String data;
    private Object value;
    private int line, pos;

    public Token() {
    }

    public Token(int type, String data, int line, int pos) {
        this.type = type;
        this.data = data;
        this.line = line;
        this.pos = pos;
    }

    public Token(int id, int type, String data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }
}
