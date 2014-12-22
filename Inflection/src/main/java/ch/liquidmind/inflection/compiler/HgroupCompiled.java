package ch.liquidmind.inflection.compiler;

import java.util.ArrayList;
import java.util.List;

public class HgroupCompiled extends InflectionResourceCompiled
{
	private static final long serialVersionUID = 1L;

	private String extendedHgroupName;
	private List< String > classViewNames = new ArrayList< String >();
	
	public HgroupCompiled( String name )
	{
		super( name );
	}

	public String getExtendedHgroupName()
	{
		return extendedHgroupName;
	}

	public void setExtendedHgroupName( String extendedHgroupName )
	{
		this.extendedHgroupName = extendedHgroupName;
	}

	public List< String > getClassViewNames()
	{
		return classViewNames;
	}
}
