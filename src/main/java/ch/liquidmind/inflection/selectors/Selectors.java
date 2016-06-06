package ch.liquidmind.inflection.selectors;

import ch.liquidmind.inflection.ClassSelector;

public class Selectors
{
	@ClassSelector
	public static boolean isAssignableTo( ClassSelectorContext context, Class< ? > theClass )
	{
		return theClass.isAssignableFrom( context.getCurrentClass() );
	}
	
	@ClassSelector
	public static boolean hasModifier( ClassSelectorContext context, int modifier )
	{
		return ( context.getCurrentClass().getModifiers() & modifier ) > 0;
	}
}
