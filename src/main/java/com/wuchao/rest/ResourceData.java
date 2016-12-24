package com.wuchao.rest;

/**
 * Created by air on 16/12/24.
 */
public class ResourceData {
    private long playcount;
    private long cmtcount;
    private String source;
    private long ts;

    public void setPlaycount(long playcount) {
        this.playcount = playcount;
    }

    public long getPlaycount() {
        return playcount;
    }

    public void setCmtcount(long cmtcount) {
        this.cmtcount = cmtcount;
    }

    public long getCmtcount() {
        return cmtcount;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getTs() {
        return ts;
    }

    public String getSource() {return source;}

    public void setSource(String source) {this.source = source;}
}
