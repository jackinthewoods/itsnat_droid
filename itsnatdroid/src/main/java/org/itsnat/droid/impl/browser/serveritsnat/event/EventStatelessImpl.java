package org.itsnat.droid.impl.browser.serveritsnat.event;

import org.itsnat.droid.event.EventStateless;
import org.itsnat.droid.impl.browser.serveritsnat.ItsNatDocPageItsNatImpl;
import org.itsnat.droid.impl.browser.serveritsnat.evtlistener.EventStatelessListener;

/**
 * Created by jmarranz on 7/07/14.
 */
public class EventStatelessImpl extends EventGenericImpl implements EventStateless
{
    /*
    Creates the private instance
     */
    public EventStatelessImpl(ItsNatDocPageItsNatImpl parent, EventStatelessImpl publicEvt, int commMode, long timeout)
    {
        super(new EventStatelessListener(parent,commMode,timeout));

        this.extraParams = publicEvt.extraParams;
    }

    /*
    Creates the public instance
     */
    public EventStatelessImpl()
    {
        super(null);
    }

    public EventStatelessListener getEventStatelessListener()
    {
        return (EventStatelessListener)listener;
    }


    @Override
    public void saveEvent()
    {
    }
}
