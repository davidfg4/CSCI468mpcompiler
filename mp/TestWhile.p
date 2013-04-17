program testExprRecs;

var x, y:integer; z:float;
bool1, bool2, bool3 : boolean;
begin
  while((not bool1 or bool2) or (bool1 and bool3) and (y<z)) do
  begin
    read(x);
    x := x + 2;
    write(x);
  end;
  repeat 
    x := x+y;
    y := 2*x div 2;
  until((x>100) or (y>100));
end.
