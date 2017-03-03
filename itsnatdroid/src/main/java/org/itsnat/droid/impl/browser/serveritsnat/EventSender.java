package org.itsnat.droid.impl.browser.serveritsnat;

import android.os.AsyncTask;

import org.itsnat.droid.ClientErrorMode;
import org.itsnat.droid.HttpRequestResult;
import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.ItsNatDroidServerResponseException;
import org.itsnat.droid.OnEventErrorListener;
import org.itsnat.droid.impl.browser.HttpRequestData;
import org.itsnat.droid.impl.browser.HttpRequestResultOKImpl;
import org.itsnat.droid.impl.browser.HttpUtil;
import org.itsnat.droid.impl.browser.PageImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.EventGenericImpl;
import org.itsnat.droid.impl.dom.DOMAttrRemote;
import org.itsnat.droid.impl.dom.ParsedResource;
import org.itsnat.droid.impl.dom.layout.XMLDOMLayoutPageItsNat;
import org.itsnat.droid.impl.domparser.XMLDOMParserContext;
import org.itsnat.droid.impl.util.MimeUtil;
import org.itsnat.droid.impl.util.NameValue;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by jmarranz on 10/07/14.
 */
public class EventSender
{
    protected EventManager evtManager;

    public EventSender(EventManager evtManager)
    {
        this.evtManager = evtManager;
    }

    public EventManager getEventManager()
    {
        return evtManager;
    }

    public ItsNatDocPageItsNatImpl getItsNatDocItsNatImpl()
    {
        return evtManager.getItsNatDocItsNatImpl();
    }

    public void requestSync(EventGenericImpl evt, String servletPath, List<NameValue> paramList, HttpRequestData httpRequestData,
                            Map<String,ParsedResource> urlResDownloadedMap,XMLDOMParserContext xmlDOMParserContext)
    {
        HttpRequestResultOKBeanshellImpl result = null;
        try
        {
            result = executeInBackground(this, servletPath, httpRequestData, paramList,urlResDownloadedMap,xmlDOMParserContext);
        }
        catch (Exception ex)
        {
            onFinishError(this, ex, evt);

            return; // No podemos continuar
        }

        onFinishOk(this, evt, result);
    }

