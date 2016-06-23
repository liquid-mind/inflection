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
		getPrintWriter().println( String.format( "Class %s:", getTypeName( aClass.getName() ) ) );
		getPrintWriter().increaseIndent();
		aClass.getOwnedProperties().stream().forEach( property -> printProperty( property ) );
		aClass.getOwnedAssociations().stream().forEach( association -> printAssociation( association ) );
		getPrintWriter().decreaseIndent();
		getPrintWriter().flush();
	}
	
	private void printProperty( Property property )
	{
		Property redefinedProperty = property.getRedefinedProperty();
		Property subsettedProperty = property.getSubsettedProperty();
		String redefinedPropertyName = ( redefinedProperty == null ? "NA" : getTypeName( redefinedProperty.getOwningClass().getName() ) + "." + redefinedProperty.getName() );
		String subsettedPropertyName = ( subsettedProperty == null ? "NA" : getTypeName( subsettedProperty.getOwningClass().getName() ) + "." + subsettedProperty.getName() );
		
		getPrintWriter().println( String.format( "Property %s: Related Type=%s, Aggregation=%s, Redefines=%s, Subsets=%s, Declared=%s, Derived=%s, Derived Union=%s",
			property.getName(), getTypeName( property.getRelatedType().getTypeName() ), property.getAggregation(), 
			redefinedPropertyName, subsettedPropertyName, property.isDeclared(), property.isDerived(), property.isDerivedUnion() ) );
	}

	private void printAssociation( Association association )
	{
		String displayName = ( association.getName().isEmpty() ? "NA" : association.getName() );
		
		getPrintWriter().print( String.format( "Association %s:", displayName ) );
		printPropertyReference( "Self End", association.getSelfEnd() );
		printPropertyReference( "Other End", association.getOtherEnd() );
		getPrintWriter().println( String.format( " Declared=%s", association.isDeclared() ) );
	}

	private void printPropertyReference( String title, Property property )
	{
		getPrintWriter().increaseIndent();
		getPrintWriter().print( " " + title + "=");
		printPropertyReference( property );
		getPrintWriter().decreaseIndent();
	}
	
	private void printPropertyReference( Property property )
	{
		if ( property == null )
		{
			getPrintWriter().print( "NA" );
		}
		else
		{
			getPrintWriter().print( String.format( "%s.%s",
				getTypeName( property.getOwningClass().getName() ), property.getName() ) );
		}
	}
}
