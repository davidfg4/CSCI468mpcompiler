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
      read(x,y,z,bar,baz,nested1,nested2);
      nested2 := nested1+(x+2)*y;
      nested1 := nested2;
      if(bar+x>y+3) then
        write(-nested2+1, nested1-2, x div y, 5*(y*3+z-x), -z);
    end;
begin
  bar := x*y+z;
  baz := 2*(x+y)+bar*10;
  writeln(bar, baz, x);
end;
begin
end.