    public void requestAsync(EventGenericImpl evt, String servletPath, List<NameValue> paramList, HttpRequestData httpRequestData,
                                Map<String,ParsedResource> urlResDownloadedMap,XMLDOMParserContext xmlDOMParserContext)
    {
        HttpPostEventAsyncTask task = new HttpPostEventAsyncTask(this, evt, servletPath, paramList, httpRequestData,urlResDownloadedMap,xmlDOMParserContext);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); // Con execute() a secas se ejecuta en un "pool" de un sólo hilo sin verdadero paralelismo
    }

    public static HttpRequestResultOKBeanshellImpl executeInBackground(EventSender eventSender, String servletPath, HttpRequestData httpRequestData, List<NameValue> paramList,
                                                    Map<String,ParsedResource> urlResDownloadedMap,XMLDOMParserContext xmlDOMParserContext) throws Exception
    {
        // Ejecutado en multihilo en el caso async
        HttpRequestResultOKImpl result = HttpUtil.httpPost(servletPath, httpRequestData, paramList, null);
        if (!(result instanceof HttpRequestResultOKBeanshellImpl))
            throw new ItsNatDroidServerResponseException("Expected " + MimeUtil.MIME_BEANSHELL + " MIME in Content-Type:" + result.getMimeType(),result);

        HttpRequestResultOKBeanshellImpl resultBS = (HttpRequestResultOKBeanshellImpl)result;

        // No veo problemas de multithread en estas 4 líneas de código:
        PageItsNatImpl pageItsNat = eventSender.getItsNatDocItsNatImpl().getPageItsNatImpl();
        String pageURLBase = pageItsNat.getPageURLBase();
        XMLDOMLayoutPageItsNat xmlDOMLayoutPage = pageItsNat.getInflatedXMLLayoutPageItsNatImpl().getXMLDOMLayoutPageItsNat();
        String itsNatServerVersion = pageItsNat.getItsNatServerVersion();

        String code = resultBS.getResponseText();

        XMLDOMLayoutPageItsNatDownloader downloader = XMLDOMLayoutPageItsNatDownloader.createXMLDOMLayoutPageItsNatDownloader(xmlDOMLayoutPage,pageURLBase, httpRequestData,itsNatServerVersion,urlResDownloadedMap,xmlDOMParserContext);
        LinkedList<DOMAttrRemote> attrRemoteListBSParsed = downloader.parseBeanShellAndDownloadRemoteResources(code);

        if (attrRemoteListBSParsed != null)
            resultBS.setAttrRemoteListBSParsed(attrRemoteListBSParsed);

        return resultBS;
    }

    public static void onFinishOk(EventSender eventSender, EventGenericImpl evt, HttpRequestResultOKBeanshellImpl result)
    {
        try
        {
            eventSender.processResult(evt, result, true);
        }
        catch (Exception ex)
        {
            PageImpl page = eventSender.getEventManager().getItsNatDocItsNatImpl().getPageImpl();
            OnEventErrorListener errorListener = page.getOnEventErrorListener();
            if (errorListener != null)
            {
                HttpRequestResult resultError = (ex instanceof ItsNatDroidServerResponseException) ? ((ItsNatDroidServerResponseException) ex).getHttpRequestResult() : result;
                errorListener.onError(page, evt, ex, resultError);
            }
            else
            {
                if (ex instanceof ItsNatDroidException) throw (ItsNatDroidException) ex;
                else throw new ItsNatDroidException(ex);
            }
        }
    }


    public static void onFinishError(EventSender eventSender, Exception ex, EventGenericImpl evt)
    {
        HttpRequestResult result = (ex instanceof ItsNatDroidServerResponseException) ? ((ItsNatDroidServerResponseException) ex).getHttpRequestResult() : null;

        ItsNatDroidException exFinal = eventSender.convertExceptionAndFireEventMonitors(evt, ex);

        PageImpl page = eventSender.getEventManager().getItsNatDocItsNatImpl().getPageImpl();
        OnEventErrorListener errorListener = page.getOnEventErrorListener();
        if (errorListener != null)
        {
            errorListener.onError(page, evt, exFinal, result);
        }
        else
        {
            int errorMode = eventSender.getItsNatDocItsNatImpl().getClientErrorMode();
            if (errorMode != ClientErrorMode.NOT_CATCH_ERRORS)
            {
                // Error del servidor, lo normal es que haya lanzado una excepción
                ItsNatDocPageItsNatImpl itsNatDoc = eventSender.getItsNatDocItsNatImpl();
                itsNatDoc.showErrorMessage(true, result, exFinal, errorMode);
            }
            else throw exFinal;
        }

    }



    public void processResult(EventGenericImpl evt,HttpRequestResultOKBeanshellImpl result,boolean async)
    {
        ItsNatDocPageItsNatImpl itsNatDoc = getItsNatDocItsNatImpl();
        itsNatDoc.fireEventMonitors(false,false,evt);

        String responseText = result.getResponseText();
        itsNatDoc.evalEventResultScript(responseText, evt, result.getAttrRemoteListBSParsed());

        if (async) evtManager.returnedEvent(evt);
    }

    public ItsNatDroidException convertExceptionAndFireEventMonitors(EventGenericImpl evt, Exception ex)
    {
        ItsNatDocPageItsNatImpl itsNatDoc = getItsNatDocItsNatImpl();

        if (ex instanceof ItsNatDroidException && ex.getCause() instanceof SocketTimeoutException)
        {
            // Esperamos este error en el caso de timeout y lo tratamos de forma singular en fireEventMonitors (el segundo param a true)
            itsNatDoc.fireEventMonitors(false, true, evt);
        }
        else
        {
            itsNatDoc.fireEventMonitors(false, false, evt);
        }

        if (ex instanceof ItsNatDroidException) return (ItsNatDroidException)ex;
        else return new ItsNatDroidException(ex);
    }


}
