var cl = casserole.callee;

var greeting = cl.say_hello("Jack");
console.log("Result: %o", greeting);

function getTheAnswer() { return 42; }
console.log(cl.exec(getTheAnswer));

var obj = {};
var patched = cl.add_attr(obj, "readIt", getTheAnswer);
console.log(patched.readIt());


