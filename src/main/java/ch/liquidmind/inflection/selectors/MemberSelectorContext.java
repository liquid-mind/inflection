package ch.liquidmind.inflection.selectors;

import java.util.Set;

import ch.liquidmind.inflection.loader.TaxonomyLoader;

public abstract class MemberSelectorContext extends SelectorContext
{
	private Class< ? > currentAuxiliaryClass;

	public MemberSelectorContext( TaxonomyLoader loader, Set< Class< ? > > selectableClasses, Class< ? > currentClass, Class< ? > currentAuxiliaryClass )
	{
		super( loader, selectableClasses, currentClass );
		this.currentAuxiliaryClass = currentAuxiliaryClass;
	}

	public Class< ? > getCurrentAuxiliaryClass()
	{
		return currentAuxiliaryClass;
	}
}
