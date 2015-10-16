<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:http="http://expath.org/ns/http-client"
                xmlns:h="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="#all"
                version="2.0">

   <xsl:import href="http://expath.org/ns/http-client.xsl"/>

   <xsl:template match="/">
      <xsl:variable name="req" as="element()">
         <http:request href="http://www.fgeorges.org/" method="get"/>
      </xsl:variable>
      <title>
         <xsl:value-of select="http:send-request($req)[2]/h:html/h:head/h:title"/>
      </title>
   </xsl:template>

</xsl:stylesheet>
