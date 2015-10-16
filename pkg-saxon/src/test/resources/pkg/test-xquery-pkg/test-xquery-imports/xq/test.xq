module namespace test = "http://www.expath.org/test/imports";

import module namespace i = "http://www.expath.org/test/imported"
   at "imported.xq";

declare function test:hello() as element()
{
  <hello>{ i:who() }!</hello>
};
