package jp.try0.wicket.example.panel;

import jp.try0.wicket.example.panel.list.InfiniteScrollListViewExamplePanel;
import jp.try0.wicket.example.panel.list.PartialUpdatableListViewExamplePanel;

public class ListComponentExamplePanel extends AbstractExamplePanel {

	public ListComponentExamplePanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new PartialUpdatableListViewExamplePanel("PartialUpdatableListViewExamplePanel"));
		add(new InfiniteScrollListViewExamplePanel("InfiniteScrollListViewExamplePanel"));
	}

}
