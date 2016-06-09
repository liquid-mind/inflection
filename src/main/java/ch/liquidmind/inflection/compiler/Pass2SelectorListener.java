package ch.liquidmind.inflection.compiler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import __java.lang.__Class;
import __java.lang.__NoSuchFieldException;
import __java.lang.reflect.__Field;
import __java.lang.reflect.__Method;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.StaticClassImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.StaticMemberImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.TypeImport;
import ch.liquidmind.inflection.grammar.InflectionParser.BooleanLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.CharacterLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ClassReferenceContext;
import ch.liquidmind.inflection.grammar.InflectionParser.DecimalNumberContext;
import ch.liquidmind.inflection.grammar.InflectionParser.DigitsContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExpressionContext;
import ch.liquidmind.inflection.grammar.InflectionParser.FloatingPointLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IntegerLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.MethodArgumentContext;
import ch.liquidmind.inflection.grammar.InflectionParser.MethodInvocationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.NullLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.StaticFieldReferenceContext;
import ch.liquidmind.inflection.grammar.InflectionParser.StaticMethodReferenceContext;
import ch.liquidmind.inflection.grammar.InflectionParser.StaticReferenceContext;
import ch.liquidmind.inflection.grammar.InflectionParser.StringLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeContext;
import ch.liquidmind.inflection.selectors.ClassSelectorContext;

public class Pass2SelectorListener extends AbstractInflectionListener
{
	private ClassSelectorContext classSelectorContext;
	private Stack< Object > expressionStack = new Stack< Object >();
	private Boolean expressionValue;
	
	public Pass2SelectorListener( CompilationUnit compilationUnit, ClassSelectorContext classSelectorContext )
	{
		super( compilationUnit );
		this.classSelectorContext = classSelectorContext;
	}
	
	public boolean getExpressionValue( ExpressionContext expressionContext )
	{
		if ( expressionValue == null )
		{
			if ( expressionStack.size() != 1 )
				throw new IllegalStateException( "expressionStack should contain exactly one element." );
			
			if ( !(expressionStack.peek() instanceof Boolean) )
				reportError( expressionContext.start, expressionContext.stop, "Expression must evaluate to boolean." );
			
			expressionValue = (Boolean)expressionStack.pop();
		}
		
		return expressionValue;
	}

	public static final String LOGICAL_NOT = "!";
	public static final String LOGICAL_AND = "&&";
	public static final String LOGICAL_OR = "||";
	
	private boolean logicalNot( Object value, ExpressionContext expressionContext )
	{
		boolean valueCast = castOperatorValue( value, Boolean.class, expressionContext );
		
		return !valueCast;
	}
	
	private boolean logicalAnd( Object value1, Object value2, ExpressionContext expressionContext1, ExpressionContext expressionContext2 )
	{
		boolean value1Cast = castOperatorValue( value1, Boolean.class, expressionContext1 );
		boolean value2Cast = castOperatorValue( value2, Boolean.class, expressionContext2 );
		
		return value1Cast && value2Cast;
	}
	
	private boolean logicalOr( Object value1, Object value2, ExpressionContext expressionContext1, ExpressionContext expressionContext2 )
	{
		boolean value1Cast = castOperatorValue( value1, Boolean.class, expressionContext1 );
		boolean value2Cast = castOperatorValue( value2, Boolean.class, expressionContext2 );
		
		return value1Cast || value2Cast;
	}
	
	@SuppressWarnings( "unchecked" )
	private < T > T castOperatorValue( Object value, Class< T > expectedType, ExpressionContext expressionContext )
	{
		if ( !expectedType.isAssignableFrom( value.getClass() ) )
			reportError( expressionContext.start, expressionContext.stop, "Expected type " + expectedType.getName() + " but was " + value.getClass().getName() + "." );
		
		return (T)value;
	}
	
