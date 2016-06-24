package ch.liquidmind.inflection.selectors;

import ch.liquidmind.inflection.association.AssociationRegistry;
import ch.liquidmind.inflection.association.Class;
import ch.liquidmind.inflection.association.Property;

public class Selectors
{
	public static boolean hasRedefinedProperty()
	{
		return getCurrentClass().getOwnedProperties().stream().filter( property -> property.getRedefiningProperty() != null ).findAny().isPresent();
	}

	public static boolean hasSubsettedProperty()
	{
		return getCurrentClass().getOwnedProperties().stream().filter( property -> !property.getSubsetttingProperties().isEmpty() ).findAny().isPresent();
	}
	
	public static boolean isRedefinedProperty()
	{
		return getCurrentProperty().getRedefiningProperty() != null;
	}
	
	public static boolean isSubsettedProperty()
	{
		return !getCurrentProperty().getSubsetttingProperties().isEmpty();
	}
	
	private static Property getCurrentProperty()
	{
		if ( !( SelectorContext.get() instanceof PropertySelectorContext ) )
			throw new RuntimeException( "This selector can only be applied to properties." );
		
		PropertySelectorContext psContext = SelectorContext.get();
		Class theClass = getCurrentClass( psContext );
		Property property = theClass.getOwnedProperty( psContext.getCurrentProperty().getName() );
		
		return property;
	}

	private static Class getCurrentClass()
	{
		if ( !( SelectorContext.get() instanceof ClassSelectorContext ) )
			throw new RuntimeException( "This selector can only be applied to classes." );
		
		ClassSelectorContext csContext = SelectorContext.get();
		
		return getCurrentClass( csContext );
	}
	
	private static Class getCurrentClass( SelectorContext selectorContext )
	{
		String className = selectorContext.getCurrentClass().getName();
		Class theClass = AssociationRegistry.instance().getRegisteredClass( className );
		
		if ( theClass == null )
			throw new RuntimeException( String.format( "The class %s is not registered with %s.", className, AssociationRegistry.class.getName() ) );
		
		return theClass;
	}
	
	public static boolean isAssignableTo( java.lang.Class< ? > theClass )
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
		String wildcardExpressionRegEx = wildcardExpression.replace( ".", "\\." ).replace( "*", ".*?" ).toLowerCase();
		
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
