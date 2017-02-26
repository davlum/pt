package utils;

/**
 * Created by hal on 2017-02-26.
 */
public class SidebarElement {
    public String link;
    public String name;
    public String hover;

    public SidebarElement(String link, String name, String hover) {
        this.link = link;
        this.name = name;
        this.hover = hover;
    }

    public SidebarElement(String link, String name) {
        this.link = link;
        this.name = name;
        this.hover = "";
    }
}
