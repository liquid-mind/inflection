package ch.zhaw.inflection.demo2;

public class Account
{
	private String number;
	private String IBAN;
	private String SWIFTCode;
	private AccountType type;
	private String clearingNumber;
	
	public Account( String number, String iBAN, String sWIFTCode, AccountType type, String clearingNumber )
	{
		super();
		this.number = number;
		IBAN = iBAN;
		SWIFTCode = sWIFTCode;
		this.type = type;
		this.clearingNumber = clearingNumber;
	}

	public String getNumber()
	{
		return number;
	}

	public void setNumber( String number )
	{
		this.number = number;
	}

	public String getIBAN()
	{
		return IBAN;
	}

	public void setIBAN( String iBAN )
	{
		IBAN = iBAN;
	}

	public String getSWIFTCode()
	{
		return SWIFTCode;
	}

	public void setSWIFTCode( String sWIFTCode )
	{
		SWIFTCode = sWIFTCode;
	}

	public AccountType getType()
	{
		return type;
	}

	public void setType( AccountType type )
	{
		this.type = type;
	}

	public String getClearingNumber()
	{
		return clearingNumber;
	}

	public void setClearingNumber( String clearingNumber )
	{
		this.clearingNumber = clearingNumber;
	}
}
