package org.itsnat.droid.impl.xmlinflater.shared.attr;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.xmlinflater.AttrContext;
import org.itsnat.droid.impl.xmlinflater.XMLInflaterContext;
import org.itsnat.droid.impl.xmlinflater.shared.classtree.ClassDesc;


/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescReflecFieldSetDimensionIntRound<TclassDesc extends ClassDesc,TattrTarget,TattrContext extends AttrContext>
        extends AttrDescReflecFieldSetDimensionIntBase<TclassDesc,TattrTarget,TattrContext>
{
    public AttrDescReflecFieldSetDimensionIntRound(TclassDesc parent, String name, String fieldName, Integer defaultValue)
    {
        super(parent,name,fieldName,defaultValue);
    }

    @Override
    public int getDimensionInt(DOMAttr attr, XMLInflaterContext xmlInflaterContext)
    {
        return getDimensionIntRound(attr.getResourceDesc(), xmlInflaterContext);
    }

}
