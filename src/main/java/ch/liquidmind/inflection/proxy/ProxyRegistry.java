package ch.liquidmind.inflection.proxy;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import __java.lang.__ClassLoader;
import __java.lang.reflect.__Constructor;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

// TODO: look into using weak-/soft references to manage memory.
public class ProxyRegistry
{
	// TODO: put this in a different location: also used by ProxyGenerator.
	public static final Map< Class< ? >, Class< ? > > collectionsByProxy = new HashMap< Class< ? >, Class< ? > >();
	public static final Map< Class< ? >, Class< ? > > proxiesByCollection = new HashMap< Class< ? >, Class< ? > >();
	
	static
	{
		collectionsByProxy.put( ListProxy.class, ArrayList.class );
		collectionsByProxy.put( SetProxy.class, HashSet.class );
		collectionsByProxy.put( MapProxy.class, HashMap.class );
		
		proxiesByCollection.put( ArrayList.class, ListProxy.class );
		proxiesByCollection.put( HashSet.class, SetProxy.class );
		proxiesByCollection.put( HashMap.class, MapProxy.class );
		proxiesByCollection.put( List.class, ListProxy.class );
		proxiesByCollection.put( Set.class, SetProxy.class );
		proxiesByCollection.put( Map.class, MapProxy.class );
	}
	
	private static ThreadLocal< ProxyRegistry > contextProxyRegistry = new ThreadLocal< ProxyRegistry >();
	
	private Map< Taxonomy, PairTables > pairTablesByTaxonomy = new HashMap< Taxonomy, PairTables >();
	
	public static class PairTables
	{
		private Map< Integer, Set< ProxyObjectPair > > pairsByObjectHashcode = new HashMap< Integer, Set< ProxyObjectPair > >();
		private Map< Integer, Set< ProxyObjectPair > > pairsByProxyHashcode = new HashMap< Integer, Set< ProxyObjectPair > >();
		
		public Map< Integer, Set< ProxyObjectPair > > getPairsByObjectHashcode()
		{
			return pairsByObjectHashcode;
		}
		
		public Map< Integer, Set< ProxyObjectPair > > getPairsByProxyHashcode()
		{
			return pairsByProxyHashcode;
		}
	}
	
	public static class ProxyObjectPair
	{
		private Proxy proxy;
		private Object object;
		
		public ProxyObjectPair( Proxy proxy, Object object )
		{
			super();
			this.proxy = proxy;
			this.object = object;
		}

		public Proxy getProxy()
		{
			return proxy;
		}

		public void setProxy( Proxy proxy )
		{
			this.proxy = proxy;
		}

		public Object getObject()
		{
			return object;
		}

		public void setObject( Object object )
		{
			this.object = object;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ( ( object == null ) ? 0 : object.hashCode() );
			result = prime * result + ( ( proxy == null ) ? 0 : proxy.hashCode() );
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
			ProxyObjectPair other = (ProxyObjectPair)obj;
			if ( object == null )
			{
				if ( other.object != null )
					return false;
			}
			else if ( object != other.object )
				return false;
			if ( proxy == null )
			{
				if ( other.proxy != null )
					return false;
			}
			else if ( proxy != other.proxy )
				return false;
			return true;
		}
	}
	
	public static ProxyRegistry getContextProxyRegistry()
	{
		if ( contextProxyRegistry.get() == null )
			contextProxyRegistry.set( new ProxyRegistry() );
		
		return contextProxyRegistry.get();
	}
	
