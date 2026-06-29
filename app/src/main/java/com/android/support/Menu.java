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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.ValueAnimator;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Looper;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;

public class Menu {
    // ********** Here you can easly change the menu appearance **********//

    // region Variable
    public static final String TAG = "Mod_Menu";
    private static Menu instance;
    public static boolean isLoggedIn = false;
    public static int ACCENT_COLOR = Color.parseColor("#FFFFFB");
    static final int COLOR_CYAN = -16728876;
    static final int COLOR_GREEN = -11751600;
    static final int COLOR_RED = -769226;
    static final int MAX_TIP = 5;
    static final List<View> sTipQueue = new ArrayList<>();

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

    // Email / feedback fields
    private final String SENDER_EMAIL;
    private final String AUTH_CODE;
    private final String RECEIVER_EMAIL;
    private final String SMTP_SERVER = "smtp.qq.com";
    private final int SMTP_PORT = 465;
    private int FEEDBACK_ACCENT;
    private String feedbackImagePath;
    private String feedbackContent;
    private LinearLayout feedbackMod;

    // Music player fields
    private MediaPlayer mediaPlayer;
    private List<Map<String, String>> currentPlaylist;
    private int currentPlaylistIndex;
    private volatile String currentPlayingId;
    private boolean isPlaying;
    private boolean isPaused;
    private volatile boolean isPreparing;
    private boolean isSeeking;
    private Handler musicHandler;
    private final Handler mainHandler;
    private List<Map<String, String>> lyricsData;
    private int currentLyricIndex;
    private LinearLayout modsMusic;
    private LinearLayout musicListContainer;
    private LinearLayout musicControlContainer;
    private ImageView btnPlayPause;
    private ImageView btnNext;
    private ImageView btnPrev;
    private SeekBar seekBarProgress;
    private TextView tvCurrentSong;
    private TextView tvCurrentArtist;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private TextView tvLyricCurrent;
    private Runnable progressRunnable;
    private final android.view.animation.Interpolator ELASTIC;
    private final java.util.Map<View, Boolean> pendingRemove;
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

    native String getNativeAuthCode();

    native String getNativeReceiverEmail();

    native String getNativeSenderEmail();

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

        // Feedback/email init
        feedbackImagePath = null;
        feedbackContent = "";
        FEEDBACK_ACCENT = Color.parseColor("#007AFF");
        SENDER_EMAIL = getNativeSenderEmail();
        AUTH_CODE = getNativeAuthCode();
        RECEIVER_EMAIL = getNativeReceiverEmail();

        // Music player init
        mainHandler = new Handler(android.os.Looper.getMainLooper());
        ELASTIC = new android.view.animation.Interpolator() {
            private final float mFactor = 0.4f;
            @Override
            public float getInterpolation(float input) {
                return (float)(Math.pow(2, -10 * input) * Math.sin((input - 0.1f) * 6.283185307179586 / 0.4f) + 1);
            }
        };
        pendingRemove = new java.util.HashMap<>();
        currentPlaylist = new java.util.ArrayList<>();
        currentPlaylistIndex = -1;
        isPaused = false;
        isSeeking = false;
        musicHandler = new Handler(android.os.Looper.getMainLooper());
        isPlaying = false;
        lyricsData = new java.util.ArrayList<>();
        currentLyricIndex = 0;
        currentPlayingId = null;
        isPreparing = false;

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

        modsMusic = new LinearLayout(context);
        modsMusic.setOrientation(LinearLayout.VERTICAL);

        feedbackMod = new LinearLayout(context);
        feedbackMod.setOrientation(LinearLayout.VERTICAL);

        // ********** Mod menu feature list **********
        final int[] scrollPositions = new int[12];
        final int[] currentTab = {0};
        final LinearLayout[] modLayouts = {mods, mods2, mods3, mods4, mods5, mods6, mods7, mods8, mods9, mSettings, modsMusic, feedbackMod};

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

        String[] tabTitles = {"实用", "传送", "跑图", "人物", "娱乐", "魔法", "衣柜", "环境", "彩蛋", "日志", "音乐", "反馈"};
        final TextView[] segItems = new TextView[12];

