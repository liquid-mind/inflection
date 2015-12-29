package ch.liquidmind.inflection.model.linked;

import ch.liquidmind.inflection.compiler.AbstractInflectionListener;
import ch.liquidmind.inflection.model.external.AliasableElement;

public abstract class AliasableElementLinked extends SelectingElementLinked implements AliasableElement
{
	private String alias;

	public AliasableElementLinked( String name )
	{
		super( name );
	}

	@Override
	public String getAlias()
	{
		return alias;
	}

	public void setAlias( String alias )
	{
		this.alias = alias;
	}

	@Override
	public String getNameOrAlias()
	{
		String nameOrAlias;
		
		if ( alias == null )
		{
			nameOrAlias = getName();
		}
		else
		{
			String packageName = getPackageName( getName() );
			nameOrAlias = ( packageName == AbstractInflectionListener.DEFAULT_PACKAGE_NAME ? alias : packageName + "." + alias );
		}
			
		return nameOrAlias;
	}

	@Override
	public String getSimpleNameOrAlias()
	{
		String nameOrAlias;
		
		if ( alias == null )
			nameOrAlias = getSimpleName( getName() );
		else
			nameOrAlias = alias;
			
		return nameOrAlias;
	}
}
