package ch.zhaw.inflection;

import ch.zhaw.inflection.model.ClassView;

// TODO Look through code base for references of basic types and replace
// with these constants.
public class BasicTypes
{
	private static final String VIEW = "View";

	public static final ClassView< ? > byteView = getClassView( byte.class );
	public static final ClassView< ? > shortView = getClassView( short.class );
	public static final ClassView< ? > intView = getClassView( int.class );
	public static final ClassView< ? > longView = getClassView( long.class );
	public static final ClassView< ? > floatView = getClassView( float.class );
	public static final ClassView< ? > doubleView = getClassView( double.class );
	public static final ClassView< ? > charView = getClassView( char.class );
	public static final ClassView< ? > booleanView = getClassView( boolean.class );
	
	private static ClassView< ? > getClassView( Class< ? > javaType )
	{
		return getClassView( javaType.getSimpleName() );
	}
	
	private static ClassView< ? > getClassView( String name )
	{
		try
		{
			return InflectionResourceLoader.getSystemInflectionResourceLoader().loadClassView( name + VIEW );
		}
		catch ( ClassViewNotFoundException e )
		{
			throw new IllegalStateException( e );
		}
	}
}
