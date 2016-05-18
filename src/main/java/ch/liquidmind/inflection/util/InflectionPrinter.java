package ch.liquidmind.inflection.util;

import java.io.File;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import __java.net.__URI;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Field;
import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Property;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.linked.UnparsedAnnotation;

public class InflectionPrinter
{
	public static final PrintStream DEFAULT_PRINT_STREAM = System.out;
	public static final boolean DEFAULT_SHOW_SIMPLE_NAMES = true;
	public static final boolean DEFAULT_SHOW_INHERITED = false;
	
	private IndentingPrintWriter printWriter;
	private boolean showSimpleNames;
	private boolean showInherited;

	public InflectionPrinter()
	{
		this( DEFAULT_PRINT_STREAM, DEFAULT_SHOW_SIMPLE_NAMES, DEFAULT_SHOW_INHERITED );
	}
	
	public InflectionPrinter( PrintStream printStream )
	{
		this( printStream, DEFAULT_SHOW_SIMPLE_NAMES, DEFAULT_SHOW_INHERITED );
	}
	
	public InflectionPrinter( boolean showSimpleNames, boolean showInherited )
	{
		this( DEFAULT_PRINT_STREAM, showSimpleNames, showInherited );
	}

	public InflectionPrinter( Writer writer, boolean showSimpleNames, boolean showInherited )
	{
		super();
		this.printWriter = new IndentingPrintWriter( writer );
		this.showSimpleNames = showSimpleNames;
		this.showInherited = showInherited;
	}
	
	public InflectionPrinter( PrintStream printStream, boolean showSimpleNames, boolean showInherited )
	{
		super();
		this.printWriter = new IndentingPrintWriter( printStream );
		this.showSimpleNames = showSimpleNames;
		this.showInherited = showInherited;
	}
	
	public static void main( String[] args )
	{
		Map< String, List< String > > options = parseOptions( args );
		
		boolean showSimpleNames = options.containsKey( "-showSimpleNames" );
		boolean showInherited = options.containsKey( "-showInherited" );
		String taxonomyName = options.get( "-taxonomy" ).get( 0 );
		String classpath = options.get( "-classpath" ).get( 0 );
		String[] classpathArray = classpath.split( "[:|;]" );
		
		printTaxonomy( classpathArray, taxonomyName, DEFAULT_PRINT_STREAM, showSimpleNames, showInherited );
	}
	
	public static void printTaxonomy( String[] classpath, String taxonomyName, PrintStream printStream, boolean showSimpleNames, boolean showInherited )
	{
		URL[] urls = new URL[ classpath.length ];
		
		for ( int i = 0 ; i < classpath.length ; ++i )
			urls[ i ] = __URI.toURL( new File( classpath[ i ] ).toURI() );
		
		ClassLoader classLoader = new URLClassLoader( urls, Thread.currentThread().getContextClassLoader() );
		TaxonomyLoader loader = new TaxonomyLoader( TaxonomyLoader.getContextTaxonomyLoader(), classLoader );
		printTaxonomy( loader, taxonomyName, printStream, showSimpleNames, showInherited );
	}
	
	public static void printTaxonomy( TaxonomyLoader loader, String taxonomyName, PrintStream printStream, boolean showSimpleNames, boolean showInherited )
	{
		Taxonomy taxonomy = loader.loadTaxonomy( taxonomyName );
		InflectionPrinter printer = new InflectionPrinter( printStream, showSimpleNames, showInherited );
		printer.printTaxonomy( taxonomy );
	}

	public static void printView( Taxonomy taxonomy, View view, Writer writer, boolean showSimpleNames, boolean showInherited )
	{
		InflectionPrinter printer = new InflectionPrinter( writer, showSimpleNames, showInherited );
		printer.printView( taxonomy, view );
	}
	
	// TODO Also used by ProxyGenerator: move to a common location.
	public static Map< String, List< String > > parseOptions( String[] args )
	{
		Map< String, List< String > > options = new HashMap< String, List< String > >();

		for ( int i = 0 ; i < args.length ; ++i )
		{
			if ( args[ i ].startsWith( "-" ) )
			{
				String optionName = args[ i ];
				List< String > optionArgs = new ArrayList< String >();
				
				if ( ( i + 1 ) < args.length && !args[ i + 1 ].startsWith( "-" ) )
				{
					++i;
					for ( ; i < args.length ; ++i )
					{
						if ( args[ i ].startsWith( "-" ) )
							break;
						
						optionArgs.add( args[ i ] );
					}
					--i;
				}

				options.put( optionName, optionArgs );
			}
		}
		
		return options;
	}

	public void printTaxonomy( Taxonomy taxonomy )
	{
		printTaxonomy( taxonomy, System.out );
	}
	
