package jp.try0.wicket.example.markup.html.list;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

/**
 * インフィニットスクロール<br>
 * 本コンポーネントの親要素にもwicket:idを割り振ること
 *
 * @author Ryo Tsunoda
 *
 * @param <T>
 */
public abstract class InfiniteScrollListView<T> extends PartialUpdatableListView<T> {
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_ITEM_COUNT = 20;

	/**
	 * アイテムロードタイプ
	 *
	 * @author Ryo Tsunoda
	 *
	 */
	public static enum LoadContentPosition {
		/**
		 * 要素を先頭に追加する
		 */
		BEFORE,
		/**
		 * 要素を末尾に追加する
		 */
		AFTER,
		;
	}

	/**
	 * ロードタイプ
	 */
	private LoadContentPosition loadContentPosition = LoadContentPosition.AFTER;

	/**
	 * アイテムデータプロバイダー
	 */
	private IDataProvider<T> itemProvider;

	/**
	 * 1回のロードアイテム数
	 */
	private int loadItemCount = DEFAULT_ITEM_COUNT;

	/**
	 * ロード時に実行するスクリプト
	 */
	private String onLoadScript = "";

	/**
	 * ロード処理
	 */
	private AbstractDefaultAjaxBehavior itemLoadBehavior = new AbstractDefaultAjaxBehavior() {

		@Override
		protected void respond(AjaxRequestTarget target) {

			Iterator<? extends T> itemIterator = itemProvider.iterator(getModelObject().size(), loadItemCount);

			switch (loadContentPosition) {
			case AFTER:
				addItems(target, itemIterator);
				break;
			case BEFORE:
				addItemsBefore(target, itemIterator);
				break;
			default:
				break;
			}

			onLoadItem(target);
		}

	};

	/**
	 * コンストラクター
	 *
	 * @param id
	 */
	public InfiniteScrollListView(String id) {
		super(id);
	}

	/**
	 * コンストラクター
	 *
	 * @param id
	 * @param model
	 * @param itemProvider TODO IDataProviderじゃなく専用のプロバイダー用意したほうがいいか？
	 */
	public InfiniteScrollListView(String id, IModel<List<T>> model, IDataProvider<T> itemProvider) {
		super(id, model);
		this.itemProvider = itemProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		String listContainerId = getListContainerMarkupId();

		String loadScript = "var " + listContainerId + " = document.querySelector('#" + listContainerId + "');";

		if (loadContentPosition == LoadContentPosition.BEFORE) {
			// 先頭でロード場合は、表示初期値は要素末尾とする
			loadScript += listContainerId + ".scrollTop = " + listContainerId + ".scrollHeight;";
		}

		loadScript += listContainerId + ".addEventListener('scroll', function() {";
		if (loadContentPosition == LoadContentPosition.BEFORE) {
			// 先頭でロード
			loadScript += "if(" + listContainerId + ".scrollTop == 0) {"
					+ itemLoadBehavior.getCallbackScript()
					+ getOnLoadScript()
					+ "} ";
		} else {
			// 末尾でロード
			loadScript += " if(" + listContainerId + ".scrollTop + " + listContainerId + ".clientHeight >= "
					+ listContainerId
					+ ".scrollHeight) {"
					+ itemLoadBehavior.getCallbackScript()
					+ getOnLoadScript()
					+ "}";
		}

		loadScript += "});";

		response.render(OnDomReadyHeaderItem.forScript(loadScript));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onInitialize() {
		super.onInitialize();

		// ListViewに追加しても発火しない
		getParent().add(itemLoadBehavior);

	}

	/**
	 * ロードタイプを取得します。
	 *
	 * @return
	 */
	public LoadContentPosition getLoadContentPosition() {
		return loadContentPosition;
	}

	/**
	 * ロードタイプをセットします。
	 *
	 * @param loadContentPosition
	 */
	public void setLoadContentPosition(LoadContentPosition loadContentPosition) {
		this.loadContentPosition = loadContentPosition;
	}

	/**
	 * アイテムプロバイダーを取得します。
	 *
	 * @return
	 */
	public IDataProvider<T> getItemProvider() {
		return itemProvider;
	}

	/**
	 * アイテムプロバイダーをセットします。
	 *
	 * @param itemProvider
	 */
	public void setItemProvider(IDataProvider<T> itemProvider) {
		this.itemProvider = itemProvider;
	}

	/**
	 * ロードアイテム数を取得します。
	 *
	 * @return
	 */
	public int getLoadItemCount() {
		return loadItemCount;
	}

	/**
	 * ロードアイテム数をセットします。
	 *
	 * @param loadItemCount
	 */
	public void setLoadItemCount(int loadItemCount) {
		this.loadItemCount = loadItemCount;
	}

	/**
	 * アイテムロード時に実行するスクリプトを取得します。
	 *
	 * @return
	 */
	public String getOnLoadScript() {
		return onLoadScript;
	}

	/**
	 * アイテムロード時に実行するスクリプトをセットします。
	 *
	 * @param javascript
	 */
	public void setOnLoadScript(String onLoadScript) {
		this.onLoadScript = onLoadScript;
	}



	/**
	 * リストコンテナーを取得します。
	 *
	 * @see #getParent()
	 * @return
	 */
	public MarkupContainer getListContainer() {
		return getParent();
	}

	/**
	 * リストコンテナーのIDを取得します。
	 *
	 * @see #getListContainer()
	 * @see MarkupContainer#getMarkupId()
	 * @return
	 */
	public String getListContainerMarkupId() {
		return getListContainer().getMarkupId();
	}

	/**
	 * アイテムロード時に実行します。
	 *
	 * @param target
	 * @param position
	 */
	protected void onLoadItem(AjaxRequestTarget target) {
		// noop
	}



}
