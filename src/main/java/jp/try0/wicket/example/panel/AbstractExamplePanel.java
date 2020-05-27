package jp.try0.wicket.example.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class AbstractExamplePanel extends Panel {

	private IModel<List<Class<? extends Component>>> relatedComponentModel = Model.ofList(new ArrayList<>());

	public AbstractExamplePanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new Label("exampleTitle", getClass().getSimpleName().replace("Panel", "")));

		add(new ExternalLink("linkGitHub", getGitHubUrl(getClass())));

		add(new WebMarkupContainer("areaRelatedComponent") {
			{
				add(new ListView<Class<? extends Component>>("listRelatedComponent", relatedComponentModel) {

					@Override
					protected void populateItem(ListItem<Class<? extends Component>> item) {

						item.add(new ExternalLink("linkRelatedComponent", getGitHubUrl(item.getModelObject())) {

							{
								add(new Label("lblRelatedComponent", item.getModelObject().getSimpleName()));
							}
						});
					}

				});
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();

				setVisibilityAllowed(!relatedComponentModel.getObject().isEmpty());
			}
		});
	}

	/**
	 * Exampleと関連するコンポーネントクラスを追加します。
	 *
	 * @param components
	 */
	@SafeVarargs
	protected final void addRelatedComponents(Class<? extends Component>... components) {
		for (Class<? extends Component> component : components) {
			relatedComponentModel.getObject().add(component);
		}
	}

	/**
	 * 対象クラスのGitHubリンクを取得します。
	 *
	 * @param clazz
	 * @return
	 */
	public static String getGitHubUrl(Class<?> clazz) {
		String base = "https://github.com/try0/wicket-example/tree/master/src/main/java/";
		return base + clazz.getName().replaceAll(Pattern.quote("."), "/") + ".java";
	}

}
