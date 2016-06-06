package ch.liquidmind.inflection.selectors;

import java.util.Set;

public class ClassSelectorContext
{
	private static ThreadLocal< ClassSelectorContext > classSelectorContextThreadLocal = new ThreadLocal< ClassSelectorContext >();
	
	public static void set( ClassSelectorContext classSelectorContext )
	{
		classSelectorContextThreadLocal.set( classSelectorContext );
	}
	
	public static ClassSelectorContext get()
	{
		return classSelectorContextThreadLocal.get();
	}
	
	// TODO: introduce a compiler option for filtering the class path classes
	// --> would reduce the number of classes to evaluate by one or more order
	// of magnitudes. For this reason I am calling this field allClasses
	// rather than allClassesInClassPath in anticipation of this change.
	private Set< Class< ? > > allClasses;
	private Class< ? > currentClass;
	
	public ClassSelectorContext( Set< Class< ? > > allClasses, Class< ? > currentClass )
	{
		super();
		this.allClasses = allClasses;
		this.currentClass = currentClass;
	}

	public Set< Class< ? > > getAllClasses()
	{
		return allClasses;
	}

	public Class< ? > getCurrentClass()
	{
		return currentClass;
	}
}
