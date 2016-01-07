package org.itsnat.droid.impl.xmlinflater.values.classtree;

import org.itsnat.droid.impl.xmlinflated.values.ElementValues;
import org.itsnat.droid.impl.xmlinflated.values.ElementValuesChildNoChildElem;
import org.itsnat.droid.impl.xmlinflated.values.ElementValuesColor;
import org.itsnat.droid.impl.xmlinflater.values.ClassDescValuesMgr;

/**
 * Created by jmarranz on 07/01/2016.
 */
public class ClassDescValuesColor extends ClassDescValuesChildNoChildElem<ElementValuesColor>
{
    public ClassDescValuesColor(ClassDescValuesMgr classMgr)
    {
        super(classMgr, "color");
    }

    @Override
    public ElementValuesChildNoChildElem createElementValuesChildNoChildren(ElementValues parentChildValues, String name, String value)
    {
        return new ElementValuesColor(parentChildValues, name, value);
    }

}