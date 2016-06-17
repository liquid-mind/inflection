package ch.liquidmind.inflection.test.assoc.model;

import java.util.Set;

import ch.liquidmind.inflection.association.Aggregation;
import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Dimension;
import ch.liquidmind.inflection.association.annotations.Property;

// Alternative annotation at class level where both properties are referenced:
// @Association( selfEnd = "powerSource", otherEnd = "vehicle" )
public abstract class Vehicle
{
	// Aggregation type rules:
	// 1. If a property does *not* use a @Property annotation --> aggregation = Aggregation.COMPOSITE.
	// 2. If a property *does* use a @Property annotation:
	//   2.1. If a value *is* specified for aggregation --> aggregation = the specified value.
	//   2.2. If a value is not specified for aggregation --> aggregation = Aggregation.NONE.
	// Type rules:
	// 1. If the property type is known from the signature (either directly or via generic parameter of
	//    then type may be INFERED or the property type.
	// 2. Otherwise, type may *not* be INFERED but may be any other type.
	// Redefinition rules:
	// 1. The redefined property must exist somewhere in the class hierarchy.
	// 2. Overriding properties are automatically interpreted as redefining properties (overriding is a
	// subset of redefinition).
	// 3. The redefined property is always the leaf in an overriding hierarchy.
	// 4. Only non-final properties may be redefined.
	// Subsetting rules:
	// 1. The subsetted property must exist somewhere in the hierarchy of the other class.
	// 2. The subsetted property is always the leaf in an overriding hierarchy.
	// Dimension rules:
	// 1. If the dimensions are implemented using an array or using one or more collections with generic
	//    parameters --> The size of the dimension array must be equal to the declared size.
	// 2. For properties using direct references (i.e., no arrays or collections), the number of dimensions
	//    must be one and the multiplicity <= 1.
	public abstract String getName();
	public abstract void setName( String name );
	
	// The member ends of associations are governed by the following rules:
	// 1. If the association is declared at the class level then selfEnd must refer to a declared property of
	//    the class; otherwise, the selfEnd must be unspecified or equal to the associated property.
	// 2. If otherEnd is specified then is refers to a property in the other class.
	// Alternative annotation where this end is (redundantly) specified:
	// @Association( selfEnd = "powerSource", otherEnd = "vehicle" )
	// Alternative annotation where this end is specifically set to the empty string (signifying "unspecified")
	// @Association( selfEnd = "", otherEnd = "vehicle" )
	// Short form of specifying both ends, where this end is inferred from subsequent property declaration.
	@Association( otherEnd = "vehicle" )
	@Property( aggregation = Aggregation.COMPOSITE )
	public abstract PowerSource getPowerSource();
	public abstract void setPowerSource( PowerSource powerSource );
	
	// The @Association annotation is not necessary for unidirectional associations.
	@Property( aggregation = Aggregation.COMPOSITE, dimensions = @Dimension( multiplicity = "1..*" ) )
	public abstract Set< PowerTransmitter > getPowerTransmitters();
	
	@Association( otherEnd = "vehicle" )
	@Property( aggregation = Aggregation.COMPOSITE )
	public abstract VehicleConfiguration getVehicleConfiguration();
	public abstract void setVehicleConfiguration( VehicleConfiguration vehicleConfiguration );
}
