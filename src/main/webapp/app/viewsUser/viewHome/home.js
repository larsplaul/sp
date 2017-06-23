'use strict';

var app = angular.module('myAppRename.user.home', ['ngRoute']);

app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/view_studentDetail', {
      templateUrl: 'app/viewsUser/viewHome/home.html',
      controller: 'StudentStudyPointCtrl'
    });
  }]);

app.filter("maxPointForPeriod", function () {
  return function (period) {
    var sum = 0;
    if (period == null || period.tasks == null) {
      return sum;
    }
    period.tasks.forEach(function (task) {
      sum += task.maxScore;
    });
    return sum;
  }
});
app.filter("roundDown", function () {
  return function (val) {
    return Math.floor(val);
  }
})

app.controller('StudentStudyPointCtrl', ['$scope', '$http', '$routeParams', '$filter', function ($scope, $http, $routeParams, $filter, restErrorHandler) {

    $scope.name = $routeParams.name; //Only set from Admin View
    $scope.studentId = $routeParams.studentId;
    $scope.max = 100;
    var selectedPeriodName = null;
    var selectedClass = null;

    $scope.showPeriod = function (periodName) {
      $scope.period = getPeriod($scope.allPeriods, periodName);
      selectedPeriodName = periodName;
    };

    $scope.registerAttendance = function (taskId, studypointId) {
      //Hack (deprecated code) to get the value from the input field
      var f = angular.element(document.getElementById("inp" + taskId));
      var code = f[0].value;
      var data = {
        taskId: taskId,
        spId: studypointId,
        code: code
      };

      $http({method: "PUT", url: 'api/user/registerAttendance', data: data})
              .success(function (data, status, headers, config) {
                $scope.getClass(selectedClass);
              })
              .error(function (data, status, headers, config) {
                console.log("UUUUUPPPPPPS");
              });
    };


    $scope.getClass = function (id) {
      selectedClass = id;
      var url = "api/user/myStudyPoints/" + id;
      $http.get(url)
              .success(function (data, status, headers, config) {
                $scope.allPeriods = data.periods;
                $scope.maxPointForSemester = data.maxPointForSemester;
                $scope.requiredPointsPercentage = data.requiredPoints;
                $scope.reqPointsPercentageString = data.requiredPoints + "%";
                $scope.requiredPoints = $scope.maxPointForSemester * data.requiredPoints / 100;
                
                var pointsScoredForPeriod = $filter('sumForSemester')($scope.allPeriods);
                var pointScoredPercent = pointsScoredForPeriod * 100 / $scope.maxPointForSemester;
                if (pointScoredPercent < $scope.requiredPointsPercentage) {
                  $scope.type = 'warning';
                } else {
                  $scope.type = 'success';
                }
                $scope.period = selectedPeriodName === null ? null: getPeriod($scope.allPeriods,selectedPeriodName);
              })
              .error(function (data, status, headers, config) {
                restErrorHandler.handleErrors(data, status, $scope);
              })
    }

    var urlForStudentsClasses = "api/user/myClasses";
    $http.get(urlForStudentsClasses)
            .success(function (data, status, headers, config) {
              $scope.classes = data;
              $scope.allPeriods = null;
              $scope.period = null;
              ;
              $scope.error = null;
            })
            .error(function (data, status, headers, config) {
              restErrorHandler.handleErrors(data, status, $scope);
            });


    function getPeriod(periods, periodName) {
      for (var i = 0; i < periods.length; i++) {
        if (periods[i].periodName === periodName)
          return periods[i];
      }
      return null;
    }

  }]);




