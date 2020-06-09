package Utils;

public class Constants {

    public static final String MAIN_DEF = "main";
    public static final String MAIN_PATTERN = "main";
    public static final int MAIN_TYPE = 0;

    public static final String INTEGER_DEF = "int";
    public static final String INTEGER_PATTERN = "int";
    public static final int INTEGER_TYPE = 1;
    public static final int INTEGER_DEFAULT_VALUE = 0;

    public static final String char_DEF = "char";
    public static final String CHAR_PATTERN = "char";
    public static final int CHAR_TYPE = 2;

    public static final String BOOLEAN_DEF = "boolean";
    public static final String BOOLEAN_PATTERN = "boolean";
    public static final int BOOLEAN_TYPE = 3;

    public static final String EOF_DEF = "endOfFile";
    public static final String EOF_PATTERN = "eof";
    public static final int EOF_TYPE = 404;

    public static final String NUMBER_DEF = "number";
    public static final String NUMBER_PATTERN = "[0-9]+";
    public static final int NUMBER_TYPE = 11;

    public static final String IDENTIFIER_DEF = "identifier";
    public static final String IDENTIFIER_PATTERN = "[a-zA-Z0-9]+";
    public static final int IDENTIFIER_TYPE = 10;

    public static final String OP_ADD_DEF = "op_add";
    public static final String OP_ADD_PATTERN = "\\+";
    public static final int OP_ADD_TYPE = 20;

    public static final String OP_ASSIGN_DEF = "op_assign";
    public static final String OP_ASSIGN_PATTERN = "=";
    public static final Integer OP_ASSIGN_TYPE = 21;

    public static final String OP_SUBTRACT_DEF = "op_subtract";
    public static final String OP_SUBTRACT_PATTERN = "-";
    public static final int OP_SUBTRACT_TYPE = 22;



    public static final String OP_EQUAL_DEF = "op_equal";
    public static final String OP_EQUAL_PATTERN = "==";
    public static final int OP_EQUAL_TYPE = 100;

    public static final String OP_NO_EQUAL_DEF = "op_no_equal";
    public static final String OP_NO_EQUAL_PATTERN = "!=";
    public static final int OP_NO_EQUAL_TYPE = 102;


    public static final String OP_GREATER = "op_greater";
    public static final String OP_GREATER_PATTERN = ">";
    public static final int OP_GREATER_TYPE = 103;

    public static final String OP_LOWER = "op_lower";
    public static final String OP_LOWER_PATTERN = "<";
    public static final int OP_LOWER_TYPE = 104;

    public static final String IF_DEF = "if";
    public static final String IF_PATTERN = "if";
    public static final int IF_TYPE = 101;

    public static final String WHILE_DEF = "while";
    public static final String WHILE_PATTERN = "while";
    public static final int WHILE_TYPE = 102;

    public static final String DELIMITER_DEF = "delimiter";
    public static final String DELIMITER_PATTERN = ";";
    public static final int DELIMITER_TYPE = 200;


    public static final String OPEN_PARENT_DEF = "(";
    public static final String OPEN_PARENT_PATTERN = "\\(";
    public static final int OPEN_PARENT_TYPE = 204;

    public static final String CLOSE_PARENT_DEF = ")";
    public static final String CLOSE_PARENT_PATTERN = "\\)";
    public static final int CLOSE_PARENT_TYPE = 205;

    public static final String OPEN_CURLY_DEF = "{";
    public static final String OPEN_CURLY_PATTERN = "\\{";
    public static final int OPEN_CURLY_TYPE = 206;

    public static final String CLOSE_CURLY_DEF = "}";
    public static final String CLOSE_CURLY_PATTERN = "\\}";
    public static final int CLOSE_CURLY_TYPE = 207;


    public static final String UNDEF_DEF = "undef";
    public static final String UNDEF_PATTERN = "undef";
    public static final int UNDEF_TYPE = 400;

    public static final String STRING_VALUE_DEF = "string_value";
    public static final String STRING_VALUE_PATTERN = "[a-zA-Z0-9]+";
    public static final int STRING_VALUE_TYPE = 10;

    public static final String[] OPERATORS = new String[]{"-", "+", "/", "*", "(", ")", "{", "}", ";","=","<",">", "\n"};

    public static final int SCOPE_GLOBAL = 0;
    public static final int SCOPE_MAIN = 1;
}
