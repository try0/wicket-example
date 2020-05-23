package jp.try0.wicket.example.markup.html.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;

// TODO モデルの差分をリストアップして、更新できる処理実装

/**
 * リストアイテムの部分更新可能なListView<br>
 *
 * @see https://wicketinaction.com/2008/10/repainting-only-newly-created-repeater-items-via-ajax/
 *
 * @author Ryo Tsunoda
 *
 * @param <T>
 */
public abstract class PartialUpdatableListView<T> extends ListView<T> {
	private static final long serialVersionUID = 1L;

	/**
	 * 更新対象用のダミータグ名
	 */
	private String itemTagName = null;



	/**
	 * コンストラクター
	 *
	 * @param id
	 */
	public PartialUpdatableListView(String id) {
		super(id);
	}

	/**
	 * コンストラクター
	 *
	 * @param id
	 * @param model
	 */
	public PartialUpdatableListView(String id, IModel<List<T>> model) {
		super(id, model);
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onInitialize() {
		super.onInitialize();

		if (itemTagName == null) {
			// リピーターアイテムのタグ名を保持します。

			var markupStream = findMarkupStream();
			var tag = markupStream.getTag();
			var tagName = tag.getName();
			itemTagName = tagName;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListItem<T> newItem(int index, org.apache.wicket.model.IModel<T> itemModel) {
		ListItem<T> item = super.newItem(index, itemModel);
		item.setOutputMarkupId(true);
		return item;
	}

	/**
	 * リストアイテムを取得しkます。
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ListItem<T>> getListItems() {
		List<ListItem<T>> items = new ArrayList<>();
		visitChildren(ListItem.class, (item, visit) -> {
			items.add((ListItem<T>) item);

			visit.dontGoDeeper();
		});
		return items;
	}

	/**
	 * リストアイテムを追加します。
	 *
	 * @param index
	 * @param model
	 * @return
	 */
	private ListItem<T> createAndAppendItem(int index, IModel<T> model) {
		// Add list item
		ListItem<T> item = newItem(index, model);
		item.setOutputMarkupId(true);

		add(item);

		return item;
	}

	/**
	 * populate系イベントをコールします。
	 *
	 * @param item
	 */
	private void raizePopulation(ListItem<T> item) {

		// Populate the list item
		onBeginPopulateItem(item);
		populateItem(item);

	}

	public void partialUpdate() {
		// TODO モデルの差分をリストアップして、部分描画実行
	}

	/**
	 * 末尾に要素を追加します。
	 *
	 * @param target
	 * @param obj
	 */
	public void addItem(AjaxRequestTarget target, T obj) {
		addItems(target, Arrays.asList(obj));
	}

	public void addItems(AjaxRequestTarget target, List<T> objs) {
		addItems(target, objs.iterator());
	}

	/**
	 * 末尾に要素を追加します。
	 *
	 * @param target
	 * @param objs
	 */
	public void addItems(AjaxRequestTarget target, Iterator<? extends T> iterator) {

		// add new model object
		List<T> modelObject = getModelObject();

		List<ListItem<?>> appendItems = new ArrayList<>();

		for (Iterator<? extends T> iter = iterator; iterator.hasNext();) {
			T obj = iter.next();
			modelObject.add(obj);

			// Add list item
			ListItem<T> item = createAndAppendItem(modelObject.size() - 1, () -> obj);

			raizePopulation(item);
			appendItems.add(item);

			// update
			target.add(item);
		}

		var scriptAppendReplaceMarker = getAppendPlaceHolderElementScript(getParent(), appendItems);
		target.prependJavaScript(scriptAppendReplaceMarker);
	}

	/**
	 * 指定したリストアイテムの次に要素を追加します。
	 *
	 * @param target
	 * @param markItem
	 * @param obj
	 */
	public void addItemAfter(AjaxRequestTarget target, ListItem<T> markItem, T obj) {
		addItemsAfter(target, markItem, Arrays.asList(obj));
	}

	/**
	 * 指定したリストアイテムの次に要素を追加します。
	 *
	 * @param target
	 * @param markItem
	 * @param appendObject
	 */
	public void addItemsAfter(AjaxRequestTarget target, ListItem<T> markItem, List<T> objs) {

		if (objs.isEmpty()) {
			return;
		}

		int insIndex = markItem.getIndex() + 1;
		List<T> modelObject = getModelObject();

		if (modelObject.size() - 1 == markItem.getIndex()) {
			modelObject.addAll(objs);
		} else {
			modelObject.addAll(insIndex, objs);
		}

		List<ListItem<T>> oldItems = getListItems();

		// リストインデックス更新のためリストアイテム構築しなおし
		removeAll();

		List<ListItem<?>> appendItems = new ArrayList<>();

		int objIndex = 0;
		for (T obj : modelObject) {
			// Add list item
			ListItem<T> item = createAndAppendItem(objIndex, () -> obj);

			if (objIndex >= insIndex && objIndex < insIndex + objs.size()) {
				appendItems.add(item);
				// update
				target.add(item);
			} else {
				item.setMarkupId(oldItems.get(objIndex - appendItems.size()).getMarkupId());
			}

			raizePopulation(item);

			objIndex++;
		}

		var scriptAppendReplaceMarker = getAfterPlaceHolderElementScript(markItem, appendItems);
		target.prependJavaScript(scriptAppendReplaceMarker);
	}

	/**
	 * 指定したリストアイテムの前に要素を追加します。
	 *
	 * @param target
	 * @param obj
	 */
	public void addItemBefore(AjaxRequestTarget target, T obj) {
		addItemsBefore(target, Arrays.asList(obj));
	}

	public void addItemsBefore(AjaxRequestTarget target, List<T> objs) {
		addItemsBefore(target, objs.iterator());
	}

	/**
	 * 指定したリストアイテムの前に要素を追加します。
	 *
	 * @param target
	 * @param markItem
	 * @param appendObject
	 */
	public void addItemsBefore(AjaxRequestTarget target, Iterator<? extends T> iterator) {

		if (!iterator.hasNext()) {
			return;
		}

		List<T> modelObject = getModelObject();

		boolean hasModelObject = modelObject.size() > 0;

		List<T> objs = new ArrayList<T>();
		for (Iterator<? extends T> iter = iterator; iterator.hasNext();) {
			T obj = iter.next();
			objs.add(obj);
		}

		modelObject.addAll(0, objs);

		List<ListItem<T>> oldItems = getListItems();

		// TODO indexの更新のほうがいいのか
		// リストインデックス更新のためリストアイテム構築しなおし
		removeAll();

		List<ListItem<?>> appendItems = new ArrayList<>();

		int objIndex = 0;
		for (T obj : modelObject) {
			// Add list item

			ListItem<T> item = createAndAppendItem(objIndex, () -> obj);

			if (objIndex < objs.size()) {
				appendItems.add(item);
				// update
				target.add(item);
			} else {
				item.setMarkupId(oldItems.get(objIndex - appendItems.size()).getMarkupId());
			}

			raizePopulation(item);

			objIndex++;
		}

		if (hasModelObject) {
			var scriptAppendReplaceMarker = getBeforePlaceHolderElementScript(oldItems.get(0), appendItems);
			target.prependJavaScript(scriptAppendReplaceMarker);

			target.appendJavaScript(getAdjustScrollTopScript());
		} else {
			target.add(getParent());
		}

	}

	/**
	 * 指定したリストアイテムの前に要素を追加します。
	 *
	 * @param target
	 * @param markItem
	 * @param appendObject
	 */
	public void addItemsBefore(AjaxRequestTarget target, ListItem<T> markItem, List<T> objs) {

		if (objs.isEmpty()) {
			return;
		}

		int insIndex = markItem.getIndex();
		List<T> modelObject = getModelObject();

		if (insIndex != 0) {
			insIndex = insIndex - 1;
		}

		modelObject.addAll(insIndex, objs);

		// TODO indexの更新のほうがいいのか
		// リストインデックス更新のためリストアイテム構築しなおし
		removeAll();

		List<ListItem<?>> appendItems = new ArrayList<>();

		for (T obj : modelObject) {
			// Add list item
			ListItem<T> item = createAndAppendItem(modelObject.indexOf(obj), () -> obj);
			raizePopulation(item);
			int objIndex = modelObject.indexOf(obj);
			if (objIndex >= insIndex && objIndex < insIndex + objs.size()) {
				appendItems.add(item);
				// update
				target.add(item);
			}
		}

		var scriptAppendReplaceMarker = getBeforePlaceHolderElementScript(markItem, appendItems);
		target.prependJavaScript(scriptAppendReplaceMarker);

	}

	/**
	 * 追加する要素のID文字列配列を取得します。
	 *
	 * @param items
	 * @return
	 */
	private String getItemIdArrayScript(List<ListItem<?>> items) {
		String itemIdArray = items.stream()
				.map(item -> "'" + item.getMarkupId() + "'")
				.collect(Collectors.joining(","));

		return itemIdArray;
	}

	/**
	 * Wicketが描画する更新対象リストアイテムに置き換えるプレースホルダーエレメントを作成するスクリプトを取得します。
	 *
	 * @param container
	 * @param items
	 * @return
	 */
	protected String getAppendPlaceHolderElementScript(Component container, List<ListItem<?>> items) {
		String itemIdArray = getItemIdArrayScript(items);

		return String.format("for (var itemId of [%s]) {" +
				"var item=document.createElement('%s');item.id=itemId;Wicket.$('%s').appendChild(item);" + "}",
				itemIdArray, itemTagName, container.getMarkupId());
	}

	/**
	 * プレースホルダーエレメントを追加するスクリプトを取得します。
	 *
	 * @param baseItem
	 * @param items
	 * @return
	 */
	protected String getAfterPlaceHolderElementScript(ListItem<?> baseItem, List<ListItem<?>> items) {
		String itemIdArray = getItemIdArrayScript(items);

		return String.format("for (var itemId of [%s]) {" +
				"var item=document.createElement('%s');item.id=itemId;Wicket.$('%s').after(item);" + "}",
				itemIdArray, itemTagName, baseItem.getMarkupId());
	}

	/**
	 * プレースホルダーエレメントを追加するスクリプトを取得します。
	 *
	 * @param baseItem
	 * @param items
	 * @return
	 */
	protected String getBeforePlaceHolderElementScript(ListItem<?> baseItem, List<ListItem<?>> items) {
		String itemIdArray = getItemIdArrayScript(items);

		var listContainerId = getParent().getMarkupId();

		var oldPos = "var container = document.getElementById('" + listContainerId + "');"
				+ "var sh = container.scrollHeight;"
				+ "content.setAttribute('data-old-scrollHeight', sh);";

		return oldPos + String.format("for (var itemId of [%s]) {" +
				"var item=document.createElement('%s');item.id=itemId;$(Wicket.$('%s')).before(item);" + "}",
				itemIdArray, itemTagName, baseItem.getMarkupId());
	}

	/**
	 * 古い先頭の要素の表示がキープされるよう、scrollTopを調整するスクリプトを取得します。<br>
	 *
	 * @return
	 */
	protected String getAdjustScrollTopScript() {
		var listContainerId = getParent().getMarkupId();

		return "var container = document.getElementById('" + listContainerId + "');"
				+ "var sh = container.scrollHeight;"
				+ "container.scrollTop = (sh - container.getAttribute('data-old-scrollHeight'));";
	}

}
