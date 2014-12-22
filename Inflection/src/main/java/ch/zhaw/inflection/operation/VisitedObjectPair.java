package ch.zhaw.inflection.operation;


public class VisitedObjectPair
{
	private IdentifiableObjectPair identifiableObjectPair;
	private int visitCount;
	
	public VisitedObjectPair( IdentifiableObjectPair identifiableObjectPair )
	{
		super();
		this.identifiableObjectPair = identifiableObjectPair;
		this.visitCount = 0;
	}

	public void incrementVisitCount()
	{
		++visitCount;
	}

	public int getVisitCount()
	{
		return visitCount;
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( identifiableObjectPair.getLeftObject() == null ) ? 0 : identifiableObjectPair.getLeftObject().hashCode() );
		result = prime * result + ( ( identifiableObjectPair.getRightObject() == null ) ? 0 : identifiableObjectPair.getRightObject().hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		VisitedObjectPair other = (VisitedObjectPair)obj;
		if ( identifiableObjectPair.getLeftObject() == null )
		{
			if ( other.identifiableObjectPair.getLeftObject() != null )
				return false;
		}
		else if ( !identifiableObjectPair.getLeftObject().equals( other.identifiableObjectPair.getLeftObject() ) )
			return false;
		if ( identifiableObjectPair.getRightObject() == null )
		{
			if ( other.identifiableObjectPair.getRightObject() != null )
				return false;
		}
		else if ( !identifiableObjectPair.getRightObject().equals( other.identifiableObjectPair.getRightObject() ) )
			return false;
		return true;
	}	
}
