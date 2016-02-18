package org.itsnat.droid.impl.dom;

import android.content.res.Configuration;

import org.itsnat.droid.impl.util.MiscUtil;

import java.util.Locale;

/**
 * Created by jmarranz on 3/11/14.
 */
public class DOMAttrRemote extends DOMAttrDynamic
{
    public DOMAttrRemote(String namespaceURI, String name, String value,Configuration configuration)
    {
        super(namespaceURI, name, value, configuration);
    }

    public boolean isDownloaded()
    {
        return resource != null;
    }

    public static boolean isRemote(String value)
    {
        return value.startsWith("@remote:");
    }

    public static boolean isPendingToDownload(DOMAttr attr)
    {
        return (attr instanceof DOMAttrRemote && !((DOMAttrRemote) attr).isDownloaded());
    }

    public void check(String namespaceURI,String name,String value)
    {
        // Este chequeo nos sirve para quedarnos más tranquilos y cuesta muy poco
        if (!MiscUtil.equalsNullAllowed(this.namespaceURI,namespaceURI)) throw MiscUtil.internalError();
        if (!MiscUtil.equalsNullAllowed(this.name,name)) throw MiscUtil.internalError();
        if (!MiscUtil.equalsNullAllowed(this.value,value)) throw MiscUtil.internalError();
    }
}