	@SuppressWarnings( "unchecked" )
	public < T extends Proxy > T getProxy( Taxonomy taxonomy, Object object )
	{
		if ( object == null )
			return null;
		
		PairTables pairTables = pairTablesByTaxonomy.get( taxonomy );
		
		if ( pairTables == null )
		{
			pairTables = new PairTables();
			pairTablesByTaxonomy.put( taxonomy, pairTables );
		}

		Set< ProxyObjectPair > proxyObjectPairs = pairTables.getPairsByObjectHashcode().get( object.hashCode() );
		
		if ( proxyObjectPairs == null )
		{
			proxyObjectPairs = new HashSet< ProxyObjectPair >();
			pairTables.getPairsByObjectHashcode().put( object.hashCode(), proxyObjectPairs );
		}
		
		ProxyObjectPair proxyObjectPairFound = null;
		
		for ( ProxyObjectPair proxyObjectPair : proxyObjectPairs )
		{
			if ( proxyObjectPair.getObject() == object )
			{
				proxyObjectPairFound = proxyObjectPair;
				break;
			}
		}

		T proxy = null;
		
		if ( proxyObjectPairFound == null )
		{
			proxy = createProxy( taxonomy, object );
			
			if ( proxy != null )
			{
				proxyObjectPairFound = new ProxyObjectPair( proxy, object );
				proxyObjectPairs.add( proxyObjectPairFound );
				pairTables.getPairsByProxyHashcode().put( proxy.hashCode(), proxyObjectPairs );
			}
		}
		else
		{
			proxy = (T)proxyObjectPairFound.getProxy();
		}
		
		return proxy;
	}
	
	// TODO: currently constrained to one-dimensional collections; thus, e.g., 
	// List< List< T > > is not considered. Fix this.
	@SuppressWarnings( "unchecked" )
	private < T extends Proxy > T createProxy( Taxonomy taxonomy, Object object )
	{
		T proxy = null;
		
		if ( proxiesByCollection.containsKey( object.getClass() ) )
		{
			Class< ? > proxyClass = proxiesByCollection.get( object.getClass() );
			Constructor< ? > constructor = __Class.getDeclaredConstructor( proxyClass, String.class );
			constructor.setAccessible( true );
			proxy = (T)__Constructor.newInstance( constructor, taxonomy.getName() );
		}
		else
		{
			View view = taxonomy.resolveView( object.getClass() );
			
			if ( view != null )
			{
				String proxyClassName = ProxyGenerator.getFullyQualifiedViewName( taxonomy, view );
				Class< ? > proxyClass = __ClassLoader.loadClass( taxonomy.getTaxonomyLoader().getClassLoader(), proxyClassName );
				proxy = (T)__Class.newInstance( proxyClass );
			}
		}
		
		return proxy;
	}
	
	@SuppressWarnings( "unchecked" )
	public < T extends Object > T getObject( Proxy proxy )
	{
		if ( proxy == null )
			return null;
		
		PairTables pairTables = pairTablesByTaxonomy.get( proxy.getTaxonomy() );
		
		if ( pairTables == null )
		{
			pairTables = new PairTables();
			pairTablesByTaxonomy.put( proxy.getTaxonomy(), pairTables );
		}
		
		Set< ProxyObjectPair > proxyObjectPairs = pairTables.getPairsByProxyHashcode().get( proxy.hashCode() );
		
		if ( proxyObjectPairs == null )
		{
			proxyObjectPairs = new HashSet< ProxyObjectPair >();
			pairTables.getPairsByProxyHashcode().put( proxy.hashCode(), proxyObjectPairs );
		}		
		
		ProxyObjectPair proxyObjectPairFound = null;
		
		for ( ProxyObjectPair proxyObjectPair : proxyObjectPairs )
		{
			if ( proxyObjectPair.getProxy() == proxy )
			{
				proxyObjectPairFound = proxyObjectPair;
				break;
			}
		}

		if ( proxyObjectPairFound == null )
		{
			proxyObjectPairFound = new ProxyObjectPair( proxy, createObject( proxy ) );
			proxyObjectPairs.add( proxyObjectPairFound );
			pairTables.getPairsByObjectHashcode().put( proxyObjectPairFound.getObject().hashCode(), proxyObjectPairs );
		}
		
		return (T)proxyObjectPairFound.getObject();
	}
	
	// TODO: Currently constrained to creating instances of List, Set and Map;
	// need to consider cases in which target objects declare specific collection implementations.
	// TODO: Arrays are currently not supported.
	@SuppressWarnings( "unchecked" )
	private < T > T createObject( Proxy proxy )
	{
		Class< ? > objectClass = collectionsByProxy.get( proxy.getClass() );
		
		if ( objectClass == null )
			objectClass = proxy.getView().getViewedClass();
		
		return (T)__Class.newInstance( objectClass );
	}
}
