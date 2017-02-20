package org.itsnat.droid.impl.browser;

import android.content.Context;
import android.view.View;

import org.apache.http.params.HttpParams;
import org.itsnat.droid.HttpRequestResult;
import org.itsnat.droid.ItsNatDoc;
import org.itsnat.droid.ItsNatDroidBrowser;
import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.OnEventErrorListener;
import org.itsnat.droid.OnHttpRequestErrorListener;
import org.itsnat.droid.OnScriptErrorListener;
import org.itsnat.droid.OnServerStateLostListener;
import org.itsnat.droid.Page;
import org.itsnat.droid.PageRequest;
import org.itsnat.droid.UserData;
import org.itsnat.droid.impl.ItsNatDroidImpl;
import org.itsnat.droid.impl.dom.layout.XMLDOMLayout;
import org.itsnat.droid.impl.domparser.XMLDOMParserContext;
import org.itsnat.droid.impl.util.UserDataImpl;
import org.itsnat.droid.impl.xmlinflated.InflatedXML;
import org.itsnat.droid.impl.xmlinflated.InflatedXMLPage;
import org.itsnat.droid.impl.xmlinflated.layout.InflatedXMLLayoutPageImpl;
import org.itsnat.droid.impl.xmlinflater.XMLInflater;
import org.itsnat.droid.impl.xmlinflater.layout.XMLInflaterLayout;
import org.itsnat.droid.impl.xmlinflater.layout.page.XMLInflaterLayoutPage;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

/**
 * Created by jmarranz on 4/06/14.
 */
public abstract class PageImpl implements Page
{
    protected final PageRequestImpl pageRequestCloned; // Nos interesa únicamente para el reload, es un clone del original por lo que podemos tomar datos del mismo sin miedo a cambiarse
    protected final HttpRequestResultOKImpl httpReqResult;
    protected final XMLDOMParserContext xmlDOMParserContext;
    protected final int bitmapDensityReference;
    protected final XMLInflaterLayoutPage xmlInflaterLayoutPage;
    protected final String uniqueIdForInterpreter;
    protected final Interpreter interp;
    protected final ItsNatDocImpl itsNatDoc;
    protected OnScriptErrorListener scriptErrorListener;
    protected OnEventErrorListener eventErrorListener;
    protected OnServerStateLostListener stateLostListener;
    protected OnHttpRequestErrorListener httpReqErrorListener;
    protected UserDataImpl userData;
    protected boolean dispose;

    public PageImpl(PageRequestImpl pageRequestToClone,PageRequestResult pageReqResult)
    {
        this.pageRequestCloned = pageRequestToClone.clonePageRequest(); // De esta manera conocemos como se ha creado pero así podemos reutilizar el PageRequestImpl original
        this.httpReqResult = pageReqResult.getHttpRequestResultOKImpl();
        this.xmlDOMParserContext = pageReqResult.getXMLDOMParserContext();

        this.scriptErrorListener = pageRequestToClone.getOnScriptErrorListener();

        HttpRequestResultOKImpl httpReqResult = pageReqResult.getHttpRequestResultOKImpl();

        Integer bitmapDensityReference = httpReqResult.getBitmapDensityReference(); // Sólo está definida en el caso de ItsNat server, por eso se puede pasar por el usuario también a través del PageRequest
        this.bitmapDensityReference = bitmapDensityReference != null ? bitmapDensityReference : pageRequestCloned.getBitmapDensityReference();

        ItsNatDroidImpl itsNatDroid = pageRequestCloned.getItsNatDroidBrowserImpl().getItsNatDroidImpl();
        ItsNatDroidBrowserImpl browser = pageRequestCloned.getItsNatDroidBrowserImpl();

        this.uniqueIdForInterpreter = browser.getUniqueIdGenerator().generateId("i"); // i = interpreter
        this.interp = new Interpreter(new StringReader(""), System.out, System.err, false, new NameSpace(browser.getInterpreter().getNameSpace(), uniqueIdForInterpreter)); // El StringReader está copiado del código fuente de beanshell2 https://code.google.com/p/beanshell2/source/browse/branches/v2.1/src/bsh/Interpreter.java

        // Definimos pronto el itsNatDoc para que los layout include tengan algún soporte de scripting de ItsNatDoc por ejemplo toast, eval, alert etc antes de inflarlos
        this.itsNatDoc = ItsNatDocImpl.createItsNatDoc(this); // Casi el último para que  PageImpl esté ya bien creado antes de inicializar  ItsNatDocImpl, el xmlInflaterLayoutPage siguiente necesita acceder a ItsNatDocImpl desde PageImpl

        XMLDOMLayout domLayout = pageReqResult.getXMLDOMLayout();

        this.xmlInflaterLayoutPage = (XMLInflaterLayoutPage)XMLInflaterLayout.createXMLInflaterLayout(itsNatDroid, domLayout, this.bitmapDensityReference, pageRequestCloned.getAttrResourceInflaterListener(), getContext(), this);
        View rootView = xmlInflaterLayoutPage.inflateLayout(null,-1);


        try
        {
            interp.set("itsNatDoc", itsNatDoc);
        }
        catch (EvalError ex)
        {
            throw new ItsNatDroidException(ex);
        }
        catch (Exception ex)
        {
            throw new ItsNatDroidException(ex);
        }

        StringBuilder methods = new StringBuilder();
        methods.append("alert(data){itsNatDoc.alert(data);}");
        methods.append("toast(value,duration){itsNatDoc.toast(value,duration);}");
        methods.append("toast(value){itsNatDoc.toast(value);}");
        methods.append("eval(code){itsNatDoc.eval(code);}");

        itsNatDoc.eval(methods.toString()); // Rarísimo que de error


        InflatedXMLLayoutPageImpl inflatedLayoutPage = xmlInflaterLayoutPage.getInflatedXMLLayoutPageImpl();

        List<String> scriptList = inflatedLayoutPage.getScriptList();

        if (!scriptList.isEmpty())
        {
            for (String code : scriptList)
            {
                itsNatDoc.eval(code);
            }
        }

        finishLoad(pageReqResult);
    }

