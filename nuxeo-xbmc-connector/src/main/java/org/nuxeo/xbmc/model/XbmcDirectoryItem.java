package org.nuxeo.xbmc.model;

public interface XbmcDirectoryItem {

    String getId();

    String getName();

    String getIconImage();

    String getThumbnailImage();

    String getMediaType();

    String getTitle();

    String getUrl();

    boolean isDirectory();
}
