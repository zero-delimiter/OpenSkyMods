package irene.window.algui;



/**
 * @Author 𝘽𝙮·艾琳 - ［3］游戏逆向交流群730967224 - 作者QQ3353484607
 * @Date 2023/12/30 19:31
 * @Describe ALGUI - Java内存修改工具
 */
public class AlguiMemory {

    public static final String TAG = "AlguiMemory";

    //以下是内存类型 (最好不要修改它的值！)
    public static int  RANGE_ALL = 0;//全部内存
    public static int  RANGE_ANONYMOUS = 7;//A内存
    public static int  RANGE_ASHMEM = 12;//AS内存
    public static int  RANGE_B_BAD = 6;//B内存
    public static int  RANGE_C_ALLOC = 3;//CA内存
    public static int  RANGE_C_BSS = 1;//CB内存
    public static int  RANGE_C_DATA = 4;//CD内存
    public static int  RANGE_C_HEAP = 2;//CH内存
    public static int  RANGE_JAVA_HEAP = 8;//JH内存
    public static int  RANGE_OTHER = 13;//O内存
    public static int  RANGE_STACK = 5;//S内存
    public static int  RANGE_VIDEO = 11;//V内存
    public static int  RANGE_CODE_SYSTEM = 10;//XS内存
    public static int  RANGE_CODE_APP = 9; //XA内存

    public static int  TYPE_DWORD = 1;//d类型
    public static int  TYPE_FLOAT = 2;//f类型
    public static int  TYPE_DOUBLE = 3;//e类型


    //加载C层函数
    static {
        System.loadLibrary("Modification");
    }

    //以下是Java映射内存修改方法，你可以直接在Java使用它们
    public static native void clearResult();//清空搜索结果
    public static native void setRange(int memory);//设置内存范围
    public static native void setPackageName(String packageName);//设置包名
    public static native void RangeMemorySearch(String value, int type);//内存搜索
    public static native void MemoryOffset(String value, int type, long offset);//内存偏移搜索
    public static native void MemoryWrite(String value, int type, long offset);//内存写入

    public static native int getPackageNamePid(String packageName);//获取包名pid (pid=进程ID)
    public static native int getResultCount();//获取搜索结果数量
    public static native void startFreeze();//开启冻结线程
    public static native void stopFreeze();//关闭冻结线程

    public static native long getModuleAddress(String name);//获取基址
    public static native long readLong(long value);//读取指针
    public static native void setValue(String value, long addr, int type);//设置数值


    //(DeBug)
    /*public static void test() {
     AlguiMemory.setPackageName("com.fingersoft.hillclimb.noncmcc");//设置包名
     long sa = AlguiMemory.getModuleAddress("libgame.so");//获取模块地址
     long saPointer = AlguiMemory.readLong(sa);//获取模块指针
     long address=saPointer+0x376E10;


     Log.d("alguitest","模块地址= "+decimalToHex(sa));
     Log.d("alguitest","模块指针= "+decimalToHex(saPointer));
     Log.d("alguitest","最终地址= "+decimalToHex(address));
     AlguiMemory.setValue("5201314", address, AlguiMemory.TYPE_DWORD);//修改值

     }
     //十进制转十六进制 (DeBug)
     public static String decimalToHex(long decimalNumber) {
     String hexString = Long.toHexString(decimalNumber);
     return hexString;
     }*/
}
