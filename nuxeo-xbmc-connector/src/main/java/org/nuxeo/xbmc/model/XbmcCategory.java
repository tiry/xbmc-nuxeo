package org.nuxeo.xbmc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XbmcCategory implements XbmcDirectoryItem, XbmcFilterInfo {

    protected final String name;

    protected final String title;

    protected String url;

    protected String mediaType = "Directory";

    protected final List<String> whereClauses = new ArrayList<String>();

    protected final List<String> orderClauses = new ArrayList<String>();

    protected final List<XbmcCategory> children = new ArrayList<XbmcCategory>();

    protected String userInput;

    public XbmcCategory(String name, String title) {
        this.name = name;
        this.title = title;
        this.url = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIconImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getThumbnailImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMediaType() {
        return mediaType;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getUrl() {
        String result = url.replace("root/", "");
        if (userInput != null) {
            result = result + "?" + userInput;
        }
        return result;
    }

    @Override
    public List<String> listWhereClauses() {
        return whereClauses;
    }

    @Override
    public List<String> listOrderClauses() {
        return orderClauses;
    }

    public void addWhereClause(String clause) {
        whereClauses.add(clause);
    }

    public void addOrderClause(String clause) {
        orderClauses.add(clause);
    }

    @Override
    public List<XbmcCategory> children() {
        return children;
    }

    public void addChildren(XbmcCategory filter) {
        children.add(filter);
        filter.setUrl(getUrl() + "/" + filter.getName());
    }

    public void setMediaType(String type) {
        this.mediaType = type;
    }

    public boolean isDirectory() {
        return true;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    @Override
    public String getId() {
        return getName();
    }

    @Override
    public Map<String, Object> getMetaData() {
        Map<String, Object> meta = new HashMap<String, Object>();
        meta.put("Title", getTitle());
        return meta;
    }
}
