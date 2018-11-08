package com.example.ext;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;

public class SimpleCall extends ExtensionFunctionCall {

   @Override
   public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
      if (arguments.length != 1) {
         throw new XPathException("There is not exactly 1 param: " + arguments.length);
      } else {
         Item item = arguments[0].head();
         if (item == null) {
            throw new XPathException("The param is an empty sequence");
         } else if (count(arguments[0]) > 1) {
            throw new XPathException("The param sequence has more than one item");
         } else if (!(item instanceof StringValue)) {
            throw new XPathException("The param is not a string");
         } else {
            String res = Simple.hello(item.getStringValue());
            return new StringValue(res);
         }
      }
   }

   private int count(final Sequence argument) throws XPathException {
      final SequenceIterator it = argument.iterate();
      int i = 0;
      while (it.next() != null) {
         i++;
      }
      return i;
   }
}
