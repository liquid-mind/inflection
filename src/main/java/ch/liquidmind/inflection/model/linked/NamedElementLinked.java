package ch.liquidmind.inflection.model.linked;

import ch.liquidmind.inflection.compiler.AbstractInflectionListener;

public class NamedElementLinked
{
	private String name;

	public NamedElementLinked( String name )
	{
		super();
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public static String getPackageName( String name )
	{
		return name.contains( "." ) ? name.substring( 0, name.lastIndexOf( "." ) ) : AbstractInflectionListener.DEFAULT_PACKAGE_NAME;
	}

	public static String getSimpleName( String name )
	{
		return name.contains( "." ) ? name.substring( name.lastIndexOf( "." ) + 1 ) : name;
	}
}
