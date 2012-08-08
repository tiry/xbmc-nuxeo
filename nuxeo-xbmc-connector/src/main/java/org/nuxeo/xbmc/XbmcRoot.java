/*
 * (C) Copyright ${year} Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */

package org.nuxeo.xbmc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.xbmc.model.XbmcDirectoryListing;

/**
 * The root entry for the WebEngine module.
 * 
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */
@Path("/xbmc")
@WebObject(type = "XbmcRoot")
public class XbmcRoot extends ModuleRoot {

    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
        XbmcBrowserHelper.init();
    }

    @GET
    @Produces("text/json")
    public XbmcDirectoryListing doGet() {
        return XbmcBrowserHelper.getRootCategories();
    }

    @Path("/{mediaType}")
    @Produces("text/json")
    public Object traverse(@PathParam(value = "mediaType")
    String mediaType) {
        return getContext().newObject("XbmcFilter");
    }

}
