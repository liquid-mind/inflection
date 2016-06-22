package ch.liquidmind.inflection.association;

import java.util.Map;

public class Pass2Scanner extends AbstractScanner
{
	public Pass2Scanner( Map< String, Class > classes )
	{
		super( classes );
	}

	public void scan()
	{
		getClasses().values().forEach( aClass -> setupClass( aClass ) );
	}
	
	private void setupClass( Class aClass )
	{
		aClass.setSuperClass( determineSuperClass( aClass ) );
		aClass.setOwningClass( determineOwningClass( aClass ) );
	}
	
	private Class determineSuperClass( Class aClass )
	{
		return getClasses().get( aClass.getTargetClass().getSuperclass().getName() );
	}
	
	private Class determineOwningClass( Class aClass )
	{
		java.lang.Class< ? > owningClass = aClass.getTargetClass().getEnclosingClass();
		String owningClassName = ( owningClass == null ? null : owningClass.getName() );
		
		return getClasses().get( owningClassName );
	}
}
