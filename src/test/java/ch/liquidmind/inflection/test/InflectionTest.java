package ch.liquidmind.inflection.test;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import __java.lang.__Class;
import __java.lang.__Thread;
import __java.lang.reflect.__Field;
import ch.liquidmind.inflection.Inflection;
import ch.liquidmind.inflection.loader.TaxonomyLoader;
import ch.liquidmind.inflection.model.external.Taxonomy;
import ch.liquidmind.inflection.print.InflectionPrinter;
import ch.liquidmind.inflection.proxy.memory.ManualMemoryManager;
import ch.liquidmind.inflection.proxy.memory.MemoryManager;
import ch.liquidmind.inflection.proxy.memory.MemoryManager.MemoryManagementStrategy;
import ch.liquidmind.inflection.test.model.Address;
import ch.liquidmind.inflection.test.model.Gender;
import ch.liquidmind.inflection.test.model.Person;
import ch.liquidmind.inflection.test.model.FullTaxonomy.ch.liquidmind.inflection.test.model.FullTaxonomy_Person;
import ch.liquidmind.inflection.test.model.UseCase1.ch.liquidmind.inflection.test.model.UseCase1_Person;
import ch.liquidmind.inflection.test.model.UseCase2.ch.liquidmind.inflection.test.model.UseCase2_Person;
import ch.liquidmind.inflection.test.model.UseCase3.ch.liquidmind.inflection.test.model.UseCase3_Person;
import ch.liquidmind.inflection.test.model.UseCase4.ch.liquidmind.inflection.test.model.UseCase4_Address;
import ch.liquidmind.inflection.test.model.UseCase5.ch.liquidmind.inflection.test.model.UseCase5_Person;

public class InflectionTest
{
	private static PrintStream printStream = System.out;
	
	@Ignore
	@Test
	public void testGarbageCollectingMemoryManagement()
	{
		gc();
		long maxUsedMemory = getUsedMemory() + 1_000_000;
		setMemoryManagementStrategy( MemoryManagementStrategy.GARBAGE_COLLECTING );
		boolean success = true;
		
		for ( int i = 0 ; i < 10_000 ; ++i )
		{
			Person person = createTestPerson();
			Inflection.cast( UseCase1_Person.class, person );
			
			if ( !usedMemoryLessThan( maxUsedMemory ) )
			{
				success = false;
				break;
			}
		}
		
		Assert.assertTrue( success );
	}

	@Ignore
	@Test
	public void testManualMemoryManagementWithoutClearing()
	{
		gc();
		long maxUsedMemory = getUsedMemory() + 1_000_000;
		setMemoryManagementStrategy( MemoryManagementStrategy.MANUAL );
		boolean success = false;
		
		for ( int i = 0 ; i < 10_000 ; ++i )
		{
			Person person = createTestPerson();
			Inflection.cast( UseCase1_Person.class, person );
			
			if ( !usedMemoryLessThan( maxUsedMemory ) )
			{
				success = true;
				break;
			}
		}
		
		Assert.assertTrue( success );
	}

	@Ignore
	@Test
	public void testManualMemoryManagementWithClearing()
	{
		gc();
		long maxUsedMemory = getUsedMemory() + 1_000_000;
		setMemoryManagementStrategy( MemoryManagementStrategy.MANUAL );
		boolean success = true;
		
		for ( int i = 0 ; i < 10_000 ; ++i )
		{
			Person person = createTestPerson();
			Inflection.cast( UseCase1_Person.class, person );
			
			if ( i % 100 == 0 )
			{
				ManualMemoryManager memManager = MemoryManager.getContextMemoryManager();
				memManager.clear();
			}
			
			if ( !usedMemoryLessThan( maxUsedMemory ) )
			{
				success = false;
				break;
			}
		}
		
		Assert.assertTrue( success );
	}
	
	private static void gc()
	{
		long usedMemoryDelta = 1;
		
		while ( usedMemoryDelta > 0 )
		{
			long usedMemoryBefore = getUsedMemory();
			System.gc();
			__Thread.sleep( Thread.currentThread(), 300 );
			long usedMemoryAfter = getUsedMemory();
			usedMemoryDelta = usedMemoryBefore - usedMemoryAfter;
		}
	}
	
	private static boolean usedMemoryLessThan( long size )
	{
		boolean usedMemoryLessThan = true;

		if ( getUsedMemory() > size )
		{
			gc();

			if ( getUsedMemory() > size )
				usedMemoryLessThan = false;
		}
		
		return usedMemoryLessThan;
	}
	
