package ch.liquidmind.inflection.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import __java.lang.__Class;
import __java.lang.__ClassLoader;
import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

// TODO: look into using weak-/soft references to manage memory.
public class ProxyRegistry
{
	private static final Map< Class< ? >, Class< ? > > PROXY_BASE_CLASSES = new HashMap< Class< ? >, Class< ? > >();
	public static final Map< Class< ? >, Class< ? > > COLLECTION_CLASSES = new HashMap< Class< ? >, Class< ? > >();
	
	static
	{
		PROXY_BASE_CLASSES.put( List.class, ListProxy.class );
		PROXY_BASE_CLASSES.put( Set.class, SetProxy.class );
		PROXY_BASE_CLASSES.put( Map.class, MapProxy.class );
		PROXY_BASE_CLASSES.put( Iterator.class, IteratorProxy.class );
		
		COLLECTION_CLASSES.put( ListProxy.class, ArrayList.class );
		COLLECTION_CLASSES.put( SetProxy.class, HashSet.class );
		COLLECTION_CLASSES.put( MapProxy.class, HashMap.class );
	}
	
	public static Class< ? > getProxyBaseClass( Class< ? > viewedClass )
	{
		Class< ? > proxyBaseClass = null;
		
		if ( viewedClass.isArray() )
			proxyBaseClass = ListProxy.class;
		else
			proxyBaseClass = PROXY_BASE_CLASSES.get( viewedClass );
		
		return proxyBaseClass;
	}
	
	private static ThreadLocal< ProxyRegistry > contextProxyRegistry = new ThreadLocal< ProxyRegistry >();
	
	private Map< Taxonomy, PairTables > pairTablesByTaxonomy = new HashMap< Taxonomy, PairTables >();
	
	public static class PairTables
	{
		// TODO: change Set< ProxyObjectPair > to List< ProxyObjectPair > (to avoid potentially overwriting objects in set; could
		// happen depending on given object's hashcode/equals implementation)
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

		Set< ProxyObjectPair > proxyObjectPairs = pairTables.getPairsByObjectHashcode().get( System.identityHashCode( object ) );
		
		if ( proxyObjectPairs == null )
		{
			proxyObjectPairs = new HashSet< ProxyObjectPair >();
			pairTables.getPairsByObjectHashcode().put( System.identityHashCode( object ), proxyObjectPairs );
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
				pairTables.getPairsByProxyHashcode().put( System.identityHashCode( proxy ), proxyObjectPairs );
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
		
		Set< Class< ? > > intersection = new HashSet< Class< ? > >( PROXY_BASE_CLASSES.keySet() );
		intersection.retainAll( getInterfacesRecursive( object.getClass() ) );
		
		if ( !intersection.isEmpty() )
		{
			if ( intersection.size() > 1 )
				throw new IllegalStateException( "intersection should contain exactly one element." );
			
			String proxyClassName = ProxyGenerator.getFullyQualifiedCollectionName( taxonomy, PROXY_BASE_CLASSES.get( intersection.iterator().next() ) );
			Class< ? > proxyClass = __ClassLoader.loadClass( Thread.currentThread().getContextClassLoader(), proxyClassName );
			proxy = (T)__Class.newInstance( proxyClass );
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
		
		PairTables pairTables = pairTablesByTaxonomy.get( Inflection.getTaxonomy( proxy ) );
		
		if ( pairTables == null )
		{
			pairTables = new PairTables();
			pairTablesByTaxonomy.put( Inflection.getTaxonomy( proxy ), pairTables );
		}
		
		Set< ProxyObjectPair > proxyObjectPairs = pairTables.getPairsByProxyHashcode().get( System.identityHashCode( proxy ) );
		
		if ( proxyObjectPairs == null )
		{
			proxyObjectPairs = new HashSet< ProxyObjectPair >();
			pairTables.getPairsByProxyHashcode().put( System.identityHashCode( proxy ), proxyObjectPairs );
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
			pairTables.getPairsByObjectHashcode().put( System.identityHashCode( proxyObjectPairFound.getObject() ), proxyObjectPairs );
		}
		
		return (T)proxyObjectPairFound.getObject();
	}
	
	// TODO: Currently constrained to creating instances of List, Set and Map;
	// need to consider cases in which target objects declare specific collection implementations.
	// TODO: Arrays are currently not supported.
	@SuppressWarnings( "unchecked" )
	private < T > T createObject( Proxy proxy )
	{
		Class< ? > objectClass = null;
		Set< Class< ? > > intersection = new HashSet< Class< ? > >( COLLECTION_CLASSES.keySet() );
		intersection.retainAll( getClassesRecursive( proxy.getClass() ) );
		
		if ( !intersection.isEmpty() )
		{
			if ( intersection.size() > 1 )
				throw new IllegalStateException( "intersection should contain exactly one element." );
			
			objectClass = COLLECTION_CLASSES.get( intersection.iterator().next() );
		}
		
		if ( objectClass == null )
			objectClass = Inflection.getView( proxy ).getViewedClass();
		
		return (T)__Class.newInstance( objectClass );
	}
	
	private List< Class< ? > > getClassesRecursive( Class< ? > aClass )
	{
		List< Class< ? > > classes = new ArrayList< Class< ? > >();
		classes.add( aClass );
		
		if ( aClass.getSuperclass() != null )
			classes.addAll( getClassesRecursive( aClass.getSuperclass() ) );
		
		return classes;
	}
	
	private List< Class< ? > > getInterfacesRecursive( Class< ? > aClass )
	{
		List< Class< ? > > interfaces = new ArrayList< Class< ? > >();
		interfaces.addAll( Arrays.asList( aClass.getInterfaces() ) );
		
		if ( aClass.getSuperclass() != null )
			interfaces.addAll( getInterfacesRecursive( aClass.getSuperclass() ) );
		
		return interfaces;
	}
}