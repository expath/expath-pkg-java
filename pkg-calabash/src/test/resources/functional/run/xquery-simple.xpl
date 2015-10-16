<?xml version="1.0" encoding="UTF-8"?>
<!-- ===================================================================== -->
<!--  File:       xquery-simple.xpl                                        -->
<!--  Author:     F. Georges                                               -->
<!--  Company:    H2O Consulting                                           -->
<!--  Date:       2010-05-15                                               -->
<!--  Tags:                                                                -->
<!--    Copyright (c) 2010 Florent Georges (see end of file.)              -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->


<!--
   The purpose of this test is to be sure than it passes without packaging
   specific stuff.  So in case the packaging related tests fail, we know if
   something is wrong with something else (like the imported query itself is
   not correct in the first place...)
-->

<p:declare-step xmlns:p="http://www.w3.org/ns/xproc"
                xmlns:pkg="http://expath.org/ns/pkg"
                version="1.0">

   <p:input port="source">
      <p:document href="external-invoice.xml"/>
   </p:input>

   <p:output port="result"/>

   <p:xquery>
      <p:input port="query">
         <p:data href="repo/invoice-1.0/invoice/invoice-main.xq"/>
      </p:input>
      <p:input port="parameters">
         <p:empty/>
      </p:input>
   </p:xquery>

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
