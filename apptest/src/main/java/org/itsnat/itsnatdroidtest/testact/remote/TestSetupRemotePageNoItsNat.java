package org.itsnat.itsnatdroidtest.testact.remote;

import org.itsnat.droid.ItsNatDroidBrowser;
import org.itsnat.itsnatdroidtest.testact.TestActivityTabFragment;

/**
 * Created by jmarranz on 16/07/14.
 */
public class TestSetupRemotePageNoItsNat extends TestSetupRemotePageBase
{
    public TestSetupRemotePageNoItsNat(TestActivityTabFragment fragment, ItsNatDroidBrowser droidBrowser)
    {
        super(fragment,droidBrowser,false);
    }

    public void test(String url)
    {
        executePageRequest(url);
    }

}
