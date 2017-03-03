package org.itsnat.droid.impl.browser.serveritsnat.evtlistener;

import android.view.View;

import org.itsnat.droid.impl.browser.serveritsnat.CustomFunction;
import org.itsnat.droid.impl.browser.serveritsnat.ItsNatDocPageItsNatImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.NormalEventImpl;
import org.itsnat.droid.impl.browser.serveritsnat.event.UserEventImpl;

/**
 * Created by jmarranz on 4/07/14.
 */
public class UserEventListener extends DOMExtEventListener
{
    protected String name;

    public UserEventListener(ItsNatDocPageItsNatImpl parent, View currentTarget, String name, CustomFunction customFunc, String id, int commMode, long timeout)
    {
        super(parent,"user",currentTarget,customFunc,id,commMode,timeout);
    }

    public String getName() { return name; }

    @Override
    public NormalEventImpl createNormalEvent(Object evt)
    {
        return new UserEventImpl(this,(UserEventImpl)evt);
    }
}
