program testExprRecs;

var x, y:integer; z:float;

function foo:integer;
var
  bar: float;
  baz: integer;
  function nested:float;
  var
    nested1: integer;
    nested2: float;
    begin
      nested2 := nested1+(x+2)*y;
      nested1 := nested2;
    end;
begin
  bar := x*y+z;
  baz := 2+x+y*10;
  
end;
begin
end.
