xbmc-nuxeo
==========

Integration between Nuxeo Content Repository (especially DAM) and <A href="http://xbmc.org/">XBMC media browser</A>

## About this repository

This repo contains plugins for Nuxeo and for XBMC so that XBMC can browser assets (Videos / Audio / Pictures) stored inside a remote Nuxeo server.

For this to work there are 2 plugins :

### nuxeo-xbmc-connector

A Nuxeo plugin that exposes a dedicated Rest/JSON api for XBMC

### nuxeo-addon-for-xbmc

A python based plugin for XBMC that uses the Rest API to fetch data from the Nuxeo server.

