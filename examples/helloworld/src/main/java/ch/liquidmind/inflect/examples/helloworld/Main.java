package ch.liquidmind.inflect.examples.helloworld;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import ch.liquidmind.inflect.examples.helloworld.model.Person;
import ch.liquidmind.inflect.examples.helloworld.model.AllProperties.ch.liquidmind.inflect.examples.helloworld.model.AllProperties_Person;
import ch.liquidmind.inflect.examples.helloworld.model.SingleProperty.ch.liquidmind.inflect.examples.helloworld.model.SingleProperty_Person;
import ch.liquidmind.inflection.Inflection;

public class Main
{
	public static void main( String[] args ) throws JsonGenerationException, JsonMappingException, IOException
	{
		Person person = new Person( "Jon", "Doe" );
		SingleProperty_Person spPerson = Inflection.cast( SingleProperty_Person.class, person );
		AllProperties_Person apPerson = Inflection.cast( AllProperties_Person.class, person );
		
		demoAccess( person, spPerson );
		demoJson( person, spPerson, apPerson );
	}
	
	private static void demoAccess( Person person, SingleProperty_Person spPerson )
	{
		System.out.println();
		System.out.println( "INITIAL STATE" );
		System.out.println( "person.getFirstName(): " + person.getFirstName() );		// firstName == Jon
		System.out.println( "spPerson.getFirstName(): " + spPerson.getFirstName() );	// firstName == Jon
		
		System.out.println();
		person.setFirstName( "Jane" );
		System.out.println( "AFTER WRITING TO VIEWABLE" );
		System.out.println( "person.getFirstName(): " + person.getFirstName() );		// firstName == Jane
		System.out.println( "spPerson.getFirstName(): " + spPerson.getFirstName() );	// firstName == Jane
		
		System.out.println();
		spPerson.setFirstName( "Jeff" );
		System.out.println( "AFTER WRITING TO VIEW" );
		System.out.println( "person.getFirstName(): " + person.getFirstName() );		// firstName == Jeff
		System.out.println( "spPerson.getFirstName(): " + spPerson.getFirstName() );	// firstName == Jeff
		System.out.println();
	}
	
	private static void demoJson( Person person, SingleProperty_Person spPerson, AllProperties_Person apPerson ) throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		
		System.out.println( Person.class.getSimpleName() );
		System.out.println( writer.writeValueAsString( person ) );
		System.out.println();
		System.out.println( SingleProperty_Person.class.getSimpleName() );
		System.out.println( writer.writeValueAsString( spPerson ) );
		System.out.println();
		System.out.println( AllProperties_Person.class.getSimpleName() );
		System.out.println( writer.writeValueAsString( apPerson ) );
		System.out.println();
	}
}
