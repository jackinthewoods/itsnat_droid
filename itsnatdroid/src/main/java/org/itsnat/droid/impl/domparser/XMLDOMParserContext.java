package org.itsnat.droid.impl.domparser;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * Created by jmarranz on 24/02/2016.
 */
public class XMLDOMParserContext
{
    protected final XMLDOMRegistry xmlDOMRegistry;
    protected final AssetManager assetManager;
    protected final Configuration configuration; // Aunque se use onConfigurationChanged(Configuration newConfig) y no se recree la actividad el objeto antiguo afortunadamente se actualiza por lo que si se "salva" este XMLDOMParserContext asociado a un Context que no se recrea, aunque haya un Configuration nuevo dado por onConfigurationChanged, el anterior sigue valiendo (por ejemplo orientation está actualizado)
    protected final DisplayMetrics displayMetrics;
    protected final Context ctxToOpenInternFiles;

    private XMLDOMParserContext(XMLDOMRegistry xmlDOMRegistry,AssetManager assetManager,Configuration configuration,DisplayMetrics displayMetrics,Context ctx)
    {
        this.xmlDOMRegistry = xmlDOMRegistry;
        this.assetManager = assetManager;
        this.configuration = configuration;
        this.displayMetrics = displayMetrics;
        this.ctxToOpenInternFiles = ctx;
    }

    public XMLDOMParserContext(XMLDOMRegistry xmlDOMRegistry,Context ctx)
    {
        this(xmlDOMRegistry,ctx.getResources().getAssets(),ctx.getResources().getConfiguration(),ctx.getResources().getDisplayMetrics(),ctx);
    }

    public XMLDOMRegistry getXMLDOMRegistry()
    {
        return xmlDOMRegistry;
    }

    public AssetManager getAssetManager()
    {
        return assetManager;
    }

    public Configuration getConfiguration()
    {
        return configuration;
    }

    public DisplayMetrics getDisplayMetrics()
    {
        return displayMetrics;
    }

    public Context getContextToOpenInternFiles() { return ctxToOpenInternFiles; }
}
