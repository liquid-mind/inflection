package ch.zhaw.inflection.operation;



public interface InflectionTraverser
{
	public void traverse( IdentifiableObjectPair identifiableObjectPair );
	public void continueTraversal();
}
