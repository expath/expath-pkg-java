<?xml version="1.0" encoding="UTF-8"?>
<!-- ===================================================================== -->
<!--  File:       invoice.xpl                                              -->
<!--  Author:     F. Georges                                               -->
<!--  Company:    H2O Consulting                                           -->
<!--  Date:       2009-10-19                                               -->
<!--  Tags:                                                                -->
<!--    Copyright (c) 2009 Florent Georges (see end of file.)              -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->


<p:library xmlns:tns="http://fgeorges.org/test/invoice-steps"
           xmlns:p="http://www.w3.org/ns/xproc"
           xmlns:c="http://www.w3.org/ns/xproc-step"
           xmlns:pkg="http://expath.org/ns/pkg"
           version="1.0">

   <p:declare-step type="tns:validate-external">
      <p:input  port="source" primary="true"/>
      <p:output port="result" primary="true"/>
      <!-- Validated twice, just to test both RNC and XSD. -->
      <p:validate-with-relax-ng assert-valid="true">
         <p:input port="schema">
            <p:data href="http://fgeorges.org/test/external-invoice.rnc"
                    pkg:kind="rnc"/>
         </p:input>
      </p:validate-with-relax-ng>
      <p:validate-with-xml-schema mode="strict">
         <p:input port="schema">
            <p:document href="http://fgeorges.org/test/external-invoice"
                        pkg:kind="xsd"/>
         </p:input>
      </p:validate-with-xml-schema>
   </p:declare-step>

   <p:declare-step type="tns:transform">
      <p:input  port="source" primary="true"/>
      <p:output port="result" primary="true"/>
      <p:xslt>
         <p:input port="stylesheet">
            <p:document href="http://fgeorges.org/test/invoice.xsl"
                        pkg:kind="xslt"/>
         </p:input>
         <p:input port="parameters">
            <p:empty/>
         </p:input>
      </p:xslt>
   </p:declare-step>

</p:library>


<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.             -->
<!--                                                                       -->
<!-- The contents of this file are subject to the Mozilla Public License   -->
<!-- Version 1.0 (the "License"); you may not use this file except in      -->
<!-- compliance with the License. You may obtain a copy of the License at  -->
<!-- http://www.mozilla.org/MPL/.                                          -->
<!--                                                                       -->
<!-- Software distributed under the License is distributed on an "AS IS"   -->
<!-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See  -->
<!-- the License for the specific language governing rights and limitations-->
<!-- under the License.                                                    -->
<!--                                                                       -->
<!-- The Original Code is: all this file.                                  -->
<!--                                                                       -->
<!-- The Initial Developer of the Original Code is Florent Georges.        -->
<!--                                                                       -->
<!-- Contributor(s): none.                                                 -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
