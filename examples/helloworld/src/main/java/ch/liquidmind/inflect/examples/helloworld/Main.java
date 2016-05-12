package ch.liquidmind.inflect.examples.helloworld;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;

import ch.liquidmind.inflect.examples.helloworld.model.Person;
import ch.liquidmind.inflect.examples.helloworld.model.MyTaxonomy.ch.liquidmind.inflect.examples.helloworld.model.MyTaxonomy_Person;
import ch.liquidmind.inflection.Inflection;

public class Main
{
	public static void main( String[] args ) throws JsonGenerationException, JsonMappingException, IOException
	{
		Person person = new Person( "Jon", "Doe" );
		MyTaxonomy_Person mtPerson = Inflection.cast( MyTaxonomy_Person.class, person );
		
		demoAccess( person, mtPerson );
		demoJson( person, mtPerson );
	}
	
	private static void demoAccess( Person person, MyTaxonomy_Person mtPerson )
	{
		System.out.println();
		System.out.println( "Demonstrate setting firstName through person or mtPerson." );
		
		System.out.println( "    Initial State:" );
		System.out.println( "        person.getFirstName(): " + person.getFirstName() );		// firstName == Jon
		System.out.println( "        mtPerson.getFirstName(): " + mtPerson.getFirstName() );	// firstName == Jon

		person.setFirstName( "Jane" );
		System.out.println( "    After invoking person.setFirstName( \"Jane\" ):" );
		System.out.println( "        person.getFirstName(): " + person.getFirstName() );		// firstName == Jane
		System.out.println( "        mtPerson.getFirstName(): " + mtPerson.getFirstName() );	// firstName == Jane
		
		mtPerson.setFirstName( "Jeff" );
		System.out.println( "    After invoking mtPerson.setFirstName( \"Jeff\" ):" );
		System.out.println( "        person.getFirstName(): " + person.getFirstName() );		// firstName == Jeff
		System.out.println( "        mtPerson.getFirstName(): " + mtPerson.getFirstName() );	// firstName == Jeff
	}
	
	private static void demoJson( Person person, MyTaxonomy_Person mtPerson ) throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure( SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY, true );
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		
		System.out.println();
		System.out.println( "Demonstrate serializing to JSON" );

		System.out.println( "    " + Person.class.getSimpleName() );
		System.out.println( indent( writer.writeValueAsString( person ) ) );
		System.out.println( "    " + MyTaxonomy_Person.class.getSimpleName() );
		System.out.println( indent( writer.writeValueAsString( mtPerson ) ) );
		System.out.println();
	}
	
	private static String indent( String text )
	{
		return "    " + text.replaceAll( System.lineSeparator(), System.lineSeparator() + "    " );
	}
}
