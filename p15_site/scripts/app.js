'use strict';

/* 
 * It is needed to add the ui.bootstrap module 
 * as a dependency to the Angular modules.
 */
angular.module('TMDashboard-Main', ['ui.bootstrap', 'ngResource'])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {

          templateUrl: 'views/initializer.html',
          controller:  'BambooP15RestController'
      })
      .otherwise({
        redirectTo: '/'
      });
  });
