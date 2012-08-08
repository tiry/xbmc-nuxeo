## About

This simple WebEngine plugin exposes a dedicated REST/JSON API that is used by the XBMC Nuxeo plugin.

## Why not using automation

 - XBMC Python Virtual Folder plugin model uses an url system that work better with a GET rest API than with Automation
 - Nuxeo Python Automation lib is LGPL whereas XBMC is GPL

## Navigation

The navigation system is completely driven by the Server and is currently configured (hard-coded) in the XbmxBrowserHelper class.
In the future this may be moved to a real service with an extension point so that new categories / navigation entries can be added.

## Rest API

The API uses simple GET requests and returns JSON formated data :

    http://127.0.0.1:8080/nuxeo/site/xbmc/

Gives access to the root categories (Audio/Video/Pictures)

    http://127.0.0.1:8080/nuxeo/site/xbmc/videos

Gives access to the Video categories

    http://127.0.0.1:8080/nuxeo/site/xbmc/videos/recent

Gives access to the recently added videos

    http://127.0.0.1:8080/nuxeo/site/xbmc/videos/search?searchkeyword=foo

Gives access to the video that match full text search for keyword foo




