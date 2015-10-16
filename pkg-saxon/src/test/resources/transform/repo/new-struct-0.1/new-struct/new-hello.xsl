<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:n="http://example.org/ns/new/hello"
                version="2.0">

   <xsl:function name="n:new-hello" as="xs:string">
      <xsl:param name="who" as="xs:string"/>
      <xsl:sequence select="concat('New hello, ', $who, '!')"/>
   </xsl:function>

</xsl:stylesheet>
