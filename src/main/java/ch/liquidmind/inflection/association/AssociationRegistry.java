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
	public static final String INCLUDE_FILTERS = AssociationRegistry.class.getName() + ".includeFilters";
	public static final String EXCLUDE_FILTERS = AssociationRegistry.class.getName() + ".excludeFilters";
	
	private static AssociationRegistry instance;
	// TODO: make the key java.lang.Class instead of String.
	private Map< String, Class > registeredClasses = new HashMap< String, Class >();
	private boolean performedScan = false;
	
	public static AssociationRegistry instance()
	{
		if ( instance == null )
		{
			instance = new AssociationRegistry();
			
			String includeFilters = System.getProperty( INCLUDE_FILTERS );
			String[] includeFiltersSplit = ( includeFilters == null ? new String[] {} : includeFilters.split( "," ) );
			String[] includeFiltersTrimed = Arrays.asList( includeFiltersSplit ).stream().map( includeFilter -> includeFilter.trim() ).collect( Collectors.toList() ).toArray( new String[ includeFiltersSplit.length ] );
			
			String excludeFilters = System.getProperty( EXCLUDE_FILTERS );
			String[] excludeFiltersSplit = ( excludeFilters == null ? new String[] {} : excludeFilters.split( "," ) );
			String[] excludeFiltersTrimed = Arrays.asList( excludeFiltersSplit ).stream().map( excludeFilter -> excludeFilter.trim() ).collect( Collectors.toList() ).toArray( new String[ excludeFiltersSplit.length ] );
			
			instance.scan( includeFiltersTrimed, excludeFiltersTrimed );
		}
		
		return instance;
	}
	
	public void scan( String[] includeFilters, String[] excludeFilters )
	{
		scan( Thread.currentThread().getContextClassLoader(), includeFilters, excludeFilters );
	}
	
	public void scan( ClassLoader loader, String[] includeFilters, String[] excludeFilters )
	{
		if ( performedScan )
			throw new RuntimeException( String.format( "Scan for this %s was already performed.", AssociationRegistry.class.getSimpleName() ) );
		
		Set< String > adjustedIncludeFilters = Arrays.asList( includeFilters ).stream().map( includeFilter -> includeFilter.replace( "*", ".*" ) ).collect( Collectors.toSet() );
		Set< String > adjustedExcludeFilters = Arrays.asList( excludeFilters ).stream().map( excludeFilter -> excludeFilter.replace( "*", ".*" ) ).collect( Collectors.toSet() );
		
		Set< ClassInfo > allClasses = ExceptionWrapper.ClassPath_from( loader ).getAllClasses();
		Set< ClassInfo > classesAfterInclusion = allClasses.stream().filter( classInfo -> classInfoMatchesFilter( classInfo, adjustedIncludeFilters ) ).collect( Collectors.toSet() );
		Set< ClassInfo > classesAfterExclusion = classesAfterInclusion.stream().filter( classInfo -> !classInfoMatchesFilter( classInfo, adjustedExcludeFilters ) ).collect( Collectors.toSet() );
		
		Pass1Scanner pass1Scanner = new Pass1Scanner( classesAfterExclusion, registeredClasses );
		Pass2Scanner pass2Scanner = new Pass2Scanner( registeredClasses );
		Pass3Scanner pass3Scanner = new Pass3Scanner( registeredClasses );
		pass1Scanner.scan();
		pass2Scanner.scan();
		pass3Scanner.scan();
		
		performedScan = true;
	}
	
	private boolean classInfoMatchesFilter( ClassInfo classInfo, Set< String > classNameFilters )
	{
		return classNameFilters.stream().filter(
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
