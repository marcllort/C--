package Lexer;


import Utils.Constants;

import java.util.regex.Pattern;

public enum Keywords {

    MAIN(Constants.MAIN_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.MAIN_TYPE, input, line, pos);
        }
    },
    INT_TYPE(Constants.INTEGER_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.INTEGER_TYPE, input, line, pos);
        }
    },
    NUMBER(Constants.NUMBER_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.NUMBER_TYPE, input, line, pos);
        }
    },
    ASSIGN(Constants.OP_ASSIGN_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OP_ASSIGN_TYPE, input, line, pos);
        }
    },
    EQUALS(Constants.OP_EQUAL_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OP_EQUAL_TYPE, input, line, pos);
        }
    },
    NOT_EQUALS(Constants.OP_NO_EQUAL_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OP_NO_EQUAL_TYPE, input, line, pos);
        }
    },
    GREATER(Constants.OP_GREATER_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OP_GREATER_TYPE, input, line, pos);
        }
    },

    LOWER(Constants.OP_LOWER_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OP_LOWER_TYPE, input, line, pos);
        }
    },

    DELIMITER(Constants.DELIMITER_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.DELIMITER_TYPE, input, line, pos);
        }
    },
    ADDITION(Constants.OP_ADD_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OP_ADD_TYPE, input, line, pos);
        }
    },
    SUBSTRACTION(Constants.OP_SUBTRACT_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OP_SUBTRACT_TYPE, input, line, pos);
        }
    },
    IF(Constants.IF_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.IF_TYPE, input, line, pos);
        }
    },

    WHILE(Constants.WHILE_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.WHILE_TYPE, input, line, pos);
        }
    },

    OPEN_PARENT(Constants.OPEN_PARENT_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OPEN_PARENT_TYPE, input, line, pos);
        }
    },
    CLOSE_PARENT(Constants.CLOSE_PARENT_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.CLOSE_PARENT_TYPE, input, line, pos);
        }
    },
    OPEN_CURLY(Constants.OPEN_CURLY_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.OPEN_CURLY_TYPE, input, line, pos);
        }
    },

    CLOSE_CURLY(Constants.CLOSE_CURLY_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.CLOSE_CURLY_TYPE, input, line, pos);
        }
    },

    EOF(Constants.EOF_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.EOF_TYPE, input, line, pos);
        }
    },
    UNDEF(Constants.UNDEF_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.UNDEF_TYPE, input, line, pos);
        }
    },
    STRING(Constants.STRING_VALUE_PATTERN) {
        public Token generate(String input, int line, int pos) {
            return new Token(Constants.STRING_VALUE_TYPE, input, line, pos);
        }
    },
    ;

    protected Pattern pattern;

    Keywords(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public abstract Token generate(String input, int line, int pos);

    public static Token checkKeyword(String input, int line, int pos) {
        for (Keywords kw : Keywords.values()) {
            if (kw.pattern.matcher(input).matches()) {
                return kw.generate(input, line, pos);
            }
        }
        return UNDEF.generate(input, line, pos);
    }
}