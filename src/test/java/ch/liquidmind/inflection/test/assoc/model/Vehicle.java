package ch.liquidmind.inflection.test.assoc.model;

import ch.liquidmind.inflection.association.annotations.Association;
import ch.liquidmind.inflection.association.annotations.Property;
import ch.liquidmind.inflection.association.annotations.Property.Aggregation;

// Alternative annotation at class level where both properties are referenced:
// @Association( memberEnds = { "powerSource", "vehicle" } )
public abstract class Vehicle
{
	// The aggregation type of properties is governed by the following rules:
	// 1. If a property does *not* use a @Property annotation:
	//   1.1. If that property is *not* referenced by an association --> aggregation = Aggregation.COMPOSITE.
	//   2.1. If that property *is* referenced by an association --> aggregation = Aggregation.NONE.
	// 2. If a property *does* use a @Property annotation:
	//   2.1. If a value *is* specified for aggregation --> aggregation = the specified value.
	//   2.2. If a value is not specified for aggregation --> aggregation = Aggregation.NONE.
	public abstract String getName();
	public abstract void setName( String name );
	
	// The member ends of associations are governed by the following rules:
	// 1. The attributes memberEnds and otherEnd are mutually exclusive.
	// 2. The attribute otherEnd may only be used with associations declared at the property level.
	// 3. The first element of memberEnds always refers to a property in the owning class of the association.
	// 4. The type attribute of the first memberEnd element may be either UNSPECIFIED_CLASS.class or the owning class.
	// 5. If the association is declared at the class level:
	//   5.1. If the class represents an association class (isAssociationClass == true) --> the member ends
	//        are assumed to point to properties in this class.
	//   5.2. Otherwise, the first member end is a property in this class and the second is a property in
	//        the class of the property referenced by the first member end.
	// 6. If the association is declared at the property level:
	//   6.1. If otherEnd is specified, then it refers to a property in the class of this property.
	//   6.2. If memberEnds are specified, then the first must be this property itself and the second is a property
	//        in the class of this property.
	
	// Alternative annotation where this end is (redundantly) specified:
	// @Association( memberEnds = { "powerSource", "vehicle" } )
	// Short form of specifying both ends, where this end is inferred from subsequent property declaration.
	@Association( otherEnd = "vehicle" )
	@Property( aggregation = Aggregation.COMPOSITE )
	public abstract PowerSource getPowerSource();
	public abstract void setPowerSource( PowerSource powerSource );
	
	// The @Association annotation is not necessary for unidirectional associations.
	@Property( aggregation = Aggregation.COMPOSITE )
	public abstract PowerTransmitter getPowerTransmitter();
	public abstract void setPowerTransmitter( PowerTransmitter powerTransmitter );
}
