package irene.window.algui.Tools;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import android.content.ComponentName;
/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/23 15:26
 * @Describe 系统工具
 */
public class SystemTool {

    public static final String TAG = "SystemTool";

    //网络连接判断
    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            return info.isConnected();
        } else {
            return false;
        }
    }
    
    //是否为MIUI系统
    public static boolean isMIUI() {
        String manufacturer = Build.MANUFACTURER;
        String brand = Build.BRAND;
        String systemProperty = getSystemProperty("ro.miui.ui.version.name"); // 获取 MIUI 版本号
        return !TextUtils.isEmpty(systemProperty) || "Xiaomi".equalsIgnoreCase(manufacturer) || "Xiaomi".equalsIgnoreCase(brand);
    }

    private static String getSystemProperty(String key) {
        String result = "";
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class);
            result = (String) method.invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    //打开隐藏MIUI性能模式
    public static boolean OpenMIUIPerformanceMode(Context context) {
        if(!isMIUI()){
            return false;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings",
                                              "com.android.settings.fuelgauge.PowerModeSettings"));
        context.startActivity(intent);
        return true;
    }
    
    /*检测手机是否有ROOT*/
    public static boolean inspectRootPermission() {
        File file=null;
        String[]paths={"/system/bin/","/system/xbin/"};
        try {
            for (String path:paths) {
                file = new File(path + "su");
                if (file.exists() && file.canExecute()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return false;
    }

    //获取当前SELinux处于什么模式
    public static String getSELinuxMode() {
        String mode = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String selinuxMode = System.getProperty("selinux.mode", "");
            if ("enforcing".equals(selinuxMode)) {
                mode = "强制模式";
            } else if ("permissive".equals(selinuxMode)) {
                mode = "宽容模式";
            }
        } else {
            String legacyMode = getLegacySELinuxMode();
            if ("Enforcing".equals(legacyMode)) {
                mode = "强制模式";
            } else if ("Permissive".equals(legacyMode)) {
                mode = "宽容模式";
            }
        }
        return mode;
    }
    private static String getLegacySELinuxMode() {
        try {
            Process process = Runtime.getRuntime().exec("getenforce");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            reader.close();
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 检测 SELinux 是否处于宽容模式
     * @return true 表示处于宽容模式，false 表示未处于宽容模式
     */
    public static boolean isSELinuxPermissive() {
        String status = getSELinuxStatus();
        return status != null && status.equals("Permissive");
    }

    /**
     * 获取当前 SELinux 状态
     * @return 返回字符串 "Enforcing" 表示强制模式，"Permissive" 表示宽容模式，"Disabled" 表示已禁用，null 表示获取失败
     */
    private static String getSELinuxStatus() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/fs/selinux/enforce"));
            String line = reader.readLine();
            reader.close();
            return line.equals("1") ? "Enforcing" : "Permissive";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    //获取内核版本
    public static String getKernelVersion() {
        String kernelVersion = "";
        try {
            Process process = new ProcessBuilder()
                .command("/system/bin/uname", "-a")
                .redirectErrorStream(true)
                .start();
            process.waitFor();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                kernelVersion += line + "\n";
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return kernelVersion;
    }


    // 获取网络状态
    public static String getNetworkStatus(Context context) {
        if (!AppPermissionTool.isAndroidManifestPermissionExist(context, "android.permission.ACCESS_NETWORK_STATE")) {
            return "程序清单xml文件中没有声明android.permission.ACCESS_NETWORK_STATE权限！";
        }
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            switch (activeNetworkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return "Wifi Connected";
                case ConnectivityManager.TYPE_MOBILE:
                    switch (activeNetworkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            return "2G GPRS Connected";
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                            return "2G EDGE Connected";
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            return "2G CDMA Connected";
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            return "2G 1xRTT Connected";
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "2G IDEN Connected";
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                            return "3G UMTS Connected";
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            return "3G EVDO_0 Connected";
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            return "3G EVDO_A Connected";
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                            return "3G HSDPA Connected";
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                            return "3G HSUPA Connected";
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                            return "3G HSPA Connected";
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            return "3G EVDO_B Connected";
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                            return "3G EHRPD Connected";
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G HSPAP Connected";
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G LTE Connected";
                        case TelephonyManager.NETWORK_TYPE_NR:
                            return "5G NR Connected";
                        default:
                            return "Unknown Network Connected";
                    }
                default:
                    return "Unknown Network Connected";
            }
        } else {
            return "No Network Connection";
        }
    }
    // 获取网络速度
    /**
     * 得到网络速度
     * @param context
     * @return
     */
    /* public static String  getNetSpeed(Context context) {


     String netSpeed = "0 kb/s";
     long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
     long nowTimeStamp = System.currentTimeMillis();
     long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

     lastTimeStamp = nowTimeStamp;
     lastTotalRxBytes = nowTotalRxBytes;
     netSpeed  = String.valueOf(speed) + " kb/s";
     return  netSpeed;
     }*/
    /*public static String getNetworkSpeed(Context context) {
     ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
     NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
     if (activeNetworkInfo != null && activeNetworkInfo.isConnected() && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
     WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
     WifiInfo wifiInfo = wifiManager.getConnectionInfo();
     int speedMbps = wifiInfo.getLinkSpeed();
     return String.format(Locale.getDefault(), "Wifi Speed: %d Mbps", speedMbps);
     } else {
     return "";
     }
     }*/














    // 获取电池剩余电量
    public static int getBatteryLevel(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (int) ((level / (float) scale) * 100);
    }

    // 格式化手机内存(方便阅读)
    public static String formatMemorySize(long bytes) {
        if (bytes <= 0) {
            return "0 B";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));

        return String.format("%.2f %s", bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    //获取手机总内存 参数上下文，是否格式化
    public static String getTotalMemory(Context context, boolean isFormat) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        if (isFormat) {
            return formatMemorySize(memoryInfo.totalMem);
        } else {
            return memoryInfo.totalMem + "";
        }


    }
    // 获取手机可用内存 参数上下文，是否格式化
    public static String getMemoryUsage(Context context, boolean isFormat) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        if (isFormat) {
            return formatMemorySize(memoryInfo.availMem);
        } else {
            return memoryInfo.availMem + "";
        }

    }


    //获取系统总存储 参数 是否格式化
    public static String getTotalStorage(boolean isFormat) {
        File path = Environment.getDataDirectory();
        long totalStorage = path.getTotalSpace();
        if (isFormat) {
            return formatMemorySize(totalStorage);
        } else {
            return totalStorage + "";
        }
    }

    //获取系统可用存储 参数 是否格式化
    public static String getAvailableStorage(boolean isFormat) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File externalDir = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(externalDir.getPath());
            long blockSize = statFs.getBlockSizeLong();
            long availableBlocks = statFs.getAvailableBlocksLong();
            if (isFormat) {
                return formatMemorySize(blockSize * availableBlocks);
            } else {
                return (blockSize * availableBlocks) + "";
            }

        }
        return "null";
    }



    //获取屏幕分辨率
    public static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        return widthPixels + "x" + heightPixels + "px";
    }
    //获取屏幕真实dp值
    public static int getRealScreenDP(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        int widthDp = (int) (displayMetrics.widthPixels / density);
        int heightDp = (int) (displayMetrics.heightPixels / density);
        return Math.min(widthDp, heightDp);
    }


    /**
     * 打印手机信息
     * 需要 <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     *
     * @return
     */
    public static String printSystemInfo() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        StringBuilder sb = new StringBuilder();
        sb.append("_______  系统信息  ").append(time).append(" ______________");
        sb.append("\nID                 :").append(Build.ID); // Either  a changelist number, or a label like "M4-rc20".
        sb.append("\nBRAND              :").append(Build.BRAND); //品牌名 如 Xiaomi
        sb.append("\nMODEL              :").append(Build.MODEL); //手机型号
        sb.append("\nRELEASE            :").append(Build.VERSION.RELEASE); //frimware版本(系统版本) 如：2.1-update1
        sb.append("\nSDK                :").append(Build.VERSION.SDK); //sdk版本号

        sb.append("\n_______ OTHER _______");
        sb.append("\nBOARD              :").append(Build.BOARD); //基板名 如 MSM8974
        sb.append("\nPRODUCT            :").append(Build.PRODUCT); //The name of the overall product.
        sb.append("\nDEVICE             :").append(Build.DEVICE); //品牌型号名，如小米4对应cancro
        sb.append("\nFINGERPRINT        :").append(Build.FINGERPRINT); //包含制造商，设备名，系统版本等诸多信息 如  Xiaomi/cancro_wc_lte/cancro:6.0.1/MMB29M/V8.1.3.0.MXDCNDI:user/release-keys
        sb.append("\nHOST               :").append(Build.HOST); // 如 c3-miui-ota-bd43
        sb.append("\nTAGS               :").append(Build.TAGS); //Comma-separated tags describing the build, like "unsigned,debug".
        sb.append("\nTYPE               :").append(Build.TYPE); //The type of build, like "user" or "eng".
        sb.append("\nTIME               :").append(Build.TIME); //当前时间，毫秒值
        sb.append("\nINCREMENTAL        :").append(Build.VERSION.INCREMENTAL);

        sb.append("\n_______ CUPCAKE-3 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sb.append("\nDISPLAY            :").append(Build.DISPLAY); // 如 MMB29M
        }

        sb.append("\n_______ DONUT-4 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            sb.append("\nSDK_INT            :").append(Build.VERSION.SDK_INT);
            sb.append("\nMANUFACTURER       :").append(Build.MANUFACTURER); // The manufacturer of the product/hardware. 如 Xiaomi
            sb.append("\nBOOTLOADER         :").append(Build.BOOTLOADER); //The system bootloader version number. 如
            sb.append("\nCPU_ABI            :").append(Build.CPU_ABI); // 如 armeabi-v7a
            sb.append("\nCPU_ABI2           :").append(Build.CPU_ABI2); // 如 armeabi
            sb.append("\nHARDWARE           :").append(Build.HARDWARE); // The name of the hardware (from the kernel command line or /proc). 如 qcom
            sb.append("\nUNKNOWN            :").append(Build.UNKNOWN); // Value used for when a build property is unknown.
            sb.append("\nCODENAME           :").append(Build.VERSION.CODENAME);
        }

        sb.append("\n_______ GINGERBREAD-9 _______");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sb.append("\nSERIAL             :").append(Build.SERIAL); // A hardware serial number, if available. 如 abcdefgh
        }
        return sb.toString();
    }




    /**
     * 获得IP地址，分为两种情况，一是wifi下，二是移动网络下，得到的ip地址是不一样的
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //调用方法将int转换为地址字符串
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }
    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
            ((ip >> 8) & 0xFF) + "." +
            ((ip >> 16) & 0xFF) + "." +
            (ip >> 24 & 0xFF);
    }



    /**
     * 返回屏幕的宽度
     */
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
    /**
     * 返回包括虚拟键在内的总的屏幕高度
     * 即使虚拟按键显示着，也会加上虚拟按键的高度
     */
    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }



    /**
     * 是否为鸿蒙系统
     * @return true为鸿蒙系统
     */
    public static boolean isHarmonyOs() {
        try {
            Class<?> buildExClass = Class.forName("com.huawei.system.BuildEx");
            Object osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass);
            return "Harmony".equalsIgnoreCase(osBrand.toString());
        } catch (Throwable x) {
            return false;
        }
    }
    /**
     * 获取鸿蒙系统版本号
     * @return 版本号
     */
    public static String getHarmonyVersion() {
        return getProp("hw_sc.build.platform.version", "");
    }

    /**
     * 获取属性
     * @param property
     * @param defaultValue
     * @return
     */
    private static String getProp(String property, String defaultValue) {
        try {
            Class spClz = Class.forName("android.os.SystemProperties");
            Method method = spClz.getDeclaredMethod("get", String.class);
            String value = (String) method.invoke(spClz, property);
            if (TextUtils.isEmpty(value)) {
                return defaultValue;
            }
            return value;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * 获得鸿蒙系统版本号（含小版本号，实际上同Android的android.os.Build.DISPLAY）
     * @return 版本号
     */
    public static String getHarmonyDisplayVersion() {
        return android.os.Build.DISPLAY;
    }
}
