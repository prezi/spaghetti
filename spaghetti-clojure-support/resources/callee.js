var JavaScriptExampleObject = {
  myInt: 42,
  myString: "fortytwo",
  myArray: [4,2],

  getInt: function() {
    return this.myInt;
  },
  setInt: function(someInt) {
    this.myInt = someInt;
  },
  getString: function() {
    return this.myString;
  },
  setString: function(someString) {
    this.myString = someString;
  },
  getArray: function() {
    return this.myArray;
  },
  setArray: function(someArray) {
    this.myArray = someArray;
  },
  callBack: function(callBackFunction) {
    if (typeof callBackFunction!==undefined) {
      return callBackFunction();
    }
  }
}

console.log(JavaScriptExampleObject.getInt());
console.log(JavaScriptExampleObject.setInt(43));
console.log(JavaScriptExampleObject.getInt());

console.log(JavaScriptExampleObject.getString());
console.log(JavaScriptExampleObject.setString("fortythree"));
console.log(JavaScriptExampleObject.getString());

console.log(JavaScriptExampleObject.getArray());
console.log(JavaScriptExampleObject.setArray([4,3]));
console.log(JavaScriptExampleObject.getArray());

console.log(JavaScriptExampleObject.callBack(function(){return "the meaning of life"}));
