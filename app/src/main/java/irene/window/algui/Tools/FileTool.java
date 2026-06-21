package irene.window.algui.Tools;
import android.content.Context;
import android.icu.text.DecimalFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Locale;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/23 15:47
 * @Describe 文件工具

 * @方法清单：
 * 1、文件的新建、删除；
 * 2、文件的复制；
 * 3、获取文件扩展名；
 * 4、文件的重命名；
 * 5、获取某个文件的详细信息；
 * 6、计算某个文件的大小；
 * 7、文件大小的格式化；
 * 8、获取某个目录下的文件列表；
 * 9、目录的新建、删除; 11、目录的复制；
 * 10、计算某个目录包含的文件数量；
 * 11、计算某个目录包含的文件大小；
 * 12、读取文件内容到字符串;
 * 13、保存字符串到文件;
 * 14、获取文件的名字不包含后缀;
 * 15、获取文件的名字包含后缀;

 */
public class FileTool {

    public static final String TAG = "FileTool";



    private static final String[][] MIME_MapTable =
    {
        // {后缀名， MIME类型}
        {".3gp", "video/3gpp"}, {".apk", "application/vnd.android.package-archive"},
        {".asf", "video/x-ms-asf"}, {".avi", "video/x-msvideo"},
        {".bin", "application/octet-stream"}, {".bmp", "image/bmp"}, {".c", "text/plain"},
        {".class", "application/octet-stream"}, {".conf", "text/plain"}, {".cpp", "text/plain"},
        {".doc", "application/msword"},
        {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
        {".xls", "application/vnd.ms-excel"},
        {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
        {".exe", "application/octet-stream"}, {".gif", "image/gif"},
        {".gtar", "application/x-gtar"}, {".gz", "application/x-gzip"}, {".h", "text/plain"},
        {".htm", "text/html"}, {".html", "text/html"}, {".jar", "application/java-archive"},
        {".java", "text/plain"}, {".jpeg", "image/jpeg"}, {".jpg", "image/jpeg"},
        {".js", "application/x-javascript"}, {".log", "text/plain"}, {".m3u", "audio/x-mpegurl"},
        {".m4a", "audio/mp4a-latm"}, {".m4b", "audio/mp4a-latm"}, {".m4p", "audio/mp4a-latm"},
        {".m4u", "video/vnd.mpegurl"}, {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"},
        {".mp2", "audio/x-mpeg"}, {".mp3", "audio/x-mpeg"}, {".mp4", "video/mp4"},
        {".mpc", "application/vnd.mpohun.certificate"}, {".mpe", "video/mpeg"},
        {".mpeg", "video/mpeg"}, {".mpg", "video/mpeg"}, {".mpg4", "video/mp4"},
        {".mpga", "audio/mpeg"}, {".msg", "application/vnd.ms-outlook"}, {".ogg", "audio/ogg"},
        {".pdf", "application/pdf"}, {".png", "image/png"},
        {".pps", "application/vnd.ms-powerpoint"}, {".ppt", "application/vnd.ms-powerpoint"},
        {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
        {".prop", "text/plain"}, {".rc", "text/plain"}, {".rmvb", "audio/x-pn-realaudio"},
        {".rtf", "application/rtf"}, {".sh", "text/plain"}, {".tar", "application/x-tar"},
        {".tgz", "application/x-compressed"}, {".txt", "text/plain"}, {".wav", "audio/x-wav"},
        {".wma", "audio/x-ms-wma"}, {".wmv", "audio/x-ms-wmv"},
        {".wps", "application/vnd.ms-works"}, {".xml", "text/plain"},
        {".z", "application/x-compress"}, {".zip", "application/x-zip-compressed"}, {"", "*/*"}
    };

    /**
     * 根据文件后缀名获得对应的MIME类型
     *
     * @param filePath 文件路径
     */
    public static String getMIMEType(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "getMIMEType: 文件不存在");
            return "";
        }

        if (!file.isFile()) {
            Log.e(TAG, "getMIMEType: 当前文件类型是目录");
            return "";
        }
        String type = "*/*";
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf("."); // 获取后缀名前的分隔符"."在fileName中的位置
        if (dotIndex < 0) {
            return type;
        }

        String end = fileName.substring(dotIndex, fileName.length()).toLowerCase(Locale.getDefault()); // 获取文件的后缀名
        if (end.length() == 0) {
            return type;
        }

        // 在MIME和文件类型的匹配表中找到对应的MIME类型
        for (String[] aMIME_MapTable : MIME_MapTable) {
            if (end.equals(aMIME_MapTable[0])) {
                type = aMIME_MapTable[1];
            }
        }
        return type;
    }

    /**
     * 获取设备的sd根路径
     */
    public static String getSDPath() {
        File sdDir = null;
        String sdPath;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        assert sdDir != null;
        sdPath = sdDir.toString();
        Log.w(TAG, "getSDPath:" + sdPath);
        return sdPath;
    }

    /**
     * 根据文件名获得文件的扩展名
     *
     * @param fileName 文件名
     * @return 文件扩展名（不带点）
     */
    public static String getFileSuffix(String fileName) {
        Log.w(TAG, "getFileSuffix: fileName::" + fileName);
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1, fileName.length());
    }

    //**************************************************创建文件操作*****************************************************************

    /**
     * 创建文件
     *
     * @param dirPath  文件所在目录的目录名，如/java/test/1.txt,要在当前目录下创建一个文件名为1.txt的文件
     *                 则path为/java/test，fileName为1.txt（也可以封装成直接传递文件的绝对路径）
     * @param fileName 文件名
     * @return 文件新建成功则返回true
     */
    public static boolean createFile(String dirPath, String fileName) {
        String filePath = dirPath + File.separator + fileName;
        Log.w(TAG, "createFile: filePath::" + filePath + "  File.separator ::" + File.separator);
        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
            Log.w(TAG, "createFile: 文件所在目录不存在，创建目录成功");
        }

        if (file.exists()) {
            Log.e(TAG, "新建文件失败：file.exist()=" + file.exists());
            return false;
        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 新建目录
     *
     * @param dir 目录的绝对路径
     * @return 创建成功则返回true
     */
    public static boolean createFolder(String dir) {
        File file = new File(dir);
        return file.mkdir();
    }

    //*******************************************删除文件操作************************************************************************

    /**
     * 删除单个文件
     *
     * @param filePath 要删除的文件路径
     * @return 文件删除成功则返回true
     */
    public static boolean deleteFile(String filePath) {

        File file = new File(filePath);
        if (file.exists()) {
            boolean isDeleted = file.delete();
            Log.w(TAG, file.getName() + "删除结果：" + isDeleted);
            return isDeleted;
        } else {
            Log.w(TAG, "文件删除失败：文件不存在！");
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param file 要删除的文件对象
     * @return 文件删除成功则返回true
     */
    private static boolean deleteFile(File file) {
        if (file.exists()) {
            boolean isDeleted = file.delete();
            Log.w(TAG, file.getName() + "删除结果：" + isDeleted);
            return isDeleted;
        } else {
            Log.w(TAG, "文件删除失败：文件不存在！");
            return false;
        }
    }

    /**
     * 删除文件夹及其包含的所有文件(会自身循环调用)
     *
     * @param file 要删除的文件对象
     * @return 文件删除成功则返回true
     */
    public static boolean deleteFolder(File file) {
        boolean flag;
        File files[] = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    // 删除子文件
                    flag = deleteFile(f);
                    if (!flag) {
                        return false;
                    }
                } else {
                    // 删除子目录
                    flag = deleteFolder(f);
                    if (!flag) {
                        return false;
                    }
                }
            }
        }
        //能成功走到这，说明当前目录下的所有子文件和子目录都已经删除完毕
        flag = file.delete();//将此空目录也进行删除
        return flag;
    }

    //*************************************复制/重命名操作********************************************

    /**
     * 复制文件
     *
     * @param srcPath 源文件绝对路径
     * @param destDir 目标文件所在目录
     * @return boolean 复制成功则返回true
     */
    public static boolean copyFile(String srcPath, String destDir) {
        boolean flag = false;
        File srcFile = new File(srcPath); // 源文件
        if (!srcFile.exists()) {
            // 源文件不存在
            Log.w(TAG, "copyFile: 源文件不存在");
            return false;
        }
        // 获取待复制文件的文件名
        String fileName = srcPath.substring(srcPath.lastIndexOf(File.separator));
        String destPath = destDir + fileName;
        if (destPath.equals(srcPath)) {
            // 源文件路径和目标文件路径重复
            Log.w(TAG, "copyFile: 源文件路径和目标文件路径重复");
            return false;
        }

        File destFile = new File(destPath); // 目标文件
        if (destFile.exists() && destFile.isFile()) {
            // 该路径下已经有一个同名文件
            Log.w(TAG, "copyFile: 目标目录下已有同名文件!");
            return false;
        }

        File destFileDir = new File(destDir);
        destFileDir.mkdirs();
        try {
            FileInputStream fis = new FileInputStream(srcPath);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fis.close();
            fos.close();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (flag) {
            Log.w(TAG, "copyFile: 复制文件成功!");
        }
        return flag;
    }

    /**
     * 复制目录
     *
     * @param srcDir  源文件夹目录
     * @param destDir 目标文件夹所在目录
     * @return 复制成功则返回true
     */
    public static boolean copyFolder(String srcDir, String destDir) {
        Log.w(TAG, "copyFolder: 复制文件夹开始!");
        boolean flag = false;

        File srcFile = new File(srcDir);
        if (!srcFile.exists()) {
            // 源文件夹不存在
            Log.w(TAG, "copyFolder: 源文件夹不存在");
            return false;
        }
        String dirName = getDirName(srcDir); // 获得待复制的文件夹的名字，比如待复制的文件夹为"E://dir"则获取的名字为"dir"

        String destPath = destDir ; // 如果想连同源目录名拷贝则String destPath = destDir　+ File.separator + dirName
        // Util.toast("目标文件夹的完整路径为：" + destPath);
        if (destPath.equals(srcDir)) {
            Log.w(TAG, "copyFolder: 目标文件夹与源文件夹重复");
            return false;
        }
        File destDirFile = new File(destPath);
        if (!destDirFile.exists()) {
            destDirFile.mkdirs(); // 如果指定目录不存在则生成目录
        }

        File[] files = srcFile.listFiles(); // 获取源文件夹下的子文件和子文件夹
        Log.i(TAG, "copyFolder: -----files,length::" + files.length + "      files::" + files);
        if (files.length == 0) {
            // 如果源文件夹为空目录则直接设置flag为true，这一步非常隐蔽，debug了很久
            flag = true;
        } else {
            for (File temp : files) {
                if (temp.isFile()) {
                    // 文件
                    flag = copyFile(temp.getAbsolutePath(), destPath);
                } else if (temp.isDirectory()) {
                    // 文件夹
                    flag = copyFolder(temp.getAbsolutePath(), destPath);
                }
                if (!flag) {
                    break;
                }
            }
        }
        if (flag) {
            Log.w(TAG, "copyFolder: 复制文件夹成功!");
        }
        return flag;
    }

    /**
     * 重命名文件
     *
     * @param oldPath 旧文件的绝对路径
     * @param newPath 新文件的绝对路径
     * @return 文件重命名成功则返回true
     */
    public static boolean renameTo(String oldPath, String newPath) {
        if (oldPath.equals(newPath)) {
            Log.w(TAG, "文件重命名失败：新旧文件名绝对路径相同！");
            return false;
        }
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);

        boolean isSuccess = oldFile.renameTo(newFile);
        Log.w(TAG, "文件重命名是否成功：" + isSuccess);
        return isSuccess;
    }

    /**
     * 重命名文件
     *
     * @param oldFile 旧文件对象，File类型
     * @param newName 新文件的文件名，String类型
     * @return 重命名成功则返回true
     */
    public static boolean renameTo(File oldFile, String newName) {
        File newFile = new File(oldFile.getParentFile() + File.separator + newName);
        boolean flag = oldFile.renameTo(newFile);
        Log.w(TAG, "文件重命名是否成功：" + flag);
        return flag;
    }

    //*********************************计算文件大小/格式化**************************************************************


    /**
     * 计算某个文件的大小
     *
     * @param path 文件的绝对路径
     * @return
     */
    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.length();
        } else {
            Log.e(TAG, "getFileSize: error!");
            return -1;
        }
    }

    /**
     * 计算某个文件的大小
     *
     * @param file 文件对象
     * @return 文件大小，如果file不是文件，则返回-1
     */
    private static long getFileSize(File file) {
        if (file.isFile()) {
            return file.length();
        } else {
            return -1;
        }
    }


    /**
     * 计算某个目录的大小
     *
     * @param directory 目录生成的file对象
     * @return
     */
    public static long getFolderSize(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            long size = 0;
            for (File f : files) {
                if (f.isFile()) {
                    // 获得子文件的大小
                    size = size + getFileSize(f);
                } else {
                    // 获得子目录的大小
                    size = size + getFolderSize(f);
                }
            }
            return size;
        }
        return -1;
    }

    /**
     * 文件大小的格式化
     *
     * @param size 文件大小，单位为byte
     * @return 文件大小格式化后的文本
     */
    public static String formatSize(long size) {
        DecimalFormat df = new DecimalFormat("####.00");
        if (size < 1024) {
            return size + "Byte";
        } else if (size < 1024 * 1024) {
            float kSize = size / 1024f;
            return df.format(kSize) + "KB";
        } else if (size < 1024 * 1024 * 1024) {
            float mSize = size / 1024f / 1024f;
            return df.format(mSize) + "MB";
        } else if (size < 1024L * 1024L * 1024L * 1024L) {
            float gSize = size / 1024f / 1024f / 1024f;
            return df.format(gSize) + "GB";
        } else {
            return "size: error";
        }
    }

    /**
     * 格式化文件最后修改时间字符串(针对以时间戳结尾的文件)
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", Locale.getDefault());
        String formatedTime = sdf.format(date);
        return formatedTime;
    }

    //*********************************************获取文件列表*******************************************
    /**
     * 获取某个目录下的文件列表（如果存在子目录，则子目录中的文件是否计入）
     *
     * @param dir 文件目录
     * @return 文件列表File[] files
     */
    public static File[] getFileList(String dir) {
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                return files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    //********************************************文件操作中封装的一些小方法*********************************************
    /**
     * 获取待复制文件夹的文件夹名
     *
     * @param dir 待复制的目录名称
     * @return String
     */
    private static String getDirName(String dir) {
        if (dir.endsWith(File.separator)) {
            // 如果文件夹路径以"//"结尾，则先去除末尾的"//"
            dir = dir.substring(0, dir.lastIndexOf(File.separator));
        }
        return dir.substring(dir.lastIndexOf(File.separator) + 1);
    }

    /**
     * 计算某个目录包含的文件数量
     *
     * @param directory
     * @return
     */
    public static int getFileCount(File directory) {
        File[] files = directory.listFiles();
        int count = files.length;
        return count;
    }

    /**
     * 获取文件的名字
     * 不包含后缀
     */
    public static String getFileName(String url) {
        int start = url.lastIndexOf("/");
        int end = url.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return url.substring(start + 1, end);
        } else {
            return null;
        }
    }

    /**
     * 获取文件的名字
     * 保留文件名及后缀
     */
    public static String getFileNameWithSuffix(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }

    /**
     * 读取文件内容到字符串
     * @param strFilePath
     * @return
     */
    public static String ReadTxtFile(String strFilePath) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            //打开文件
            File file = new File(strFilePath);
            //如果path是传递过来的参数，可以做一个非目录的判断
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader = null;

                inputreader = new InputStreamReader(instream, "utf-8");

                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                instream.close();
            }
        } catch (Exception e) {
        }
        return stringBuffer.toString();
    }

    /**
     * 保存字符串到文件.
     */
    public static String saveFilePlt(Context mContext, String content, String fileName) {
        try {
            String dir = mContext.getExternalFilesDir("").getAbsolutePath()
                + File.separator + "lensun";
            (new File(dir)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File fs = new File(dir, fileName + ".plt");
            FileOutputStream os = new FileOutputStream(fs);
            os.write(content.getBytes());
            os.flush();
            os.close();
            return fs.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
