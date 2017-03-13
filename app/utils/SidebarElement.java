package utils;

public final class SidebarElement {
    public String link;
    public String name;
    public String hover;

    private SidebarElement(String link, String name, String hover) {
        this.link = link;
        this.name = name;
        this.hover = hover;
    }

    public static SidebarElement newInstance(String alink, String aname, String ahover) {
        return new SidebarElement(alink, aname, ahover);
    }
}
