package zjut.salu.share.myinterface;

/**视频播放接口
 * Created by Salu on 2017/1/29.
 */

public interface MediaPlayerListener {
    void start();

    void pause();

    int getDuration();

    int getCurrentPosition();

    void seekTo(long pos);

    boolean isPlaying();

    int getBufferPercentage();

    boolean canPause();
}
