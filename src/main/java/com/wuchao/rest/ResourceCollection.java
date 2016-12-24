package com.wuchao.rest;

import java.util.ArrayList;

/**
 * Created by air on 16/12/24.
 */
public class ResourceCollection {
    private ArrayList<ResourceData> list;

    public ResourceCollection(){
        this.list = new ArrayList<ResourceData>();
    }

    public void add(ResourceData rd) {
        this.list.add(rd);
    }

    public void setList(ArrayList<ResourceData> list) {
        this.list = list;
    }

    public ArrayList<ResourceData> getList() {
        return this.list;
    }
}
