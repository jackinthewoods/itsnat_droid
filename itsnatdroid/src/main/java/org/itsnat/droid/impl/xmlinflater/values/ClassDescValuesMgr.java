package org.itsnat.droid.impl.xmlinflater.values;

import org.itsnat.droid.impl.xmlinflater.ClassDescMgr;
import org.itsnat.droid.impl.xmlinflater.XMLInflaterRegistry;
import org.itsnat.droid.impl.xmlinflater.values.classtree.ClassDescValues;
import org.itsnat.droid.impl.xmlinflater.values.classtree.ClassDescValuesArrayBase;
import org.itsnat.droid.impl.xmlinflater.values.classtree.ClassDescValuesItemNormal;
import org.itsnat.droid.impl.xmlinflater.values.classtree.ClassDescValuesStyle;

import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_ANIM;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_ANIMATOR;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_ARRAY;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_BOOL;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_COLOR;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_DIMEN;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_DRAWABLE;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_FLOAT;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_ID;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_INTEGER;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_INTEGER_ARRAY;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_LAYOUT;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_STRING;
import static org.itsnat.droid.impl.dom.values.XMLDOMValues.TYPE_STRING_ARRAY;

/**
 * Created by jmarranz on 30/04/14.
 */
public class ClassDescValuesMgr extends ClassDescMgr<ClassDescValues>
{
    public ClassDescValuesMgr(XMLInflaterRegistry parent)
    {
        super(parent);
        initClassDesc();
    }

    @Override
    public ClassDescValues get(String resourceTypeName)
    {
        return classes.get(resourceTypeName);
    }


    @Override
    protected void initClassDesc()
    {
        ClassDescValuesStyle style = new ClassDescValuesStyle(this);
        addClassDesc(style);

        // color, dimen etc

        ClassDescValuesItemNormal anim = new ClassDescValuesItemNormal(this,TYPE_ANIM);
        addClassDesc(anim);

        ClassDescValuesItemNormal animator = new ClassDescValuesItemNormal(this,TYPE_ANIMATOR);
        addClassDesc(animator);

        ClassDescValuesItemNormal bool = new ClassDescValuesItemNormal(this,TYPE_BOOL);
        addClassDesc(bool);

        ClassDescValuesItemNormal color = new ClassDescValuesItemNormal(this,TYPE_COLOR);
        addClassDesc(color);

        ClassDescValuesItemNormal dimen = new ClassDescValuesItemNormal(this,TYPE_DIMEN);
        addClassDesc(dimen);

        ClassDescValuesItemNormal dimenFloat = new ClassDescValuesItemNormal(this,TYPE_FLOAT); // ej <item name=".." type="dimen" format="float">...</item>
        addClassDesc(dimenFloat);

        ClassDescValuesItemNormal drawable = new ClassDescValuesItemNormal(this,TYPE_DRAWABLE);
        addClassDesc(drawable);

        ClassDescValuesItemNormal id = new ClassDescValuesItemNormal(this,TYPE_ID);
        addClassDesc(id);

        //ClassDescValuesItemNormal idPlus = new ClassDescValuesItemNormal(this,TYPE_ID_PLUS);
        //addClassDesc(idPlus);

        ClassDescValuesItemNormal integer = new ClassDescValuesItemNormal(this,TYPE_INTEGER);
        addClassDesc(integer);

        ClassDescValuesItemNormal string = new ClassDescValuesItemNormal(this,TYPE_STRING);
        addClassDesc(string);

        // Arrays
        ClassDescValuesArrayBase stringArray = new ClassDescValuesArrayBase(this,TYPE_STRING_ARRAY);
        addClassDesc(stringArray);
        ClassDescValuesArrayBase integerArray = new ClassDescValuesArrayBase(this,TYPE_INTEGER_ARRAY);
        addClassDesc(integerArray);
        ClassDescValuesArrayBase array = new ClassDescValuesArrayBase(this,TYPE_ARRAY);
        addClassDesc(array);

        // No hemos encontrado de layout y ningún ejemplo en values/ pero esperamos que sea similar a <drawable>
        ClassDescValuesItemNormal layout = new ClassDescValuesItemNormal(this,TYPE_LAYOUT);
        addClassDesc(layout);

    }
}
