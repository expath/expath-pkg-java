<?xml version="1.0" encoding="UTF-8"?>
<!-- ===================================================================== -->
<!--  File:       xsd-redefine.xpl                                         -->
<!--  Author:     F. Georges                                               -->
<!--  Company:    H2O Consulting                                           -->
<!--  Date:       2009-10-19                                               -->
<!--  Tags:                                                                -->
<!--    Copyright (c) 2009 Florent Georges (see end of file.)              -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->


<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:i="http://fgeorges.org/test/external-invoice"
                version="1.0">

   <p:input port="source">
      <p:inline>
         <extended xmlns="http://fgeorges.org/test/external-invoice"
                   date="2009-10-12">
            <line price="15" quantity="10" unitary="1.5">
               <desc>Some stuff.</desc>
            </line>
            <total tax-excl="115" tax-incl="139.15"/>
            <desc>Hello, world!</desc>
         </extended>
      </p:inline>
   </p:input>

   <p:output port="result"/>

   <p:validate-with-xml-schema mode="strict">
      <p:input port="schema">
         <p:inline>
            <xs:schema targetNamespace="http://fgeorges.org/test/external-invoice"
                       elementFormDefault="qualified">
               <xs:redefine schemaLocation="http://fgeorges.org/test/external-invoice.xsd">
                  <xs:complexType name="invoice">
                     <xs:complexContent>
                        <xs:extension base="i:invoice">
                           <xs:sequence>
                              <xs:element name="desc" type="xs:string" minOccurs="0"/>
                           </xs:sequence>
                        </xs:extension>
                     </xs:complexContent>
                  </xs:complexType>
               </xs:redefine>
               <xs:element name="extended" type="i:invoice"/>
            </xs:schema>
         </p:inline>
      </p:input>
   </p:validate-with-xml-schema>

</p:declare-step>


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
