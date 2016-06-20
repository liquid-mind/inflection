package ch.liquidmind.inflection.association;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;

public abstract class AbstractScanner
{
	private Map< String, Class > classes;
	
	public AbstractScanner( Map< String, Class > classes )
	{
		super();
		this.classes = classes;
	}

	public Map< String, Class > getClasses()
	{
		return classes;
	}

	protected ch.liquidmind.inflection.association.annotations.Property getPropertyAnnotation( Property property )
	{
		PropertyDescriptor descriptor = property.getTargetProperty();
		Method readMethod = descriptor.getReadMethod();
		Method writeMethod = descriptor.getWriteMethod();
		ch.liquidmind.inflection.association.annotations.Property readMethodAnnotation = ( readMethod == null ? null : readMethod.getAnnotation( ch.liquidmind.inflection.association.annotations.Property.class ) );
		ch.liquidmind.inflection.association.annotations.Property writeMethodAnnotation = ( writeMethod == null ? null : writeMethod.getAnnotation( ch.liquidmind.inflection.association.annotations.Property.class ) );
		
		if ( readMethodAnnotation != null && writeMethodAnnotation != null )
			throw new RuntimeException( String.format( "Ambiguous usage of %s for property %s in class %s: annotation must not be declared on both read and write methods.",
				ch.liquidmind.inflection.association.annotations.Property.class.getSimpleName(), property.getName(), property.getOwningClass().getName() ) );
			
		ch.liquidmind.inflection.association.annotations.Property propertyAnnotation = ( readMethodAnnotation != null ? readMethodAnnotation : writeMethodAnnotation );
		
		return propertyAnnotation;
	}
}
