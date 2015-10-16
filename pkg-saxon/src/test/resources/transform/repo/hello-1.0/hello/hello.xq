module namespace h = "http://www.example.org/hello";

declare function h:hello($who as xs:string) as xs:string
{
   concat('Hello, ', $who, '!')
};
