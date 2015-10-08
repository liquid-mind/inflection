grammar Inflection;

@header
{
	package ch.liquidmind.inflection.grammar;
}

/*
 * Notes:
 * - Includes are evaluated before excludes.
 * - Wildcards are resolved at runtime.
 * - Members referred to by member references must be declared in the class; they cannot be
 *   inherited from a super class.
 */


// COMPILATION UNIT

compilationUnit
	:	packageDeclaration importDeclarations taxonomyDeclaration*
	;

packageDeclaration
	:	PACKAGE aPackage SEMICOLON
	|	// default package
	;

importDeclarations
	:	importDeclaration*
	;

importDeclaration
	:	IMPORT importSymbol SEMICOLON
	;

importSymbol
	:	aPackage
	|	type
	;

// TAXONOMY

taxonomyDeclaration
	:	annotation* TAXONOMY taxonomyName ( EXTENDS taxonomyRef ( COMMA taxonomyRef )* )? taxonomyBody
	;
	
taxonomyName
	:	identifier
	;
	
taxonomyRef
	:	type
	;

taxonomyBody
	:	CURLY_BRACKET_OPEN defaultAccessMethodModifier? viewDeclaration* CURLY_BRACKET_CLOSE
	;

defaultAccessMethodModifier
	:	DEFAULT accessMethodModifier SEMICOLON
	;

// VIEW

viewDeclaration
	:	annotation* INCLUDE? VIEW aliasableView ( COMMA aliasableView )* ( USE classRef ( COMMA classRef )* )? viewBody
	;

// Aliases cannot conflict with class names or other class aliases.
aliasableView
	:	classRef ( AS alias )?
	;
	
viewBody
	:	CURLY_BRACKET_OPEN memberDeclaration* CURLY_BRACKET_CLOSE
	;

// MEMBER

memberDeclaration
	:	annotation* selectionTypeModifier accessMethodModifier aliasableMember ( COMMA aliasableMember )* SEMICOLON
	;
	
selectionTypeModifier
	:	INCLUDE
	|	EXCLUDE
	|	// default is INCLUDE
	;	
	
accessMethodModifier
	:	PROPERTY
	|	FIELD
	|	// default is from taxonomy
	;
	
// Aliases cannot conflict with member names or other member aliases.
aliasableMember
	:	memberRef ( AS alias )?
	;
	
// COMMON

annotation
	:	ANNOTATION
	;

classRef
	:	type
	;
	
memberRef
	:	identifier
	;
	
type
	:	( aPackage DOT )? simpleType
	;
	
aPackage
	:	identifier ( DOT identifier )*
	;
	
simpleType
	:	identifier
	;
	
alias		// aliases cannot have wildcards
	:	identifier
	;
	
identifier
	:	IDENTIFIER
	;

ANNOTATION
	:	'@' IDENTIFIER ( '(' .*? ')' )?
	;
	
MULTI_LINE_COMMENT
	:	'/*' .*? '*/' -> skip
	;

SINGLE_LINE_COMMENT
	:	'//' .*? '\r'? '\n' -> skip
	;
	
PACKAGE		: 'package';
IMPORT		: 'import';
DEFAULT		: 'default';
TAXONOMY	: 'taxonomy';
EXTENDS		: 'extends';
VIEW		: 'view';
USE			: 'use';
PROPERTY	: 'property';
FIELD		: 'field';
INCLUDE		: 'include';
EXCLUDE		: 'exclude';
AS			: 'as';
BASIC_TYPE	: 'byte' | 'short' | 'int' | 'long' | 'float' | 'double' | 'boolean' | 'char';

// Note that while most of these keywords are not used in the
// grammar, they are never the less declared in the lexer to
// ensure that they are not used as identifiers.
JAVA_KEYWORD
	:	'abstract' | 'continue' | 'for' | 'new' | 'switch'
	|	'assert' | 'default' | 'if' | 'package' | 'synchronized'
	|	'boolean' | 'do' | 'goto' | 'private' | 'this'
	|	'break' | 'double' | 'implements' | 'protected' | 'throw'
	|	'byte' | 'else' | 'import' | 'public' | 'throws'
	|	'case' | 'enum' | 'instanceof' | 'return' | 'transient'
	|	'catch' | 'extends' | 'int' | 'short' | 'try'
	|	'char' | 'final' | 'interface' | 'static' | 'void'
	|	'class' | 'finally' | 'long' | 'strictfp' | 'volatile'
	|	'const' | 'float' | 'native' | 'super' | 'while'
	;
	
IDENTIFIER
	:	[a-zA-Z_$] [a-zA-Z0-9]*
	;

SEMICOLON			: ';';
COLON				: ':';
DOT					: '.';
COMMA				: ',';
CURLY_BRACKET_OPEN	: '{';
CURLY_BRACKET_CLOSE	: '}';
ASTERISK			: '*';

WS	:	[ \r\t\n]+ -> skip;

