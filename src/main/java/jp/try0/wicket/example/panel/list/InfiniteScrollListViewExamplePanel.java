package jp.try0.wicket.example.panel.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import jp.try0.wicket.example.markup.html.list.InfiniteScrollListView;
import jp.try0.wicket.example.markup.html.list.PartialUpdatableListView;
import jp.try0.wicket.example.panel.AbstractExamplePanel;

/**
 * {@link InfiniteScrollListView} Example.
 *
 * @author Ryo Tsunoda
 *
 */
public class InfiniteScrollListViewExamplePanel extends AbstractExamplePanel {

	public static class Chat implements Serializable {

		public int id;

		public String message;
	}

	public InfiniteScrollListViewExamplePanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		addRelatedComponents(InfiniteScrollListView.class, PartialUpdatableListView.class);

		final WebMarkupContainer isArea;
		add(isArea = new WebMarkupContainer("isArea") {

			{
				setOutputMarkupId(true);
			}
		});
		queue(new WebMarkupContainer("isContainer") {
			{
				setOutputMarkupId(true);

				IDataProvider<Chat> itemProvider = new IDataProvider<Chat>() {

					@Override
					public Iterator<? extends Chat> iterator(long first, long count) {

						try {
							Thread.sleep(1000);
						} catch (Exception ignore) {

						}
						Iterator<Chat> iterator = IntStream.range((int) first, (int) (first + count))
								.boxed()
								.map(i -> {
									Chat chat = new Chat();
									chat.id = i;
									chat.message = String.valueOf(i);
									return chat;
								})
								.collect(Collectors.toList())
								.iterator();
						return iterator;
					}

					@Override
					public long size() {
						// TODO
						throw new UnsupportedOperationException();
					}

					@Override
					public IModel<Chat> model(Chat object) {
						return Model.of(object);
					}
				};

				IModel<List<Chat>> itemModel = Model.ofList(new ArrayList<>());

				add(new InfiniteScrollListView<Chat>("chatListView", itemModel, itemProvider) {
					{
						setLoadItemCount(20);
						setOnLoadScript("$('#" + isArea.getMarkupId() + "').addClass('loading');");

						for (Iterator<? extends Chat> it = itemProvider.iterator(0, getLoadItemCount()); it
								.hasNext();) {
							Chat item = it.next();
							itemModel.getObject().add(item);
						}
					}

					@Override
					protected void populateItem(ListItem<Chat> item) {

						item.add(new Label("chatMessage", item.getModelObject().message));
					}

					@Override
					protected void onLoadItem(AjaxRequestTarget target) {
						target.appendJavaScript("$('#" + isArea.getMarkupId() + "').removeClass('loading');");
					}
				});
			}
		});

	}



}
