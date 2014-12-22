package ch.zhaw.inflection.operation.extended;

import ch.zhaw.inflection.IdentifiableObject;

public class JsonQuotingVisitor extends JsonTerminalVisitor
{
	@Override
	protected String getMemberValue( IdentifiableObject< ?, ? > iObject )
	{
		return "\"" + iObject.getObject().toString() + "\"";
	}
}
