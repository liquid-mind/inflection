package ch.liquidmind.inflection.support;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class RelatedTypeVisitor extends RelatingTypeVisitor
{
	private Type relatedType;
	
	@Override
	public void visitType( Type type )
	{
		relatedType = type;
		super.visitType( type );
		
		if ( relatedType instanceof Class && ( Collection.class.isAssignableFrom( (Class< ? >)relatedType ) || Map.class.isAssignableFrom( (Class< ? >)relatedType ) ) )
			relatedType = Object.class;
	}

	public Type getRelatedType()
	{
		return relatedType;
	}
}
