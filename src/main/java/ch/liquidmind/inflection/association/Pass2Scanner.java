package ch.liquidmind.inflection.association;

import java.util.Set;

public class Pass2Scanner extends AbstractScanner
{
	private Set< Class > classes;
	
	public Pass2Scanner( Set< Class > classes )
	{
		super();
		this.classes = classes;
	}

	public void scan()
	{
		classes.forEach( aClass -> setupClass( aClass ) );
	}
	
	private void setupClass( Class aClass )
	{
		setupProperties( aClass );
		setupAssociations( aClass );
	}

	private void setupProperties( Class aClass )
	{
		aClass.getOwnedProperties().forEach( property -> setupProperty( property ) );
	}
	
	private void setupProperty( Property property )
	{
		ch.liquidmind.inflection.association.annotations.Property propertyAnnotation = getPropertyAnnotation( property );
		property.setRedefinedProperty( determineRedefinedProperty( property, propertyAnnotation ) );
		property.setSubsettedProperty( determineSubsettedProperty( property, propertyAnnotation ) );
	}
	
	private Property determineRedefinedProperty( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		return null;
	}
	
	private Property determineSubsettedProperty( Property property, ch.liquidmind.inflection.association.annotations.Property propertyAnnotation )
	{
		return null;
	}
	
	private void setupAssociations( Class aClass )
	{
		setupClassLevelAssociations( aClass );
		setupPropertyLevelAssociations( aClass );
	}
	
	private void setupClassLevelAssociations( Class aClass )
	{}
	
	private void setupPropertyLevelAssociations( Class aClass )
	{}
}
