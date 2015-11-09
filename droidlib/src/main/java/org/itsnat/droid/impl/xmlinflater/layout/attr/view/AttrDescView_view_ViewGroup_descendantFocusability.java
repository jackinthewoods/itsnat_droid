package org.itsnat.droid.impl.xmlinflater.layout.attr.view;

import android.view.ViewGroup;

import org.itsnat.droid.impl.util.MapSmart;
import org.itsnat.droid.impl.xmlinflater.layout.attr.AttrDescViewReflecMethodSingleName;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescView_view_ViewGroup_descendantFocusability extends AttrDescViewReflecMethodSingleName<Integer>
{
    public static final MapSmart<String,Integer> valueMap = MapSmart.<String,Integer>create( 3 );
    static
    {
        valueMap.put("beforeDescendants", ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        valueMap.put("afterDescendants",  ViewGroup.FOCUS_AFTER_DESCENDANTS);
        valueMap.put("blocksDescendants", ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    public AttrDescView_view_ViewGroup_descendantFocusability(ClassDescViewBased parent)
    {
        super(parent,"descendantFocusability",int.class,valueMap,"beforeDescendants");
    }

}
