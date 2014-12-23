package ch.liquidmind.inflection;

public class Test
{
	int i;
	float f;
	boolean z;
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits( f );
		result = prime * result + i;
		result = prime * result + ( z ? 1231 : 1237 );
		return result;
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		Test other = (Test)obj;
		if ( Float.floatToIntBits( f ) != Float.floatToIntBits( other.f ) )
			return false;
		if ( i != other.i )
			return false;
		if ( z != other.z )
			return false;
		return true;
	}
	
	
	
	
	
//	@Override
//	public int hashCode()
//	{
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ( ( i == null ) ? 0 : i.hashCode() );
//		return result;
//	}
//
//	@Override
//	public boolean equals( Object obj )
//	{
//		if ( this == obj )
//			return true;
//		if ( obj == null )
//			return false;
//		if ( getClass() != obj.getClass() )
//			return false;
//		Test other = (Test)obj;
//		if ( i == null )
//		{
//			if ( other.i != null )
//				return false;
//		}
//		else if ( !i.equals( other.i ) )
//			return false;
//		return true;
//	}
//
//	static
//	{
//		new Integer( 1 ).hashCode();
//	}

	
	
//	@Override
//	public int hashCode()
//	{
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ( ( f == null ) ? 0 : f.hashCode() );
//		return result;
//	}
//
//	@Override
//	public boolean equals( Object obj )
//	{
//		if ( this == obj )
//			return true;
//		if ( obj == null )
//			return false;
//		if ( getClass() != obj.getClass() )
//			return false;
//		Test other = (Test)obj;
//		if ( f == null )
//		{
//			if ( other.f != null )
//				return false;
//		}
//		else if ( !f.equals( other.f ) )
//			return false;
//		return true;
//	}

//	@Override
//	public int hashCode()
//	{
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + Float.floatToIntBits( f );
//		return result;
//	}
//
//	@Override
//	public boolean equals( Object obj )
//	{
//		if ( this == obj )
//			return true;
//		if ( obj == null )
//			return false;
//		if ( getClass() != obj.getClass() )
//			return false;
//		Test other = (Test)obj;
//		if ( Float.floatToIntBits( f ) != Float.floatToIntBits( other.f ) )
//			return false;
//		return true;
//	}

//	@Override
//	public int hashCode()
//	{
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + i;
//		return result;
//	}
//
//	@Override
//	public boolean equals( Object obj )
//	{
//		if ( this == obj )
//			return true;
//		if ( obj == null )
//			return false;
//		if ( getClass() != obj.getClass() )
//			return false;
//		Test other = (Test)obj;
//		if ( i != other.i )
//			return false;
//		return true;
//	}
	
	
}
