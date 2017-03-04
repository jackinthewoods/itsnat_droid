package org.itsnat.itsnatdroidtest.testact.local.asset;

import android.content.res.Resources;
import android.view.View;
import android.widget.AdapterViewFlipper;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;

import org.itsnat.droid.InflatedLayout;
import org.itsnat.itsnatdroidtest.R;
import org.itsnat.itsnatdroidtest.testact.TestActivity;
import org.itsnat.itsnatdroidtest.testact.TestActivityTabFragment;
import org.itsnat.itsnatdroidtest.testact.util.CustomScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jmarranz on 16/07/14.
 */
public class TestSetupAssetLayout1 extends TestSetupAssetLayoutBase
{
    public TestSetupAssetLayout1(TestActivityTabFragment fragment)
    {
        super(fragment);
    }

    public void test()
    {
        final TestActivity act = fragment.getTestActivity();
        final View compiledRootView = loadCompiledAndBindBackReloadButtons(R.layout.test_local_layout_1_compiled);

        final View buttonReload = compiledRootView.findViewById(R.id.buttonReload);
        buttonReload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TEST de carga dinámica de layout guardado localmente
                InflatedLayout layout = loadAssetAndBindBackReloadButtons("res/layout/test_local_layout_1_asset.xml"); // R.raw.test_local_layout_1_asset
                View dynamicRootView = layout.getItsNatDoc().getRootView();

                initialConfiguration(act, dynamicRootView);

                TestAssetLayout1.test((CustomScrollView) compiledRootView, (CustomScrollView) dynamicRootView, layout);
            }
        });

        initialConfiguration(act, compiledRootView);
    }

    private static void initialConfiguration(TestActivity act, View rootView)
    {
        defineAutoCompleteTextViewDropdown(act, rootView);
        defineAdapterViewAnimator(act, rootView);
        defineGridView(act, rootView);
        defineExpandableListView(act, rootView);
        defineSpinnerDialog(act, rootView);
        defineSpinnerDropdown(act, rootView);
    }

    private static void defineAdapterViewAnimator(TestActivity act, View rootView)
    {
        // Resources res = act.getResources();
        AdapterViewFlipper viewFlipper = (AdapterViewFlipper) rootView.findViewById(R.id.adapterViewAnimatorTestId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(act, R.array.sports_array, android.R.layout.simple_list_item_1);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewFlipper.setAdapter(adapter);
    }

    private static void defineGridView(TestActivity act, View rootView)
    {
        Resources res = act.getResources();
        GridView gridView = (GridView) rootView.findViewById(R.id.gridViewTestId);
        CharSequence[] entries = res.getTextArray(R.array.sports_array);
        gridView.setAdapter(new ArrayAdapter<CharSequence>(act, android.R.layout.simple_list_item_1, entries));
    }

    private static void defineExpandableListView(TestActivity act, View rootView)
    {
        // http://stackoverflow.com/questions/17636735/expandable-listview-in-fragment

        // Resources res = act.getResources();
        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.expanListViewTestId);

        final int NUM_GROUPS = 10;

        final String NAME = "NAME";
        final String IS_EVEN = "IS_EVEN";

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        for (int i = 0; i < NUM_GROUPS; i++) // 10 grupos
        {
            Map<String, String> curGroupMap = new HashMap<String, String>(); // Grupo
            groupData.add(curGroupMap);
            curGroupMap.put(NAME, "Group " + i);
            // Comentamos el segundo texto del item de grupo porque simple_expandable_list_item_1 sólo tiene text1
            //curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even" : "This group is odd"); // No se muestra porque
        }

        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        for (int i = 0; i < NUM_GROUPS; i++) // 10 grupos
        {
            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
            for (int j = 0; j < 2; j++)
            {
                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(NAME, "Child " + i + " " + j);
                curChildMap.put(IS_EVEN, (j % 2 == 0) ? "This child is even" : "This child is odd");
            }
            childData.add(children);
        }

        // Set up our adapter
        SimpleExpandableListAdapter mAdapter = new SimpleExpandableListAdapter(act, groupData, android.R.layout.simple_expandable_list_item_1, // https://github.com/android/platform_frameworks_base/blob/master/core/res/res/layout/simple_expandable_list_item_1.xml
                new String[]{NAME}, //new String[] { NAME, IS_EVEN },
                new int[]{android.R.id.text1}, // new int[] { android.R.id.text1, android.R.id.text2 },
                childData, android.R.layout.simple_expandable_list_item_2, // https://github.com/android/platform_frameworks_base/blob/master/core/res/res/layout/simple_expandable_list_item_2.xml
                new String[]{NAME, IS_EVEN}, new int[]{android.R.id.text1, android.R.id.text2});

        listView.setAdapter(mAdapter);
    }

    private static void defineSpinnerDialog(TestActivity act, View rootView)
    {
        // Resources res = act.getResources();
        Spinner gridView = (Spinner) rootView.findViewById(R.id.spinnerDialogTestId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(act, R.array.sports_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Aunque parece que es para dropdown sirve para dialog también y queda mejor que sin definir
        gridView.setAdapter(adapter);
    }

    private static void defineSpinnerDropdown(TestActivity act, View rootView)
    {
        // Resources res = act.getResources();
        Spinner gridView = (Spinner) rootView.findViewById(R.id.spinnerDropdownTestId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(act, R.array.sports_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gridView.setAdapter(adapter);
    }

    private static void defineAutoCompleteTextViewDropdown(TestActivity act, View rootView)
    {
        AutoCompleteTextView textView;
        ArrayAdapter<CharSequence> adapter;

        textView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextViewTestId);
        adapter = ArrayAdapter.createFromResource(act, R.array.sports_array, android.R.layout.simple_list_item_1);
        textView.setAdapter(adapter);

        textView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextViewTestId2);
        adapter = ArrayAdapter.createFromResource(act, R.array.sports_array, android.R.layout.simple_list_item_1);
        textView.setAdapter(adapter);
    }
}
