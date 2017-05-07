package com.libs.custombottomsheetbehavior.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bdb.bikedeboa.R;

import java.lang.ref.WeakReference;

/**
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~
 ~ https://github.com/miguelhincapie/CustomBottomSheetBehavior
 */

/**
 * This behavior should be applied on an AppBarLayout... More Explanations coming soon
 */
public class MergedAppBarLayoutBehavior extends AppBarLayout.ScrollingViewBehavior {

    private static final String TAG = MergedAppBarLayoutBehavior.class.getSimpleName();

    private boolean mInit = false;

    private FrameLayout.LayoutParams mBackGroundLayoutParams;

    private Context mContext;
    /**
     * To avoid using multiple "peekheight=" in XML and looking flexibility allowing {@link BottomSheetBehaviorGoogleMapsLike#mPeekHeight}
     * get changed dynamically we get the {@link NestedScrollView} that has
     * "app:layout_behavior=" {@link BottomSheetBehaviorGoogleMapsLike} inside the {@link CoordinatorLayout}
     */
    private WeakReference<BottomSheetBehaviorGoogleMapsLike> mBottomSheetBehaviorRef;
    private float mInitialY;
    private boolean mVisible = false;

    private String mToolbarTitle;

    private Toolbar mToolbar;
    private TextView mTitleTextView;
    private View mBackground;
    private View.OnClickListener mOnNavigationClickListener;

    private int mCurrentTitleAlpha = 0;

    public MergedAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (dependency instanceof NestedScrollView) {
            try {
                BottomSheetBehaviorGoogleMapsLike.from(dependency);
                return true;
            }
            catch (IllegalArgumentException e){}
        }
        return false;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        if (!mInit) {
            init(parent, child);
        }
        /**
         * Following docs we should return true if the Behavior changed the child view's size or position, false otherwise
         */
        boolean childMoved = false;

