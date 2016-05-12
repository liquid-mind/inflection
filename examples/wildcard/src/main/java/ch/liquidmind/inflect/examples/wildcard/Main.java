package ch.liquidmind.inflect.examples.wildcard;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;

import ch.liquidmind.inflect.examples.wildcard.model.Address;
import ch.liquidmind.inflect.examples.wildcard.model.Gender;
import ch.liquidmind.inflect.examples.wildcard.model.Person;
import ch.liquidmind.inflect.examples.wildcard.model.Everything.ch.liquidmind.inflect.examples.wildcard.model.Everything_Person;
import ch.liquidmind.inflect.examples.wildcard.model.PersonOnly.ch.liquidmind.inflect.examples.wildcard.model.PersonOnly_Person;
import ch.liquidmind.inflection.Inflection;

public class Main
{
	public static void main( String[] args ) throws JsonGenerationException, JsonMappingException, IOException
	{
		Calendar cal = new GregorianCalendar();
		cal.set( 1972, 8, 8 );
		Address address = new Address( 1, "Main Street 1", "Smalleville", "12345", "USA" );
		Person person = new Person( 2, "Jon", "Doe", "Mr.", "+1 123 456 7890", null, "jondoe@gmail.com", Gender.MALE, cal.getTime() );
		person.getAddresses().add( address );
		
		Everything_Person ePerson = Inflection.cast( Everything_Person.class, person );
		PersonOnly_Person poPerson = Inflection.cast( PersonOnly_Person.class, person );

		System.out.println();
		
		demoJson( person );
		demoJson( ePerson );
		demoJson( poPerson );
	}
	
	private static void demoJson( Object object ) throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure( SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, true );
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		
		System.out.println( "    " + object.getClass().getSimpleName() );
		System.out.println( indent( writer.writeValueAsString( object ) ) );
		System.out.println();
	}
	
	private static String indent( String text )
	{
		return "    " + text.replaceAll( System.lineSeparator(), System.lineSeparator() + "    " );
	}
}
