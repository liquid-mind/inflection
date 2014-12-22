grammar Inflection;

@header
{
	package ch.zhaw.inflection.grammar;
}

compilationUnit
	:	packageDeclaration? importDeclarations declarations
	;

packageDeclaration
	:	PACKAGE aPackage SEMICOLON
	;

importDeclarations
	:	importDeclaration*
	;

importDeclaration
	:	IMPORT importSymbol SEMICOLON
	;

importSymbol
	:	importPackageSymbol
	|	importTypeSymbol
	;
	
importPackageSymbol
	:	aPackage DOT ASTERISK
	;
	
importTypeSymbol
	:	type
	;
	
declarations
	:	declaration*
	;
	
declaration
	:	classViewDeclaration
	|	vmapDeclaration
	|	hgroupDeclaration
	;

// VIEW

classViewDeclaration
	:	VIEW identifier viewofDeclaration ( superDeclaration | defaultSuperDeclaration ) viewBody
	;

viewofDeclaration
	:	OF aClass
	;

defaultSuperDeclaration
	:
	;

superDeclaration
	:	EXTENDS classView
	;

viewBody
	:	CURLY_BRACKET_OPEN memberViewDeclaration* CURLY_BRACKET_CLOSE
	;
	
memberViewDeclaration
	:	( memberTypeModifier | defaultMemberTypeModifier ) ( aggregationModifier | defaultAggregationModifier ) memberView memberViewName SEMICOLON
	;
	
defaultMemberTypeModifier
	:
	;
	
memberTypeModifier
	:	PROPERTY
	|	FIELD
	;

defaultAggregationModifier
	:
	;
	
aggregationModifier
	:	DISCRETE
	|	COMPOSITE
	;

memberView
	:	classView
	;
	
memberViewName
	:	identifier
	;

// VMAP

vmapDeclaration
	:	VMAP identifier ( superVmapDeclaration | noSuperVmapDeclaration ) vmapBody
	;

noSuperVmapDeclaration
	:
	;

superVmapDeclaration
	:	EXTENDS vmap
	;

vmapBody
	:	CURLY_BRACKET_OPEN mappingDeclaration* CURLY_BRACKET_CLOSE
	;
	
mappingDeclaration
	:	defaultMappingDeclaration
	|	standardMappingDeclaration
	;
	
defaultMappingDeclaration
	:	DEFAULT mappedVisitor SEMICOLON
	;
	
standardMappingDeclaration
	:	mappingInflectionViews COLON mappedVisitors SEMICOLON
	;
	
mappingInflectionViews
	:	mappingInflectionView ( COMMA mappingInflectionView )*
	;
	
mappingInflectionView
	:	mappingMemberView
	|	mappingClassView
	;
	
mappingClassView
	:	classView
	;
	
mappingMemberView
	:	classView POINTER identifier
	;
	
mappedVisitors
	:	mappedVisitor ( COMMA mappedVisitor )*
	;
	
mappedVisitor
	:	aClass
	;
	
vmap
	:	type
	;
	
// HGROUP

hgroupDeclaration
	:	HGROUP identifier ( superHgroupDeclaration | noSuperHgroupDeclaration) hgroupBody
	;
	
noSuperHgroupDeclaration
	:
	;
	
superHgroupDeclaration
	:	EXTENDS hgroup
	;
	
hgroupBody
	:	CURLY_BRACKET_OPEN groupedClassView ( COMMA groupedClassView )* CURLY_BRACKET_CLOSE
	;
	
groupedClassView
	:	classView
	;
	
hgroup
	:	type
	;
	
// COMMON
	
aClass
	:	type
	;

classView
	:	type
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
	|	BASIC_TYPE
	;
	
MULTI_LINE_COMMENT
	:	'/*' .*? '*/' -> skip
	;

SINGLE_LINE_COMMENT
	:	'//' .*? '\r'? '\n' -> skip
	;
	
SEMICOLON			: ';';
COLON				: ':';
DOT					: '.';
COMMA				: ',';
CURLY_BRACKET_OPEN	: '{';
CURLY_BRACKET_CLOSE	: '}';
ASTERISK			: '*';
POINTER				: '->';

DEFAULT		: 'default';
VMAP		: 'vmap';
HGROUP		: 'hgroup';
PACKAGE		: 'package';
IMPORT		: 'import';
VIEW		: 'view';
OF			: 'of';
EXTENDS		: 'extends';
PROPERTY	: 'property';
FIELD		: 'field';
DISCRETE	: 'discrete';
COMPOSITE	: 'composite';
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

WS	:	[ \r\t\n]+ -> skip;

