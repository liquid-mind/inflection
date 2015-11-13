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
 * - Solution to bootstraping problem: introduce ability to parse Java class files; the views
 *   could then refer to either compiled or uncompiled classes. This would allow you to run
 *   inflection *before* normal compilation, thus avoiding the catch 22 of classes that
 *   reference proxies that have not yet been generated and thus cannot be compiled.
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
	:	DEFAULT accessMethodModifier SEMICOLON
	|	// default is INHERITED
	;

// VIEW

viewDeclaration
	:	annotation* includeViewModifier VIEW includableClassSelector ( COMMA includableClassSelector )* ( USE usedClassSelector ( COMMA usedClassSelector )* )? viewBody
	|	annotation* excludeViewModifier VIEW excludableClassSelector ( COMMA excludableClassSelector )* SEMICOLON
	;
	
includeViewModifier
	:	includModifier
	;
	
excludeViewModifier
	:	excludeModifier
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
	:	annotation* includeMemberModifier accessMethodModifier includableMemberSelector ( COMMA includableMemberSelector )* SEMICOLON
	|	annotation* excludeMemberModifier accessMethodModifier excludableMemberSelector ( COMMA excludableMemberSelector )* SEMICOLON
	;
	
includeMemberModifier
	:	includModifier
	;
	
excludeMemberModifier
	:	excludeModifier
	;
	
accessMethodModifier
	:	PROPERTY
	|	FIELD
	|	// default is INHERITED
	;

includableMemberSelector
	:	aliasableMemberSelector
	|	wildcardMemberSelector
	;

excludableMemberSelector
	:	wildcardMemberSelector
	;

aliasableMemberSelector
	:	memberSelector AS alias			// Aliases cannot conflict with member names or other member aliases.
	;

memberSelector
	:	identifier
	;
	
wildcardMemberSelector
	:	identifier
	|	wildcardIdentifier
	;
	
// COMMON

includModifier
	:	INCLUDE?
	;

excludeModifier
	:	EXCLUDE
	;

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
	:	[a-zA-Z_$] [a-zA-Z_$0-9]*
	;

WILDCARD_IDENTIFIER
	:	[a-zA-Z_$*] [a-zA-Z_$*0-9]*
	;

SEMICOLON			: ';';
COLON				: ':';
DOT					: '.';
COMMA				: ',';
CURLY_BRACKET_OPEN	: '{';
CURLY_BRACKET_CLOSE	: '}';

WS	:	[ \r\t\n]+ -> skip;

