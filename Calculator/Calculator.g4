grammar Calculator;

expression: '(' expression ')'| multiplyExpression ((PLUS | MINUS) multiplyExpression)*;
multiplyExpression: exponentationExpression ((MULT | DIV) exponentationExpression)*;
exponentationExpression: sqrtExpression (POW exponentationExpression)?;
sqrtExpression: atom | SQRT sqrtExpression;
atom: INT | MINUS atom | '(' expression ')';

INT: [0-9]+;
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
POW: '^';
SQRT: 'sqrt';
WS: [ \t\r\n]+ -> skip;

