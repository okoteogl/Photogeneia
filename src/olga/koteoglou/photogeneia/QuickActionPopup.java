package olga.koteoglou.photogeneia;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class QuickActionPopup extends MyPopupWindow implements OnDismissListener {
	private View mRootView;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private ScrollView mScroller;

	private OnActionItemClickListener mItemClickListener;
	private OnDismissListener mDismissListener;

	private List<QuickActionItem> actionItems = new ArrayList<QuickActionItem>();

	private boolean mDidAction;
	private boolean reverseOrientationItem = false;

	private int mChildPos;
	private int mInsertPos;
	private int rootWidth=0;


	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	public static final int ANIM_GROW_FROM_LEFT = 1;


	/**
	 * Constructor for default vertical layout
	 * 
	 * @param context  Context
	 */
	public QuickActionPopup(Context context) {
		this(context, VERTICAL);
	}






	/**
	 * Constructor allowing orientation override
	 * 
	 * @param context    Context
	 * @param orientation Layout orientation, can be vartical or horizontal
	 */
	public QuickActionPopup(Context context, int orientation) {
		super(context);

		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		//set popup menu orientation to vertical
		setRootViewId(R.layout.popup_vertical);


		//mAnimStyle  = ANIM_GROW_FROM_LEFT;
		mChildPos   = 0;
	}

	/**
	 * Set the background of the popup and the two arrows. Must be 9-patch.
	 * @param popup
	 * @param arrowUp
	 * @param arrowDown
	 */
	public void setBackgroundResources(int popup, int arrowUp, int arrowDown){

		if(popup!=0 && arrowUp!=0 && arrowDown!=0){

			mScroller.setBackgroundResource(popup);
//			mArrowDown.setImageResource(arrowDown);
//			mArrowUp.setImageResource(arrowUp);

		}
	}


	/**
	 * Get action item at an index
	 * 
	 * @param index  Index of item (position from callback)
	 * 
	 * @return  Action Item at the position
	 */
	public QuickActionItem getActionItem(int index) {
		return actionItems.get(index);
	}

	/**
	 * Set root view.
	 * 
	 * @param id Layout resource id
	 */
	private void setRootViewId(int id) {
		mRootView   = (ViewGroup) mInflater.inflate(id, null);
		mTrack  = (ViewGroup) mRootView.findViewById(R.id.tracks);
		mScroller   = (ScrollView) mRootView.findViewById(R.id.scroller);

		mRootView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}

	/**
	 * Set animation style
	 * 
	 * @param mAnimStyle animation style, default is set to ANIM_AUTO
	 */
//	public void setAnimStyle(int mAnimStyle) {
//		this.mAnimStyle = mAnimStyle;
//	}

	/**
	 * Set listener for action item clicked.
	 * 
	 * @param listener Listener
	 */
	public void setOnActionItemClickListener(OnActionItemClickListener listener) {
		mItemClickListener = listener;
	}

	/**
	 * Add action item
	 * 
	 * @param action  {@link QuickActionItem}
	 */
	public void addActionItem(QuickActionItem action) {
		actionItems.add(action);

		String title    = action.getTitle();
		Drawable icon   = action.getIcon();

		View container;
		container = mInflater.inflate(R.layout.action_item_horizontal, null);

		ImageView img   = (ImageView) container.findViewById(R.id.iv_icon);
		TextView text   = (TextView) container.findViewById(R.id.tv_title);

		if (icon != null) {
			img.setImageDrawable(icon);
		} else {
			img.setVisibility(View.GONE);
		}

		if (title != null) {
			text.setText(title);
		} else {
			text.setVisibility(View.GONE);
		}

		final int pos   =  mChildPos;
		final int actionId  = action.getActionId();

		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mItemClickListener != null) {
					mItemClickListener.onItemClick(QuickActionPopup.this, pos, actionId);
				}

				if (!getActionItem(pos).isSticky()) {  
					mDidAction = true;

					dismiss();
				}
			}
		});

		container.setFocusable(true);
		container.setClickable(true);            

		mTrack.addView(container, mInsertPos);

		mChildPos++;
		mInsertPos++;
	}

	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor                                
	 * 
	 */
	public void show (View anchor) {
		preShow();

		int xPos, yPos;

		mDidAction          = false;

		int[] location      = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();

		if (rootWidth == 0) {

			rootWidth   = mRootView.getMeasuredWidth();
		}

		@SuppressWarnings("deprecation")
		int screenWidth     = mWindowManager.getDefaultDisplay().getWidth();
		@SuppressWarnings("deprecation")
		int screenHeight    = mWindowManager.getDefaultDisplay().getHeight();

		if ((anchorRect.left + rootWidth) > screenWidth) {


			xPos        = anchorRect.left - (rootWidth-anchor.getWidth());          
			xPos        = (xPos < 0) ? 0 : xPos;

			//arrowPos    = anchorRect.centerX()-xPos;

		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth/2);


			} else {
				xPos = anchorRect.left;
			}

			//arrowPos = anchorRect.centerX()-xPos;
		}

		int dyTop           = anchorRect.top;
		int dyBottom        = screenHeight - anchorRect.bottom;

		boolean onTop       = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos            = 15;
				LayoutParams l  = mScroller.getLayoutParams();
				l.height        = dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) { 
				LayoutParams l  = mScroller.getLayoutParams();
				l.height        = dyBottom;
			}
		}

		//showArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

		//setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	/**
	 * Set animation style
	 * 
	 * @param screenWidth screen width
	 * @param requestedX distance from left edge
	 * @param onTop flag to indicate where the popup should be displayed. Set TRUE if  *displayed on top of anchor view and vice versa
	 */

//	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
//		//int arrowPos = requestedX - mArrowUp.getMeasuredWidth()/2;
//
//		switch (mAnimStyle) {
//		case ANIM_GROW_FROM_LEFT:
//			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
//			break;
//
//		}
//	}


	//private void showArrow(int whichArrow, int requestedX) {

		//		final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
		//		final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;
		//
		//		final int arrowWidth = mArrowUp.getMeasuredWidth();
		//
		//		showArrow.setVisibility(View.VISIBLE);
		//
		//		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)showArrow.getLayoutParams();
		//
		//		param.leftMargin = requestedX - arrowWidth / 2;
		//
		//		hideArrow.setVisibility(View.INVISIBLE);
	//}

	/**
	 * Listener for dismissing the window.
	 */

	public void setOnDismissListener(QuickActionPopup.OnDismissListener listener) {
		setOnDismissListener(this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	/**
	 * If we want to reverse the item orientation.
	 */

	public boolean isReverseOrientationItem() {
		return reverseOrientationItem;
	}

	public void setReverseOrientationItem(boolean reverseOrientationItem) {
		this.reverseOrientationItem = reverseOrientationItem;
	}

	/**
	 * Listener for item click
	 *
	 */
	public interface OnActionItemClickListener {

		public abstract void onItemClick(QuickActionPopup source, int pos, int actionId);
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
}