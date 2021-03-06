package ch.liquidmind.inflection.test.association.model;

import ch.liquidmind.inflection.association.annotations.Property;

public abstract class CarConfiguration extends VehicleConfiguration
{
	@Property( redefines = "vehicle" )
	public abstract Car getCar();
	
	@Property( redefines = "powerSource" )
	public abstract CarEngine getCarEngine();
	
	@Property( redefines = "powerTransmitter" )
	public abstract Wheel getWheel();
}
