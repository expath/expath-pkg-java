package com.example.ext;

import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.value.SequenceType;

import static net.sf.saxon.expr.StaticProperty.EXACTLY_ONE;

public class SimpleFunction extends ExtensionFunctionDefinition {
   private static final String NS_PREFIX = "com.example.ext";
   private static final String NS_URI = "http://www.example.com/ext";
   private static final String LOCAL_NAME = "hello";

   @Override
   public StructuredQName getFunctionQName() {
      return new StructuredQName(NS_PREFIX, NS_URI, LOCAL_NAME);
   }

   @Override
   public SequenceType[] getArgumentTypes() {
      ItemType itype = BuiltInAtomicType.STRING;
      SequenceType stype = SequenceType.makeSequenceType(itype, EXACTLY_ONE);
      return new SequenceType[]{stype};
   }

   @Override
   public SequenceType getResultType(SequenceType[] params) {
      ItemType itype = BuiltInAtomicType.STRING;
      return SequenceType.makeSequenceType(itype, EXACTLY_ONE);
   }

   @Override
   public ExtensionFunctionCall makeCallExpression() {
      return new SimpleCall();
   }
}
