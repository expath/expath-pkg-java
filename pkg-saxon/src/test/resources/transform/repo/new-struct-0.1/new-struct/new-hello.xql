module namespace n = "http://example.org/ns/new/hello";

declare function n:new-hello($who as xs:string) as xs:string
{
   concat('New hello, ', $who, '!')
};
