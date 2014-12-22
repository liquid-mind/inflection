package ch.zhaw.inflection.model;

import java.util.ArrayList;
import java.util.List;

import ch.zhaw.inflection.InflectionResourceLoader;

// TODO Should ClassViews be constructable outside of inflection resource loaders?
// If not, need to make constructors non-visible.
public class ClassView< ObjectType > extends InflectionView implements InflectionResource
{
	public static final String PROPERTY_VIEW = "property";
	public static final String FIELD_VIEW = "field";
	
	private InflectionResourceLoader inflectionResourceLoader;
	private Class< ObjectType > javaClass;
	private ClassView< ? > extendedClassView;
	private List< MemberView > aggregateMemberViews;
	
	public ClassView( String name, InflectionResourceLoader inflectionResourceLoader )
	{
		this( name, null, inflectionResourceLoader );
	}
	
	public ClassView( String name, Class< ? > javaClass, InflectionResourceLoader inflectionResourceLoader )
	{
		this( name, javaClass, null, inflectionResourceLoader );
	}

	@SuppressWarnings( "unchecked" )
	public ClassView( String name, Class< ? > javaClass, ClassView< ? > extendedClassView, InflectionResourceLoader inflectionResourceLoader )
	{
		super( name );
		this.javaClass = (Class< ObjectType >)javaClass;
		this.extendedClassView = extendedClassView;
		this.inflectionResourceLoader = inflectionResourceLoader;
	}
	
	public ClassView< ? > forName( String className )
	{
		return forName( className, inflectionResourceLoader );
	}

	public static ClassView< ? > forName( String className, InflectionResourceLoader loader )
	{
		return loader.loadClassView( className );
	}

	public InflectionResourceLoader getInflectionResourceLoader()
	{
		return inflectionResourceLoader;
	}

	public String getSimpleName()
	{
		String simpleName = getName();
		int lastIndexOf = getName().lastIndexOf( "." );
		
		if ( lastIndexOf != -1 )
			simpleName = getName().substring( lastIndexOf + 1 );

		return simpleName;
	}
	
	public Class< ObjectType > getJavaClass()
	{
		return javaClass;
	}

	@SuppressWarnings( "unchecked" )
	public void setJavaClass( Class< ? > javaClass )
	{
		this.javaClass = (Class< ObjectType >)javaClass;
	}

	public ClassView< ? > getExtendedClassView()
	{
		return extendedClassView;
	}

	public void setExtendedClassView( ClassView< ? > extendedClassView )
	{
		this.extendedClassView = extendedClassView;
	}
	
	public List< MemberView > getMemberViews()
	{
		// Aggregate member views cannot change within the 
		// life-cycle of a class view and may therefore be cached.
		if ( aggregateMemberViews == null )
			aggregateMemberViews = getAggregateMemberViews();
		
		return aggregateMemberViews;
	}
	
	private List< MemberView > getAggregateMemberViews()
	{
		List< MemberView > aggregateMemberViews = new ArrayList< MemberView >();
		
		if ( extendedClassView != null )
			aggregateMemberViews.addAll( extendedClassView.getMemberViews() );
		
		// This logic ensures that any members from super class views
		// are overridden.
		List< MemberView > overridableMemberViews = new ArrayList< MemberView >();
		for( MemberView memberView : getDeclaredMemberViews() )
			for ( MemberView aggregateMemberView : aggregateMemberViews )
				if ( aggregateMemberView.getName().equals( memberView.getName() ) )
					overridableMemberViews.add( aggregateMemberView );

		aggregateMemberViews.removeAll( overridableMemberViews );
		
		aggregateMemberViews.addAll( getDeclaredMemberViews() );
		
		return aggregateMemberViews;
	}
	
	@SuppressWarnings( "unchecked" )
	public List< MemberView > getDeclaredMemberViews()
	{
		return (List< MemberView >)(Object)getChildViews();
	}

	public MemberView getMemberView( String name )
	{
		MemberView foundView = getDeclaredMemberView( name );
		
		if ( foundView == null && extendedClassView != null )
			foundView = extendedClassView.getMemberView( name );
			
		return foundView;
	}
	
	public MemberView getDeclaredMemberView( String name )
	{
		MemberView foundView = null;
		
		for ( MemberView memberView : getDeclaredMemberViews() )
		{
			if ( memberView.getName().equals( name ) )
			{
				foundView = memberView;
				break;
			}
		}
		
		return foundView;
	}
	
	@Override
	public String toString()
	{
		String classViewName = getName();
		String extendsClause = ( extendedClassView == null ? "" : TAB + "extends " + extendedClassView.getName() + CARRIAGE_RETURN );
		
		Class< ? > javaClass = getJavaClass();
		String viewofClause = TAB + "of " + javaClass.getName() + CARRIAGE_RETURN;
		
		String classHeader = "view " + classViewName + CARRIAGE_RETURN;
		classHeader += viewofClause;
		classHeader += extendsClause;
		
		String classBody = "{" + CARRIAGE_RETURN;
		classBody += classViewBodyToString();
		classBody += "}" + CARRIAGE_RETURN;
		
		String fullString = classHeader + classBody;

		return fullString;
	}
	
	private String classViewBodyToString()
	{
		String classViewBody = "";
		
		for ( MemberView memberView : getMemberViews() )
			classViewBody += memberToString( memberView );
		
		return classViewBody;
	}
	
	private String memberToString( MemberView memberView )
	{
		String member = "";
		
		String memberTypeClause = getMemberType( memberView ) + " ";
		
		String typeAndName = memberView.getReferencedClassView().getName() + " " + memberView.getName();
		
		Aggregation aggregation = memberView.getAggregation();
		String aggregationClause = ( aggregation == null || aggregation.equals( Aggregation.Composite ) ? "composite " : "discrete " );
		
		String dimensions = dimensionsToString( memberView.getDimensionViews() );
		
		member += TAB + memberTypeClause + aggregationClause + typeAndName + dimensions + ";" + CARRIAGE_RETURN;
		
		return member;
	}
	
	private String dimensionsToString( List< DimensionView > dimensionViews )
	{
		String dimensions = "";
		
		for ( DimensionView dimensionView : dimensionViews )
			dimensions += dimensionToString( dimensionView );
		
		return dimensions;
	}
	
	private String dimensionToString( DimensionView dimensionView )
	{
		String multiplicity = ( dimensionView.getMultiplicity().equals( Multiplicity.One ) ? "1" : "*" );
		String isOrdered = ( dimensionView.isOrdered() ? "o" : "" );
		String isMapped = ( dimensionView.isMapped() ? "m" : "" );
		String dimension = "[" + multiplicity + isOrdered + isMapped + "]";
		
		return dimension;
	}

	private String getMemberType( MemberView memberView )
	{
		String type;
		
		if ( memberView instanceof PropertyView )
			type = PROPERTY_VIEW;
		else if ( memberView instanceof FieldView )
			type = FIELD_VIEW;
		else
			throw new IllegalStateException( "Unexpected type for memberView: " + memberView.getClass().getName() );
		
		return type;
	}
}
