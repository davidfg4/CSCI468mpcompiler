program test_program;
var
a,b,c: integer;
x: float;
y,z: boolean;

procedure testProcedure(arg1:integer ; arg2:float);
begin
	testFunction;
end;

function testFunction:integer;
var
var1: integer;
var2: float;
begin
	testProcedure(var1, var2);
end;

begin
	someFunction;
    a := b;
    c := testFunction;
	while a <> 5 do
	begin
		testProcedure(b, x);
        testFunction
	end
end.
