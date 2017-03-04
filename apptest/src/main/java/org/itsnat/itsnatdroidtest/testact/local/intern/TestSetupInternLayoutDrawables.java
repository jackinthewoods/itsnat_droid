package org.itsnat.itsnatdroidtest.testact.local.intern;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.graphics.drawable.RotateDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.widget.TextView;

import org.itsnat.droid.InflatedLayout;
import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.itsnatdroidtest.R;
import org.itsnat.itsnatdroidtest.testact.TestActivity;
import org.itsnat.itsnatdroidtest.testact.TestActivityTabFragment;
import org.itsnat.itsnatdroidtest.testact.local.TestSetupLocalLayoutBase;
import org.itsnat.itsnatdroidtest.testact.local.asset.TestSetupAssetLayoutBase;
import org.itsnat.itsnatdroidtest.testact.util.TestUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by jmarranz on 16/07/14.
 */
public class TestSetupInternLayoutDrawables extends TestSetupAssetLayoutBase
{
    public TestSetupInternLayoutDrawables(final TestActivityTabFragment fragment)
    {
        super(fragment);
    }

    protected TestActivity getTestActivity()
    {
        return fragment.getTestActivity();
    }

    private void showNotFirstLoadedError()
    {
        TestActivity act = getTestActivity();
        TestUtil.alertDialog(act,"No remotely delivered intern files, must be downloaded before, click on CLEAN/RELOAD REMOTE INTERN (DRAWABLES)");
    }

    public void test()
    {
        TestActivity act = getTestActivity();
        File dirRoot = act.getDir(TestSetupLocalLayoutBase.internLocationBase, Context.MODE_PRIVATE);
        // Util.cleanFileTree(dirRoot);
        if (!dirRoot.exists() || dirRoot.listFiles() == null)
        {
            showNotFirstLoadedError();
            return;
        }

        // TEST de carga dinámica de layout guardado localmente
        InflatedLayout layout;
        try
        {
            layout = loadInternAndBindBackReloadButtons("res/layout/test_local_layout_drawables_intern.xml");
        }
        catch(ItsNatDroidException ex)
        {
            if (ex.getCause() instanceof FileNotFoundException)
            {
                showNotFirstLoadedError();
                return;
            }
            throw ex;
        }

        View dynamicRootView = layout.getItsNatDoc().getRootView();

        initialConfiguration(getTestActivity(), dynamicRootView);
    }

    private static void initialConfiguration(TestActivity act, View rootView)
    {

        TextView testClipDrawable = (TextView) rootView.findViewById(R.id.testClipDrawableId);
        ((ClipDrawable) testClipDrawable.getBackground()).setLevel(5000); // La mitad se verá

        TextView testClipDrawable2 = (TextView) rootView.findViewById(R.id.testClipDrawableId2);
        ((ClipDrawable) testClipDrawable2.getBackground()).setLevel(5000); // La mitad se verá


        TextView testLevelListDrawable = (TextView) rootView.findViewById(R.id.testLevelListDrawableId);
        final LevelListDrawable levelListDrawable = (LevelListDrawable) testLevelListDrawable.getBackground();
        levelListDrawable.setLevel(1);
        testLevelListDrawable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                levelListDrawable.setLevel(4);
            }
        });

        TextView testTransitionDrawable = (TextView) rootView.findViewById(R.id.testTransitionDrawableId);
        final TransitionDrawable transitionDrawable = (TransitionDrawable) testTransitionDrawable.getBackground();
        testTransitionDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionDrawable.startTransition(1000); // Begin the second layer on top of the first layer.
            }
        });

        TextView testScaleDrawable = (TextView) rootView.findViewById(R.id.testScaleDrawableId);
        ScaleDrawable scaleDrawable = (ScaleDrawable) testScaleDrawable.getBackground();
        scaleDrawable.setLevel(1);

        TextView testAnimationDrawable = (TextView) rootView.findViewById(R.id.testAnimationDrawableId);
        AnimationDrawable animationDrawable = (AnimationDrawable) testAnimationDrawable.getBackground();
        animationDrawable.start();

        TextView testRotateDrawable = (TextView) rootView.findViewById(R.id.testRotateDrawableId);
        RotateDrawable rotateDrawable = (RotateDrawable) testRotateDrawable.getBackground();
        rotateDrawable.setLevel(10000); // 0 (minimum) to 10000 (maximum) para que rote los 45%
    }
}
