package ch.liquidmind.inflection.test.assoc.model;

import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Property;
import ch.liquidmind.inflection.association.annotations.Property.Aggregation;

public abstract class Car extends Vehicle
{
	@Association( otherEnd = "car" )
	@Property( aggregation = Aggregation.COMPOSITE, redefines = "powerSource" )
	public abstract CarEngine getCarEngine();
	public abstract void setCarEngine( CarEngine carEngine );
	
	@Property( aggregation = Aggregation.COMPOSITE, redefines = "powerTransmitter" )
	public abstract Wheel getWheel();
	public abstract void setWheel( Wheel wheel );
}