	// TODO: skip second operand evaluation in the following cases:
	// 1. Boolean AND: if first operand is false.
	// 2. Boolean OR: if first operand is true.
	// TODO: Extend the number of supported operators, which will require
	// in some cases extensive type checking and/or conversion. For example,
	// the == operator works differently depending whether the operands are
	// basic or complex types, and in the case of basic types on which
	// basic types are being compared. This will take some work and isn't the
	// first priority at this time. Also, it might be better to delegate more
	// complex expressions to the helper methods, anyway.
	@Override
	public void exitExpression( ExpressionContext expressionContext )
	{
		if ( expressionContext.getChildCount() < 2 )
			return;
		
		if ( expressionContext.getChild( 0 ).getText().equals( LOGICAL_NOT ) )
			expressionStack.push( logicalNot( expressionStack.pop(), getChildExpression( expressionContext, 1 ) ) );
		else if ( expressionContext.getChild( 1 ).getText().equals( LOGICAL_AND ) )
			expressionStack.push( logicalAnd( expressionStack.pop(), expressionStack.pop(), getChildExpression( expressionContext, 2 ), getChildExpression( expressionContext, 0 ) ) );
		else if ( expressionContext.getChild( 1 ).getText().equals( LOGICAL_OR ) )
			expressionStack.push( logicalOr( expressionStack.pop(), expressionStack.pop(), getChildExpression( expressionContext, 2 ), getChildExpression( expressionContext, 0 ) ) );
	}
	
	private ExpressionContext getChildExpression( ExpressionContext expressionContext, int index )
	{
		return (ExpressionContext)expressionContext.getChild( index );
	}
	
	public int parameterCount;

	@Override
	public void enterMethodInvocation( MethodInvocationContext methodInvocationContext )
	{
		parameterCount = 0;
	}

	@Override
	public void enterMethodArgument( MethodArgumentContext methodArgumentContext )
	{
		++parameterCount;
	}

	@Override
	public void exitMethodInvocation( MethodInvocationContext methodInvocationContext )
	{
		List< Object > paramsAsList = new ArrayList< Object >();
		
		// Fetch parameters and invocation object from the stack.
		for ( int i = 0 ; i < parameterCount ; ++i )
			paramsAsList.add( expressionStack.pop() );
		
		Collections.reverse( paramsAsList );
		
		// Determine parameter types.
		List< Class< ? > > paramTypesAsList = new ArrayList< Class< ? > >();
		paramsAsList.stream().forEach( x -> paramTypesAsList.add( ( x == null ? null : x.getClass() ) ) );

		// Convert to arrays.
		Object[] params = paramsAsList.toArray( new Object[ parameterCount ] );
		Class< ? >[] paramTypes = paramTypesAsList.toArray( new Class< ? >[ parameterCount ] );
		
		// Locate and invoke method.
		StaticMethodReferenceContext staticMethodReferenceContext = (StaticMethodReferenceContext)methodInvocationContext.getChild( 0 );
		Method method = locateMethod( staticMethodReferenceContext, paramTypes );
		ClassSelectorContext.set( classSelectorContext );
		Object retVal = __Method.invoke( method, null, params );	// TODO: handle the exceptions.
		
		// Put the return value on the stack.
		expressionStack.push( retVal );
	}
	
//	private Class< ? > getStaticReferenceClass( StaticReferenceContext staticReferenceContext )
//	{
//		if ( staticReferenceContext.getChildCount() == 1 )
//			throw new IllegalStateException( "No support yet for unqualified static references." );
//		
//		TypeContext typeContext = (TypeContext)staticReferenceContext.getChild( 0 );
//		String className = resolveClassReference( typeContext );
//		Class< ? > theClass = getClass( className );
//		
//		return theClass;
//	}
	
	public enum StaticReferenceType
	{
		STATIC_FIELD, STATIC_METHOD
	}
	
	private static final Map< StaticReferenceType, String > MEMBER_DISPLAY_NAMES = new HashMap< StaticReferenceType, String >();
	
