package org.itsnat.droid.impl.dom.anim;

import org.itsnat.droid.impl.dom.DOMElement;

/**
 * Created by jmarranz on 25/04/2016.
 */
public abstract class DOMElemAnimation extends DOMElement
{
    public DOMElemAnimation(String tagName, DOMElemAnimationSet parentElement)
    {
        super(tagName, parentElement);
    }
}
