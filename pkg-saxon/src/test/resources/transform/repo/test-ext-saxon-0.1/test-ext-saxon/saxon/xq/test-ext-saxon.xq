module namespace ext = "http://www.example.com/ext";
declare namespace java = "java:com.example.ext.Simple";

declare function ext:hello($who as xs:string) as xs:string
{
   java:hello($who)
};
