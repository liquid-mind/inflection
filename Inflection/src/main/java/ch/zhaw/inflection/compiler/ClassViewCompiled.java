package ch.zhaw.inflection.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.inflection.model.Aggregation;

public class ClassViewCompiled extends InflectionResourceCompiled
{
	private static final long serialVersionUID = 1L;

	public static class MemberViewCompiled implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public enum Type { Property, Field };
		
		private ClassViewCompiled classViewCompiled;
		private Type type;
		private Aggregation aggregation;
		private String classViewName;
		private String name;
		
		public MemberViewCompiled( ClassViewCompiled classViewCompiled )
		{
			this.classViewCompiled = classViewCompiled;
		}
		
		public ClassViewCompiled getClassViewCompiled()
		{
			return classViewCompiled;
		}

		public void setClassViewCompiled( ClassViewCompiled classViewCompiled )
		{
			this.classViewCompiled = classViewCompiled;
		}

		public Type getType()
		{
			return type;
		}
		
		public void setType( Type type )
		{
			this.type = type;
		}
		
		public Aggregation getAggregation()
		{
			return aggregation;
		}
		
		public void setAggregation( Aggregation aggregation )
		{
			this.aggregation = aggregation;
		}

		public String getClassViewName()
		{
			return classViewName;
		}

		public void setClassViewName( String classViewName )
		{
			this.classViewName = classViewName;
		}

		public String getName()
		{
			return name;
		}

		public void setName( String name )
		{
			this.name = name;
		}
	}

	private String javaClassName;
	private String extendedClassViewName;
	private List< MemberViewCompiled > memberViews = new ArrayList< MemberViewCompiled >();

	public ClassViewCompiled( String name )
	{
		super( name );
	}

	public String getJavaClassName()
	{
		return javaClassName;
	}

	public void setJavaClassName( String javaClassName )
	{
		this.javaClassName = javaClassName;
	}

	public String getExtendedClassViewName()
	{
		return extendedClassViewName;
	}

	public void setExtendedClassViewName( String extendedClassViewName )
	{
		this.extendedClassViewName = extendedClassViewName;
	}

	public List< MemberViewCompiled > getMemberViews()
	{
		return memberViews;
	}
}
