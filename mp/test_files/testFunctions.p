program Main;
  var a, b, c: integer;
  d : float;

  function Fred(var x : integer): float;
    var t : integer;
    begin
        read(x);
        Fred := x * a * b * c * d;
      end;

  procedure Proc(var proc1 : integer; var proc2 : float);
  var procVar1 : integer; 
  begin
    read(proc1);
    read(d);
    c := proc1 * d;
  end;


  begin
    read(a,b);
    Proc(a, d);
    writeln('a:', a);
    writeln('b:', b);
    writeln('c:', c);
    writeln('d:', d);
    writeln('Fred: ', -Fred(a), ' a:', a);
end.

