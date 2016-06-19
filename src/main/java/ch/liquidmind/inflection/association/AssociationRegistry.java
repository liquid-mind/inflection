package ch.liquidmind.inflection.association;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.reflect.ClassPath.ClassInfo;

import __java.beans.__PropertyDescriptor;
import ch.liquidmind.inflection.exception.ExceptionWrapper;
import ch.liquidmind.inflection.support2.RelatedTypeVisitor;
import ch.liquidmind.inflection.support2.TypeWalker;

public class AssociationRegistry
{
	private static Set< Class > registeredClasses;
	
	public static void scan( String ... classNameFilters )
	{
		scan( Thread.currentThread().getContextClassLoader(), classNameFilters );
	}
	
	public static void scan( ClassLoader loader, String ... classNameFilters )
	{
		Set< ClassInfo > allClasses = ExceptionWrapper.ClassPath_from( loader ).getAllClasses();
		Set< ClassInfo > filteredClasses = allClasses.stream().filter( classInfo -> classInfoMatchesFilter( classInfo, classNameFilters ) ).collect( Collectors.toSet() );
		
		scanPass1( filteredClasses );
		scanPass2();
	}
	
	private static boolean classInfoMatchesFilter( ClassInfo classInfo, String[] classNameFilters )
	{
		return Arrays.asList( classNameFilters ).stream().filter(
			classNameFilter -> classInfo.getName().matches( classNameFilter ) ).findFirst().isPresent();
	}
	
	private static void scanPass1( Set< ClassInfo > classes )
	{
		registeredClasses = classes.stream().map( classInfo -> createClass( classInfo ) ).collect( Collectors.toSet() );
	}
	
	private static Class createClass( ClassInfo classInfo )
	{
		java.lang.Class< ? > aClass = classInfo.load();
		
		Set< Property > properties = createProperties( aClass );
		Class createdClass = new Class( aClass, properties );
		
		return createdClass;
	}
	
	private static Set< Property > createProperties( java.lang.Class< ? > aClass )
	{
		Set< PropertyDescriptor > javaBeanProperties = getDeclaredJavaBeanProperties( aClass );
		Set< Property > properties = javaBeanProperties.stream().map( property -> new Property( property ) ).collect( Collectors.toSet() );
		
		return properties;
	}

	private static Set< PropertyDescriptor > getDeclaredJavaBeanProperties( java.lang.Class< ? > declaringClass )
	{
		PropertyDescriptor[] allProperties = PropertyUtils.getPropertyDescriptors( declaringClass );
		List< PropertyDescriptor > allPropertiesAsList = Arrays.asList( allProperties );
		allPropertiesAsList.forEach( property -> removeInheritedMethods( declaringClass, property ) );
		Set< PropertyDescriptor > declaredProperties = allPropertiesAsList.stream().filter( property -> !isPropertyEmpty( property ) ).collect( Collectors.toSet() );
		
		return declaredProperties;
	}
	
	private static void removeInheritedMethods( java.lang.Class< ? > inheritingClass, PropertyDescriptor property )
	{
		if ( property.getWriteMethod() != null && !property.getWriteMethod().getDeclaringClass().equals( inheritingClass ) )
			__PropertyDescriptor.setWriteMethod( property, null );
		if ( property.getReadMethod() != null && !property.getReadMethod().getDeclaringClass().equals( inheritingClass ) )
			__PropertyDescriptor.setReadMethod( property, null );
	}
	
	private static boolean isPropertyEmpty( PropertyDescriptor property )
	{
		return property.getReadMethod() == null && property.getWriteMethod() == null;
	}
	
	private static void scanPass2()
	{
		registeredClasses.forEach( aClass -> setupClass( aClass ) );
	}
	
	private static void setupClass( Class aClass )
	{
		setupProperties( aClass );
		setupAssociations( aClass );
	}
	
	private static void setupProperties( Class aClass )
	{
		aClass.getOwnedProperties().forEach( property -> setupProperty( property ) );
	}
	
