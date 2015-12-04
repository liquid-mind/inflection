package ch.liquidmind.inflection.model.external;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.liquidmind.inflection.compiler.util.InflectionCompilerTestUtility;
import ch.liquidmind.inflection.model.SelectionType;
import ch.liquidmind.inflection.model.external.util.TaxonomyTestUtility;
import ch.liquidmind.inflection.test.model.A;

public class MemberTest
{

	private static File compiledTaxonomyDir;

	@BeforeClass
	public static void beforeClass() throws Exception
	{

		StringBuilder builder = new StringBuilder();
		builder.append( "package ch.liquidmind.inflection.model.external;" );
		builder.append( "import ch.liquidmind.inflection.test.model.*;" );
		builder.append( "taxonomy MemberTestTaxonomy {}" );
		builder.append( "taxonomy MemberTest_GetSelectionTypeTaxonomy extends MemberTestTaxonomy" );
		builder.append( "{" );
		builder.append( "	view A { *; }" );
		builder.append( "}" );

		compiledTaxonomyDir = InflectionCompilerTestUtility.compileInflection( "ch.liquidmind.inflection.model.external", builder.toString() );
	}

	@Test
	public void testGetSelectionType_Include_CorrectType() throws Exception
	{
		Taxonomy taxonomy = TaxonomyTestUtility.getTestTaxonomy( compiledTaxonomyDir, this.getClass().getPackage().getName(), "MemberTest_GetSelectionTypeTaxonomy" );
		View view = taxonomy.getView( A.class.getCanonicalName() );
		Member member = view.getDeclaredMember( "id" );
		assertEquals( SelectionType.INCLUDE, member.getSelectionType() );
	}

}
