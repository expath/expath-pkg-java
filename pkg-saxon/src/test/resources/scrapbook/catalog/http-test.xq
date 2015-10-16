xquery version "1.0";

import module namespace http = "http://expath.org/ns/http-client";

http:send-request(
   <http:request href="http://www.balisage.net/" method="get"/>
)[1]
