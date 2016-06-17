package ch.liquidmind.inflection.test.assoc.model;

import ch.liquidmind.inflection.association.Aggregation;
import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Property;

public abstract class Wheel extends PowerTransmitter
{
	@Association( otherEnd = "wheel" )
	@Property( aggregation = Aggregation.COMPOSITE, redefines = "vehicleConfiguration" )
	public abstract CarConfiguration getCarConfiguration();
	public abstract void setCarConfiguration( CarConfiguration carConfiguration );
}
