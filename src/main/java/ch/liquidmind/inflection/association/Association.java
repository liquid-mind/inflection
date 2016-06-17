package ch.liquidmind.inflection.association;

public class Association
{
	private Property selfEnd, otherEnd;
	private boolean isDeclared;
	private Class owningClass;

	public Property getSelfEnd()
	{
		return selfEnd;
	}

	void setSelfEnd( Property selfEnd )
	{
		this.selfEnd = selfEnd;
	}

	public Property getOtherEnd()
	{
		return otherEnd;
	}

	void setOtherEnd( Property otherEnd )
	{
		this.otherEnd = otherEnd;
	}

	public boolean isDeclared()
	{
		return isDeclared;
	}

	void setDeclared( boolean isDeclared )
	{
		this.isDeclared = isDeclared;
	}

	public Class getOwningClass()
	{
		return owningClass;
	}

	void setOwningClass( Class owningClass )
	{
		this.owningClass = owningClass;
	}
}
