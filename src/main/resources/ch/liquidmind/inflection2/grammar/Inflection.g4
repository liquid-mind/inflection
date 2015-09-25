grammar Inflection;

@header
{
	package ch.liquidmind.inflection2.grammar;
}

/*
 * Notes:
 * 1. Includes are evaluated before excludes.
 * 2. Wildcards are resolved at runtime.
 * 3. Two representations of taxonomies:
 *    3.1. Raw: output of compilation; includes symbols with wildcards
 *    3.2.. 
 */



// COMPILATION UNIT

compilationUnit
	:	packageDeclaration importDeclarations taxonomyDeclaration*
	;

packageDeclaration
	:	PACKAGE aPackage SEMICOLON		// Package names cannot have wildcards.
	|	// default package
	;

importDeclarations
	:	importDeclaration*
	;

importDeclaration
	:	IMPORT importSymbol SEMICOLON		// Import symbol can only have wildcard at end (Java-style).
	;

importSymbol
	:	aPackage
	|	type
	;

// TAXONOMY

taxonomyDeclaration
	:	TAXONOMY taxonomy ( EXTENDS taxonomy ( COMMA taxonomy )* )? taxonomyBody
	;

taxonomyBody
	:	CURLY_BRACKET_OPEN defaultAccessMethodModifier? viewDeclaration* CURLY_BRACKET_CLOSE
	;

defaultAccessMethodModifier
	:	DEFAULT accessMethodModifier SEMICOLON
	;

// VIEW

viewDeclaration
	:	annotation* INCLUDE? VIEW aliasableView ( USE aClass ( COMMA aClass )* )? viewBody
	|	EXCLUDE VIEW nonAliasableView SEMICOLON
	;

// 1. Aliases only legal when no wildcard is used.
// 2. Aliases always take on the package name of the aliased class.
// 3. Aliases cannot conflict with class names or other class aliases.
aliasableView
	:	aClass ( AS identifier )?		// Should class aliases be able to specify a different package?
	;
	
nonAliasableView
	:	aClass
	;
	
viewBody
	:	CURLY_BRACKET_OPEN memberDeclaration* CURLY_BRACKET_CLOSE
	;

// MEMBER

memberDeclaration
	:	annotation* INCLUDE? accessMethodModifier aliasableMember  ( COMMA aliasableMember )* SEMICOLON
	|	EXCLUDE accessMethodModifier nonAliasableMember ( COMMA nonAliasableMember )* SEMICOLON
	;
	
accessMethodModifier
	:	PROPERTY
	|	FIELD
	|	// default modifier (from taxonomy)
	;
	
// 1. Aliases only legal when no wildcard is used.
// 2. Aliases cannot conflict with member names or other member aliases.
aliasableMember
	:	member ( AS identifier )?	// Note that AS is only legal if member contains no wildcards
	;

nonAliasableMember
	:	member
	;
	
// COMMON

annotation
	:	ANNOTATION
	;

taxonomy		// taxonomy cannot have wildcards
	:	type
	;

aClass
	:	type
	;
	
member
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
	:	[a-zA-Z_$*] [a-zA-Z0-9*]*
	;

SEMICOLON			: ';';
COLON				: ':';
DOT					: '.';
COMMA				: ',';
CURLY_BRACKET_OPEN	: '{';
CURLY_BRACKET_CLOSE	: '}';
ASTERISK			: '*';

WS	:	[ \r\t\n]+ -> skip;

