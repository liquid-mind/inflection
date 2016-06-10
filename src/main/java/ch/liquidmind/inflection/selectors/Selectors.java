package ch.liquidmind.inflection.selectors;

public class Selectors
{
	public static boolean isAssignableTo( Class< ? > theClass )
	{
		return theClass.isAssignableFrom( SelectorContext.get().getCurrentClass() );
	}
	
	public static boolean hasModifier( int modifier )
	{
		return ( SelectorContext.get().getCurrentClass().getModifiers() & modifier ) > 0;
	}
	
	public static boolean matches( String wildcardExpression )
	{
		boolean matches;
		SelectorContext selectorContext = SelectorContext.get();
		String wildcardExpressionRegEx = wildcardExpression.replace( ".", "\\." ).replace( "*", "[a-zA-Z0-9_$]*?" ).toLowerCase();
		
		if ( selectorContext instanceof ClassSelectorContext )
			matches = matches( (ClassSelectorContext)selectorContext, wildcardExpressionRegEx );
		else if ( selectorContext instanceof PropertySelectorContext )
			matches = matches( (PropertySelectorContext)selectorContext, wildcardExpressionRegEx );
		else if ( selectorContext instanceof FieldSelectorContext )
			matches = matches( (FieldSelectorContext)selectorContext, wildcardExpressionRegEx );
		else
			throw new IllegalStateException( "Unexpected type for selectorContext: " + selectorContext.getClass().getName() );
		
		return matches;
	}
	
	private static boolean matches( ClassSelectorContext classSelectorContext, String wildcardExpression )
	{
		String className = classSelectorContext.getCurrentClass().getName().toLowerCase();
		return className.matches( wildcardExpression );
	}
	
	private static boolean matches( FieldSelectorContext fieldSelectorContext, String wildcardExpression )
	{
		String fieldName = fieldSelectorContext.getCurrentField().getName().toLowerCase();
		return fieldName.matches( wildcardExpression );
	}
	
	private static boolean matches( PropertySelectorContext propertySelectorContext, String wildcardExpression )
	{
		String propertyName = propertySelectorContext.getCurrentProperty().getName().toLowerCase();
		return propertyName.matches( wildcardExpression );
	}
}
