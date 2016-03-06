package org.itsnat.droid.impl.xmlinflater.layout.attr.widget;

import android.content.Context;
import android.os.Build;
import android.view.View;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.util.MiscUtil;
import org.itsnat.droid.impl.xmlinflater.MethodContainer;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.ViewStyleAttr;
import org.itsnat.droid.impl.xmlinflater.layout.ViewStyleAttrDynamic;
import org.itsnat.droid.impl.xmlinflater.layout.classtree.ClassDescViewBased;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDesc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmarranz on 30/04/14.
 */
public class AttrDescView_widget_CalendarView_weekDayTextAppearance extends AttrDesc<ClassDescViewBased,View,AttrLayoutContext>
{
    protected MethodContainer<Boolean> method;

    public AttrDescView_widget_CalendarView_weekDayTextAppearance(ClassDescViewBased parent)
    {
        super(parent, "weekDayTextAppearance");

        if (Build.VERSION.SDK_INT <= MiscUtil.ICE_CREAM_SANDWICH_MR1) // 4.0.3 Level 15
            this.method = new MethodContainer<Boolean>(parent.getDeclaredClass(),"setUpHeader",new Class[]{int.class});
        else
            this.method = new MethodContainer<Boolean>(parent.getDeclaredClass(),"setWeekDayTextAppearance",new Class[]{int.class});
    }

    @Override
    public void setAttribute(View view, DOMAttr attr, AttrLayoutContext attrCtx)
    {
        Context ctx = attrCtx.getContext();
        ViewStyleAttr style = getViewStyle(attr, attrCtx.getXMLInflaterLayout());
        List<DOMAttr> styleItemsDynamicAttribs = (style instanceof ViewStyleAttrDynamic) ? new ArrayList<DOMAttr>() : null;
        int weekDayTextAppearanceResId = getViewStyle(style,styleItemsDynamicAttribs,ctx);

        if (weekDayTextAppearanceResId > 0)
            method.invoke(view, weekDayTextAppearanceResId);

        if (styleItemsDynamicAttribs != null)
        {
            ClassDescViewBased classDesc = getClassDesc();
            for(DOMAttr styleAttr : styleItemsDynamicAttribs)
            {
                classDesc.setAttributeOrInlineEventHandler(view,styleAttr,attrCtx);
            }
        }
    }


    @Override
    public void removeAttribute(View view, AttrLayoutContext attrCtx)
    {
        // Android tiene un estilo por defecto
    }

}
