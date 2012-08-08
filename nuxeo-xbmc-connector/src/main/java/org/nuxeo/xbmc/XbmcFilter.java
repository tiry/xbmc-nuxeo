package org.nuxeo.xbmc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.DefaultObject;
import org.nuxeo.xbmc.model.XbmcDirectoryListing;

@WebObject(type = "XbmcFilter")
public class XbmcFilter extends DefaultObject {

    protected static Log log = LogFactory.getLog(XbmcFilter.class);

    protected String getFilterPath() {
        String resourcePath = getPath();
        String rootPath = getContext().getRoot().getPath();
        return resourcePath.replaceFirst(rootPath, "");
    }

    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
    }

    protected Map<String, String> getQueryParams() {
        HttpServletRequest request = getContext().getRequest();
        Map<String, String> result = new HashMap<String, String>();
        Enumeration<?> pEnum = request.getParameterNames();
        while (pEnum.hasMoreElements()) {
            String name = (String) pEnum.nextElement();
            result.put(name, request.getParameter(name));
        }
        return result;
    }

    @GET
    @Produces("text/json")
    public XbmcDirectoryListing getItems() throws ClientException {
        XbmcDirectoryListing subCats = XbmcBrowserHelper.getSubCategories(
                getContext().getCoreSession(), getFilterPath(),
                getQueryParams());
        return subCats;
    }

    @Path("/{filter}")
    @Produces("text/json")
    public Object traverse(@PathParam(value = "filter")
    String filter) {
        return getContext().newObject("XbmcFilter");
    }

}
