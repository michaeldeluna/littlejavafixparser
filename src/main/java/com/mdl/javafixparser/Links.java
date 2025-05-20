package com.mdl.javafixparser;

import java.util.Arrays;

public class Links {
    class Link {
        public TagOffset tagOffset;
        public Link next;
    }

    private final Link[] links;
    private final Link[] linkArray;
    private int size = 0;

    public Links(int capacity, int maxTagValue) {
        Link[] links = new Link[capacity];
        for (int i = 0; i < links.length; i++)
            links[i] = new Link();

        this.links = links;
        this.linkArray = new Link[maxTagValue];
    }

    public boolean contains(int tag) {
        return linkArray[tag] != null;
    }

    public void clear() {
        size = 0;
        Arrays.fill(linkArray, null);
    }

    public void put(int tag, TagOffset t) {
        if (!contains(tag)) {
            linkArray[tag] = link(t);
            return;
        }

        Link lastLink = linkArray[tag];
        while (lastLink.next != null)
            lastLink = lastLink.next;

        lastLink.next = link(t);
    }

    public Link get(int tag, int group) {
        Link link = linkArray[tag];
        for (int i = 0; i < group; i++)
            link = link.next;

        return link;
    }

    private Link link(TagOffset t) {
        Link link = links[size];
        link.tagOffset = t;
        link.next = null;
        ++size;
        return link;
    }
}
