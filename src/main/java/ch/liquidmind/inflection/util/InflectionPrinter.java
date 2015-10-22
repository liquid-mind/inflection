package ch.liquidmind.inflection.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;

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
		
		printWriter.println( "{" );
		printWriter.increaseIndent();
		
		for ( View view : taxonomy.getViews() )
		{
			printWriter.print( "view " + getTypeName( view.getName() ) );
			
			if ( view.getAlias() != null )
				printWriter.print( " alias " + view.getAlias() );
			
			List< String > usedClassNames = new ArrayList< String >();
			
			for ( Class< ? > usedClass : view.getUsedClasses() )
				usedClassNames.add( getTypeName( usedClass.getName() ) );
			
			if ( !usedClassNames.isEmpty() )
				printWriter.println( " use " + String.join( ", ", usedClassNames ) );
			else
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
