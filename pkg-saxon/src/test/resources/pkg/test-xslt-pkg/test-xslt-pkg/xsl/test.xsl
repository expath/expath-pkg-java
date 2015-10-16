<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:test="http://www.expath.org/test"
                exclude-result-prefixes="test"
                version="2.0">

   <xsl:function name="test:hello" as="element()">
      <hello>World!</hello>
   </xsl:function>

</xsl:stylesheet>
