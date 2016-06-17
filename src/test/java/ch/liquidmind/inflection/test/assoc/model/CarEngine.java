package ch.liquidmind.inflection.test.assoc.model;

import ch.liquidmind.inflection.association.annotations.Property;

public abstract class CarEngine extends PowerSource
{
	@Property( redefines = "vehicle" )
	public abstract Car getCar();
	public abstract void setCar( Car car );
}
