lexer grammar WACCLexer;

BEGIN: 'begin' ;
END: 'end' ;
EXIT: 'exit' ;
RETURN: 'return' ;

//SKIP name reserved
SKIP_TOK: 'skip' ;

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;

COMMA: ',' ;
IS: 'is' ;

//list related functions
LIST: 'list' ;
ADDTOLIST: 'addToList' ;
REMOVEFROMLIST: 'removeFromList' ;
GETFROMLIST: 'getFromList' ;
LISTCONTAINS: 'listContains' ;
LISTSIZE: 'listsize' ;
NEWLIST: 'newlist' ;
CALLFUNC: '.' ;

//built-in functions
READ: 'read' ;

PRINT: 'print' ;
PRINTLN: 'println' ;
FREE: 'free' ;

//if, while keywords
IF: 'if' ;
THEN: 'then' ;
ELSE: 'else' ;
FI: 'fi' ;

//pair type keywords
PAIR: 'pair' ;
NEWPAIR: 'newpair' ;
FST: 'fst' ;
SND: 'snd' ;
NULL: 'null' ;

CALL: 'call' ;

OPEN_SQUARE: '[' ;
CLOSE_SQUARE: ']' ;

//unary operators, for negation use MINUS
NOT: '!' ;
LEN: 'len' ;
ORD: 'ord' ;
CHR: 'chr' ;
BIT_NOT: '~' ;


WHILE: 'while' ;
DO: 'do' ;
DONE: 'done' ;

SEMICOLON: ';' ;

//Binary operators
AND: '&&' ;
OR: '||' ;
PLUS: '+' ;
MINUS: '-' ;
MULTIPLY: '*' ;
DIVIDE: '/' ;
MOD: '%' ;
GREATER_THAN: '>' ;
GREATER_EQ: '>=' ;
LESS_THAN: '<' ;
LESS_EQ: '<=' ;
EQ: '==' ;
NOT_EQ: '!=' ;
BIT_AND: '&' ;
BIT_OR: '|' ;
BIT_EOR: '^' ;
SHIFT_L: '<<' ;
SHIFT_R: '>>' ;

//base-types
VOID: 'void' ;
INT: 'int' ;
BOOL: 'bool' ;
CHAR: 'char' ;
STRING: 'string' ;

//bool literals
TRUE: 'true' ;
FALSE: 'false' ;

//assign token
ASSIGN: '=' ;

IDENT: [_a-zA-Z] [_a-zA-Z0-9]* ;

//numbers
fragment DIGIT: '0'..'9' ;

INTEGER: DIGIT+ ;

STRING_LITER: '"' CHARACTER* '"' ;
fragment ESCAPE_CHARACTER: ('0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\') ;
fragment CHARACTER: ~('\\' | '"' | '\'') | '\\' ESCAPE_CHARACTER ;

CHAR_LITER: '\'' CHARACTER '\'' ;

//Comments and Whitespaces that we want to ignore
fragment EOL: [\n\r] ;

COMMENT: '#' .*? EOL -> skip ;
NEW_LINE: '\n'+ -> skip ;
WHITESPACE: [ \n\t\r]+ -> skip ;

