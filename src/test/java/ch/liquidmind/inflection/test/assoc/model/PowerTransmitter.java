package ch.liquidmind.inflection.test.assoc.model;

import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Property;
import ch.liquidmind.inflection.association.annotations.Property.Aggregation;

public abstract class PowerTransmitter
{
	@Association( otherEnd = "powerTransmitter" )
	@Property( aggregation = Aggregation.COMPOSITE )
	public abstract VehicleConfiguration getVehicleConfiguration();
	public abstract void setVehicleConfiguration( VehicleConfiguration vehicleConfiguration );
}
