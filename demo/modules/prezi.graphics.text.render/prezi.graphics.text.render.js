define(["require","prezi.graphics.core","prezi.graphics.text"],function(){var module=(function(dependencies){return function(init){return init.call({},(function(){var moduleUrl=dependencies[0]["toUrl"]("prezi.graphics.text.render.js");var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/"));return{getSpaghettiVersion:function(){return "2.0-rc6-SNAPSHOT";},getName:function(){return "prezi.graphics.text.render";},getVersion:function(){return "0.1";},getResourceUrl:function(resource){if(resource.substr(0,1)!="/"){resource="/"+resource;}return baseUrl+resource;},"dependencies":{"require":dependencies[0],"prezi.graphics.core":dependencies[1],"prezi.graphics.text":dependencies[2]}};})());};})(arguments);return{"module":module(function(Spaghetti) {
var prezi;
(function (prezi) {
    (function (graphics) {
        (function (_text) {
            (function (render) {
                var RenderModule = (function () {
                    function RenderModule() {
                    }
                    RenderModule.createRenderer = function (prefix, suffix) {
                        console.log("Text renderer name: " + Spaghetti.getName());
                        console.log("Text renderer version: " + Spaghetti.getVersion());
                        console.log("Text renderer built by Spaghetti version " + Spaghetti.getSpaghettiVersion());
                        console.log("Core stuff: " + prezi.graphics.core.Core.giveMeANumber());
                        return new RendererImpl(prefix, suffix);
                    };
                    RenderModule.getResource = function () {
                        return Spaghetti.getResourceUrl("some-resource.txt");
                    };
                    return RenderModule;
                })();
                render.RenderModule = RenderModule;

                var RendererImpl = (function () {
                    function RendererImpl(prefix, suffix) {
                        this.prefix = prefix;
                        this.suffix = suffix;
                        this.testStuff = prezi.graphics.text.Layout.createTestStuff();
                        var generic = {
                            element: "lajos"
                        };
                        console.log("Generic: ", generic.element);
                    }
                    RendererImpl.prototype.render = function (text) {
                        return this.prefix + text.getRawText() + this.suffix + " (" + prezi.graphics.text.Values.HELLO + ")";
                    };

                    RendererImpl.prototype.f = function () {
                        var x = render.Values.MAX_LENGTH;
                        var y = text.Values.HI;
                    };
                    return RendererImpl;
                })();
                render.RendererImpl = RendererImpl;
            })(_text.render || (_text.render = {}));
            var render = _text.render;
        })(graphics.text || (graphics.text = {}));
        var text = graphics.text;
    })(prezi.graphics || (prezi.graphics = {}));
    var graphics = prezi.graphics;
})(prezi || (prezi = {}));
var prezi;
(function (prezi) {
    (function (graphics) {
        (function (core) {
            var Core = (function () {
                function Core() {
                }
                Core.giveMeANumber = function () {
                    return Core.module.giveMeANumber();
                };
                Core.module = Spaghetti["dependencies"]["prezi.graphics.core"]["module"];
                return Core;
            })();
            core.Core = Core;
            (function (JsEnum) {
                JsEnum[JsEnum["ALMA"] = 0] = "ALMA";
                JsEnum[JsEnum["BELA"] = 1] = "BELA";
            })(core.JsEnum || (core.JsEnum = {}));
            var JsEnum = core.JsEnum;
            var JsConst = (function () {
                function JsConst() {
                }
                JsConst.alma = 1;
                JsConst.bela = "bela";
                return JsConst;
            })();
            core.JsConst = JsConst;
        })(graphics.core || (graphics.core = {}));
        var core = graphics.core;
    })(prezi.graphics || (prezi.graphics = {}));
    var graphics = prezi.graphics;
})(prezi || (prezi = {}));
var prezi;
(function (prezi) {
    (function (graphics) {
        (function (_text) {
            var Layout = (function () {
                function Layout() {
                }
                Layout.createText = function (text) {
                    return Layout.module.createText(text);
                };
                Layout.createTestStuff = function () {
                    return Layout.module.createTestStuff();
                };
                Layout.getResource = function () {
                    return Layout.module.getResource();
                };
                Layout.createTestStuffWithStringKey = function () {
                    return Layout.module.createTestStuffWithStringKey();
                };
                Layout.module = Spaghetti["dependencies"]["prezi.graphics.text"]["module"];
                return Layout;
            })();
            _text.Layout = Layout;

            (function (CharacterStyleType) {
                CharacterStyleType[CharacterStyleType["COLOR"] = 0] = "COLOR";

                CharacterStyleType[CharacterStyleType["FONT_FAMILY"] = 1] = "FONT_FAMILY";

                CharacterStyleType[CharacterStyleType["FONT_STYLE"] = 2] = "FONT_STYLE";

                CharacterStyleType[CharacterStyleType["FONT_STRETCH"] = 3] = "FONT_STRETCH";

                CharacterStyleType[CharacterStyleType["FONT_WEIGHT"] = 4] = "FONT_WEIGHT";

                CharacterStyleType[CharacterStyleType["UNDERLINE"] = 5] = "UNDERLINE";

                CharacterStyleType[CharacterStyleType["URL"] = 6] = "URL";
            })(_text.CharacterStyleType || (_text.CharacterStyleType = {}));
            var CharacterStyleType = _text.CharacterStyleType;

            

            

            var Values = (function () {
                function Values() {
                }
                Values.HELLO = 12;

                Values.HI = "Hello";
                return Values;
            })();
            _text.Values = Values;
        })(graphics.text || (graphics.text = {}));
        var text = graphics.text;
    })(prezi.graphics || (prezi.graphics = {}));
    var graphics = prezi.graphics;
})(prezi || (prezi = {}));
var prezi;
(function (prezi) {
    (function (graphics) {
        (function (text) {
            (function (render) {
                var Values = (function () {
                    function Values() {
                    }
                    Values.MAX_LENGTH = 5;

                    Values.PLACEHOLDER = "string";
                    return Values;
                })();
                render.Values = Values;
                var __RenderModuleProxy = (function () {
                    function __RenderModuleProxy() {
                    }
                    __RenderModuleProxy.prototype.createRenderer = function (prefix, suffix) {
                        return prezi.graphics.text.render.RenderModule.createRenderer(prefix, suffix);
                    };
                    __RenderModuleProxy.prototype.getResource = function () {
                        return prezi.graphics.text.render.RenderModule.getResource();
                    };
                    return __RenderModuleProxy;
                })();
                render.__RenderModuleProxy = __RenderModuleProxy;

                function __createSpaghettiModule() {
                    return new prezi.graphics.text.render.__RenderModuleProxy();
                }
                render.__createSpaghettiModule = __createSpaghettiModule;
            })(text.render || (text.render = {}));
            var render = text.render;
        })(graphics.text || (graphics.text = {}));
        var text = graphics.text;
    })(prezi.graphics || (prezi.graphics = {}));
    var graphics = prezi.graphics;
})(prezi || (prezi = {}));

return prezi.graphics.text.render.__createSpaghettiModule(Spaghetti);

})
,"version":"0.1","spaghettiVersion":"2.0-rc6-SNAPSHOT"};});