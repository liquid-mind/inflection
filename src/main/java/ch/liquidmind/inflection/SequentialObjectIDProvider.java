package ch.liquidmind.inflection;


public class SequentialObjectIDProvider implements ObjectIDProvider< Long >
{
	private static long idCursor = 0;

	@Override
	public Long createObjectID()
	{
		return idCursor++;
	}
}
