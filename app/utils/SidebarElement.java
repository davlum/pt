package utils;

/**
 * Class representing the elements that show up in the sidebar of the application.
 */
public final class SidebarElement {
    public String link;
    public String name;
    public String hover;

    public SidebarElement(String link, String name, String hover) {
        this.link = link;
        this.name = name;
        this.hover = hover;
    }

}
