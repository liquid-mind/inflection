package ch.liquidmind.inflection.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.linked.FieldLinked;
import ch.liquidmind.inflection.model.linked.PropertyLinked;

public class InflectionPrinter
{
	public static final PrintStream DEFAULT_PRINT_STREAM = System.out;
	public static final boolean DEFAULT_SHOW_SIMPLE_NAMES = true;
	
	private IndentingPrintWriter printWriter;
	private boolean showSimpleNames;

	public InflectionPrinter()
	{
		this( DEFAULT_PRINT_STREAM, DEFAULT_SHOW_SIMPLE_NAMES );
	}
	
	public InflectionPrinter( PrintStream printStream )
	{
		this( printStream, DEFAULT_SHOW_SIMPLE_NAMES );
	}
	
	public InflectionPrinter( PrintStream printStream, boolean showSimpleNames )
	{
		super();
		this.printWriter = new IndentingPrintWriter( printStream );
		this.showSimpleNames = showSimpleNames;
	}

	public void printTaxonomy( Taxonomy taxonomy )
	{
		printTaxonomy( taxonomy, System.out );
	}
	
	public void printTaxonomy( Taxonomy taxonomy, PrintStream printStream )
	{
		printWriter.print( "taxonomy " + getTypeName( taxonomy.getName() ) );
		List< String > taxonomyNames = new ArrayList< String >();
		
		for ( Taxonomy extendedTaxonomy : taxonomy.getExtendedTaxonomies() )
			taxonomyNames.add( getTypeName( extendedTaxonomy.getName() ) );
		
		if ( !taxonomyNames.isEmpty() )
			printWriter.println( " extends " + String.join( ", ", taxonomyNames ) );
		else
			printWriter.println();

		List< View > views = taxonomy.getDeclaredViews();
		
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
			
			printWriter.print( "view " + getTypeName( view.getName() ) );
			
			if ( view.getAlias() != null )
				printWriter.print( " as " + view.getAlias() );
			
			List< String > usedClassNames = new ArrayList< String >();
			
			for ( Class< ? > usedClass : view.getUsedClasses() )
				usedClassNames.add( getTypeName( usedClass.getName() ) );
			
			if ( !usedClassNames.isEmpty() )
				printWriter.print( " use " + String.join( ", ", usedClassNames ) );
			
			if ( view.getSuperview() != null)
				printWriter.println( " extends " + getTypeName( view.getSuperview().getName() ) );
			else
				printWriter.println();
			
			List< Member > members = view.getDeclaredMembers();
			
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
					
					if ( member instanceof FieldLinked )
						accessType = "field";
					else if ( member instanceof PropertyLinked )
						accessType = "property";
					else
						throw new IllegalStateException( "Unexpected type for member: " + member.getClass().getName() );
					
					String alias = ( member.getAlias() == null ? "" : " as " + member.getAlias() );
					
					printWriter.println( accessType + " " + member.getName() + alias + ";" );
				}
				
				printWriter.decreaseIndent();
				printWriter.println( "}" );
			}
			
			if ( i + 1 != views.size() )
				printWriter.println();
		}
		
		printWriter.decreaseIndent();
		printWriter.println( "}" );
		printWriter.flush();
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
