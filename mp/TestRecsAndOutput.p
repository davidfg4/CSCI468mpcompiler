program testExprRecs;

var x, y:integer; z:float;
bool1, bool2:boolean;
string1 : string;

function foo:integer;
var
  bar: float;
  baz: integer;
  function nested:float;
  var
    nested1: integer;
    nested2: float;
    nestBool: boolean;
    string1: string;
    begin
      read(x,y,z,string1,bar,baz,nested1,nested2);
      nested2 := nested1+(x+2)*y;
      nested1 := nested2;
      if(not(bar+x>y+3)) then
        write(-nested2+1, nested1-2, x div y, 5*(y*3+z-x), -z);
      bool1 := bool1 or bool2 and bool1 or (nestBool and bool2);
    end;
begin
  bar := x*y+z;
  baz := 2*(x+y)+bar*10;
  writeln(bar, baz, x, -z);
end;
begin
  string1 := 'string_assgn';
  write('string_lit', string1);
  if((x>4) or bool1 and bool2) then 
    bool1 := false
  else
  begin
    bool2 := true;
    x := 3;
    if(bool1) then
      x := z;
  end
end.