	private Class< ? > resolveStaticReferenceClass( StaticReferenceContext staticReferenceContext, StaticReferenceType staticReferenceType )
	{
		Set< Class< ? > > matches = new HashSet< Class< ? > >();
		Class< ? > theClass = getStaticReferenceClass( staticReferenceContext );
		
		if ( theClass == null )
		{
			// First, try matching to a static member import
			String staticReferenceName = getStaticReferenceName( staticReferenceContext );
			StaticMemberImport staticMemberImport = getStaticMemberImports().get( staticReferenceName );
			
			if ( staticMemberImport != null )
			{
				staticMemberImport.setWasReferenced( true );
				String className = staticMemberImport.getParserRuleContext().getChild( 0 ).getText();
				
				if ( classExists( className ) )
					matches.add( getClass( className ) );
			}
			
			// Then, try matching to a static class import
			for ( StaticClassImport staticClassImport : getStaticClassImports() )
			{
				String className = staticClassImport.getName();
				
				if ( staticReferenceType.equals( StaticReferenceType.STATIC_FIELD ) && staticFieldExists( className, staticReferenceName ) )
				{
					staticClassImport.setWasReferenced( true );
					matches.add( getClass( className ) );
				}
				else if ( staticReferenceType.equals( StaticReferenceType.STATIC_METHOD ) && staticMethodExists( className, staticReferenceName ) )
				{
					staticClassImport.setWasReferenced( true );
					matches.add( getClass( className ) );
				}
			}
			
			if ( matches.size() == 0 )
				reportError( staticReferenceContext.start, staticReferenceContext.stop, "Could not find referenced " + MEMBER_DISPLAY_NAMES.get( staticReferenceType ) + " (Did you misspell? Or forget an import? Or a jar?)." );
			else if ( matches.size() == 1 )
				theClass = matches.iterator().next();
			else if ( matches.size() > 1 )
				reportError( staticReferenceContext.start, staticReferenceContext.stop, "Referenced " + MEMBER_DISPLAY_NAMES.get( staticReferenceType ) + " is ambiguous; could refer to any of: " +
						String.join( ", ", matches.stream().map( x -> x.getName() ).collect(Collectors.toList() ) ) + "." );
			else
				throw new IllegalStateException( "Unexpected value for matches.size()." );
		}
		
		return theClass;
	}
	
	private boolean staticFieldExists( String className, String fieldName )
	{
		boolean staticFieldExists;
		
		try
		{
			__Class.getField( getClass( className ), fieldName );
			staticFieldExists = true;
		}
		catch ( __NoSuchFieldException e )
		{
			staticFieldExists = false;
		}
		
		return staticFieldExists;
	}
	
	private boolean staticMethodExists( String className, String methodName )
	{
		return Arrays.asList( getClass( className ).getMethods() ).stream().filter( x -> x.getName().equals( methodName ) ).findFirst().isPresent();
	}
	
	private Class< ? > getStaticReferenceClass( StaticReferenceContext staticReferenceContext )
	{
		Class< ? > theClass;
		
		if ( staticReferenceContext.getChildCount() == 3 )
		{
			TypeContext typeContext = (TypeContext)staticReferenceContext.getChild( 0 );
			String className = resolveClassReference( typeContext );
			theClass = getClass( className );
		}
		else if ( staticReferenceContext.getChildCount() == 1 )
		{
			theClass = null;
		}
		else
		{
			throw new IllegalStateException( "Unexpected number of children for staticReferenceContext.");
		}
		
		return theClass;
	}
	
	private String getStaticReferenceName( StaticReferenceContext staticReferenceContext )
	{
		String staticReferenceName;
		
		if ( staticReferenceContext.getChildCount() == 1 )
			staticReferenceName = staticReferenceContext.getChild( 0 ).getText();
		else if ( staticReferenceContext.getChildCount() == 3 )
			staticReferenceName = staticReferenceContext.getChild( 2 ).getText();
		else
			throw new IllegalStateException( "Unexpected number of children for staticReferenceContext." );
		
		return staticReferenceName;
	}
	
	private Method locateMethod( StaticMethodReferenceContext staticMethodReferenceContext, Class< ? >[] paramTypes )
	{
		StaticReferenceContext staticReferenceContext = (StaticReferenceContext)staticMethodReferenceContext.getChild( 0 );
		Class< ? > theClass = resolveStaticReferenceClass( staticReferenceContext, StaticReferenceType.STATIC_METHOD );
		String methodName = getStaticReferenceName( staticReferenceContext );
		Method matchingMethod = null;
		
		for ( Method method : theClass.getMethods() )
		{
			if ( method.getName().equals( methodName ) )
			{
				if ( matchingMethod != null )
					throw new IllegalStateException( "No support for overloaded static methods at this time.");
				else
					matchingMethod = method;
			}
		}
		
		return matchingMethod;
	}

	@Override
	public void enterIntegerLiteral( IntegerLiteralContext integerLiteralContext )
	{
		DigitsContext digitsContext = (DigitsContext)integerLiteralContext.getChild( 0 );
		String digits = digitsContext.getText();
		boolean isLong = ( integerLiteralContext.getChildCount() == 2 ? true : false );
		
		Object integer;
		
		if ( isLong )
			integer = Long.valueOf( digits );
		else
			integer = Integer.valueOf( digits );

		expressionStack.push( integer );
	}

