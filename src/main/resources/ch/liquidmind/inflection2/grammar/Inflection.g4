grammar Inflection;

@header
{
	package ch.liquidmind.inflection2.grammar;
}

/*
 * Notes:
 * - Includes are evaluated before excludes.
 * - Wildcards are resolved at runtime.
 * - Name selectors effect only the class they are declared in; it is not possible, e.g.,
 *   to override the definition of a super class member, unless that definition also occurs
 *   in the sub class (this is NOT the way the mechanism is described in evolution.txt, but
 *   I think it makes sense and is more consistent: views don't actually add or subtract, they
 *   merely filter (or re-route, in the case of the "use" clause) ).
 * - Two representations of taxonomies:
 *   - Compiled: symbols as String; name selectors unresolved
 *   - Linked: symbols as object references; name selectors resolved 
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
	:	annotation* TAXONOMY taxonomy ( EXTENDS taxonomy ( COMMA taxonomy )* )? taxonomyBody
	;

taxonomyBody
	:	CURLY_BRACKET_OPEN defaultAccessMethodModifier? viewDeclaration* CURLY_BRACKET_CLOSE
	;

defaultAccessMethodModifier
	:	DEFAULT accessMethodModifier SEMICOLON
	;

// VIEW

viewDeclaration
	:	annotation* INCLUDE? VIEW aliasableView ( COMMA aliasableView )* ( USE classSelector ( COMMA classSelector )* )? viewBody
	|	EXCLUDE VIEW nonAliasableView ( COMMA nonAliasableView )* SEMICOLON
	;

// 1. Aliases only legal when no wildcard is used.
// 2. Aliases always take on the package name of the aliased class.
// 3. Aliases cannot conflict with class names or other class aliases.
aliasableView
	:	classSelector ( AS alias )?		// Should class aliases be able to specify a different package?
	;
	
nonAliasableView
	:	classSelector
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
	:	memberSelector ( AS alias )?	// Note that AS is only legal if member contains no wildcards
	;

nonAliasableMember
	:	memberSelector
	;
	
// COMMON

annotation
	:	ANNOTATION
	;

taxonomy	// taxonomy cannot have wildcards
	:	type
	;

classSelector
	:	type
	;
	
memberSelector
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

