program testNegation;

var x, y:integer; z:float;
bool1, bool2, bool3 : boolean;
begin
  bool1:=true;
  bool2:=false;
  bool3:=true;
  x:=1;
  y:=2;
  z:=-3.2;
  read(x);
  if(not(x>-5)) then
    write(-x);
 while(x>0) do
  begin
    read(x);
    x := -(-x + 2);
    write(-(x*x));
  end;
  repeat 
    x := x-z;
    y := 2*x div 2;
  until((x>100) or (y>100));
end.
