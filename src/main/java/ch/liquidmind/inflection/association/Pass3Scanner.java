package ch.liquidmind.inflection.association;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Pass3Scanner extends AbstractScanner
{
	public Pass3Scanner( Map< String, Class > classes )
	{
		super( classes );
	}

	public void scan()
	{
		getClasses().values().forEach( aClass -> setupClass( aClass ) );
	}
	
	private void setupClass( Class aClass )
	{
		setupProperties( aClass );
		setupAssociations( aClass );
	}

	private void setupProperties( Class aClass )
	{
		aClass.getOwnedProperties().forEach( property -> setupProperty( property ) );
	}
	
	private void setupProperty( Property property )
	{
		ch.liquidmind.inflection.association.annotations.Property propertyAnnotation = getPropertyAnnotation( property );
		property.setRedefinedProperty( determineRedefinedProperty( property, propertyAnnotation ) );
		property.setSubsettedProperty( determineSubsettedProperty( property, propertyAnnotation ) );
	}
	
	private Property determineRedefinedProperty( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		Property specifiedProperty = ( propertyAnnotation == null ? null : findProperty( property.getOwningClass(), propertyAnnotation.redefines() ) );
		Property overriddenProperty = findProperty( property.getOwningClass().getSuperClass(), property.getName() );
		
		if ( ( specifiedProperty != null && overriddenProperty != null ) && !specifiedProperty.equals( overriddenProperty ) )
			throw new RuntimeException( String.format( "Ambiguous redefinition for property %s.%s: property both overrides %s.%s and redefines %s.%s.", 
				property.getOwningClass().getName(), property.getName(), overriddenProperty.getOwningClass().getName(), overriddenProperty.getName(),
				 specifiedProperty.getOwningClass().getName(), specifiedProperty.getName() ) );
		
		if ( propertyAnnotation != null && !propertyAnnotation.redefines().isEmpty() && specifiedProperty == null )
			throw new RuntimeException( String.format( "Illegal redefinition for property %s.%s: specified property %s cannot be found.", 
				property.getOwningClass().getName(), property.getName(), propertyAnnotation.redefines() ) );

		Property redefinedProperty = ( specifiedProperty != null ? specifiedProperty : overriddenProperty );
		Property redefiningProperty = ( redefinedProperty == null ? null : redefinedProperty.getRedefiningProperty() );
		
		java.lang.Class< ? > classOfRedefiningProperty = ( redefiningProperty == null ? null : redefiningProperty.getOwningClass().getTargetClass() );
		java.lang.Class< ? > classOfProperty = property.getOwningClass().getTargetClass();
		
		if ( classOfRedefiningProperty != null && classOfRedefiningProperty.isAssignableFrom( classOfProperty ) )
			throw new RuntimeException( String.format( "Illegal redefinition for property %s.%s: specified property %s.%s is already redefined by %s.%s.",
				property.getOwningClass().getName(), property.getName(), redefinedProperty.getOwningClass().getName(), redefinedProperty.getName(),
				redefiningProperty.getOwningClass().getName(), redefiningProperty.getName() ) );
				
		if ( redefinedProperty != null && isFinal( redefinedProperty ) )
			throw new RuntimeException( String.format( "Illegal redefinition for final property %s.%s.",
				redefinedProperty.getOwningClass().getName(), redefinedProperty.getName() ) );
		
		return redefinedProperty;
	}
	
	private boolean isFinal( Property property )
	{
		PropertyDescriptor propertyDescriptor = property.getTargetProperty();
		Method readMethod = propertyDescriptor.getReadMethod();
		Method writeMethod = propertyDescriptor.getWriteMethod();
		boolean readMethodIsFinal = ( readMethod == null ? false : Modifier.isFinal( readMethod.getModifiers() ) );
		boolean writeMethodIsFinal = ( writeMethod == null ? false : Modifier.isFinal( writeMethod.getModifiers() ) );
		boolean isFinal = readMethodIsFinal || writeMethodIsFinal;
		
		return isFinal;
	}
	
	private Property determineSubsettedProperty( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		Property subsettedProperty = ( propertyAnnotation == null ? null : findProperty( property.getOwningClass(), propertyAnnotation.subsets() ) );
		
		if ( propertyAnnotation != null && !propertyAnnotation.subsets().isEmpty() && subsettedProperty == null )
			throw new RuntimeException( String.format( "Illegal subsetting for property %s.%s: specified property %s cannot be found.", 
				property.getOwningClass().getName(), property.getName(), propertyAnnotation.subsets() ) );

		return subsettedProperty;
	}
	
	private void setupAssociations( Class aClass )
	{
		setupClassLevelAssociations( aClass );
		setupPropertyLevelAssociations( aClass );
	}
	
	private void setupClassLevelAssociations( Class aClass )
	{
		ch.liquidmind.inflection.association.annotations.Association[] associationAnnotations = aClass.getTargetClass().getAnnotationsByType( ch.liquidmind.inflection.association.annotations.Association.class );
		Arrays.asList( associationAnnotations ).stream().forEach( associationAnnotation -> setupClassLevelAssociation( aClass, associationAnnotation ) );
	}
	
	@SuppressWarnings( "unused" )
	private void setupClassLevelAssociation( Class selfClass, ch.liquidmind.inflection.association.annotations.Association associationAnnotation )
	{
		Property selfProperty = findProperty( selfClass, associationAnnotation.selfEnd() );
		Class otherClass = getClasses().get( selfProperty.getRelatedClass().getName() );
		Property otherProperty = findProperty( otherClass, associationAnnotation.otherEnd() );
		String associationName = associationAnnotation.name();
		String associationDisplayName = ( associationName.isEmpty() ? "UNSPECIFIED" : associationName );
		
		if ( selfProperty == null )
			// TODO: figure out why eclipse marks this as dead code.
			throw new RuntimeException( String.format( "Illegal value for selfEnd in association %s.%s: specified property %s cannot be found", 
				selfClass.getName(), associationDisplayName, selfProperty.getName() ) );
		
		if ( otherProperty == null )
			throw new RuntimeException( String.format( "Illegal value for otherEnd in association %s.%s: specified property %s cannot be found", 
				selfClass.getName(), associationDisplayName, associationAnnotation.otherEnd() ) );
		
		Association association = new Association( associationName, selfProperty, otherProperty, true );
		
		if ( selfClass.getOwnedAssociations().contains( association ) )
			throw new RuntimeException( String.format( "Illegal association %s.%s: association already exists.", 
				selfClass.getName(), associationDisplayName, selfProperty.getName() ) );
		
		association.setOwningClass( selfClass );
	}
	
	private void setupPropertyLevelAssociations( Class aClass )
	{
		aClass.getOwnedProperties().stream().forEach( property -> setupPropertyLevelAssociations( property ) );
	}
	
	private void setupPropertyLevelAssociations( Property property )
	{
		Set< ch.liquidmind.inflection.association.annotations.Association > associationAnnotations = new HashSet< ch.liquidmind.inflection.association.annotations.Association >();
		PropertyDescriptor propertyDescriptor = property.getTargetProperty();
		Method readMethod = propertyDescriptor.getReadMethod();
		Method writeMethod = propertyDescriptor.getWriteMethod();
		associationAnnotations.addAll( ( readMethod == null ? new HashSet< ch.liquidmind.inflection.association.annotations.Association >() : Arrays.asList( readMethod.getAnnotationsByType( ch.liquidmind.inflection.association.annotations.Association.class ) ) ) );
		associationAnnotations.addAll( ( writeMethod == null ? new HashSet< ch.liquidmind.inflection.association.annotations.Association >() : Arrays.asList( writeMethod.getAnnotationsByType( ch.liquidmind.inflection.association.annotations.Association.class ) ) ) );
		associationAnnotations.stream().forEach( associationAnnotation -> setupPropertyLevelAssociation( property, associationAnnotation ) );
		
		if ( associationAnnotations.isEmpty() )
		{
			Association association = new Association( "", property, null, false );
			association.setOwningClass( property.getOwningClass() );
		}
	}
	
	private void setupPropertyLevelAssociation( Property selfProperty, ch.liquidmind.inflection.association.annotations.Association associationAnnotation )
	{
		Class selfClass = selfProperty.getOwningClass();
		Property specifiedSelfProperty = findProperty( selfClass, associationAnnotation.selfEnd() );
		String associationName = associationAnnotation.name();
		String associationDisplayName = ( associationName.isEmpty() ? "NA" : associationName );
		
		if ( !associationAnnotation.selfEnd().isEmpty() )
		{
			if ( specifiedSelfProperty == null )
				throw new RuntimeException( String.format( "Illegal association %s.%s: specified property %s cannot be found.",
					selfClass.getName(), associationDisplayName, associationAnnotation.selfEnd() ) );
			
			if ( !selfProperty.equals( specifiedSelfProperty ) )
				throw new RuntimeException( String.format( "Illegal association %s.%s: selfEnd %s must be either unspecified or the same as the associated property %s.",
					selfClass.getName(), associationDisplayName, selfProperty.getName() ) );
		}

		Class otherClass = getClasses().get( selfProperty.getRelatedClass().getName() );
		Property otherProperty = findProperty( otherClass, associationAnnotation.otherEnd() );
		
		if ( otherClass == null )
			throw new RuntimeException( String.format( "Illegal value for otherEnd in association %s.%s: specified class %s cannot be found.", 
				selfClass.getName(), associationDisplayName, selfProperty.getRelatedClass().getName() ) );
		
		if ( otherProperty == null )
			throw new RuntimeException( String.format( "Illegal value for otherEnd in association %s.%s: specified property %s cannot be found.", 
				selfClass.getName(), associationDisplayName, associationAnnotation.otherEnd() ) );
		
		Association association = new Association( associationName, selfProperty, otherProperty, true );
		
		if ( selfClass.getOwnedAssociations().contains( association ) )
			throw new RuntimeException( String.format( "Illegal association %s.%s: association already exists.", 
				selfClass.getName(), associationDisplayName, selfProperty.getName() ) );
		
		association.setOwningClass( selfClass );
	}
	
	private Property findProperty( Class aClass, String propertyName )
	{
		Property foundProperty = null;
		
		if ( aClass != null )
		{
			Optional< Property > optionalProperty = aClass.getOwnedProperties().stream().filter( property -> property.getName().equals( propertyName ) ).findFirst();
			foundProperty = ( optionalProperty.isPresent() ? optionalProperty.get() : findProperty( aClass.getSuperClass(), propertyName ) );
		}

		return foundProperty;
	}
}
