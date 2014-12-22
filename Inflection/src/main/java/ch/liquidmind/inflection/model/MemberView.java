package ch.liquidmind.inflection.model;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public abstract class MemberView extends InflectionView
{
	public static final Aggregation DEFAULT_AGGREGATION = Aggregation.Composite;
	
	private ClassView< ? > referencedClassView;
	private Aggregation aggregation;

	public MemberView()
	{
		super();
	}
	
	public MemberView( String name, ClassView< ? > owingClassView )
	{
		this( name, owingClassView, null, DEFAULT_AGGREGATION );
	}

	public MemberView( String name, ClassView< ? > owingClassView, ClassView< ? > referencedClassView, Aggregation aggregation )
	{
		super( name );
		this.referencedClassView = referencedClassView;
		setOwningClassView( owingClassView );
		createDimensionViews( getDimensionViews(), this, true, getRawMemberType() );
		this.aggregation = aggregation;
	}
	
	public static void createDimensionViews( List< DimensionView > dimensionViews, MemberView owningMemberView, boolean isStatic, Type type )
	{
		if ( type instanceof Class )
			createDimensionViews( dimensionViews, owningMemberView, isStatic, (Class< ? >)type );
		else if ( type instanceof ParameterizedType )
			createDimensionViews( dimensionViews, owningMemberView, isStatic, (ParameterizedType)type );
		else if ( type instanceof GenericArrayType )
			createDimensionViews( dimensionViews, owningMemberView, isStatic, (GenericArrayType)type );
		else if ( type instanceof TypeVariable )
			createDimensionViews( dimensionViews, owningMemberView, isStatic, (TypeVariable< ? >)type );
		else if ( type instanceof WildcardType )
			createDimensionViews( dimensionViews, owningMemberView, isStatic, (WildcardType)type );
		else
			throw new IllegalStateException( "Unexpected type for type: " + type.getClass().getName() );
	}
	
	private static void createDimensionViews( List< DimensionView > dimensionViews, MemberView owningMemberView, boolean isStatic, Class< ? > theClass )
	{
		if ( Collection.class.isAssignableFrom( theClass ) || Map.class.isAssignableFrom( theClass ) )
		{
			boolean isOrdered;
			boolean isMapped;
			
			if ( List.class.isAssignableFrom( theClass ) )
				isOrdered = true;
			else
				isOrdered = false;
			
			if ( Map.class.isAssignableFrom( theClass ) )
				isMapped = true;
			else
				isMapped = false;
			
			dimensionViews.add( new DimensionView( isOrdered, isMapped, false, Multiplicity.Many, theClass, owningMemberView ) );
			createDimensionViews( dimensionViews, owningMemberView, isStatic, Object.class );
		}
		else if ( theClass.isArray() )
		{
			addDimensionView( dimensionViews, new DimensionView( true, false, isStatic, Multiplicity.Many, theClass, owningMemberView ) );
			createDimensionViews( dimensionViews, owningMemberView, isStatic, theClass.getComponentType() );
		}
		else
		{
			addDimensionView( dimensionViews, new DimensionView( false, false, isStatic, Multiplicity.One, theClass, owningMemberView ) );
		}
	}
	
	private static void createDimensionViews( List< DimensionView > dimensionViews, MemberView owningMemberView, boolean isStatic, ParameterizedType parameterizedType )
	{
		Class< ? > rawType = (Class< ? >)parameterizedType.getRawType();
		Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();

		if ( Collection.class.isAssignableFrom( rawType ) || Map.class.isAssignableFrom( rawType ) )
		{
			boolean isOrdered;
			boolean isMapped;
			Type type;
			
			if ( List.class.isAssignableFrom( rawType ) )
				isOrdered = true;
			else
				isOrdered = false;
			
			if ( Map.class.isAssignableFrom( rawType ) )
			{
				isMapped = true;
				type = actualTypeArgs[ 1 ];
			}
			else
			{
				isMapped = false;
				type = actualTypeArgs[ 0 ];
			}
			
			addDimensionView( dimensionViews, new DimensionView( isOrdered, isMapped, false, Multiplicity.Many, rawType, owningMemberView ) );
			createDimensionViews( dimensionViews, owningMemberView, isStatic, type );
		}
		else
		{
			addDimensionView( dimensionViews, new DimensionView( false, false, false, Multiplicity.One, rawType, owningMemberView ) );
		}
	}
	
	private static void createDimensionViews( List< DimensionView > dimensionViews, MemberView owningMemberView, boolean isStatic, GenericArrayType genericArrayType )
	{
		addDimensionView( dimensionViews, new DimensionView( true, false, isStatic, Multiplicity.Many, genericArrayType, owningMemberView ) );
		createDimensionViews( dimensionViews, owningMemberView, isStatic, genericArrayType.getGenericComponentType() );
	}

	private static void createDimensionViews( List< DimensionView > dimensionViews, MemberView owningMemberView, boolean isStatic, TypeVariable< ? > typeVariable )
	{
		addDimensionView( dimensionViews, new DimensionView( false, false, isStatic, Multiplicity.One, typeVariable, owningMemberView ) );
	}

	private static void createDimensionViews( List< DimensionView > dimensionViews, MemberView owningMemberView, boolean isStatic, WildcardType wildcardType )
	{
		addDimensionView( dimensionViews, new DimensionView( false, false, isStatic, Multiplicity.One, wildcardType, owningMemberView ) );
	}
	
	private static void addDimensionView( List< DimensionView > dimensionViews, DimensionView dimensionView )
	{
		if ( dimensionViews.size() > 0 && dimensionView.getMultiplicity().equals( Multiplicity.One ) )
			return;
		
		dimensionViews.add( dimensionView );
	}
	
	public abstract Type getRawMemberType();
	public abstract < T > T getMemberInstance( Object containingObject );

	public ClassView< ? > getOwningClassView()
	{
		return (ClassView< ? >)getParentView();
	}

	public void setOwningClassView( ClassView< ? > owingClassView )
	{
		setParentView( owingClassView );
	}
	
	@SuppressWarnings( "unchecked" )
	public List< DimensionView > getDimensionViews()
	{
		return (List< DimensionView >)(Object)getChildViews();
	}

	public DimensionView getInitialDimensionView()
	{
		return getDimensionViews().get( 0 );
	}
	
	public DimensionView getNextDimensionView( DimensionView dimensionView )
	{
		DimensionView nextDimensionView = null;
		int currentViewIndex = getDimensionViews().lastIndexOf( dimensionView );
		int nextViewIndex = currentViewIndex + 1;
		
		if ( nextViewIndex <= getDimensionViews().size() - 1 )
			nextDimensionView = getDimensionViews().get( nextViewIndex );

		return nextDimensionView;
	}
	
	public InflectionView getNextView( DimensionView dimensionView )
	{
		InflectionView nextView = getNextDimensionView( dimensionView );
		
		if ( nextView == null )
			nextView = referencedClassView;
		
		return nextView;
	}
	
	public ClassView< ? > getReferencedClassView()
	{
		return referencedClassView;
	}

	public void setReferencedClassView( ClassView< ? > referencedClassView )
	{
		this.referencedClassView = referencedClassView;
	}

	public Aggregation getAggregation()
	{
		return aggregation;
	}

	public void setAggregation( Aggregation aggregation )
	{
		this.aggregation = aggregation;
	}
}
