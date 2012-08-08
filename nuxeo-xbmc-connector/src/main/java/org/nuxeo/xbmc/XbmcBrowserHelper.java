package org.nuxeo.xbmc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.xbmc.model.XbmcCategory;
import org.nuxeo.xbmc.model.XbmcDirectoryListing;
import org.nuxeo.xbmc.model.XbmcFilterInfo;

public class XbmcBrowserHelper {

    private static XbmcCategory root;

    protected static final Log log = LogFactory.getLog(XbmcBrowserHelper.class);

    public static void init() {
        root = new XbmcCategory("root", "root");

        // media types
        XbmcCategory video = new XbmcCategory("videos", "Videos");
        video.addWhereClause("ecm:mixinType = 'Video'");
        XbmcCategory picture = new XbmcCategory("pictures", "Pictures");
        picture.addWhereClause("ecm:mixinType = 'Picture'");
        XbmcCategory audio = new XbmcCategory("audio", "Music");
        audio.addWhereClause("ecm:mixinType = 'Audio'");

        root.addChildren(video);
        root.addChildren(picture);
        root.addChildren(audio);

        // video entries
        XbmcCategory recentVideos = new XbmcCategory("recent", "Recent videos");
        recentVideos.addOrderClause("dc:modified");
        video.addChildren(recentVideos);

        XbmcCategory searchVideos = new XbmcCategory("search", "Search videos");
        searchVideos.setMediaType("search");
        searchVideos.addWhereClause("ecm:fulltext like '$searchkeyword$'");
        searchVideos.setUserInput("searchkeyword");
        video.addChildren(searchVideos);

        // picture entries
        XbmcCategory recentPictures = new XbmcCategory("recent",
                "Recent pictures");
        recentPictures.addOrderClause("dc:modified");
        picture.addChildren(recentPictures);

        XbmcCategory searchPictures = new XbmcCategory("search",
                "Search pictures");
        searchPictures.setMediaType("search");
        searchPictures.addWhereClause("ecm:fulltext like '$searchkeyword$'");
        searchPictures.setUserInput("searchkeyword");
        picture.addChildren(searchPictures);

        // audio entries
        XbmcCategory recentAudio = new XbmcCategory("recent", "Recent music");
        recentAudio.addOrderClause("dc:modified");
        audio.addChildren(recentAudio);

        XbmcCategory searchAudio = new XbmcCategory("search", "Search music");
        searchAudio.setMediaType("search");
        searchAudio.addWhereClause("ecm:fulltext like '$searchkeyword$'");
        searchAudio.setUserInput("searchkeyword");
        audio.addChildren(searchAudio);
    }

    public static XbmcDirectoryListing getRootCategories() {
        XbmcDirectoryListing categories = new XbmcDirectoryListing();
        categories.addAll(root.children());
        return categories;
    }

    public static XbmcDirectoryListing getSubCategories(CoreSession session,
            String subPath, Map<String, String> params) throws ClientException {
        if (subPath.startsWith("/")) {
            subPath = subPath.substring(1);
        }
        XbmcCategory currentNode = root;
        String[] parts = subPath.split("/");
        List<XbmcCategory> filters = new ArrayList<XbmcCategory>();
        for (String pathSegment : parts) {
            if (!pathSegment.isEmpty()) {
                for (XbmcCategory child : currentNode.children()) {
                    if (child.getName().equals(pathSegment)) {
                        currentNode = child;
                        filters.add(child);
                        break;
                    }
                }
            }
        }
        if (currentNode.children().size() == 0) {
            return query(session, filters, params);
        }
        XbmcDirectoryListing categories = new XbmcDirectoryListing();
        categories.addAll(currentNode.children());
        return categories;
    }

    protected static XbmcDirectoryListing query(CoreSession session,
            List<XbmcCategory> filters, Map<String, String> params)
            throws ClientException {
        XbmcDirectoryListing items = new XbmcDirectoryListing();

        StringBuffer sb = new StringBuffer("select * from Document ");
        List<String> whereClauses = new ArrayList<String>();
        List<String> orderClauses = new ArrayList<String>();
        for (XbmcFilterInfo filterInfo : filters) {
            whereClauses.addAll(filterInfo.listWhereClauses());
            orderClauses.addAll(filterInfo.listOrderClauses());
        }
        if (whereClauses.size() > 0) {
            sb.append(" where ");
            for (int i = 0; i < whereClauses.size(); i++) {
                if (i > 0) {
                    sb.append(" AND ");
                }
                sb.append(whereClauses.get(i));
            }
        }
        if (orderClauses.size() > 0) {
            sb.append(" order by ");
            for (int i = 0; i < orderClauses.size(); i++) {
                if (i > 0) {
                    sb.append(" , ");
                }
                sb.append(orderClauses.get(i));
            }
        }

        String query = sb.toString();
        for (String paramName : params.keySet()) {
            if (query.contains("$" + paramName + "$")) {
                query = query.replace("$" + paramName + "$",
                        params.get(paramName));
            }
        }
        DocumentModelList docs = session.query(query);
        items.addDocuments(docs);
        return items;
    }

}
