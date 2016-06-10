package ch.liquidmind.inflection.selectors;

import java.lang.reflect.Field;
import java.util.Set;

import ch.liquidmind.inflection.loader.TaxonomyLoader;

public class FieldSelectorContext extends MemberSelectorContext
{
	private Set< Field > selectableFields;
	private Field currentField;
	
	public FieldSelectorContext( TaxonomyLoader loader, Set< Class< ? > > selectableClasses, Class< ? > currentClass, Class< ? > currentAuxiliaryClass, Set< Field > selectableFields, Field currentField )
	{
		super( loader, selectableClasses, currentClass, currentAuxiliaryClass );
		this.selectableFields = selectableFields;
		this.currentField = currentField;
	}

	public Set< Field > getSelectableFields()
	{
		return selectableFields;
	}

	public Field getCurrentField()
	{
		return currentField;
	}
}