        if(isDependencyYBelowAnchorPoint(parent, dependency)){

            childMoved = setToolbarVisible(false,child);

        }else if(isDependencyYBetweenAnchorPointAndToolbar(parent, child,dependency)){

            childMoved = setToolbarVisible(false,child);
            setFullBackGroundColor(android.R.color.transparent);
            setPartialBackGroundHeight(0);

        } else if(isDependencyYBelowToolbar(child, dependency) && ! isDependencyYReachTop(dependency)){

            childMoved = setToolbarVisible(false,child);
            if(isStatusBarVisible())
                setStatusBarBackgroundVisible(false);
            setFullBackGroundColor(android.R.color.transparent);
            setPartialBackGroundHeight((int)((child.getHeight() + child.getY()) - dependency.getY()));

        } else if(isDependencyYBelowStatusToolbar(child, dependency) || isDependencyYReachTop(dependency)){

            childMoved = setToolbarVisible(false,child);
            if(!isStatusBarVisible())
                setStatusBarBackgroundVisible(false);
            setFullBackGroundColor(android.R.color.transparent);
            setPartialBackGroundHeight(0);
        }
        return childMoved;
    }

    private void init(@NonNull CoordinatorLayout parent, @NonNull View child){

        AppBarLayout appBarLayout = (AppBarLayout) child;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarLayout.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        }

        mToolbar = (Toolbar) appBarLayout.findViewById(R.id.expanded_toolbar);
        mBackground = appBarLayout.findViewById(R.id.background);
        mBackGroundLayoutParams = (FrameLayout.LayoutParams) mBackground.getLayoutParams();
        getBottomSheetBehavior(parent);

        mInitialY = child.getY();

        child.setVisibility(mVisible ? View.VISIBLE : View.INVISIBLE);
        setStatusBarBackgroundVisible(mVisible);

        setFullBackGroundColor(android.R.color.transparent);
        setPartialBackGroundHeight(0);
        mInit = true;
        setToolbarVisible(false,child);
    }

    /**
     * Look into the CoordiantorLayout for the {@link BottomSheetBehaviorGoogleMapsLike}
     * @param coordinatorLayout with app:layout_behavior= {@link BottomSheetBehaviorGoogleMapsLike}
     */
    private void getBottomSheetBehavior(@NonNull CoordinatorLayout coordinatorLayout) {

        for (int i = 0; i < coordinatorLayout.getChildCount(); i++) {
            View child = coordinatorLayout.getChildAt(i);

            if (child instanceof NestedScrollView) {

                try {
                    BottomSheetBehaviorGoogleMapsLike temp = BottomSheetBehaviorGoogleMapsLike.from(child);
                    mBottomSheetBehaviorRef = new WeakReference<>(temp);
                    break;
                }
                catch (IllegalArgumentException e){}
            }
        }
    }

    private boolean isDependencyYBelowAnchorPoint(@NonNull CoordinatorLayout parent, @NonNull View dependency){
        if (mBottomSheetBehaviorRef == null || mBottomSheetBehaviorRef.get() == null)
            getBottomSheetBehavior(parent);
        return dependency.getY() > mBottomSheetBehaviorRef.get().getAnchorPoint();
    }

    private boolean isDependencyYBetweenAnchorPointAndToolbar(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency){
        if (mBottomSheetBehaviorRef == null || mBottomSheetBehaviorRef.get() == null)
            getBottomSheetBehavior(parent);
        return dependency.getY() <= mBottomSheetBehaviorRef.get().getAnchorPoint() && dependency.getY() > child.getY() + child.getHeight();
    }

    private boolean isDependencyYBelowToolbar(@NonNull View child, @NonNull View dependency){
        return dependency.getY() <= child.getY() + child.getHeight() && dependency.getY() > child.getY();
    }

    private boolean isDependencyYBelowStatusToolbar(@NonNull View child, @NonNull View dependency){
        return dependency.getY() <= child.getY();
    }

    private boolean isDependencyYReachTop(@NonNull View dependency){
        return dependency.getY() == 0;
    }

    private void setPartialBackGroundHeight(int height){
        mBackGroundLayoutParams.height = height;
        mBackground.setLayoutParams(mBackGroundLayoutParams);
    }

    private void setFullBackGroundColor(@ColorRes int colorRes){
        mToolbar.setBackgroundColor(ContextCompat.getColor(mContext,colorRes));
    }

    private TextView findTitleTextView(Toolbar toolbar){
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View toolBarChild = toolbar.getChildAt(i);
            if (toolBarChild instanceof TextView &&
                    ((TextView)toolBarChild).getText() != null &&
                    toolBarChild.getId() == R.id.name) {
                return (TextView) toolBarChild;
            }
        }
        return null;
    }

    private boolean setToolbarVisible(boolean visible, final View child){
        ViewPropertyAnimator mAppBarLayoutAnimation;
        boolean childMoved = false;
        if(visible && !mVisible){
            childMoved = true;
            child.setY(-child.getHeight()/3);
            mAppBarLayoutAnimation = child.animate().setDuration(mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
            mAppBarLayoutAnimation.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    child.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // ((AppCompatActivity)mContext).setSupportActionBar(mToolbar);
//                    mToolbar.setNavigationOnClickListener(mOnNavigationClickListener);
//                    ActionBar actionBar = ((AppCompatActivity)mContext).getSupportActionBar();
//                    if (actionBar != null) {
//                        actionBar.hide();
//                   }
                    mVisible = true;
                }
            });
            mAppBarLayoutAnimation.alpha(1).y(mInitialY).start();
        }else if(!visible && mVisible){
            mAppBarLayoutAnimation = child.animate().setDuration(mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
            mAppBarLayoutAnimation.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    child.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // ((AppCompatActivity)mContext).setSupportActionBar(null);
                    mVisible = false;
                }
            });
            mAppBarLayoutAnimation.alpha(0).start();
        }

        return childMoved;
    }

    private boolean isStatusBarVisible(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return ((Activity)mContext).getWindow().getStatusBarColor() ==
                    ContextCompat.getColor(mContext,R.color.colorPrimaryDark);
        }
        return true;
    }

    private void setStatusBarBackgroundVisible(boolean visible){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(visible){
                Window window = ((Activity)mContext).getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(mContext,android.R.color.transparent));
            }else {
                Window window = ((Activity)mContext).getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ContextCompat.getColor(mContext,android.R.color.transparent));
            }
        }
    }

    public void setNavigationOnClickListener(View.OnClickListener listener){
        this.mOnNavigationClickListener = listener;
    }

    public void setToolbarTitle(String title) {
        this.mToolbarTitle = title;
        if(this.mToolbar!=null)
            this.mToolbar.setTitle(title);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, View child) {
        return new SavedState(super.onSaveInstanceState(parent, child),
                mVisible,
                mToolbarTitle,
                mCurrentTitleAlpha);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, View child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        this.mVisible = false; // ss.mVisible;
        this.mToolbarTitle = ss.mToolbarTitle;
        this.mCurrentTitleAlpha = ss.mTitleAlpha;
    }

    protected static class SavedState extends View.BaseSavedState {

        final boolean mVisible;
        final String mToolbarTitle;
        final int mTitleAlpha;

        public SavedState(Parcel source) {
            super(source);
            mVisible = source.readByte() != 0;
            mToolbarTitle = source.readString();
            mTitleAlpha = source.readInt();
        }

        public SavedState(Parcelable superState, boolean visible, String toolBarTitle, int titleAlpha) {
            super(superState);
            this.mVisible = visible;
            this.mToolbarTitle = toolBarTitle;
            this.mTitleAlpha = titleAlpha;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (mVisible ? 1 : 0));
            out.writeString(mToolbarTitle);
            out.writeInt(mTitleAlpha);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public static <V extends View> MergedAppBarLayoutBehavior from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof MergedAppBarLayoutBehavior)) {
            throw new IllegalArgumentException("The view is not associated with " +
                    "MergedAppBarLayoutBehavior");
        }
        return (MergedAppBarLayoutBehavior) behavior;
    }
}