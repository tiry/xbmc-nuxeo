package org.nuxeo.xbmc.model;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

public class XbmcDirectoryListing extends ArrayList<XbmcDirectoryItem>
        implements List<XbmcDirectoryItem> {

    private static final long serialVersionUID = 1L;

    protected long pageIndex = 0;

    protected long nbPages = 0;

    protected int pageSize = 0;

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

    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        this.pageIndex = pageIndex;
    }

    public long getNbPages() {
        return nbPages;
    }

    public void setNbPages(long nbPages) {
        this.nbPages = nbPages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
