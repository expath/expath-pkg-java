<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:http="http://expath.org/ns/http-client"
                version="2.0">

   <!-- the public and absolute import URI -->
   <xsl:import href="http://expath.org/ns/http-client.xsl"/>

   <xsl:output indent="yes"/>

   <xsl:template name="main" match="/">

      <!-- the request element -->
      <xsl:variable name="req" as="element()">
         <http:request href="http://www.balisage.net/" method="get"/>
      </xsl:variable>

      <!-- actually sending it -->
      <xsl:sequence select="http:send-request($req)[1]"/>

   </xsl:template>

</xsl:stylesheet>
