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

package org.nuxeo.xbmc.model;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.ecm.platform.picture.api.adapters.MultiviewPicture;
import org.nuxeo.ecm.platform.video.VideoDocument;

/**
 * @author <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */
public class XbmcDirectoryAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(final DocumentModel doc, Class<?> itf) {

        String thumbUrl = null;
        String type = "?";
        VideoDocument video = doc.getAdapter(VideoDocument.class);
        MultiviewPicture picture = doc.getAdapter(MultiviewPicture.class);
        if (video != null) {
            thumbUrl = "/nxpicsfile/" + doc.getRepositoryName() + "/"
                    + doc.getId() + "/StaticPlayerView:content/";
            type = "Video";
        } else if (picture != null) {
            thumbUrl = "/nxpicsfile/" + doc.getRepositoryName() + "/"
                    + doc.getId() + "/Medium:content/";
            type = "Picture";
        }

        final String thumbNailUrl = thumbUrl;
        final String resourceType = type;

        return new XbmcDirectoryItem() {

            @Override
            public String getUrl() {
                return "/nxbigfile/" + doc.getRepositoryName() + "/"
                        + doc.getId() + "/blobholder:0/";
            }

            @Override
            public String getTitle() {
                try {
                    return doc.getTitle();
                } catch (ClientException e) {
                    return getName();
                }
            }

            @Override
            public String getThumbnailImage() {
                return thumbNailUrl;
            }

            @Override
            public String getName() {
                return doc.getName();
            }

            @Override
            public String getMediaType() {
                return resourceType;
            }

            @Override
            public String getIconImage() {
                return null;
            }

            public boolean isDirectory() {
                return false;
            }

        };
    }

}
