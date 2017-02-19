package org.itsnat.droid.impl;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import org.itsnat.droid.InflateLayoutRequest;
import org.itsnat.droid.ItsNatDroid;
import org.itsnat.droid.ItsNatDroidBrowser;
import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.R;
import org.itsnat.droid.impl.browser.ItsNatDroidBrowserImpl;
import org.itsnat.droid.impl.domparser.XMLDOMRegistry;
import org.itsnat.droid.impl.xmlinflater.XMLInflaterRegistry;
import org.itsnat.droid.impl.xmlinflater.layout.stdalone.InflateLayoutRequestStandaloneImpl;


/**
 * Created by jmarranz on 3/05/14.
 */
public class ItsNatDroidImpl implements ItsNatDroid
{
    public static ItsNatDroidImpl DEFAULT;

    protected Application app;
    protected XMLInflaterRegistry xmlInflaterRegistry = new XMLInflaterRegistry(this); // Sólo creamos una instancia pues cuesta mucho instanciar los objetos procesadores de clases y atributos
    protected XMLDOMRegistry xmlDOMRegistry = new XMLDOMRegistry(this);

    public ItsNatDroidImpl(Application app)
    {
        this.app = app;
    }

    public static void init(Application app)
    {
        if (DEFAULT != null) throw new ItsNatDroidException("ItsNat Droid is already initialized");
        DEFAULT = new ItsNatDroidImpl(app);
    }

    public Application getApplication()
    {
        return app;
    }

    @Override
    public ItsNatDroidBrowser createItsNatDroidBrowser()
    {
        return new ItsNatDroidBrowserImpl(this);
    }

    @Override
    public InflateLayoutRequest createInflateLayoutRequest()
    {
        // El modelo ItsNat está muy bien pero ofrecemos como alternativa que el propio programador se descargue sus layouts
        // y los gestione a su manera
        return new InflateLayoutRequestStandaloneImpl(this);
    }

    @Override
    public String getVersionName()
    {
        Resources res = app.getApplicationContext().getResources();
        return res.getString(R.string.itsNatDroidLibVersionName);
    }

    @Override
    public int getVersionCode()
    {
        Resources res = app.getApplicationContext().getResources();
        return res.getInteger(R.integer.itsNatDroidLibVersionCode);
    }

    public XMLInflaterRegistry getXMLInflaterRegistry()
    {
        return xmlInflaterRegistry;
    }

    public XMLDOMRegistry getXMLDOMRegistry()
    {
        return xmlDOMRegistry;
    }

    public void onConfigurationChanged(Activity activity, Configuration newConfig)
    {
        // Este método conviene ser llamado en onConfigurationChanged(Configuration newConfig) en la actividad de la app que usa la librería
        // Llamando a cleanCaches de esta manera se recrean los DOMAttrDynamic en los cuales se aplican los filtros de nuevo de acuerdo con los cambios de configuración
        getXMLDOMRegistry().cleanCaches();
    }
}
