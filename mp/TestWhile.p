program testExprRecs;

var x, y:integer; z:float;
bool1, bool2, bool3 : boolean;
string1 : string;
begin
  while((bool1 or bool2) or (bool1 and bool3) and (y<z) and ((5*2) > z)) do
  begin
    read(x);
    x := x + 2;
    string1 := string1 + 'asdf';
    write(x);
  end
end.
