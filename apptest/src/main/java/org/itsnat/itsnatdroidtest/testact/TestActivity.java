package org.itsnat.itsnatdroidtest.testact;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.itsnat.droid.ItsNatDroid;
import org.itsnat.droid.ItsNatDroidBrowser;
import org.itsnat.droid.ItsNatDroidRoot;
import org.itsnat.droid.impl.dommini.DMNode;
import org.itsnat.droid.impl.dommini.DOMMiniParser;
import org.itsnat.droid.impl.dommini.DOMMiniRender;
import org.itsnat.itsnatdroidtest.R;
import org.itsnat.itsnatdroidtest.testact.util.Assert;
import org.itsnat.itsnatdroidtest.testact.util.TestUtil;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;


public class TestActivity extends Activity implements ActionBar.TabListener
{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    protected TestActivityPagerAdapter mTestActivityPagerAdapter;
    protected ViewPager mViewPager;
    protected ItsNatDroidBrowser droidBrowser;
    public static String urlTestBase;
    protected String urlTestCore;
    protected String urlTestIncludeLayout;
    protected String urlTestRemDrawables;
    protected String urlTestRemAnimations1;
    protected String urlTestRemAnimations2;
    protected String urlTestRemCtrl;
    protected String urlTestStatelessCore;
    protected String urlTestComponents;
    protected String urlTestRemoteNoItsNat;

    //protected String urlTestCoreAttachServerLauncher;

    // protected Configuration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (dm.densityDpi != 320)
        {
            Assert.executeAllTests = false; // Las imágenes están copiadas y definidas en densidad xdpi, con otra densidad son cambiadas y algunos test fallan
            TestUtil.alertDialog(this, "Some internal tests are not executed because they're designed for density 320 dpi (xdpi) and your device is: " + dm.densityDpi);
        }
*/

        if (ItsNatDroidRoot.get() == null)
            ItsNatDroidRoot.init(getApplication());
        this.droidBrowser = ItsNatDroidRoot.get().createItsNatDroidBrowser();
        droidBrowser.setFileCacheMaxSize(10*1024);

        Intent intent = getIntent();

        this.urlTestBase = intent.getStringExtra("urlTestBase");
        String itsNatServlet = "ItsNatDroidServletExample";

        this.urlTestCore =           urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_core";
        this.urlTestIncludeLayout =  urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_include_slowloadmode_layout";
        this.urlTestRemDrawables =   urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_remote_drawables";
        this.urlTestRemAnimations1 =  urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_remote_animations_1";
        this.urlTestRemAnimations2 =  urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_remote_animations_2";
        this.urlTestRemCtrl =        urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_remote_ctrl";
        this.urlTestStatelessCore =  urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_stateless_core_initial";
        this.urlTestComponents =     urlTestBase + itsNatServlet + "?itsnat_doc_name=test_droid_components";
        this.urlTestRemoteNoItsNat = urlTestBase + "ItsNatDroidServletNoItsNat";

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // Muestra y activa el simbolito del back y define el android.R.id.home cuando se pulsa

        setContentView(R.layout.activity_test);

        // Set up the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mTestActivityPagerAdapter = new TestActivityPagerAdapter(getFragmentManager(),getResources());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTestActivityPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, put a tab to the action bar.
        for (int i = 0; i < mTestActivityPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mTestActivityPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


        testMisc();
        //org.itsnat.droid_compiled.impl.util.MapLightAndRealPerformTest.test();

        // this.configuration = this.getResources().getConfiguration();
    }

    public ViewPager getViewPager()
    {
        return mViewPager;
    }

    public TestActivityPagerAdapter getTestActivityPagerAdapter()
    {
        return mTestActivityPagerAdapter;
    }


    public ItsNatDroidBrowser getItsNatDroidBrowser()
    {
        return droidBrowser;
    }


    public String getURLTestBase()
    {
        return urlTestBase;
    }

    //String itsNatServlet = "ItsNatDroidServletExample";

    public String getUrlTestCore()
    {
        return urlTestCore;
    }

    public String getUrlTestIncludeLayout()
    {
        return urlTestIncludeLayout;
    }


    public String getUrlTestRemoteDrawables()
    {
        return urlTestRemDrawables;
    }

    public String getUrlTestRemoteAnimations1()
    {
        return urlTestRemAnimations1;
    }

    public String getUrlTestRemoteAnimations2()
    {
        return urlTestRemAnimations2;
    }

    public String getUrlTestRemCtrl()
    {
        return urlTestRemCtrl;
    }

    public String getUrlTestStatelessCore()
    {
        return urlTestStatelessCore;
    }

    public String getUrlTestComponents() { return urlTestComponents; }

    public String getUrlTestRemoteNoItsNat() { return urlTestRemoteNoItsNat; }


    //public String getUrlTestCoreAttachServerLauncher() { return urlTestCoreAttachServerLauncher; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            TestUtil.alertDialog(this,"Settings","Nothing to do");
            return true;
        }
        else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void clickHandler(View view)
    {
        Toast.makeText(this,"Executed onClick handler",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ItsNatDroid itsNatDroid = ItsNatDroidRoot.get();
        itsNatDroid.onConfigurationChanged(this, newConfig);

        // this.configuration = newConfig;

        // Configuration configuration = getResources().getConfiguration();
        //int orientation = this.getResources().getConfiguration().orientation;
    }

    private void testMisc()
    {
        String markup = "<root>Hello <b>I'm a robot</b></root>";
        StringReader input = new StringReader(markup);
        XmlPullParser parser = Xml.newPullParser();
        try
        {
            parser.setInput(input);
            parser.nextToken(); // XmlPullParser.START_TAG

            DMNode[] nodeArray = DOMMiniParser.parse(parser);

            if (parser.getEventType() != XmlPullParser.END_TAG) throw new RuntimeException("FAILED TEST");

            String res = DOMMiniRender.toString(nodeArray);
            Assert.assertEquals("Hello <b>I'm a robot</b>",res);
        }
        catch (XmlPullParserException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