	private static long getUsedMemory()
	{
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	@SuppressWarnings( "unchecked" )
	private static void setMemoryManagementStrategy( MemoryManagementStrategy memoryManagementStrategy )
	{
		Field contextMemoryManagerField = __Class.getDeclaredField( MemoryManager.class, "contextMemoryManager" );
		contextMemoryManagerField.setAccessible( true );
		ThreadLocal< MemoryManager > contextMemoryManager = (ThreadLocal< MemoryManager >)__Field.get( contextMemoryManagerField, null );
		contextMemoryManager.set( null );
		MemoryManager.setMemoryManagementStrategy( memoryManagementStrategy );
	}
	
	@Test
	public void testPrinterMulti() throws Throwable
	{
		long before = System.currentTimeMillis();
		
		for ( int i = 0 ; i < 1 ; ++i )
			testPrinter();

		long after = System.currentTimeMillis();
		long delta = after - before;
		
		System.out.println( "testPrinterMulti(): " + delta + "ms" );
	}

	public void testPrinter()
	{
		String[] taxonomyNames = new String[] { "FullTaxonomy", "UseCase1", "UseCase2", "UseCase3", "UseCase4", "UseCase5" };
		
		for ( String taxonomyName : taxonomyNames )
		{
			Taxonomy taxonomy = TaxonomyLoader.getContextTaxonomyLoader().loadTaxonomy( "ch.liquidmind.inflection.test.model." + taxonomyName );
			InflectionPrinter printer = new InflectionPrinter( printStream, true, true );
			printer.printTaxonomy( taxonomy );
			printStream.println();
		}
	}
	
	@Ignore
	@Test
	public void testProxiesMulti() throws Throwable
	{
		long before = System.currentTimeMillis();
		
		for ( int i = 0 ; i < 1 ; ++i )
			testProxies();

		long after = System.currentTimeMillis();
		long delta = after - before;
		
		System.out.println( "testProxiesMulti(): " + delta + "ms" );
	}

	@Test
	public void testProxies() throws Throwable
	{
		Person person = createTestPerson();
		
		FullTaxonomy_Person fullTaxonomyPerson = Inflection.cast( FullTaxonomy_Person.class, person );
		UseCase1_Person useCase1Person = Inflection.cast( UseCase1_Person.class, person );
		UseCase2_Person useCase2Person = Inflection.cast( UseCase2_Person.class, person );
		UseCase3_Person useCase3Person = Inflection.cast( UseCase3_Person.class, person );
		UseCase4_Address useCase4Address = Inflection.cast( UseCase4_Address.class, person.getAddresses().get( 0 ) );
		UseCase5_Person useCase5Person = Inflection.cast( UseCase5_Person.class, person );
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		ObjectReader reader = mapper.reader().withType( FullTaxonomy_Person.class );
		
		reader.readValue( writer.writeValueAsString( fullTaxonomyPerson ) );
		
		printStream.println( "FullTaxonomy_Person:" );
		printStream.println( writer.writeValueAsString( fullTaxonomyPerson ) );
		printStream.println();
		printStream.println( "UseCase1_Person:" );
		printStream.println( writer.writeValueAsString( useCase1Person ) );
		printStream.println();
		printStream.println( "UseCase2_Person:" );
		printStream.println( writer.writeValueAsString( useCase2Person ) );
		printStream.println();
		printStream.println( "UseCase3_Person:" );
		printStream.println( writer.writeValueAsString( useCase3Person ) );
		printStream.println();
		printStream.println( "UseCase4_Address:" );
		printStream.println( writer.writeValueAsString( useCase4Address ) );
		printStream.println();
		printStream.println( "UseCase5_Person:" );
		printStream.println( writer.writeValueAsString( useCase5Person ) );
		printStream.println();
	}
	
	private static Person createTestPerson()
	{
		Calendar cal = new GregorianCalendar();
		cal.set( 1972, 8, 8 );
		Person person = new Person( 1, "John", "Brush", "Mr.", "+41 79 235 17 56", "+41 79 235 17 56", "jebrush@gmail.com", Gender.MALE, cal.getTime() );
		Address address = new Address( 2, "Feldgüetliweg 82", "Feldmeilen", "8706", "Switzerland" );
		person.getAddresses().add( address );
		address.getPeople().add( person );
		person.setProfileImage( new byte[] { 0x4b, 0x69, 0x6c, 0x72, 0x6f, 0x79, 0x20, 0x77, 0x61, 0x73, 0x20, 0x68, 0x65, 0x72, 0x65 } );

		return person;
	}
}