        for (int i = 0; i < 12; i++) {
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
                        for (int k = 0; k < 12; k++) {
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

                        for (int k = 0; k < 12; k++) {
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
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
        stopMusic();
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

        final Stack<LinearLayout> stack = new Stack<>();

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
                featureStr = featureStr.replaceFirst("CollapseAdd_", "");
                if (featureStr.startsWith("Collapse_") && !stack.isEmpty()) {
                    mCollapse = stack.peek();
                }
                currentLayout = mCollapse;
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
                    if (currentLayout == parentLayout) {
                        stack.clear();
                    } else if (stack.isEmpty() || stack.peek() != mCollapse) {
                        stack.push(mCollapse);
                    }
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

    public static void setLiveStreamMode(boolean on) {
        Menu menu = instance;
        if (instance != null && menu.mWindowManager != null && menu.rootFrame != null && menu.vmParams != null) {
            int secureFlag = on ? WindowManager.LayoutParams.FLAG_SECURE : 0;
            menu.vmParams.flags = (secureFlag | 0x40028);

            menu.rootFrame.setVisibility(View.INVISIBLE);
            menu.mWindowManager.removeView(menu.rootFrame);
            menu.mWindowManager.addView(menu.rootFrame, menu.vmParams);
            menu.rootFrame.setVisibility(View.VISIBLE);
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
                    feedbackMod.removeAllViews();
                    initFeedbackPage(feedbackMod);
                    modsMusic.removeAllViews();
                    initMusicPage(modsMusic);
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


    // ==================== Utility ====================

    private GradientDrawable roundBg(int color, float cornerRadius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.setCornerRadius(cornerRadius);
        return gd;
    }

    private String formatTime(int ms) {
        if (ms <= 0) return "00:00";
        int s = ms / 1000;
        return String.format("%02d:%02d", s / 60, s % 60);
    }

    private int countPathDepth(String path) {
        int depth = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') depth++;
        }
        return depth;
    }

    private byte[] readFileToBytes(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        try {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        } finally {
            fis.close();
        }
    }

    // ==================== Email ====================

    private void sendCommand(BufferedWriter writer, String cmd) throws Exception {
        writer.write(cmd + "\r\n");
        writer.flush();
    }

    private boolean sendEmailBySocket(String host, int port, String sender, String authCode,
                                      String recipient, String subject, final String body) {
        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            socket = (Socket) SSLSocketFactory.getDefault().createSocket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            if (!reader.readLine().startsWith("220")) return false;

            sendCommand(writer, "EHLO android");
            while (true) {
                String line = reader.readLine();
                if (line == null) return false;
                if (line.startsWith("250 ")) break;
                if (!line.startsWith("250-")) return false;
            }

            sendCommand(writer, "AUTH LOGIN");
            if (!reader.readLine().startsWith("334")) return false;
            sendCommand(writer, Base64.encodeToString(sender.getBytes(), 2));
            if (!reader.readLine().startsWith("334")) return false;
            sendCommand(writer, Base64.encodeToString(authCode.getBytes(), 2));
            if (!reader.readLine().startsWith("235")) return false;

            sendCommand(writer, "MAIL FROM:<" + sender + ">");
            if (!reader.readLine().startsWith("250")) return false;
            sendCommand(writer, "RCPT TO:<" + recipient + ">");
            if (!reader.readLine().startsWith("250")) return false;
            sendCommand(writer, "DATA");
            if (!reader.readLine().startsWith("354")) return false;

            final String boundary = "----=_Part_" + System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append("From: =?UTF-8?B?").append(Base64.encodeToString("游戏反馈系统".getBytes("UTF-8"), 2)).append("?= <").append(sender).append(">\r\n");
            sb.append("To: ").append(recipient).append("\r\n");
            sb.append("Subject: =?UTF-8?B?").append(Base64.encodeToString(subject.getBytes("UTF-8"), 2)).append("?=\r\n");
            sb.append("MIME-Version: 1.0\r\n");
            sb.append("Content-Type: multipart/mixed; boundary=\"").append(boundary).append("\"\r\n\r\n");
            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Type: text/html; charset=UTF-8\r\n");
            sb.append("Content-Transfer-Encoding: base64\r\n\r\n");

            String bodyB64 = Base64.encodeToString(body.getBytes("UTF-8"), 2);
            for (int i = 0; i < bodyB64.length(); i += 76) {
                sb.append(bodyB64, i, Math.min(i + 76, bodyB64.length())).append("\r\n");
            }

            if (this.feedbackImagePath != null) {
                File imgFile = new File(this.feedbackImagePath);
                if (imgFile.exists()) {
                    sb.append("--").append(boundary).append("\r\n");
                    String fname = imgFile.getName().toLowerCase();
                    String mime = fname.endsWith(".png") ? "image/png" : fname.endsWith(".gif") ? "image/gif" : "image/jpeg";
                    sb.append("Content-Type: ").append(mime).append("; name=\"").append(imgFile.getName()).append("\"\r\n");
                    sb.append("Content-Disposition: attachment; filename=\"").append(imgFile.getName()).append("\"\r\n");
                    sb.append("Content-Transfer-Encoding: base64\r\n\r\n");
                    String attachB64 = Base64.encodeToString(readFileToBytes(imgFile), 2);
                    for (int i = 0; i < attachB64.length(); i += 76) {
                        sb.append(attachB64, i, Math.min(i + 76, attachB64.length())).append("\r\n");
                    }
                }
            }

            sb.append("--").append(boundary).append("--\r\n.\r\n");
            writer.write(sb.toString());
            writer.flush();

            if (reader.readLine().startsWith("250")) {
                sendCommand(writer, "QUIT");
                return true;
            }
            return false;
        } catch (Exception e) {
            android.util.Log.e("EmailSocket", "发送失败: " + e.getMessage());
            return false;
        } finally {
            try { if (writer != null) writer.close(); } catch (Exception ignored) {}
            try { if (reader != null) reader.close(); } catch (Exception ignored) {}
            try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        }
    }

    private boolean sendFeedbackEmail(String subject, String htmlContent) {
        try {
            return sendEmailBySocket(SMTP_SERVER, SMTP_PORT, SENDER_EMAIL, AUTH_CODE, RECEIVER_EMAIL, subject, htmlContent);
        } catch (Exception e) {
            android.util.Log.e("Email", "发送失败: " + e.getMessage());
            return false;
        }
    }

    private String buildFeedbackEmailContent() {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<style>body{font-family:'Microsoft YaHei',sans-serif;background:#1C1C1E;margin:0;padding:20px;}");
        sb.append(".container{max-width:600px;margin:0 auto;background:#2C2C2E;border-radius:16px;overflow:hidden;}");
        sb.append(".header{background:linear-gradient(135deg,#007AFF 0%,#5856D6 100%);color:white;padding:30px;text-align:center;}");
        sb.append(".header h1{margin:0;font-size:24px;}");
        sb.append(".content{padding:30px;color:#FFFFFF;line-height:1.8;}");
        sb.append(".info-box{background:#1C1C1E;border-left:4px solid #007AFF;padding:15px;margin:20px 0;border-radius:0 8px 8px 0;color:#8E8E93;}");
        sb.append(".footer{background:#38383A;padding:20px;text-align:center;color:#8E8E93;font-size:12px;}");
        sb.append("</style></head><body>");
        sb.append("<div class='container'>");
        sb.append("<div class='header'><h1>Sky_Mod反馈</h1></div>");
        sb.append("<div class='content'>");
        sb.append("<div class='info-box'><strong>反馈时间：</strong>").append(time).append("</div>");
        sb.append("<h3 style='color:#007AFF;'>问题描述</h3>");
        sb.append("<p>").append(feedbackContent.replace("\n", "<br>")).append("</p>");
        if (feedbackImagePath != null) {
            sb.append("<p style='color:#007AFF;'><strong>已附加截图</strong></p>");
        }
        sb.append("</div>");
        sb.append("<div class='footer'>来自游戏内反馈系统</div>");
        sb.append("</div></body></html>");
        return sb.toString();
    }

    private void submitFeedback() {
        if (feedbackContent.isEmpty()) {
            showGlobalTip("错误！", "请输入问题描述", 0);
            return;
        }
        showSubmitConfirmDialog("反馈箱", buildFeedbackEmailContent());
    }


    private void showSubmitConfirmDialog(final String subject, final String htmlContent) {
        final WindowManager windowManager = (WindowManager) this.getContext.getSystemService("window");
        final FrameLayout frameLayout = new FrameLayout(this.getContext);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        final View overlay = new View(this.getContext);
        overlay.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        overlay.setBackgroundColor(Color.parseColor("#80000000"));
        overlay.setAlpha(0.0f);
        final LinearLayout popup = new LinearLayout(this.getContext);
        popup.setOrientation(LinearLayout.VERTICAL);
        popup.setBackground(glassCard(dp(20)));
        popup.setPadding(dp(24), dp(20), dp(24), dp(24));
        popup.setAlpha(0.0f);
        popup.setScaleX(0.8f);
        popup.setScaleY(0.8f);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(300), -2);
        lp.gravity = android.view.Gravity.CENTER;
        popup.setLayoutParams(lp);
        TextView title = new TextView(this.getContext);
        title.setText("确认提交");
        title.setTextColor(ACCENT_COLOR);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(android.view.Gravity.CENTER);
        popup.addView(title);
        View divider = new View(this.getContext);
        divider.setBackgroundColor(MENU_CARD_BORDER);
        LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(-1, dp(1));
        divLp.setMargins(0, dp(16), 0, dp(16));
        divider.setLayoutParams(divLp);
        popup.addView(divider);
        TextView info = new TextView(this.getContext);
        StringBuilder infoText = new StringBuilder();
        infoText.append("即将发送以下信息：\n\n");
        infoText.append("• 主题：投稿箱\n");
        infoText.append("• 问题描述：");
        infoText.append(feedbackContent.length() > 20 ? feedbackContent.substring(0, 20) + "..." : feedbackContent);
        infoText.append("\n• 截图：").append(feedbackImagePath != null ? "已附加" : "无");
        infoText.append("\n\n收件人：******");
        info.setText(infoText.toString());
        info.setTextColor(TEXT_COLOR_2);
        info.setTextSize(13);
        popup.addView(info);
        LinearLayout btnRow = new LinearLayout(this.getContext);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setPadding(0, dp(20), 0, 0);
        final Button cancelBtn = new Button(this.getContext);
        cancelBtn.setText("取消");
        cancelBtn.setAllCaps(false);
        cancelBtn.setTextColor(Color.parseColor("#8E8E93"));
        cancelBtn.setBackground(roundBg(MENU_CARD_COLOR, dp(10)));
        LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(0, dp(44), 1.0f);
        cancelLp.setMarginEnd(dp(8));
        cancelBtn.setLayoutParams(cancelLp);
        final Button sendBtn = new Button(this.getContext);
        sendBtn.setText("发送邮件");
        sendBtn.setAllCaps(false);
        sendBtn.setTextColor(Color.parseColor("#1C1C1E"));
        GradientDrawable sendBg = new GradientDrawable();
        sendBg.setColor(ACCENT_COLOR);
        sendBg.setCornerRadius(dp(10));
        sendBtn.setBackground(sendBg);
        LinearLayout.LayoutParams sendLp = new LinearLayout.LayoutParams(0, dp(44), 1.0f);
        sendLp.setMarginStart(dp(8));
        sendBtn.setLayoutParams(sendLp);
        btnRow.addView(cancelBtn);
        btnRow.addView(sendBtn);
        popup.addView(btnRow);
        frameLayout.addView(overlay);
        frameLayout.addView(popup);
        final android.widget.PopupWindow pw = new android.widget.PopupWindow(frameLayout, -1, -1, true);
        pw.setBackgroundDrawable(null);
        pw.setOutsideTouchable(false);
        pw.setFocusable(true);
        try {
            pw.showAtLocation(mRootContainer, android.view.Gravity.CENTER, 0, 0);
        } catch (Exception e) { return; }
        overlay.animate().alpha(1.0f).setDuration(200).start();
        popup.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(300).setInterpolator(new OvershootInterpolator(1.2f)).start();
        final Runnable dismiss = new Runnable() {
            @Override public void run() {
                overlay.animate().alpha(0.0f).setDuration(150).start();
                popup.animate().alpha(0.0f).scaleX(0.8f).scaleY(0.8f).setDuration(200).withEndAction(new Runnable() {
                    @Override public void run() { pw.dismiss(); }
                }).start();
            }
        };
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { dismiss.run(); }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                sendBtn.setEnabled(false);
                sendBtn.setText("发送中...");
                new Thread(new Runnable() {
                    @Override public void run() {
                        final boolean ok = sendFeedbackEmail(subject, htmlContent);
                        mainHandler.post(new Runnable() {
                            @Override public void run() {
                                dismiss.run();
                                if (ok) {
                                    soundEffectPlayer.playSoundEffect("Inform_Message_GTA.ogg");
                                    showGlobalTip("反馈提交", "邮件已发送", 1);
                                    feedbackContent = "";
                                    feedbackImagePath = null;
                                } else {
                                    showGlobalTip("反馈提交", "发送失败", 0);
                                }
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void showGlobalTip(final String title, final String detail, int type) {
        if (getContext == null) return;
        if (Menu.sTipQueue.size() >= 5) return;
        final WindowManager wm = (WindowManager) getContext.getSystemService("window");
        final WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
        wlp.height = -2;
        wlp.width = -2;
        wlp.format = -3;
        wlp.flags = 264;
        wlp.gravity = 85;
        wlp.x = dp(12);
        wlp.type = (Build.VERSION.SDK_INT >= 26) ? 2038 : 2002;
        final LinearLayout root = new LinearLayout(getContext);
        root.setOrientation(LinearLayout.HORIZONTAL);
        int pad = dp(12);
        root.setPadding(pad, pad, pad, pad);
        root.setBackground(roundBg(0xE6FFFFFF, dp(20)));
        View dot = new View(getContext);
        int dotSize = dp(14);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dotSize, dotSize);
        dotLp.gravity = android.view.Gravity.CENTER_VERTICAL;
        dotLp.setMargins(0, 0, dp(10), 0);
        dot.setLayoutParams(dotLp);
        int dotColor = (type == 2) ? COLOR_CYAN : (type == 1) ? COLOR_GREEN : COLOR_RED;
        dot.setBackground(roundBg(dotColor, dotSize / 2));
        LinearLayout textCol = new LinearLayout(getContext);
        textCol.setOrientation(LinearLayout.VERTICAL);
        TextView tv1 = new TextView(getContext);
        tv1.setText(title);
        tv1.setTextColor(Color.BLACK);
        tv1.setTextSize(16);
        TextView tv2 = new TextView(getContext);
        tv2.setText(detail);
        tv2.setTextColor(Color.BLACK);
        tv2.setTextSize(12);
        textCol.addView(tv1);
        textCol.addView(tv2);
        root.addView(dot);
        root.addView(textCol);
        root.setAlpha(0.0f);
        root.setTranslationY(-dp(60));
        try {
            wm.addView(root, wlp);
            Menu.sTipQueue.add(root);
            ValueAnimator anim = ValueAnimator.ofFloat(-dp(60), 0);
            anim.setDuration(350);
            anim.setInterpolator(ELASTIC);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator va) {
                    root.setTranslationY((float) va.getAnimatedValue());
                }
            });
            root.animate().alpha(1.0f).setDuration(350).start();
            anim.start();
            root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    rearrangeTips();
                }
            });
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override public void run() { removeTipWithSlide(root); }
            }, 2000);
        } catch (Exception ignored) {}
    }

    void rearrangeTips() {
        if (Menu.sTipQueue.isEmpty()) return;
        final WindowManager wm = (WindowManager) getContext.getSystemService("window");
        int y = dp(24);
        for (final View v : Menu.sTipQueue) {
            if (pendingRemove.containsKey(v)) continue;
            if (v.getParent() == null) continue;
            final WindowManager.LayoutParams lp = (WindowManager.LayoutParams) v.getLayoutParams();
            if (lp == null) continue;
            final int targetY = y;
            ValueAnimator anim = ValueAnimator.ofInt(lp.y, targetY);
            anim.setDuration(250);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator va) {
                    if (v.getParent() == null) return;
                    lp.y = (int) va.getAnimatedValue();
                    try { wm.updateViewLayout(v, lp); } catch (Exception ignored) {}
                }
            });
            anim.start();
            y += v.getHeight() + dp(6);
        }
    }

    private void removeTipWithSlide(final View view) {
        if (view == null || view.getParent() == null) return;
        final WindowManager wm = (WindowManager) getContext.getSystemService("window");
        pendingRemove.put(view, Boolean.TRUE);
        view.animate().translationY(-view.getHeight() * 0.2f).alpha(0.0f).setDuration(150).withEndAction(new Runnable() {
            @Override public void run() {
                try { wm.removeView(view); } catch (Exception ignored) {}
                Menu.sTipQueue.remove(view);
                pendingRemove.remove(view);
                rearrangeTips();
            }
        }).start();
    }


    // ==================== Music ====================

    private void stopProgressUpdate() {
        if (progressRunnable != null) {
            musicHandler.removeCallbacks(progressRunnable);
            progressRunnable = null;
        }
    }

    private void startProgressUpdate() {
        stopProgressUpdate();
        progressRunnable = new Runnable() {
            @Override public void run() {
                if (mediaPlayer != null && isPlaying && !isSeeking) {
                    try {
                        int pos = mediaPlayer.getCurrentPosition();
                        int dur = mediaPlayer.getDuration();
                        if (dur > 0) {
                            seekBarProgress.setProgress((int)(pos * 1000L / dur));
                            tvCurrentTime.setText(formatTime(pos));
                            tvTotalTime.setText(formatTime(dur));
                        }
                        updateCurrentLyric(pos);
                    } catch (Exception ignored) {}
                }
                if (progressRunnable != null) {
                    musicHandler.postDelayed(this, 50);
                }
            }
        };
        musicHandler.post(progressRunnable);
    }

    private void togglePlayPause() {
        if (mediaPlayer != null && currentPlayingId != null) {
            if (isPaused) {
                mediaPlayer.start();
                isPaused = false;
                isPlaying = true;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                startProgressUpdate();
                showGlobalTip(tvCurrentSong.getText().toString(), "继续播放", 1);
            } else {
                mediaPlayer.pause();
                isPaused = true;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                stopProgressUpdate();
                showGlobalTip(tvCurrentSong.getText().toString(), "已暂停", 2);
            }
            return;
        }
        showGlobalTip("提示", "没有正在播放的歌曲", 2);
    }

    public void stopMusic() {
        stopProgressUpdate();
        lyricsData.clear();
        currentLyricIndex = 0;
        if (tvLyricCurrent != null) tvLyricCurrent.setText("等待播放...");
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }
        isPlaying = false;
        isPaused = false;
        isPreparing = false;
        currentPlayingId = null;
        if (btnPlayPause != null) btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
    }

    private void playNext() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            if (++currentPlaylistIndex >= currentPlaylist.size()) currentPlaylistIndex = 0;
            Map<String, String> song = currentPlaylist.get(currentPlaylistIndex);
            playMusic(song.get("id"), song.get("name"), song.get("artist"));
            showGlobalTip("下一首", song.get("name"), 2);
            return;
        }
        showGlobalTip("提示", "播放列表为空", 2);
    }

    private void playPrevious() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            if (--currentPlaylistIndex < 0) currentPlaylistIndex = currentPlaylist.size() - 1;
            Map<String, String> song = currentPlaylist.get(currentPlaylistIndex);
            playMusic(song.get("id"), song.get("name"), song.get("artist"));
            showGlobalTip("上一首", song.get("name"), 2);
            return;
        }
        showGlobalTip("提示", "播放列表为空", 2);
    }

    private String[] parseLyricLine(String line) {
        Matcher m = Pattern.compile("\\[(\\d{2}):(\\d{2}\\.?\\d*)\\](.*)").matcher(line.trim());
        if (m.find()) {
            int ms = (int)((Integer.parseInt(m.group(1)) * 60 + Float.parseFloat(m.group(2))) * 1000);
            String text = m.group(3).trim();
            if (text.length() > 0) return new String[]{String.valueOf(ms), text};
        }
        return null;
    }

    private void parseLyricsJson(String json) {
        try {
            JSONObject root = new JSONObject(json);
            if (!root.has("lrc") || root.isNull("lrc")) return;
            String mainLrc = root.getJSONObject("lrc").optString("lyric", "");
            String transLrc = "";
            JSONObject tlyric = root.optJSONObject("tlyric");
            if (tlyric != null) transLrc = tlyric.optString("lyric", "");
            HashMap<Integer, String> transMap = new HashMap<>();
            if (transLrc.length() > 0) {
                for (String l : transLrc.split("\\n")) {
                    String[] parts = parseLyricLine(l);
                    if (parts != null) transMap.put(Integer.parseInt(parts[0]), parts[1]);
                }
            }
            for (String l : mainLrc.split("\\n")) {
                String[] parts = parseLyricLine(l);
                if (parts != null) {
                    HashMap<String, String> entry = new HashMap<>();
                    entry.put("time", parts[0]);
                    String text = parts[1];
                    Integer key = Integer.parseInt(parts[0]);
                    if (transMap.containsKey(key)) text += "\n" + transMap.get(key);
                    entry.put("text", text);
                    lyricsData.add(entry);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Lyrics", "解析歌词失败: " + e.getMessage());
        }
    }

    private void getLyrics(final String songId) {
        lyricsData.clear();
        currentLyricIndex = 0;
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(
                            "https://music.163.com/api/song/lyric?os=pc&id=" + songId + "&lv=-1&kv=-1&tv=-1"
                    ).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setRequestProperty("Referer", "https://music.163.com/");
                    if (conn.getResponseCode() == 200) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) sb.append(line);
                        br.close();
                        parseLyricsJson(sb.toString());
                    }
                } catch (Exception e) {
                    android.util.Log.e("Lyrics", "获取歌词失败: " + e.getMessage());
                }
                mainHandler.post(new Runnable() {
                    @Override public void run() { updateLyricsUI(); }
                });
            }
        }).start();
    }

    private void updateLyricsUI() {
        if (tvLyricCurrent == null) return;
        if (lyricsData.isEmpty()) {
            tvLyricCurrent.setText("纯音乐，无歌词");
            return;
        }
        tvLyricCurrent.setText(lyricsData.get(0).get("text"));
        currentLyricIndex = 0;
    }

    private void updateCurrentLyric(int posMs) {
        if (lyricsData == null || lyricsData.isEmpty()) return;
        int idx = 0;
        for (int i = 0; i < lyricsData.size(); i++) {
            if (Integer.parseInt(lyricsData.get(i).get("time")) > posMs) break;
            idx = i;
        }
        if (idx != currentLyricIndex) {
            currentLyricIndex = idx;
            final String text = lyricsData.get(idx).get("text");
            mainHandler.post(new Runnable() {
                @Override public void run() {
                    if (tvLyricCurrent == null) return;
                    String[] parts = text.split("\\n|\\r\\n|\\|");
                    if (parts.length >= 2) {
                        tvLyricCurrent.setText(parts[0] + "\n" + parts[1]);
                    } else {
                        tvLyricCurrent.setText(text);
                    }
                    tvLyricCurrent.animate().scaleX(1.05f).scaleY(1.05f).setDuration(150).withEndAction(new Runnable() {
                        @Override public void run() {
                            tvLyricCurrent.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                        }
                    }).start();
                }
            });
        }
    }

    private void playCachedFile(final File file, final String id, final String name, final String artist) {
        if (!isPreparing && currentPlayingId != null && !currentPlayingId.equals(id)) {
            file.delete();
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override public void onPrepared(MediaPlayer mp) {
                    isPlaying = true;
                    isPreparing = false;
                    currentPlayingId = id;
                    if (musicControlContainer != null) musicControlContainer.setVisibility(View.VISIBLE);
                    if (tvCurrentSong != null) tvCurrentSong.setText(name);
                    if (tvCurrentArtist != null) tvCurrentArtist.setText(artist);
                    if (btnPlayPause != null) btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                    int dur = mp.getDuration();
                    if (seekBarProgress != null) seekBarProgress.setMax(1000);
                    if (tvTotalTime != null) tvTotalTime.setText(formatTime(dur));
                    mp.start();
                    startProgressUpdate();
                    showGlobalTip(name, "开始播放", 1);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override public void onCompletion(MediaPlayer mp) { playNext(); }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override public boolean onError(MediaPlayer mp, int what, int extra) {
                    stopMusic();
                    String err = (what == -1004) ? "文件损坏" : "播放错误(" + what + ")";
                    showGlobalTip(name, err, 0);
                    file.delete();
                    return true;
                }
            });
            mediaPlayer.prepareAsync();
            musicHandler.postDelayed(new Runnable() {
                @Override public void run() {
                    if (mediaPlayer != null && !isPlaying) {
                        stopMusic();
                        file.delete();
                    }
                }
            }, 5000);
        } catch (Exception e) {
            stopMusic();
            showGlobalTip("播放失败", e.getMessage(), 0);
            file.delete();
        }
    }

    private void playMusic(final String id, final String name, final String artist) {
        // Clean old cache files for other songs
        try {
            File[] files = getContext.getCacheDir().listFiles();
            if (files != null) {
                for (File f : files) {
                    String fn = f.getName();
                    if (fn.startsWith("music_cache_") && fn.endsWith(".mp3") && !fn.equals("music_cache_" + id + ".mp3")) {
                        f.delete();
                    }
                }
            }
        } catch (Exception ignored) {}

        if (isPreparing) { showGlobalTip("提示", "正在准备播放，请稍候", 2); return; }
        if (id.equals(currentPlayingId) && isPaused && mediaPlayer != null) { togglePlayPause(); return; }
        if (id.equals(currentPlayingId) && isPlaying && !isPaused) { showGlobalTip("提示", "正在播放: " + name, 2); return; }

        stopMusic();
        getLyrics(id);
        isPreparing = true;
        showGlobalTip("缓存中", name + " - " + artist, 2);

        final String downloadUrl = "https://music.163.com/song/media/outer/url?id=" + id + ".mp3";
        final File cacheFile = new File(getContext.getCacheDir(), "music_cache_" + id + ".mp3");

        new Thread(new Runnable() {
            @Override public void run() {
                boolean success = false;
                String error = null;
                java.io.InputStream in = null;
                FileOutputStream out = null;
                HttpsURLConnection conn = null;
                try {
                    if (cacheFile.exists()) cacheFile.delete();
                    conn = (HttpsURLConnection) new URL(downloadUrl).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(30000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setInstanceFollowRedirects(false);
                    int code = conn.getResponseCode();
                    if (code == 301 || code == 302 || code == 303) {
                        String loc = conn.getHeaderField("Location");
                        conn.disconnect();
                        if (loc.startsWith("http://")) loc = loc.replace("http://", "https://");
                        conn = (HttpsURLConnection) new URL(loc).openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(15000);
                        conn.setReadTimeout(30000);
                        conn.setInstanceFollowRedirects(true);
                        code = conn.getResponseCode();
                    }
                    if (code != 200) throw new Exception("HTTP " + code);
                    int contentLen = conn.getContentLength();
                    in = conn.getInputStream();
                    out = new FileOutputStream(cacheFile);
                    byte[] buf = new byte[8192];
                    int read;
                    long total = 0;
                    while ((read = in.read(buf)) != -1) {
                        out.write(buf, 0, read);
                        total += read;
                        if (contentLen > 0 && total % 102400 < 8192) {
                            final int pct = (int)(100 * total / contentLen);
                            final String n2 = name;
                            mainHandler.post(new Runnable() {
                                @Override public void run() { showGlobalTip(n2, "缓存中 " + pct + "%", 2); }
                            });
                        }
                    }
                    out.flush();
                    success = true;
                } catch (Exception e) {
                    error = e.getMessage();
                    if (cacheFile.exists()) cacheFile.delete();
                } finally {
                    try { if (in != null) in.close(); } catch (Exception ignored) {}
                    try { if (out != null) out.close(); } catch (Exception ignored) {}
                    try { if (conn != null) conn.disconnect(); } catch (Exception ignored) {}
                }
                final boolean ok = success;
                final String err = error;
                mainHandler.post(new Runnable() {
                    @Override public void run() {
                        isPreparing = false;
                        if (ok && cacheFile.exists() && cacheFile.length() > 0) {
                            playCachedFile(cacheFile, id, name, artist);
                        } else {
                            showGlobalTip("缓存失败", err != null ? err : "未知错误", 0);
                        }
                    }
                });
            }
        }).start();
    }


    private void searchMusic(final String searchKey) {
        musicListContainer.removeAllViews();
        final TextView loading = new TextView(getContext);
        loading.setText("正在请求API...");
        loading.setTextColor(TEXT_COLOR_2);
        loading.setGravity(17);
        loading.setPadding(0, dp(40), 0, 0);
        musicListContainer.addView(loading);
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final HttpURLConnection conn = (HttpURLConnection) new URL(
                            "https://music.163.com/api/search/get?s=" + URLEncoder.encode(searchKey, "UTF-8") + "&type=1&offset=0&total=true&limit=20"
                    ).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
                    conn.setRequestProperty("Referer", "https://music.163.com/");
                    if (conn.getResponseCode() != 200)
                        throw new Exception("HTTP " + conn.getResponseCode());
                    final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    final StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                    br.close();
                    final List<Map<String, String>> songs = parseSongSearchResult(sb.toString());
                    currentPlaylist = new java.util.ArrayList<>(songs);
                    mainHandler.post(new Runnable() {
                        @Override public void run() {
                            musicListContainer.removeAllViews();
                            if (songs.isEmpty()) {
                                final TextView empty = new TextView(getContext);
                                empty.setText("未找到相关音乐");
                                empty.setTextColor(TEXT_COLOR_2);
                                empty.setGravity(17);
                                empty.setPadding(0, dp(20), 0, 0);
                                musicListContainer.addView(empty);
                            } else {
                                for (int i = 0; i < songs.size(); i++) {
                                    final Map<String, String> song = songs.get(i);
                                    addMusicItem(song.get("name"), song.get("artist"), song.get("id"), i);
                                }
                            }
                        }
                    });
                } catch (final Exception e) {
                    mainHandler.post(new Runnable() {
                        @Override public void run() {
                            musicListContainer.removeAllViews();
                            final TextView err = new TextView(getContext);
                            err.setText("加载失败: " + e.getMessage());
                            err.setTextColor(Color.parseColor("#FF6B6B"));
                            err.setGravity(17);
                            err.setPadding(0, dp(20), 0, 0);
                            musicListContainer.addView(err);
                        }
                    });
                }
            }
        }).start();
    }

    private List<Map<String, String>> parseSongSearchResult(final String json) {
        final List<Map<String, String>> result = new ArrayList<>();
        try {
            final JSONObject root = new JSONObject(json);
            if (root.optInt("code", -1) != 200) return result;
            final JSONObject resultObj = root.optJSONObject("result");
            if (resultObj == null) return result;
            final JSONArray songs = resultObj.optJSONArray("songs");
            if (songs == null) return result;
            for (int i = 0; i < songs.length(); i++) {
                final JSONObject song = songs.getJSONObject(i);
                final long idLong = song.optLong("id", 0);
                if (idLong == 0) continue;
                final String id = String.valueOf(idLong);
                final String name = song.optString("name", "");
                final StringBuilder artistSb = new StringBuilder();
                JSONArray artists = song.optJSONArray("artists");

                if (artists == null || artists.length() == 0) {
                    artists = song.optJSONArray("ar");
                }

                if (artists != null) {
                    for (int j = 0; j < artists.length(); j++) {
                        if (j > 0) artistSb.append("/");
                        artistSb.append(artists.getJSONObject(j).optString("name", "未知歌手"));
                    }
                }
                final Map<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("name", name);
                map.put("artist", artistSb.toString());
                result.add(map);
            }
        } catch (Exception e) {
            android.util.Log.e("Search", "parse failed: " + e.getMessage());
        }
        return result;
    }

    public void onMusicSearch(final String s) {
        if (s != null && s.trim().length() != 0) {
            searchMusic(s.trim());
            return;
        }
        showGlobalTip("提示", "请输入搜索内容", 2);
    }

    private void addMusicItem(final String name, final String artist, final String id, final int index) {
        final LinearLayout item = new LinearLayout(getContext);
        item.setOrientation(0);
        item.setGravity(16);
        item.setPadding(dp(16), dp(12), dp(16), dp(12));
        final LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(-1, -2);
        itemLp.setMargins(0, 0, 0, dp(8));
        item.setLayoutParams(itemLp);
        item.setBackground(roundBg(MENU_CARD_COLOR, 12));
        item.setAlpha(0.0f);
        item.setTranslationY((float) dp(20));
        final LinearLayout info = new LinearLayout(getContext);
        info.setOrientation(1);
        info.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        final TextView tvName = new TextView(getContext);
        tvName.setText(name);
        tvName.setTextColor(TEXT_COLOR);
        tvName.setTextSize(15);
        tvName.setTypeface(null, 1);
        tvName.setSingleLine(true);
        tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);
        final TextView tvArtist = new TextView(getContext);
        tvArtist.setText(artist);
        tvArtist.setTextColor(Color.parseColor("#8E8E93"));
        tvArtist.setTextSize(12);
        tvArtist.setPadding(0, dp(4), 0, 0);
        info.addView(tvName);
        info.addView(tvArtist);
        final ImageView btnPlay = new ImageView(getContext);
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
        btnPlay.setColorFilter(Color.parseColor("#34C759"));
        btnPlay.setPadding(dp(8), dp(8), dp(8), dp(8));
        btnPlay.setLayoutParams(new LinearLayout.LayoutParams(dp(40), dp(40)));
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {
                currentPlaylistIndex = index;
                playMusic(id, name, artist);
            }
        });
        item.addView(info);
        item.addView(btnPlay);
        musicListContainer.addView(item);
        item.animate().alpha(1.0f).translationY(0).setDuration(300)
                .setStartDelay(index * 80)
                .setInterpolator(new android.view.animation.DecelerateInterpolator()).start();
    }


    private void loadThumbForFeedback(final ImageView iv, final String path) {
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    final android.graphics.BitmapFactory.Options opts = new android.graphics.BitmapFactory.Options();
                    opts.inSampleSize = 8;
                    final android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeFile(path, opts);
                    mainHandler.post(new Runnable() {
                        @Override public void run() {
                            if (bmp != null) {
                                iv.setImageBitmap(bmp);
                            } else {
                                iv.setImageResource(android.R.drawable.ic_menu_gallery);
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadImagesForFeedback(final LinearLayout container, final ImageView targetImage,
                                       final ImageView placeholderIcon, final TextView placeholderText,
                                       final ImageView deleteBtn, final FrameLayout rootView, final android.view.WindowManager wm) {
        new Thread(new Runnable() {
            @Override public void run() {
                final java.util.ArrayList<ImageItem> list = new java.util.ArrayList<>();
                final String sel = android.provider.MediaStore.Images.Media.DATA + " LIKE ? OR "
                        + android.provider.MediaStore.Images.Media.DATA + " LIKE ? OR "
                        + android.provider.MediaStore.Images.Media.DATA + " LIKE ? OR "
                        + android.provider.MediaStore.Images.Media.DATA + " LIKE ?";
                android.database.Cursor cursor = null;
                try {
                    cursor = getContext.getContentResolver().query(
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{"_id", "_data", "date_added", "_display_name", "_size"},
                            sel,
                            new String[]{"%.jpg", "%.jpeg", "%.png", "%.gif"},
                            "date_added DESC");
                    if (cursor != null && cursor.moveToFirst()) {
                        final int colData = cursor.getColumnIndexOrThrow("_data");
                        final int colName = cursor.getColumnIndexOrThrow("_display_name");
                        final int colSize = cursor.getColumnIndexOrThrow("_size");
                        do {
                            final String imgPath = cursor.getString(colData);
                            final String imgName = cursor.getString(colName);
                            final long imgSize = cursor.getLong(colSize);
                            if (imgSize >= 1024) {
                                list.add(new ImageItem(imgPath, imgName, countPathDepth(imgPath), imgSize));
                            }
                        } while (cursor.moveToNext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) cursor.close();
                }
                java.util.Collections.sort(list, new java.util.Comparator<ImageItem>() {
                    @Override public int compare(ImageItem a, ImageItem b) { return b.depth - a.depth; }
                });
                final int cap = Math.min(list.size(), 70);
                final java.util.ArrayList<ImageItem> finalList = new java.util.ArrayList<>(list.subList(0, cap));
                mainHandler.post(new Runnable() {
                    @Override public void run() {
                        if (finalList.isEmpty()) {
                            final TextView tv = new TextView(getContext);
                            tv.setText("未找到图片");
                            tv.setTextColor(TEXT_COLOR_2);
                            tv.setGravity(17);
                            tv.setPadding(0, dp(50), 0, 0);
                            container.addView(tv);
                            return;
                        }
                        LinearLayout row = null;
                        for (int i = 0; i < finalList.size(); i++) {
                            if (i % 3 == 0) {
                                row = new LinearLayout(getContext);
                                row.setOrientation(0);
                                container.addView(row);
                            }
                            final ImageItem item = finalList.get(i);
                            final FrameLayout cell = new FrameLayout(getContext);
                            final LinearLayout.LayoutParams cellLp = new LinearLayout.LayoutParams(dp(90), dp(90));
                            cellLp.setMargins(dp(4), dp(4), dp(4), dp(4));
                            cell.setLayoutParams(cellLp);
                            final android.widget.ImageView thumb = new android.widget.ImageView(getContext);
                            thumb.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
                            thumb.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
                            thumb.setBackgroundColor(MENU_CARD_COLOR);
                            loadThumbForFeedback(thumb, item.path);
                            cell.addView(thumb);
                            final String folderName;
                            final java.io.File parent = new java.io.File(item.path).getParentFile();
                            final String raw = (parent != null) ? parent.getName() : "";
                            folderName = raw.length() > 6 ? raw.substring(0, 6) : raw;
                            final TextView label = new TextView(getContext);
                            label.setText(folderName);
                            label.setTextColor(-1);
                            label.setTextSize(7);
                            label.setPadding(dp(2), 0, dp(2), 0);
                            label.setBackgroundColor(Color.parseColor("#80000000"));
                            final FrameLayout.LayoutParams labelLp = new FrameLayout.LayoutParams(-2, -2);
                            labelLp.gravity = 83;
                            label.setLayoutParams(labelLp);
                            cell.addView(label);
                            cell.setOnClickListener(new View.OnClickListener() {
                                @Override public void onClick(View v) {
                                    feedbackImagePath = item.path;
                                    try {
                                        final android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeFile(item.path);
                                        if (bmp != null) {
                                            targetImage.setImageBitmap(bmp);
                                            targetImage.setVisibility(View.VISIBLE);
                                            placeholderIcon.setVisibility(View.GONE);
                                            placeholderText.setVisibility(View.GONE);
                                            deleteBtn.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception ignored) {}
                                    try { wm.removeView(rootView); } catch (Exception ignored) {}
                                }
                            });
                            if (row != null) row.addView(cell);
                        }
                    }
                });
            }
        }).start();
    }

    private void showImagePickerForFeedback(final ImageView realImage, final ImageView placeholderIcon,
                                            final TextView placeholderText, final ImageView deleteBtn) {
        final android.view.WindowManager wm = (android.view.WindowManager) getContext.getSystemService("window");
        final FrameLayout rootView = new FrameLayout(getContext);
        rootView.setBackgroundColor(Color.parseColor("#CC000000"));
        final LinearLayout panel = new LinearLayout(getContext);
        panel.setOrientation(1);
        panel.setBackground(glassCard((float) dp(16)));
        final FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(dp(300), dp(400));
        panelLp.gravity = 17;
        panel.setLayoutParams(panelLp);
        final LinearLayout header = new LinearLayout(getContext);
        header.setOrientation(0);
        header.setPadding(dp(16), dp(16), dp(16), dp(16));
        header.setGravity(16);
        final TextView title = new TextView(getContext);
        title.setText("选择截图");
        title.setTextColor(ACCENT_COLOR);
        title.setTextSize(18);
        title.setTypeface(null, 1);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        final android.widget.Button closeBtn = new android.widget.Button(getContext);
        closeBtn.setText("×");
        closeBtn.setTextColor(Color.parseColor("#8E8E93"));
        closeBtn.setBackgroundColor(0);
        closeBtn.setTextSize(20);
        header.addView(title);
        header.addView(closeBtn);
        panel.addView(header);
        final View divider = new View(getContext);
        divider.setBackgroundColor(MENU_CARD_BORDER);
        divider.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(1)));
        panel.addView(divider);
        final LinearLayout gridContainer = new LinearLayout(getContext);
        gridContainer.setOrientation(1);
        final android.widget.ScrollView scrollView = new android.widget.ScrollView(getContext);
        scrollView.addView(gridContainer);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));
        panel.addView(scrollView);
        rootView.addView(panel);
        int windowType = (android.os.Build.VERSION.SDK_INT >= 26) ? 2038 : 2002;
        final android.view.WindowManager.LayoutParams wlp = new android.view.WindowManager.LayoutParams(-1, -1, windowType, 8, -3);
        try {
            wm.addView(rootView, wlp);
            loadImagesForFeedback(gridContainer, realImage, placeholderIcon, placeholderText, deleteBtn, rootView, wm);
            final Runnable dismiss = new Runnable() {
                @Override public void run() {
                    try { wm.removeView(rootView); } catch (Exception ignored) {}
                }
            };
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { dismiss.run(); }
            });
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (v == rootView) dismiss.run();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showFeedbackInputDialog(final android.widget.Button triggerBtn, final TextView previewText) {
        final android.view.WindowManager wm = (android.view.WindowManager) getContext.getSystemService("window");
        final FrameLayout rootFrame = new FrameLayout(getContext);
        rootFrame.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        final View overlay = new View(getContext);
        overlay.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        overlay.setBackgroundColor(Color.parseColor("#80000000"));
        overlay.setAlpha(0.0f);
        final int menuW = (mExpanded.getWidth() > 0) ? mExpanded.getWidth() : dp(MENU_WIDTH);
        final int menuH = (mExpanded.getHeight() > 0) ? mExpanded.getHeight() : dp(MENU_HEIGHT);
        final int popW = (int)(menuW * 0.9f);
        final int minH = dp(220);
        int popH = (int)(menuH * 0.65f);
        if (popH < minH) popH = minH;
        final int titleH = dp(40);
        final int counterH = dp(24);
        final int btnH = dp(48);
        int inputH = popH - titleH - counterH - btnH - dp(64);
        if (inputH < dp(60)) inputH = dp(60);
        final LinearLayout popup = new LinearLayout(getContext);
        popup.setOrientation(1);
        popup.setBackground(glassCard((float) dp(16)));
        popup.setPadding(dp(16), dp(16), dp(16), dp(16));
        popup.setAlpha(0.0f);
        popup.setScaleX(0.8f);
        popup.setScaleY(0.8f);
        final FrameLayout.LayoutParams popupLp = new FrameLayout.LayoutParams(popW, popH);
        popupLp.gravity = 17;
        popup.setLayoutParams(popupLp);
        final TextView titleTv = new TextView(getContext);
        titleTv.setText("\uD83D\uDCDD \u63CF\u8FF0\u95EE\u9898");
        titleTv.setTextColor(ACCENT_COLOR);
        titleTv.setTextSize(18);
        titleTv.setTypeface(null, 1);
        titleTv.setGravity(17);
        titleTv.setLayoutParams(new LinearLayout.LayoutParams(-1, titleH));
        popup.addView(titleTv);
        final android.widget.EditText input = new android.widget.EditText(getContext);
        input.setHint("请描述：问题现象、触发条件、期望结果");
        input.setTextColor(TEXT_COLOR_2);
        input.setHintTextColor(TEXT_COLOR_SECONDARY);
        final android.graphics.drawable.GradientDrawable inputBg = new android.graphics.drawable.GradientDrawable();
        inputBg.setColor(MENU_CARD_COLOR);
        inputBg.setStroke(dp(1), MENU_CARD_BORDER);
        inputBg.setCornerRadius((float) dp(12));
        input.setBackground(inputBg);
        input.setPadding(dp(16), dp(12), dp(16), dp(12));
        input.setTextSize(14);
        input.setGravity(48);
        final LinearLayout.LayoutParams inputLp = new LinearLayout.LayoutParams(-1, inputH);
        inputLp.setMargins(0, 0, 0, dp(8));
        input.setLayoutParams(inputLp);
        if (!feedbackContent.isEmpty()) {
            input.setText(feedbackContent);
            input.setSelection(feedbackContent.length());
        }
        popup.addView(input);
        final TextView charCount = new TextView(getContext);
        charCount.setText((feedbackContent.isEmpty() ? "0" : feedbackContent.length()) + "/500");
        charCount.setTextColor(feedbackContent.length() > 500 ? Color.parseColor("#FF6B6B") : TEXT_COLOR_SECONDARY);
        charCount.setTextSize(12);
        charCount.setGravity(5);
        final LinearLayout.LayoutParams counterLp = new LinearLayout.LayoutParams(-1, counterH);
        counterLp.setMargins(0, 0, dp(8), dp(8));
        charCount.setLayoutParams(counterLp);
        popup.addView(charCount);
        input.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void afterTextChanged(android.text.Editable e) {}
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                charCount.setText(s.length() + "/500");
                charCount.setTextColor(s.length() > 500 ? Color.parseColor("#FF6B6B") : TEXT_COLOR_SECONDARY);
            }
        });
        final LinearLayout btnRow = new LinearLayout(getContext);
        btnRow.setOrientation(0);
        btnRow.setLayoutParams(new LinearLayout.LayoutParams(-1, btnH));
        final android.widget.Button cancelBtn = new android.widget.Button(getContext);
        cancelBtn.setText("取消");
        cancelBtn.setAllCaps(false);
        cancelBtn.setTextColor(Color.parseColor("#8E8E93"));
        cancelBtn.setBackground(roundBg(MENU_CARD_COLOR, (float) dp(10)));
        cancelBtn.setTextSize(14);
        final LinearLayout.LayoutParams cancelLp = new LinearLayout.LayoutParams(0, -1, 1.0f);
        cancelLp.setMarginEnd(dp(8));
        cancelBtn.setLayoutParams(cancelLp);
        final android.widget.Button confirmBtn = new android.widget.Button(getContext);
        confirmBtn.setText("确定");
        confirmBtn.setAllCaps(false);
        confirmBtn.setTextColor(Color.parseColor("#1C1C1E"));
        final android.graphics.drawable.GradientDrawable confirmBg = new android.graphics.drawable.GradientDrawable();
        confirmBg.setColor(ACCENT_COLOR);
        confirmBg.setCornerRadius((float) dp(10));
        confirmBtn.setBackground(confirmBg);
        confirmBtn.setTextSize(14);
        confirmBtn.setTypeface(null, 1);
        final LinearLayout.LayoutParams confirmLp = new LinearLayout.LayoutParams(0, -1, 1.0f);
        confirmLp.setMarginStart(dp(8));
        confirmBtn.setLayoutParams(confirmLp);
        btnRow.addView(cancelBtn);
        btnRow.addView(confirmBtn);
        popup.addView(btnRow);
        rootFrame.addView(overlay);
        rootFrame.addView(popup);
        final android.widget.PopupWindow pw = new android.widget.PopupWindow(rootFrame, -1, -1, true);
        pw.setBackgroundDrawable(null);
        pw.setOutsideTouchable(false);
        pw.setFocusable(true);
        pw.setSoftInputMode(16);
        try {
            pw.showAtLocation(mRootContainer, 17, 0, 0);
            final Runnable[] dismissHolder = { null };
            dismissHolder[0] = new Runnable() {
                @Override public void run() {
                    final android.view.inputmethod.InputMethodManager imm =
                            (android.view.inputmethod.InputMethodManager) getContext.getSystemService("input_method");
                    if (imm != null) imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                    overlay.animate().alpha(0.0f).setDuration(150).start();
                    popup.animate().alpha(0.0f).scaleX(0.8f).scaleY(0.8f).setDuration(200)
                            .withEndAction(new Runnable() {
                                @Override public void run() { pw.dismiss(); }
                            }).start();
                }
            };
            overlay.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { dismissHolder[0].run(); }
            });
            overlay.animate().alpha(1.0f).setDuration(200).start();
            popup.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(300)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                    .withEndAction(new Runnable() {
                        @Override public void run() {
                            input.requestFocus();
                            mainHandler.postDelayed(new Runnable() {
                                @Override public void run() {
                                    final android.view.inputmethod.InputMethodManager imm =
                                            (android.view.inputmethod.InputMethodManager) getContext.getSystemService("input_method");
                                    if (imm != null) imm.showSoftInput(input, 1);
                                }
                            }, 300);
                        }
                    }).start();
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { dismissHolder[0].run(); }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    final String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        android.widget.Toast.makeText(getContext, "请输入内容", 0).show();
                        return;
                    }
                    feedbackContent = text;
                    triggerBtn.setText("\uD83D\uDCDD \u5DF2\u8F93\u5165\u95EE\u9898\u63CF\u8FF0");
                    final String preview = text.length() > 30 ? text.substring(0, 30) + "..." : text;
                    previewText.setText(preview);
                    previewText.setVisibility(View.VISIBLE);
                    dismissHolder[0].run();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initFeedbackPage(final LinearLayout container) {
        container.setPadding(dp(16), dp(16), dp(16), dp(16));
        container.setBackground(roundBg(MENU_BG_COLOR, 0));
        // --- content input card ---
        final LinearLayout contentCard = new LinearLayout(getContext);
        contentCard.setOrientation(1);
        contentCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        contentCard.setBackground(glassCard((float) dp(16)));
        final LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(-1, -2);
        cardLp.setMargins(0, 0, 0, dp(12));
        contentCard.setLayoutParams(cardLp);
        final android.widget.Button contentBtn = new android.widget.Button(getContext);
        contentBtn.setText("\uD83D\uDCDD \u70B9\u51FB\u8F93\u5165\u95EE\u9898\u63CF\u8FF0");
        contentBtn.setAllCaps(false);
        contentBtn.setTextColor(ACCENT_COLOR);
        contentBtn.setBackground(darkFillBorder());
        contentBtn.setPadding(dp(12), dp(8), dp(8), dp(12));
        contentBtn.setGravity(19);
        final TextView previewText = new TextView(getContext);
        previewText.setText("暂无内容");
        previewText.setTextColor(Color.parseColor("#34C759"));
        previewText.setTextSize(14);
        previewText.setPadding(dp(12), dp(8), dp(12), dp(8));
        previewText.setVisibility(View.GONE);
        contentBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showFeedbackInputDialog(contentBtn, previewText);
            }
        });
        contentCard.addView(contentBtn);
        contentCard.addView(previewText);
        container.addView(contentCard);
        // --- image card ---
        final LinearLayout imageCard = new LinearLayout(getContext);
        imageCard.setOrientation(1);
        imageCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        imageCard.setBackground(glassCard((float) dp(16)));
        final LinearLayout.LayoutParams imgCardLp = new LinearLayout.LayoutParams(-1, -2);
        imgCardLp.setMargins(0, 0, 0, dp(12));
        imageCard.setLayoutParams(imgCardLp);
        final FrameLayout imgFrame = new FrameLayout(getContext);
        imgFrame.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(180)));
        imgFrame.setBackground(roundBg(MENU_CARD_COLOR, (float) dp(12)));
        final android.widget.ImageView placeholderIcon = new android.widget.ImageView(getContext);
        placeholderIcon.setImageResource(android.R.drawable.ic_menu_gallery);
        placeholderIcon.setColorFilter(Color.parseColor("#666666"));
        placeholderIcon.setLayoutParams(new FrameLayout.LayoutParams(dp(48), dp(48), 17));
        imgFrame.addView(placeholderIcon);
        final TextView placeholderText = new TextView(getContext);
        placeholderText.setText("点击上传截图");
        placeholderText.setTextColor(Color.parseColor("#666666"));
        placeholderText.setTextSize(12);
        final FrameLayout.LayoutParams phTvLp = new FrameLayout.LayoutParams(-2, -2);
        phTvLp.gravity = 17;
        ((android.view.ViewGroup.MarginLayoutParams) phTvLp).topMargin = dp(32);
        placeholderText.setLayoutParams(phTvLp);
        imgFrame.addView(placeholderText);
        final android.widget.ImageView realImage = new android.widget.ImageView(getContext);
        realImage.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        realImage.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        realImage.setVisibility(View.GONE);
        imgFrame.addView(realImage);
        final android.widget.ImageView deleteBtn = new android.widget.ImageView(getContext);
        deleteBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        deleteBtn.setColorFilter(-1);
        deleteBtn.setBackground(roundBg(Color.parseColor("#FF6B6B"), (float) dp(12)));
        deleteBtn.setPadding(dp(8), dp(8), dp(8), dp(8));
        final FrameLayout.LayoutParams delLp = new FrameLayout.LayoutParams(dp(36), dp(36));
        delLp.gravity = 53;
        delLp.setMargins(0, dp(8), dp(8), 0);
        deleteBtn.setLayoutParams(delLp);
        deleteBtn.setVisibility(View.GONE);
        imgFrame.addView(deleteBtn);
        imgFrame.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (feedbackImagePath == null) {
                    showImagePickerForFeedback(realImage, placeholderIcon, placeholderText, deleteBtn);
                }
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                feedbackImagePath = null;
                realImage.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
                placeholderIcon.setVisibility(View.VISIBLE);
                placeholderText.setVisibility(View.VISIBLE);
            }
        });
        imageCard.addView(imgFrame);
        container.addView(imageCard);
        // --- submit button ---
        final android.widget.Button submitBtn = new android.widget.Button(getContext);
        submitBtn.setText("提交反馈");
        submitBtn.setAllCaps(false);
        submitBtn.setTextColor(Color.parseColor("#1C1C1E"));
        submitBtn.setTextSize(16);
        submitBtn.setTypeface(null, 1);
        final android.graphics.drawable.GradientDrawable submitBg = new android.graphics.drawable.GradientDrawable();
        submitBg.setColor(ACCENT_COLOR);
        submitBg.setCornerRadius((float) dp(16));
        submitBtn.setBackground(submitBg);
        submitBtn.setPadding(dp(20), dp(16), dp(20), dp(16));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showSubmitConfirmDialog("反馈箱", buildFeedbackEmailContent()); }
        });
        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, android.view.MotionEvent e) {
                if (e.getAction() == 0) v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                else if (e.getAction() == 1) v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150)
                        .setInterpolator(new android.view.animation.OvershootInterpolator(2.0f)).start();
                return false;
            }
        });
        container.addView(submitBtn);
        this.feedbackMod = container;
    }

    private void initMusicPage(final LinearLayout container) {
        container.setPadding(dp(12), dp(8), dp(12), dp(12));
        container.setOrientation(1);
        // --- search bar ---
        final LinearLayout searchBar = new LinearLayout(getContext);
        searchBar.setOrientation(0);
        searchBar.setGravity(16);
        searchBar.setPadding(dp(12), dp(10), dp(12), dp(10));
        searchBar.setBackground(roundBg(MENU_CARD_COLOR, (float) dp(12)));
        final LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(-1, -2);
        searchLp.setMargins(0, 0, 0, dp(8));
        searchBar.setLayoutParams(searchLp);
        final android.widget.ImageView searchIcon = new android.widget.ImageView(getContext);
        searchIcon.setImageResource(android.R.drawable.ic_menu_search);
        searchIcon.setColorFilter(Color.parseColor("#34C759"));
        searchIcon.setLayoutParams(new LinearLayout.LayoutParams(dp(20), dp(20)));
        final TextView searchHint = new TextView(getContext);
        searchHint.setText("点击搜索音乐");
        searchHint.setTextColor(Color.parseColor("#8E8E93"));
        searchHint.setTextSize(14);
        final LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(0, -2, 1.0f);
        hintLp.setMargins(dp(8), 0, 0, 0);
        searchHint.setLayoutParams(hintLp);
        final android.widget.ImageView arrowIcon = new android.widget.ImageView(getContext);
        arrowIcon.setImageResource(android.R.drawable.arrow_up_float);
        arrowIcon.setColorFilter(Color.parseColor("#34C759"));
        arrowIcon.setLayoutParams(new LinearLayout.LayoutParams(dp(20), dp(20)));
        searchBar.addView(searchIcon);
        searchBar.addView(searchHint);
        searchBar.addView(arrowIcon);
        searchBar.setClickable(true);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext);
                final android.widget.EditText edit = new android.widget.EditText(getContext);
                edit.setHint("输入歌曲名或歌手");
                edit.setTextColor(android.graphics.Color.BLACK);
                edit.setHintTextColor(Color.parseColor("#8E8E93"));
                builder.setTitle("搜索音乐");
                builder.setView(edit);
                builder.setPositiveButton("搜索", new android.content.DialogInterface.OnClickListener() {
                    @Override public void onClick(android.content.DialogInterface dialog, int which) {
                        final String q = edit.getText().toString().trim();
                        if (q.length() > 0) {
                            searchHint.setText(q);
                            searchHint.setTextColor(TEXT_COLOR_2);
                            onMusicSearch(q);
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                if (overlayRequired) {
                    final android.app.AlertDialog dlg = builder.create();
                    final android.view.Window win = dlg.getWindow();
                    win.setType((android.os.Build.VERSION.SDK_INT >= 26) ? 2038 : 2002);
                    dlg.show();
                } else {
                    builder.show();
                }
            }
        });
        container.addView(searchBar);
        // --- song info row ---
        final LinearLayout songInfo = new LinearLayout(getContext);
        songInfo.setOrientation(1);
        songInfo.setGravity(17);
        songInfo.setPadding(0, dp(8), 0, dp(8));
        tvCurrentSong = new TextView(getContext);
        tvCurrentSong.setText("未在播放");
        tvCurrentSong.setTextColor(TEXT_COLOR);
        tvCurrentSong.setTextSize(15);
        tvCurrentSong.setTypeface(null, 1);
        tvCurrentSong.setGravity(17);
        tvCurrentSong.setSingleLine(true);
        tvCurrentSong.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvCurrentArtist = new TextView(getContext);
        tvCurrentArtist.setText("点击歌曲开始播放");
        tvCurrentArtist.setTextColor(Color.parseColor("#8E8E93"));
        tvCurrentArtist.setTextSize(12);
        tvCurrentArtist.setGravity(17);
        tvCurrentArtist.setPadding(0, dp(4), 0, 0);
        songInfo.addView(tvCurrentSong);
        songInfo.addView(tvCurrentArtist);
        container.addView(songInfo);
        // --- lyrics card ---
        final LinearLayout lyricsCard = new LinearLayout(getContext);
        lyricsCard.setOrientation(1);
        lyricsCard.setPadding(dp(16), dp(16), dp(16), dp(16));
        lyricsCard.setBackground(roundBg(MENU_CARD_COLOR, (float) dp(16)));
        final LinearLayout.LayoutParams lyricsLp = new LinearLayout.LayoutParams(-1, -2);
        lyricsLp.setMargins(0, 0, 0, dp(8));
        lyricsCard.setLayoutParams(lyricsLp);
        final TextView lyricsLabel = new TextView(getContext);
        lyricsLabel.setText("歌词");
        lyricsLabel.setTextColor(Color.parseColor("#34C759"));
        lyricsLabel.setTextSize(12);
        lyricsLabel.setTypeface(null, 1);
        lyricsLabel.setPadding(0, 0, 0, dp(8));
        lyricsCard.addView(lyricsLabel);
        tvLyricCurrent = new TextView(getContext);
        tvLyricCurrent.setText("等待播放...");
        tvLyricCurrent.setTextColor(Color.parseColor("#34C759"));
        tvLyricCurrent.setTextSize(18);
        tvLyricCurrent.setTypeface(null, 1);
        tvLyricCurrent.setGravity(17);
        tvLyricCurrent.setSingleLine(false);
        tvLyricCurrent.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvLyricCurrent.setPadding(0, dp(6), 0, dp(6));
        lyricsCard.addView(tvLyricCurrent);
        container.addView(lyricsCard);
        // --- controls card ---
        musicControlContainer = new LinearLayout(getContext);
        musicControlContainer.setOrientation(1);
        musicControlContainer.setPadding(dp(16), dp(12), dp(16), dp(12));
        musicControlContainer.setBackground(roundBg(MENU_CARD_COLOR, (float) dp(16)));
        final LinearLayout.LayoutParams ctrlLp = new LinearLayout.LayoutParams(-1, -2);
        ctrlLp.setMargins(0, 0, 0, dp(8));
        musicControlContainer.setLayoutParams(ctrlLp);
        // seekbar row
        final LinearLayout seekRow = new LinearLayout(getContext);
        seekRow.setOrientation(0);
        seekRow.setGravity(16);
        tvCurrentTime = new TextView(getContext);
        tvCurrentTime.setText("0:00");
        tvCurrentTime.setTextColor(Color.parseColor("#8E8E93"));
        tvCurrentTime.setTextSize(11);
        seekBarProgress = new android.widget.SeekBar(getContext);
        final LinearLayout.LayoutParams seekLp = new LinearLayout.LayoutParams(0, -2, 1.0f);
        seekLp.setMargins(dp(8), 0, dp(8), 0);
        seekBarProgress.setLayoutParams(seekLp);
        seekBarProgress.setMax(1000);
        seekBarProgress.getThumb().setColorFilter(SeekBarProgressColor, android.graphics.PorterDuff.Mode.SRC_IN);
        seekBarProgress.getProgressDrawable().setColorFilter(SeekBarProgressColor, android.graphics.PorterDuff.Mode.SRC_IN);
        seekBarProgress.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(android.widget.SeekBar sb, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null && isPlaying) {
                    tvCurrentTime.setText(formatTime((int)(progress / 1000.0f * mediaPlayer.getDuration())));
                }
            }
            @Override public void onStartTrackingTouch(android.widget.SeekBar sb) { isSeeking = true; }
            @Override public void onStopTrackingTouch(android.widget.SeekBar sb) {
                isSeeking = false;
                if (mediaPlayer != null && isPlaying) {
                    mediaPlayer.seekTo((int)(sb.getProgress() / 1000.0f * mediaPlayer.getDuration()));
                }
            }
        });
        tvTotalTime = new TextView(getContext);
        tvTotalTime.setText("0:00");
        tvTotalTime.setTextColor(Color.parseColor("#8E8E93"));
        tvTotalTime.setTextSize(11);
        seekRow.addView(tvCurrentTime);
        seekRow.addView(seekBarProgress);
        seekRow.addView(tvTotalTime);
        musicControlContainer.addView(seekRow);
        // buttons row
        final LinearLayout btnRow = new LinearLayout(getContext);
        btnRow.setOrientation(0);
        btnRow.setGravity(17);
        btnRow.setPadding(0, dp(8), 0, 0);
        btnPrev = new android.widget.ImageView(getContext);
        btnPrev.setImageResource(android.R.drawable.ic_media_rew);
        btnPrev.setColorFilter(-1);
        btnPrev.setPadding(dp(12), dp(8), dp(12), dp(8));
        btnPrev.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { playPrevious(); }
        });
        btnPlayPause = new android.widget.ImageView(getContext);
        btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        btnPlayPause.setColorFilter(-1);
        btnPlayPause.setBackground(roundBg(Color.parseColor("#34C759"), (float) dp(28)));
        btnPlayPause.setPadding(dp(14), dp(14), dp(14), dp(14));
        final LinearLayout.LayoutParams playLp = new LinearLayout.LayoutParams(dp(56), dp(56));
        playLp.setMargins(dp(24), 0, dp(24), 0);
        btnPlayPause.setLayoutParams(playLp);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { togglePlayPause(); }
        });
        btnNext = new android.widget.ImageView(getContext);
        btnNext.setImageResource(android.R.drawable.ic_media_ff);
        btnNext.setColorFilter(-1);
        btnNext.setPadding(dp(12), dp(8), dp(12), dp(8));
        btnNext.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { playNext(); }
        });
        btnRow.addView(btnPrev);
        btnRow.addView(btnPlayPause);
        btnRow.addView(btnNext);
        musicControlContainer.addView(btnRow);
        container.addView(musicControlContainer);
        // --- search results card ---
        final LinearLayout resultsCard = new LinearLayout(getContext);
        resultsCard.setOrientation(1);
        resultsCard.setPadding(dp(12), dp(12), dp(12), dp(12));
        resultsCard.setBackground(roundBg(MENU_CARD_COLOR, (float) dp(16)));
        final LinearLayout.LayoutParams resLp = new LinearLayout.LayoutParams(-1, -2);
        resLp.setMargins(0, 0, 0, dp(8));
        resultsCard.setLayoutParams(resLp);
        final TextView resTitle = new TextView(getContext);
        resTitle.setText("搜索结果");
        resTitle.setTextColor(TEXT_COLOR);
        resTitle.setTextSize(14);
        resTitle.setTypeface(null, 1);
        resTitle.setPadding(dp(4), dp(4), dp(4), dp(8));
        resultsCard.addView(resTitle);
        musicListContainer = new LinearLayout(getContext);
        musicListContainer.setOrientation(1);
        resultsCard.addView(musicListContainer);
        final TextView emptyHint = new TextView(getContext);
        emptyHint.setText("输入关键词搜索音乐");
        emptyHint.setTextColor(Color.parseColor("#8E8E93"));
        emptyHint.setTextSize(13);
        emptyHint.setGravity(17);
        emptyHint.setPadding(0, dp(30), 0, dp(30));
        musicListContainer.addView(emptyHint);
        container.addView(resultsCard);
        this.modsMusic = container;
    }

    private class ImageItem {
        String path;
        String name;
        int depth;
        long size;
        ImageItem(String path, String name, int depth, long size) {
            this.path = path;
            this.name = name;
            this.depth = depth;
            this.size = size;
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
