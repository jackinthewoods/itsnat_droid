package org.itsnat.droid.impl.xmlinflater.drawable.classtree;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.util.TypedValue;

import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.dom.drawable.DOMElemDrawable;
import org.itsnat.droid.impl.util.MiscUtil;
import org.itsnat.droid.impl.util.NamespaceUtil;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawableChildBase;
import org.itsnat.droid.impl.xmlinflated.drawable.ElementDrawableChildRoot;
import org.itsnat.droid.impl.xmlinflater.FieldContainer;
import org.itsnat.droid.impl.xmlinflater.PercFloatImpl;
import org.itsnat.droid.impl.xmlinflater.XMLInflaterContext;
import org.itsnat.droid.impl.xmlinflater.XMLInflaterRegistry;
import org.itsnat.droid.impl.xmlinflater.drawable.AttrDrawableContext;
import org.itsnat.droid.impl.xmlinflater.drawable.ClassDescDrawableMgr;
import org.itsnat.droid.impl.xmlinflater.drawable.XMLInflaterDrawable;

import java.util.ArrayList;

/**
 * Created by jmarranz on 10/11/14.
 */
public class ClassDescRotateDrawable extends ClassDescElementDrawableBased<RotateDrawable> implements ClassDescCallback
{
    protected FieldContainer<Drawable.ConstantState> rotateStateField;
    protected FieldContainer<Drawable> mDrawableField;
    protected FieldContainer<Boolean> mPivotXRelField;
    protected FieldContainer<Float> mPivotXField;
    protected FieldContainer<Boolean> mPivotYRelField;
    protected FieldContainer<Float> mPivotYField;
    protected FieldContainer<Float> mFromDegreesField;
    protected FieldContainer<Float> mCurrentDegreesField;
    protected FieldContainer<Float> mToDegreesField;


    public ClassDescRotateDrawable(ClassDescDrawableMgr classMgr,ClassDescElementDrawableBased<? super RotateDrawable> parent)
    {
        super(classMgr,"rotate",parent);

        this.rotateStateField = new FieldContainer<Drawable.ConstantState>(RotateDrawable.class, "mState");
        Class rotateStateClass = MiscUtil.resolveClass(RotateDrawable.class.getName() + "$RotateState");

        if (Build.VERSION.SDK_INT >= MiscUtil.MARSHMALLOW) // level 23, v6.0
            this.mDrawableField = new FieldContainer<Drawable>("android.graphics.drawable.DrawableWrapper", "mDrawable");
        else
            this.mDrawableField = new FieldContainer<Drawable>(rotateStateClass, "mDrawable");

        this.mPivotXRelField = new FieldContainer<Boolean>(rotateStateClass, "mPivotXRel");
        this.mPivotXField = new FieldContainer<Float>(rotateStateClass, "mPivotX");
        this.mPivotYRelField = new FieldContainer<Boolean>(rotateStateClass, "mPivotYRel");
        this.mPivotYField = new FieldContainer<Float>(rotateStateClass, "mPivotY");
        this.mFromDegreesField = new FieldContainer<Float>(rotateStateClass, "mFromDegrees");
        this.mCurrentDegreesField = new FieldContainer<Float>(rotateStateClass, "mCurrentDegrees");
        this.mToDegreesField = new FieldContainer<Float>(rotateStateClass, "mToDegrees");
    }

