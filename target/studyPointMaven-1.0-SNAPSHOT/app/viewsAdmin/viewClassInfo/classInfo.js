'use strict';

var app = angular.module('myAppRename.admin.classInfo', ['ngRoute'])

        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider.when('/view_admin_classInfo', {
              templateUrl: 'app/viewsAdmin/viewClassInfo/classInfo.html',
              controller: 'classInfoCtrl'
            });
          }]);

app.filter("roundDown", function () {
  return function (val) {
    return Math.floor(val);
  };
});

app.factory("classStore", function () {
  var classes = [];
  var obj = {};
  obj.setClasses = function (c) {
    classes = c;
  };
  obj.getClasses = function () {
    return classes;
  };
  return obj;
});

app.controller('classInfoCtrl', ['$scope', '$http', 'classStore','restErrorHandler', function ($scope, $http, classStore,restErrorHandler) {

    //REST-1
    $scope.getClasses = function () {
      $scope.classes = classStore.getClasses();
      if ($scope.classes.length === 0) {
        $http.get("api/studyAdministration/classes").then(function (response) {
          $scope.classes = response.data;
          $scope.dropdownTitle = 'Select class';
          classStore.setClasses(response.data);
        });
      };
    };
    //REST-2
    $scope.getClass = function (classId) {
      $scope.showSpinner = true;
      $http.get("api/studyAdministration/pointsForAllStudents/" + classId).then(function (response) {
        $scope.showSpinner = false;
        $scope.selectedClass.points = response.data;
      },
      function err(res){
        $scope.showSpinner = false;
        restErrorHandler.handleErrors(res.data, res.status, $scope);
      });
    };
    $scope.getStudent = function (classId, studentId) {
      $http.get("api/studyAdministration/studentStudypoint/" + classId + "/" + studentId).then(function (response) {
        console.log(response);
        $scope.selectedStudent.data = response.data;
        $scope.studentTitle = response.data.user + ', ' + response.data.studentName;
      });
    };
    $scope.dropdownTitle = 'Loading classes...';
    //initial loading classes list
    $scope.getClasses();

    $scope.showSpinner = false;
    $scope.predicate = "name";
    $scope.reverse = false;
    $scope.order = function (pred) {
      $scope.predicate = pred;
      $scope.reverse = !$scope.reverse;
    };

    $scope.selectClass = function (classObj) {
      if (classObj) {
        $scope.selectedClass = classObj;
        $scope.dropdownTitle = classObj.nameShort;
        $scope.studentTitle = '';
        $scope.periodTitle = '';
        $scope.getClass(classObj._id);
        $scope.selectedStudent = null;
        $scope.selectedPeriod = null;
      }
      else {
        $scope.selectedClass = null;
        $scope.dropdownTitle = 'Select class';
        $scope.studentTitle = '';
        $scope.periodTitle = '';
        $scope.selectedStudent = null;
        $scope.selectedPeriod = null;
      }
    };
    $scope.type = 'warning';
    $scope.getType = function (score) {
     //if (score < $scope.selectedClass.points.maxPointForSemester) {
     if (score < ($scope.selectedClass.points.maxPointForSemester * $scope.selectedClass.points.requiredPoints / 100)) {
        $scope.type = 'warning';
        return "warning";
      } else {
        $scope.type = 'success';
        return "success";
      }
    };

    $scope.selectStudent = function (studentObj) {
      if (studentObj) {
        $scope.selectedID = studentObj.id;
        $scope.selectedStudent = studentObj;
        $scope.periodTitle = '';
        $scope.getStudent($scope.selectedClass._id, studentObj.id);
        $scope.selectedPeriod = null;
      }
      else {
        $scope.selectedStudent = null;
        $scope.selectedPeriod = null;
        $scope.periodTitle = '';
      }
    };
    $scope.selectPeriod = function (period) {
      $scope.selectedPeriod = period;
      $scope.periodTitle = $scope.selectedPeriod.periodName + ' (' + $scope.selectedPeriod.periodDescription + ')';
    };
  }]);