	public void printTaxonomy( Taxonomy taxonomy, PrintStream printStream )
	{
		printAnnotations( taxonomy.getAnnotations() );
		printWriter.print( "taxonomy " + getTypeName( taxonomy.getName() ) );
		List< String > taxonomyNames = new ArrayList< String >();
		
		for ( Taxonomy extendedTaxonomy : taxonomy.getExtendedTaxonomies() )
			taxonomyNames.add( getTypeName( extendedTaxonomy.getName() ) );
		
		if ( !taxonomyNames.isEmpty() )
			printWriter.println( " extends " + String.join( ", ", taxonomyNames ) );
		else
			printWriter.println();

		List< View > views;
		
		if ( showInherited )
			views = taxonomy.getViews();
		else
			views = taxonomy.getDeclaredViews();
		
		if ( views.isEmpty() )
		{
			printWriter.println( "{}" );
			return;
		}
		
		printWriter.println( "{" );
		printWriter.increaseIndent();
		
		for ( int i = 0 ; i < views.size() ; ++i )
		{
			View view = views.get( i );
			
			printView( taxonomy, view );
			
			if ( i + 1 != views.size() )
				printWriter.println();
		}
		
		printWriter.decreaseIndent();
		printWriter.println( "}" );
		printWriter.flush();
	}
	
	private void printView( Taxonomy taxonomy, View view )
	{
		printAnnotations( view.getAnnotations() );
		printWriter.print( "view " + getTypeName( view.getName() ) );
		
		if ( showInherited && !view.getParentTaxonomy().equals( taxonomy ) )
			printWriter.print( " from " + getTypeName( view.getParentTaxonomy().getName() ) );
		
		if ( view.getAlias() != null )
			printWriter.print( " as " + view.getAlias() );
		
		if ( view.getUsedClass() != null )
			printWriter.print( " use " + view.getUsedClass().getName() );
		
		if ( taxonomy.getSuperview( view ) != null)
			printWriter.println( " extends " + getTypeName( taxonomy.getSuperview( view ).getName() ) );
		else
			printWriter.println();
		
		List< Member > members;
		
		if ( showInherited )
			members = taxonomy.getMembers( view );
		else
			members = view.getDeclaredMembers();
		
		if ( members.isEmpty() )
		{
			printWriter.println( "{}" );
		}
		else
		{
			printWriter.println( "{" );
			printWriter.increaseIndent();

			for ( Member member : members )
			{
				String accessType;
				
				if ( member instanceof Field )
					accessType = "field";
				else if ( member instanceof Property )
					accessType = "property";
				else
					throw new IllegalStateException( "Unexpected type for member: " + member.getClass().getName() );
				
				String memberType = getTypeName( getMemberType( member ) );
				
				String alias = ( member.getAlias() == null ? "" : " as " + member.getAlias() );
				String from = "";
				
				if ( showInherited && !member.getParentView().equals( view ) )
					from = " from " + getTypeName( member.getParentView().getName() );
				
				printAnnotations( member.getAnnotations() );
				printWriter.println( accessType + " " + memberType + " " + member.getName() + from + alias + ";" );
			}
			
			printWriter.decreaseIndent();
			printWriter.println( "}" );
		}
	}
	
	private Type getMemberType( Member member )
	{
		Type memberType = null;
		
		if ( member instanceof Field )
		{
			Field field = (Field)member;
			memberType = field.getField().getType();
		}
		else if ( member instanceof Property )
		{
			Property property = (Property)member;
			
			if ( property.getReadMethod() != null )
				memberType = property.getReadMethod().getGenericReturnType();
			else if ( property.getWriteMethod() != null )
				memberType = property.getWriteMethod().getGenericParameterTypes()[ 0 ];
			else
				throw new IllegalStateException( "Both read and write methods are null." );
		}
		
		return memberType;
	}
	
	private String getTypeName( Type type )
	{
		String typeName;
		
		if ( type instanceof Class )
		{
			Class< ? > aClass = (Class< ? >)type;
			typeName = getTypeName( aClass.getName() );
		}
		else if ( type instanceof ParameterizedType )
		{
			ParameterizedType parameterizedType = (ParameterizedType)type;
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			List< String > actualTypeArgumentsConverted = new ArrayList< String >();
			String rawTypeConverted = getTypeName( parameterizedType.getRawType() );

			for ( Type actualTypeArgument : actualTypeArguments )
			{
				if ( actualTypeArgument instanceof Class )
					actualTypeArgumentsConverted.add( getTypeName( actualTypeArgument ) );
				else
					throw new IllegalStateException( "No support for general purpose generics at this time (only for collections), type: " + type.getTypeName() );
			}
			
			typeName = rawTypeConverted + "< " + String.join( ", ", actualTypeArgumentsConverted ) + " >";
		}
		else
		{
			throw new IllegalStateException( "Unexpected type for 'type': " + type.getClass().getName() );
		}
		
		return typeName;
	}
	
	private void printAnnotations( List< Annotation > annotations )
	{
		for ( Annotation annotation : annotations )
		{
			UnparsedAnnotation unparsedAnnotation = (UnparsedAnnotation)annotation;
			printWriter.println( unparsedAnnotation.value() );				
		}
	}
	
	private String getTypeName( String fqTypeName )
	{
		String typeName;
		
		if ( showSimpleNames )
			typeName = ( fqTypeName.contains( "." ) ? fqTypeName.substring( fqTypeName.lastIndexOf( "." ) + 1 ) : fqTypeName );
		else
			typeName = fqTypeName;
		
		return typeName;
	}
}
