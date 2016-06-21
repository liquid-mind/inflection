package ch.liquidmind.inflection.test.association.model;

import ch.liquidmind.inflection.association.Aggregation;
import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Property;

public abstract class PowerSource
{
	public abstract Vehicle getVehicle();
	public abstract void setVehicle( Vehicle vehicle );
	
	@Association( otherEnd = "powerSource" )
	@Property( aggregation = Aggregation.COMPOSITE )
	public abstract VehicleConfiguration getVehicleConfiguration();
	public abstract void setVehicleConfiguration( VehicleConfiguration vehicleConfiguration );
}
