package olga.koteoglou.photogeneia;

import android.graphics.drawable.Drawable;

public class QuickActionItem {

	private Drawable icon;
	private String title;
	private int actionId = -1;
	private boolean sticky;

	public QuickActionItem(int actionId, String title, Drawable icon) {

		this.title = title;
		this.icon = icon;
		this.actionId = actionId;
	}

	public QuickActionItem() {

		this(-1, null, null);
	}

	public QuickActionItem(int actionId, String title) {

		this(actionId, title, null);
	}

	public QuickActionItem(Drawable icon) {

		this(-1, null, icon);
	}

	public QuickActionItem(int actionId, Drawable icon) {

		this(actionId, null, icon);
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public Drawable getIcon() {
		return this.icon;
	}

	public void setActionId(int actionId) {

		this.actionId = actionId;
	}

	public int getActionId() {

		return actionId;
	}

	public void setSticky(boolean sticky) {

		this.sticky = sticky;
	}

	public boolean isSticky() {

		return sticky;
	}

}           