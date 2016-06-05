package ch.liquidmind.inflection.compiler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import __java.lang.reflect.__Method;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.PackageImport;
import ch.liquidmind.inflection.compiler.CompilationUnit.CompilationUnitCompiled.TypeImport;
import ch.liquidmind.inflection.grammar.InflectionParser.ClassReferenceContext;
import ch.liquidmind.inflection.grammar.InflectionParser.DecimalNumberContext;
import ch.liquidmind.inflection.grammar.InflectionParser.DigitsContext;
import ch.liquidmind.inflection.grammar.InflectionParser.ExpressionContext;
import ch.liquidmind.inflection.grammar.InflectionParser.FloatingPointLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.IntegerLiteralContext;
import ch.liquidmind.inflection.grammar.InflectionParser.MethodInvocationContext;
import ch.liquidmind.inflection.grammar.InflectionParser.TypeContext;
import ch.liquidmind.inflection.selectors.ClassSelectorContext;

public class SelectorListener extends AbstractInflectionListener
{
	private ClassSelectorContext classSelectorContext;
	private Stack< Object > expressionStack = new Stack< Object >();
	private Boolean expressionValue;
	
	private static class MethodInvocation
	{
		private String methodName;

		public MethodInvocation( String methodName )
		{
			super();
			this.methodName = methodName;
		}

		public String getMethodName()
		{
			return methodName;
		}
	}
	
	public SelectorListener( CompilationUnit compilationUnit, ClassSelectorContext classSelectorContext )
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
	
	@Override
	public void enterMethodInvocation( MethodInvocationContext methodInvocationContext )
	{
		expressionStack.add( new MethodInvocation( methodInvocationContext.getChild( 0 ).getText() ) );
	}

	@Override
	public void exitMethodInvocation( MethodInvocationContext methodInvocationContext )
	{
		List< Object > paramsAsList = new ArrayList< Object >();
		
		// Fetch parameters and invocation object from the stack.
		while ( !(expressionStack.peek() instanceof MethodInvocation) )
			paramsAsList.add( expressionStack.pop() );
		
		paramsAsList.add( classSelectorContext );
		MethodInvocation invocation = (MethodInvocation)expressionStack.pop();
		Collections.reverse( paramsAsList );
		
		// Determine parameter types.
		List< Class< ? > > paramTypesAsList = new ArrayList< Class< ? > >();
		paramsAsList.stream().forEach( x -> paramTypesAsList.add( x.getClass() ) );

		// Convert to arrays.
		Object[] params = paramsAsList.toArray( new Object[ paramsAsList.size() ] );
		Class< ? >[] paramTypes = paramTypesAsList.toArray( new Class< ? >[ paramTypesAsList.size() ] );
		
		// Locate and invoke method.
		Method method = locateMethod( invocation.getMethodName(), paramTypes );
		Object retVal = __Method.invoke( method, null, params );	// TODO: handle the exceptions.
		
		// Put the return value on the stack.
		expressionStack.push( retVal );
	}
	
	private Method locateMethod( String methodName, Class< ? >[] paramTypes )
	{
		Set< Method > methods = getCompilationUnit().getParentCompilationJob().getClassSelectorMethods();
		Method matchingMethod = null;
		
		for ( Method method : methods )
		{
			if ( method.getName().equals( methodName ) )
			{
				// TODO: check signature (need to investigate exact rules).
				matchingMethod = method;
				break;
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
}