    public abstract void finishLoad(PageRequestResult pageReqResult);

    public static PageImpl getPageImpl(XMLInflater xmlInflater)
    {
        InflatedXML inflatedXML = xmlInflater.getInflatedXML();
        return getPageImpl(inflatedXML);
    }

    public static PageImpl getPageImpl(InflatedXML inflatedXML)
    {
        if (inflatedXML instanceof InflatedXMLPage) // Contiene un PageImpl
            return ((InflatedXMLPage)inflatedXML).getPageImpl();
        return null;
    }

    public XMLDOMParserContext getXMLDOMParserContext()
    {
        return xmlDOMParserContext;
    }

    public ItsNatDroidBrowserImpl getItsNatDroidBrowserImpl()
    {
        return pageRequestCloned.getItsNatDroidBrowserImpl();
    }

    public PageRequestImpl getPageRequestClonedImpl()
    {
        return pageRequestCloned;
    }

    @Override
    public ItsNatDroidBrowser getItsNatDroidBrowser()
    {
        return getItsNatDroidBrowserImpl();
    }


    @Override
    public Map<String, List<String>> getRequestProperties()
    {
        return pageRequestCloned.getRequestProperties();
    }

    public RequestPropertyMap getRequestPropertyMapImpl()
    {
        return getPageRequestClonedImpl().getRequestPropertyMap();
    }

    public HttpParams getHttpParams()
    {
        return getPageRequestClonedImpl().getHttpParams();
    }

    @Override
    public int getConnectTimeout()
    {
        return getPageRequestClonedImpl().getConnectTimeout();
    }

    @Override
    public int getReadTimeout()
    {
        return getPageRequestClonedImpl().getReadTimeout();
    }

    @Override
    public HttpRequestResult getHttpRequestResult()
    {
        return getHttpRequestResultOKImpl();
    }

    public HttpRequestResultOKImpl getHttpRequestResultOKImpl()
    {
        return httpReqResult;
    }

    public int getBitmapDensityReference()
    {
        return bitmapDensityReference;
    }

    public int getClientErrorMode()
    {
        return pageRequestCloned.getClientErrorMode();
    }

    @Override
    public String getURL()
    {
        return pageRequestCloned.getURL();
    }

    public String getPageURLBase() // Para la carga de recursos (scripts, imágenes etc)
    {
        return pageRequestCloned.getPageURLBase();
    }

    public abstract String getItsNatServerVersion();

    public InflatedXMLLayoutPageImpl getInflatedXMLLayoutPageImpl()
    {
        return xmlInflaterLayoutPage.getInflatedXMLLayoutPageImpl();
    }

    public XMLInflaterLayoutPage getXMLInflaterLayoutPage()
    {
        return xmlInflaterLayoutPage;
    }

    public Interpreter getInterpreter()
    {
        return interp;
    }


    public Context getContext()
    {
        return pageRequestCloned.getContext();
    }

    public UserData getUserData()
    {
        if (userData == null) this.userData = new UserDataImpl();
        return userData;
    }

    public ItsNatDoc getItsNatDoc()
    {
        return getItsNatDocImpl();
    }

    public ItsNatDocImpl getItsNatDocImpl()
    {
        return itsNatDoc;
    }

    public OnEventErrorListener getOnEventErrorListener()
    {
        return eventErrorListener;
    }

    @Override
    public void setOnEventErrorListener(OnEventErrorListener listener)
    {
        this.eventErrorListener = listener;
    }

    public OnScriptErrorListener getOnScriptErrorListener()
    {
        return scriptErrorListener;
    }

    @Override
    public void setOnScriptErrorListener(OnScriptErrorListener listener)
    {
        this.scriptErrorListener = listener;
    }

    public OnServerStateLostListener getOnServerStateLostListener()
    {
        return stateLostListener;
    }

    public void setOnServerStateLostListener(OnServerStateLostListener listener)
    {
        this.stateLostListener = listener;
    }

    public OnHttpRequestErrorListener getOnHttpRequestErrorListener()
    {
        return httpReqErrorListener;
    }

    public void setOnHttpRequestErrorListener(OnHttpRequestErrorListener listener)
    {
        this.httpReqErrorListener = listener;
    }

    public PageRequest reusePageRequest()
    {
        return pageRequestCloned.clonePageRequest();
    }

    public boolean isDisposed()
    {
        return dispose;
    }

    public void dispose()
    {
        if (dispose) return;
        this.dispose = true;
    }


}
