package org.itsnat.droid.impl.xmlinflater.layout.classtree;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;

import org.itsnat.droid.AttrResourceInflaterListener;
import org.itsnat.droid.ItsNatDroidException;
import org.itsnat.droid.impl.browser.PageImpl;
import org.itsnat.droid.impl.browser.serveritsnat.NodeToInsertImpl;
import org.itsnat.droid.impl.dom.DOMAttr;
import org.itsnat.droid.impl.dom.DOMAttrRemote;
import org.itsnat.droid.impl.dom.DOMAttributeMap;
import org.itsnat.droid.impl.dom.layout.DOMElemView;
import org.itsnat.droid.impl.util.IOUtil;
import org.itsnat.droid.impl.util.MiscUtil;
import org.itsnat.droid.impl.util.NamespaceUtil;
import org.itsnat.droid.impl.util.StringUtil;
import org.itsnat.droid.impl.xmlinflater.MethodContainer;
import org.itsnat.droid.impl.xmlinflater.XMLInflaterContext;
import org.itsnat.droid.impl.xmlinflater.layout.AttrLayoutContext;
import org.itsnat.droid.impl.xmlinflater.layout.ClassDescViewMgr;
import org.itsnat.droid.impl.xmlinflater.layout.PendingPostInsertChildrenTasks;
import org.itsnat.droid.impl.xmlinflater.layout.PendingViewPostCreateProcess;
import org.itsnat.droid.impl.xmlinflater.layout.PendingViewPostCreateProcessChildGridLayout;
import org.itsnat.droid.impl.xmlinflater.layout.PendingViewPostCreateProcessDefault;
import org.itsnat.droid.impl.xmlinflater.layout.ViewStyleAttribs;
import org.itsnat.droid.impl.xmlinflater.layout.XMLInflaterLayout;
import org.itsnat.droid.impl.xmlinflater.layout.page.XMLInflaterLayoutPageItsNat;
import org.itsnat.droid.impl.xmlinflater.shared.attr.AttrDesc;
import org.itsnat.droid.impl.xmlinflater.shared.classtree.ClassDesc;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jmarranz on 30/04/14.
 */
public class ClassDescViewBased extends ClassDesc<View>
{
    protected static MethodContainer<ViewGroup.LayoutParams> methodGenerateLP =
                            new MethodContainer<ViewGroup.LayoutParams>(ViewGroup.class, "generateDefaultLayoutParams");

    protected Class<View> clasz;
    protected Constructor<? extends View> constructor1P;
    protected Constructor<? extends View> constructor3P;

    public ClassDescViewBased(ClassDescViewMgr classMgr, String className, ClassDescViewBased parentClass)
    {
        super(classMgr, className, parentClass);
    }

    @Override
    protected void init()
    {
        initClass();

        super.init();
    }

