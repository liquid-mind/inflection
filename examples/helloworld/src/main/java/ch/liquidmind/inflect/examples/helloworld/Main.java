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
