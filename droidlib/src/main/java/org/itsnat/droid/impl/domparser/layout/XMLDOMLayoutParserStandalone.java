package org.itsnat.droid.impl.domparser.layout;

import android.content.res.AssetManager;
import android.content.res.Configuration;

import org.itsnat.droid.impl.dom.layout.XMLDOMLayout;
import org.itsnat.droid.impl.dom.layout.XMLDOMLayoutStandalone;
import org.itsnat.droid.impl.domparser.XMLDOMParserContext;
import org.itsnat.droid.impl.domparser.XMLDOMRegistry;

/**
 * Created by jmarranz on 27/10/14.
 */
public class XMLDOMLayoutParserStandalone extends XMLDOMLayoutParser
{
    public XMLDOMLayoutParserStandalone(XMLDOMParserContext xmlDOMParserContext)
    {
        super(xmlDOMParserContext);
    }

    @Override
    public XMLDOMLayout createXMLDOMLayout()
    {
        return new XMLDOMLayoutStandalone();
    }
}
