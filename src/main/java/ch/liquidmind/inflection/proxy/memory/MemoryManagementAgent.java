package ch.liquidmind.inflection.proxy.memory;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ResourceInfo;

import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;

public class MemoryManagementAgent implements ClassFileTransformer
{
	private static Set< String > instrumentableClassNames;
	private static boolean agentActive = false;

	public static void premain( String agentArgs, Instrumentation instrumentation )
	{
		premainWithExceptionHandling( agentArgs, instrumentation );
	}
	
	public static void premainWithExceptionHandling( String agentArgs, Instrumentation instrumentation )
	{
		try
		{
			instrumentableClassNames = determineInstrumentableClasses();
			instrumentation.addTransformer( new MemoryManagementAgent() );
			agentActive = true;
		}
		catch ( Throwable t )
		{
			t.printStackTrace();
		}
	}
	
	public static Set< String > determineInstrumentableClasses() throws Throwable
	{
		TaxonomyLoader loader = new AgentTaxonomyLoader();
		Set< ResourceInfo > resources = ClassPath.from( loader.getClassLoader() ).getResources();
		Set< Class< ? > > viewedClasses = new HashSet< Class< ? > >();
		resources.stream()
			.filter( resourceInfo -> resourceInfo.getResourceName().endsWith( ".tax" ) )
			.map( resourceInfo -> getTaxonomy( loader, resourceInfo.getResourceName() ) )
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
	
	private static Taxonomy getTaxonomy( TaxonomyLoader loader, String taxonomyResourceName )
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
				CtField field = new CtField( voReference, MemoryManagementAgent.class.getPackage().getName() + ".virtualObjectReference", theClass );
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

	public static boolean isAgentActive()
	{
		return agentActive;
	}
}
