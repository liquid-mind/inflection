package ch.liquidmind.inflection.proxy;

import ch.liquidmind.inflection.model.external.View;

public class Proxy
{
	private View view;

	protected Proxy( View view )
	{
		super();
		this.view = view;
	}

	public View getView()
	{
		return view;
	}
}