    @Override
    public ElementDrawableChildRoot createElementDrawableChildRoot(DOMElemDrawable rootElem, AttrDrawableContext attrCtx)
    {
        ElementDrawableChildRoot elementDrawableRoot = new ElementDrawableChildRoot();

        RotateDrawable drawable = new RotateDrawable();

        XMLInflaterContext xmlInflaterContext = attrCtx.getXMLInflaterContext();
        XMLInflaterDrawable xmlInflaterDrawable = attrCtx.getXMLInflaterDrawable();

        xmlInflaterDrawable.processChildElements(rootElem, elementDrawableRoot,attrCtx);
        ArrayList<ElementDrawableChildBase> childList = elementDrawableRoot.getElementDrawableChildList();

        XMLInflaterRegistry xmlInflaterRegistry = classMgr.getXMLInflaterRegistry();
        Drawable.ConstantState rotateState = rotateStateField.get(drawable);

        Drawable childDrawable = getDrawableChild("drawable", rootElem, xmlInflaterContext, childList);
        if (Build.VERSION.SDK_INT >= MiscUtil.MARSHMALLOW) // level 23, v6.0
            mDrawableField.set(drawable,childDrawable);
        else
            mDrawableField.set(rotateState,childDrawable);

        DOMAttr pivotXAttr = rootElem.getDOMAttribute(NamespaceUtil.XMLNS_ANDROID, "pivotX");
        PercFloatImpl pivotXObj = pivotXAttr != null ? xmlInflaterRegistry.getDimensionPercFloat(pivotXAttr.getResourceDesc(),xmlInflaterContext) : null;
        boolean pivotXRel;
        float pivotX;
        if (pivotXObj == null)
        {
            pivotXRel = true;
            pivotX = 0.5f;
        }
        else
        {
            pivotXRel = pivotXObj.getDataType() == TypedValue.TYPE_FRACTION;
            pivotX = pivotXObj.toFloatBasedOnDataType();
        }
        mPivotXRelField.set(rotateState,pivotXRel);
        mPivotXField.set(rotateState,pivotX);

        DOMAttr pivotYAttr = rootElem.getDOMAttribute(NamespaceUtil.XMLNS_ANDROID, "pivotY");
        PercFloatImpl pivotYObj = pivotYAttr != null ? xmlInflaterRegistry.getDimensionPercFloat(pivotYAttr.getResourceDesc(),xmlInflaterContext) : null;
        boolean pivotYRel;
        float pivotY;
        if (pivotYObj == null)
        {
            pivotYRel = true;
            pivotY = 0.5f;
        }
        else
        {
            pivotYRel = pivotYObj.getDataType() == TypedValue.TYPE_FRACTION;
            pivotY = pivotYObj.toFloatBasedOnDataType();
        }
        mPivotYRelField.set(rotateState,pivotYRel);
        mPivotYField.set(rotateState,pivotY);


        DOMAttr fromDegreesAttr = rootElem.getDOMAttribute(NamespaceUtil.XMLNS_ANDROID, "fromDegrees");
        float fromDegrees = fromDegreesAttr != null ? xmlInflaterRegistry.getFloat(fromDegreesAttr.getResourceDesc(),xmlInflaterContext) : 0.0f;
        mFromDegreesField.set(rotateState,fromDegrees);
        mCurrentDegreesField.set(rotateState,fromDegrees);

        DOMAttr toDegreesAttr = rootElem.getDOMAttribute(NamespaceUtil.XMLNS_ANDROID, "toDegrees");
        float toDegrees = toDegreesAttr != null ? xmlInflaterRegistry.getFloat(toDegreesAttr.getResourceDesc(),xmlInflaterContext) : 360.0f;
        mToDegreesField.set(rotateState,toDegrees);

        setCallback(childDrawable,drawable); // childDrawable no puede ser nulo

        elementDrawableRoot.setDrawable(drawable);

        return elementDrawableRoot;
    }


    @Override
    public Class<RotateDrawable> getDrawableOrElementDrawableClass()
    {
        return RotateDrawable.class;
    }

    @Override
    public boolean isAttributeIgnored(RotateDrawable resource, String namespaceURI, String name)
    {
        if (super.isAttributeIgnored(resource,namespaceURI,name))
            return true;

        if (NamespaceUtil.XMLNS_ANDROID.equals(namespaceURI))
        {
            // Se usan en tiempo de construcción
            return ("drawable".equals(name) || "pivotX".equals(name) || "pivotY".equals(name) || "fromDegrees".equals(name) || "toDegrees".equals(name));
        }
        return false;
    }

    //@SuppressWarnings("unchecked")
    protected void init()
    {
        super.init();


    }

}
