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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;
import org.nuxeo.ecm.platform.picture.api.adapters.MultiviewPicture;
import org.nuxeo.ecm.platform.video.Stream;
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

            protected String getCacheKey() {
                try {
                    Calendar modified = (Calendar) doc.getPropertyValue("dc:modified");
                    if (modified != null) {
                        return "TS" + modified.getTimeInMillis();
                    } else {
                        return doc.getCacheKey();
                    }
                } catch (ClientException e) {
                    return doc.getId();
                }
            }

            @Override
            public String getId() {
                return doc.getId();
            }

            @Override
            public String getUrl() {
                return "/nxbigfile/" + doc.getRepositoryName() + "/"
                        + doc.getId() + "/blobholder:0/?cacheKey="
                        + getCacheKey();
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
                return thumbNailUrl + "?cacheKey=" + getCacheKey();
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

            @Override
            public Map<String, Object> getMetaData() {

                Map<String, Object> meta = new HashMap<String, Object>();
                meta.put("Title", getTitle());

                if ("Video".equals(resourceType)) {
                    addVideoMetaData(doc, meta);
                } else if ("Picture".equals(resourceType)) {

                }
                return meta;
            }

        };

    }

    protected static void addVideoMetaData(DocumentModel document,
            Map<String, Object> meta) {
        VideoDocument video = document.getAdapter(VideoDocument.class);

        try {
            meta.put("Duration", video.getVideo().getVideoInfo().getDuration()
                    / 60 + "");
            meta.put("Plot",
                    (String) document.getPropertyValue("dc:description"));

            if (meta.get("Plot") == null || meta.get("Plot").equals("")) {
                meta.put("Plot", document.getTitle());
            }

            meta.put("VideoResolution", video.getVideo().getHeight());
            meta.put(
                    "VideoAspect",
                    ((video.getVideo().getWidth() + 0.0) / video.getVideo().getHeight())
                            + "");

            meta.put("Year", 2019);
            meta.put("Genre", "YoMan");
            meta.put("VideoResolution", 576);
            meta.put("VideoAspect", 1.33);

            for (Stream stream : video.getVideo().getStreams()) {
                if (stream.getType().equals("Video")) {
                    meta.put("VideoCodec", stream.getCodec());
                } else if (stream.getType().equals("Audio")) {
                    meta.put("AudioCodec", stream.getCodec());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
