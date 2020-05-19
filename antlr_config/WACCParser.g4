parser grammar WACCParser;

@header{
import java.util.*;
import static uk.ac.ic.doc.wacc.error.CompileError.Type.SYNTAX;
import static uk.ac.ic.doc.wacc.Utils.isIntParsable;
import uk.ac.ic.doc.wacc.error.CompileError;
}

options {
  tokenVocab=WACCLexer;
}

voidFunc: VOID ident OPEN_PARENTHESES paramList? CLOSE_PARENTHESES IS stat END;

func: type ident OPEN_PARENTHESES paramList? CLOSE_PARENTHESES IS func_body END;

func_end: RETURN expr #FuncEndReturn
| EXIT expr           #FuncEndExit
;

func_body: func_end                                             #FuncEnd
|stat SEMICOLON func_end                                        #CompoundFuncEnd
|(stat SEMICOLON)? IF expr THEN func_body ELSE func_body FI     #IfStatementFuncEnd
;

paramList: param (COMMA param)*;

param: type ident;

stat: SKIP_TOK                                              #Skip
| type ident ASSIGN assignRhs                               #AssignNewVar
| assignLhs ASSIGN assignRhs                                #AssignVar
| READ assignRhs                                            #Read
| FREE expr                                                 #Free
| RETURN expr                                               #Return
| EXIT expr                                                 #Exit
| PRINT expr                                                #Print
| PRINTLN expr                                              #PrintLn
| IF expr THEN stat ELSE stat FI                            #If
| WHILE expr DO stat DONE                                   #While
| BEGIN stat END                                            #Begin
| CALL ident OPEN_PARENTHESES argList? CLOSE_PARENTHESES    #StatementCall
| genericListStat                                           #ListStat
| stat SEMICOLON stat                                       #Compound
;

genericListStat:
  ident CALLFUNC ADDTOLIST OPEN_PARENTHESES expr CLOSE_PARENTHESES       #AddToList
| ident CALLFUNC REMOVEFROMLIST OPEN_PARENTHESES expr CLOSE_PARENTHESES  #RemoveFromList
;

assignLhs: ident
| arrayElem
| pairElem
;

assignRhs: expr                                                   #AssignExpr
| arrayLiter                                                      #AssignArrayLiter
| NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES      #AssignNewPair
| NEWLIST OPEN_PARENTHESES CLOSE_PARENTHESES                      #AssignNewList
| pairElem                                                        #AssignPairElem
| CALL ident OPEN_PARENTHESES argList? CLOSE_PARENTHESES          #AssignCall
;

argList: expr (COMMA expr)* ;

pairElem: FST expr
| SND expr
;

type: LIST genericType          #TypeList
| baseType                      #TypeBase
| type OPEN_SQUARE CLOSE_SQUARE #TypeArray
| pairType                      #TypePair
;

genericType: LESS_THAN type GREATER_THAN ;

baseType: INT
| BOOL
| CHAR
| STRING ;

arrayType: type OPEN_SQUARE CLOSE_SQUARE ;

pairType: PAIR OPEN_PARENTHESES pairElemType COMMA pairElemType CLOSE_PARENTHESES ;

pairElemType: baseType
| arrayType
| PAIR ;

expr: OPEN_PARENTHESES expr CLOSE_PARENTHESES                         #BracketedExpression
| intLiter                                                            #Integer
| unaryOper expr                                                      #UnaryExpression
| expr compareOper expr                                               #ComparisonExpression
| expr fstPrecedenceOp expr                                           #ArithmeticFstPrecedence
| expr sndPrecedenceOp expr                                           #ArithmeticSndPrecedence
| expr thirdPrecedenceOp expr                                         #ArithmeticThirdPrecedence
| expr bitAndOp expr                                                  #BitwiseFstPrecedence
| expr bitOrOp expr                                                   #BitwiseSndPrecedence
| expr bitEorOp expr                                                  #BitwiseThirdPrecedence
| expr andBooleanOp expr                                              #BooleanFstPrecedence
| expr orBooleanOp expr                                               #BooleanSndPrecedence
| ident CALLFUNC GETFROMLIST OPEN_PARENTHESES expr CLOSE_PARENTHESES  #GetFromList
| ident CALLFUNC LISTCONTAINS OPEN_PARENTHESES expr CLOSE_PARENTHESES #ListContains
| boolLiter                                                           #Boolean
| charLiter                                                           #Character
| strLiter                                                            #String
| pairLiter                                                           #Pair
| ident                                                               #Identifier
| arrayElem                                                           #Array
;

fstPrecedenceOp: DIVIDE
| MOD
| MULTIPLY ;

sndPrecedenceOp: PLUS
| MINUS ;

thirdPrecedenceOp: SHIFT_L
| SHIFT_R ;

unaryOper: NOT
| MINUS
| LEN
| ORD
| CHR
| BIT_NOT
| LISTSIZE ;

compareOper: GREATER_THAN
| GREATER_EQ
| LESS_THAN
| LESS_EQ
| EQ
| NOT_EQ ;

bitAndOp: BIT_AND ;

andBooleanOp: AND ;

orBooleanOp: OR ;

bitOrOp: BIT_OR ;

bitEorOp: BIT_EOR ;

ident: IDENT ;

arrayElem: ident ( OPEN_SQUARE expr CLOSE_SQUARE )+ ;

intLiter: intSign? INTEGER {isIntParsable($intSign.text, $INTEGER.text)}? ;

intSign : PLUS | MINUS ;

boolLiter: TRUE | FALSE ;

charLiter: CHAR_LITER ;

strLiter: STRING_LITER ;

arrayLiter: OPEN_SQUARE ( expr ( COMMA expr)* )? CLOSE_SQUARE ;

pairLiter: NULL ;

// EOF indicates that the program must consume to the end of the input.
prog: BEGIN voidFunc* func* stat END EOF ;
