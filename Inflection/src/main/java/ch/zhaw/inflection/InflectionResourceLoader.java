package ch.zhaw.inflection;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import __java.lang.__Class;
import __java.lang.reflect.__Constructor;
import ch.zhaw.inflection.compiler.ClassViewCompiled;
import ch.zhaw.inflection.compiler.ClassViewCompiled.MemberViewCompiled;
import ch.zhaw.inflection.compiler.HgroupCompiled;
import ch.zhaw.inflection.compiler.InflectionResourceCompiled;
import ch.zhaw.inflection.compiler.VmapCompiled;
import ch.zhaw.inflection.compiler.VmapCompiled.MappingCompiled;
import ch.zhaw.inflection.model.ClassView;
import ch.zhaw.inflection.model.FieldView;
import ch.zhaw.inflection.model.HGroup;
import ch.zhaw.inflection.model.InflectionResource;
import ch.zhaw.inflection.model.InflectionView;
import ch.zhaw.inflection.model.MemberView;
import ch.zhaw.inflection.model.PropertyView;
import ch.zhaw.inflection.model.VMap;
import ch.zhaw.inflection.operation.InflectionVisitor;

public abstract class InflectionResourceLoader
{
	private static InflectionResourceLoader systemInflectionResourceLoader;
	private static ThreadLocal< InflectionResourceLoader > contextInflectionResourceLoader = new ThreadLocal< InflectionResourceLoader >();

	public static final Map< String, Class< ? > > BASIC_TYPE_MAP = new HashMap< String, Class< ? > >();
	public static final Map< String, ClassView< ? > > BASIC_TYPE_VIEW_MAP = new HashMap< String, ClassView< ? > >();
	
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
	
	private static void initBasicTypeViewMap( InflectionResourceLoader loader )
	{
		ClassView< Byte > byteClassView = new ClassView< Byte >( byte.class + "View", byte.class, loader );
		ClassView< Short > shortClassView = new ClassView< Short >( short.class + "View", short.class, loader );
		ClassView< Integer > intClassView = new ClassView< Integer >( int.class + "View", int.class, loader );
		ClassView< Long > longClassView = new ClassView< Long >( long.class + "View", long.class, loader );
		ClassView< Float > floatClassView = new ClassView< Float >( float.class + "View", float.class, loader );
		ClassView< Double > doubleClassView = new ClassView< Double >( double.class + "View", double.class, loader );
		ClassView< Character > charClassView = new ClassView< Character >( char.class + "View", char.class, loader );
		ClassView< Boolean > booleanClassView = new ClassView< Boolean >( boolean.class + "View", boolean.class, loader );
		
		BASIC_TYPE_VIEW_MAP.put( byteClassView.getName(), byteClassView );
		BASIC_TYPE_VIEW_MAP.put( shortClassView.getName(), shortClassView );
		BASIC_TYPE_VIEW_MAP.put( intClassView.getName(), intClassView );
		BASIC_TYPE_VIEW_MAP.put( longClassView.getName(), longClassView );
		BASIC_TYPE_VIEW_MAP.put( floatClassView.getName(), floatClassView );
		BASIC_TYPE_VIEW_MAP.put( doubleClassView.getName(), doubleClassView );
		BASIC_TYPE_VIEW_MAP.put( charClassView.getName(), charClassView );
		BASIC_TYPE_VIEW_MAP.put( booleanClassView.getName(), booleanClassView );
	}
	
	private InflectionResourceLoader parentInflectionResourceLoader;
	private Map< String, InflectionResource > inflectionResourceMap = new HashMap< String, InflectionResource >();

	// Constructor for System InflectionResourceLoader.
	InflectionResourceLoader( Void unused )
	{
		initBasicTypeViewMap( this );
	}
	
	protected InflectionResourceLoader()
	{
		parentInflectionResourceLoader = getSystemInflectionResourceLoader();
	}
	
	public InflectionResourceLoader( InflectionResourceLoader parentInflectionResourceLoader )
	{
		if ( parentInflectionResourceLoader == null )
			throw new IllegalArgumentException( "Parent InflectionResourceLoader cannot be null." );
		
		this.parentInflectionResourceLoader = parentInflectionResourceLoader;
	}
	
