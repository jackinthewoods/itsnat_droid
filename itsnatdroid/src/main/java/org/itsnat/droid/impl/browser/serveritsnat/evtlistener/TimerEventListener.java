package org.itsnat.droid.impl.browser.serveritsnat.evtlistener;

import android.view.View;

import org.itsnat.droid.impl.browser.serveritsnat.CustomFunction;
import org.itsnat.droid.impl.browser.serveritsnat.ItsNatDocPageItsNatImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.DOMExtEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.NormalEventImpl;

/**
 * Created by jmarranz on 4/07/14.
 */
public class TimerEventListener extends DOMExtEventListener
{
    protected Runnable callback;

    public TimerEventListener(ItsNatDocPageItsNatImpl parent, View currentTarget, CustomFunction customFunc, String id, int commMode, long timeout)
    {
        super(parent,"timer",currentTarget,customFunc,id,commMode,timeout);
    }

    @Override
    public NormalEventImpl createNormalEvent(Object evt)
    {
        return new DOMExtEventImpl(this);
    }

    public Runnable getCallback()
    {
        return callback;
    }

    public void setCallback(Runnable callback)
    {
        this.callback = callback;
    }
}
