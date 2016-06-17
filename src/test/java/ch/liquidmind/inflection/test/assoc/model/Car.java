package ch.liquidmind.inflection.test.assoc.model;

import java.util.Set;

import ch.liquidmind.inflection.association.Aggregation;
import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Dimension;
import ch.liquidmind.inflection.association.annotations.Property;

public abstract class Car extends Vehicle
{
	@Association( otherEnd = "car" )
	@Property( aggregation = Aggregation.COMPOSITE, redefines = "powerSource" )
	public abstract CarEngine getCarEngine();
	public abstract void setCarEngine( CarEngine carEngine );
	
	@Property( aggregation = Aggregation.COMPOSITE, redefines = "powerTransmitter", dimensions = @Dimension( multiplicity = "4" ) )
	public abstract Set< Wheel > getWheels();
	
	@Association( otherEnd = "car" )
	@Property( aggregation = Aggregation.COMPOSITE, redefines = "vehicleConfiguration" )
	public abstract CarConfiguration getCarConfiguration();
	public abstract void setCarConfiguration( CarConfiguration carConfiguration );
}
