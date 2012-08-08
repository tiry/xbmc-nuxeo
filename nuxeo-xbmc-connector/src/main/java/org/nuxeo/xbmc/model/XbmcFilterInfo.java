package org.nuxeo.xbmc.model;

import java.util.List;

public interface XbmcFilterInfo {

    String getName();

    List<String> listWhereClauses();

    List<String> listOrderClauses();

    List<XbmcCategory> children();
}
