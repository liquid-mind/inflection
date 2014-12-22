package ch.liquidmind.inflection;

import java.util.UUID;

public class UUIDObjectIDProvider implements ObjectIDProvider< UUID >
{
	@Override
	public UUID createObjectID()
	{
		return UUID.randomUUID();
	}
}
