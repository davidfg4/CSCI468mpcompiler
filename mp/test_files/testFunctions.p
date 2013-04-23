program Main;
  var a, b, c: integer;

  {function Fred(x : integer): integer;
    var t : integer;
    begin
	writeln('a:', x);
        read(c);
        Fred := c;
      end; }

  function Proc(var proc1 : integer) : integer;
  var procVar1 : integer; 
  begin
    read(procVar1);
    c := proc1;
    writeln('proc1: ', proc1, ' procVar1: ', procVar1, ' c: ', c);
  end;


  begin
    read(a,b);
    {c := Fred(a);}
    {Proc(1);}
    {Proc(a+b);}
    Proc(a);
    writeln('b:', b);
    writeln('c:', c);
end.

