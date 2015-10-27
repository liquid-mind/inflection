package ch.liquidmind.inflection.model.external;

public interface AliasableElement extends SelectingElement
{
	public String getAlias();
	public String getNameOrAlias();
	public String getSimpleNameOrAlias();
}
