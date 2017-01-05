package zjut.salu.share.myinterface;

/**
 * Created by Salu on 2016/12/7.
 */

public interface OnFoldListener {
    public void onStartFold(float foldFactor);
    public void onFoldingState(float foldFactor, float foldDrawHeight);
    public void onEndFold(float foldFactor);
}
