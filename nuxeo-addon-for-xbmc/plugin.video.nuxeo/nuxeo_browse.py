#!/usr/bin/python
# -*- coding: utf-8 -*-
import urllib,urllib2,re,xbmcplugin,xbmcgui,sys,xbmcaddon,json,base64
import xbmcctx
import os

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

pageSize = settings.getSetting("pageSize")
if (pageSize == "" or pageSize == None) :
  pageSize = 5
else:
  pageSize = int(pageSize)

def fetchFromNuxeoServer(path='', pageIndex=0, pageSize = pageSize):
        items = getNuxeoData(path, pageIndex, pageSize)
        displayItems(items)

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

def getNuxeoData(url, pageIndex, pageSize):
        if url.find("?searchkeyword")>0:
          keyboard = xbmc.Keyboard('', "enter search keyword")
          keyboard.doModal()
          if keyboard.isConfirmed() and keyboard.getText():
            search_string = keyboard.getText().replace(" ","+")
            url = url + "=" + search_string
        url = getNuxeoBaseUrl() + url

        if url.find("?") < 0 :
          url = url + "?"
        else:
          url = url + "&"
        url = url + "pageSize=" + str(pageSize) + "&page=" + str(pageIndex)
        print "query url=" + url
        req = urllib2.Request(url)
        req.add_header("authorization", "basic " +  base64.b64encode(username + ":" + password))
        response = urllib2.urlopen(req, timeout=30)
        jsondata = response.read()
        items = json.loads(jsondata)
        response.close()
        return items;

def displayItems(items):
        pageSize = items['pageSize']
        pageIndex = items['pageIndex']
        numberOfPages = items['numberOfPages']

        print "display page=" + str(pageIndex) + "/" + str(numberOfPages) + " with pageSize=" + str(pageSize)

        #if (pageIndex>0) :
        #  liz=xbmcgui.ListItem("previous page", iconImage="DefaultVideo.png")
        #  itemUrl = pluginurl + '?url=' + urllib.quote_plus(url) + "&pageIndex=" + str(pageIndex-1)
        #  print "adding prev page button with url=" + itemUrl
        #  ok=xbmcplugin.addDirectoryItem(handle=int(sys.argv[1]),url=itemUrl,listitem=liz,isFolder=True)

        for item in items['result']:
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
          contextMenu = [("Myitem","MyCB()")]
          contextMenu = [("MyItem2",'XBMC.Container.Update(%s?mode=ctx&item=%s)' % (sys.argv[0],item['id']))]
          liz.addContextMenuItems(contextMenu)
          ok=xbmcplugin.addDirectoryItem(handle=int(sys.argv[1]),url=itemUrl,listitem=liz,isFolder=isDir)

        if (numberOfPages>0 and pageIndex<(numberOfPages-1)) :
          iconPath = os.path.join(settings.getAddonInfo('path'),"resources","skins","default","media","NextPage.png")
          liz=xbmcgui.ListItem("more (" + str(pageIndex+2) + "/" + str(numberOfPages) +")", iconImage=iconPath)
          itemUrl = pluginurl + '?url=' + urllib.quote_plus(url) + "&pageIndex=" + str(pageIndex+1)
          print "adding next page button with url=" + itemUrl
          ok=xbmcplugin.addDirectoryItem(handle=int(sys.argv[1]),url=itemUrl,listitem=liz,isFolder=True)

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
runctx = xbmcctx.context().getContext()

print "##################### Nuxeo Plugin"
print "pluginurl=" + pluginurl
print "url=" + str(url)
print "pluginhandle=" + str(pluginhandle)
print "ctx=" + runctx
print "params=" + str(params)
print "path=" + str(settings.getAddonInfo('path'))

pageIndex = params.get("pageIndex")
if pageIndex == "" or pageIndex == None:
  pageIndex = 0
else:
  pageIndex = int(pageIndex)


if (url==None):
  if (runctx == 'video'):
    url='videos'
  elif (runctx=='image'):
    url='pictures'
  elif (runctx=='audio'):
    url='audio'
  else:
    url=''

if type(url)==type(str()):
  url=urllib.unquote_plus(url)

fetchFromNuxeoServer(url, pageIndex, pageSize)
