package ch.liquidmind.inflection.selectors;

import java.util.Set;

import ch.liquidmind.inflection.loader.TaxonomyLoader;

public abstract class SelectorContext
{
	private static ThreadLocal< SelectorContext > selectorContextThreadLocal = new ThreadLocal< SelectorContext >();
	
	public static void set( SelectorContext selectorContext )
	{
		selectorContextThreadLocal.set( selectorContext );
	}
	
	@SuppressWarnings( "unchecked" )
	public static < T extends SelectorContext > T get()
	{
		return (T)selectorContextThreadLocal.get();
	}
	
	// TODO: introduce a compiler option for filtering the class path classes
	// --> would reduce the number of classes to evaluate by one or more order
	// of magnitudes. For this reason I am calling this field allClasses
	// rather than allClassesInClassPath in anticipation of this change.
	private TaxonomyLoader loader;
	private Set< Class< ? > > selectableClasses;
	private Class< ? > currentClass;
	
	public SelectorContext( TaxonomyLoader loader, Set< Class< ? > > selectableClasses, Class< ? > currentClass )
	{
		super();
		this.loader = loader;
		this.selectableClasses = selectableClasses;
		this.currentClass = currentClass;
	}

	public TaxonomyLoader getLoader()
	{
		return loader;
	}

	public Set< Class< ? > > getSelectableClasses()
	{
		return selectableClasses;
	}

	public Class< ? > getCurrentClass()
	{
		return currentClass;
	}
}
