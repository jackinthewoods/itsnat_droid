package org.itsnat.droid.impl.xmlinflater.drawable.classtree;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.impl.dom.DOMElement;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawable;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawableChildDrawableBridge;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawableContainer;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawableRoot;
import org.itsnat.droid.impl.xmlinflated.drawable.TransitionDrawableItem;
import org.itsnat.droid.impl.xmlinflater.drawable.ClassDescDrawableMgr;
import org.itsnat.droid.impl.xmlinflater.drawable.XMLInflaterDrawable;

import java.util.ArrayList;

/**
 * Created by jmarranz on 27/11/14.
 */
public abstract class ClassDescElementDrawableRoot<Tdrawable extends Drawable> extends ClassDescDrawable
{
    public ClassDescElementDrawableRoot(ClassDescDrawableMgr classMgr, String elemName)
    {
        super(classMgr, elemName, null);
    }

    public ClassDescElementDrawableRoot(ClassDescDrawableMgr classMgr, String elemName,ClassDescDrawable parentClass)
    {
        super(classMgr, elemName, parentClass);
    }

    public static Drawable[] getDrawables(ArrayList<ElementDrawable> itemList)
    {
        Drawable[] drawableLayers = new Drawable[itemList.size()];
        for (int i = 0; i < itemList.size(); i++)
        {
            ElementDrawableContainer item = (ElementDrawableContainer) itemList.get(i);
            drawableLayers[i] = item.getDrawable();
        }
        return drawableLayers;
    }

    public Drawable getChildDrawable(ArrayList<ElementDrawable> childList)
    {
        // Si el drawable está definido como elemento hijo gana éste por delante del atributo drawable
        Drawable drawable = null;

        if (childList != null) {
            if (childList.size() > 1)
                throw new ItsNatDroidException("Expected just a single child element or none, processing " + getElementName());
            if (childList.size() == 1) {
                ElementDrawableChildDrawableBridge childDrawable = (ElementDrawableChildDrawableBridge) childList.get(0);
                drawable = childDrawable.getDrawable();
            }
        }
        return drawable; // Puede ser null
    }

    public abstract ElementDrawableRoot createRootElementDrawable(DOMElement rootElem, XMLInflaterDrawable inflaterDrawable, Context ctx);
}

