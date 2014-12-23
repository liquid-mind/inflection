package ch.liquidmind.inflection.model;

public interface InflectionResource
{
	public static final String CARRIAGE_RETURN = System.lineSeparator();
	public static final String TAB = "    ";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	public static final String SEMICOLON = ";";

	public String getName();
	public void setName( String name );
}
