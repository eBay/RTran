/**
 * Copyright (C) 2012 Apache Software Foundation (jdcasey@commonjava.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.maven.io.util;

import org.apache.commons.io.IOUtils;
import org.apache.maven.model.PatternSet;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jdom2.CDATA;
import org.jdom2.Content;
import org.jdom2.DefaultJDOMFactory;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@SuppressWarnings( "all" )
public final class WriterUtils2
{

    private static final String defaultIndent = "    ";

    private static final String lineSeparator = "\n";

    private static final DefaultJDOMFactory factory = new DefaultJDOMFactory();

    private WriterUtils2()
    {
    }

    /**
     * Method updateElement.
     *
     * @param counter
     * @param shouldExist
     * @param name
     * @param parent
     * @return Element
     */
    public static Element updateElement( final IndentationCounter counter, final Element parent, final String name,
                                         final boolean shouldExist )
    {
        Element element = parent.getChild( name, parent.getNamespace() );
        if ( shouldExist )
        {
            if ( element == null )
            {
                element = factory.element( name, parent.getNamespace() );
                insertAtPreferredLocation( parent, element, counter );
            }
            counter.increaseCount();
        }
        else if ( element != null )
        {
            final int index = parent.indexOf( element );
            if ( index > 0 )
            {
                final Content previous = parent.getContent( index - 1 );
                if ( previous instanceof Text )
                {
                    final Text txt = (Text) previous;
                    if ( txt.getTextTrim().length() == 0 )
                    {
                        parent.removeContent( txt );
                    }
                }
            }
            parent.removeContent( element );
        }
        return element;
    } // -- Element updateElement( Counter, Element, String, boolean )

    /**
     * Method findAndReplaceXpp3DOM.
     *
     * @param counter
     * @param dom
     * @param name
     * @param parent
     * @return Element
     */
    public static Element findAndReplaceXpp3DOM( final IndentationCounter counter, final Element parent,
                                                 final String name, final Xpp3Dom dom )
    {
        final boolean shouldExist = ( dom != null ) && ( dom.getChildCount() > 0 || dom.getValue() != null );
        final Element element = updateElement( counter, parent, name, shouldExist );
        if ( shouldExist )
        {
            replaceXpp3DOM( element, dom, new IndentationCounter( counter.getDepth() + 1 ) );
        }
        return element;
    } // -- Element findAndReplaceXpp3DOM( Counter, Element, String, Xpp3Dom )

    /**
     * Method replaceXpp3DOM.
     *
     * @param parent
     * @param counter
     * @param parentDom
     */
    public static void replaceXpp3DOM( final Element parent, final Xpp3Dom parentDom, final IndentationCounter counter )
    {
        if ( parentDom.getChildCount() > 0 )
        {
            final Xpp3Dom[] childs = parentDom.getChildren();
            final Collection domChilds = new ArrayList();
            for ( final Xpp3Dom child : childs )
            {
                domChilds.add( child );
            }
            final ListIterator it = parent.getChildren().listIterator();
            while ( it.hasNext() )
            {
                final Element elem = (Element) it.next();
                final Iterator it2 = domChilds.iterator();
                Xpp3Dom corrDom = null;
                while ( it2.hasNext() )
                {
                    final Xpp3Dom dm = (Xpp3Dom) it2.next();
                    if ( dm.getName().equals( elem.getName() ) )
                    {
                        corrDom = dm;
                        break;
                    }
                }
                if ( corrDom != null )
                {
                    domChilds.remove( corrDom );
                    replaceXpp3DOM( elem, corrDom, new IndentationCounter( counter.getDepth() + 1 ) );
                    counter.increaseCount();
                }
                else
                {
                    it.remove();
                }
            }
            final Iterator it2 = domChilds.iterator();
            while ( it2.hasNext() )
            {
                final Xpp3Dom dm = (Xpp3Dom) it2.next();
                final String rawName = dm.getName();
                final String[] parts = rawName.split( ":" );

                Element elem;
                if ( parts.length > 1 )
                {
                    final String nsId = parts[0];
                    String nsUrl = dm.getAttribute( "xmlns:" + nsId );
                    final String name = parts[1];
                    if ( nsUrl == null )
                    {
                        nsUrl = parentDom.getAttribute( "xmlns:" + nsId );
                    }
                    elem = factory.element( name, Namespace.getNamespace( nsId, nsUrl ) );
                }
                else
                {
                    Namespace root = parent.getNamespace();
                    for ( Namespace n : parent.getNamespacesInherited())
                    {
                        if ( n.getPrefix() == null || n.getPrefix().length() == 0 )
                        {
                            root = n;
                            break;
                        }
                    }
                    elem = factory.element( dm.getName(), root );
                }

                final String[] attributeNames = dm.getAttributeNames();
                for ( final String attrName : attributeNames )
                {
                    if ( attrName.startsWith( "xmlns:" ) )
                    {
                        continue;
                    }
                    elem.setAttribute( attrName, dm.getAttribute( attrName ) );
                }

                insertAtPreferredLocation( parent, elem, counter );
                counter.increaseCount();
                replaceXpp3DOM( elem, dm, new IndentationCounter( counter.getDepth() + 1 ) );
            }
        }
        else if ( parentDom.getValue() != null )
        {
            List<Content> cl = parent.getContent();
            boolean foundCdata = false;
            for ( Content c : cl )
            {
                if (c instanceof CDATA)
                {
                    // If its a CDATA and, ignoring whitespace, there are differences then replace the text.
                    if ( ! ((CDATA) c).getTextTrim().equals(parentDom.getValue()) )
                    {
                        ((CDATA) c).setText(parentDom.getValue());
                    }
                    foundCdata = true;
                }
            }

            if ( ! foundCdata)
            {
                parent.setText( parentDom.getValue() );
            }
        }
    } // -- void replaceXpp3DOM( Element, Xpp3Dom, Counter )

    /**
     * Method insertAtPreferredLocation.
     *
     * @param parent
     * @param counter
     * @param child
     */
    public static void insertAtPreferredLocation( final Element parent, final Element child,
                                                  final IndentationCounter counter )
    {
        int contentIndex = 0;
        int elementCounter = 0;
        final Iterator it = parent.getContent().iterator();
        Text lastText = null;
        int offset = 0;
        while ( it.hasNext() && elementCounter <= counter.getCurrentIndex() )
        {
            final Object next = it.next();
            offset = offset + 1;
            if ( next instanceof Element )
            {
                elementCounter = elementCounter + 1;
                contentIndex = contentIndex + offset;
                offset = 0;
            }
            if ( next instanceof Text && it.hasNext() )
            {
                lastText = (Text) next;
            }
        }
        String indent = deduceIndent(parent, counter);
        if ( lastText != null && lastText.getTextTrim().length() == 0 )
        {
            lastText = lastText.clone();
        }
        else
        {
            String starter = lineSeparator;
            for ( int i = 0; i < counter.getDepth(); i++ )
            {
                starter = starter + indent; // TODO make settable?
            }
            lastText = factory.text( starter );
        }
        if ( parent.getContentSize() == 0 )
        {
            final Text finalText = lastText.clone();
            finalText.setText( finalText.getText().substring( 0, finalText.getText().length() - indent.length() ) );
            parent.addContent( contentIndex, finalText );
        }
        parent.addContent( contentIndex, child );
        parent.addContent( contentIndex, lastText );
    } // -- void insertAtPreferredLocation( Element, Element, Counter )

    public static String deduceIndent(Element parent, IndentationCounter counter) {
        String indent = defaultIndent;
        try {
            boolean isDeduced = false;
            for (int level = counter.getDepth(); level > 0; level--) {
                for (int i = parent.getContentSize() - 1; i >= 0; i--) {
                    Content content = parent.getContent().get(i);
                    if (i==parent.getContentSize()-1) {
                        continue;
                    }
                    if (content instanceof Text) {
                        String text = content.getValue();
                        String[] textArr = text.split(IOUtils.LINE_SEPARATOR_UNIX);
                        if (textArr.length > 0) {
                            String lineText = textArr[textArr.length-1];
                            lineText = lineText.replace(IOUtils.LINE_SEPARATOR_WINDOWS, "").
                                    replace(IOUtils.LINE_SEPARATOR_UNIX, "");
                            int indentLen =(int)Math.ceil((double)lineText.length()/level);
                            String lastTest = lineText.substring(0, indentLen);
                            if (lastTest.trim().length() == 0) {
                                indent = lastTest;
                                isDeduced = true;
                                break;
                            }
                        }
                    }
                }
                if (isDeduced) {
                    break;
                }
                parent = parent.getParentElement();
            }
        } catch (Exception e) {
            System.out.println("Deduce indent failed");
        }

        return indent;

    }

    /**
     * Method findAndReplaceProperties.
     *
     * @param counter
     * @param props
     * @param name
     * @param parent
     * @return Element
     */
    public static Element findAndReplaceProperties( final IndentationCounter counter, final Element parent,
                                                    final String name, final Map props )
    {
        final boolean shouldExist = ( props != null ) && !props.isEmpty();
        final Element element = updateElement( counter, parent, name, shouldExist );
        if ( shouldExist )
        {
            Iterator it = props.keySet().iterator();
            final IndentationCounter innerCounter = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final String key = (String) it.next();
                findAndReplaceSimpleElement( innerCounter, element, key, (String) props.get( key ), null );
            }
            final ArrayList lst = new ArrayList( props.keySet() );
            it = element.getChildren().iterator();
            while ( it.hasNext() )
            {
                final Element elem = (Element) it.next();
                final String key = elem.getName();
                if ( !lst.contains( key ) )
                {
                    it.remove();
                }
            }
        }
        return element;
    } // -- Element findAndReplaceProperties( Counter, Element, String, Map )

    /**
     * Method findAndReplaceSimpleElement.
     *
     * @param counter
     * @param defaultValue
     * @param text
     * @param name
     * @param parent
     * @return Element
     */
    public static Element findAndReplaceSimpleElement( final IndentationCounter counter, final Element parent,
                                                       final String name, final String text, final String defaultValue )
    {
        if ( ( defaultValue != null ) && ( text != null ) && defaultValue.equals( text ) )
        {
            final Element element = parent.getChild( name, parent.getNamespace() );
            // if exist and is default value or if doesn't exist.. just keep the way it is..
            if ( ( element != null && defaultValue.equals( element.getText() ) ) || element == null )
            {
                return element;
            }
        }
        final boolean shouldExist = ( text != null ) && ( text.trim().length() >= 0 );
        final Element element = updateElement( counter, parent, name, shouldExist );
        if ( shouldExist )
        {
            element.setText( text );
        }
        return element;
    } // -- Element findAndReplaceSimpleElement( Counter, Element, String, String, String )

    /**
     * Method findAndReplaceSimpleLists.
     *
     * @param counter
     * @param childName
     * @param parentName
     * @param list
     * @param parent
     * @return Element
     */
    public static Element findAndReplaceSimpleLists( final IndentationCounter counter, final Element parent,
                                                     final java.util.Collection list, final String parentName,
                                                     final String childName )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentName, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childName, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final String value = (String) it.next();
                Element el;
                if ( elIt != null && elIt.hasNext() )
                {
                    el = (Element) elIt.next();
                    if ( !elIt.hasNext() )
                    {
                        elIt = null;
                    }
                }
                else
                {
                    el = factory.element( childName, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                el.setText( value );
                innerCount.increaseCount();
            }
            if ( elIt != null )
            {
                while ( elIt.hasNext() )
                {
                    elIt.next();
                    elIt.remove();
                }
            }
        }
        return element;
    } // -- Element findAndReplaceSimpleLists( Counter, Element, java.util.Collection, String, String )

    /**
     * Method updatePatternSet.
     *
     * @param patternSet
     * @param element
     * @param counter
     * @param xmlTag
     */
    public static void updatePatternSet( final PatternSet patternSet, final String xmlTag,
                                         final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( patternSet != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleLists( innerCount, root, patternSet.getIncludes(), "includes", "include" );
            findAndReplaceSimpleLists( innerCount, root, patternSet.getExcludes(), "excludes", "exclude" );
        }
    } // -- void updatePatternSet( PatternSet, String, Counter, Element )

}
