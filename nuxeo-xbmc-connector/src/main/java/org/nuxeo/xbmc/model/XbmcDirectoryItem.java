package org.nuxeo.xbmc.model;

import java.util.Map;

public interface XbmcDirectoryItem {

    String getId();

    String getName();

    String getIconImage();

    String getThumbnailImage();

    String getMediaType();

    String getTitle();

    String getUrl();

    boolean isDirectory();

    Map<String, Object> getMetaData();
}
