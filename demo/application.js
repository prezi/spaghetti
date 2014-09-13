console.log("Application booting up.");
require["config"]({"baseUrl":".","paths":{"prezi.graphics.core": "modules/prezi.graphics.core/prezi.graphics.core","prezi.graphics.text": "modules/prezi.graphics.text/prezi.graphics.text","prezi.graphics.text.render": "modules/prezi.graphics.text.render/prezi.graphics.text.render","prezi.test.client": "modules/prezi.test.client/prezi.test.client"}});require(["prezi.test.client"],function(__mainModule){__mainModule["module"]["main"]();});
console.log("Application has run.");
