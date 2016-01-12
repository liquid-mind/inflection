package ch.liquidmind.inflection.util;

import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import ch.liquidmind.inflection.model.external.Member;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.model.external.View;
import ch.liquidmind.inflection.model.linked.FieldLinked;
import ch.liquidmind.inflection.model.linked.PropertyLinked;
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
	
	public InflectionPrinter( PrintStream printStream, boolean showSimpleNames, boolean showInherited )
	{
		super();
		this.printWriter = new IndentingPrintWriter( printStream );
		this.showSimpleNames = showSimpleNames;
		this.showInherited = showInherited;
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
			
			printAnnotations( view.getAnnotations() );
			printWriter.print( "view " + getTypeName( view.getName() ) );
			
			if ( showInherited && !view.getParentTaxonomy().equals( taxonomy ) )
				printWriter.print( " from " + getTypeName( view.getParentTaxonomy().getName() ) );
			
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
					
					if ( member instanceof FieldLinked )
						accessType = "field";
					else if ( member instanceof PropertyLinked )
						accessType = "property";
					else
						throw new IllegalStateException( "Unexpected type for member: " + member.getClass().getName() );
					
					String alias = ( member.getAlias() == null ? "" : " as " + member.getAlias() );
					String from = "";
					
					if ( showInherited && !member.getParentView().equals( view ) )
						from = " from " + getTypeName( member.getParentView().getName() );
					
					printAnnotations( member.getAnnotations() );
					printWriter.println( accessType + " " + member.getName() + from + alias + ";" );
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
