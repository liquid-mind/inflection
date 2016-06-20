package ch.liquidmind.inflection.association;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public abstract class AbstractScanner
{
	protected ch.liquidmind.inflection.association.annotations.Property getPropertyAnnotation( Property property )
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
}
