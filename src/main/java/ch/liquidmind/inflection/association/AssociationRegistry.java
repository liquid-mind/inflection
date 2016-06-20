package ch.liquidmind.inflection.association;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath.ClassInfo;

import ch.liquidmind.inflection.exception.ExceptionWrapper;

public class AssociationRegistry
{
	private static Map< String, Class > registeredClasses = new HashMap< String, Class >();
	
	public static void scan( String ... classNameFilters )
	{
		scan( Thread.currentThread().getContextClassLoader(), classNameFilters );
	}
	
	public static void scan( ClassLoader loader, String ... classNameFilters )
	{
		Set< ClassInfo > allClasses = ExceptionWrapper.ClassPath_from( loader ).getAllClasses();
		Set< ClassInfo > filteredClasses = allClasses.stream().filter( classInfo -> classInfoMatchesFilter( classInfo, classNameFilters ) ).collect( Collectors.toSet() );
		
		Pass1Scanner pass1Scanner = new Pass1Scanner( filteredClasses, registeredClasses );
		Pass2Scanner pass2Scanner = new Pass2Scanner( registeredClasses );
		pass1Scanner.scan();
		pass2Scanner.scan();
	}
	
	private static boolean classInfoMatchesFilter( ClassInfo classInfo, String[] classNameFilters )
	{
		return Arrays.asList( classNameFilters ).stream().filter(
			classNameFilter -> classInfo.getName().matches( classNameFilter ) ).findFirst().isPresent();
	}

	public static Class getRegisteredClass( String name )
	{
		return registeredClasses.get( name );
	}
}
