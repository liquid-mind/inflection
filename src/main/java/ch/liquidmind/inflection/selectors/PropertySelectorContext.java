package ch.liquidmind.inflection.selectors;

import java.beans.PropertyDescriptor;
import java.util.Set;

import ch.liquidmind.inflection.loader.TaxonomyLoader;

public class PropertySelectorContext extends MemberSelectorContext
{
	private Set< PropertyDescriptor > selectableProperties;
	private PropertyDescriptor currentProperty;
	
	public PropertySelectorContext( TaxonomyLoader loader, Set< Class< ? > > selectableClasses, Class< ? > currentClass, Class< ? > currentAuxiliaryClass, Set< PropertyDescriptor > selectableProperties, PropertyDescriptor currentProperty )
	{
		super( loader, selectableClasses, currentClass, currentAuxiliaryClass );
		this.selectableProperties = selectableProperties;
		this.currentProperty = currentProperty;
	}

	public Set< PropertyDescriptor > getSelectableProperties()
	{
		return selectableProperties;
	}

	public PropertyDescriptor getCurrentProperty()
	{
		return currentProperty;
	}
}
