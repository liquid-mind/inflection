package ch.liquidmind.inflection.model.compiled;

public abstract class AliasableElementCompiled extends SelectingElementCompiled
{
	private static final long serialVersionUID = 1L;

	private String alias;

	public AliasableElementCompiled( String name )
	{
		super( name );
	}

	public String getAlias()
	{
		return alias;
	}

	public void setAlias( String alias )
	{
		this.alias = alias;
	}
}
