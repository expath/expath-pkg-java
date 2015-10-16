<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:ext="http://www.example.com/ext"
                xmlns:java="java:com.example.ext.Simple"
                exclude-result-prefixes="xs ext"
                version="2.0">

   <xsl:function name="ext:hello" as="xs:string">
      <xsl:param name="who" as="xs:string"/>
      <xsl:sequence select="java:hello($who)"/>
   </xsl:function>

</xsl:stylesheet>
