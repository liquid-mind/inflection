package ch.liquidmind.inflection.loader;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import __java.lang.__Class;
import ch.liquidmind.inflection.model.AccessType;
import ch.liquidmind.inflection.model.compiled.AnnotatableElementCompiled;
import ch.liquidmind.inflection.model.compiled.AnnotationCompiled;
import ch.liquidmind.inflection.model.compiled.MemberCompiled;
import ch.liquidmind.inflection.model.compiled.TaxonomyCompiled;
import ch.liquidmind.inflection.model.compiled.ViewCompiled;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.linked.AnnotatableElementLinked;
import ch.liquidmind.inflection.model.linked.FieldLinked;
import ch.liquidmind.inflection.model.linked.MemberLinked;
import ch.liquidmind.inflection.model.linked.PropertyLinked;
import ch.liquidmind.inflection.model.linked.TaxonomyLinked;
import ch.liquidmind.inflection.model.linked.UnparsedAnnotation;
import ch.liquidmind.inflection.model.linked.ViewLinked;

public class TaxonomyLoader
{
	private static TaxonomyLoader systemTaxonomyLoader;
	private static ThreadLocal< TaxonomyLoader > contextTaxonomyLoader = new ThreadLocal< TaxonomyLoader >();

	public static final Map< String, Class< ? > > BASIC_TYPE_MAP = new HashMap< String, Class< ? > >();
	
	static
	{
		BASIC_TYPE_MAP.put( byte.class.getName(), byte.class );
		BASIC_TYPE_MAP.put( short.class.getName(), short.class );
		BASIC_TYPE_MAP.put( int.class.getName(), int.class );
		BASIC_TYPE_MAP.put( long.class.getName(), long.class );
		BASIC_TYPE_MAP.put( float.class.getName(), float.class );
		BASIC_TYPE_MAP.put( double.class.getName(), double.class );
		BASIC_TYPE_MAP.put( boolean.class.getName(), boolean.class );
		BASIC_TYPE_MAP.put( char.class.getName(), char.class );
	}
	
	private TaxonomyLoader parentTaxonomyLoader;
	private Map< String, TaxonomyLinked > loadedTaxonomies = new HashMap< String, TaxonomyLinked >();
	private ClassLoader classLoader;
	
	protected TaxonomyLoader()
	{
		parentTaxonomyLoader = getSystemTaxonomyLoader();
		classLoader = Thread.currentThread().getContextClassLoader();
	}
	
	public TaxonomyLoader( TaxonomyLoader parentTaxonomyLoader, ClassLoader classLoader )
	{
		if ( parentTaxonomyLoader == null && !( this instanceof SystemTaxonomyLoader ) )
			throw new IllegalArgumentException( "Parent taxonomy loader cannot be null." );
		
		if ( classLoader == null )
			throw new IllegalArgumentException( "Parent class loader cannot be null." );
		
		this.parentTaxonomyLoader = parentTaxonomyLoader;
		this.classLoader = classLoader;
	}
	
	public static TaxonomyLoader getSystemTaxonomyLoader()
	{
		if ( systemTaxonomyLoader == null )
			systemTaxonomyLoader = new SystemTaxonomyLoader();
		
		return systemTaxonomyLoader;
	}
	
	public static TaxonomyLoader getContextTaxonomyLoader()
	{
		if ( contextTaxonomyLoader.get() == null )
			contextTaxonomyLoader.set( getSystemTaxonomyLoader() );
		
		return contextTaxonomyLoader.get();
	}
	
	public static void setContextTaxonomyLoader( TaxonomyLoader taxonomyLoader )
	{
		contextTaxonomyLoader.set( taxonomyLoader );
	}
	
	public Taxonomy loadTaxonomy( String name )
	{
		Taxonomy taxonomy = findLoadedTaxonomy( name );
		
		if ( taxonomy == null && parentTaxonomyLoader != null )
		{
			try
			{
				taxonomy = parentTaxonomyLoader.loadTaxonomy( name );
			}
			catch ( TaxonomyNotFoundException e )
			{
			}
		}
		
		if ( taxonomy == null )
			taxonomy = findTaxonomy( name );
		
		return taxonomy;
	}
	
	protected Taxonomy findLoadedTaxonomy( String name )
	{
		return loadedTaxonomies.get( name );
	}
	
	protected Taxonomy findTaxonomy( String name )
	{
		TaxonomyCompiled taxonomyCompiled = loadTaxonomyInternal( name );
		Taxonomy taxonomy = defineTaxonomy( taxonomyCompiled );
		
		return taxonomy;
	}

	private TaxonomyCompiled loadTaxonomyInternal( String name )
	{
		String taxonomyName = name.replace( ".", "/" ) + TaxonomyCompiled.TAXONOMY_COMPILED_SUFFIX;
		InputStream inputStream = classLoader.getResourceAsStream( taxonomyName );
		TaxonomyCompiled taxonomyCompiled;

		try
		{
			taxonomyCompiled = TaxonomyCompiled.load( inputStream );
		}
		catch ( Throwable t )
		{
			throw new TaxonomyNotFoundException( "Unable to load class view: " + name, t );
		}
		
		return taxonomyCompiled;
	}
	
	// TODO ClassLoader.resolveClass() is used to link Java classes; it should
	// be mandatory, but appears to be optional. Look into how this works in
	// Java and design TaxonomyLoader accordingly.
	protected final void resolveTaxonomy( Taxonomy taxonomy )
	{
		throw new UnsupportedOperationException();
	}
	
