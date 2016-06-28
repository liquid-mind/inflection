package ch.liquidmind.inflection.selectors;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import ch.liquidmind.inflection.association.Aggregation;
import ch.liquidmind.inflection.association.AssociationRegistry;
import ch.liquidmind.inflection.association.Class;
import ch.liquidmind.inflection.association.Property;

public class Selectors
{
	public static boolean isProperyOrRedefinitionOf( String propertyName )
	{
		return isProperyOrRedefinitionOf( getCurrentProperty(), propertyName );
	}
	
	private static boolean isProperyOrRedefinitionOf( Property currentProperty, String propertyName )
	{
		boolean isProperyOrRedefinitionOf = ( currentProperty == null ? false : currentProperty.getName().equals( propertyName ) );

		if ( currentProperty != null )
		{
			if ( !isProperyOrRedefinitionOf )
				isProperyOrRedefinitionOf = isProperyOrRedefinitionOf( currentProperty.getRedefinedProperty(), propertyName );
			
			if ( !isProperyOrRedefinitionOf )
				isProperyOrRedefinitionOf = isProperyOrRedefinitionOf( getOverriddenProperty( currentProperty ), propertyName );
		}
		
		return isProperyOrRedefinitionOf;
	}
	
	private static Property getOverriddenProperty( Property property )
	{
		Property overriddenProperty = null;
		Class currentClass = property.getOwningClass().getSuperClass();
		
		while ( currentClass != null )
		{
			overriddenProperty = currentClass.getOwnedProperty( property.getName() );
			
			if ( overriddenProperty != null )
				break;
			
			currentClass = currentClass.getSuperClass();
		}
		
		return overriddenProperty;
	}
	
	public static boolean hasNonCompositeEndOfCompositeAssociation()
	{
		return getCurrentClass().getOwnedProperties().stream().filter( property -> hasCompositeAssociatedProperty( property ) ).findAny().isPresent();
	}
	
	public static boolean isNonCompositeEndOfCompositeAssociation()
	{
		return hasCompositeAssociatedProperty( getCurrentProperty() );
	}
	
	private static boolean hasCompositeAssociatedProperty( Property property )
	{
		try
		{
			return property.getAssociatedProperties().stream().filter( associatedProperty -> associatedProperty.getAggregation().equals( Aggregation.COMPOSITE ) ).findAny().isPresent();
		}
		catch ( NullPointerException e )
		{
			throw e;
		}
	}
	
	public static boolean hasRedefinedProperty( boolean includeOverrides )
	{
		return hasMatchingProperty( includeOverrides, ( aClass, propertyNames ) -> aClass.getOwnedProperties().stream().filter( property -> property.getRedefiningProperty() != null && propertyNames.contains( property.getName() ) ).findAny().isPresent() );
	}

	public static boolean hasSubsettedProperty( boolean includeOverrides )
	{
		return hasMatchingProperty( includeOverrides, ( aClass, propertyNames ) -> aClass.getOwnedProperties().stream().filter( property -> !property.getSubsetttingProperties().isEmpty() && propertyNames.contains( property.getName() ) ).findAny().isPresent() );
	}
	
	public static boolean hasMatchingProperty( boolean includeOverrides, BiFunction< Class, Set< String >, Boolean > matchingFunction )
	{
		Class currentClass = getCurrentClass();
		Set< String > propertyNames = currentClass.getOwnedProperties().stream().map( property -> property.getName() ).collect( Collectors.toSet() );
		boolean hasMatchingProperty = false;
		
		while ( currentClass != null )
		{
			if ( hasMatchingProperty = matchingFunction.apply( currentClass, propertyNames ) )
				break;
			
			if ( includeOverrides )
				currentClass = currentClass.getSuperClass();
			else
				currentClass = null;
		}
		
		return hasMatchingProperty;
	}
	
	public static boolean isRedefinedProperty( boolean includeOverrides )
	{
		return isMatchingProperty( includeOverrides, ( aClass, propertyName ) -> aClass.getOwnedProperty( propertyName ) != null && aClass.getOwnedProperty( propertyName ).getRedefiningProperty() != null );
	}
	
	public static boolean isSubsettedProperty( boolean includeOverrides )
	{
		return isMatchingProperty( includeOverrides, ( aClass, propertyName ) -> aClass.getOwnedProperty( propertyName ) != null && !aClass.getOwnedProperty( propertyName ).getSubsetttingProperties().isEmpty() );
	}
	
	public static boolean isMatchingProperty( boolean includeOverrides, BiFunction< Class, String, Boolean > matchingFunction )
	{
		Class currentClass = getCurrentClass( SelectorContext.get() );
		String propertyName = getCurrentProperty().getName();
		boolean isMatchingProperty = false;
		
		while ( currentClass != null )
		{
			if ( isMatchingProperty = matchingFunction.apply( currentClass, propertyName ) )
				break;
			
			if ( includeOverrides )
				currentClass = currentClass.getSuperClass();
			else
				currentClass = null;
		}
		
		return isMatchingProperty;
	}
	
	public static Property getCurrentProperty()
	{
		if ( !( SelectorContext.get() instanceof PropertySelectorContext ) )
			throw new RuntimeException( "This selector can only be applied to properties." );
		
		PropertySelectorContext psContext = SelectorContext.get();
		Class theClass = getCurrentClass( psContext );
		Property property = theClass.getOwnedProperty( psContext.getCurrentProperty().getName() );
		
		return property;
	}

	public static Class getCurrentClass()
	{
		if ( !( SelectorContext.get() instanceof ClassSelectorContext ) )
			throw new RuntimeException( "This selector can only be applied to classes." );
		
		ClassSelectorContext csContext = SelectorContext.get();
		
		return getCurrentClass( csContext );
	}
	
	public static Class getCurrentClass( SelectorContext selectorContext )
	{
		String className = selectorContext.getCurrentClass().getName();
		Class theClass = AssociationRegistry.instance().getRegisteredClass( className );
		
		if ( theClass == null )
			throw new RuntimeException( String.format( "The class %s is not registered with %s.", className, AssociationRegistry.class.getName() ) );
		
		return theClass;
	}
	
	public static boolean isSubclassOf( java.lang.Class< ? > theClass )
	{
		return isAssignableTo( theClass ) && ! SelectorContext.get().getCurrentClass().equals( theClass );
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
