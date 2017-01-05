package zjut.salu.share.model;

import java.io.Serializable;

/**
 * Created by Salu on 2016/11/19.
 */

public class NineImage implements Serializable {
    private String position;
    private String url;
    private int width;
    private int height;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
