package org.itsnat.droid.impl.xmlinflater.drawable;

import org.itsnat.droid.impl.domparser.XMLDOMParserContext;
import org.itsnat.droid.impl.xmlinflater.AttrContext;

/**
 * Created by Jose on 09/11/2015.
 */
public class AttrDrawableContext extends AttrContext
{
    public AttrDrawableContext(XMLInflaterDrawable xmlInflaterDrawable,XMLDOMParserContext xmlDOMParserContext)
    {
        super(xmlInflaterDrawable,xmlDOMParserContext);
    }

    public XMLInflaterDrawable getXMLInflaterDrawable()
    {
        return (XMLInflaterDrawable)xmlInflater;
    }
}
