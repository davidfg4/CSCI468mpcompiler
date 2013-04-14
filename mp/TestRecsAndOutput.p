program testExprRecs;

var x, y:integer; z:float;
bool1, bool2:boolean;

function foo:integer;
var
  bar: float;
  baz: integer;
  function nested:float;
  var
    nested1: integer;
    nested2: float;
    nestBool: boolean;
    begin
      read(x,y,z,bar,baz,nested1,nested2);
      nested2 := nested1+(x+2)*y;
      nested1 := nested2;
      if(not(bar+x>y+3)) then
        write(-nested2+1, nested1-2, x div y, 5*(y*3+z-x), -z);
      bool1 := bool1 or bool2 and bool1 or (nestBool and bool2);
    end;
begin
  bar := x*y+z;
  baz := 2*(x+y)+bar*10;
  writeln(bar, baz, x);
end;
begin
end.
