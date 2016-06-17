package ch.liquidmind.inflection.support2;

public class RelatedTypeWalker extends TypeWalker
{
	public RelatedTypeWalker()
	{
		super( new AbstractTypeVisitor() {} );
	}

	@Override
	public void walkClass( Class< ? > classType )
	{
		System.out.println( String.format( "classType: %1$s", classType.getName() ) );
		super.walkClass( classType );
	}
}
