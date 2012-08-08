package org.nuxeo.xbmc.model;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

public class XbmcDirectoryListing extends ArrayList<XbmcDirectoryItem>
        implements List<XbmcDirectoryItem> {

    private static final long serialVersionUID = 1L;

    public XbmcDirectoryListing() {
        super();
    }

    public XbmcDirectoryListing(List<XbmcDirectoryItem> items) {
        super(items);
    }

    public void addDocuments(List<DocumentModel> docs) {
        for (DocumentModel doc : docs) {
            add(doc.getAdapter(XbmcDirectoryItem.class));
        }
    }
}
