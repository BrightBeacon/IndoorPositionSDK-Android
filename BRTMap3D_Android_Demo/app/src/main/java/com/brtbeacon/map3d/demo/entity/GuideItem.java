package com.brtbeacon.map3d.demo.entity;

import java.util.LinkedList;
import java.util.List;

public class GuideItem {
    public int id;
    public String name;
    public List<GuideItem> childs = new LinkedList<>();
    public Class<?> cls;

    public GuideItem(String name) {
        this.name = name;
    }

    public GuideItem(String name, Class<?> cls) {
        this.name = name;
        this.cls = cls;
    }

    public void addAll(List<GuideItem> items) {
        childs.addAll(items);
    }

    public void add(GuideItem item) {
        childs.add(item);
    }

}
