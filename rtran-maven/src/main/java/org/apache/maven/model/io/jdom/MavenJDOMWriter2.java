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
package org.apache.maven.model.io.jdom;

import static org.apache.maven.io.util.WriterUtils2.findAndReplaceProperties;
import static org.apache.maven.io.util.WriterUtils2.findAndReplaceSimpleElement;
import static org.apache.maven.io.util.WriterUtils2.findAndReplaceSimpleLists;
import static org.apache.maven.io.util.WriterUtils2.findAndReplaceXpp3DOM;
import static org.apache.maven.io.util.WriterUtils2.insertAtPreferredLocation;
import static org.apache.maven.io.util.WriterUtils2.updateElement;

import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.io.util.AbstractJDOMWriter;
import org.apache.maven.io.util.IndentationCounter;
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jdom2.Element;

/**
 * Class MavenJDOMWriter.
 *
 * @version $Revision$ $Date$
 */
@SuppressWarnings( "all" )
public class MavenJDOMWriter2
        extends AbstractJDOMWriter<Model, MavenJDOMWriter2>
{
    public MavenJDOMWriter2()
    {
    }

    public MavenJDOMWriter2( final String encoding )
    {
        super( encoding );
    }

    public MavenJDOMWriter2( final Model model )
    {
        super( model.getModelEncoding() == null ? "UTF-8" : model.getModelEncoding() );
    }

    /**
     * Method iterateContributor.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateContributor( final IndentationCounter counter, final Element parent,
                                       final java.util.Collection list, final java.lang.String parentTag,
                                       final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Contributor value = (Contributor) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateContributor( value, childTag, innerCount, el );
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
    } // -- void iterateContributor( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateDependency.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateDependency( final IndentationCounter counter, final Element parent,
                                      final java.util.Collection list, final java.lang.String parentTag,
                                      final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            int elItCounter = 0;
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Dependency value = (Dependency) it.next();
                Element el;
                if ( elIt != null && elIt.hasNext() )
                {
                    el = (Element) elIt.next();
                    elItCounter++;
                    if ( !elIt.hasNext() )
                    {
                        elIt = null;
                    }
                }
                else
                {
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                populateDepIfExist(value, childTag, element, el, elItCounter);
                updateDependency( value, childTag, innerCount, el );
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
    } // -- void iterateDependency( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /***
     * If the dependency already exists in doc, will clone the content to the target element, to avoid format issue
     * @param dep the dependency in pomModel, which is writing to doc
     * @param childTag target element tag
     * @param parent dependencies element
     * @param targetEle the element which is writing to
     */
    private void populateDepIfExist(Dependency dep, String childTag,Element parent, Element targetEle, int targetCounter) {
        Iterator elIt = parent.getChildren(childTag, parent.getNamespace() ).iterator();
        int counter = 0;
        while (elIt.hasNext()) {
            Element el = (Element) elIt.next();
            counter++;
            if (counter < targetCounter) {
                continue;
            }
            if (dep.getArtifactId() != null && dep.getGroupId() != null) {
                Element artifactEl = el.getChild("artifactId", el.getNamespace());
                Element groupEl = el.getChild("groupId", el.getNamespace());
                if (artifactEl != null && groupEl != null
                        && dep.getArtifactId().equals(artifactEl.getText())
                        && dep.getGroupId().equals(groupEl.getText())) {
                    targetEle.setContent(el.cloneContent());
                    break;
                }
            }
        }
    }

    /**
     * Method iterateDeveloper.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateDeveloper( final IndentationCounter counter, final Element parent,
                                     final java.util.Collection list, final java.lang.String parentTag,
                                     final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Developer value = (Developer) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateDeveloper( value, childTag, innerCount, el );
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
    } // -- void iterateDeveloper( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateExclusion.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateExclusion( final IndentationCounter counter, final Element parent,
                                     final java.util.Collection list, final java.lang.String parentTag,
                                     final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Exclusion value = (Exclusion) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateExclusion( value, childTag, innerCount, el );
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
    } // -- void iterateExclusion( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateExtension.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateExtension( final IndentationCounter counter, final Element parent,
                                     final java.util.Collection list, final java.lang.String parentTag,
                                     final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Extension value = (Extension) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateExtension( value, childTag, innerCount, el );
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
    } // -- void iterateExtension( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateLicense.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateLicense( final IndentationCounter counter, final Element parent,
                                   final java.util.Collection list, final java.lang.String parentTag,
                                   final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final License value = (License) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateLicense( value, childTag, innerCount, el );
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
    } // -- void iterateLicense( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateMailingList.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateMailingList( final IndentationCounter counter, final Element parent,
                                       final java.util.Collection list, final java.lang.String parentTag,
                                       final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final MailingList value = (MailingList) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateMailingList( value, childTag, innerCount, el );
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
    } // -- void iterateMailingList( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateNotifier.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateNotifier( final IndentationCounter counter, final Element parent,
                                    final java.util.Collection list, final java.lang.String parentTag,
                                    final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Notifier value = (Notifier) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateNotifier( value, childTag, innerCount, el );
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
    } // -- void iterateNotifier( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iteratePlugin.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iteratePlugin( final IndentationCounter counter, final Element parent,
                                  final java.util.Collection list, final java.lang.String parentTag,
                                  final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Plugin value = (Plugin) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updatePlugin( value, childTag, innerCount, el );
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
    } // -- void iteratePlugin( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iteratePluginExecution.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iteratePluginExecution( final IndentationCounter counter, final Element parent,
                                           final java.util.Collection list, final java.lang.String parentTag,
                                           final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final PluginExecution value = (PluginExecution) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updatePluginExecution( value, childTag, innerCount, el );
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
    } // -- void iteratePluginExecution( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateProfile.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateProfile( final IndentationCounter counter, final Element parent,
                                   final java.util.Collection list, final java.lang.String parentTag,
                                   final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Profile value = (Profile) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateProfile( value, childTag, innerCount, el );
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
    } // -- void iterateProfile( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateReportPlugin.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateReportPlugin( final IndentationCounter counter, final Element parent,
                                        final java.util.Collection list, final java.lang.String parentTag,
                                        final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final ReportPlugin value = (ReportPlugin) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateReportPlugin( value, childTag, innerCount, el );
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
    } // -- void iterateReportPlugin( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateReportSet.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateReportSet( final IndentationCounter counter, final Element parent,
                                     final java.util.Collection list, final java.lang.String parentTag,
                                     final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final ReportSet value = (ReportSet) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateReportSet( value, childTag, innerCount, el );
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
    } // -- void iterateReportSet( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateRepository.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateRepository( final IndentationCounter counter, final Element parent,
                                      final java.util.Collection list, final java.lang.String parentTag,
                                      final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Repository value = (Repository) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateRepository( value, childTag, innerCount, el );
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
    } // -- void iterateRepository( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method iterateResource.
     *
     * @param counter
     * @param childTag
     * @param parentTag
     * @param list
     * @param parent
     */
    protected void iterateResource( final IndentationCounter counter, final Element parent,
                                    final java.util.Collection list, final java.lang.String parentTag,
                                    final java.lang.String childTag )
    {
        final boolean shouldExist = ( list != null ) && ( list.size() > 0 );
        final Element element = updateElement( counter, parent, parentTag, shouldExist );
        if ( shouldExist )
        {
            final Iterator it = list.iterator();
            Iterator elIt = element.getChildren( childTag, element.getNamespace() ).iterator();
            if ( !elIt.hasNext() )
            {
                elIt = null;
            }
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            while ( it.hasNext() )
            {
                final Resource value = (Resource) it.next();
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
                    el = factory.element( childTag, element.getNamespace() );
                    insertAtPreferredLocation( element, el, innerCount );
                }
                updateResource( value, childTag, innerCount, el );
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
    } // -- void iterateResource( Counter, Element, java.util.Collection, java.lang.String, java.lang.String )

    /**
     * Method updateActivation.
     *
     * @param activation
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateActivation( final Activation activation, final String xmlTag,
                                     final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( activation != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "activeByDefault",
                    activation.isActiveByDefault() == false ? null
                            : String.valueOf( activation.isActiveByDefault() ),
                    "false" );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "jdk",
                    activation.getJdk() == null ? null : activation.getJdk(),
                    null );
            updateActivationOS( activation.getOs(), "os", innerCount, root );
            updateActivationProperty( activation.getProperty(), "property", innerCount, root );
            updateActivationFile( activation.getFile(), "file", innerCount, root );
        }
    } // -- void updateActivation( Activation, String, Counter, Element )

    /**
     * Method updateActivationFile.
     *
     * @param activationFile
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateActivationFile( final ActivationFile activationFile, final String xmlTag,
                                         final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( activationFile != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "missing", activationFile.getMissing() == null ? null
                    : activationFile.getMissing(), null );
            findAndReplaceSimpleElement( innerCount, root, "exists", activationFile.getExists() == null ? null
                    : activationFile.getExists(), null );
        }
    } // -- void updateActivationFile( ActivationFile, String, Counter, Element )

    /**
     * Method updateActivationOS.
     *
     * @param activationOS
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateActivationOS( final ActivationOS activationOS, final String xmlTag,
                                       final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( activationOS != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "name",
                    activationOS.getName() == null ? null : activationOS.getName(),
                    null );
            findAndReplaceSimpleElement( innerCount, root, "family", activationOS.getFamily() == null ? null
                    : activationOS.getFamily(), null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "arch",
                    activationOS.getArch() == null ? null : activationOS.getArch(),
                    null );
            findAndReplaceSimpleElement( innerCount, root, "version", activationOS.getVersion() == null ? null
                    : activationOS.getVersion(), null );
        }
    } // -- void updateActivationOS( ActivationOS, String, Counter, Element )

    /**
     * Method updateActivationProperty.
     *
     * @param activationProperty
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateActivationProperty( final ActivationProperty activationProperty, final String xmlTag,
                                             final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( activationProperty != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "name", activationProperty.getName() == null ? null
                    : activationProperty.getName(), null );
            findAndReplaceSimpleElement( innerCount, root, "value", activationProperty.getValue() == null ? null
                    : activationProperty.getValue(), null );
        }
    } // -- void updateActivationProperty( ActivationProperty, String, Counter, Element )

    /**
     * Method updateBuild.
     *
     * @param build
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateBuild( final Build build, final String xmlTag, final IndentationCounter counter,
                                final Element element )
    {
        final boolean shouldExist = ( build != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "sourceDirectory", build.getSourceDirectory() == null ? null
                    : build.getSourceDirectory(), null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "scriptSourceDirectory",
                    build.getScriptSourceDirectory() == null ? null
                            : build.getScriptSourceDirectory(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "testSourceDirectory",
                    build.getTestSourceDirectory() == null ? null : build.getTestSourceDirectory(),
                    null );
            findAndReplaceSimpleElement( innerCount, root, "outputDirectory", build.getOutputDirectory() == null ? null
                    : build.getOutputDirectory(), null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "testOutputDirectory",
                    build.getTestOutputDirectory() == null ? null : build.getTestOutputDirectory(),
                    null );
            iterateExtension( innerCount, root, build.getExtensions(), "extensions", "extension" );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "defaultGoal",
                    build.getDefaultGoal() == null ? null : build.getDefaultGoal(),
                    null );
            iterateResource( innerCount, root, build.getResources(), "resources", "resource" );
            iterateResource( innerCount, root, build.getTestResources(), "testResources", "testResource" );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "directory",
                    build.getDirectory() == null ? null : build.getDirectory(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "finalName",
                    build.getFinalName() == null ? null : build.getFinalName(),
                    null );
            findAndReplaceSimpleLists( innerCount, root, build.getFilters(), "filters", "filter" );
            updatePluginManagement( build.getPluginManagement(), "pluginManagement", innerCount, root );
            iteratePlugin( innerCount, root, build.getPlugins(), "plugins", "plugin" );
        }
    } // -- void updateBuild( Build, String, Counter, Element )

    /**
     * Method updateBuildBase.
     *
     * @param buildBase
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateBuildBase( final BuildBase buildBase, final String xmlTag, final IndentationCounter counter,
                                    final Element element )
    {
        final boolean shouldExist = ( buildBase != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "defaultGoal", buildBase.getDefaultGoal() == null ? null
                    : buildBase.getDefaultGoal(), null );
            iterateResource( innerCount, root, buildBase.getResources(), "resources", "resource" );
            iterateResource( innerCount, root, buildBase.getTestResources(), "testResources", "testResource" );
            findAndReplaceSimpleElement( innerCount, root, "directory", buildBase.getDirectory() == null ? null
                    : buildBase.getDirectory(), null );
            findAndReplaceSimpleElement( innerCount, root, "finalName", buildBase.getFinalName() == null ? null
                    : buildBase.getFinalName(), null );
            findAndReplaceSimpleLists( innerCount, root, buildBase.getFilters(), "filters", "filter" );
            updatePluginManagement( buildBase.getPluginManagement(), "pluginManagement", innerCount, root );
            iteratePlugin( innerCount, root, buildBase.getPlugins(), "plugins", "plugin" );
        }
    } // -- void updateBuildBase( BuildBase, String, Counter, Element )

    /**
     * Method updateCiManagement.
     *
     * @param ciManagement
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateCiManagement( final CiManagement ciManagement, final String xmlTag,
                                       final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( ciManagement != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "system", ciManagement.getSystem() == null ? null
                    : ciManagement.getSystem(), null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "url",
                    ciManagement.getUrl() == null ? null : ciManagement.getUrl(),
                    null );
            iterateNotifier( innerCount, root, ciManagement.getNotifiers(), "notifiers", "notifier" );
        }
    } // -- void updateCiManagement( CiManagement, String, Counter, Element )

    /**
     * Method updateConfigurationContainer.
     *
     * @param configurationContainer
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateConfigurationContainer( final ConfigurationContainer configurationContainer,
                                                 final String xmlTag, final IndentationCounter counter,
                                                 final Element element )
    {
        final boolean shouldExist = ( configurationContainer != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "inherited",
                    configurationContainer.getInherited() == null ? null
                            : configurationContainer.getInherited(),
                    null );
            findAndReplaceXpp3DOM( innerCount,
                    root,
                    "configuration",
                    (Xpp3Dom) configurationContainer.getConfiguration() );
        }
    } // -- void updateConfigurationContainer( ConfigurationContainer, String, Counter, Element )

    /**
     * Method updateContributor.
     *
     * @param contributor
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateContributor( final Contributor contributor, final String xmlTag,
                                      final IndentationCounter counter, final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "name",
                contributor.getName() == null ? null : contributor.getName(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "email",
                contributor.getEmail() == null ? null : contributor.getEmail(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "url",
                contributor.getUrl() == null ? null : contributor.getUrl(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "organization", contributor.getOrganization() == null ? null
                : contributor.getOrganization(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "organizationUrl",
                contributor.getOrganizationUrl() == null ? null : contributor.getOrganizationUrl(),
                null );
        findAndReplaceSimpleLists( innerCount, root, contributor.getRoles(), "roles", "role" );
        findAndReplaceSimpleElement( innerCount, root, "timezone", contributor.getTimezone() == null ? null
                : contributor.getTimezone(), null );
        findAndReplaceProperties( innerCount, root, "properties", contributor.getProperties() );
    } // -- void updateContributor( Contributor, String, Counter, Element )

    /**
     * Method updateDependency.
     *
     * @param dependency
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateDependency( final Dependency dependency, final String xmlTag,
                                     final IndentationCounter counter, final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "groupId",
                dependency.getGroupId() == null ? null : dependency.getGroupId(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "artifactId", dependency.getArtifactId() == null ? null
                : dependency.getArtifactId(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "version",
                dependency.getVersion() == null ? null : dependency.getVersion(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "type",
                dependency.getType() == null ? null : dependency.getType(),
                "jar" );
        findAndReplaceSimpleElement( innerCount, root, "classifier", dependency.getClassifier() == null ? null
                : dependency.getClassifier(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "scope",
                dependency.getScope() == null ? null : dependency.getScope(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "systemPath", dependency.getSystemPath() == null ? null
                : dependency.getSystemPath(), null );
        iterateExclusion( innerCount, root, dependency.getExclusions(), "exclusions", "exclusion" );
        findAndReplaceSimpleElement( innerCount,
                root,
                "optional",
                dependency.getOptional() == null ? null : dependency.getOptional(),
                null );
    } // -- void updateDependency( Dependency, String, Counter, Element )

    /**
     * Method updateDependencyManagement.
     *
     * @param dependencyManagement
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateDependencyManagement( final DependencyManagement dependencyManagement, final String xmlTag,
                                               final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( dependencyManagement != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            iterateDependency( innerCount, root, dependencyManagement.getDependencies(), "dependencies", "dependency" );
        }
    } // -- void updateDependencyManagement( DependencyManagement, String, Counter, Element )

    /**
     * Method updateDeploymentRepository.
     *
     * @param deploymentRepository
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateDeploymentRepository( final DeploymentRepository deploymentRepository, final String xmlTag,
                                               final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( deploymentRepository != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "uniqueVersion",
                    deploymentRepository.isUniqueVersion() == true ? null
                            : String.valueOf( deploymentRepository.isUniqueVersion() ),
                    "true" );
            updateRepositoryPolicy( deploymentRepository.getReleases(), "releases", innerCount, root );
            updateRepositoryPolicy( deploymentRepository.getSnapshots(), "snapshots", innerCount, root );
            findAndReplaceSimpleElement( innerCount, root, "id", deploymentRepository.getId() == null ? null
                    : deploymentRepository.getId(), null );
            findAndReplaceSimpleElement( innerCount, root, "name", deploymentRepository.getName() == null ? null
                    : deploymentRepository.getName(), null );
            findAndReplaceSimpleElement( innerCount, root, "url", deploymentRepository.getUrl() == null ? null
                    : deploymentRepository.getUrl(), null );
            findAndReplaceSimpleElement( innerCount, root, "layout", deploymentRepository.getLayout() == null ? null
                    : deploymentRepository.getLayout(), "default" );
        }
    } // -- void updateDeploymentRepository( DeploymentRepository, String, Counter, Element )

    /**
     * Method updateDeveloper.
     *
     * @param developer
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateDeveloper( final Developer developer, final String xmlTag, final IndentationCounter counter,
                                    final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount, root, "id", developer.getId() == null ? null : developer.getId(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "name",
                developer.getName() == null ? null : developer.getName(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "email",
                developer.getEmail() == null ? null : developer.getEmail(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "url",
                developer.getUrl() == null ? null : developer.getUrl(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "organization", developer.getOrganization() == null ? null
                : developer.getOrganization(), null );
        findAndReplaceSimpleElement( innerCount, root, "organizationUrl", developer.getOrganizationUrl() == null ? null
                : developer.getOrganizationUrl(), null );
        findAndReplaceSimpleLists( innerCount, root, developer.getRoles(), "roles", "role" );
        findAndReplaceSimpleElement( innerCount,
                root,
                "timezone",
                developer.getTimezone() == null ? null : developer.getTimezone(),
                null );
        findAndReplaceProperties( innerCount, root, "properties", developer.getProperties() );
    } // -- void updateDeveloper( Developer, String, Counter, Element )

    /**
     * Method updateDistributionManagement.
     *
     * @param distributionManagement
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateDistributionManagement( final DistributionManagement distributionManagement,
                                                 final String xmlTag, final IndentationCounter counter,
                                                 final Element element )
    {
        final boolean shouldExist = ( distributionManagement != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            updateDeploymentRepository( distributionManagement.getRepository(), "repository", innerCount, root );
            updateDeploymentRepository( distributionManagement.getSnapshotRepository(),
                    "snapshotRepository",
                    innerCount,
                    root );
            updateSite( distributionManagement.getSite(), "site", innerCount, root );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "downloadUrl",
                    distributionManagement.getDownloadUrl() == null ? null
                            : distributionManagement.getDownloadUrl(),
                    null );
            updateRelocation( distributionManagement.getRelocation(), "relocation", innerCount, root );
            findAndReplaceSimpleElement( innerCount, root, "status", distributionManagement.getStatus() == null ? null
                    : distributionManagement.getStatus(), null );
        }
    } // -- void updateDistributionManagement( DistributionManagement, String, Counter, Element )

    /**
     * Method updateExclusion.
     *
     * @param exclusion
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateExclusion( final Exclusion exclusion, final String xmlTag, final IndentationCounter counter,
                                    final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount, root, "artifactId", exclusion.getArtifactId() == null ? null
                : exclusion.getArtifactId(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "groupId",
                exclusion.getGroupId() == null ? null : exclusion.getGroupId(),
                null );
    } // -- void updateExclusion( Exclusion, String, Counter, Element )

    /**
     * Method updateExtension.
     *
     * @param extension
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateExtension( final Extension extension, final String xmlTag, final IndentationCounter counter,
                                    final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "groupId",
                extension.getGroupId() == null ? null : extension.getGroupId(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "artifactId", extension.getArtifactId() == null ? null
                : extension.getArtifactId(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "version",
                extension.getVersion() == null ? null : extension.getVersion(),
                null );
    } // -- void updateExtension( Extension, String, Counter, Element )

    /**
     * Method updateFileSet.
     *
     * @param fileSet
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateFileSet( final FileSet fileSet, final String xmlTag, final IndentationCounter counter,
                                  final Element element )
    {
        final boolean shouldExist = ( fileSet != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "directory",
                    fileSet.getDirectory() == null ? null : fileSet.getDirectory(),
                    null );
            findAndReplaceSimpleLists( innerCount, root, fileSet.getIncludes(), "includes", "include" );
            findAndReplaceSimpleLists( innerCount, root, fileSet.getExcludes(), "excludes", "exclude" );
        }
    } // -- void updateFileSet( FileSet, String, Counter, Element )

    /**
     * Method updateIssueManagement.
     *
     * @param issueManagement
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateIssueManagement( final IssueManagement issueManagement, final String xmlTag,
                                          final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( issueManagement != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "system", issueManagement.getSystem() == null ? null
                    : issueManagement.getSystem(), null );
            findAndReplaceSimpleElement( innerCount, root, "url", issueManagement.getUrl() == null ? null
                    : issueManagement.getUrl(), null );
        }
    } // -- void updateIssueManagement( IssueManagement, String, Counter, Element )

    /**
     * Method updateLicense.
     *
     * @param license
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateLicense( final License license, final String xmlTag, final IndentationCounter counter,
                                  final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "name",
                license.getName() == null ? null : license.getName(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "url", license.getUrl() == null ? null : license.getUrl(), null );
        findAndReplaceSimpleElement( innerCount, root, "distribution", license.getDistribution() == null ? null
                : license.getDistribution(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "comments",
                license.getComments() == null ? null : license.getComments(),
                null );
    } // -- void updateLicense( License, String, Counter, Element )

    /**
     * Method updateMailingList.
     *
     * @param mailingList
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateMailingList( final MailingList mailingList, final String xmlTag,
                                      final IndentationCounter counter, final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "name",
                mailingList.getName() == null ? null : mailingList.getName(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "subscribe", mailingList.getSubscribe() == null ? null
                : mailingList.getSubscribe(), null );
        findAndReplaceSimpleElement( innerCount, root, "unsubscribe", mailingList.getUnsubscribe() == null ? null
                : mailingList.getUnsubscribe(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "post",
                mailingList.getPost() == null ? null : mailingList.getPost(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "archive",
                mailingList.getArchive() == null ? null : mailingList.getArchive(),
                null );
        findAndReplaceSimpleLists( innerCount, root, mailingList.getOtherArchives(), "otherArchives", "otherArchive" );
    } // -- void updateMailingList( MailingList, String, Counter, Element )

    /**
     * Method updateModel.
     *
     * @param model
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateModel( final Model model, final String xmlTag, final IndentationCounter counter,
                                final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "modelVersion",
                model.getModelVersion() == null ? null : model.getModelVersion(),
                null );
        updateParent( model.getParent(), "parent", innerCount, root );
        findAndReplaceSimpleElement( innerCount,
                root,
                "groupId",
                model.getGroupId() == null ? null : model.getGroupId(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "artifactId",
                model.getArtifactId() == null ? null : model.getArtifactId(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "version",
                model.getVersion() == null ? null : model.getVersion(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "packaging",
                model.getPackaging() == null ? null : model.getPackaging(),
                "jar" );
        findAndReplaceSimpleElement( innerCount, root, "name", model.getName() == null ? null : model.getName(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "description",
                model.getDescription() == null ? null : model.getDescription(),
                null );
        findAndReplaceSimpleElement( innerCount, root, "url", model.getUrl() == null ? null : model.getUrl(), null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "inceptionYear",
                model.getInceptionYear() == null ? null : model.getInceptionYear(),
                null );
        updateOrganization( model.getOrganization(), "organization", innerCount, root );
        iterateLicense( innerCount, root, model.getLicenses(), "licenses", "license" );
        iterateDeveloper( innerCount, root, model.getDevelopers(), "developers", "developer" );
        iterateContributor( innerCount, root, model.getContributors(), "contributors", "contributor" );
        iterateMailingList( innerCount, root, model.getMailingLists(), "mailingLists", "mailingList" );
        updatePrerequisites( model.getPrerequisites(), "prerequisites", innerCount, root );
        findAndReplaceSimpleLists( innerCount, root, model.getModules(), "modules", "module" );
        updateScm( model.getScm(), "scm", innerCount, root );
        updateIssueManagement( model.getIssueManagement(), "issueManagement", innerCount, root );
        updateCiManagement( model.getCiManagement(), "ciManagement", innerCount, root );
        updateDistributionManagement( model.getDistributionManagement(), "distributionManagement", innerCount, root );
        findAndReplaceProperties( innerCount, root, "properties", model.getProperties() );
        updateDependencyManagement( model.getDependencyManagement(), "dependencyManagement", innerCount, root );
        iterateDependency( innerCount, root, model.getDependencies(), "dependencies", "dependency" );
        iterateRepository( innerCount, root, model.getRepositories(), "repositories", "repository" );
        iterateRepository( innerCount, root, model.getPluginRepositories(), "pluginRepositories", "pluginRepository" );
        updateBuild( model.getBuild(), "build", innerCount, root );
        findAndReplaceXpp3DOM( innerCount, root, "reports", (Xpp3Dom) model.getReports() );
        updateReporting( model.getReporting(), "reporting", innerCount, root );
        iterateProfile( innerCount, root, model.getProfiles(), "profiles", "profile" );
    } // -- void updateModel( Model, String, Counter, Element )

    /**
     * Method updateModelBase.
     *
     * @param modelBase
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateModelBase( final ModelBase modelBase, final String xmlTag, final IndentationCounter counter,
                                    final Element element )
    {
        final boolean shouldExist = ( modelBase != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleLists( innerCount, root, modelBase.getModules(), "modules", "module" );
            updateDistributionManagement( modelBase.getDistributionManagement(),
                    "distributionManagement",
                    innerCount,
                    root );
            findAndReplaceProperties( innerCount, root, "properties", modelBase.getProperties() );
            updateDependencyManagement( modelBase.getDependencyManagement(), "dependencyManagement", innerCount, root );
            iterateDependency( innerCount, root, modelBase.getDependencies(), "dependencies", "dependency" );
            iterateRepository( innerCount, root, modelBase.getRepositories(), "repositories", "repository" );
            iterateRepository( innerCount,
                    root,
                    modelBase.getPluginRepositories(),
                    "pluginRepositories",
                    "pluginRepository" );
            findAndReplaceXpp3DOM( innerCount, root, "reports", (Xpp3Dom) modelBase.getReports() );
            updateReporting( modelBase.getReporting(), "reporting", innerCount, root );
        }
    } // -- void updateModelBase( ModelBase, String, Counter, Element )

    /**
     * Method updateNotifier.
     *
     * @param notifier
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateNotifier( final Notifier notifier, final String xmlTag, final IndentationCounter counter,
                                   final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "type",
                notifier.getType() == null ? null : notifier.getType(),
                "mail" );
        findAndReplaceSimpleElement( innerCount,
                root,
                "sendOnError",
                notifier.isSendOnError() == true ? null
                        : String.valueOf( notifier.isSendOnError() ),
                "true" );
        findAndReplaceSimpleElement( innerCount, root, "sendOnFailure", notifier.isSendOnFailure() == true ? null
                : String.valueOf( notifier.isSendOnFailure() ), "true" );
        findAndReplaceSimpleElement( innerCount, root, "sendOnSuccess", notifier.isSendOnSuccess() == true ? null
                : String.valueOf( notifier.isSendOnSuccess() ), "true" );
        findAndReplaceSimpleElement( innerCount, root, "sendOnWarning", notifier.isSendOnWarning() == true ? null
                : String.valueOf( notifier.isSendOnWarning() ), "true" );
        findAndReplaceSimpleElement( innerCount,
                root,
                "address",
                notifier.getAddress() == null ? null : notifier.getAddress(),
                null );
        findAndReplaceProperties( innerCount, root, "configuration", notifier.getConfiguration() );
    } // -- void updateNotifier( Notifier, String, Counter, Element )

    /**
     * Method updateOrganization.
     *
     * @param organization
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateOrganization( final Organization organization, final String xmlTag,
                                       final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( organization != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "name",
                    organization.getName() == null ? null : organization.getName(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "url",
                    organization.getUrl() == null ? null : organization.getUrl(),
                    null );
        }
    } // -- void updateOrganization( Organization, String, Counter, Element )

    /**
     * Method updateParent.
     *
     * @param parent
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateParent( final Parent parent, final String xmlTag, final IndentationCounter counter,
                                 final Element element )
    {
        final boolean shouldExist = ( parent != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "artifactId",
                    parent.getArtifactId() == null ? null : parent.getArtifactId(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "groupId",
                    parent.getGroupId() == null ? null : parent.getGroupId(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "version",
                    parent.getVersion() == null ? null : parent.getVersion(),
                    null );
            findAndReplaceSimpleElement( innerCount, root, "relativePath", parent.getRelativePath() == null ? null
                    : parent.getRelativePath(), "../pom.xml" );
        }
    } // -- void updateParent( Parent, String, Counter, Element )

    /**
     * Method updatePlugin.
     *
     * @param plugin
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updatePlugin( final Plugin plugin, final String xmlTag, final IndentationCounter counter,
                                 final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "groupId",
                plugin.getGroupId() == null ? null : plugin.getGroupId(),
                "org.apache.maven.plugins" );
        findAndReplaceSimpleElement( innerCount,
                root,
                "artifactId",
                plugin.getArtifactId() == null ? null : plugin.getArtifactId(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "version",
                plugin.getVersion() == null ? null : plugin.getVersion(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "extensions",
                plugin.getExtensions() == null ? null : plugin.getExtensions(),
                null );
        iteratePluginExecution( innerCount, root, plugin.getExecutions(), "executions", "execution" );
        iterateDependency( innerCount, root, plugin.getDependencies(), "dependencies", "dependency" );
        findAndReplaceXpp3DOM( innerCount, root, "goals", (Xpp3Dom) plugin.getGoals() );
        findAndReplaceSimpleElement( innerCount,
                root,
                "inherited",
                plugin.getInherited() == null ? null : plugin.getInherited(),
                null );
        findAndReplaceXpp3DOM( innerCount, root, "configuration", (Xpp3Dom) plugin.getConfiguration() );
    } // -- void updatePlugin( Plugin, String, Counter, Element )

    /**
     * Method updatePluginConfiguration.
     *
     * @param pluginConfiguration
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updatePluginConfiguration( final PluginConfiguration pluginConfiguration, final String xmlTag,
                                              final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( pluginConfiguration != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            updatePluginManagement( pluginConfiguration.getPluginManagement(), "pluginManagement", innerCount, root );
            iteratePlugin( innerCount, root, pluginConfiguration.getPlugins(), "plugins", "plugin" );
        }
    } // -- void updatePluginConfiguration( PluginConfiguration, String, Counter, Element )

    /**
     * Method updatePluginContainer.
     *
     * @param pluginContainer
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updatePluginContainer( final PluginContainer pluginContainer, final String xmlTag,
                                          final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( pluginContainer != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            iteratePlugin( innerCount, root, pluginContainer.getPlugins(), "plugins", "plugin" );
        }
    } // -- void updatePluginContainer( PluginContainer, String, Counter, Element )

    /**
     * Method updatePluginExecution.
     *
     * @param pluginExecution
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updatePluginExecution( final PluginExecution pluginExecution, final String xmlTag,
                                          final IndentationCounter counter, final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "id",
                pluginExecution.getId() == null ? null : pluginExecution.getId(),
                "default" );
        findAndReplaceSimpleElement( innerCount, root, "phase", pluginExecution.getPhase() == null ? null
                : pluginExecution.getPhase(), null );
        findAndReplaceSimpleLists( innerCount, root, pluginExecution.getGoals(), "goals", "goal" );
        findAndReplaceSimpleElement( innerCount, root, "inherited", pluginExecution.getInherited() == null ? null
                : pluginExecution.getInherited(), null );
        findAndReplaceXpp3DOM( innerCount, root, "configuration", (Xpp3Dom) pluginExecution.getConfiguration() );
    } // -- void updatePluginExecution( PluginExecution, String, Counter, Element )

    /**
     * Method updatePluginManagement.
     *
     * @param pluginManagement
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updatePluginManagement( final PluginManagement pluginManagement, final String xmlTag,
                                           final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( pluginManagement != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            iteratePlugin( innerCount, root, pluginManagement.getPlugins(), "plugins", "plugin" );
        }
    } // -- void updatePluginManagement( PluginManagement, String, Counter, Element )

    /**
     * Method updatePrerequisites.
     *
     * @param prerequisites
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updatePrerequisites( final Prerequisites prerequisites, final String xmlTag,
                                        final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( prerequisites != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "maven", prerequisites.getMaven() == null ? null
                    : prerequisites.getMaven(), "2.0" );
        }
    } // -- void updatePrerequisites( Prerequisites, String, Counter, Element )

    /**
     * Method updateProfile.
     *
     * @param profile
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateProfile( final Profile profile, final String xmlTag, final IndentationCounter counter,
                                  final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "id",
                profile.getId() == null ? null : profile.getId(),
                "default" );
        updateActivation( profile.getActivation(), "activation", innerCount, root );
        updateBuildBase( profile.getBuild(), "build", innerCount, root );
        findAndReplaceSimpleLists( innerCount, root, profile.getModules(), "modules", "module" );
        updateDistributionManagement( profile.getDistributionManagement(), "distributionManagement", innerCount, root );
        findAndReplaceProperties( innerCount, root, "properties", profile.getProperties() );
        updateDependencyManagement( profile.getDependencyManagement(), "dependencyManagement", innerCount, root );
        iterateDependency( innerCount, root, profile.getDependencies(), "dependencies", "dependency" );
        iterateRepository( innerCount, root, profile.getRepositories(), "repositories", "repository" );
        iterateRepository( innerCount, root, profile.getPluginRepositories(), "pluginRepositories", "pluginRepository" );
        findAndReplaceXpp3DOM( innerCount, root, "reports", (Xpp3Dom) profile.getReports() );
        updateReporting( profile.getReporting(), "reporting", innerCount, root );
    } // -- void updateProfile( Profile, String, Counter, Element )

    /**
     * Method updateRelocation.
     *
     * @param relocation
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateRelocation( final Relocation relocation, final String xmlTag,
                                     final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( relocation != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "groupId", relocation.getGroupId() == null ? null
                    : relocation.getGroupId(), null );
            findAndReplaceSimpleElement( innerCount, root, "artifactId", relocation.getArtifactId() == null ? null
                    : relocation.getArtifactId(), null );
            findAndReplaceSimpleElement( innerCount, root, "version", relocation.getVersion() == null ? null
                    : relocation.getVersion(), null );
            findAndReplaceSimpleElement( innerCount, root, "message", relocation.getMessage() == null ? null
                    : relocation.getMessage(), null );
        }
    } // -- void updateRelocation( Relocation, String, Counter, Element )

    /**
     * Method updateReportPlugin.
     *
     * @param reportPlugin
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateReportPlugin( final ReportPlugin reportPlugin, final String xmlTag,
                                       final IndentationCounter counter, final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount, root, "groupId", reportPlugin.getGroupId() == null ? null
                : reportPlugin.getGroupId(), "org.apache.maven.plugins" );
        findAndReplaceSimpleElement( innerCount, root, "artifactId", reportPlugin.getArtifactId() == null ? null
                : reportPlugin.getArtifactId(), null );
        findAndReplaceSimpleElement( innerCount, root, "version", reportPlugin.getVersion() == null ? null
                : reportPlugin.getVersion(), null );
        iterateReportSet( innerCount, root, reportPlugin.getReportSets(), "reportSets", "reportSet" );
        findAndReplaceSimpleElement( innerCount, root, "inherited", reportPlugin.getInherited() == null ? null
                : reportPlugin.getInherited(), null );
        findAndReplaceXpp3DOM( innerCount, root, "configuration", (Xpp3Dom) reportPlugin.getConfiguration() );
    } // -- void updateReportPlugin( ReportPlugin, String, Counter, Element )

    /**
     * Method updateReportSet.
     *
     * @param reportSet
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateReportSet( final ReportSet reportSet, final String xmlTag, final IndentationCounter counter,
                                    final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "id",
                reportSet.getId() == null ? null : reportSet.getId(),
                "default" );
        findAndReplaceSimpleLists( innerCount, root, reportSet.getReports(), "reports", "report" );
        findAndReplaceSimpleElement( innerCount,
                root,
                "inherited",
                reportSet.getInherited() == null ? null : reportSet.getInherited(),
                null );
        findAndReplaceXpp3DOM( innerCount, root, "configuration", (Xpp3Dom) reportSet.getConfiguration() );
    } // -- void updateReportSet( ReportSet, String, Counter, Element )

    /**
     * Method updateReporting.
     *
     * @param reporting
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateReporting( final Reporting reporting, final String xmlTag, final IndentationCounter counter,
                                    final Element element )
    {
        final boolean shouldExist = ( reporting != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "excludeDefaults",
                    reporting.getExcludeDefaults() == null ? null : reporting.getExcludeDefaults(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "outputDirectory",
                    reporting.getOutputDirectory() == null ? null : reporting.getOutputDirectory(),
                    null );
            iterateReportPlugin( innerCount, root, reporting.getPlugins(), "plugins", "plugin" );
        }
    } // -- void updateReporting( Reporting, String, Counter, Element )

    /**
     * Method updateRepository.
     *
     * @param repository
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateRepository( final Repository repository, final String xmlTag,
                                     final IndentationCounter counter, final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        updateRepositoryPolicy( repository.getReleases(), "releases", innerCount, root );
        updateRepositoryPolicy( repository.getSnapshots(), "snapshots", innerCount, root );
        findAndReplaceSimpleElement( innerCount,
                root,
                "id",
                repository.getId() == null ? null : repository.getId(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "name",
                repository.getName() == null ? null : repository.getName(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "url",
                repository.getUrl() == null ? null : repository.getUrl(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "layout",
                repository.getLayout() == null ? null : repository.getLayout(),
                "default" );
    } // -- void updateRepository( Repository, String, Counter, Element )

    /**
     * Method updateRepositoryBase.
     *
     * @param repositoryBase
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateRepositoryBase( final RepositoryBase repositoryBase, final String xmlTag,
                                         final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( repositoryBase != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "id",
                    repositoryBase.getId() == null ? null : repositoryBase.getId(),
                    null );
            findAndReplaceSimpleElement( innerCount, root, "name", repositoryBase.getName() == null ? null
                    : repositoryBase.getName(), null );
            findAndReplaceSimpleElement( innerCount, root, "url", repositoryBase.getUrl() == null ? null
                    : repositoryBase.getUrl(), null );
            findAndReplaceSimpleElement( innerCount, root, "layout", repositoryBase.getLayout() == null ? null
                    : repositoryBase.getLayout(), "default" );
        }
    } // -- void updateRepositoryBase( RepositoryBase, String, Counter, Element )

    /**
     * Method updateRepositoryPolicy.
     *
     * @param repositoryPolicy
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateRepositoryPolicy( final RepositoryPolicy repositoryPolicy, final String xmlTag,
                                           final IndentationCounter counter, final Element element )
    {
        final boolean shouldExist = ( repositoryPolicy != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "enabled", repositoryPolicy.getEnabled() == null ? null
                    : repositoryPolicy.getEnabled(), null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "updatePolicy",
                    repositoryPolicy.getUpdatePolicy() == null ? null
                            : repositoryPolicy.getUpdatePolicy(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "checksumPolicy",
                    repositoryPolicy.getChecksumPolicy() == null ? null
                            : repositoryPolicy.getChecksumPolicy(),
                    null );
        }
    } // -- void updateRepositoryPolicy( RepositoryPolicy, String, Counter, Element )

    /**
     * Method updateResource.
     *
     * @param resource
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateResource( final Resource resource, final String xmlTag, final IndentationCounter counter,
                                   final Element element )
    {
        final Element root = element;
        final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
        findAndReplaceSimpleElement( innerCount,
                root,
                "targetPath",
                resource.getTargetPath() == null ? null : resource.getTargetPath(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "filtering",
                resource.getFiltering() == null ? null : resource.getFiltering(),
                null );
        findAndReplaceSimpleElement( innerCount,
                root,
                "directory",
                resource.getDirectory() == null ? null : resource.getDirectory(),
                null );
        findAndReplaceSimpleLists( innerCount, root, resource.getIncludes(), "includes", "include" );
        findAndReplaceSimpleLists( innerCount, root, resource.getExcludes(), "excludes", "exclude" );
    } // -- void updateResource( Resource, String, Counter, Element )

    /**
     * Method updateScm.
     *
     * @param scm
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateScm( final Scm scm, final String xmlTag, final IndentationCounter counter,
                              final Element element )
    {
        final boolean shouldExist = ( scm != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "connection",
                    scm.getConnection() == null ? null : scm.getConnection(),
                    null );
            findAndReplaceSimpleElement( innerCount,
                    root,
                    "developerConnection",
                    scm.getDeveloperConnection() == null ? null : scm.getDeveloperConnection(),
                    null );
            findAndReplaceSimpleElement( innerCount, root, "tag", scm.getTag() == null ? null : scm.getTag(), "HEAD" );
            findAndReplaceSimpleElement( innerCount, root, "url", scm.getUrl() == null ? null : scm.getUrl(), null );
        }
    } // -- void updateScm( Scm, String, Counter, Element )

    /**
     * Method updateSite.
     *
     * @param site
     * @param element
     * @param counter
     * @param xmlTag
     */
    protected void updateSite( final Site site, final String xmlTag, final IndentationCounter counter,
                               final Element element )
    {
        final boolean shouldExist = ( site != null );
        final Element root = updateElement( counter, element, xmlTag, shouldExist );
        if ( shouldExist )
        {
            final IndentationCounter innerCount = new IndentationCounter( counter.getDepth() + 1 );
            findAndReplaceSimpleElement( innerCount, root, "id", site.getId() == null ? null : site.getId(), null );
            findAndReplaceSimpleElement( innerCount, root, "name", site.getName() == null ? null : site.getName(), null );
            findAndReplaceSimpleElement( innerCount, root, "url", site.getUrl() == null ? null : site.getUrl(), null );
        }
    } // -- void updateSite( Site, String, Counter, Element )

    @Override
    protected void update( final Model source, final IndentationCounter indentationCounter, final Element rootElement )
            throws IOException
    {
        updateModel( source, "project", indentationCounter, rootElement );
    }

}
