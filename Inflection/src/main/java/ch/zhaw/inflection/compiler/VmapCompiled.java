package ch.zhaw.inflection.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VmapCompiled extends InflectionResourceCompiled
{
	private static final long serialVersionUID = 1L;
	
	public static class MappingCompiled implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private VmapCompiled vmapCompiled;
		private String inflectionViewName;
		private String visitorClassName;
		
		public MappingCompiled( VmapCompiled vmapCompiled )
		{
			super();
			this.vmapCompiled = vmapCompiled;
		}

		public VmapCompiled getVmapCompiled()
		{
			return vmapCompiled;
		}

		public void setVmapCompiled( VmapCompiled vmapCompiled )
		{
			this.vmapCompiled = vmapCompiled;
		}

		public String getInflectionViewName()
		{
			return inflectionViewName;
		}

		public void setInflectionViewName( String inflectionViewName )
		{
			this.inflectionViewName = inflectionViewName;
		}

		public String getVisitorClassName()
		{
			return visitorClassName;
		}

		public void setVisitorClassName( String visitorClassName )
		{
			this.visitorClassName = visitorClassName;
		}
	}

	public VmapCompiled( String name )
	{
		super( name );
	}
	
	private String extendedVmapName;
	private List< MappingCompiled > classViewToVisitorMappings = new ArrayList< MappingCompiled >();
	private String defaultVisitorClassName;

	public String getExtendedVmapName()
	{
		return extendedVmapName;
	}

	public void setExtendedVmapName( String extendedVmapName )
	{
		this.extendedVmapName = extendedVmapName;
	}

	public void setClassViewToVisitorMappings( List< MappingCompiled > classViewToVisitorMappings )
	{
		this.classViewToVisitorMappings = classViewToVisitorMappings;
	}

	public List< MappingCompiled > getClassViewToVisitorMappings()
	{
		return classViewToVisitorMappings;
	}

	public String getDefaultVisitorClassName()
	{
		return defaultVisitorClassName;
	}

	public void setDefaultVisitorClassName( String defaultVisitorClassName )
	{
		this.defaultVisitorClassName = defaultVisitorClassName;
	}
}
