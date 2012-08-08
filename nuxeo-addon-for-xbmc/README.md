
## About

This is a very basic XBMC plugin that allows XBMC to browse assets stored in a remote Nuxeo server.

## How it works

This XBMC plugin behaves like a virtual folder that fetches content from the Nuxeo server via Http/Json.

The plugin is declared in the 3 categories : Video / Audio / Picture

The plugin is pretty simple since all the navigation logic is handled on the server side.

## Installing

For now : copy the video.plugin.nuxeo folder in ~/.xbmc/plugins/ directory and restart XBMC

## Settings

For this plugin to work, you will need to use the addon settings screen to enter the location of the Nuxeo server and a valid Login/Password.

NB : for now, only Basic Authentication is available


