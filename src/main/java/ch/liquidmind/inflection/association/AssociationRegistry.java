package ch.liquidmind.inflection.association;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath.ClassInfo;

import ch.liquidmind.inflection.exception.ExceptionWrapper;

// TODO: replace RuntimeExceptions with ValidationExceptions.
// TODO: introduce AssociationClass annotation that defines the following constraints:
//   1. The class must define at least two associations.
//   2. The otherEnds of any owned associations must not be composite.
public class AssociationRegistry
{
	private static AssociationRegistry instance = new AssociationRegistry();
	// TODO: make the key java.lang.Class instead of String.
	private Map< String, Class > registeredClasses = new HashMap< String, Class >();
	
	public static AssociationRegistry instance()
	{
		return instance;
	}
	
	public void scan( String[] includeFilters, String[] excludeFilters )
	{
		scan( Thread.currentThread().getContextClassLoader(), includeFilters, excludeFilters );
	}
	
	public void scan( ClassLoader loader, String[] includeFilters, String[] excludeFilters )
	{
		Set< ClassInfo > allClasses = ExceptionWrapper.ClassPath_from( loader ).getAllClasses();
		Set< ClassInfo > classesAfterInclusion = allClasses.stream().filter( classInfo -> classInfoMatchesFilter( classInfo, includeFilters ) ).collect( Collectors.toSet() );
		Set< ClassInfo > classesAfterExclusion = classesAfterInclusion.stream().filter( classInfo -> !classInfoMatchesFilter( classInfo, excludeFilters ) ).collect( Collectors.toSet() );
		
		Pass1Scanner pass1Scanner = new Pass1Scanner( classesAfterExclusion, registeredClasses );
		Pass2Scanner pass2Scanner = new Pass2Scanner( registeredClasses );
		pass1Scanner.scan();
		pass2Scanner.scan();
	}
	
	private boolean classInfoMatchesFilter( ClassInfo classInfo, String[] classNameFilters )
	{
		return Arrays.asList( classNameFilters ).stream().filter(
			classNameFilter -> classInfo.getName().matches( classNameFilter ) ).findFirst().isPresent();
	}

	public Class getRegisteredClass( String name )
	{
		return registeredClasses.get( name );
	}
	
	public Set< Class > getRegisteredClasses()
	{
		return new HashSet< Class >( registeredClasses.values() );
	}
}
