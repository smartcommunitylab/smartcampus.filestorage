<!DOCTYPE html>
<html lang="en" ng-app="dev">
  <head>
    <meta charset="utf-8">
    <title>SmartCampus Developers</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="../css/bootstrap.min.css" rel="stylesheet">
    <link href="../css/bs-ext.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
      .sidebar-nav {
        padding: 9px 0;
      }
    </style>
    <link href="../css/bootstrap-responsive.min.css" rel="stylesheet">

    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular-resource.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular-cookies.min.js"></script>
    <script src="../lib/jquery.js"></script>
    <script src="../lib/bootstrap.min.js"></script>
    <script src="../js/services.js"></script>
  </head>

  <body>
    <div class="container" ng-controller="StorageController">
		    <div class="alert alert-error" ng-show="error != ''">{{error}}</div>
		    <div class="alert alert-success" ng-show="info != ''">{{info}}</div>
        <form ng-submit="fetchStorage()">
          <fieldset>    
              <legend>Storage configuration</legend>
               <label>Client token (generate it through the Developer Console)<label>
               <input type="text" ng-model="token" placeholder="Client credentials flow token" required class="input-xxlarge">

               <label>App ID parameter<label>
               <input type="text" ng-model="appId" placeholder="appId" required class="input-xlarge">
               <div class="row-fluid" >
                 <button type="submit" class="btn btn-primary">Fetch</button>
               </div>
           </fieldset>
        </form>
    
        <form ng-submit="saveStorage()">
            <fieldset>    
               <label>Storage name (optional)<label>
               <input ng-model="storage.name" type="text" placeholder="name" class="input-xlarge">
			
               <label>Storage type<label>
       				<select ng-model="storage.storageType" class="input-medium">
                     <option value="DROPBOX">DropBox</option>
               </select>

		         	<label>Redirect URI (optional, required in case of Web app usage)<label>
               <input type="text" ng-model="storage.redirect" placeholder="redirect" class="input-xlarge">

               <label>Add configuration parameter</label>
 							<div class="row-fluid">
						    <div class="span7">
							    <input type="text" ng-model="pname" placeholder="parameter key" class="input-xlarge">
							  	<input type="text" ng-model="pvalue" placeholder="parameter value" class="input-xlarge">
							  </div>	
							  <div class="span1"><a class="btn btn-mini btn-primary" href="#" ng-click="addConfig()"><i class="icon-plus icon-white"></i></a></div>
						  </div>
					  	<hr class="hr-min"/>
						  <div ng-repeat="config in storage.configurations">
							  <div class="row-fluid" >
								 <div class="span3">{{config.name}}</div>
								 <div class="span4">{{config.value}}</div>
								 <div class="span1"><a class="btn btn-mini" href="#" ng-click="removeConfig(config.name)"><i class="icon-minus"></i></a></div>
							  </div>
						  </div>	
						  <hr class="hr-min"/>
						  <button type="submit" class="btn btn-primary">Save configuration</button>
           </fieldset>
        </form>
    </div>
  </body>
</html>