	@SuppressWarnings( "unchecked" )
	public static InflectionResourceLoader getSystemInflectionResourceLoader()
	{
		if ( systemInflectionResourceLoader == null )
		{
			Constructor< ? > voidConstructor = __Class.getDeclaredConstructor( Void.class );
			voidConstructor.setAccessible( true );
			Void voidObject = (Void)__Constructor.newInstance( voidConstructor );

			Constructor< ? > loaderConstructor = __Class.getDeclaredConstructor( DelegatingInflectionResourceLoader.class, Void.class );
			loaderConstructor.setAccessible( true );
			systemInflectionResourceLoader = (InflectionResourceLoader)__Constructor.newInstance( loaderConstructor, voidObject );
		}
		
		return systemInflectionResourceLoader;
	}
	
	public static InflectionResourceLoader getContextInflectionResourceLoader()
	{
		if ( contextInflectionResourceLoader.get() == null )
			contextInflectionResourceLoader.set( getSystemInflectionResourceLoader() );
		
		return contextInflectionResourceLoader.get();
	}
	
	public static void setContextInflectionResourceLoader( InflectionResourceLoader inflectionResourceLoader )
	{
		contextInflectionResourceLoader.set( inflectionResourceLoader );
	}
	
	@SuppressWarnings( "unchecked" )
	public < ObjectType > ClassView< ObjectType > loadClassView( String name )
	{
		return (ClassView< ObjectType >)loadInflectionResource( name );
	}
	
	public VMap loadVmap( String name )
	{
		return (VMap)loadInflectionResource( name );
	}
	
	public HGroup loadHGroup( String name )
	{
		return (HGroup)loadInflectionResource( name );
	}
	
	// TODO Use type parameters for all loader methods, or use
	// wildcards, but make consistent. Also be consistent with
	// ClassView/ObjectView methods.
	public InflectionResource loadInflectionResource( String name )
	{
		InflectionResource resource = findLoadedInflectionResource( name );
		
		if ( resource == null && parentInflectionResourceLoader != null )
		{
			try
			{
				resource = parentInflectionResourceLoader.loadInflectionResource( name );
			}
			catch ( ClassViewNotFoundException e )
			{
			}
		}
		
		if ( resource == null )
			resource = BASIC_TYPE_VIEW_MAP.get( name );
		
		if ( resource == null )
			resource = findInflectionResource( name );
		
		return resource;
	}
	
	public InflectionResource findLoadedInflectionResource( String name )
	{
		return inflectionResourceMap.get( name );
	}
	
	public InflectionResource findInflectionResource( String name )
	{
		throw new ClassViewNotFoundException();
	}
		
	private Class< ? > findClassWithExceptionHandling( String name )
	{
		try
		{
			Class< ? > theClass = BASIC_TYPE_MAP.get( name );
			
			if ( theClass == null )
				theClass = findClass( name );
				
			return theClass;
		}
		catch ( ClassNotFoundException e )
		{
			throw new ClassViewFormatError( "Cannot find class referenced by class view.", e );
		}
	}
	
	public Class< ? > findClass( String name ) throws ClassNotFoundException
	{
		throw new ClassNotFoundException();
	}
	
	// TODO ClassLoader.resolveClass() is used to link Java classes; it should
	// be mandatory, but appears to be optional. Look into how this works in
	// Java and design InflectionResourceLoader accordingly.
	public final void resolveInflectionResource( InflectionResource inflectionResource )
	{
		throw new UnsupportedOperationException();
	}
	
	public InflectionResource defineInflectionResource( InflectionResourceCompiled inflectionResourceCompiled )
	{
		InflectionResource resource;
		
		if ( inflectionResourceCompiled instanceof ClassViewCompiled )
			resource = defineClassView( (ClassViewCompiled)inflectionResourceCompiled );
		else if ( inflectionResourceCompiled instanceof VmapCompiled )
			resource = defineVmap( (VmapCompiled)inflectionResourceCompiled );
		else if ( inflectionResourceCompiled instanceof HgroupCompiled )
			resource = defineHGroup( (HgroupCompiled)inflectionResourceCompiled );
		else
			throw new IllegalStateException( "Unexpected type for inflectionResourceCompiled: " + inflectionResourceCompiled.getClass().getName() );

		return resource;
	}

