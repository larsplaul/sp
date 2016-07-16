'use strict';

angular.module('myAppRename.admin.delphi', ['ngRoute'])

  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/view_admin_delphi', {
      templateUrl: 'app/viewsAdmin/viewDelphi/delphi.html',
      controller: 'delphiCtrl'
    });
  }])
  .factory("evalData",function(){
     var evaluation = {};
     var getEvaluation = function(){
       return evaluation;
     }
    return getEvaluation;
  })

  .controller('delphiCtrl', function ($http, $scope,CONSTANTS,evalData) {

    $scope.evaluation = evalData();
    $scope.submitted = false;
    //var domain= $location.protocol() + "://" + $location.host() + ":" + $location.port();
    var domain= CONSTANTS.DELPHI_DOMAIN;

    $scope.submit = function () {
      $http({
        method: 'POST',
        url: CONSTANTS.DELPHI_API,
        data: $scope.evaluation
      }).then(function successCallback(res) {
        $scope.url = domain +"/eval/"+res.data.url;
        $scope.idOpen = domain +"/eval/open/"+res.data.url+"/"+res.data.idOpen;
        $scope.idClose = domain +"/eval/close/"+res.data.url+"/"+res.data.idClose;
        $scope.submitted = true;
        $scope.codes = res.data.codes.join("\n");
      }, function errorCallback(res) {
        $scope.error = res.status + ": " + res.data.statusText;
      });
    };

    $scope.clear = function(){
      $scope.submitted = false;
      $scope.evaluation = {};
      $scope.url = null;
      $scope.codes = [];
    };

  });