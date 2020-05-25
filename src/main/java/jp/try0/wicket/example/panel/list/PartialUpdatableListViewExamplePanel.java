package jp.try0.wicket.example.panel.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.Model;

import jp.try0.wicket.example.markup.html.list.PartialUpdatableListView;
import jp.try0.wicket.example.panel.AbstractExamplePanel;

/**
 * {@link PartialUpdatableListView} Example.
 *
 * @author Ryo Tsunoda
 *
 */
public class PartialUpdatableListViewExamplePanel extends AbstractExamplePanel {


	public static class Chat implements Serializable {

		public int id;

		public String message;
	}

	public PartialUpdatableListViewExamplePanel(String id) {
		super(id);
	}


	@Override
	protected void onInitialize() {
		super.onInitialize();
		List<String> listItem = new ArrayList<>();
		listItem.add("1");
		listItem.add("2");
		listItem.add("3");

		WebMarkupContainer listContainer;
		add(listContainer = new WebMarkupContainer("lvContainer") {

			{
				setOutputMarkupId(true);
			}
		});

		PartialUpdatableListView<String> listView;
		listContainer.add(listView = new PartialUpdatableListView<String>("list", Model.ofList(listItem)) {
			{
				setOutputMarkupId(true);
			}

			@Override
			protected void populateItem(ListItem<String> item) {

				item.add(new Label("itemLabel", item.getModel()));

			}

		});

		add(new AjaxLink<Void>("btnAddItem") {

			@Override
			public void onClick(AjaxRequestTarget target) {
				List<String> items = new ArrayList<>();

				for (int i = listItem.size() + 1; i <= listItem.size() + 3; i++) {
					items.add(String.valueOf(i));
				}

				listView.addItems(target, items);
			}

		});

	}

}
