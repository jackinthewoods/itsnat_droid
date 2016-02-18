package org.itsnat.droid.impl.domparser.drawable;

import android.content.res.AssetManager;
import android.content.res.Configuration;

import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.impl.dom.DOMElement;
import org.itsnat.droid.impl.dom.drawable.DOMElemDrawable;
import org.itsnat.droid.impl.dom.drawable.XMLDOMDrawable;
import org.itsnat.droid.impl.domparser.XMLDOMParser;
import org.itsnat.droid.impl.domparser.XMLDOMRegistry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;

/**
 * Created by jmarranz on 31/10/14.
 */
public class XMLDOMDrawableParser extends XMLDOMParser
{
    protected XMLDOMDrawableParser(XMLDOMRegistry xmlDOMRegistry,AssetManager assetManager,Configuration configuration)
    {
        super(xmlDOMRegistry,assetManager,configuration);
    }

    public static XMLDOMDrawableParser createXMLDOMDrawableParser(XMLDOMRegistry xmlDOMRegistry,AssetManager assetManager,Configuration configuration)
    {
        return new XMLDOMDrawableParser(xmlDOMRegistry,assetManager,configuration);
    }

    public void parse(String markup,XMLDOMDrawable xmlDOMDrawable)
    {
        StringReader input = new StringReader(markup);
        parse(input,xmlDOMDrawable);
    }

    private void parse(Reader input,XMLDOMDrawable xmlDOMDrawable)
    {
        try
        {
            XmlPullParser parser = newPullParser(input);
            parse(parser,xmlDOMDrawable);
        }
        catch (IOException ex) { throw new ItsNatDroidException(ex); }
        catch (XmlPullParserException ex) { throw new ItsNatDroidException(ex); }
        finally
        {
            try { input.close(); }
            catch (IOException ex) { throw new ItsNatDroidException(ex); }
        }
    }

    private void parse(XmlPullParser parser,XMLDOMDrawable xmlDOMDrawable) throws IOException, XmlPullParserException
    {
        String rootElemName = getRootElementName(parser);
        parseRootElement(rootElemName,parser, xmlDOMDrawable);
    }

    @Override
    protected DOMElement createElement(String name,DOMElement parent)
    {
        return new DOMElemDrawable(name,(DOMElemDrawable)parent);
    }
}
