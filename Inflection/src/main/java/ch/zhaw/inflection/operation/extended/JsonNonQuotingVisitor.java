package ch.zhaw.inflection.operation.extended;

import ch.zhaw.inflection.IdentifiableObject;

public class JsonNonQuotingVisitor extends JsonTerminalVisitor
{
	@Override
	protected String getMemberValue( IdentifiableObject< ?, ? > iObject )
	{
		return iObject.getObject().toString();
	}
}
