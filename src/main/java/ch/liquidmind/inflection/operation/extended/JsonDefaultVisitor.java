package ch.liquidmind.inflection.operation.extended;

import ch.liquidmind.inflection.IdentifiableObject;
import ch.liquidmind.inflection.model.MemberView;
import ch.liquidmind.inflection.model.Multiplicity;
import ch.liquidmind.inflection.operation.ClassViewFrame;
import ch.liquidmind.inflection.operation.ClassViewPair;
import ch.liquidmind.inflection.operation.DimensionViewFrame;
import ch.liquidmind.inflection.operation.InflectionViewFrame;
import ch.liquidmind.inflection.operation.MemberViewFrame;

public class JsonDefaultVisitor extends JsonAbstractVisitor
{
	public static final String VERSION = "JSON for Inflection V1.0";
	
	@Override
	public void visit( ClassViewFrame frame )
	{
		ClassViewPair pair = frame.getClassViewPair();
		IdentifiableObject< ?, ? > iObject = pair.getLeftObject();
		Object objectId = iObject.getObjectId();
		MemberViewFrame lastMemberViewFrame = getTraverser().getLastMemberViewFrame();
		MemberView lastMemberView = ( lastMemberViewFrame == null ? null : lastMemberViewFrame.getMemberViewPair().getLeftMemberView() );
		String memberViewName = getMemberViewName( lastMemberView );
		memberViewName = ( memberViewName.isEmpty() ? "" : memberViewName + " " );

		if ( frame.getVisitCount() == 0 )
		{
			boolean isReferencable = getTraverser().getMultiplyTraversedObjects().contains( iObject );
			
			if ( !isReferencable && !memberViewName.isEmpty() )
				println( "\"" + memberViewName + "\" :" );
			
			openCurlyBraces();
			
			if ( isReferencable )
				println( memberViewName + "\"@id\" : " + objectId + "," );
			
			if ( lastMemberView == null )
			{
				println( "\"@version\" : \"" + VERSION + "\"," );
				println( "\"@hgroup\" : \"" + getTraverser().getHGroup().getName() + "\"," );
			}
			
			print( "\"@class\" : \"" + pair.getLeftObject().getObject().getClass().getName() + "\"" );
			printCommaIfNecessaryAfterMetaData();
			getTraverser().continueTraversal();
			closeCurlyBraces();
		}
		else
		{
			openCurlyBraces();
			println( memberViewName + "\"@ref\" : " + objectId );
			closeCurlyBraces();
		}

		printCommaIfNecessary();
	}

	@Override
	public void visit( MemberViewFrame frame )
	{
		MemberView memberView = frame.getMemberViewPair().getLeftMemberView();

		if ( memberView.getDimensionViews().get( 0 ).getMultiplicity().equals( Multiplicity.Many ) )
			println( "\"" + memberView.getName() + "\" :" );

		getTraverser().continueTraversal();
		printCommaIfNecessary();
	}

	@Override
	public void visit( DimensionViewFrame frame )
	{
		if ( frame.getDimensionViewPair().getLeftDimensionView().getMultiplicity().equals( Multiplicity.Many ) )
		{
			openBrackets();
			getTraverser().continueTraversal();
			closeBrackets();
			printCommaIfNecessary();
		}
		else
		{
			getTraverser().continueTraversal();
		}
	}
	
	protected void openCurlyBraces()
	{
		println( "{" );
		increaseIndent();
	}
	
	protected void closeCurlyBraces()
	{
		decreaseIndent();
		print( "}" );
	}
	
	protected void openBrackets()
	{
		println( "[" );
		increaseIndent();
	}
	
	protected void closeBrackets()
	{
		decreaseIndent();
		print( "]" );
	}
	
	private void printCommaIfNecessary()
	{
		InflectionViewFrame frame = getTraverser().getCurrentFrame();
		
		if ( frame.getPositionCurrent() < frame.getPositionMax() - 1 )
			println( "," );
		else
			println();
	}
	
	private void printCommaIfNecessaryAfterMetaData()
	{
		InflectionViewFrame frame = getTraverser().getCurrentFrame();
		
		if ( frame instanceof ClassViewFrame )
		{
			ClassViewFrame classViewFrame = (ClassViewFrame)frame;

			if ( classViewFrame.getClassViewPair().getLeftClassView().getMemberViews().size() > 0 )
				println( "," );
			else
				println();
		}
	}
}
