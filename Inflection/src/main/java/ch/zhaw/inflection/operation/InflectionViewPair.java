package ch.zhaw.inflection.operation;

import ch.zhaw.inflection.model.InflectionView;

public abstract class InflectionViewPair
{
	private InflectionView leftInflectionView;
	private InflectionView rightInflectionView;
	private Integer leftPositionMax;
	private Integer rightPositionMax;
	private Integer leftPositionCurrent;
	private Integer rightPositionCurrent;
	
	public InflectionViewPair(
			InflectionView leftInflectionView, InflectionView rightInflectionView,
			Integer leftPositionMax, Integer rightPositionMax,
			Integer leftPositionCurrent, Integer rightPositionCurrent )
	{
		super();
		this.leftInflectionView = leftInflectionView;
		this.rightInflectionView = rightInflectionView;
		this.leftPositionMax = leftPositionMax;
		this.rightPositionMax = rightPositionMax;
		this.leftPositionCurrent = leftPositionCurrent;
		this.rightPositionCurrent = rightPositionCurrent;
	}

	public InflectionView getLeftInflectionView()
	{
		return leftInflectionView;
	}

	public void setLeftInflectionView( InflectionView leftInflectionView )
	{
		this.leftInflectionView = leftInflectionView;
	}

	public InflectionView getRightInflectionView()
	{
		return rightInflectionView;
	}

	public void setRightInflectionView( InflectionView rightInflectionView )
	{
		this.rightInflectionView = rightInflectionView;
	}

	public InflectionView getReferenceInflectionView()
	{
		return leftInflectionView == null ? rightInflectionView : leftInflectionView;
	}
	
	public Integer getLeftPositionMax()
	{
		return leftPositionMax;
	}

	public void setLeftPositionMax( Integer leftPositionMax )
	{
		this.leftPositionMax = leftPositionMax;
	}

	public Integer getLeftPositionCurrent()
	{
		return leftPositionCurrent;
	}

	public void setLeftPositionCurrent( Integer leftPositionCurrent )
	{
		this.leftPositionCurrent = leftPositionCurrent;
	}

	public Integer getRightPositionMax()
	{
		return rightPositionMax;
	}

	public void setRightPositionMax( Integer rightPositionMax )
	{
		this.rightPositionMax = rightPositionMax;
	}

	public Integer getRightPositionCurrent()
	{
		return rightPositionCurrent;
	}

	public void setRightPositionCurrent( Integer rightPositionCurrent )
	{
		this.rightPositionCurrent = rightPositionCurrent;
	}
}
