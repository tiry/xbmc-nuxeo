package org.nuxeo.xbmc;

import java.util.HashSet;
import java.util.Set;

import org.nuxeo.ecm.webengine.app.WebEngineModule;
import org.nuxeo.xbmc.model.XbmcDirectoryListingWriter;

public class XbmcApplication extends WebEngineModule {

    @Override
    public Set<Object> getSingletons() {
        Set<Object> result = new HashSet<Object>();
        result.add(new XbmcDirectoryListingWriter());
        return result;
    }
}
