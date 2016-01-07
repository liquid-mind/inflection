package ch.liquidmind.inflection.bidir;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import __java.lang.reflect.__Field;

public class Link
{
	public static class LinkEnd
	{
		private Object object;
		private Field field;
		
		public LinkEnd( Object object, Field field )
		{
			super();
			this.object = object;
			this.field = field;
		}

		public Object getObject()
		{
			return object;
		}

		public Field getField()
		{
			return field;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ( ( field == null ) ? 0 : field.hashCode() );
			result = prime * result + ( ( object == null ) ? 0 : object.hashCode() );
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
			LinkEnd other = (LinkEnd)obj;
			if ( field == null )
			{
				if ( other.field != null )
					return false;
			}
			else if ( !field.equals( other.field ) )
				return false;
			if ( object == null )
			{
				if ( other.object != null )
					return false;
			}
			else if ( !object.equals( other.object ) )
				return false;
			return true;
		}
	}
	
	private LinkEnd linkEndA;
	private LinkEnd linkEndB;
	
	public Link( LinkEnd linkEndA, LinkEnd linkEndB )
	{
		super();
		this.linkEndA = linkEndA;
		this.linkEndB = linkEndB;
	}

	public LinkEnd getLinkEndA()
	{
		return linkEndA;
	}

	public LinkEnd getLinkEndB()
	{
		return linkEndB;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + linkEndA.hashCode() + linkEndB.hashCode();
		return result;
	}

	// Links are equal if the link ends are equal, even if they are reversed.
	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		Link other = (Link)obj;
		
		if ( ( linkEndA.equals( other.linkEndA ) && linkEndB.equals( other.linkEndB ) ) ||
			( linkEndA.equals( other.linkEndB ) && linkEndB.equals( other.linkEndA ) ) )
			return true;
		
		return false;
	}

	public static Set< Link > getLinks( Object objectA, Field fieldA )
	{
		Object valueA = __Field.get( fieldA, objectA );
		
		return getLinks( objectA, fieldA, valueA );
	}
	
	// valueA may be a simple reference or a collection and it may be the current/old or the new value.
	public static Set< Link > getLinks( Object objectA, Field fieldA, Object valueA )
	{
		Set< Link > links = new HashSet< Link >();
		
		if ( valueA != null )
		{
			Class< ? > classB = BidirectionalAspect.getOpposingClass( fieldA );
			Field fieldB = BidirectionalAspect.getOpposingField( fieldA, classB );
			
			if ( valueA instanceof Collection )
			{
				Collection< ? > collectionA = (Collection< ? >)valueA;
				
				for ( Object objectB : collectionA )
					links.add( new Link( new LinkEnd( objectA, fieldA ), new LinkEnd( objectB, fieldB ) ) );
			}
			else
			{
				links.add( new Link( new LinkEnd( objectA, fieldA ), new LinkEnd( valueA, fieldB ) ) );
			}
		}
		
		return links;
	}
	
	public static void removeLinks( Set< Link > links )
	{
		for ( Link link : links )
		{
			removeLinkEnd( link.getLinkEndA(), link.getLinkEndB() );
			removeLinkEnd( link.getLinkEndB(), link.getLinkEndA() );
		}
	}
	
	private static Object getValue( LinkEnd linkEnd )
	{
		return __Field.get( linkEnd.getField(), linkEnd.getObject() );
	}
	
	private static void removeLinkEnd( LinkEnd linkEndA, LinkEnd linkEndB )
	{
		Object valueA = getValue( linkEndA );
		
		if ( valueA instanceof Collection )
		{
			Collection< ? > collectionA = (Collection< ? >)valueA;
			collectionA.remove( linkEndB.getObject() );
		}
		else
		{
			BidirectionalAspect.setField( linkEndA.getField(), linkEndA.getObject(), null );
		}
	}
	
	public static void addLinks( Set< Link > links )
	{
		for ( Link link : links )
		{
			addLinkEnd( link.getLinkEndA(), link.getLinkEndB() );
			addLinkEnd( link.getLinkEndB(), link.getLinkEndA() );
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private static void addLinkEnd( LinkEnd linkEndA, LinkEnd linkEndB )
	{
		Object valueA = getValue( linkEndA );
		
		if ( valueA instanceof Collection )
		{
			Collection< Object > collectionA = (Collection< Object >)valueA;
			
			if ( !collectionA.contains( linkEndB.getObject() ) )
				collectionA.add( linkEndB.getObject() );
		}
		else
		{
			BidirectionalAspect.setField( linkEndA.getField(), linkEndA.getObject(), linkEndB.getObject() );
		}
	}
}
