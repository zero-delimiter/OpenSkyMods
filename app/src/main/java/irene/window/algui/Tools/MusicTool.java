package irene.window.algui.Tools;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.IOException;

/**
 * @Author 𝘽𝙮·艾琳 - 游戏逆向交流群730967224  - 作者QQ3353484607
 * @Date 2023/12/27 12:29
 * @Describe 音乐工具
 */
public class MusicTool {

    public static final String TAG = "MusicTool";


    //播放Assets文件夹下mp3文件
    private static MediaPlayer mediaPlayer;
    public static void playAssetMp3(Context context, String filename) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else {
            mediaPlayer.reset();
        }

        try {
            AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(filename);
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Failed to play asset mp3: " + e.getMessage());
        }
    }

}
