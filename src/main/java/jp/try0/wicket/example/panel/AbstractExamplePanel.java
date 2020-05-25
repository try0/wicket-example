package jp.try0.wicket.example.panel;

import java.util.regex.Pattern;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class AbstractExamplePanel extends Panel {

	public AbstractExamplePanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new Label("exampleTitle", getClass().getSimpleName().replace("Panel", "")));

		add(new ExternalLink("linkGitHub", getGitHubUrl(getClass())));
	}

	public static String getGitHubUrl(Class<?> clazz) {
		String base = "https://github.com/try0/wicket-example/tree/master/src/main/java/";
		return base + clazz.getName().replaceAll(Pattern.quote("."), "/") + ".java";
	}

}
