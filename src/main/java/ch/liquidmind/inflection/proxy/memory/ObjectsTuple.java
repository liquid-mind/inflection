package ch.liquidmind.inflection.proxy.memory;

import ch.liquidmind.inflection.Auxiliary;
import ch.liquidmind.inflection.proxy.Proxy;
import ch.liquidmind.inflection.proxy.memory.TaxonomySpecificMemoryManager.ObjectType;

public class ObjectsTuple
{
	private Proxy proxy;
	private Object object;
	private Auxiliary auxiliary;
	
	public ObjectsTuple( Proxy proxy, Object object, Auxiliary auxiliary )
	{
		super();
		this.proxy = proxy;
		this.object = object;
		this.auxiliary = auxiliary;
	}

	@SuppressWarnings( "unchecked" )
	public < T > T getObject( ObjectType objectType )
	{
		T targetObject = null;
		
		if ( objectType.equals( ObjectType.Proxy ) )
			targetObject = (T)proxy;
		else if ( objectType.equals( ObjectType.Object ) )
			targetObject = (T)object;
		else if ( objectType.equals( ObjectType.Auxiliary ) )
			targetObject = (T)auxiliary;
		else
			throw new IllegalStateException( "Target object cannot be identified." );
		
		return targetObject;
	}
	
	public Proxy getProxy()
	{
		return proxy;
	}

	public Object getObject()
	{
		return object;
	}

	public Auxiliary getAuxiliary()
	{
		return auxiliary;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( auxiliary == null ) ? 0 : System.identityHashCode( auxiliary ) );
		result = prime * result + ( ( object == null ) ? 0 : System.identityHashCode( object ) );
		result = prime * result + ( ( proxy == null ) ? 0 : System.identityHashCode( proxy ) );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		ObjectsTuple other = (ObjectsTuple)obj;
		if ( auxiliary == null )
		{
			if ( other.auxiliary != null )
				return false;
		}
		else if ( !auxiliary.equals( other.auxiliary ) )
			return false;
		if ( object == null )
		{
			if ( other.object != null )
				return false;
		}
		else if ( !object.equals( other.object ) )
			return false;
		if ( proxy == null )
		{
			if ( other.proxy != null )
				return false;
		}
		else if ( !proxy.equals( other.proxy ) )
			return false;
		return true;
	}
}