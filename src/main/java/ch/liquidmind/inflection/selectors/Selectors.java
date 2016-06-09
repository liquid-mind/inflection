package ch.liquidmind.inflection.selectors;

public class Selectors
{
	public static boolean isAssignableTo( Class< ? > theClass )
	{
		return theClass.isAssignableFrom( ClassSelectorContext.get().getCurrentClass() );
	}
	
	public static boolean hasModifier( int modifier )
	{
		return ( ClassSelectorContext.get().getCurrentClass().getModifiers() & modifier ) > 0;
	}
	
	public static boolean matches( String wildcardExpression )
	{
		// TODO: implement.
		return true;
	}
	
}
