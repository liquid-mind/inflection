package ch.liquidmind.inflection.proxy;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

import __org.apache.commons.io.__IOUtils;
import ch.liquidmind.inflection.Auxiliary;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;

public class ProxyAgent implements ClassFileTransformer
{
	public static class VirtualObjectReference {}
	
	public static class ProxyOwnedVirtualObjectReference extends VirtualObjectReference
	{
		private Object viewedObject;
		private Auxiliary auxiliaryObject;
	}
	
	public static class AuxiliaryOwnedVirtualObjectReference extends VirtualObjectReference
	{
		private Object viewedObject;
		private Proxy proxyObject;
	}
	
	public static class ViewedObjectVirtualObjectReferences extends VirtualObjectReference
	{
		private Map< Taxonomy, ViewedObjectVirtualObjectReference > references = new HashMap< Taxonomy, ViewedObjectVirtualObjectReference >();
	}
	
	public static class ViewedObjectVirtualObjectReference extends VirtualObjectReference
	{
		private Object viewedObject;
		private Proxy proxyObject;
	}
	
	public static void premain( String agentArgs, Instrumentation instrumentation )
	{
		premainWithExceptionHandling( agentArgs, instrumentation );
	}
	
	public static void premainWithExceptionHandling( String agentArgs, Instrumentation instrumentation )
	{
		try
		{
			instrumentation.addTransformer( new ProxyAgent(), true );
			determineInstrumentableClassesInContext();
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
		}
	}
	
	public static class AgentClassLoader extends ClassLoader
	{
		private ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();

		public AgentClassLoader()
		{
			super( getExtensionsClassLoader() );
		}
		
		private static ClassLoader getExtensionsClassLoader()
		{
			ClassLoader extendsionsLoader = null;
			ClassLoader currentLoader = ClassLoader.getSystemClassLoader();
			
			while ( ( currentLoader = currentLoader.getParent() ) != null )
			{
				if ( currentLoader.getClass().getName().equals( "sun.misc.Launcher$ExtClassLoader" ) )
				{
					extendsionsLoader = currentLoader;
					break;
				}
			}
			
			return extendsionsLoader;
		}

		@Override
		protected Class< ? > loadClass( String name, boolean resolve ) throws ClassNotFoundException
		{
			Class< ? > aClass = parentLoadClass( name, resolve );
			
			if ( aClass == null )
				aClass = delegateLoadClass( name, resolve );
			
			return aClass;
		}
		
		private Class< ? > parentLoadClass( String name, boolean resolve )
		{
			Class< ? > aClass = null;
			
			try
			{
				aClass = super.loadClass( name, resolve );
			}
			catch ( ClassNotFoundException e )
			{
				// Ignore and return null.
			}
			
			return aClass;
		}
		
		private Class< ? > delegateLoadClass( String name, boolean resolve ) throws ClassNotFoundException
		{
			InputStream classStream = getResourceAsStream( name.replace( ".", File.separator ) + ".class" );
			byte[] binaryClass = __IOUtils.toByteArray( null, classStream );
			Class< ? > aClass = defineClass( name, binaryClass, 0, binaryClass.length );
			resolveClass( aClass );
			
			return aClass;
		}

		@Override
		public InputStream getResourceAsStream( String name )
		{
			return contextLoader.getResourceAsStream( name );
		}
	}
	
	private static Set< String > instrumentableClassNames;
	private static TaxonomyLoader loader;
	
	public static void determineInstrumentableClassesInContext() throws Throwable
	{
		ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
		
		try
		{
			AgentClassLoader agentClassLoader = new AgentClassLoader();
			Thread.currentThread().setContextClassLoader( agentClassLoader.getParent() );
			loader = new TaxonomyLoader( TaxonomyLoader.getSystemTaxonomyLoader(), agentClassLoader );
			instrumentableClassNames = determineInstrumentableClasses();
		}
		finally
		{
			Thread.currentThread().setContextClassLoader( contextLoader );
		}
	}
	
	public static Set< String > determineInstrumentableClasses() throws Throwable
	{
		Set< ResourceInfo > resources = ClassPath.from( ClassLoader.getSystemClassLoader() ).getResources();
		Set< Class< ? > > viewedClasses = new HashSet< Class< ? > >();
		resources.stream()
			.filter( resourceInfo -> resourceInfo.getResourceName().endsWith( ".tax" ) )
			.map( resourceInfo -> getTaxonomy( resourceInfo.getResourceName() ) )
			.filter( taxonomy -> taxonomy != null )
			.forEach( taxonomy -> viewedClasses.addAll( getViewedClasses( taxonomy ) ) );
		Set< String > instrumentableClassNames = viewedClasses.stream()
			.filter( viewedClass -> isTopLevelClass( viewedClass, viewedClasses ) )
			.map( viewedClass -> viewedClass.getName() )
			.collect( Collectors.toSet() );
		
		return instrumentableClassNames;
	}
	
	private static boolean isTopLevelClass( Class< ? > viewedClass, Set< Class< ? > > viewedClasses )
	{
		Set< Class< ? > > ancestors = getAncestors( viewedClass );
		ancestors.retainAll( viewedClasses );
		
		return ancestors.isEmpty();
	}
	
	private static Set< Class< ? > > getAncestors( Class< ? > aClass )
	{
		Set< Class< ? > > ancestors = new HashSet< Class< ? > >();
		Class< ? > currentClass = aClass;
		
		while ( ( currentClass = currentClass.getSuperclass() ) != null )
			ancestors.add( currentClass );
		
		return ancestors;
	}
	
	private static Set< Class< ? > > getViewedClasses( Taxonomy taxonomy )
	{
		return taxonomy.getViews().stream().map( view -> view.getViewedClass() ).collect( Collectors.toSet() );
	}
	
	private static Taxonomy getTaxonomy( String taxonomyResourceName )
	{
		String taxonomyName = taxonomyResourceName.substring( 0, taxonomyResourceName.length() - ".tax".length() ).replace( File.separator, "." );
		Taxonomy taxonomy = null;
		
		try
		{
			taxonomy = loader.loadTaxonomy( taxonomyName );
		}
		catch ( Throwable t )
		{
			if ( t instanceof Error )
				throw t;
		}
		
		return taxonomy;
	}
	
	@Override
	public byte[] transform( ClassLoader loader, String className, Class< ? > classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer ) throws IllegalClassFormatException
	{
		byte[] byteCode = classfileBuffer;
		className = className.replace( "/", "." );

		if ( instrumentableClassNames.contains( className ) )
		{
			try
			{
				ClassPool classPool = ClassPool.getDefault();
				CtClass theClass = classPool.get( className );
				CtClass voReference = classPool.get( ViewedObjectVirtualObjectReferences.class.getName() );
				CtField field = new CtField( voReference, ProxyAgent.class.getPackage().getName() + ".virtualObjectReferences", theClass );
				field.setModifiers( Modifier.PRIVATE );
				theClass.addField( field );
				byteCode = theClass.toBytecode();
			}
			catch ( Exception e )
			{
				throw new RuntimeException( e );
			}
		}

		return byteCode;
	}
}
