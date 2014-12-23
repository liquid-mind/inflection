package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.IdentifiableObject;

public class JsonNonQuotingVisitor extends JsonTerminalVisitor
{
	@Override
	protected String getMemberValue( IdentifiableObject< ?, ? > iObject )
	{
		return iObject.getObject().toString();
	}
}
