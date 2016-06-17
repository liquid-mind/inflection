package ch.liquidmind.inflection.test.assoc.model;

import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Property;

@Association( isAssociationClass = true, memberEnds = { "vehicle", "powerSource", "powerTransmitter" } )
public abstract class VehicleConfiguration
{
	@Property
	public abstract Vehicle getVehicle();
	
	@Property
	public abstract PowerSource getPowerSource();
	
	@Property
	public abstract PowerTransmitter getPowerTransmitter();
}
