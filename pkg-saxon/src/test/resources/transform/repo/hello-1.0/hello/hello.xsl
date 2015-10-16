<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:h="http://www.example.org/hello"
                version="2.0">

   <xsl:function name="h:hello" as="xs:string">
      <xsl:param name="who" as="xs:string"/>
      <xsl:sequence select="concat('Hello, ', $who, '!')"/>
   </xsl:function>

</xsl:stylesheet>