	// TODO perform class view verification.
	private ClassView< ? > defineClassView( ClassViewCompiled classViewCompiled )
	{
		ClassView< ? > classView = new ClassView< Object >( classViewCompiled.getName(), this );
		
		// Ensures that this class view will be found on recursive calls
		// to loadClassView() (avoids infinite recursion).
		// TODO Make InflectionResourceLoader thread safe.
		inflectionResourceMap.put( classView.getName(), classView );
		
		classView.setJavaClass( findClassWithExceptionHandling( classViewCompiled.getJavaClassName() ) );
		
		if ( classViewCompiled.getExtendedClassViewName() != null )
			classView.setExtendedClassView( loadClassView( classViewCompiled.getExtendedClassViewName() ) );
		
		for ( MemberViewCompiled memberViewCompiled : classViewCompiled.getMemberViews() )
		{
			MemberView memberView;
			
			if ( memberViewCompiled.getType().equals( MemberViewCompiled.Type.Property ) )
				memberView = new PropertyView( memberViewCompiled.getName(), classView, loadClassView( memberViewCompiled.getClassViewName() ), memberViewCompiled.getAggregation() );
			else if ( memberViewCompiled.getType().equals( MemberViewCompiled.Type.Field ) )
				memberView = new FieldView( memberViewCompiled.getName(), classView, loadClassView( memberViewCompiled.getClassViewName() ), memberViewCompiled.getAggregation() );
			else
				throw new IllegalStateException( "Unexpected value for memberViewCompiled.getType(): " + memberViewCompiled.getType() );

			classView.getDeclaredMemberViews().add( memberView );
		}

		return classView;
	}
	
	@SuppressWarnings( "unchecked" )
	private VMap defineVmap( VmapCompiled vmapCompiled )
	{
		VMap vmap = new VMap( vmapCompiled.getName() );
		inflectionResourceMap.put( vmap.getName(), vmap );
		
		if ( vmapCompiled.getExtendedVmapName() != null )
			vmap.setExtendedVMap( loadVmap( vmapCompiled.getExtendedVmapName() ) );
		
		for ( MappingCompiled mappingCompiled : vmapCompiled.getClassViewToVisitorMappings() )
		{
			InflectionView inflectionView;
			String inflectionViewName = mappingCompiled.getInflectionViewName();
			
			if ( inflectionViewName.contains( "->" ) )
			{
				String owningClassViewName = inflectionViewName.substring( 0, inflectionViewName.indexOf( "->" ) );
				String simpleMemberViewName = inflectionViewName.substring( inflectionViewName.indexOf( "->" ) + 2 );
				ClassView< ? > owningClassView = loadClassView( owningClassViewName );
				inflectionView = owningClassView.getMemberView( simpleMemberViewName );
			}
			else
			{
				inflectionView = loadClassView( inflectionViewName );
			}
			
			Class< ? > visitorClass = findClassWithExceptionHandling( mappingCompiled.getVisitorClassName() );
			vmap.addViewToVisitorClassMapping( inflectionView, visitorClass );
		}
		
		if ( vmapCompiled.getDefaultVisitorClassName() != null )
		{
			Class< InflectionVisitor< ? > > defaultVisitorClass = (Class< InflectionVisitor< ? > >)findClassWithExceptionHandling( vmapCompiled.getDefaultVisitorClassName() );
			vmap.setDefaultVisitorClass( defaultVisitorClass );
		}
		
		return vmap;
	}
	
	private HGroup defineHGroup( HgroupCompiled hgroupCompiled )
	{
		HGroup hGroup = new HGroup( hgroupCompiled.getName() );
		inflectionResourceMap.put( hGroup.getName(), hGroup );
		
		if ( hgroupCompiled.getExtendedHgroupName() != null )
			hGroup.setExtendedHGroup( loadHGroup( hgroupCompiled.getExtendedHgroupName() ) );
		
		for ( String classViewName : hgroupCompiled.getClassViewNames() )
			hGroup.getDeclaredClassViews().add( loadClassView( classViewName ) );
		
		return hGroup;
	}
}
