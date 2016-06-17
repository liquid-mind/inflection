package ch.liquidmind.inflection.test.assoc.model;

import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Property;

@Association( isAssociationClass = true, memberEnds = { "car", "carEngine", "wheel" } )
public abstract class CarConfiguration extends VehicleConfiguration
{
	@Property( redefines = "vehicle" )
	public abstract Car getCar();
	
	@Property( redefines = "powerSource" )
	public abstract CarEngine getCarEngine();
	
	@Property( redefines = "powerTransmitter" )
	public abstract Wheel getWheel();
}
