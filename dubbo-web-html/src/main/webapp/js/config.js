// config

var app = angular.module('app');
/**
 * http响应拦截器
 */
app.factory('httpInterceptor', [ '$q', '$injector',function($q, $injector) {
    return {
        'responseError': function (response) {
            if (response.status == 401) {
                var rootScope = $injector.get('$rootScope');
                sessionStorage.removeItem('token');
                rootScope.$state.go('signin');
                $.notify({
                    message: "<div style='text-align: center'><i class='fa fa-warning'></i> 身份验证异常，请重新登录！</div>"
                },{
                    type:"danger",
                    placement:{
                        align:"center"
                    }
                });
            } else {
            	// response.status == 400、405、415、500
            	console.log("httpInterceptor: "+response);
            	var result = response.data;
            	$.notify({
                    message: "<div style='text-align: center'><i class='fa fa-warning'></i>"+result.message+"</div>"
                },{
                    type:"danger",
                    placement:{
                    	from:"bottom",
                        align:"right"
                    }
                });
            }
            return $q.reject(response);
        },
        'response': function (response) {
            return response;
        },
        'request': function (config) {
            var token;
            if (token = sessionStorage.getItem('token'))
                config.headers['Authorization'] = token;
            console.dir(config);
            if(config.url.toLowerCase().indexOf('authenticate') > 0) {
            	config.headers['Content-Type'] = "application/x-www-form-urlencoded";
            }
            return config;
        },
        'requestError': function (config) {
            return $q.reject(config);
        }
    };
}]);

app.config(
    [        '$controllerProvider', '$compileProvider', '$filterProvider', '$provide', '$locationProvider','$httpProvider','angularShiroConfigProvider','authenticatorProvider',
    function ($controllerProvider,   $compileProvider,   $filterProvider,   $provide ,$locationProvider, $httpProvider, shiroConfig, authenticator) {
        // lazy controller, directive and service
        app.controller = $controllerProvider.register;
        app.directive  = $compileProvider.directive;
        app.filter     = $filterProvider.register;
        app.factory    = $provide.factory;
        app.service    = $provide.service;
        app.constant   = $provide.constant;
        app.value      = $provide.value;
        $httpProvider.defaults.headers.put['Content-Type'] = 'application/json;charset=UTF-8';
        $httpProvider.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
        //$httpProvider.defaults.headers.post['X-Requested-With'] = 'XMLHttpRequest';  

        $httpProvider.defaults.transformRequest = function (data) {
            return angular.isObject(data) && String(data) !== '[object File]' ? $.param(data) : data;
        };
        // $locationProvider.html5Mode(true);
        $httpProvider.interceptors.push('httpInterceptor');	// 注册拦截器服务
        
        shiroConfig.setAuthenticateUrl('http://localhost:8114/api/authenticate');
    }
  ]);

app.constant('host','http://localhost:8114');