	@Override
	public void enterFloatingPointLiteral( FloatingPointLiteralContext floatingPointLiteralContext )
	{
		DecimalNumberContext decimalNumberContext = (DecimalNumberContext)floatingPointLiteralContext.getChild( 0 );
		String decimalNumber = decimalNumberContext.getText();
		boolean isFloat = ( floatingPointLiteralContext.getChildCount() == 2 ? true : false );
		
		Object aFloat;
		
		if ( isFloat )
			aFloat = Float.valueOf( decimalNumber );
		else
			aFloat = Double.valueOf( decimalNumber );

		expressionStack.push( aFloat );
	}

	@Override
	public void enterClassReference( ClassReferenceContext classReferenceContext )
	{
		TypeContext typeContext = (TypeContext)classReferenceContext.getChild( 0 );
		String className = resolveClassReference( typeContext );
		Class< ? > theClass = getClass( className );
		expressionStack.push( theClass );
	}
	
	private String resolveClassReference( TypeContext typeContext )
	{
		// TODO Implement by looking first in the type imports and second in the 
		// package imports. Look for matching taxonomies as well as matching classes
		// in the latter and report an error if the resolution is ambiguous.
		
		String resolvedClassReference = null;
		List< String > matches = new ArrayList< String >();
		String packageName = getPackageName( typeContext );
		String simpleName = getSimpleTypeName( typeContext );
		
		if ( packageName.isEmpty() )
		{
			// First, try matching to a type import.
			TypeImport typeImport = getTypeImports().get( simpleName );
			
			if ( typeImport != null )
			{
				typeImport.setWasReferenced( true );
				String match = typeImport.getName();
				
				if ( classExists( match ) )
					matches.add( match );
			}

			// Then, try matching to one of the package imports.
			for ( PackageImport packageImport : getPackageImports() )
			{
				String potentialMatch = ( packageImport.getName().isEmpty() ? simpleName : packageImport.getName() + "." + simpleName );
				
				if ( classExists( potentialMatch ) )
				{
					packageImport.setWasReferenced( true );
					matches.add( potentialMatch );
				}
			}
		}
		else
		{
			String typeName = getTypeName( typeContext );
			
			if ( classExists( typeName ) )
				matches.add( typeName );
		}
		
		if ( matches.size() == 0 )
			reportError( typeContext.start, typeContext.stop, "Could not find referenced class (Did you misspell? Or forget an import? Or a jar?)." );
		else if ( matches.size() == 1 )
			resolvedClassReference = matches.get( 0 );
		else if ( matches.size() > 1 )
			reportError( typeContext.start, typeContext.stop, "Class reference is ambiguous; could refer to any of: " + String.join( ", ", matches ) + "." );
		else
			throw new IllegalStateException( "Unexpected value for matches.size()." );
		
		return resolvedClassReference;
	}
	
	private boolean classExists( String name )
	{
		return getClass( name ) != null;
	}

	@Override
	public void enterStaticFieldReference( StaticFieldReferenceContext staticFieldReferenceContext )
	{
		StaticReferenceContext staticReferenceContext = (StaticReferenceContext)staticFieldReferenceContext.getChild( 0 );
		Class< ? > theClass = resolveStaticReferenceClass( staticReferenceContext, StaticReferenceType.STATIC_FIELD );
		String staticFieldName = getStaticReferenceName( staticReferenceContext );
		Field field = __Class.getField( theClass, staticFieldName );
		Object value = __Field.get( field, null );
		
		expressionStack.push( value );
	}

	@Override
	public void enterBooleanLiteral( BooleanLiteralContext booleanLiteralContext )
	{
		expressionStack.push( Boolean.valueOf( booleanLiteralContext.getText() ) );
	}

	@Override
	public void enterCharacterLiteral( CharacterLiteralContext characterLiteralContext )
	{
		expressionStack.push( characterLiteralContext.getChild( 1 ).getText().charAt( 0 ) );
	}

	@Override
	public void enterStringLiteral( StringLiteralContext stringLiteralContext )
	{
		expressionStack.push( stringLiteralContext.getChild( 1 ).getText() );
	}

	@Override
	public void enterNullLiteral( NullLiteralContext NullLiteralContext )
	{
		expressionStack.push( null );
	}
}
