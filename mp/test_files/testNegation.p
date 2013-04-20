program testNegation;

var x, y:integer; z:float;
bool1, bool2, bool3 : boolean;
begin
  if(not(not bool1 and bool2) or not(not bool1 and not bool2)) then
    x:= -(-(-x+2) + (-(y+(-x)*(-(z+2)))));
end.