	protected Taxonomy defineTaxonomy( TaxonomyCompiled taxonomyCompiled )
	{
		TaxonomyLinked taxonomyLinked = new TaxonomyLinked( taxonomyCompiled.getName() );
		
		for ( String extendedTaxonomy : taxonomyCompiled.getExtendedTaxonomies() )
			taxonomyLinked.getExtendedTaxonomiesLinked().add( (TaxonomyLinked)loadTaxonomy( extendedTaxonomy ) );
		
		defineAnnotations( taxonomyCompiled, taxonomyLinked );
		
		taxonomyLinked.setDefaultAccessType( taxonomyCompiled.getDefaultAccessType() );
		
		for ( ViewCompiled viewCompiled : taxonomyCompiled.getViewsCompiled() )
			taxonomyLinked.getViewsLinked().add( defineView( taxonomyLinked, viewCompiled, taxonomyLinked.getDefaultAccessType() ) );
		
		return taxonomyLinked;
	}
	
	private ViewLinked defineView( TaxonomyLinked parentTaxonomyLinked, ViewCompiled viewCompiled, AccessType defaultAccessType )
	{
		ViewLinked viewLinked = new ViewLinked( viewCompiled.getName() );
		viewLinked.setParentTaxonomyLinked( parentTaxonomyLinked );
		viewLinked.setViewedClass( loadClass( viewLinked.getName() ) );
		
		for ( String usedClass : viewCompiled.getUsedClasses() )
			viewLinked.getUsedClasses().add( loadClass( usedClass ) );
		
		defineAnnotations( viewCompiled, viewLinked );
		viewLinked.setSelectionType( viewCompiled.getSelectionType() );
		viewLinked.setAlias( viewCompiled.getAlias() );
		
		for ( MemberCompiled memberCompiled : viewCompiled.getMembersCompiled() )
			viewLinked.getMembersLinked().add( defineMember( viewLinked, memberCompiled, defaultAccessType ) );
		
		return viewLinked;
	}
	
	private MemberLinked defineMember( ViewLinked parentViewLinked, MemberCompiled memberCompiled, AccessType defaultAccessType )
	{
		MemberLinked memberLinked = null;
		AccessType effectiveAccessType = ( memberCompiled.getAccessType() == null ? defaultAccessType : memberCompiled.getAccessType() );
		Class< ? > viewedClass = parentViewLinked.getViewedClass();
		
		if ( effectiveAccessType.equals( AccessType.Field ) )
		{
			FieldLinked fieldLinked = new FieldLinked( memberCompiled.getName() );
			Field field = __Class.getDeclaredField( viewedClass, fieldLinked.getName() );
			field.setAccessible( true );
			fieldLinked.setField( field );
			memberLinked = fieldLinked;
		}
		else if ( effectiveAccessType.equals( AccessType.Property ) )
		{
			PropertyLinked propertyLinked = new PropertyLinked( memberCompiled.getName() );
			Map< String, Method > declaredMethods = new HashMap< String, Method >();
			
			for ( Method method : viewedClass.getDeclaredMethods() )
				declaredMethods.put( method.getName().toLowerCase(), method );
			
			Method writeMethod = declaredMethods.get( "set" + propertyLinked.getName().toLowerCase() );
			Method readMethod = declaredMethods.get( "is" + propertyLinked.getName().toLowerCase() );
			
			if ( readMethod == null )
				readMethod = declaredMethods.get( "get" + propertyLinked.getName().toLowerCase() );
			
			if ( writeMethod != null )
				writeMethod.setAccessible( true );
			
			if ( readMethod != null )
				readMethod.setAccessible( true );
			
			propertyLinked.setReadMethod( readMethod );
			propertyLinked.setWriteMethod( writeMethod );
			memberLinked = propertyLinked;
		}
		else
		{
			throw new IllegalStateException( "Unexpected value for effectiveAccessType: " + effectiveAccessType );
		}
		
		memberLinked.setParentViewLinked( parentViewLinked );
		memberLinked.setAlias( memberCompiled.getAlias() );
		memberLinked.setSelectionType( memberCompiled.getSelectionType() );
		
		return memberLinked;
	}
	
	private void defineAnnotations( AnnotatableElementCompiled annotatableElementCompiled, AnnotatableElementLinked annotatableElementLinked )
	{
		for ( AnnotationCompiled annotationCompiled : annotatableElementCompiled.getAnnotationsCompiled() )
			annotatableElementLinked.getAnnotations().add( createUnparsedAnnotation( annotationCompiled.getUnparsedAnnotation() ) );
	}
	
	private UnparsedAnnotation createUnparsedAnnotation( String value )
	{
		return new UnparsedAnnotation() {
			@Override
			public Class< ? extends Annotation > annotationType()
			{
				return UnparsedAnnotation.class;
			}
			
			@Override
			public String value()
			{
				return value;
			}
		};
	}
	
	private Class< ? > loadClass( String name )
	{
		try
		{
			Class< ? > theClass = BASIC_TYPE_MAP.get( name );
			
			if ( theClass == null )
				theClass = classLoader.loadClass( name );
				
			return theClass;
		}
		catch ( ClassNotFoundException e )
		{
			throw new TaxonomyFormatError( "Cannot find class referenced by taxonomy.", e );
		}
	}
}
