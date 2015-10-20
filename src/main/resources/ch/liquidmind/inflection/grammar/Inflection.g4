grammar Inflection;

@header
{
	package ch.liquidmind.inflection.grammar;
}

/*
 * Notes:
 * - Includes are evaluated before excludes.
 * - Members referred to by member references must be declared in the class; they cannot be
 *   inherited from a super class.
 */
 
/*
 * Ideas:
 * - New modifier operation type which can be either read, write or readwrite. Can be
 *   set at method or taxonomy level.
 */

// COMPILATION UNIT

compilationUnit
	:	packageDeclaration importDeclarations taxonomyDeclaration*
	;

packageDeclaration
	:	specificPackage
	|	defaultPackage
	;
	
specificPackage
	:	PACKAGE aPackage SEMICOLON
	;
	
defaultPackage
	:
	;

importDeclarations
	:	importDeclaration*
	;

importDeclaration
	:	IMPORT importSymbol SEMICOLON
	;

importSymbol
	:	typeImport
	|	packageImport
	;
	
typeImport
	:	type
	;
	
packageImport
	:	aPackage DOT wildcardIdentifier	// wildcardIdentifier must be exactly '*'
	;

// TAXONOMY

taxonomyDeclaration
	:	annotation* TAXONOMY taxonomyName taxonomyExtensions taxonomyBody
	;
	
taxonomyName
	:	identifier
	;
	
taxonomyExtensions
	:	( EXTENDS extendedTaxonomy ( COMMA extendedTaxonomy )* )?
	|	// default is Taxonomy
	;
	
extendedTaxonomy
	:	type
	;

taxonomyBody
	:	CURLY_BRACKET_OPEN defaultAccessMethodModifier viewDeclaration* CURLY_BRACKET_CLOSE
	;

defaultAccessMethodModifier
	:	DEFAULT ( PROPERTY | FIELD | INHERITED ) SEMICOLON
	|	// default is INHERITED
	;

// VIEW

viewDeclaration
	:	annotation* includeViewModifier VIEW includableClassSelector ( COMMA includableClassSelector )* ( USE usedClassSelector ( COMMA usedClassSelector )* )? viewBody
	|	annotation* excludeViewModifier VIEW excludableClassSelector ( COMMA excludableClassSelector )* SEMICOLON
	;
	
includeViewModifier
	:	INCLUDE?
	;

excludeViewModifier
	:	EXCLUDE
	;

viewBody
	:	CURLY_BRACKET_OPEN memberDeclaration* CURLY_BRACKET_CLOSE
	;

includableClassSelector
	:	aliasableClassSelector
	|	wildcardClassSelector
	;
	
excludableClassSelector
	:	wildcardClassSelector
	;

aliasableClassSelector
	:	classSelector AS alias
	;
	
usedClassSelector
	:	classSelector
	;

classSelector
	:	type
	;

wildcardClassSelector
	:	type
	|	wildcardType
	;

// MEMBER

memberDeclaration
	:	memberAnnotation* INCLUDE? accessMethodModifier includableMemberSelector ( COMMA includableMemberSelector )* SEMICOLON
	|	memberAnnotation* EXCLUDE accessMethodModifier excludableMemberSelector ( COMMA excludableMemberSelector )* SEMICOLON
	;
	
memberAnnotation
	:	annotation
	;
	
accessMethodModifier
	:	PROPERTY
	|	FIELD
	|	INHERITED
	|
	;

includableMemberSelector
	:	aliasableMemberSelector
	|	wildcardMemberSelector
	;

excludableMemberSelector
	:	wildcardMemberSelector
	;

aliasableMemberSelector
	:	memberSelector ( AS alias )?		// Aliases cannot conflict with member names or other member aliases.
	;

memberSelector
	:	identifier
	;
	
wildcardMemberSelector
	:	identifier
	|	wildcardIdentifier
	;
	
// COMMON

annotation
	:	ANNOTATION
	;
	
type
	:	( aPackage DOT )? simpleType
	;
	
wildcardType
	:	( aPackage DOT )? wildcardSimpleType
	;
	
aPackage
	:	identifier ( DOT identifier )*
	;
	
simpleType
	:	identifier
	;
	
wildcardSimpleType
	:	wildcardIdentifier
	;
	
alias		// aliases cannot have wildcards
	:	identifier
	;
	
identifier
	:	IDENTIFIER
	;

wildcardIdentifier
	:	WILDCARD_IDENTIFIER
	;
	
// TOKENS

// TODO: introduce support for nested annotations, or; introduce full support for annotations.
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
INHERITED	: 'inherited';
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

WILDCARD_IDENTIFIER
	:	[a-zA-Z_$*] [a-zA-Z0-9*]*
	;

SEMICOLON			: ';';
COLON				: ':';
DOT					: '.';
COMMA				: ',';
CURLY_BRACKET_OPEN	: '{';
CURLY_BRACKET_CLOSE	: '}';

WS	:	[ \r\t\n]+ -> skip;