    public Class<View> getDeclaredClass()
    {
        return clasz;
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends View> initClass()
    {
        // El motivo de ésto es evitar usar el .class lo que obliga a cargar la clase aunque no se use, así la clase nativa se carga cuando se necesita por primera vez
        if (clasz == null)
        {
            this.clasz = resolveClass();
        }
        return clasz;
    }

    @SuppressWarnings("unchecked")
    protected Class<View> resolveClass()
    {
        String className = getClassOrDOMElemName();
        return (Class<View>)MiscUtil.resolveClass(className);
    }

    public ClassDescViewMgr getClassDescViewMgr()
    {
        return (ClassDescViewMgr) classMgr;
    }

    public ClassDescViewBased getParentClassDescViewBased()
    {
        return (ClassDescViewBased) getParentClassDesc();
    }

    private static boolean isStyleAttribute(String namespaceURI, String name)
    {
        return StringUtil.isEmpty(namespaceURI) && name.equals("style");
    }

    private static boolean isClassAttribute(String namespaceURI, String name)
    {
        return StringUtil.isEmpty(namespaceURI) && name.equals("class"); // En teoría es sólo en el caso de <view class="..."> pero me da pereza chequear el "view", el que use el atributo class en otro contexto es que es tonto
    }

    protected boolean isAttributeIgnored(String namespaceURI, String name)
    {
        return isStyleAttribute(namespaceURI, name) || isClassAttribute(namespaceURI, name) ||  // Se trata de forma especial en otro lugar
                NamespaceUtil.XMLNS_ITSNATDROID_RESOURCE.equals(namespaceURI); // No se hace nada con ellos, son para precargar y acceder via ItsNatResources.getLayout() etc
    }

    public boolean setAttributeOrInlineEventHandler(final View view, final DOMAttr attr, final AttrLayoutContext attrCtx)
    {
        // Devolvemos true si consideramos "procesado", esto incluye que sea ignorado o procesado custom

        XMLInflaterLayout xmlInflaterLayout = attrCtx.getXMLInflaterLayout();
        if (xmlInflaterLayout.setAttributeInlineEventHandler(view, attr)) // ej onclick="..."
            return true;

        return setAttribute(view, attr, attrCtx);
    }

    public boolean setAttribute(View view,DOMAttr attr,AttrLayoutContext attrCtx)
    {
        String namespaceURI = attr.getNamespaceURI();
        String name = attr.getName(); // El nombre devuelto no contiene el namespace
        String value = attr.getValue();


        if ("background".equals(name))
        {
            if("@assets:drawable/res/drawable/test_state_list_drawable_asset.xml".equals(attr.getValue()))
            {
                attr.getValue();
            }
        }

        try
        {
            if (setAttributeThisClass(view,attr,attrCtx))
                return true;

            return processSetAttrCustom(view, namespaceURI, name, value, attrCtx.getXMLInflaterContext());
        }
        catch (Exception ex)
        {
            throw new ItsNatDroidException("Error setting attribute: " + namespaceURI + " " + name + " " + value + " in object " + view, ex);
        }
    }

    protected boolean setAttributeThisClass(final View view, final DOMAttr attr, final AttrLayoutContext attrCtx)
    {
        // Devolvemos true si consideramos "procesado", esto incluye que sea ignorado o procesado custom

        if (!isInit()) init();

        String namespaceURI = attr.getNamespaceURI();
        String name = attr.getName(); // El nombre devuelto no contiene el namespace
        //String value = attr.getValue();

        if (isAttributeIgnored(namespaceURI, name))
            return true; // Se trata de forma especial en otro lugar

        final AttrDesc<ClassDescViewBased, View, AttrLayoutContext> attrDesc = this.<ClassDescViewBased, View, AttrLayoutContext>getAttrDesc(namespaceURI, name);
        if (attrDesc != null)
        {
            Runnable task = new Runnable()
            {
                @Override
                public void run()
                {
                    attrDesc.setAttribute(view, attr, attrCtx);
                }
            };
            if (DOMAttrRemote.isPendingToDownload(attr)) // Hay al menos un caso es el cuando una página remota NO generada por ItsNat tiene Views que por ejemplo referencian recursos remotos tal y como drawables, al menos así damos más soporte a las páginas no ItsNat
                AttrDesc.processDownloadTask((DOMAttrRemote) attr, task, attrCtx.getXMLInflaterContext());
            else
                task.run();

            return true;
        }
        else
        {
            // Es importante recorrer las clases de abajo a arriba pues algún atributo se repite en varios niveles tal y como minHeight y minWidth
            // y tiene prioridad la clase más derivada
            ClassDescViewBased parentClass = getParentClassDescViewBased();
            if (parentClass != null)
            {
                return parentClass.setAttributeThisClass(view, attr, attrCtx);
            }

            return false;
        }

    }

    public boolean removeAttributeOrInlineEventHandler(View view, String namespaceURI, String name, AttrLayoutContext attrCtx)
    {
        XMLInflaterLayout xmlInflaterLayout = attrCtx.getXMLInflaterLayout();
        if (xmlInflaterLayout.removeAttributeInlineEventHandler(view, namespaceURI, name)) // ej onclick="..."
            return true;

        return removeAttribute(view, namespaceURI, name, attrCtx);
    }

    public boolean removeAttribute(View view, String namespaceURI, String name, AttrLayoutContext attrCtx)
    {
        try
        {
            if (removeAttributeThisClass(view,namespaceURI,name,attrCtx))
                return true;

            return processRemoveAttrCustom(view, namespaceURI, name, attrCtx.getXMLInflaterContext());
        }
        catch(Exception ex)
        {
            throw new ItsNatDroidException("Error removing attribute: " + namespaceURI + " " + name + " in object " + view, ex);
        }
    }

    protected boolean removeAttributeThisClass(View view, String namespaceURI, String name, AttrLayoutContext attrCtx)
    {
        if (!isInit()) init();

        if (isAttributeIgnored(namespaceURI,name))
            return true; // Se trata de forma especial en otro lugar

        AttrDesc<ClassDescViewBased,View,AttrLayoutContext> attrDesc = this.<ClassDescViewBased,View,AttrLayoutContext>getAttrDesc(namespaceURI, name);
        if (attrDesc != null)
        {
            attrDesc.removeAttribute(view,attrCtx);
            // No tiene mucho sentido añadir isPendingToDownload etc aquí, no encuentro un caso de que al eliminar el atributo el valor por defecto a definir sea remoto aunque sea un drawable lo normal será un "@null" o un drawable por defecto nativo de Android
            return true;
        }
        else
        {
            ClassDescViewBased parentClass = getParentClassDescViewBased();
            if (parentClass != null)
            {
                return parentClass.removeAttributeThisClass(view, namespaceURI, name, attrCtx);
            }

            return false;
        }

    }

    private boolean processSetAttrCustom(View view, String namespaceURI, String name, String value, XMLInflaterContext xmlInflaterContext)
    {
        // No se encuentra opción de proceso custom
        AttrResourceInflaterListener listener = xmlInflaterContext.getAttrResourceInflaterListener();
        if(listener!=null)
        {
            PageImpl page = xmlInflaterContext.getPageImpl(); // Puede ser null
            return listener.setAttribute(page, view, namespaceURI, name, value);
        }
        return false;
    }

    private boolean processRemoveAttrCustom(View view, String namespaceURI, String name, XMLInflaterContext xmlInflaterContext)
    {
        // No se encuentra opción de proceso custom
        AttrResourceInflaterListener listener = xmlInflaterContext.getAttrResourceInflaterListener();
        if(listener!=null)
        {
            PageImpl page = xmlInflaterContext.getPageImpl(); // Puede ser null
            return listener.removeAttribute(page, view, namespaceURI, name);
        }
        return false;
    }

    public PendingViewPostCreateProcess createPendingViewPostCreateProcess(View view, ViewGroup viewParent)
    {
        // Se redefine en un caso
        return (viewParent instanceof GridLayout)
                     ? new PendingViewPostCreateProcessChildGridLayout(view) // No llevar este código a ClassDescView_widget_GridLayout porque es el caso DE View PADRE y este ClassDesc es un hijo, NO es GridLayout
                     : new PendingViewPostCreateProcessDefault(view);
    }

    private void addViewObject(View view,ViewGroup viewParent,int index,PendingViewPostCreateProcess pendingViewPostCreateProcess)
    {
        pendingViewPostCreateProcess.executePendingLayoutParamsTasks(); // Aquí ya definimos los atributos para los LayoutParams inmediatamente antes de añadir al View padre que es más o menos lo que se hace en addView antes de añadir al padre y después de definir el LayoutParams del View via generateDefaultLayoutParams(AttributeSet)

        if (viewParent != null) // Si es null es el caso de root view
        {
            if (index < 0) viewParent.addView(view);
            else viewParent.addView(view, index);
        }

        pendingViewPostCreateProcess.executePendingPostAddViewTasks(); // Aunque sea el root lo llamamos pues de otra manera podemos dejar alguna acción sin ejecutar
    }

    public View createRootViewObjectAndFillAttributes(DOMElemView rootDOMElemView,XMLInflaterLayout xmlInflaterLayout, PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks)
    {
        ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams(null);
        List<DOMAttr> styleLayoutParamsAttribs = new ArrayList<DOMAttr>(); // capacity = 12
        List<DOMAttr> styleDynamicAttribs = new ArrayList<DOMAttr>(); // capacity = 12

        View rootView = createViewObject(rootDOMElemView.getDOMAttributeMap(), xmlInflaterLayout.getXMLInflaterContext(),layoutParams,styleLayoutParamsAttribs,styleDynamicAttribs, pendingPostInsertChildrenTasks);
        xmlInflaterLayout.getInflatedXMLLayoutImpl().setRootView(rootView); // Lo antes posible porque los inline event handlers lo necesitan, es el root View del template, no el View.getRootView() pues una vez insertado en la actividad de alguna forma el verdadero root cambia

        if (styleLayoutParamsAttribs.isEmpty()) styleLayoutParamsAttribs = null;
        if (styleDynamicAttribs.isEmpty()) styleDynamicAttribs = null;

        rootView.setLayoutParams(layoutParams);

        fillViewAttributesAndAdd(rootView, null, -1, rootDOMElemView.getDOMAttributes(), styleLayoutParamsAttribs, styleDynamicAttribs, xmlInflaterLayout, pendingPostInsertChildrenTasks);

        return rootView;
    }

    public View createViewObjectAndFillAttributesAndAdd(ViewGroup viewParent, DOMAttributeMap attributeMap,int index,XMLInflaterLayout xmlInflaterLayout, PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks)
    {
        // viewParent es NO nulo, de otra manera el método se llamaría createRootView... pues el caso root lo tratamos siempre aparte
        ViewGroup.LayoutParams layoutParams = generateDefaultLayoutParams(viewParent);
        List<DOMAttr> styleLayoutParamsAttribs = new ArrayList<DOMAttr>(); // capacity = 12
        List<DOMAttr> styleDynamicAttribs = new ArrayList<DOMAttr>(); // capacity = 12

        View view = createViewObject(attributeMap, xmlInflaterLayout.getXMLInflaterContext(), layoutParams, styleLayoutParamsAttribs,styleDynamicAttribs, pendingPostInsertChildrenTasks);

        if (styleLayoutParamsAttribs.isEmpty()) styleLayoutParamsAttribs = null;
        if (styleDynamicAttribs.isEmpty()) styleDynamicAttribs = null;

        view.setLayoutParams(layoutParams);

        fillViewAttributesAndAdd(view, viewParent, index, attributeMap.getDOMAttributes(), styleLayoutParamsAttribs, styleDynamicAttribs, xmlInflaterLayout, pendingPostInsertChildrenTasks);

        return view;
    }


    private ViewStyleAttribs findStyleAttribute(DOMAttributeMap attribMap, XMLInflaterContext xmlInflaterContext)
    {
        DOMAttr domAttr = findAttribute(null, "style", attribMap);
        if (domAttr == null)
            return null;
        return getXMLInflaterRegistry().getViewStyle(domAttr.getResourceDesc(), xmlInflaterContext);
    }

    private View createViewObject(DOMAttributeMap attributeMap,XMLInflaterContext xmlInflaterContext, ViewGroup.LayoutParams layoutParams,List<DOMAttr> styleLayoutParamsAttribs,List<DOMAttr> styleDynamicAttribs, PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks)
    {
        ViewStyleAttribs style = findStyleAttribute(attributeMap, xmlInflaterContext);
        int idStyleCompiledOrParent = getXMLInflaterRegistry().getViewStyle(style, styleDynamicAttribs, xmlInflaterContext.getContext());

        View view = createViewObject(attributeMap, idStyleCompiledOrParent, pendingPostInsertChildrenTasks, xmlInflaterContext.getContext());

        if (idStyleCompiledOrParent != 0)
            getLayoutParamsFromStyleId(idStyleCompiledOrParent,layoutParams,styleLayoutParamsAttribs,(ContextThemeWrapper)view.getContext());
        return view;
    }

    protected View createViewObject(DOMAttributeMap attributeMap, int idStyle, PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks,Context ctx)
    {
        // Se redefine completamente en el caso de Spinner que necesita el domElemView
        return createViewObject(idStyle, pendingPostInsertChildrenTasks, ctx);
    }

    protected View createViewObject(int idStyle,PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks,Context ctx)
    {
        View view;

        Class<? extends View> clasz = initClass();

        try
        {
            if (idStyle != 0)
            {
                // http://stackoverflow.com/questions/3142067/android-set-style-in-code
                // http://stackoverflow.com/questions/11723881/android-set-view-style-programatically
                // http://stackoverflow.com/questions/8369504/why-so-complex-to-set-style-from-code-in-android
                // En teoría un parámetro es suficiente (con ContextThemeWrapper) pero curiosamente por ej en ProgressBar son necesarios los tres parámetros
                // de otra manera el idStyle es ignorado, por tanto aunque parece redundate el paso del idStyle, ambos params son necesarios en algún caso (NO ESTA CLARO)
                if (constructor3P == null) constructor3P = clasz.getConstructor(Context.class, AttributeSet.class, int.class);
                view = constructor3P.newInstance(new ContextThemeWrapper(ctx,idStyle),(AttributeSet)null, 0 /*idStyle */);

                // ALTERNATIVA QUE NO FUNCIONA (idStyle es ignorado):
                //if (constructor3P == null) constructor3P = clasz.getConstructor(Context.class, AttributeSet.class, int.class);
                //view = constructor3P.newInstance(ctxToOpenInternFiles, null, idStyle);
            }
            else
            {
                // Notas: Android suele llamar al constructor de dos params (Context,AttributeSet) supongo al menos que cuando
                // no hay atributo style.
                // En teoría da igual pues el constructor de 1 param (Context) llama al de dos con null, sin embargo
                // nos encontramos por ej con TabHost en donde no es así y el constructor de 1 param inicializa mal el componente.
                if (constructor1P == null) constructor1P = clasz.getConstructor(Context.class,AttributeSet.class);
                view = constructor1P.newInstance(ctx,(AttributeSet)null);
            }
        }
        catch (InvocationTargetException ex) { throw new ItsNatDroidException(ex); }
        catch (NoSuchMethodException ex) { throw new ItsNatDroidException(ex); }
        catch (InstantiationException ex) { throw new ItsNatDroidException(ex); }
        catch (IllegalAccessException ex) { throw new ItsNatDroidException(ex); }

        return view;
    }

    private void fillViewAttributesAndAdd(View view, ViewGroup viewParent, int index, Map<String, DOMAttr> attribMap, List<DOMAttr> styleLayoutParamsAttribs, List<DOMAttr> styleDynamicAttribs, XMLInflaterLayout xmlInflaterLayout, PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks)
    {
        PendingViewPostCreateProcess pendingViewPostCreateProcess = createPendingViewPostCreateProcess(view, viewParent);
        AttrLayoutContext attrCtx = new AttrLayoutContext(xmlInflaterLayout, pendingViewPostCreateProcess, pendingPostInsertChildrenTasks);

        fillViewAttributes(view, attribMap,styleLayoutParamsAttribs,styleDynamicAttribs, attrCtx); // Los atributos los definimos después porque el addView define el LayoutParameters adecuado según el padre (LinearLayout, RelativeLayout...)
        addViewObject(view,viewParent,index, pendingViewPostCreateProcess);

        pendingViewPostCreateProcess.destroy();
    }

    private void fillViewAttributes(View view,Map<String,DOMAttr> attribMap,List<DOMAttr> styleLayoutParamsAttribs,List<DOMAttr> styleDynamicAttribs, AttrLayoutContext attrCtx)
    {
        if (styleLayoutParamsAttribs != null) // Definimos antes los styleLayoutParamsAttribs que los definidos en el View XML porque si está alguno repetido en el XML del View gana el éste sobre el style
        {
            for(DOMAttr attr : styleLayoutParamsAttribs)
                setAttributeOrInlineEventHandler(view, attr, attrCtx);
        }

        if (styleDynamicAttribs != null)
        {
            for(DOMAttr attr : styleDynamicAttribs)
                setAttributeOrInlineEventHandler(view, attr, attrCtx);
        }

        if (attribMap != null)
        {
            for (Map.Entry<String,DOMAttr> entry : attribMap.entrySet())
            {
                DOMAttr attr = entry.getValue();
                setAttributeOrInlineEventHandler(view, attr, attrCtx);
            }
        }


        attrCtx.getPendingViewPostCreateProcess().executePendingSetAttribsTasks();
    }


    public void fillIncludeAttributesFromGetLayout(View rootViewChild,ViewGroup viewParent,XMLInflaterLayout xmlInflaterLayout,ArrayList<DOMAttr> includeAttribs)
    {
        PendingViewPostCreateProcess pendingViewPostCreateProcess = createPendingViewPostCreateProcess(rootViewChild, viewParent);
        AttrLayoutContext attrCtx = new AttrLayoutContext(xmlInflaterLayout, pendingViewPostCreateProcess, null);

        for (int i = 0; i < includeAttribs.size(); i++)
        {
            DOMAttr attr = includeAttribs.get(i);
            setAttributeOrInlineEventHandler(rootViewChild, attr, attrCtx);
        }

        pendingViewPostCreateProcess.executePendingSetAttribsTasks();
        pendingViewPostCreateProcess.executePendingLayoutParamsTasks();
    }


    public View createViewObjectAndFillAttributesAndAddFromRemote(ViewGroup viewParent, NodeToInsertImpl newChildToIn, int index,XMLInflaterLayoutPageItsNat xmlInflaterLayout,PendingPostInsertChildrenTasks pendingPostInsertChildrenTasks)
    {
        View view = createViewObjectAndFillAttributesAndAdd(viewParent,newChildToIn.getDOMAttributeMap(),index,xmlInflaterLayout,pendingPostInsertChildrenTasks);
        newChildToIn.setView(view);
        return view;
    }


    private static void getLayoutParamsFromStyleId(int styleId,ViewGroup.LayoutParams layoutParams,List<DOMAttr> styleLayoutParamsAttribs,ContextThemeWrapper ctx)
    {
        // ctxToOpenInternFiles es el ContextThemeWrapper creado para pasar el style al View en creación, él conoce los atributos del style que se meten en el theme pues se pasó en el constructor, el Context padre NO conoce los atributos asociados al style

        // Este método es debido a que cuando usamos un atributo style podemos "incluir" indirectamente atributos destinados a objetos LayoutParams tal y como layout_width y layout_height
        // el problema es que son internos y nativos, Android en compilación los "extrae" y los añade al AttributeSet que es pasado al método generateDefaultLayoutParams, eso no lo podemmos hacer de forma tan fácil
        // por lo que nosotros tenemos que extraerlos manualmente (si se usaron en el style) y convertirlos en atributos normales DOMAttr

        // http://stackoverflow.com/questions/13719103/how-to-retrieve-style-attributes-programatically-from-styles-xml

        if (styleId == 0) throw MiscUtil.internalError();


        // El orden es importante, cada clase con instanceof debe estar ANTES que el instanceof de su clase base

        if (layoutParams instanceof TableRow.LayoutParams) // Clase base: LinearLayout.LayoutParams
        {
            ClassDescView_widget_TableLayout.getTableRowLayoutParamsFromStyleId(styleId, styleLayoutParamsAttribs, ctx);
        }
        else if (layoutParams instanceof FrameLayout.LayoutParams) // Clase base: ViewGroup.MarginLayoutParams
        {
            ClassDescView_widget_FrameLayout.getFrameLayoutLayoutParamsFromStyleId(styleId, styleLayoutParamsAttribs, ctx);
        }
        else if (layoutParams instanceof GridLayout.LayoutParams) // Clase base: ViewGroup.MarginLayoutParams
        {
            ClassDescView_widget_GridLayout.getGridLayoutLayoutParamsFromStyleId(styleId,styleLayoutParamsAttribs, ctx);
        }
        else if (layoutParams instanceof LinearLayout.LayoutParams) // Clase base: ViewGroup.MarginLayoutParams
        {
            ClassDescView_widget_LinearLayout.getLinearLayoutLayoutParamsFromStyleId(styleId, styleLayoutParamsAttribs, ctx);
        }
        else if (layoutParams instanceof RelativeLayout.LayoutParams) // Clase base: ViewGroup.MarginLayoutParams
        {
            ClassDescView_widget_RelativeLayout.getRelativeLayoutLayoutParamsFromStyleId(styleId, styleLayoutParamsAttribs, ctx);
        }
        else if (layoutParams instanceof ViewGroup.MarginLayoutParams) // Clase base: ViewGroup.LayoutParams
        {
            ClassDescView_view_ViewGroup.getViewGroupMarginLayoutParamsFromStyleId(styleId,styleLayoutParamsAttribs, ctx);
        }
        else // ViewGroup.LayoutParams
        {
            ClassDescView_view_ViewGroup.getViewGroupLayoutParamsFromStyleId(styleId,styleLayoutParamsAttribs, ctx);
        }

    }

    private static ViewGroup.LayoutParams generateDefaultLayoutParams(ViewGroup viewParent)
    {
        if (viewParent == null)
        {
            // Esto ocurre con el View root del layout cuando no se ha suministrado un parent externo, hasta el final del inflado no podemos insertarlo en el ViewGroup contenedor que nos ofrece Android por ej en la actividad, no creo que sea necesario algo diferente a un ViewGroup.LayoutParams
            // aunque creo que no funciona el poner valores concretos salvo el match_parent que afortunadamente es el único que interesa para un View root que se inserta.
            return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else
        {
            return methodGenerateLP.invoke(viewParent);
        }
    }

    protected static AttributeSet readAttributeSetLayout(Context ctx,int layoutId)
    {
        // Método para crear un AttributeSet del elemento root a partir de un XML compilado

        XmlResourceParser parser = ctx.getResources().getLayout(layoutId);

        try
        {
            while (parser.next() != XmlPullParser.START_TAG) {}
        }
        catch (XmlPullParserException ex) { throw new ItsNatDroidException(ex); }
        catch (IOException ex) { throw new ItsNatDroidException(ex); }

        AttributeSet attributes = Xml.asAttributeSet(parser); // En XML compilados es un simple cast
        return attributes;
    }

    protected static AttributeSet readAttributeSet_ANTIGUO(Context ctx)
    {
        // NO SE USA, el método Resources.getLayout() ya devuelve un XmlResourceParser compilado
        // Y antes funcionaba pero ya NO (Android 4.4).


        // Este método experimental es para create un AttributeSet a partir de un XML compilado, se trataria
        // de crear un archivo XML tal y como "<tag />" ir al apk generado y copiar el archivo compilado, abrirlo
        // y copiar el contenido compilado y guardarlo finalmente como un byte[] constante
        // El problema es que no he conseguido usar AttributeSet vacío para lo que lo quería.
        // El método lo dejo inutilizado por si en el futuro se necesita un AttributeSet

        // http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/4.4.2_r1/android/content/res/XmlBlock.java?av=f

        Resources res = ctx.getResources();
        InputStream input = null; // res.openRawResource(R.raw.prueba_compilado_raw);
        byte[] content = IOUtil.read(input);

        try
        {
            Class<?> xmlBlockClass = Class.forName("android.content.res.XmlBlock");

            Constructor xmlBlockClassConstr = xmlBlockClass.getDeclaredConstructor(byte[].class);
            xmlBlockClassConstr.setAccessible(true);
            Object xmlBlock = xmlBlockClassConstr.newInstance(content);

            Method xmlBlockNewParserMethod = xmlBlockClass.getDeclaredMethod("newParser");
            xmlBlockNewParserMethod.setAccessible(true);
            XmlResourceParser parser = (XmlResourceParser)xmlBlockNewParserMethod.invoke(xmlBlock);

            while (parser.next() != XmlPullParser.START_TAG) {}

            AttributeSet attributes = Xml.asAttributeSet(parser);
            return attributes;
        }
        catch (ClassNotFoundException ex) { throw new ItsNatDroidException(ex); }
        catch (NoSuchMethodException ex) { throw new ItsNatDroidException(ex); }
        catch (InstantiationException ex) { throw new ItsNatDroidException(ex); }
        catch (IllegalAccessException ex) { throw new ItsNatDroidException(ex); }
        catch (InvocationTargetException ex) { throw new ItsNatDroidException(ex); }
        catch (XmlPullParserException ex) { throw new ItsNatDroidException(ex); }
        catch (IOException ex) { throw new ItsNatDroidException(ex); }
    }

}
