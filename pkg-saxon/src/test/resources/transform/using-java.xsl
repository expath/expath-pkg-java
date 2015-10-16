<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ext="http://www.example.com/ext"
                exclude-result-prefixes="#all"
                version="2.0">

   <xsl:import href="http://www.example.com/ext.xsl"/>

   <xsl:output omit-xml-declaration="yes"/>

   <xsl:template match="/" name="main">
      <result>
         <xsl:value-of select="ext:hello('world')"/>
      </result>
   </xsl:template>

</xsl:stylesheet>
