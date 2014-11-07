define(["require","todos.storage"],function(){var module=(function(dependencies){return function(init){return init.call({},(function(){var moduleUrl=dependencies[0]["toUrl"]("todos.ui.js");var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/"));return{getSpaghettiVersion:function(){return "2.0";},getModuleName:function(){return "todos.ui";},getModuleVersion:function(){return "unspecified";},getResourceUrl:function(resource){if(resource.substr(0,1)!="/"){resource="/"+resource;}return baseUrl+resource;},"dependencies":{"require":dependencies[0],"todos.storage":dependencies[1]}};})());};})(arguments);return{"module":(function(){return module(function(Spaghetti) {
/// <reference path='../_all.ts' />
/// <reference path='../_all.ts' />
var todos;
(function (todos) {
    var ui;
    (function (ui) {
        'use strict';
        /**
         * Directive that places focus on the element it is applied to when the expression it binds to evaluates to true.
         */
        function todoFocus($timeout) {
            return {
                link: function ($scope, element, attributes) {
                    $scope.$watch(attributes.todoFocus, function (newval) {
                        if (newval) {
                            $timeout(function () { return element[0].focus(); }, 0, false);
                        }
                    });
                }
            };
        }
        ui.todoFocus = todoFocus;
        todoFocus.$inject = ['$timeout'];
    })(ui = todos.ui || (todos.ui = {}));
})(todos || (todos = {}));
/// <reference path='../_all.ts' />
var todos;
(function (todos) {
    var ui;
    (function (ui) {
        'use strict';
        /**
         * Directive that executes an expression when the element it is applied to loses focus.
         */
        function todoBlur() {
            return {
                link: function ($scope, element, attributes) {
                    element.bind('blur', function () {
                        $scope.$apply(attributes.todoBlur);
                    });
                }
            };
        }
        ui.todoBlur = todoBlur;
    })(ui = todos.ui || (todos.ui = {}));
})(todos || (todos = {}));
/// <reference path='../_all.ts' />
var todos;
(function (todos) {
    var ui;
    (function (ui) {
        'use strict';
        /**
         * The main controller for the app. The controller:
         * - retrieves and persists the model via the todoStorage service
         * - exposes the model to the template and provides event handlers
         */
        var TodoCtrl = (function () {
            // dependencies are injected via AngularJS $injector
            // controller's name is registered in Application.ts and specified from ng-controller attribute in index.html
            function TodoCtrl($scope, $location, todoStorage, filterFilter) {
                var _this = this;
                this.$scope = $scope;
                this.$location = $location;
                this.todoStorage = todoStorage;
                this.filterFilter = filterFilter;
                this.todos = $scope.todos = todoStorage.get();
                $scope.newTodo = '';
                $scope.editedTodo = null;
                // 'vm' stands for 'view model'. We're adding a reference to the controller to the scope
                // for its methods to be accessible from view / HTML
                $scope.vm = this;
                // watching for events/changes in scope, which are caused by view/user input
                // if you subscribe to scope or event with lifetime longer than this controller, make sure you unsubscribe.
                $scope.$watch('todos', function () { return _this.onTodos(); }, true);
                $scope.$watch('location.path()', function (path) { return _this.onPath(path); });
                if ($location.path() === '')
                    $location.path('/');
                $scope.location = $location;
            }
            TodoCtrl.prototype.onPath = function (path) {
                this.$scope.statusFilter = (path === '/active') ? { completed: false } : (path === '/completed') ? { completed: true } : null;
            };
            TodoCtrl.prototype.onTodos = function () {
                this.$scope.remainingCount = this.filterFilter(this.todos, { completed: false }).length;
                this.$scope.doneCount = this.todos.length - this.$scope.remainingCount;
                this.$scope.allChecked = !this.$scope.remainingCount;
                this.todoStorage.put(this.todos);
            };
            TodoCtrl.prototype.addTodo = function () {
                var newTodo = this.$scope.newTodo.trim();
                if (!newTodo.length) {
                    return;
                }
                this.todos.push({ title: newTodo, completed: false });
                this.$scope.newTodo = '';
            };
            TodoCtrl.prototype.editTodo = function (todoItem) {
                this.$scope.editedTodo = todoItem;
            };
            TodoCtrl.prototype.doneEditing = function (todoItem) {
                this.$scope.editedTodo = null;
                todoItem.title = todoItem.title.trim();
                if (!todoItem.title) {
                    this.removeTodo(todoItem);
                }
            };
            TodoCtrl.prototype.removeTodo = function (todoItem) {
                this.todos.splice(this.todos.indexOf(todoItem), 1);
            };
            TodoCtrl.prototype.clearDoneTodos = function () {
                this.$scope.todos = this.todos = this.todos.filter(function (todoItem) { return !todoItem.completed; });
            };
            TodoCtrl.prototype.markAll = function (completed) {
                this.todos.forEach(function (todoItem) {
                    todoItem.completed = completed;
                });
            };
            // $inject annotation.
            // It provides $injector with information about dependencies to be injected into constructor
            // it is better to have it close to the constructor, because the parameters must match in count and type.
            // See http://docs.angularjs.org/guide/di
            TodoCtrl.$inject = [
                '$scope',
                '$location',
                'todoStorage',
                'filterFilter'
            ];
            return TodoCtrl;
        })();
        ui.TodoCtrl = TodoCtrl;
    })(ui = todos.ui || (todos.ui = {}));
})(todos || (todos = {}));
/// <reference path='_all.ts' />
var todos;
(function (_todos) {
    var storage;
    (function (storage) {
        var StorageModule = (function () {
            function StorageModule() {
            }
            StorageModule.createStorage = function () {
                return StorageModule.module.createStorage();
            };
            StorageModule.module = Spaghetti["dependencies"]["todos.storage"]["module"];
            return StorageModule;
        })();
        storage.StorageModule = StorageModule;
    })(storage = _todos.storage || (_todos.storage = {}));
})(todos || (todos = {}));
/*
 * Generated by Spaghetti 2.0 at 2014-11-07 15:02:51
 */
var todos;
(function (todos) {
    var ui;
    (function (ui) {
        var __UiModuleProxy = (function () {
            function __UiModuleProxy() {
            }
            __UiModuleProxy.prototype.main = function () {
                todos.ui.UiModule.main();
            };
            return __UiModuleProxy;
        })();
        ui.__UiModuleProxy = __UiModuleProxy;
        function __createSpaghettiModule() {
            return new todos.ui.__UiModuleProxy();
        }
        ui.__createSpaghettiModule = __createSpaghettiModule;
    })(ui = todos.ui || (todos.ui = {}));
})(todos || (todos = {}));
var todos;
(function (todos) {
    var ui;
    (function (ui) {
        var UiModule = (function () {
            function UiModule() {
            }
            UiModule.main = function () {
                angular.module('todomvc', []).controller('todoCtrl', ui.TodoCtrl).directive('todoBlur', ui.todoBlur).directive('todoFocus', ui.todoFocus).factory('todoStorage', function () {
                    return todos.storage.StorageModule.createStorage();
                });
                angular.element(document).ready(function () {
                    angular.bootstrap(document, ['todomvc']);
                });
            };
            return UiModule;
        })();
        ui.UiModule = UiModule;
    })(ui = todos.ui || (todos.ui = {}));
})(todos || (todos = {}));
/// <reference path='libs/jquery/jquery.d.ts' />
/// <reference path='libs/angular/angular.d.ts' />
/// <reference path='interfaces/ITodoScope.ts' />
/// <reference path='directives/TodoFocus.ts' />
/// <reference path='directives/TodoBlur.ts' />
/// <reference path='controllers/TodoCtrl.ts' />
/// <reference path='Application.ts' />
/// <reference path='build/headers/StorageModule.ts' />
/// <reference path='build/headers/UiModule.ts' />
/// <reference path='UiModule.ts' />
//# sourceMappingURL=ui.js.map
return todos.ui.__createSpaghettiModule(Spaghetti);

})

})(),"version":"unspecified","spaghettiVersion":"2.0"};});