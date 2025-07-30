/**
 * Contains code to work with the XML report that JDepend generates.
 *
 * @author RealLifeDeveloper
 */
@XmlJavaTypeAdapter(value = StringTrimmingXmlAdapter.class, type = String.class)
package com.reallifedeveloper.maven.jdepend.xml;

import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
