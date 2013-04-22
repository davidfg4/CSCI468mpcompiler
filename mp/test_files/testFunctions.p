program Main;
  var a, b, c: integer;

  {function Fred(x : integer): integer;
    var t : integer;
    begin
	writeln('a:', x);
        read(c);
        Fred := c;
      end; }

  procedure Proc(proc1 : integer);
  var procVar1 : integer; 
  begin
    read(procVar1);
    c := proc1;
    writeln('proc1: ', proc1, ' procVar1: ', procVar1, ' c: ', c);
  end;


  begin
    read(a,b);
    {c := Fred(a);}
    Proc(-(a+b));
    writeln('b:', b);
    writeln('c:', c);
end.

