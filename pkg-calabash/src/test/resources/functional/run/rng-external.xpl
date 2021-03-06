<?xml version="1.0" encoding="UTF-8"?>
<!-- ===================================================================== -->
<!--  File:       rng-external.xpl                                         -->
<!--  Author:     F. Georges                                               -->
<!--  Company:    H2O Consulting                                           -->
<!--  Date:       2009-10-19                                               -->
<!--  Tags:                                                                -->
<!--    Copyright (c) 2009 Florent Georges (see end of file.)              -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->


<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:c="http://www.w3.org/ns/xproc-step"
                xmlns:pkg="http://expath.org/ns/pkg"
                xmlns:i="http://fgeorges.org/test/external-invoice"
                version="1.0">

   <p:input port="source">
      <p:inline>
         <i:docs>
            <i:invoice date="2009-10-12">
               <i:line price="15" quantity="10" unitary="1.5">
                  <i:desc>Some stuff.</i:desc>
               </i:line>
               <i:line price="100">
                  <i:desc>Bigger stuff.</i:desc>
               </i:line>
               <i:total tax-excl="115" tax-incl="139.15"/>
            </i:invoice>
            <i:invoice date="2010-01-03">
               <i:line price="150">
                  <i:desc>Some stuff.</i:desc>
               </i:line>
               <i:total tax-excl="150" tax-incl="181.5"/>
            </i:invoice>
         </i:docs>
      </p:inline>
   </p:input>

   <p:output port="result"/>

   <p:validate-with-relax-ng assert-valid="true">
      <p:input port="schema">
         <p:inline>
            <element xmlns="http://relaxng.org/ns/structure/1.0"
                     xmlns:tns="http://fgeorges.org/test/external-invoice"
                     name="tns:docs">
               <oneOrMore>
                  <externalRef href="http://fgeorges.org/test/external-invoice.rng"/>
               </oneOrMore>
            </element>
         </p:inline>
      </p:input>
   </p:validate-with-relax-ng>

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
