package org.codehaus.mojo.appfuse.mojo;

/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Properties;

import org.codehaus.mojo.appfuse.utility.AppfuseProperties;

/**
 * This mojo class will create model or value objects from a set of hbm.xml files.
 * 
 * @author <a href="mailto:scott@theryansplace.com">Scott Ryan</a>
 * @version $Id$
 * @description Generate one or more model/value objects from the input hbm.xml files.
 * @goal genmodel
 */
public class GenerateModelMojo extends PojoMojoBase
{

    /**
     * Creates a new GenerateModelMojo object.
     */
    public GenerateModelMojo()
    {
        super();
        this.setMojoName( "GenerateModelMojo" );
    }

    /**
     * This method implments the abstract method in the base class to allow a different template name to be processed.
     * 
     * @return The value of template name.
     */
    public String getTemplateName()
    {
        return locateTemplate( AppfuseProperties.MODEL_TEMPLATE_NAME,
                               AppfuseProperties.MODEL_TEMPLATE_NAME_PROPERTY_KEY );
    }

    /**
     * This method implements the abstract method in the base class allowing a different pattern to be used for this
     * generation.
     * 
     * @return The value of output pattern.
     */
    public String getOutputPattern()
    {
        return buildOutputPattern( AppfuseProperties.MODEL_OUTPUT_PATTERN,
                                   AppfuseProperties.MODEL_OUTPUT_PATTERN_PROPERTY_KEY, this.getModelPackageName() );

    }

    /**
     * This method is used to make sure the proper properties are set to be used in processing this mojo. These
     * properties are passed in to the generator to be used within any freemarker templates.
     * 
     * @param inProperties
     *            The project properties that already have been populated.
     */
    protected void validateProperties( final Properties inProperties )
    {
        // add the model package name in the properties for access inside the template.
        inProperties.put( "modelpackagename", this.getModelPackageName() );
    }

    /**
     * toString methode: creates a String representation of the object
     * 
     * @return the String representation
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( super.toString() );
        buffer.append( "GenerateModelMojo[" );
        buffer.append( "]" );
        return buffer.toString();
    }
}
