package com.jinxin.jxtouchscreen.model;

/**
 * Created by yh on 2017/3/28.
 */

public class SkinItemVO {

    private String packageName = null;
    private String name = null;
    private String icon = null;
    private String id = null;
    public SkinItemVO(String id){
        this(id,null,null,null);
    }
    public SkinItemVO(String id,String packageName,String name,String icon){
        this.id = id;
        this.packageName = packageName;
        this.name = name;
        this.icon = icon;
    }
    public String getID() {
        return id;
    }
    public void setID(String iD) {
        id = iD;
    }
    public String getPackageUrl() {
        return packageName;
    }
    public void setPackageUrl(String packageUrl) {
        this.packageName = packageUrl;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

}
