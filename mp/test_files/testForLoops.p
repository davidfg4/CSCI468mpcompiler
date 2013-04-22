program testForLoops;
var i, n: integer;
begin
  n := 200;
  for i := n downto 100 do
  begin
    writeln(i);
  end;

  for i:= 1 to n do
  begin
    writeln(i);
  end
end.
