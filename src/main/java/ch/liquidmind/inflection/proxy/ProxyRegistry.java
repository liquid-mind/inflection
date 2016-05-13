package ch.liquidmind.inflection.proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	
	private Map< Taxonomy, Tuples > tuplesByTaxonomy = new HashMap< Taxonomy, Tuples >();
	
	public static class Tuples
	{
		private Taxonomy taxonomy;
		private Map< Integer, ObjectsTuple > objectsTuples = new HashMap< Integer, ObjectsTuple >();
		private Map< Class< ? >, ClassesTuple > classesTuples = new HashMap< Class< ? >, ClassesTuple >();
		
		public Tuples( Taxonomy taxonomy )
		{
			this.taxonomy = taxonomy;
		}

		private ObjectsTuple getObjectTuple( Object key )
		{
			ObjectsTuple objectsTuple = objectsTuples.get( key );
			
			if ( objectsTuple == null )
			{
				objectsTuple = createObjectTuple( key );
				objectsTuples.put( System.identityHashCode( objectsTuple.getObject() ), objectsTuple );
				objectsTuples.put( System.identityHashCode( objectsTuple.getProxy() ), objectsTuple );
				objectsTuples.put( System.identityHashCode( objectsTuple.getAuxiliary() ), objectsTuple );
			}
			
			return objectsTuple;
		}
		
		private ObjectsTuple createObjectTuple( Object object )
		{
			Class< ? > theClass = object.getClass();
			ClassesTuple classesTuple = getClassesTuple( theClass );
			
			Proxy proxy = determineObject( theClass, classesTuple.getProxyClass(), object );
			Object viewableObject = determineObject( theClass, classesTuple.getObjectClass(), object );
			Object auxiliary = determineObject( theClass, classesTuple.getProxyClass(), object );
			
			ObjectsTuple objectsTuple = new ObjectsTuple( proxy, viewableObject, auxiliary );
			
			return objectsTuple;
		}
		
		@SuppressWarnings( "unchecked" )
		private < T > T determineObject( Class< ? > classA, Class< ? > classB, Object objectA )
		{
			return (T)( classA.equals( classB ) ? objectA : __Class.newInstance( classB ) );
		}
		
		private ClassesTuple getClassesTuple( Class< ? > key )
		{
			ClassesTuple classesTuple = classesTuples.get( key );
			
			if ( classesTuple == null )
			{
				classesTuple = createClassesTuple( key );
				classesTuples.put( classesTuple.getProxyClass(), classesTuple );
				classesTuples.put( classesTuple.getObjectClass(), classesTuple );
				classesTuples.put( classesTuple.getAuxiliaryClass(), classesTuple );
			}
			
			return classesTuple;
		}
		
		private ClassesTuple createClassesTuple( Class< ? > aClass )
		{
			ClassesTuple classTuple = null;
			
			if ( Collection.class.isAssignableFrom( aClass ) || Map.class.isAssignableFrom( aClass ) )
				classTuple = createClassesTupleFromCollection( aClass );
			else
				classTuple = createClassesTupleFromNonCollection( aClass );

			return classTuple;
		}
		
		private ClassesTuple createClassesTupleFromCollection( Class< ? > aClass )
		{
			ClassesTuple classesTuple;
			
			if ( Proxy.class.isAssignableFrom( aClass ) )
				classesTuple = createClassesTupleFromProxyCollection( aClass );
			else
				classesTuple = createClassesTupleFromNonProxyCollection( aClass );
			
			return classesTuple;
		}
		
		private ClassesTuple createClassesTupleFromProxyCollection( Class< ? > proxyClass )
		{
			Class< ? > objectClass = null;
			Set< Class< ? > > intersection = new HashSet< Class< ? > >( COLLECTION_CLASSES.keySet() );
			intersection.retainAll( getClassesRecursive( proxyClass ) );
			Class< ? > proxyInterface = intersection.iterator().next();

			if ( intersection.size() != 1 )
				throw new IllegalStateException( "intersection should contain exactly one element." );
			
			objectClass = COLLECTION_CLASSES.get( proxyInterface );
			ClassesTuple classesTuple = new ClassesTuple( proxyClass, objectClass, null );
			
			return classesTuple;
		}
		
		private ClassesTuple createClassesTupleFromNonProxyCollection( Class< ? > objectClass )
		{
			Set< Class< ? > > intersection = new HashSet< Class< ? > >( PROXY_BASE_CLASSES.keySet() );
			intersection.retainAll( getInterfacesRecursive( objectClass ) );
			
			if ( intersection.size() != 1 )
				throw new IllegalStateException( "intersection should contain exactly one element." );
			
			Class< ? > objectInterface = intersection.iterator().next();
			String proxyClassName = ProxyGenerator.getFullyQualifiedCollectionName( taxonomy, PROXY_BASE_CLASSES.get( objectInterface ) );
			Class< ? > proxyClass = __ClassLoader.loadClass( Thread.currentThread().getContextClassLoader(), proxyClassName );
			ClassesTuple classesTuple = new ClassesTuple( proxyClass, objectClass, null );
			
			return classesTuple;
		}
		
		@SuppressWarnings( "unchecked" )
		private ClassesTuple createClassesTupleFromNonCollection( Class< ? > aClass )
		{
			View correspondingView;
			
			if ( Proxy.class.isAssignableFrom( aClass ) )
				correspondingView = Inflection.getView( (Class< Proxy >)aClass );
			else
				correspondingView = taxonomy.getViews().stream().filter( x -> x.getViewedClass().equals( aClass ) || x.getUsedClass().equals( aClass ) ).findFirst().get();
		
			Class< ? > objectClass = correspondingView.getViewedClass();
			Class< ? > auxiliaryClass = correspondingView.getUsedClass();
			String proxyClassName = ProxyGenerator.getFullyQualifiedViewName( taxonomy, correspondingView );
			Class< ? > proxyClass = __ClassLoader.loadClass( taxonomy.getTaxonomyLoader().getClassLoader(), proxyClassName );
			
			ClassesTuple classesTuple = new ClassesTuple( proxyClass, objectClass, auxiliaryClass );
			
			return classesTuple;
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
	
	public static class ObjectsTuple
	{
		private Proxy proxy;
		private Object object;
		private Object auxiliary;
		
		public ObjectsTuple( Proxy proxy, Object object, Object auxiliary )
		{
			super();
			this.proxy = proxy;
			this.object = object;
			this.auxiliary = auxiliary;
		}

		public Proxy getProxy()
		{
			return proxy;
		}

		public Object getObject()
		{
			return object;
		}

		public Object getAuxiliary()
		{
			return auxiliary;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ( ( auxiliary == null ) ? 0 : auxiliary.hashCode() );
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

	public static class ClassesTuple
	{
		private Class< ? > proxyClass;
		private Class< ? > objectClass;
		private Class< ? > auxiliaryClass;
		
		public ClassesTuple( Class< ? > proxyClass, Class< ? > objectClass, Class< ? > auxiliaryClass )
		{
			super();
			this.proxyClass = proxyClass;
			this.objectClass = objectClass;
			this.auxiliaryClass = auxiliaryClass;
		}

		public Class< ? > getProxyClass()
		{
			return proxyClass;
		}

		public Class< ? > getObjectClass()
		{
			return objectClass;
		}

		public Class< ? > getAuxiliaryClass()
		{
			return auxiliaryClass;
		}
	}
	
	public static ProxyRegistry getContextProxyRegistry()
	{
		if ( contextProxyRegistry.get() == null )
			contextProxyRegistry.set( new ProxyRegistry() );
		
		return contextProxyRegistry.get();
	}

	public < T > T getObject( Class< T > targetClass, Proxy key )
	{
		return getObject( targetClass, Inflection.getTaxonomy( key ), key );
	}
	
	public < T > T getObject( Class< T > targetClass, Taxonomy taxonomy, Object key )
	{
		Tuples tuples = getTuples( taxonomy );
		ObjectsTuple tuple = tuples.getObjectTuple( key );
		T targetObject = getObject( tuple, targetClass );
		
		return (T)targetObject;
	}
	
	private Tuples getTuples( Taxonomy taxonomy )
	{
		Tuples tuples = tuplesByTaxonomy.get( taxonomy );
		
		if ( tuples == null )
		{
			tuples = new Tuples( taxonomy );
			tuplesByTaxonomy.put( taxonomy, tuples );
		}
		
		return tuples;
	}
	
	@SuppressWarnings( "unchecked" )
	private < T > T getObject( ObjectsTuple tuple, Class< ? > targetClass )
	{
		T targetObject = null;
		
		if ( tuple.getProxy().getClass().equals( targetClass ) )
			targetObject = (T)tuple.getProxy();
		else if ( tuple.getObject().getClass().equals( targetClass ) )
			targetObject = (T)tuple.getObject();
		else if ( tuple.getAuxiliary().getClass().equals( targetClass ) )
			targetObject = (T)tuple.getAuxiliary();
		else
			throw new IllegalStateException( "Target object cannot be identified." );
		
		return targetObject;
	}
}