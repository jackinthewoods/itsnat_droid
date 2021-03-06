package org.itsnat.droid.impl.dom.animator;

import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.impl.dom.DOMElement;

/**
 * Created by jmarranz on 21/03/2016.
 */
public class DOMElemAnimatorValue extends DOMElemAnimator
{
    public DOMElemAnimatorValue(DOMElemAnimatorSet parentElement)
    {
        super("animator", parentElement);
    }

    public DOMElemAnimatorValue(String tagName,DOMElemAnimatorSet parentElement)
    {
        super(tagName, parentElement);
    }

    @Override
    public void addChildDOMElement(DOMElement domElement)
    {
        throw new ItsNatDroidException("This XML element " + tagName + " has no child elements");
    }
}
