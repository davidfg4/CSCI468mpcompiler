program test_program;
var
a,b,c: integer;
x: float;
y,z: boolean;

procedure testProcedure(arg1:integer ; arg2:float);
begin
	a := arg1;
end;

function testFunction:integer;
var
var1: integer;
var2: float;
begin
	testProcedure(var1, var2);
end;

begin
    a := b;
    c := testFunction;
	while a <> 5 do
	begin
		testProcedure(b, x);
        c := testFunction
	end
end.
