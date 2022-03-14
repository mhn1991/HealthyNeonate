package ir.pudica.test;

/**
 * Created by Moj-Val on 10/14/2017.
 */

public class Drug {
    private String title;
    private String subTitle;
    private String rec1;
    private String rec2;

    public Drug(String title, String subTitle, String rec1, String rec2) {
        this.title = title;
        this.subTitle = subTitle;
        this.rec1 = rec1;
        this.rec2 = rec2;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getRec1() {
        return rec1;
    }

    public String getRec2() {
        return rec2;
    }
}
