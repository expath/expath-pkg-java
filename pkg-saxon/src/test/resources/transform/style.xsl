<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:h="http://example.org/ns/new/hello"
                exclude-result-prefixes="#all"
                version="2.0">

   <xsl:import href="http://example.org/ns/new/hello.xsl"/>

   <xsl:output omit-xml-declaration="yes"/>

   <xsl:template match="/" name="main">
      <result>
         <xsl:value-of select="h:new-hello('world')"/>
      </result>
   </xsl:template>

</xsl:stylesheet>
