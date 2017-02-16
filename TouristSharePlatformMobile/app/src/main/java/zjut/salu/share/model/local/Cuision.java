package zjut.salu.share.model.local;

import java.io.Serializable;

/**菜品pojo
 * Created by Alan-Mac on 2017/02/16.
 */

public class Cuision implements Serializable{
    private static final long serialVersionUID = 1L;

    private String cuisionid;
    private String cuisionname;
    private String cuisiondescription;
    private Float cuisionprice;
    private Float cuisionforeignprice;
    private String cuisionpricecurrency;
    private String cuisionforeigncurrency;
    private String cuisionimage;
    private TourismAttraction attraction;


    public String getCuisionid() {
        return cuisionid;
    }

    public void setCuisionid(String cuisionid) {
        this.cuisionid = cuisionid;
    }

    public String getCuisionname() {
        return cuisionname;
    }

    public void setCuisionname(String cuisionname) {
        this.cuisionname = cuisionname;
    }

    public String getCuisiondescription() {
        return cuisiondescription;
    }

    public void setCuisiondescription(String cuisiondescription) {
        this.cuisiondescription = cuisiondescription;
    }

    public Float getCuisionprice() {
        return cuisionprice;
    }

    public void setCuisionprice(Float cuisionprice) {
        this.cuisionprice = cuisionprice;
    }

    public Float getCuisionforeignprice() {
        return cuisionforeignprice;
    }

    public void setCuisionforeignprice(Float cuisionforeignprice) {
        this.cuisionforeignprice = cuisionforeignprice;
    }

    public String getCuisionpricecurrency() {
        return cuisionpricecurrency;
    }

    public void setCuisionpricecurrency(String cuisionpricecurrency) {
        this.cuisionpricecurrency = cuisionpricecurrency;
    }

    public String getCuisionforeigncurrency() {
        return cuisionforeigncurrency;
    }

    public void setCuisionforeigncurrency(String cuisionforeigncurrency) {
        this.cuisionforeigncurrency = cuisionforeigncurrency;
    }

    public String getCuisionimage() {
        return cuisionimage;
    }

    public void setCuisionimage(String cuisionimage) {
        this.cuisionimage = cuisionimage;
    }

    public TourismAttraction getAttraction() {
        return attraction;
    }

    public void setAttraction(TourismAttraction attraction) {
        this.attraction = attraction;
    }
}
