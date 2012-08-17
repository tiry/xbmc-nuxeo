package org.nuxeo.xbmc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.core.CoreQueryPageProviderDescriptor;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.runtime.api.Framework;
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

        Long targetPageSize = 20L;
        if (params.containsKey("pageSize")) {
            targetPageSize = Long.parseLong(params.get("pageSize"));
        }
        Long targetPage = 1L;
        if (params.containsKey("page")) {
            targetPage = Long.parseLong(params.get("page"));
        }

        if (targetPage < 0) {
            targetPage = 0L;
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
            // return query(session, filters, params);
            return queryPage(session, filters, params, targetPageSize,
                    targetPage);
        }
        XbmcDirectoryListing categories = new XbmcDirectoryListing();
        categories.addAll(currentNode.children());
        return categories;
    }

    protected static String buildQuery(List<XbmcCategory> filters,
            Map<String, String> params) throws ClientException {

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
        return query;
    }

    protected static XbmcDirectoryListing query(CoreSession session,
            List<XbmcCategory> filters, Map<String, String> params)
            throws ClientException {
        XbmcDirectoryListing items = new XbmcDirectoryListing();
        String query = buildQuery(filters, params);
        DocumentModelList docs = session.query(query);
        items.addDocuments(docs);
        return items;
    }

    public static XbmcDirectoryListing queryPage(CoreSession session,
            List<XbmcCategory> filters, Map<String, String> params,
            Long targetPageSize, Long targetPage) throws ClientException {

        String query = buildQuery(filters, params);

        PageProviderService pps = Framework.getLocalService(PageProviderService.class);
        Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put(CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY,
                (Serializable) session);

        CoreQueryPageProviderDescriptor desc = new CoreQueryPageProviderDescriptor();
        desc.setPattern(query);

        Object[] parameters = null;
        PageProvider<DocumentModel> pp = (PageProvider<DocumentModel>) pps.getPageProvider(
                "", desc, null, targetPageSize, targetPage, props, parameters);

        XbmcDirectoryListing items = new XbmcDirectoryListing();
        items.addDocuments(pp.getCurrentPage());
        items.setPageSize((int) pp.getPageSize());
        items.setNbPages(pp.getNumberOfPages());
        items.setPageIndex(pp.getCurrentPageIndex());

        return items;
    }
}
