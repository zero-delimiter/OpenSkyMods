//Please don't replace listeners with lambda!
package com.android.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Menu {
    // ********** Here you can easly change the menu appearance **********//

    // region Variable
    public static final String TAG = "Mod_Menu";
    private static Menu instance;
    public static boolean isLoggedIn = false;
    public static int ACCENT_COLOR = Color.parseColor("#FFFFFB");

    int ACCENT_DARK;
    int ACCENT_GLOW;
    int BTN_BORDER;
    int BTN_COLOR;
    int CategoryBG;
    int CheckBoxColor;
    int MENU_BG_COLOR;
    int MENU_CARD_BORDER;
    int MENU_CARD_COLOR;
    int MENU_FEATURE_BG_COLOR;
    LinearLayout MainsubLayout;
    String NumberTxtColor;
    int TEXT_COLOR;
    int TEXT_COLOR_2;
    int TEXT_COLOR_SECONDARY;
    GradientDrawable TabCheked;
    GradientDrawable TabunCheked;
    int ToggleTrackOFF;
    int ToggleTrackON;
    Context getContext;
    LinearLayout linearLayoutbt, mCollapse, mExpanded, mSettings, mlogin, mods, mods2, mods3, mods4, mods5, mods6, mods7, mods8, mods9, subLayout, v1, v2;
    RelativeLayout mCollapsed, mRootContainer;
    WindowManager mWindowManager;
    boolean overlayRequired, stopChecking;
    FrameLayout rootFrame;
    LinearLayout.LayoutParams scrlLL, scrlLLExpanded;
    ScrollView scrollView, scrollView2;
    SoundEffectPlayer soundEffectPlayer;
    ImageView startImage;
    TextView t1, t2, t3, t4, t5, t6, t7, t8, t9, t10;
    WindowManager.LayoutParams vmParams;

    int SeekBarColor = -1;
    int SeekBarProgressColor = -1;
    int RadioColor = -1;
    int ToggleThumbOFF = -1;
    int ToggleThumbON = -1;
    int MENU_WIDTH = 500;
    int MENU_HEIGHT = 365;
    int POS_X = 0;
    int POS_Y = 100;
    float MENU_CORNER = 24.0f;
    int ICON_SIZE = 48;
    float ICON_ALPHA = 1.0f;
    int ToggleON = -16711936;
    int ToggleOFF = -65536;
    float TAB_TEXT_SIZE = 12.0f;
    // ********************************************************************//

    // initialize methods from the native library
    native String EYE();

    native String EYECLOSE();

    native String[] GetFeatureList();

    native String[] GetFeatureList2();

    native String[] GetFeatureList3();

    native String[] GetFeatureList4();

    native String[] GetFeatureList5();

    native String[] GetFeatureList6();

    native String[] GetFeatureList7();

    native String[] GetFeatureList8();

    native String[] GetFeatureList9();

    native String Icon();

    native String IconWebViewData();

    native void Init(Context context, TextView textView, TextView textView2);

    native boolean IsGameLibLoaded();

    native String[] SetMloinList();

    native String[] SettingsList();

    native void getLinearLayout(ScrollView scrollView, LinearLayout linearLayout, LinearLayout linearLayout2, LinearLayout linearLayout3);

    // Here we write the code for our Menu
    public Menu(Context context) {
        ACCENT_GLOW = Color.parseColor("#1A007AFF");
        ACCENT_DARK = Color.parseColor("#003D80");
        MENU_BG_COLOR = Color.parseColor("#1C1C1E");
        MENU_CARD_COLOR = Color.parseColor("#2C2C2E");
        MENU_CARD_BORDER = Color.parseColor("#38383A");
        MENU_FEATURE_BG_COLOR = Color.argb(0, 0, 0, 0);
        TEXT_COLOR = Color.parseColor("#FFFFFF");
        TEXT_COLOR_2 = Color.parseColor("#FFFFFF");
        TEXT_COLOR_SECONDARY = Color.parseColor("#8E8E93");
        BTN_COLOR = Color.parseColor("#2C2C2E");
        BTN_BORDER = Color.parseColor("#007AFF");
        CategoryBG = Color.parseColor("#2C2C2E");
        CheckBoxColor = Color.parseColor("#FCFAF2");
        NumberTxtColor = "#34C759";
        ToggleTrackON = Color.parseColor("#34C759");
        ToggleTrackOFF = Color.argb(60, 120, 120, 128);
        instance = this;
        getContext = context;
        Preferences.context = context;

        rootFrame = new FrameLayout(context); // Global markup
        rootFrame.setOnTouchListener(onTouchListener());

        mRootContainer = new RelativeLayout(context); // Markup on which two markups of the icon and the menu itself will be placed
        mCollapsed = new RelativeLayout(context); // Markup of the icon (when the menu is minimized)
        mCollapsed.setVisibility(View.VISIBLE);
        mCollapsed.setAlpha(ICON_ALPHA);

        soundEffectPlayer = new SoundEffectPlayer(context);
        soundEffectPlayer.playSoundEffect("");

        // ********** The box of the mod menu **********
        mExpanded = new LinearLayout(context); // Menu markup (when the menu is expanded)
        mExpanded.setVisibility(View.GONE);
        mExpanded.setOrientation(LinearLayout.VERTICAL);
        mExpanded.setPadding(0, 0, 0, 0);
        mExpanded.setLayoutParams(new LinearLayout.LayoutParams(dp(MENU_WIDTH), dp(MENU_HEIGHT)));

        GradientDrawable gdMenu = new GradientDrawable();
        gdMenu.setCornerRadius(MENU_CORNER); // Set corner
        gdMenu.setColor(MENU_BG_COLOR); // Set background color
        gdMenu.setStroke(dp(1), MENU_CARD_BORDER); // Set border
        mExpanded.setBackground(gdMenu); // Apply GradientDrawable to it

        // ********** The icon to open mod menu **********
        startImage = new ImageView(context);
        startImage.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        int iconDim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_SIZE, context.getResources().getDisplayMetrics()); // Icon size
        startImage.getLayoutParams().height = iconDim;
        startImage.getLayoutParams().width = iconDim;
        // startimage.requestLayout();
        startImage.setScaleType(ImageView.ScaleType.FIT_XY);
        byte[] iconBytes = Base64.decode(Icon(), 0);
        startImage.setImageBitmap(BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.length));
        ((ViewGroup.MarginLayoutParams) startImage.getLayoutParams()).topMargin = convertDipToPixels(10);
        // Initialize event handlers for buttons, etc.
        startImage.setOnTouchListener(onTouchListener());
        startImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCollapsed.setVisibility(View.GONE);
                mExpanded.setVisibility(View.VISIBLE);
                mExpanded.setScaleX(0.6f);
                mExpanded.setScaleY(0.6f);
                mExpanded.setAlpha(0.0f);
                mExpanded.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .alpha(1.0f)
                        .setDuration(280)
                        .setInterpolator(new OvershootInterpolator(0.7f))
                        .start();
            }
        });

        // ********** The icon in Webview to open mod menu **********
        WebView wView = new WebView(context); // Icon size width="50" height="50"
        wView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        int webViewDim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ICON_SIZE, context.getResources().getDisplayMetrics()); // Icon size
        wView.getLayoutParams().height = webViewDim;
        wView.getLayoutParams().width = webViewDim;
        wView.loadData(
                "<html>" +
                        "<head></head>" +
                        "<body style=\"margin: 0; padding: 0\">" +
                        "<img src=\"" + IconWebViewData() + "\" width=\"" + ICON_SIZE + "\" height=\"" + ICON_SIZE + "\" >" +
                        "</body>" +
                        "</html>",
                "text/html",
                "utf-8"
        );
        wView.setBackgroundColor(0); // Transparent
        wView.setAlpha(ICON_ALPHA);
        wView.getSettings().setAppCacheEnabled(true);
        wView.setOnTouchListener(onTouchListener());

        // ********** Title & Subtitle Container **********
        LinearLayout titleBar = new LinearLayout(context);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(16);
        titleBar.setPadding(dp(16), dp(14), dp(16), dp(12));

        GradientDrawable titleBarBg = new GradientDrawable();
        titleBarBg.setColor(Color.parseColor("#2C2C2E"));
        titleBarBg.setCornerRadii(new float[]{dp(24), dp(24), dp(24), dp(24), 0, 0, 0, 0});
        titleBar.setBackground(titleBarBg);

        // ********** Settings Icons (Left & Right Gears) **********
        TextView settingsLeft = new TextView(context);
        settingsLeft.setText("⚙");
        settingsLeft.setTextColor(Color.parseColor("#8E8E93"));
        settingsLeft.setTextSize(20.0f);
        settingsLeft.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        settingsLeft.setPadding(dp(12), 0, dp(4), 0);
        settingsLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpanded.animate()
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .alpha(0.0f)
                        .setDuration(200)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mCollapsed.setVisibility(View.VISIBLE);
                                mCollapsed.setAlpha(0);
                                mExpanded.setVisibility(View.GONE);
                                mExpanded.setScaleX(1.0f);
                                mExpanded.setScaleY(1.0f);
                                mExpanded.setAlpha(1.0f);
                            }
                        })
                        .start();
                Toast.makeText(view.getContext(), "图标已隐藏，请记住原位置", Toast.LENGTH_LONG).show();
            }
        });

        // ********** Title **********
        TextView titleText = new TextView(context);
        titleText.setTextColor(-1);
        titleText.setTextSize(18.0f);
        titleText.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        titleText.setGravity(17);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));

        TextView settingsRight = new TextView(context);
        settingsRight.setText("⚙");
        settingsRight.setTextColor(Color.parseColor("#8E8E93"));
        settingsRight.setTextSize(20.0f);
        settingsRight.setPadding(dp(4), 0, dp(12), 0);
        settingsRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpanded.animate()
                        .scaleX(0.8f)
                        .scaleY(0.8f)
                        .alpha(0.0f)
                        .setDuration(200)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mCollapsed.setVisibility(View.VISIBLE);
                                mCollapsed.setAlpha(ICON_ALPHA);
                                mExpanded.setVisibility(View.GONE);
                                mExpanded.setScaleX(1.0f);
                                mExpanded.setScaleY(1.0f);
                                mExpanded.setAlpha(1.0f);
                            }
                        })
                        .start();
            }
        });

        titleBar.addView(settingsLeft);
        titleBar.addView(titleText);
        titleBar.addView(settingsRight);

        // ********** Sub title **********
        TextView subTitleText = new TextView(context);
        subTitleText.setTextColor(Color.parseColor("#8E8E93"));
        subTitleText.setTextSize(18.0f);
        subTitleText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        subTitleText.setGravity(17);
        subTitleText.setPadding(0, dp(2), 0, 0);

        mlogin = new LinearLayout(context);
        mlogin.setOrientation(LinearLayout.VERTICAL);
        featureList(SetMloinList(), mlogin);

        // ********** Settings **********
        mSettings = new LinearLayout(context);
        mSettings.setOrientation(LinearLayout.VERTICAL);
        featureList(SettingsList(), mSettings);

        mods = new LinearLayout(context);
        mods.setOrientation(LinearLayout.VERTICAL);

        mods2 = new LinearLayout(context);
        mods2.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList2(), mods2);

        mods3 = new LinearLayout(context);
        mods3.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList3(), mods3);

        mods4 = new LinearLayout(context);
        mods4.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList4(), mods4);

        mods5 = new LinearLayout(context);
        mods5.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList5(), mods5);

        mods6 = new LinearLayout(context);
        mods6.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList6(), mods6);

        mods7 = new LinearLayout(context);
        mods7.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList7(), mods7);

        mods8 = new LinearLayout(context);
        mods8.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList8(), mods8);

        mods9 = new LinearLayout(context);
        mods9.setOrientation(LinearLayout.VERTICAL);
        featureList(GetFeatureList9(), mods9);

        // ********** Mod menu feature list **********
        final int[] scrollPositions = new int[10];
        final int[] currentTab = {0};
        final LinearLayout[] modLayouts = {mods, mods2, mods3, mods4, mods5, mods6, mods7, mods8, mods9, mSettings};

        LinearLayout tabLayoutContainer = new LinearLayout(context);
        tabLayoutContainer.setPadding(dp(8), dp(10), dp(8), dp(6));
        tabLayoutContainer.setGravity(17);

        final HorizontalScrollView segmentScroll = new HorizontalScrollView(context);
        segmentScroll.setHorizontalScrollBarEnabled(false);

        final LinearLayout segmentBar = new LinearLayout(context);
        segmentBar.setOrientation(LinearLayout.HORIZONTAL);
        segmentBar.setPadding(dp(3), dp(3), dp(3), dp(3));

        GradientDrawable segmentBarBg = new GradientDrawable();
        segmentBarBg.setColor(Color.parseColor("#2C2C2E"));
        segmentBarBg.setCornerRadius(dp(20));
        segmentBar.setBackground(segmentBarBg);

        final GradientDrawable segmentSelected = new GradientDrawable();
        segmentSelected.setColor(Color.parseColor("#636366"));
        segmentSelected.setCornerRadius(dp(18));

        String[] tabTitles = {"实用", "传送", "跑图", "人物", "娱乐", "魔法", "衣柜", "环境", "彩蛋", "日志"};
        final TextView[] segItems = new TextView[10];

        for (int i = 0; i < 10; i++) {
            final int idx = i;
            TextView tabView = new TextView(context);
            segItems[idx] = tabView;
            tabView.setText(tabTitles[idx]);
            tabView.setGravity(17);
            tabView.setTextSize(11.0f);
            tabView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            tabView.setPadding(dp(10), dp(6), dp(10), dp(6));
            tabView.setMinWidth(dp(38));

            if (idx == 0) {
                tabView.setBackground(segmentSelected);
                tabView.setTextColor(-1);
            } else {
                tabView.setBackground(null);
                tabView.setTextColor(Color.parseColor("#8E8E93"));
            }

            tabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Menu.isLoggedIn) {
                        for (int k = 0; k < 10; k++) {
                            if (segItems[k].getBackground() != null) {
                                scrollPositions[k] = scrollView.getScrollY();
                                break;
                            }
                        }

                        scrollView.removeAllViews();
                        scrollView.addView(modLayouts[idx]);

                        scrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.scrollTo(0, scrollPositions[idx]);
                            }
                        });

                        for (int k = 0; k < 10; k++) {
                            segItems[k].setBackground(null);
                            segItems[k].setTextColor(Color.parseColor("#8E8E93"));
                        }
                        segItems[idx].setBackground(segmentSelected);
                        segItems[idx].setTextColor(-1);
                        currentTab[0] = idx;

                        int tabWidth = segItems[0].getWidth();
                        segmentScroll.scrollTo(Math.max(0, ((idx * tabWidth) - (segmentBar.getWidth() / 2)) + (tabWidth / 2)), 0);
                    }
                }
            });

            segmentBar.addView(tabView);
        }

        segmentScroll.addView(segmentBar);
        tabLayoutContainer.addView(segmentScroll);

        scrollView = new ScrollView(context);
        // Auto size. To set size manually, change the width and height example 500, 500
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));

        GradientDrawable scrlBg = new GradientDrawable();
        scrlBg.setColor(MENU_BG_COLOR);
        float fZero = 0.0f;
        scrlBg.setCornerRadii(new float[]{fZero, fZero, fZero, fZero, dp(24), dp(24), dp(24), dp(24)});
        scrollView.setBackground(scrlBg);
        scrollView.setPadding(dp(6), dp(4), dp(6), dp(4));
        scrollView.setClipToPadding(false);

        scrlLL = new LinearLayout.LayoutParams(-1, dp(MENU_HEIGHT));
        scrlLLExpanded = new LinearLayout.LayoutParams(mExpanded.getLayoutParams());
        scrlLLExpanded.weight = 1.0f;

        // ********** Adding view components **********
        // ********** Adding view components **********
        mRootContainer.addView(mCollapsed);
        mRootContainer.addView(mExpanded);

        if (IconWebViewData() != null) {
            mCollapsed.addView(wView);
        } else {
            mCollapsed.addView(startImage);
        }

        mExpanded.addView(titleBar);
        mExpanded.addView(tabLayoutContainer);
        scrollView.addView(mlogin);
        mExpanded.addView(scrollView);

        Init(context, titleText, subTitleText);
        getLinearLayout(scrollView, mods, mSettings, mlogin);
    }

    private GradientDrawable glassCard() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(MENU_CARD_COLOR);
        gradientDrawable.setCornerRadius(dp(22.0f));
        gradientDrawable.setStroke(dp(1), MENU_CARD_BORDER);
        return gradientDrawable;
    }

    private GradientDrawable glassCard(float f) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(MENU_CARD_COLOR);
        gradientDrawable.setCornerRadius(dp(f));
        gradientDrawable.setStroke(dp(1), MENU_CARD_BORDER);
        return gradientDrawable;
    }

    private GradientDrawable accentFill() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(BTN_COLOR);
        gradientDrawable.setCornerRadius(dp(22.0f));
        gradientDrawable.setStroke(dp(1), ACCENT_COLOR);
        return gradientDrawable;
    }

    private GradientDrawable accentFill(float f) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(ACCENT_COLOR);
        gradientDrawable.setCornerRadius(dp(f));
        return gradientDrawable;
    }

    private GradientDrawable darkFill() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(BTN_COLOR);
        gradientDrawable.setCornerRadius(dp(22.0f));
        return gradientDrawable;
    }

    private GradientDrawable darkFillBorder() {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(BTN_COLOR);
        gradientDrawable.setCornerRadius(dp(22.0f));
        gradientDrawable.setStroke(dp(1), ACCENT_COLOR);
        return gradientDrawable;
    }

    private GradientDrawable accentStroke(float f) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(0);
        gradientDrawable.setCornerRadius(dp(f));
        gradientDrawable.setStroke(dp(1), ACCENT_COLOR);
        return gradientDrawable;
    }

    private GradientDrawable createDrawable(int color, float radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(dp(radius));
        return gd;
    }

    private GradientDrawable createDrawable(int color, float radius1, float radius2, float radius3, float radius4) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadii(new float[]{dp(radius1), dp(radius1), dp(radius2), dp(radius2), dp(radius3), dp(radius3), dp(radius4), dp(radius4)});
        return gd;
    }

    private GradientDrawable createOutlinedDrawable(int color, float radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0);
        gd.setStroke(dp(1), color);
        gd.setCornerRadius(dp(radius));
        return gd;
    }

    private void AddColor(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(i);
        gradientDrawable.setCornerRadii(new float[]{i6, i7, i8, i9, i10, i11, i12, i13});
        gradientDrawable.setStroke(i2, i5, i3, i4);
        view.setBackgroundDrawable(gradientDrawable);
    }

    private boolean isViewCollapsed() {
        return rootFrame == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    // For our image a little converter
    private int convertDipToPixels(int i) {
        return (int) ((i * getContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, getContext.getResources().getDisplayMetrics());
    }

    private int dp(float f) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f, getContext.getResources().getDisplayMetrics());
    }

    public void setVisibility(int i) {
        if (rootFrame != null) {
            rootFrame.setVisibility(i);
        }
    }

    public void onDestroy() {
        if (rootFrame != null) {
            mWindowManager.removeView(rootFrame);
        }
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX;
            private float initialTouchY;
            private int initialX;
            private int initialY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = vmParams.x;
                        initialY = vmParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                        int rawY = (int) (motionEvent.getRawY() - initialTouchY);
                        mExpanded.setAlpha(1.0f);
                        mCollapsed.setAlpha(1.0f);
                        // The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking. So that is click event.
                        // When user clicks on the image view of the collapsed layout, visibility of the collapsed layout will be changed to "View.GONE" and expanded view will become visible.
                        if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                            try {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                                expandedView.setScaleX(0.6f);
                                expandedView.setScaleY(0.6f);
                                expandedView.setAlpha(0.0f);
                                expandedView.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .alpha(1.0f)
                                        .setDuration(280)
                                        .setInterpolator(new OvershootInterpolator(0.7f))
                                        .start();
                            } catch (NullPointerException e) {
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mExpanded.setAlpha(0.5f);
                        mCollapsed.setAlpha(0.5f);
                        // Calculate the X and Y coordinates of the view.
                        vmParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        vmParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                        // Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(rootFrame, vmParams);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void featureList(String[] featuresList, LinearLayout parentLayout) {
        // Currently looks messy right now. Let me know if you have improvements
        LinearLayout checkBoxRow = new LinearLayout(getContext);
        checkBoxRow.setOrientation(LinearLayout.HORIZONTAL);
        int subFeat = 0;
        int i = 0;
        int checkBoxCount = 0;
        LinearLayout currentLayout = parentLayout;

        while (i < featuresList.length) {
            String featureStr = featuresList[i];
            boolean isKCheckBox = false;
            if (featureStr.contains("_KCheckBox")) {
                featureStr = featureStr.replaceFirst("_KCheckBox", "");
                isKCheckBox = true;
            }

            boolean isTrue = false;
            if (featureStr.contains("_True")) {
                featureStr = featureStr.replaceFirst("_True", "");
                isTrue = true;
            }

            boolean isTrueLower = false;
            if (featureStr.contains("_true")) {
                featureStr = featureStr.replaceFirst("_true", "");
                isTrueLower = true;
            }

            if (featureStr.contains("CollapseAdd_")) {
                currentLayout = mCollapse;
                featureStr = featureStr.replaceFirst("CollapseAdd_", "");
            } else {
                currentLayout = parentLayout;
            }

            String[] firstSplit = featureStr.split("_");
            int featNum;
            // Assign feature number
            if (TextUtils.isDigitsOnly(firstSplit[0]) || firstSplit[0].matches("-[0-9]*")) {
                featNum = Integer.parseInt(firstSplit[0]);
                featureStr = featureStr.replaceFirst(firstSplit[0] + "_", "");
                subFeat++;
            } else {
                // Subtract feature number. We don't want to count ButtonLink, Category, RichTextView and RichWebView
                featNum = i - subFeat;
            }

            String[] featureParts = featureStr.split("_");
            String featureName = featureParts[0];
            if (featureName.equals("Toggle")) {
                if (featureParts.length == 2) {
                    Switch(currentLayout, featNum, featureParts[1], isTrueLower, 16384, 16384, 16384, 16384, 16384, 16384, 16384, 16384, 16384, isTrue);
                } else if (featureParts.length == 3) {
                    Switch(currentLayout, featNum, featureParts[1], isTrueLower, Integer.parseInt(featureParts[2]), 16384, 16384, 16384, 16384, 16384, 16384, 16384, 16384, isTrue);
                } else if (featureParts.length == 4) {
                    Switch(currentLayout, featNum, featureParts[1], isTrueLower, Integer.parseInt(featureParts[2]), Integer.parseInt(featureParts[3]), 16384, 16384, 16384, 16384, 16384, 16384, 16384, isTrue);
                } else if (featureParts.length == 5) {
                    Switch(currentLayout, featNum, featureParts[1], isTrueLower, Integer.parseInt(featureParts[2]), Integer.parseInt(featureParts[3]), Integer.parseInt(featureParts[4]), 16384, 16384, 16384, 16384, 16384, 16384, isTrue);
                } else if (featureParts.length == 6) {
                    Switch(currentLayout, featNum, featureParts[1], isTrueLower, Integer.parseInt(featureParts[2]), Integer.parseInt(featureParts[3]), Integer.parseInt(featureParts[4]), Integer.parseInt(featureParts[5]), 16384, 16384, 16384, 16384, 16384, isTrue);
                }
            } else if (featureName.equals("SeekBar")) {
                SeekBar(currentLayout, featNum, featureParts[1], Integer.parseInt(featureParts[2]), Integer.parseInt(featureParts[3]));
            } else if (featureName.equals("Button")) {
                Button(currentLayout, featNum, featureParts[1]);
            } else if (featureName.equals("ButtonOnOff")) {
                ButtonOnOff(currentLayout, featNum, featureParts[1], isTrue);
            } else if (featureName.equals("Spinner")) {
                TextView(currentLayout, featureParts[1]);
                Spinner(currentLayout, featNum, featureParts[1], featureParts[2]);
            } else if (featureName.equals("InputText")) {
                InputText(currentLayout, featNum, featureParts[1]);
            } else if (featureName.equals("InputValue")) {
                if (featureParts.length == 3) {
                    InputNum(currentLayout, featNum, featureParts[2], Integer.parseInt(featureParts[1]));
                } else if (featureParts.length == 2) {
                    InputNum(currentLayout, featNum, featureParts[1], 0);
                }
            } else if (!featureName.equals("CheckBox")) {
                if (featureName.equals("ArrayBox")) {
                    currentLayout.addView(ArrayBox(featNum, featureParts[1], isTrue));
                } else if (featureName.equals("RadioButton")) {
                    RadioButton(currentLayout, featNum, featureParts[1], featureParts[2]);
                } else if (featureName.equals("Collapse")) {
                    Collapse(currentLayout, featureParts[1], isTrue);
                    subFeat++;
                } else if (featureName.equals("ButtonLink")) {
                    ButtonLink(currentLayout, featureParts[1], featureParts[2]);
                    subFeat++;
                } else if (featureName.equals("Category")) {
                    Category(currentLayout, featureParts[1]);
                    subFeat++;
                } else if (featureName.equals("RichTextView")) {
                    TextView(currentLayout, featureParts[1]);
                    subFeat++;
                } else if (featureName.equals("RichWebView")) {
                    WebTextView(currentLayout, featureParts[1]);
                    subFeat++;
                }
            } else if (isKCheckBox) {
                int limit = Math.min(Integer.parseInt(featureStr.substring(featureStr.lastIndexOf("_") + 1)), 3);
                CheckBox(checkBoxRow, featNum, featureParts[1], isTrue);
                if (++checkBoxCount == limit) {
                    currentLayout.addView(checkBoxRow);
                    checkBoxRow = new LinearLayout(getContext);
                    checkBoxRow.setOrientation(LinearLayout.HORIZONTAL);
                    checkBoxCount = 0;
                }
            } else {
                CheckBox(currentLayout, featNum, featureParts[1], isKCheckBox);
            }

            i++;
        }

        if (checkBoxCount > 0) {
            currentLayout.addView(checkBoxRow);
        }

        View spacer = new View(getContext);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(40)));
        currentLayout.addView(spacer);
    }

    private void Switch(final LinearLayout linLayout, final int featNum, final String featName, final boolean isTrue, final int featNum2, final int featNum3, final int featNum4, final int featNum5, int featNum6, int featNum7, int featNum8, int featNum9, int featNum10, boolean switchedOn) {
        final Switch switchR = new Switch(getContext);
        final GradientDrawable gdTrack = new GradientDrawable();
        gdTrack.setSize(25, 25);
        gdTrack.setCornerRadius(100);

        final GradientDrawable gdThumb = new GradientDrawable();
        gdThumb.setSize(40, 40);
        gdThumb.setShape(GradientDrawable.OVAL);

        if (Preferences.loadPrefBool(featName, featNum, switchedOn)) {
            gdTrack.setColor(ToggleTrackON);
            gdTrack.setStroke(dp(1), ToggleThumbOFF);
            gdThumb.setColor(ToggleThumbON);
            gdThumb.setStroke(dp(1), ToggleTrackOFF);
            switchR.setTrackDrawable(gdTrack);
            switchR.setThumbDrawable(gdThumb);
        } else {
            gdTrack.setColor(ToggleTrackOFF);
            gdTrack.setStroke(dp(1), ToggleThumbON);
            gdThumb.setColor(ToggleThumbOFF);
            gdThumb.setStroke(dp(1), ToggleTrackON);
        }

        switchR.setText(featName);
        switchR.setTextColor(TEXT_COLOR_2);
        switchR.setTextSize(12.0f);
        switchR.setShadowLayer(4.0f, 0.0f, 0.0f, Color.argb(40, 0, 0, 0));
        switchR.setPadding(dp(12), dp(5), dp(12), dp(5));
        switchR.setThumbDrawable(gdThumb);
        switchR.setTrackDrawable(gdTrack);
        switchR.setChecked(Preferences.loadPrefBool(featName, featNum, switchedOn));
        // Set colors of the switch. Comment out if you don't like it
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Preferences.changeFeatureBool(featName, featNum, isChecked);
                if (isChecked) {
                    gdTrack.setColor(ToggleTrackON);
                    gdTrack.setStroke(dp(1), ToggleThumbOFF);
                    gdThumb.setColor(ToggleThumbON);
                    gdThumb.setStroke(dp(1), ToggleTrackOFF);
                    switchR.setTrackDrawable(gdTrack);
                    switchR.setThumbDrawable(gdThumb);
                } else {
                    gdTrack.setColor(ToggleTrackOFF);
                    gdTrack.setStroke(dp(1), ToggleThumbON);
                    gdThumb.setColor(ToggleThumbOFF);
                    gdThumb.setStroke(dp(1), ToggleTrackON);
                    switchR.setTrackDrawable(gdTrack);
                    switchR.setThumbDrawable(gdThumb);
                }

                if (isChecked) {
                    if (featNum2 != 16384 && featNum3 == 16384) {
                        setSwitchState(linLayout, featNum2, false);
                    }
                    if (featNum2 != 16384 && featNum3 != 16384) {
                        setSwitchState(linLayout, featNum2, false);
                        setSwitchState(linLayout, featNum3, false);
                    }
                    if (featNum2 != 16384 && featNum3 != 16384 && featNum4 != 16384) {
                        setSwitchState(linLayout, featNum2, false);
                        setSwitchState(linLayout, featNum3, false);
                        setSwitchState(linLayout, featNum4, false);
                    }
                    if (featNum2 != 16384 && featNum3 != 16384 && featNum4 != 16384 && featNum5 != 16384) {
                        setSwitchState(linLayout, featNum2, false);
                        setSwitchState(linLayout, featNum3, false);
                        setSwitchState(linLayout, featNum4, false);
                        setSwitchState(linLayout, featNum5, false);
                    }
                }

                if (isChecked) {
                    if (featNum2 != 16384 && featNum3 == 16384 && isTrue) {
                        setSwitchState(linLayout, featNum2, true);
                    }
                    if (featNum2 != 16384 && featNum3 != 16384 && isTrue) {
                        setSwitchState(linLayout, featNum2, true);
                        setSwitchState(linLayout, featNum3, true);
                    }
                    if (featNum2 != 16384 && featNum3 != 16384 && featNum4 != 16384 && isTrue) {
                        setSwitchState(linLayout, featNum2, true);
                        setSwitchState(linLayout, featNum3, true);
                        setSwitchState(linLayout, featNum4, true);
                    }
                    if (featNum2 != 16384 && featNum3 != 16384 && featNum4 != 16384 && featNum5 != 16384 && isTrue) {
                        setSwitchState(linLayout, featNum2, true);
                        setSwitchState(linLayout, featNum3, true);
                        setSwitchState(linLayout, featNum4, true);
                        setSwitchState(linLayout, featNum5, true);
                    }
                }

                Preferences.changeFeatureBool(featName, featNum, isChecked);

                switch (featNum) {
                    case -3:
                        Preferences.isExpanded = isChecked;
                        scrollView.setLayoutParams(isChecked ? scrlLLExpanded : scrlLL);
                        break;
                    case -1: // Save perferences
                        Preferences.with(switchR.getContext()).writeBoolean(-1, isChecked);
                        if (!isChecked) {
                            Preferences.with(switchR.getContext()).clear(); // Clear perferences if switched off
                        }
                        break;
                }
            }
        });
        switchR.setTag(new Integer(featNum));
        linLayout.addView(switchR);
    }

    public static void ReloadFeatures() {
       Menu instance = Menu.instance;
        if (instance != null) {
            instance.reloadFeaturesInstance();
        }
    }

    private void SeekBar(LinearLayout linLayout, final int featNum, final String featName, final int min, int max) {
        int loadedProgress = Preferences.loadPrefInt(featName, featNum);
        LinearLayout seekBarLayout = new LinearLayout(getContext);
        seekBarLayout.setPadding(dp(12), dp(6), dp(12), dp(6));
        seekBarLayout.setOrientation(LinearLayout.VERTICAL);
        seekBarLayout.setGravity(17);

        final TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + (loadedProgress == 0 ? min : loadedProgress) + "</font>"));
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTextSize(11.0f);

        SeekBar seekBar = new SeekBar(getContext);
        seekBar.setPadding(dp(20), dp(6), dp(20), dp(6));
        seekBar.setMax(max);
        if (Build.VERSION.SDK_INT >= 26) {
            seekBar.setMin(min); // setMin for Oreo and above
        }
        seekBar.setProgress(loadedProgress == 0 ? min : loadedProgress);
        seekBar.getThumb().setColorFilter(SeekBarColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(SeekBarProgressColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar2) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar2) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar2, int progress, boolean z) {
                // if progress is greater than minimum, don't go below. Else, set progress
                int finalProgress = progress;
                if (finalProgress < min) {
                    finalProgress = min;
                }
                seekBar2.setProgress(finalProgress);
                Preferences.changeFeatureInt(featName, featNum, finalProgress);
                textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + finalProgress + "</font>"));
            }
        });

        seekBarLayout.addView(textView);
        seekBarLayout.addView(seekBar);
        linLayout.addView(seekBarLayout);
    }

    private void Button(LinearLayout linLayout, final int featNum, final String featName) {
        Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(dp(8), dp(5), dp(8), dp(5));
        button.setLayoutParams(layoutParams);
        button.setTextColor(ACCENT_COLOR);
        button.setAllCaps(false); // Disable caps to support html
        button.setText(Html.fromHtml(featName));
        button.setTextSize(12.0f);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackground(darkFillBorder());
        button.setPadding(dp(8), dp(0), dp(8), dp(0));
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (featNum) {
                    case -100:
                        stopChecking = true;
                        break;
                    case -6:
                        scrollView.removeView(mSettings);
                        scrollView.addView(mods);
                        break;
                }
                Preferences.changeFeatureInt(featName, featNum, 0);
            }
        });
        linLayout.addView(button);
    }

    private void ButtonLink(LinearLayout linLayout, String featName, final String url) {
        Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(dp(8), dp(5), dp(8), dp(5));
        button.setLayoutParams(layoutParams);
        button.setAllCaps(false); // Disable caps to support html
        button.setTextColor(ACCENT_COLOR);
        button.setText(Html.fromHtml(featName));
        button.setTextSize(12.0f);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackground(darkFillBorder());
        button.setPadding(dp(8), dp(10), dp(8), dp(10));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(url));
                getContext.startActivity(intent);
            }
        });
        linLayout.addView(button);
    }

    private void ButtonOnOff(LinearLayout linLayout, final int featNum, String featName, boolean switchedOn) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(dp(8), dp(5), dp(8), dp(5));
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); // Disable caps to support html
        button.setTextSize(12.0f);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setPadding(dp(8), dp(5), dp(8), dp(5));

        final String featureName = featName.replace("OnOff_", "");
        final boolean[] isOn = {false};

        if (Preferences.loadPrefBool(featName, featNum, switchedOn)) {
            button.setText(Html.fromHtml(featureName + ": 开启"));
            button.setTextColor(-1);
            button.setBackground(accentFill());
            isOn[0] = false;
        } else {
            button.setText(Html.fromHtml(featureName + ": 关闭"));
            button.setTextColor(ACCENT_COLOR);
            button.setBackground(darkFillBorder());
            isOn[0] = true;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferences.changeFeatureBool(featureName, featNum, isOn[0]);
                if (isOn[0]) {
                    button.setText(Html.fromHtml(featureName + ": 开启"));
                    button.setTextColor(-1);
                    button.setBackground(accentFill());
                    isOn[0] = false;
                } else {
                    button.setText(Html.fromHtml(featureName + ": 关闭"));
                    button.setTextColor(ACCENT_COLOR);
                    button.setBackground(darkFillBorder());
                    isOn[0] = true;
                }
            }
        });

        linLayout.addView(button);
    }

    private void Spinner(LinearLayout linLayout, final int featNum, String featName, String list) {
        final List<String> itemsList = new LinkedList<>(Arrays.asList(list.split(",")));
        // Create another LinearLayout as a workaround to use it as a background to keep the down arrow symbol. No arrow symbol if setBackgroundColor set
        LinearLayout spinnerLayout = new LinearLayout(getContext);
        spinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
        spinnerLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        spinnerLayout.setPadding(dp(10), 0, dp(10), 0);

        Button btnLeft = new Button(getContext);
        btnLeft.setText("◀");
        btnLeft.setBackground(accentStroke(10.0f));
        btnLeft.setTextColor(ACCENT_COLOR);
        btnLeft.setTextSize(10.0f);
        btnLeft.setLayoutParams(new LinearLayout.LayoutParams(dp(44), -2));
        ((LinearLayout.LayoutParams) btnLeft.getLayoutParams()).setMargins(dp(4), 0, dp(2), dp(5));

        RelativeLayout spinnerContainer = new RelativeLayout(getContext);
        spinnerContainer.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        spinnerContainer.setBackground(glassCard(10.0f));

        final Spinner spinner = new Spinner(getContext, Spinner.MODE_DROPDOWN);
        spinner.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
        spinner.getBackground().setColorFilter(new PorterDuffColorFilter(0, PorterDuff.Mode.CLEAR)); // trick to show white down arrow color
        // Creating the ArrayAdapter instance having the list
        spinner.setAdapter((SpinnerAdapter) new ArrayAdapter<String>(getContext, android.R.layout.simple_spinner_dropdown_item, itemsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.parseColor("#34C759"));
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View dropDownView = super.getDropDownView(position, convertView, parent);
                ((TextView) dropDownView).setTextColor(Color.parseColor("#34C759"));
                return dropDownView;
            }
        });
        // Setting the ArrayAdapter data on the Spinner'
        spinner.setSelection(Preferences.loadPrefInt(featName, featNum));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Preferences.changeFeatureInt(spinner.getSelectedItem().toString(), featNum, position);
            }
        });
        spinnerContainer.addView(spinner);

        Button btnRight = new Button(getContext);
        btnRight.setText("▶");
        btnRight.setBackground(accentStroke(10.0f));
        btnRight.setTextColor(ACCENT_COLOR);
        btnRight.setTextSize(10.0f);
        btnRight.setLayoutParams(new LinearLayout.LayoutParams(dp(44), -2));
        ((LinearLayout.LayoutParams) btnRight.getLayoutParams()).setMargins(dp(2), 0, dp(4), dp(5));

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = spinner.getSelectedItemPosition();
                if (pos > 0) {
                    spinner.setSelection(pos - 1);
                }
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = spinner.getSelectedItemPosition();
                if (pos < itemsList.size() - 1) {
                    spinner.setSelection(pos + 1);
                }
            }
        });

        spinnerLayout.addView(btnLeft);
        spinnerLayout.addView(spinnerContainer);
        spinnerLayout.addView(btnRight);
        linLayout.addView(spinnerLayout);
    }

    private void InputNum(LinearLayout linLayout, final int featNum, final String featName, final int maxValue) {
        final EditTextNum editTextNum = new EditTextNum();
        LinearLayout inputLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(dp(12), dp(8), dp(12), dp(8));
        inputLayout.setLayoutParams(layoutParams);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setPadding(dp(12), dp(10), dp(10), dp(10));
        inputLayout.setGravity(16);
        inputLayout.setBackground(glassCard(16.0f));

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams2.weight = 1.0f;
        LinearLayout textContainer = new LinearLayout(getContext);
        textContainer.setLayoutParams(layoutParams2);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(17);

        TextView labelView = new TextView(getContext);
        labelView.setText(featName);
        labelView.setTextColor(TEXT_COLOR_2);
        labelView.setGravity(3);
        labelView.setTextSize(12.0f);
        labelView.setPadding(dp(4), 0, 0, 0);
        labelView.setSingleLine(true);

        final TextView valueView = new TextView(getContext);
        int val = Preferences.loadPrefInt(featName, featNum);
        valueView.setText(Html.fromHtml("<font color='" + NumberTxtColor + "'>" + val + "</font>"));
        valueView.setTextColor(TEXT_COLOR_2);
        valueView.setGravity(3);
        valueView.setTextSize(12.0f);
        valueView.setPadding(dp(4), 0, 0, 0);

        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(dp(22), dp(22));
        layoutParams3.gravity = 16;
        layoutParams3.setMargins(0, 0, dp(8), 0);

        final TextView eyeView = new TextView(getContext);
        eyeView.setLayoutParams(layoutParams3);

        byte[] bArrDecode = Base64.decode(EYE(), 0);
        final BitmapDrawable eyeDrawable = new BitmapDrawable(BitmapFactory.decodeByteArray(bArrDecode, 0, bArrDecode.length));
        eyeView.setBackground(eyeDrawable);

        byte[] bArrDecode2 = Base64.decode(EYECLOSE(), 0);
        final BitmapDrawable eyeCloseDrawable = new BitmapDrawable(BitmapFactory.decodeByteArray(bArrDecode2, 0, bArrDecode2.length));

        eyeView.setOnClickListener(new View.OnClickListener() {
            boolean settingsOpen = false;

            @Override
            public void onClick(View view) {
                try {
                    settingsOpen = !settingsOpen;
                    if (settingsOpen) {
                        eyeView.setBackground(eyeCloseDrawable);
                        valueView.setTransformationMethod(new PasswordTransformationMethod() {
                            @Override
                            public CharSequence getTransformation(CharSequence charSequence, View view2) {
                                return "●●●";
                            }
                        });
                    } else {
                        eyeView.setBackground(eyeDrawable);
                        valueView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                } catch (IllegalStateException e) {
                }
            }
        });

        Button btnSet = new Button(getContext);
        btnSet.setText("设置");
        btnSet.setAllCaps(false);
        btnSet.setTextSize(11.0f);
        btnSet.setTypeface(Typeface.DEFAULT_BOLD);
        btnSet.setTextColor(ACCENT_COLOR);
        btnSet.setBackground(accentStroke(14.0f));
        btnSet.setPadding(dp(12), dp(8), dp(12), dp(8));
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog inputDialog = new AlertDialog.Builder(getContext, 2).create(); // display the dialog
                Objects.requireNonNull(inputDialog.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                inputDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        ((InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(1, 0);
                    }
                });

                LinearLayout dialogLayout = new LinearLayout(getContext);
                dialogLayout.setPadding(dp(16), dp(16), dp(16), dp(16));
                dialogLayout.setOrientation(LinearLayout.VERTICAL);

                GradientDrawable dialogBg = new GradientDrawable();
                dialogBg.setColor(MENU_BG_COLOR);
                dialogBg.setStroke(dp(1), ACCENT_COLOR);
                dialogLayout.setBackground(dialogBg);

                TextView titleView = new TextView(getContext);
                titleView.setText(Html.fromHtml("<b>" + featName + "</b>"));
                titleView.setGravity(17);
                titleView.setTypeface(Typeface.DEFAULT_BOLD);
                titleView.setTextColor(TEXT_COLOR);
                titleView.setTextSize(18.0f);
                titleView.setPadding(0, 0, 0, dp(8));

                TextView subTitleView = new TextView(getContext);
                subTitleView.setGravity(17);
                subTitleView.setTextSize(12.0f);
                subTitleView.setPadding(0, 0, 0, dp(8));
                subTitleView.setTextColor(TEXT_COLOR_SECONDARY);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
                lp.weight = 1;
                final EditText inputField = new EditText(getContext);
                inputField.setLayoutParams(lp);
                inputField.setMaxLines(1);
                inputField.setHint("输入数值");
                inputField.setHintTextColor(TEXT_COLOR_SECONDARY);
                inputField.setWidth(convertDipToPixels(280));
                inputField.setTextColor(TEXT_COLOR);
                inputField.setInputType(2);
                inputField.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));

                GradientDrawable inputBg = new GradientDrawable();
                inputBg.setColor(MENU_CARD_COLOR);
                inputBg.setStroke(dp(1), MENU_CARD_BORDER);
                inputField.setBackground(inputBg);
                inputField.setPadding(dp(14), dp(12), dp(14), dp(12));
                inputField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                inputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view2, boolean z) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (z) {
                            imm.toggleSoftInput(2, 1);
                        } else {
                            imm.toggleSoftInput(1, 0);
                        }
                    }
                });

                LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(-1, -2);
                lpButton.setMargins(0, dp(14), 0, 0);
                Button btnConfirm = new Button(getContext);

                GradientDrawable btnBg = new GradientDrawable();
                btnBg.setColor(BTN_COLOR);
                btnBg.setStroke(dp(1), ACCENT_COLOR);
                btnConfirm.setBackground(btnBg);
                btnConfirm.setLayoutParams(lpButton);
                btnConfirm.setTextColor(-1);
                btnConfirm.setPadding(dp(14), dp(12), dp(14), dp(12));
                btnConfirm.setText("确定");
                btnConfirm.setTextSize(14.0f);
                btnConfirm.setTypeface(Typeface.DEFAULT_BOLD);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        int finalVal;
                        try {
                            finalVal = Integer.parseInt(TextUtils.isEmpty(inputField.getText().toString()) ? "0" : inputField.getText().toString());
                            if (maxValue != 0 && finalVal >= maxValue) {
                                finalVal = maxValue;
                            }
                        } catch (NumberFormatException e) {
                            finalVal = 2147483640;
                        }
                        editTextNum.setNum(finalVal);
                        valueView.setText(Html.fromHtml("<font color='" + NumberTxtColor + "'>" + finalVal + "</font>"));
                        inputDialog.dismiss();
                        Preferences.changeFeatureInt(featName, featNum, finalVal);
                        inputField.setFocusable(false);
                    }
                });

                dialogLayout.addView(titleView);
                dialogLayout.addView(subTitleView);
                dialogLayout.addView(inputField); // displays the user input bar
                dialogLayout.addView(btnConfirm);

                inputDialog.setView(dialogLayout);
                inputDialog.show();
                inputField.requestFocus();
            }
        });

        textContainer.addView(labelView);
        textContainer.addView(valueView);
        inputLayout.addView(textContainer);
        inputLayout.addView(eyeView);
        inputLayout.addView(btnSet);
        linLayout.addView(inputLayout);
    }

    private void InputText(LinearLayout linLayout, final int featNum, final String featName) {
        final EditTextString editTextString = new EditTextString();
        LinearLayout inputLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(dp(12), dp(8), dp(12), dp(8));
        inputLayout.setLayoutParams(layoutParams);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setPadding(dp(12), dp(10), dp(10), dp(10));
        inputLayout.setGravity(16);
        inputLayout.setBackground(glassCard(16.0f));

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams2.weight = 1.0f;
        LinearLayout textContainer = new LinearLayout(getContext);
        textContainer.setLayoutParams(layoutParams2);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(17);

        TextView labelView = new TextView(getContext);
        labelView.setText(featName);
        labelView.setTextColor(TEXT_COLOR_2);
        labelView.setGravity(3);
        labelView.setTextSize(12.0f);
        labelView.setPadding(dp(4), 0, 0, 0);
        labelView.setSingleLine(true);

        final TextView valueView = new TextView(getContext);
        String prefStr = Preferences.loadPrefString(featName, featNum);
        editTextString.setString(prefStr != "" ? prefStr : "");
        valueView.setText(Html.fromHtml("<font color='" + NumberTxtColor + "'>" + prefStr + "</font>"));
        valueView.setTextColor(TEXT_COLOR_2);
        valueView.setGravity(3);
        valueView.setTextSize(12.0f);
        valueView.setPadding(dp(4), 0, 0, 0);

        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(dp(22), dp(22));
        layoutParams3.gravity = 16;
        layoutParams3.setMargins(0, 0, dp(8), 0);

        final TextView eyeView = new TextView(getContext);
        eyeView.setLayoutParams(layoutParams3);

        byte[] bArrDecode = Base64.decode(EYE(), 0);
        final BitmapDrawable eyeDrawable = new BitmapDrawable(BitmapFactory.decodeByteArray(bArrDecode, 0, bArrDecode.length));
        eyeView.setBackground(eyeDrawable);

        byte[] bArrDecode2 = Base64.decode(EYECLOSE(), 0);
        final BitmapDrawable eyeCloseDrawable = new BitmapDrawable(BitmapFactory.decodeByteArray(bArrDecode2, 0, bArrDecode2.length));

        eyeView.setOnClickListener(new View.OnClickListener() {
            boolean settingsOpen = false;

            @Override
            public void onClick(View view) {
                try {
                    settingsOpen = !settingsOpen;
                    if (settingsOpen) {
                        eyeView.setBackground(eyeCloseDrawable);
                        valueView.setTransformationMethod(new PasswordTransformationMethod() {
                            @Override
                            public CharSequence getTransformation(CharSequence charSequence, View view2) {
                                return "●●●";
                            }
                        });
                    } else {
                        eyeView.setBackground(eyeDrawable);
                        valueView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }
                } catch (IllegalStateException e) {
                }
            }
        });

        Button btnSet = new Button(getContext);
        btnSet.setText("设置");
        btnSet.setAllCaps(false);
        btnSet.setTextSize(11.0f);
        btnSet.setTypeface(Typeface.DEFAULT_BOLD);
        btnSet.setTextColor(ACCENT_COLOR);
        btnSet.setBackground(accentStroke(14.0f));
        btnSet.setPadding(dp(12), dp(8), dp(12), dp(8));
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog inputDialog = new AlertDialog.Builder(getContext, 2).create(); // display the dialog
                Objects.requireNonNull(inputDialog.getWindow()).setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                inputDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        ((InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(1, 0);
                    }
                });

                LinearLayout dialogLayout = new LinearLayout(getContext);
                dialogLayout.setPadding(dp(16), dp(16), dp(16), dp(16));
                dialogLayout.setOrientation(LinearLayout.VERTICAL);

                GradientDrawable dialogBg = new GradientDrawable();
                dialogBg.setColor(MENU_BG_COLOR);
                dialogBg.setStroke(dp(1), ACCENT_COLOR);
                dialogLayout.setBackground(dialogBg);

                TextView titleView = new TextView(getContext);
                titleView.setText(Html.fromHtml("<b>" + featName + "</b>"));
                titleView.setGravity(17);
                titleView.setTypeface(Typeface.DEFAULT_BOLD);
                titleView.setTextColor(TEXT_COLOR);
                titleView.setTextSize(18.0f);
                titleView.setPadding(0, 0, 0, dp(8));

                TextView subTitleView = new TextView(getContext);
                subTitleView.setGravity(17);
                subTitleView.setTextSize(12.0f);
                subTitleView.setPadding(0, 0, 0, dp(8));
                subTitleView.setTextColor(TEXT_COLOR_SECONDARY);

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
                lp.weight = 1;
                final EditText inputField = new EditText(getContext);
                inputField.setLayoutParams(lp);
                inputField.setMaxLines(1);
                inputField.setHint("输入文本");
                inputField.setHintTextColor(TEXT_COLOR_SECONDARY);
                inputField.setWidth(convertDipToPixels(280));
                inputField.setTextColor(TEXT_COLOR);
                inputField.setText(editTextString.getString());

                GradientDrawable inputBg = new GradientDrawable();
                inputBg.setColor(MENU_CARD_COLOR);
                inputBg.setStroke(dp(1), MENU_CARD_BORDER);
                inputField.setBackground(inputBg);
                inputField.setPadding(dp(14), dp(12), dp(14), dp(12));
                inputField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view2, boolean z) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (z) {
                            imm.toggleSoftInput(2, 1);
                        } else {
                            imm.toggleSoftInput(1, 0);
                        }
                    }
                });

                LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(-1, -2);
                lpButton.setMargins(0, dp(14), 0, 0);
                Button btnConfirm = new Button(getContext);

                GradientDrawable btnBg = new GradientDrawable();
                btnBg.setColor(BTN_COLOR);
                btnBg.setStroke(dp(1), ACCENT_COLOR);
                btnConfirm.setBackground(btnBg);
                btnConfirm.setLayoutParams(lpButton);
                btnConfirm.setTextColor(-1);
                btnConfirm.setPadding(dp(14), dp(12), dp(14), dp(12));
                btnConfirm.setText("确定");
                btnConfirm.setTextSize(14.0f);
                btnConfirm.setTypeface(Typeface.DEFAULT_BOLD);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        String inputVal = inputField.getText().toString();
                        editTextString.setString(inputVal);
                        valueView.setText(Html.fromHtml("<font color='" + NumberTxtColor + "'>" + inputVal + "</font>"));
                        inputDialog.dismiss();
                        Preferences.changeFeatureString(featName, featNum, inputVal);
                        inputField.setFocusable(false);
                    }
                });

                dialogLayout.addView(titleView);
                dialogLayout.addView(subTitleView);
                dialogLayout.addView(inputField); // displays the user input bar
                dialogLayout.addView(btnConfirm);

                inputDialog.setView(dialogLayout);
                inputDialog.show();
                inputField.requestFocus();
            }
        });

        textContainer.addView(labelView);
        textContainer.addView(valueView);
        inputLayout.addView(textContainer);
        inputLayout.addView(eyeView);
        inputLayout.addView(btnSet);
        linLayout.addView(inputLayout);
    }

    private void CheckBox(LinearLayout linLayout, final int featNum, final String featName, boolean switchedOn) {
        final CheckBox checkBox = new CheckBox(getContext);
        checkBox.setText(featName);
        checkBox.setTextColor(TEXT_COLOR_2);
        checkBox.setTextSize(12.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkBox.setButtonTintList(ColorStateList.valueOf(CheckBoxColor));
        }
        checkBox.setChecked(Preferences.loadPrefBool(featName, featNum, switchedOn));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Preferences.changeFeatureBool(featName, featNum, isChecked);
            }
        });
        linLayout.addView(checkBox);
    }

    private void RadioButton(LinearLayout linLayout, final int featNum, String featName, String list) {
        // Credit: LoraZalora
        final List<String> itemsList = new LinkedList<>(Arrays.asList(list.split(",")));
        final TextView labelView = new TextView(getContext);
        labelView.setText(featName + ":");
        labelView.setTextColor(TEXT_COLOR_2);
        labelView.setTextSize(12.0f);

        final RadioGroup radioGroup = new RadioGroup(getContext);
        radioGroup.setPadding(dp(12), dp(6), dp(12), dp(6));
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.addView(labelView);

        for (int k = 0; k < itemsList.size(); k++) {
            final RadioButton radioButton = new RadioButton(getContext);
            final String finalfeatName = featName;
            final String radioName = itemsList.get(k);

            radioButton.setText(radioName);
            radioButton.setTextColor(TEXT_COLOR_2);
            radioButton.setTextSize(11.0f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButton.setButtonTintList(ColorStateList.valueOf(RadioColor));
            }
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    labelView.setText(Html.fromHtml(finalfeatName + ": <font color='" + NumberTxtColor + "'>" + radioName + "</font>"));
                    Preferences.changeFeatureInt(finalfeatName, featNum, radioGroup.indexOfChild(radioButton));
                }
            });
            radioGroup.addView(radioButton);
        }

        int index = Preferences.loadPrefInt(featName, featNum);
        if (index > 0) { // Preventing it to get an index less than 1. below 1 = null = crash
            labelView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + itemsList.get(index - 1) + "</font>"));
            ((RadioButton) radioGroup.getChildAt(index)).setChecked(true);
        }
        linLayout.addView(radioGroup);
    }

    private View ArrayBox(final int featNum, String items, boolean switchedOn) {
        LinearLayout linearLayout = new LinearLayout(getContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final String[] strArrSplit = items.split(",");
        int iCeil = (int) Math.ceil(((double) strArrSplit.length) / 3.0);

        for (int i2 = 0; i2 < iCeil; i2++) {
            LinearLayout rowLayout = new LinearLayout(getContext);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int i3 = i2; i3 < strArrSplit.length; i3 += iCeil) {
                final int index = i3;
                CheckBox checkBox = new CheckBox(getContext);
                checkBox.setText(Html.fromHtml(strArrSplit[i3]));
                checkBox.setTextColor(TEXT_COLOR_2);
                checkBox.setTextSize(11.0f);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    checkBox.setButtonTintList(ColorStateList.valueOf(CheckBoxColor));
                }
                checkBox.setChecked(Preferences.loadPrefBool(strArrSplit[i3], featNum, switchedOn));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Preferences.changeFeatureInt(strArrSplit[index], featNum, index + 1);
                    }
                });
                rowLayout.addView(checkBox);
            }
            linearLayout.addView(rowLayout);
        }
        return linearLayout;
    }

    private void Collapse(LinearLayout linLayout, final String text, final boolean expanded) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        layoutParams.setMargins(dp(6), dp(6), dp(6), 0);

        LinearLayout collapseLayout = new LinearLayout(getContext);
        collapseLayout.setLayoutParams(layoutParams);
        collapseLayout.setVerticalGravity(16);
        collapseLayout.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout collapseSub = new LinearLayout(getContext);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(dp(8), dp(8), dp(8), dp(8));
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackground(glassCard(18.0f));
        collapseSub.setVisibility(View.GONE);
        mCollapse = collapseSub;

        final TextView headerView = new TextView(getContext);
        headerView.setBackground(glassCard(22.0f));
        headerView.setText("▸ " + text);
        headerView.setGravity(17);
        headerView.setTextColor(ACCENT_COLOR);
        headerView.setTypeface(null, Typeface.BOLD);
        headerView.setTextSize(12.0f);
        headerView.setPadding(dp(8), dp(12), dp(8), dp(12));

        if (expanded) {
            collapseSub.setVisibility(View.VISIBLE);
            headerView.setText("▾ " + text);
            headerView.setTextColor(-1);
            headerView.setBackground(accentFill());
        }

        headerView.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = expanded;

            @Override
            public void onClick(View view) {
                isChecked = !isChecked;
                if (isChecked) {
                    collapseSub.setVisibility(View.VISIBLE);
                    headerView.setText("▾ " + text);
                    headerView.setTextColor(-1);
                    headerView.setBackground(accentFill());
                } else {
                    collapseSub.setVisibility(View.GONE);
                    headerView.setText("▸ " + text);
                    headerView.setTextColor(ACCENT_COLOR);
                    headerView.setBackground(glassCard(22.0f));
                }
            }
        });

        collapseLayout.addView(headerView);
        collapseLayout.addView(collapseSub);
        linLayout.addView(collapseLayout);
    }

    private void Category(LinearLayout linLayout, String text) {
        LinearLayout categoryLayout = new LinearLayout(getContext);
        categoryLayout.setPadding(dp(8), dp(4), dp(8), dp(4));

        TextView textView = new TextView(getContext);
        textView.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));

        GradientDrawable categoryBg = new GradientDrawable();
        categoryBg.setColor(CategoryBG);
        categoryBg.setCornerRadius(dp(12.0f));
        categoryBg.setStroke(dp(1), Color.parseColor("#1A007AFF"));

        textView.setBackground(categoryBg);
        textView.setText(Html.fromHtml(text));
        textView.setGravity(17);
        textView.setTextColor(ACCENT_COLOR);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(11.0f);
        textView.setPadding(dp(4), dp(4), dp(4), dp(4));

        categoryLayout.addView(textView);
        linLayout.addView(categoryLayout);
    }

    private void TextView(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTextSize(11.0f);
        textView.setPadding(dp(12), dp(6), dp(12), dp(6));
        linLayout.addView(textView);
    }

    private void WebTextView(LinearLayout linLayout, String text) {
        WebView webView = new WebView(getContext);
        webView.loadData(text, "text/html", "utf-8");
        webView.setBackgroundColor(0);
        webView.setPadding(0, dp(6), 0, dp(6));
        webView.getSettings().setAppCacheEnabled(false);
        linLayout.addView(webView);
    }

    private void reloadFeaturesInstance() {
        LinearLayout linearLayout = mSettings;
        if (linearLayout != null) {
            linearLayout.removeAllViews();
            LinearLayout loadingLayout = new LinearLayout(getContext);
            loadingLayout.setOrientation(LinearLayout.VERTICAL);
            loadingLayout.setGravity(17);
            loadingLayout.setPadding(0, dp(40), 0, dp(40));

            final TextView loadingTextView = new TextView(getContext);
            loadingTextView.setText("初始化加载...");
            loadingTextView.setTextColor(ACCENT_COLOR);
            loadingTextView.setTextSize(13.0f);
            loadingTextView.setTypeface(Typeface.DEFAULT_BOLD);
            loadingTextView.setGravity(17);

            final ProgressBar progressBar = new ProgressBar(getContext, null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(200), dp(4));
            lp.setMargins(0, dp(16), 0, 0);
            progressBar.setLayoutParams(lp);
            progressBar.setProgressDrawable(createDrawable(MENU_FEATURE_BG_COLOR, 3.0f));

            GradientDrawable progressBg = new GradientDrawable();
            progressBg.setColor(ACCENT_COLOR);
            progressBg.setCornerRadius(dp(3.0f));
            progressBar.setProgressDrawable(progressBg);
            progressBar.setMax(100);
            progressBar.setProgress(0);

            loadingLayout.addView(loadingTextView);
            loadingLayout.addView(progressBar);
            mSettings.addView(loadingLayout);

            final Handler handler = new Handler();
            final int[] progress = {0};
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progress[0] += 5;
                    progressBar.setProgress(progress[0]);
                    int currentProgress = progress[0];
                    if (currentProgress < 30) {
                        loadingTextView.setText("加载功能...");
                    } else if (currentProgress < 60) {
                        loadingTextView.setText("处理菜单...");
                    } else if (currentProgress < 90) {
                        loadingTextView.setText("获取配置...");
                    } else {
                        loadingTextView.setText("✦ 完成 ✦");
                    }

                    if (progress[0] < 100) {
                        handler.postDelayed(this, 50);
                    } else {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSettings.removeAllViews();
                                featureList(SettingsList(), mSettings);
                            }
                        }, 200);
                    }
                }
            }, 100);
        }
    }

    public void setSwitchState(LinearLayout parentLayout, int featNum, boolean isChecked) {
        for (int i2 = 0; i2 < parentLayout.getChildCount(); i2++) {
            View child = parentLayout.getChildAt(i2);
            if (child instanceof Switch) {
                Switch switchView = (Switch) child;
                if (switchView.getTag() != null && ((Integer) switchView.getTag()).intValue() == featNum) {
                    switchView.setChecked(isChecked);
                    return;
                }
            }
        }
    }

    public void ShowMenu() {
        rootFrame.addView(mRootContainer);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            boolean viewLoaded = false;

            @Override
            public void run() {
                // If the save preferences is enabled, it will check if game lib is loaded
                // before starting menu
                if (Preferences.loadPref && !IsGameLibLoaded() && !stopChecking) {
                    if (!viewLoaded) {
                        Category(mods, "已启用“保存首选项”。正在等待加载游戏库。\n\n强制加载菜单可能不会立即应用mods。你需要再次激活它们");
                        Button(mods, -100, "强制加载菜单");
                        viewLoaded = true;
                    }
                    handler.postDelayed(this, 600);
                } else {
                    mods.removeAllViews();
                    featureList(GetFeatureList(), mods);
                }
            }
        }, 500);
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerActivity() {
        vmParams = new WindowManager.LayoutParams(
                -2,
                -2,
                POS_X,
                POS_Y,
                2,
                41943304,
                -2
        );
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;
        mWindowManager = ((Activity) getContext).getWindowManager();
        mWindowManager.addView(rootFrame, vmParams);
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerWindowService() {
        int paramsType;
        // Variable to check later if the phone supports Draw over other apps permission
        if (Build.VERSION.SDK_INT >= 26) {
            paramsType = 2038;
        } else {
            paramsType = 2002;
        }

        vmParams = new WindowManager.LayoutParams(
                -2,
                -2,
                paramsType,
                8,
                -3
        );
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;
        mWindowManager = (WindowManager) getContext.getSystemService("window");
        mWindowManager.addView(rootFrame, vmParams);
        overlayRequired = true;
    }

    private class EditTextString {
        private String text;

        public void setString(String str) {
            this.text = str;
        }

        public String getString() {
            return this.text;
        }
    }

    private class EditTextNum {
        private int val;

        public void setNum(int i) {
            this.val = i;
        }

        public int getNum() {
            return this.val;
        }
    }

    public class ThemeManager {
        public ThemeManager instance;
        private int themeColor;

        public ThemeManager() {
            this.themeColor = -1;
            this.themeColor = Preferences.loadPrefInt("ThemeColor", -1);
        }

        public ThemeManager getInstance() {
            if (this.instance == null) {
                this.instance = new ThemeManager();
            }
            return this.instance;
        }

        public int getThemeColor() {
            return this.themeColor;
        }

        public void setThemeColor(int i) {
            this.themeColor = i;
            Preferences.loadPrefInt("ThemeColor", i);
        }
    }
}
