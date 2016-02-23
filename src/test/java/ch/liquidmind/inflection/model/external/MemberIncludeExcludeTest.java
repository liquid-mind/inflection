package ch.liquidmind.inflection.model.external;

import static ch.liquidmind.inflection.test.mock.AbstractFileMock.UNNAMED_PACKAGE;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.liquidmind.inflection.test.AbstractInflectionTest;
import ch.liquidmind.inflection.test.TestUtility;
import ch.liquidmind.inflection.test.mock.JavaFileMock;

@RunWith( Parameterized.class )
public class MemberIncludeExcludeTest extends AbstractInflectionTest
{

	@Parameters( name = "{index}: View definition: {0}, epected member visibility: {1}" )
	public static Collection< Object[] > data()
	{
		return Arrays.asList( new Object[][] { 
			
			{ "include member1;", new boolean[] { true }}, 
			{ "exclude member1;", new boolean[] { false }},
			{ "include member1;", new boolean[] { true, false }},
			{ "exclude member1;", new boolean[] { false, false }},
			{ "member1;", new boolean[] { true }}, 
			{ "include *;", new boolean[] { true }},
			{ "include *;", new boolean[] { true, true }},
			{ "*;", new boolean[] { true }},
			{ "include *; exclude member2;", new boolean[] { true, false }},
			{ "exclude member2; include *;", new boolean[] { true, true }}, //  <inflection-error/> not sure if this test is correct
			{ "include *; include member2;", new boolean[] { true, true }},
			{ "include member2; include *;", new boolean[] { true, true }},
			{ "include member*;", new boolean[] { true, true }},
			{ "exclude member*;", new boolean[] { false, false }},
			{ "include member1*;", new boolean[] { true }},
			{ "exclude member1*;", new boolean[] { false }}
			
		} );
	}

	private final String viewDefinition;
	private final boolean[] visibleMembers;

	public MemberIncludeExcludeTest( String viewDefinition, boolean[] visibleMembers )
	{
		this.viewDefinition = viewDefinition;
		this.visibleMembers = visibleMembers;
	}

	private JavaFileMock[] createSimpleJavaModel( int memberCount )
	{
		StringBuilder javaClass = new StringBuilder();
		javaClass.append( "public class V {" );
		for ( int k = 1; k <= memberCount; k++ )
		{
			javaClass.append( TestUtility.generateMember( "int", "member" + k ) );
		}
		javaClass.append( "}" );
		
		return new JavaFileMock[] { createJavaFileMock( "V.java", UNNAMED_PACKAGE, javaClass.toString() ) };
	}

	@Test
	public void testIncludeExclude() throws Exception
	{
		StringBuilder builder = new StringBuilder();
		builder.append( "package a;" );
		builder.append( "taxonomy A {" );
		builder.append( "	view V { " + viewDefinition + " }" );
		builder.append( "}" );

		doTest( job -> {
			Taxonomy taxonomy = TestUtility.getTaxonomyLoader( job ).loadTaxonomy( "a.A" );
			View view = taxonomy.getView( "V" );
			for ( int k = 1; k <= visibleMembers.length; k++ )
			{
				boolean expectedVisbility = taxonomy.getMember( view, "member" + k ) != null == visibleMembers[ k-1 ];
				assertTrue( "Member member" + k + " is visible: " + expectedVisbility, expectedVisbility );
			}
		} , createSimpleJavaModel( visibleMembers.length ), null, createInflectionFileMock( "a", builder.toString() ) );
	}

}
