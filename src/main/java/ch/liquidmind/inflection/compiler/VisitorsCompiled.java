package ch.liquidmind.inflection.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VisitorsCompiled extends InflectionResourceCompiled
{
	private static final long serialVersionUID = 1L;
	
	public static class MappingCompiled implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		private VisitorsCompiled visitorsCompiled;
		private String inflectionViewName;
		private String visitorClassName;
		
		public MappingCompiled( VisitorsCompiled visitorsCompiled )
		{
			super();
			this.visitorsCompiled = visitorsCompiled;
		}

		public VisitorsCompiled getVisitorsCompiled()
		{
			return visitorsCompiled;
		}

		public void setVisitorsCompiled( VisitorsCompiled visitorsCompiled )
		{
			this.visitorsCompiled = visitorsCompiled;
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

	public VisitorsCompiled( String name )
	{
		super( name );
	}
	
	private String extendedVisitorsName;
	private List< MappingCompiled > classViewToVisitorMappings = new ArrayList< MappingCompiled >();
	private String defaultVisitorClassName;

	public String getExtendedVisitorsName()
	{
		return extendedVisitorsName;
	}

	public void setExtendedVisitorsName( String extendedVisitorsName )
	{
		this.extendedVisitorsName = extendedVisitorsName;
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
