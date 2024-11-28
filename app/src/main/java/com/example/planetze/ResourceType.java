package com.example.planetze;

public class ResourceType {
    private String title;
    private String description;
    private String link;
    private String icon;

    public ResourceType(String title, String description, String link, String icon) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getIcon() {
        return icon;
    }
}
