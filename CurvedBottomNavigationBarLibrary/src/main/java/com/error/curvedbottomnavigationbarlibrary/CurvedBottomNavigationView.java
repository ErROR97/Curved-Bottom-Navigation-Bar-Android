package com.error.curvedbottomnavigationbarlibrary;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CurvedBottomNavigationView extends BottomNavigationView {

    CurvedBottomNavigationView mCurvedBottomNavigationView;

    private boolean isViewCreated = false;
    private boolean setAnimation = false;
    private int animationTime = 300;

    private int curvedColor = 0;
    private int backGroundColor =0;


    private Path mPath;
    private Paint mPaint;
    private View itemSelectedView;

    Menu menu;
    private List<CostomItemMenu> menuItems = new ArrayList<>();

    private boolean isOnTop = false;

    public int CURVE_CIRCLE_REDIUS = 130;

    public Point mFirstCurveStartPoint = new Point();
    public Point mFirstCurveEndPoint = new Point();
    public Point mFirstCurveControlPoint1 = new Point();
    public Point mFirstCurveControlPoint2 = new Point();

    public Point mSecondCurveStartPoint = new Point();
    public Point mSecondCurveEndPoint = new Point();
    public Point mSecondCurveControlPoint1 = new Point();
    public Point mSecondCurveControlPoint2 = new Point();

    public int mNavigationBarWidth,mNavigationBarHeight;


    public CurvedBottomNavigationView(@NonNull Context context) {
        super(context);
        init(context,null);
    }


    public CurvedBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);

    }

    public CurvedBottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs) {

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.setElevation(0);

        if (attrs != null) {
            if (!isInEditMode()) {

                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CostomBottomNavigationView);

                try {

                    setBackgroundColor(a.getColor(R.styleable.CostomBottomNavigationView_curved_color,0));

                    mPaint.setColor(a.getColor(R.styleable.CostomBottomNavigationView_background_color,0));


                    isOnTop = a.getBoolean(R.styleable.CostomBottomNavigationView_to_top_redius,false);

                    setAnimation = a.getBoolean(R.styleable.CostomBottomNavigationView_setAnimation,false);

                    animationTime = a.getInt(R.styleable.CostomBottomNavigationView_animation_time,300);

                    menu.getItem(a.getInteger(R.styleable.CostomBottomNavigationView_default_item_selected,0)).setChecked(true);



                } catch (Exception e) {

                } finally {
                    a.recycle();
                }
            }
        }

        menu = getMenu();
        setMenuItemsArry();
        mCurvedBottomNavigationView = this;



    }

    @Override
    protected void onSizeChanged(int w,int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);

        mNavigationBarWidth = getWidth();
        mNavigationBarHeight = getHeight();

        for (int i = 0; i < menu.size(); i++) if (menu.getItem(i).isChecked()) selectItem(menu.getItem(i));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rediusToTopOnDraw();

        canvas.drawPath(mPath,mPaint);
    }



    private MenuItem getItemFromXY(int x, int y){
        final MenuItem[] item = {null};
        final boolean[] flag1 = new boolean[1];
        final boolean[] flag2 = new boolean[1];
        Menu menu = getMenu();
        int[] locationF = new int[2];
        int[] locationE = new int[2];
        for (final int[] i = {0}; i[0] < menu.size() - 1;) {
            if (menu.size() >=2) {
                View viewF = findViewById(menu.getItem(i[0]).getItemId());
                View viewE = findViewById(menu.getItem(i[0] + 1).getItemId());

                int finalI = i[0];
                viewE.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    if (!flag1[0]){
                        viewF.getLocationOnScreen(locationF);
                        viewF.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                            if (!flag2[0]){
                                viewE.getLocationOnScreen(locationE);
                                i[0]++;
                                if ((locationF[0] >= x) && (x <= locationE[0])) {
                                    if ((locationF[1] >= y) && (y <= locationE[1]))
                                        item[0] = menu.getItem(finalI);
                                }
                                flag2[0] = true;
                            }
                        });
                        flag1[0] = true;
                    }
                });
            }
        }
        return item[0];
    }

    public void selectItem(MenuItem menuItem) {

        int[] location = new int[2];
        final boolean[] flag = {true};
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            View view = findViewById(item.getItemId());
            if (item == menuItem){

                view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                    if (flag[0]) {
                        view.getLocationOnScreen(location);
                        view.setScaleX(1.3f);
                        view.setScaleY(1.3f);

                        if (isAnimation()) {

                            if (itemSelectedView != null) {
                                setAnimationDraw(location[0] + view.getWidth() / 2
                                        , ((mNavigationBarHeight / 3) + itemSelectedView.getHeight() / 9)
                                        , getAnimationTime());
                                item.setIcon(null);
                            } else
                                setAnimationDraw(location[0] + view.getWidth() / 2
                                        , ((mNavigationBarHeight / 3))
                                        , getAnimationTime());

                        } else {
                            drow(location[0] + view.getWidth() / 2
                                    , ((mNavigationBarHeight / 3) + itemSelectedView.getHeight() / 9));
                            if (itemSelectedView != null){
                                itemSelectedView.setX(location[0] + view.getWidth() / 2 - itemSelectedView.getWidth() / 2);
                                item.setIcon(null);
                            }

                        }

                        item.setEnabled(false);
                        flag[0] = false;

                    }
                });

            } else {
                view.setScaleX(1f);
                view.setScaleY(1f);
                item.setEnabled(true);
                item.setIcon(menuItems.get(i).getIcon());
            }
        }

    }

    private void setMenuItemsArry() {
        for (int i = 0; i < menu.size(); i++){
            CostomItemMenu costomMenu = new CostomItemMenu();

            costomMenu.setId(menu.getItem(i).getItemId());
            costomMenu.setIcon(menu.getItem(i).getIcon());
            costomMenu.setTitle((String) menu.getItem(i).getTitle());
            costomMenu.setGroupId(menu.getItem(i).getGroupId());
            costomMenu.setOrder(menu.getItem(i).getOrder());
            costomMenu.setIntent(menu.getItem(i).getIntent());
            costomMenu.setCheckable(menu.getItem(i).isCheckable());
            costomMenu.setVisible(menu.getItem(i).isVisible());
            costomMenu.setEnabled(menu.getItem(i).isEnabled());

            menuItems.add(costomMenu);
        }
    }

    private void rediusToTopOnDraw() {

        if (isOnTop) {

            setPadding(0,mNavigationBarHeight / 4 ,0,0);

            mPath.reset();
            mPath.moveTo(0, mNavigationBarHeight/3);

            mPath.lineTo(mFirstCurveStartPoint.x , mFirstCurveStartPoint.y + mNavigationBarHeight/3);

            mPath.cubicTo(mFirstCurveControlPoint1.x , mFirstCurveControlPoint1.y +mNavigationBarHeight/3,
                    mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y +mNavigationBarHeight/3,
                    mFirstCurveEndPoint.x, mFirstCurveEndPoint.y +mNavigationBarHeight/3);

            mPath.cubicTo(mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y +mNavigationBarHeight/3,
                    mSecondCurveControlPoint2.x , mSecondCurveControlPoint2.y +mNavigationBarHeight/3,
                    mSecondCurveEndPoint.x ,mSecondCurveEndPoint.y +mNavigationBarHeight/3);

            mPath.lineTo(mNavigationBarWidth,mNavigationBarHeight /3 );
            mPath.lineTo(mNavigationBarWidth, mNavigationBarHeight);
            mPath.lineTo(0, mNavigationBarHeight);

            mPath.close();
        } else {
            mPath.reset();
            mPath.moveTo(0, 0);

            mPath.lineTo(mFirstCurveStartPoint.x , mFirstCurveStartPoint.y );

            mPath.cubicTo(mFirstCurveControlPoint1.x , mFirstCurveControlPoint1.y ,
                    mFirstCurveControlPoint2.x, mFirstCurveControlPoint2.y ,
                    mFirstCurveEndPoint.x, mFirstCurveEndPoint.y);

            mPath.cubicTo(mSecondCurveControlPoint1.x, mSecondCurveControlPoint1.y,
                    mSecondCurveControlPoint2.x , mSecondCurveControlPoint2.y  ,
                    mSecondCurveEndPoint.x ,mSecondCurveEndPoint.y );

            mPath.lineTo(mNavigationBarWidth,0);
            mPath.lineTo(mNavigationBarWidth, mNavigationBarHeight);
            mPath.lineTo(0, mNavigationBarHeight);

            mPath.close();
        }
    }

    public void drow(int x, int redius) {

        if(isOnTop) {
            CURVE_CIRCLE_REDIUS = redius ;


            mFirstCurveStartPoint.set((x)
                    - (CURVE_CIRCLE_REDIUS * 2)
                    - (CURVE_CIRCLE_REDIUS / 3), 0);

            mFirstCurveEndPoint.set(x,
                    -(CURVE_CIRCLE_REDIUS /2
                            + (CURVE_CIRCLE_REDIUS / 3)));

            mSecondCurveStartPoint = mFirstCurveEndPoint;

            mSecondCurveEndPoint.set((x)
                    + (CURVE_CIRCLE_REDIUS * 2)
                    + (CURVE_CIRCLE_REDIUS / 3), 0);

            mFirstCurveControlPoint1.set(mFirstCurveStartPoint.x
                            + CURVE_CIRCLE_REDIUS
                            + (CURVE_CIRCLE_REDIUS / 2)
                    , mFirstCurveStartPoint.y);

            mFirstCurveControlPoint2.set(mFirstCurveEndPoint.x
                            - (CURVE_CIRCLE_REDIUS * 2)
                            + CURVE_CIRCLE_REDIUS
                    , mFirstCurveEndPoint.y);

            mSecondCurveControlPoint1.set(mSecondCurveStartPoint.x
                            + (CURVE_CIRCLE_REDIUS * 2)
                            - CURVE_CIRCLE_REDIUS
                    , mSecondCurveStartPoint.y);
            mSecondCurveControlPoint2.set(mSecondCurveEndPoint.x
                            - (CURVE_CIRCLE_REDIUS
                            + (CURVE_CIRCLE_REDIUS / 2))
                    , mSecondCurveEndPoint.y);
        } else {

            CURVE_CIRCLE_REDIUS = redius;

            mFirstCurveStartPoint.set((x)
                    - (CURVE_CIRCLE_REDIUS * 2)
                    - (CURVE_CIRCLE_REDIUS / 3), 0);

            mFirstCurveEndPoint.set(x,
                    CURVE_CIRCLE_REDIUS
                            + (CURVE_CIRCLE_REDIUS / 2));

            mSecondCurveStartPoint = mFirstCurveEndPoint;

            mSecondCurveEndPoint.set((x)
                    + (CURVE_CIRCLE_REDIUS * 2)
                    + (CURVE_CIRCLE_REDIUS / 3), 0);


            mFirstCurveControlPoint1.set(mFirstCurveStartPoint.x
                            + CURVE_CIRCLE_REDIUS
                            + (CURVE_CIRCLE_REDIUS / 2)
                    , mFirstCurveStartPoint.y);

            mFirstCurveControlPoint2.set(mFirstCurveEndPoint.x
                            - (CURVE_CIRCLE_REDIUS * 2)
                            + CURVE_CIRCLE_REDIUS
                    , mFirstCurveEndPoint.y);

            mSecondCurveControlPoint1.set(mSecondCurveStartPoint.x
                            + (CURVE_CIRCLE_REDIUS * 2)
                            - CURVE_CIRCLE_REDIUS
                    , mSecondCurveStartPoint.y);
            mSecondCurveControlPoint2.set(mSecondCurveEndPoint.x
                            - (CURVE_CIRCLE_REDIUS
                            + (CURVE_CIRCLE_REDIUS / 2))
                    , mSecondCurveEndPoint.y);
        }

    }

    public void setItemSelectedView(View view) {

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            if (!isViewCreated ) {

                int[] location = new int[2];
                int[] locationMCostomBottomNavigationView = new int[2];
                int defultItemLocation = 0;

                for (int i = 0; i < menu.size(); i++) if (menu.getItem(i).isChecked()) defultItemLocation = i;

                View viewSelected = findViewById(menu.getItem(defultItemLocation).getItemId());
                viewSelected.getLocationOnScreen(location);
                mCurvedBottomNavigationView.getLocationOnScreen(locationMCostomBottomNavigationView);
                view.setX(location[0] + viewSelected.getWidth() / 2 - view.getWidth() / 2);

                //int y = (location[1] + viewSelected.getHeight() / 2 - view.getHeight() / 2);
                //int yRedius = ((mNavigationBarHeight / 3) + view.getHeight() / 10) + (((mNavigationBarHeight / 3) + view.getHeight() / 10) / 2);


                Rect rectangle = new Rect();
                Window window = ((Activity) getContext()).getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                int statusBarHeight = rectangle.top;
                int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                int titleBarHeight = contentViewTop - statusBarHeight;

                Log.i("*** Value :: ", "StatusBar Height= " + statusBarHeight + " , TitleBar Height = " + titleBarHeight);


                if (isOnTop)
                    view.setY((location[1] + viewSelected.getHeight() / 2 - mNavigationBarHeight / 4 - view.getHeight() / 3) - titleBarHeight - statusBarHeight );
                else
                    view.setY(((location[1]
                            + viewSelected.getHeight() / 2
                            - view.getHeight() / 2)
                            -  ((mNavigationBarHeight / 3)
                            + view.getHeight() / 10)
                            + (((mNavigationBarHeight / 3)
                            + view.getHeight() / 10) / 2)
                            / 2) - titleBarHeight - statusBarHeight );

                itemSelectedView = view;
                isViewCreated = true;

            }
        });
    }

    public void setAnimationDraw(int x,int redius,int time) {

        ValueAnimator animator = ValueAnimator.ofInt(mFirstCurveEndPoint.x,x);
        animator.setDuration(time);
        animator.start();

        animator.addUpdateListener(animation -> {
            drow((Integer) animation.getAnimatedValue(),redius);
            if (itemSelectedView != null) itemSelectedView.setX((Integer) animation.getAnimatedValue() - itemSelectedView.getWidth() / 2);
            invalidate();
        });
    }

    public void setRediusOnTop() {
        isOnTop = true;
        invalidate();
    }

    public int getAnimationTime () {
        return animationTime;
    }

    public boolean isAnimation() { return setAnimation; }

    public View getItemSelectedView(){ return itemSelectedView; }

    public class CostomItemMenu{
        private Drawable icon;
        private String title;
        private int id;
        private int groupId;
        private int order;
        private Intent intent;
        private Boolean checkable;
        private Boolean checked;
        private Boolean visible;
        private Boolean enabled;


        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public Intent getIntent() {
            return intent;
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }

        public Boolean isCheckable() {
            return checkable;
        }

        public void setCheckable(Boolean checkable) {
            this.checkable = checkable;
        }

        public Boolean isChecked() {
            return checked;
        }

        public void setChecked(Boolean checked) {
            this.checked = checked;
        }

        public Boolean isVisible() {
            return visible;
        }

        public void setVisible(Boolean visible) {
            this.visible = visible;
        }

        public Boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

}
