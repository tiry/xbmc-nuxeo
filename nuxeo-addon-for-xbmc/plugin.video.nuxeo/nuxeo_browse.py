#!/usr/bin/python
# -*- coding: utf-8 -*-
import urllib,urllib2,re,xbmcplugin,xbmcgui,sys,xbmcaddon,json,base64

pluginurl = sys.argv[0];
pluginhandle = int(sys.argv[1])
settings = xbmcaddon.Addon(id='plugin.video.nuxeo')
translation = settings.getLocalizedString

language=""
language=settings.getSetting("language")
if language=="":
  settings.openSettings()
  language=settings.getSetting("language")

if language=="0":
  language="en"
elif language=="1":
  language="fr"

username = settings.getSetting("username")
password = settings.getSetting("password")

def fetchFromNuxeoServer(path=''):
        items = getNuxeoData(path)
        displayItems(items)

def search():
        keyboard = xbmc.Keyboard('', translation(30005))
        keyboard.doModal()
        if keyboard.isConfirmed() and keyboard.getText():
          search_string = keyboard.getText().replace(" ","+")
          if language=="de":
            url="http://videos.arte.tv/de/do_search/videos/suche?q="+search_string
          elif language=="fr":
            url="http://videos.arte.tv/fr/do_search/videos/recherche?q="+search_string
          listVideos(url)

def getNuxeoBaseUrl():
        return settings.getSetting("nuxeoServer") + "site/xbmc/"

def getNuxeoAuthenticatedBaseDownloadUrl():
        url = settings.getSetting("nuxeoServer");
        url = url.replace("http://","http://" + username + ":" + password + "@")
        url = url.replace("https://","https://" + username + ":" + password + "@")
        return url

def getDownloadLink(downloadUrl):
        if downloadUrl.startswith("/"):
          downloadUrl=downloadUrl[1:]
        return getNuxeoAuthenticatedBaseDownloadUrl() + downloadUrl

def getNuxeoData(url):
        if url.find("?")>0:
          keyboard = xbmc.Keyboard('', "enter search keyword")
          keyboard.doModal()
          if keyboard.isConfirmed() and keyboard.getText():
            search_string = keyboard.getText().replace(" ","+")
            url = url + "=" + search_string
        url = getNuxeoBaseUrl() + url
        print "query url=" + url
        req = urllib2.Request(url)
        req.add_header("authorization", "basic " +  base64.b64encode(username + ":" + password))
        response = urllib2.urlopen(req, timeout=30)
        jsondata = response.read()
        items = json.loads(jsondata)
        response.close()
        return items;

def displayItems(items):
        for item in items:
          isDir = item['directory']
          liz=xbmcgui.ListItem(str(item['title']), iconImage="DefaultVideo.png", thumbnailImage=getDownloadLink(str(item['thumbnailImage'])))
          liz.setInfo( type=str(item['mediaType']), infoLabels={ "Title": str(item['title']) } )
          itemUrl=''
          ok=True
          if isDir:
            itemUrl = pluginurl + '?url=' + urllib.quote_plus(str(item['url']))
            print "adding new directory item with url=" + itemUrl
          else:
            liz.setProperty('IsPlayable', 'true')
            downloadUrl = str(item['url'])
            itemUrl = getDownloadLink(downloadUrl)
            print "adding new playable item with url=" + itemUrl
          ok=xbmcplugin.addDirectoryItem(handle=int(sys.argv[1]),url=itemUrl,listitem=liz,isFolder=isDir)
        xbmcplugin.endOfDirectory(int(sys.argv[1]))

def parameters_string_to_dict(parameters):
        ''' Convert parameters encoded in a URL to a dict. '''
        paramDict = {}
        if parameters:
            paramPairs = parameters[1:].split("&")
            for paramsPair in paramPairs:
                paramSplits = paramsPair.split('=')
                if (len(paramSplits)) == 2:
                    paramDict[paramSplits[0]] = paramSplits[1]
        return paramDict
         
params=parameters_string_to_dict(sys.argv[2])
url=params.get('url')

print "##################### Nuxeo Plugin"
print "pluginurl=" + pluginurl
print "url=" + str(url)
print "pluginhandle=" + str(pluginhandle)

if type(url)==type(str()):
  url=urllib.unquote_plus(url)

if (url==None):
  url=''

fetchFromNuxeoServer(url)
