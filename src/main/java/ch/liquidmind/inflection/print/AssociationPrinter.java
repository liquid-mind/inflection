package ch.liquidmind.inflection.print;

import java.io.PrintStream;
import java.io.Writer;
import java.util.Set;

import ch.liquidmind.inflection.association.Association;
import ch.liquidmind.inflection.association.Class;
import ch.liquidmind.inflection.association.Property;

public class AssociationPrinter extends AbstractPrinter
{
	public AssociationPrinter()
	{
		super( DEFAULT_PRINT_STREAM );
	}

	public AssociationPrinter( PrintStream printStream )
	{
		super( printStream );
	}

	public AssociationPrinter( Writer writer )
	{
		super( writer );
	}

	public void printClasses( Set< Class > classes )
	{
		classes.stream().sorted( ( classA, classB ) -> classA.getName().compareToIgnoreCase( classB.getName() ) ).forEach( aClass -> printClass( aClass ) );
	}

	public void printClass( Class aClass )
	{
		getPrintWriter().println( String.format( "Associations owned by class %s:", getTypeName( aClass.getName() ) ) );
		getPrintWriter().increaseIndent();
		aClass.getOwnedAssociations().stream().forEach( association -> printAssociation( association ) );
		getPrintWriter().decreaseIndent();
		getPrintWriter().flush();
	}

	private void printAssociation( Association association )
	{
		String displayName = ( association.getName().isEmpty() ? "NA" : association.getName() );
		
		getPrintWriter().println( String.format( "Name=%s, Declared=%s", displayName, association.isDeclared() ) );
		printProperty( "Self End", association.getSelfEnd() );
		printProperty( "Other End", association.getOtherEnd() );
	}
	
	private void printProperty( String title, Property property )
	{
		getPrintWriter().increaseIndent();
		getPrintWriter().print( title + ": " );
		printProperty( property );
		getPrintWriter().decreaseIndent();
	}
	
	private void printProperty( Property property )
	{
		if ( property == null )
		{
			getPrintWriter().println( "NA" );
		}
		else
		{
			Property redefinedProperty = property.getRedefinedProperty();
			Property subsettedProperty = property.getSubsettedProperty();
			String redefinedPropertyName = ( redefinedProperty == null ? "NA" : getTypeName( redefinedProperty.getOwningClass().getName() ) + "." + redefinedProperty.getName() );
			String subsettedPropertyName = ( subsettedProperty == null ? "NA" : getTypeName( subsettedProperty.getOwningClass().getName() ) + "." + subsettedProperty.getName() );
			
			getPrintWriter().println( String.format( "Name=%s, Related Type=%s, Aggregation=%s, Redefines=%s, Subsets=%s, Declared=%s, Derived=%s, Derived Union=%s",
				property.getName(), getTypeName( property.getRelatedType().getTypeName() ), property.getAggregation(), 
				redefinedPropertyName, subsettedPropertyName, property.isDeclared(), property.isDerived(), property.isDerivedUnion() ) );
		}
	}
}
