/*
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

angular.module('dev', [ 'ngResource']);

/**
 * App management controller.
 * @param $scope
 * @param $resource
 * @param $http
 * @param $timeout
 */
function StorageController($scope, $resource, $http) {
	// storage
	$scope.storage = null;
	// token
	$scope.token = null;
	// appId
	$scope.appId = 'overview';
	// error message
	$scope.error = '';
	// info message
	$scope.info = '';
	
	// curr parameter name
	$scope.pname = null;
	// curr parameter value
	$scope.pvalue = null;
	
	
	$scope.fetchStorage = function() {
		$http(
				{method:'GET',
				 url:'../storage/app/'+$scope.appId,
				 params:{},
				 headers:{Authorization:'Bearer '+$scope.token}
				})
		.success(function(data) {
			$scope.storage = data;
			$scope.info = '';
			$scope.error = '';
		}).error(function(data) {
			$scope.info = '';
			$scope.error = "problem fetching the corresponding configuration";
		});
	};

	$scope.saveStorage = function() {
		var method = 'POST';
		if ($scope.storage && $scope.storage.id) {
			method = 'PUT';
		}
		$scope.storage.appId = $scope.appId
		$http(
				{method:method,
				 url:'../storage/app/'+$scope.appId,
				 params:{},
				 data : $scope.storage,
				 headers:{Authorization:'Bearer '+$scope.token}
				})
		.success(function(data) {
			$scope.storage = data;
			$scope.info = 'Storage saved!';
			$scope.error = '';
		}).error(function(data) {
			$scope.info = '';
			$scope.error = "problem storing the configuration";
		});
	};
	
	$scope.addConfig = function() {
		var name = $scope.pname;
		var value = $scope.pvalue;
		if (!name) {
			$scope.error = 'Parameter name cannot be empty!';
			return;
		} else {
			$scope.error = '';
		}
		$scope.info = '';
		if (!$scope.storage) {
			$scope.storage = {};
		}
		if (!$scope.storage.configurations) {
			$scope.storage.configurations = [];
		}
		for (var i = 0; i < $scope.storage.configurations.length; i++) {
			var c = $scope.storage.configurations[i];
			if (c.name == name) {
				c.value = value;
				$scope.pname = null;$scope.pvalue = null;
				return;
			}
		}
		$scope.storage.configurations.push({name:name,value:value});
		$scope.pname = null;$scope.pvalue = null;
	};

	$scope.removeConfig = function(name) {
		for (var i = 0; i < $scope.storage.configurations.length; i++) {
			var c = $scope.storage.configurations[i];
			if (c.name == name) {
				$scope.storage.configurations.splice(i,1);
				return;
			}
		}
	};

}