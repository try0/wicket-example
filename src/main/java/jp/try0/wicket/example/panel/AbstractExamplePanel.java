package jp.try0.wicket.example.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractExamplePanel extends Panel {

	public AbstractExamplePanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new Label("exampleTitle", getClass().getSimpleName().replace("Panel", "")));
	}

}
