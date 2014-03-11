function Greeter()
{
}

Greeter.prototype.saySomething = function() {
   name = name + "! ";
   return "JavaScript says - how do u do " + name;
}

var grt = new Greeter();
grt.saySomething();