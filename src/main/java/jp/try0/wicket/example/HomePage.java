package jp.try0.wicket.example;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import jp.try0.wicket.example.panel.ListComponentExamplePanel;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public HomePage(final PageParameters parameters) {
		super(parameters);
	}


	@Override
	protected void onInitialize() {
		super.onInitialize();


		add(new ListComponentExamplePanel("listComponentExample"));
	}
}