	private static void setupProperty( Property property )
	{
		ch.liquidmind.inflection.association.annotations.Property propertyAnnotation = getPropertyAnnotation( property );
		property.setRelatedType( determineRelatedType( property, propertyAnnotation ) );
		property.setAggregation( determineAggregation( property, propertyAnnotation ) );
		// This goes in the second pass
//		property.setRedefinedProperty( determineRedefinedProperty( property, propertyAnnotation ) );
//		property.setSubsettedProperty( determineSubsettedProperty( property, propertyAnnotation ) );
		property.setDimensions( determineDimensions( property, propertyAnnotation ) );
		property.setDerived( propertyAnnotation.isDerived() );
		property.setDerivedUnion( propertyAnnotation.isDerivedUnion() );
		property.setDeclared( propertyAnnotation != null );
	}
	
	private static Type determineRelatedType( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		Type propertyType = getPropertyType( property );
		
		RelatedTypeVisitor visitor = new RelatedTypeVisitor();
		TypeWalker walker = new TypeWalker( visitor );
		walker.walk( propertyType );
		Type relatedType = visitor.getRelatedType();
		
		if ( relatedType instanceof java.lang.Class )
		{
			java.lang.Class< ? > relatedClass = (java.lang.Class< ? >)relatedType;
			
			if ( relatedClass.equals( Object.class ) )
				throw new RuntimeException( "The related type of " + property.getTargetProperty().getName() + " is java.lang.Object; the related type must be more specific (i.e., a sub class of java.lang.Object)." );
		}
		
		return relatedType;
	}
	
	private static Type getPropertyType( Property property )
	{
		PropertyDescriptor descriptor = property.getTargetProperty();
		Method readMethod = descriptor.getReadMethod();
		Method writeMethod = descriptor.getWriteMethod();
		Type propertyType;
		
		if ( readMethod != null )
			propertyType = readMethod.getGenericReturnType();
		else if ( writeMethod != null )
			propertyType = writeMethod.getGenericParameterTypes()[ 0 ];
		else
			throw new IllegalStateException( "descriptor does not contain a read or write method." );
		
		return propertyType;
	}

	private static Aggregation determineAggregation( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		return ( propertyAnnotation == null ? Aggregation.COMPOSITE : propertyAnnotation.aggregation() );
	}
	
	private static Property determineRedefinedProperty( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		return null;
	}
	
	private static Property determineSubsettedProperty( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		return null;
	}
	
	private static List< Dimension > determineDimensions( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		return null;
	}
	
	private static ch.liquidmind.inflection.association.annotations.Property getPropertyAnnotation( Property property )
	{
		PropertyDescriptor descriptor = property.getTargetProperty();
		Method readMethod = descriptor.getReadMethod();
		Method writeMethod = descriptor.getWriteMethod();
		ch.liquidmind.inflection.association.annotations.Property readMethodAnnotation = readMethod.getAnnotation( ch.liquidmind.inflection.association.annotations.Property.class );
		ch.liquidmind.inflection.association.annotations.Property writeMethodAnnotation = writeMethod.getAnnotation( ch.liquidmind.inflection.association.annotations.Property.class );
		
		if ( readMethodAnnotation != null && writeMethodAnnotation != null )
			throw new RuntimeException( String.format( "Ambiguous usage of @%1$s for property %2$s in class %3$s: annotation must not be declared on both read and write methods.",
				ch.liquidmind.inflection.association.annotations.Property.class.getSimpleName(), property.getName(), property.getOwningClass().getName() ) );
			
		ch.liquidmind.inflection.association.annotations.Property propertyAnnotation = ( readMethodAnnotation != null ? readMethodAnnotation : writeMethodAnnotation );
		
		return propertyAnnotation;
	}
	
	private static void setupAssociations( Class aClass )
	{}
	
	public Set< Class > getRegisteredClasses()
	{
		return registeredClasses;
	}
}
