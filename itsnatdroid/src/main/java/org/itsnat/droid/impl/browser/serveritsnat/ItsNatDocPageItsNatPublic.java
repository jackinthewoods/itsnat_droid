package org.itsnat.droid.impl.browser.serveritsnat;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import org.itsnat.droid.event.UserEvent;
import org.itsnat.droid.impl.browser.ItsNatDocPagePublic;

/**
 * Métodos llamados por el servidor pero ninguno público para el usuario
 *
 * Created by jmarranz on 8/07/14.
 */
public interface ItsNatDocPageItsNatPublic extends ItsNatDocPagePublic
{
    public void init(String stdSessionId, String sessionToken, String sessionId, String clientId, String servletPath, int errorMode, String attachType, boolean eventsEnabled);

    public void setDisabledEvents();
    public void onServerStateLost();

    public Node getNode(Object[] idObj);
    public View getView(Object[] idObj);

    public void setAttribute(Node node, String name, String value);
    public void setAttribute2(Object[] idObj, String name, String value);
    public void setAttributeNS(Node node, String namespaceURI, String name, String value);
    public void setAttributeNS2(Object[] idObj, String namespaceURI, String name, String value);

    public void setAttrBatch(Node node, String namespaceURI, String[] attrNames, String[] attrValues);

    public void removeAttribute(Node node, String name);
    public void removeAttribute2(Object[] idObj, String name);
    public void removeAttributeNS(Node node, String namespaceURI, String name);
    public void removeAttributeNS2(Object[] idObj, String namespaceURI, String name);

    public Node createElement(String name);
    public Node createElementNS(String namespaceURI, String name);

    public void insertBefore(Node parentNode, Node newChild, Node childRef);
    public void insertBefore2(Node parentNode, Node newChild, Node childRef, String newId);
    public void insertBefore3(Object[] parentIdObj, Node newChild, Object[] childRefIdObj, String newId);

    public void appendChild(Node parentNode, Node newChild);
    public void appendChild2(Node parentNode, Node newChild, String newId);
    public void appendChild3(Object[] idObj, Node newChild, String newId);

    public void removeChild(Node child); // Realmente no es público en ItsNatDroid (si lo es en web), por simetría lo ponemos
    public void removeChild2(String id, boolean isText);
    public void removeChild3(Object[] parentIdObj, String childRelPath, boolean isText);

    public void removeAllChild2(Object[] parentIdObj);

    public Node addNodeCache(Object[] idObj);
    public void removeNodeCache(String[] idList);
    public void clearNodeCache();

    public void setInnerXML(Node parentNode, String markup);
    public void setInnerXML2(Object[] idObj, String markup);
    public void setInnerXML(Node parentNode, String className, String markup);
    public void setInnerXML2(Object[] idObj, String className, String markup);

    public void addDroidEL(Object[] idObj, String type, String listenerId, CustomFunction customFunction, boolean useCapture, int commMode, long timeout, int eventGroupCode);
    public void removeDroidEL(String listenerId);

    public void addGlobalEL(GlobalEventListener listener);
    public void removeGlobalEL(GlobalEventListener listener);

    public void sendContinueEvent(Object[] idObj, String listenerId, CustomFunction customFunc, int commMode, long timeout);

    public void addUserEL(Object[] idObj, String name, String listenerId, CustomFunction customFunc, int commMode, long timeout);
    public void removeUserEL(String listenerId);

    public void sendAsyncTaskEvent(Object[] idObj, String listenerId, CustomFunction customFunc, int commMode, long timeout);

    public void addTimerEL(Object[] idObj, String listenerId, CustomFunction customFunc, int commMode, long timeout, long delay);
    public void removeTimerEL(String listenerId);
    public void updateTimerEL(String listenerId, long delay);

    public void sendCometTaskEvent(String listenerId, CustomFunction customFunc, int commMode, long timeout);

    public MotionEvent createMotionEvent(String type, float x, float y);
    public KeyEvent createKeyEvent(String type, int keyCode);
    public Boolean createFocusEvent(boolean hasFocus);
    public CharSequence createTextChangeEvent(CharSequence newText);
    public Object createOtherEvent();

    public boolean dispatchEvent(Node node, String type, Object nativeEvt);
    public boolean dispatchEvent2(Object[] idObj, String type, Object nativeEvt);

    public boolean dispatchUserEvent2(Object[] idObj, UserEvent evt);

    public void initAttachTimerRefresh(final int interval, final int commMode, final long timeout);
    public void stopAttachTimerRefresh();
    public void sendAttachCometTaskRefresh(String listenerId, int commMode, long timeout);
    public void addAttachUnloadListener(int commMode);

}
