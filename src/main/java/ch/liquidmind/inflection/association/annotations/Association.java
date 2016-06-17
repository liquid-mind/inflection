package ch.liquidmind.inflection.association.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 * Used for denoting a binary association.
 * This annotation may be used to specifically define a unidirectional or bidirectional association, whereby
 * the former is the default for any property not specifically referenced by an association annotation.
 * Associations are always owned by a class, but they may be declared at either the class or property level. In the
 * former case, both sides of the association must be specified via the *self* and *other* attributes with the
 * following constraints:
 *   1. self.theClass must be either *UNSPECIFIED_CLASS* or owning class.
 *   2. self.name must reference a property within the owning class.
 *   3. other.theClass must be either *UNSPECIFIED_CLASS* or be type compatible with type of the property as
 *      specified either explicitly via Property.type or implicitly via the generic parameter of the property.
 *   4. other.name must reference a property within the other class.
 * In the latter case, only the other side need be specified which must follow constraints 3. and 4. above.
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface Association
{
	public String name() default "";
	public String[] memberEnds() default {};
	public String otherEnd() default "";
	public boolean isAssociationClass() default false;
}
