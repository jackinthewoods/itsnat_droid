package org.itsnat.droid.impl.domparser;

import org.itsnat.droid.impl.dom.ResourceDesc;
import org.itsnat.droid.impl.dom.ResourceDescAsset;
import org.itsnat.droid.impl.dom.ResourceDescDynamic;
import org.itsnat.droid.impl.dom.ResourceDescIntern;
import org.itsnat.droid.impl.dom.ResourceDescRemote;
import org.itsnat.droid.impl.dom.XMLDOM;

/**
 * Created by jmarranz on 24/04/2016.
 */
public abstract class ResourceCacheByMarkupAndResDescBase<TxmlDom extends XMLDOM,TxmlDomParser extends XMLDOMParser>
{
    protected ResourceCache<TxmlDom> cacheByMarkup = new ResourceCache<TxmlDom>();
    protected ResourceCache<ResourceDescDynamic> cacheByResDescValue = new ResourceCache<ResourceDescDynamic>(); // ResourceDescDynamic contiene el ParsedResource conteniendo el recurso y su localización

    protected ResourceCacheByMarkupAndResDescBase()
    {
    }

    public ResourceDescDynamic getResourceDescDynamicCacheByResourceDescValue(String resourceDescValue)
    {
        return cacheByResDescValue.get(resourceDescValue);
    }

    public void cleanCaches()
    {
        cacheByMarkup.clear();
        cacheByResDescValue.clear();
    }

    public static String getResourceDescDynamicPrefix(ResourceDescDynamic resourceDesc)
    {
        String prefix;

        if (resourceDesc instanceof ResourceDescRemote) prefix = "@remote:" + resourceDesc.getResourceType();
        else if (resourceDesc instanceof ResourceDescAsset) prefix = "@assets:" + resourceDesc.getResourceType();
        else if (resourceDesc instanceof ResourceDescIntern) prefix = "@intern:" + resourceDesc.getResourceType();
        else prefix = "@compiled:" + resourceDesc.getResourceType(); // COMPILED, no pasa nada porque no sea un prefijo real, es sólo lectura

        return prefix;
    }

}
