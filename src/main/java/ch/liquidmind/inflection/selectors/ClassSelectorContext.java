package ch.liquidmind.inflection.selectors;

import java.util.Set;

import ch.liquidmind.inflection.loader.TaxonomyLoader;

public class ClassSelectorContext extends SelectorContext
{
	public ClassSelectorContext( TaxonomyLoader loader, Set< Class< ? > > selectableClasses, Class< ? > currentClass )
	{
		super( loader, selectableClasses, currentClass );
	}
}
