package IntermediateCode;

import Lexer.Keywords;

public class Operand {

    private String name;
    private int type;


    Operand(int type, String name){
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }
}
