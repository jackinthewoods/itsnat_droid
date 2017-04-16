package org.itsnat.droid.impl.xmlinflated.menu;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.dom.DOMElement;
import org.itsnat.droid.impl.util.NamespaceUtil;
import org.itsnat.droid.impl.xmlinflater.XMLInflaterRegistry;
import org.itsnat.droid.impl.xmlinflater.menu.AttrMenuContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmarranz on 30/11/14.
 */
public class ElementMenuChildMenuItem extends ElementMenuChildNormal
{
    protected MenuItem menuItem;


    public ElementMenuChildMenuItem(ElementMenuChildBased parentElementMenu,DOMElement domElement,AttrMenuContext attrCtx)
    {
        super(parentElementMenu,domElement,attrCtx);

        // parentElementMenu es siempre el <menu> root o un submenu o un <group>

        if (parentElementMenu instanceof ElementMenuChildGroup)
        {
            ElementMenuChildRoot childMenuRoot = getParentElementMenuChildRoot(parentElementMenu);
            Menu rootMenu = childMenuRoot.getMenu();
            int groupId = ((ElementMenuChildGroup)parentElementMenu).getGroupId();

            int menuCategory = this.menuCategory;
            if (menuCategory == Menu.NONE)
                menuCategory = ((ElementMenuChildGroup)parentElementMenu).menuCategory;

            if (!this.checkeableExits) // Boolean.FALSE, no local
                checkeable = ((ElementMenuChildGroup) parentElementMenu).checkeable;

            if (!this.checkedExits)
                checked = ((ElementMenuChildGroup)parentElementMenu).checked;

            if (!this.enabledExits)
                enabled = ((ElementMenuChildGroup)parentElementMenu).enabled;

            if (!this.visibleExits)
                visible = ((ElementMenuChildGroup)parentElementMenu).visible;

            this.menuItem = rootMenu.add(groupId,itemId,menuCategory,""); // Cojemos el idemId del item sea cual sea
            menuItem.setCheckable(checkeable);
            menuItem.setChecked(checked);
            menuItem.setEnabled(enabled);
            menuItem.setVisible(visible);

            return;
        }

        if (parentElementMenu instanceof ElementMenuChildSubMenu)
        {


            // Hijo de SubMenu
            ElementMenuChildSubMenu childSubMenu = ((ElementMenuChildSubMenu)parentElementMenu);
            int groupId = childSubMenu.getGroupId();
            this.menuItem = childSubMenu.getSubMenu().add(groupId,itemId,menuCategory,"");
            return;
        }

        {
            // Vemos si es padre "visual" de un SubMenu, este padre visual es nuestro y queda pero no integrado en el SubMenu que crea el suyo por lo que al final
            // se duplican, el primero el nuestro no funcional y el segundo el de el SubMenu si es funcional
            List<DOMElement> childList =  domElement.getChildDOMElementList();
            if (childList != null && !childList.isEmpty())
            {
                DOMElement child = childList.get(0);
                if (child.getTagName().equals("menu"))
                {
                    // Hay un elemento hijo que es el <menu> esperado para un SubMenu, como este <item> tiene que desaparecer no hay que renderizarlo uniendo al árbol nativo, creamos un item via add
                    // pero lo eliminamos enseguida
                    if (itemId == 0) throw new ItsNatDroidException("id cannot be zero in this context <item>");
                    ElementMenuChildRoot childMenuRoot = (ElementMenuChildRoot) parentElementMenu;
                    this.menuItem = childMenuRoot.getMenu().add(Menu.NONE, itemId, menuCategory, ""); // No hace falta groupId, nos lo vamos a cargar y el nuevo SubMenu creado tendrá un groupId correcto

                    childMenuRoot.getMenu().removeItem(itemId); // Nos lo cargamos para que no se senderice si estuviera unido al árbol nativo
                    return;
                }
            }
        }

        if (parentElementMenu instanceof ElementMenuChildRoot)
        {
            // Resto de casos que no son <group> ni SubMenu (ni item padre ni item hijo), debe ser el último, no es necesario un groupId porque no pertenece a un grupo (group o submenu)
            ElementMenuChildRoot childMenuRoot = (ElementMenuChildRoot) parentElementMenu;
            this.menuItem = childMenuRoot.getMenu().add(Menu.NONE, itemId, menuCategory, "");
            return;
        }
    }


    public MenuItem getMenuItem()
    {
        return menuItem;
    }

}
