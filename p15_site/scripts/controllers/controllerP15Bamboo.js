'use strict';

/*
 * Encoding string "Basic user:pwd" in base64
 * @returns {@exp;window@call;btoa}
 */
function base64_encodedUser(){
    return window.btoa(unescape(encodeURIComponent('p15:p15')));
};

var URLroot ='https://appverse.gftlabs.com/builder/rest/api/1.0';


angular.module('TMDashboard-Main')
.controller('BambooP15RestController', ['$scope', '$http', function (scope, http) {
    scope.bamboodata = {
        jobData: '',
        status: '',
        newProjectName: 'defaultTitle', //title project
        archetypeVersion: '1.2.5-RELEASE', 
        newProjectGroupID: 'org.appverse',
        newProjectArtifactID: 'defaultName', //nameProject
        newProjectVersion: '0.1-SNAPSHOT',
        newProjectOwnerEmail: '', // mail form
        buildKeyData:''
        
    };
    
    /*
     * Common HTTP Headers definition.
     */
    http.defaults.headers.common = {'Access-Control-Request-Headers': 'accept, origin, authorization', 'Accept':'application/json', 'Content-Type':'application/json;charset=UTF-8'};
    http.defaults.headers.common['Authorization'] = 'Basic ' + base64_encodedUser();
    
    /*
     * Call GET to get info of a projectkey in Bamboo.
     * Authorization not required.
     * Access-Control-Request-Headers does not require authorization.
     * Response sent to jobData as usual.
     */
    scope.getBuildData = function () {

        return  http({
                    method: 'GET', 
                    url: URLroot + '/plan/' + scope.bamboodata.buildKeyData
                    
                }).
                success(function(data, status, headers, config) {
                    scope.bamboodata.jobData = data;
                    scope.bamboodata.status = status;
                }).
                error(function(data, status, headers, config) {
                    scope.bamboodata.jobData = data;
                    scope.bamboodata.status = status;
                });
    },
    /*
     * Call POST to run a job in Bamboo.
     * Authorization required.
     * Access-Control-Request-Headers requires authorization.
     * Response sent to jobData as usual.
     */
    
    
    scope.runJob = function () {
       
       /*
        * Parameters for the job in Bamboo.
        * 1-Maven Parameters:
            newProjectName 	MyProject                       ${bamboo.newProjectName}
            archetypeVersion 	1.1.0-RELEASE                   ${bamboo.archetypeVersion}
            groupId 	        org.organization.project	${bamboo.newProjectGroupID}
            artifactId 	        project                         ${bamboo.newProjectArtifactID}
            version 	        1.0-SNAPSHOT                    ${bamboo.newProjectVersion}
        * 2-Configuration Parameters:
            ${bamboo.newProjectOwnerEmail} : E-mail to send notifications

        */
       
        var params = {
            'bamboo.newProjectName': scope.bamboodata.newProjectName,
            'bamboo.archetypeVersion': scope.bamboodata.archetypeVersion,
            'bamboo.newProjectGroupID': scope.bamboodata.newProjectGroupID,
            'bamboo.newProjectArtifactID': scope.bamboodata.newProjectArtifactID,
            'bamboo.newProjectVersion': scope.bamboodata.newProjectVersion,
            'bamboo.newProjectOwnerEmail': scope.bamboodata.newProjectOwnerEmail
            
                    
        };
        console.log('Hidden' + $('#buildKeyDataHidden').val());
        console.log('Scope' + scope.bamboodata.buildKeyData);
        scope.bamboodata.buildKeyData = $('#buildKeyDataHidden').val();

        return  http({
                    method: 'POST', 
                    url: URLroot + '/queue/' + scope.bamboodata.buildKeyData,
                    params: params
                    
                }).
                success(function(data, status, headers, config) {
                    scope.bamboodata.jobData = data;
                    scope.bamboodata.status = status;
                }).
                error(function(data, status, headers, config) {
                    scope.bamboodata.jobData = data;
                    scope.bamboodata.status = status;
                });
    }

$(function() {

    /************
     CONFIG
     ************/
    var config = {
        defaultModules: {
            gwt: [
                'css-mode',
                'h5bp-content',
                'modernizr',
                'jquerymin'

            ],
            jsf: [
                'css-mode',
                'izr-responsive',
                'modernizr',
                'respond',
                'jquerymin'

            ],
            html5: [
                'boot-hero',
                'modernizr',
                'respond',
                'jquerymin'

            ]
        },
        defaultStyles: {
            button: [
                '#preconfig-blank',
                '#preconfig-initializr',
                '#preconfig-bootstrap'
            ],
            default: [
                'blank-background',
                'initializr-background',
                'bootstrap-background'
            ],
            noDefault: [
                'blank-no-background',
                'initializr-no-background',
                'bootstrap-no-background'
            ],
            gwt: [
                'blank-background',
                'initializr-no-background',
                'bootstrap-no-background'
            ],
            jsf: [
                'blank-no-background',
                'initializr-background',
                'bootstrap-no-background'
            ],
            html5: [
                'blank-no-background',
                'initializr-no-background',
                'bootstrap-background'
            ]
        },
        baseUrl: 'builder?'
    };



    /************
     VARIABLES
     ************/

    var params;
    var modules = [];
    var stylelang = '';

    /**********
     EVENTS
     **********/

    $('input').click(function() {
        update();
    });


    $('#preconfig-blank').click(function() {
        $('#customize-section').removeClass('jsfBack');
        $('#customize-section').addClass('gwtBack');
        fillDefaultModules('gwt');
        _gaq.push(['_trackEvent', 'preconfig', 'h5bp-classic']);
        manageStyles('gwt');
        $('#buildKeyDataHidden').val('P15-GWTGEN');

    });


    $('#preconfig-initializr').click(function() {
        $('#customize-section').removeClass('gwtBack');
        $('#customize-section').addClass('jsfBack');
        fillDefaultModules('jsf');
        _gaq.push(['_trackEvent', 'preconfig', 'responsive']);
        manageStyles('jsf');
        $('#buildKeyDataHidden').val('P15-GS');
    });

    $('#preconfig-bootstrap').click(function() {
        $('#hidden-section-cooming-soon').fadeIn('slow');
        $('#hidden-section').fadeOut('slow');
        $('#hidden-section-2').fadeOut('slow');
        manageStyles('html5');
    });

    $('#download').click(function() {
        $('#download>h3').html('Congratulations! In a few seconds you will receive an email with the link ;)');
        $('#download').addClass('greenBack');
        $('#download').attr("disabled", "disabled");
    });

    $('#download-link').click(function() {
        _gaq.push(['_trackEvent', 'action', 'download']);
    });

    $('#preview-link').click(function() {
        _gaq.push(['_trackEvent', 'action', 'preview']);
    });

    $('#ready').click(function() {
        $('#hidden-section-2').fadeIn('slow');
    });

    /*********
     LOGIC
     *********/

    function manageStyles(type) {
        //console.log(type);
        for (var i = 0; i < 3; i++) {
            var curStyle = config.defaultStyles[type][i];
            var buttonName = config.defaultStyles['button'][i];
            removeButtonClass(buttonName, config.defaultStyles['default'][i]);
            removeButtonClass(buttonName, config.defaultStyles['noDefault'][i]);
            addButtonClass(buttonName, curStyle);
            //console.log(buttonName + " --> "+ curStyle);
        }
        ;
    }

    function removeButtonClass(sel, styleName) {
        $(sel).removeClass(styleName);
        $(sel).parent().removeClass(styleName);
    }

    function addButtonClass(sel, styleName) {
        $(sel).addClass(styleName);
        $(sel).parent().addClass(styleName);
    }

    function fillDefaultModules(type) {

        $('#download>h3').html('download!');
        $('#download').removeClass('greenBack');
        $('#download').removeAttr("disabled");

        $('input').attr('checked', false);

        for (var i = 0, curModule; curModule = config.defaultModules[type][i++]; ) {
            $('input[value=' + curModule + ']').attr('checked', true);
        }
        ;
        update();
        $('#hidden-section-cooming-soon').fadeOut('slow');
        $('#hidden-section').fadeIn('slow');
        $('#hidden-section-2').fadeOut('slow');
    }

    function update() {
        updateModules();
        updateUrls();
    }

    function updateModules() {
        modules = [];
        $('input').each(function() {
            if ($(this).is(':checked'))
                modules.push($(this).val());
        });
        replaceSpecialModules();
    }

    function replaceSpecialModules() {
        if (modules.indexOf('jquerymin') != -1 && modules.indexOf('jquerydev') != -1) {
            modules.remove('jquerymin');
            modules.remove('jquerydev');
            modules.push('jquery');
        }

        if (modules.indexOf('modernizr') != -1 && modules.indexOf('respond') != -1) {
            modules.remove('modernizr');
            modules.remove('respond');
            modules.push('modernizrrespond');
        }

        if (modules.indexOf('html5shiv') != -1 && modules.indexOf('respond') != -1) {
            modules.remove('html5shiv');
            modules.remove('respond');
            modules.push('html5shivrespond');
        }

        if (modules.indexOf('less-mode') != -1) {
            modules.remove('less-mode');
            stylelang = 'less';
        }
        else
            stylelang = '';

        if (modules.indexOf('css-mode') != -1) {
            modules.remove('css-mode');
        }

        if (modules.indexOf('h5bp-content') != -1 || modules.indexOf('izr-responsive') != -1) {
            modules.push('h5bp-css');
            modules.push('h5bp-csshelpers');
            modules.push('h5bp-mediaqueryprint');
        }

        if (modules.indexOf('h5bp-content') != -1) {
            modules.push('h5bp-mediaqueries');
        }

        if (modules.indexOf('h5bp-iecond') == -1) {
            modules.push('simplehtmltag');
        }

        if (modules.indexOf('h5bp-scripts') == -1) {
            modules.push('izr-emptyscript');
        }

        if (modules.indexOf('boot-hero') != -1) {
            modules.push('boot-css');
            modules.push('boot-scripts');
            if (modules.indexOf('jquery') == -1 && modules.indexOf('jquerydev') == -1 && modules.indexOf('jquerymin') == -1) {
                modules.push('jquerymin');
            }
        }


    }

    function updateUrls() {
        var modeParam = '';

        if (stylelang != '') {
            modeParam = 'mode=' + stylelang + '&';
        }

        params = '';

        for (var i = 0, curModule; curModule = modules[i++]; ) {
            params += curModule + '&';
        }

        params = params.substring(0, params.length - 1);

        $('#preview-url').val(config.baseUrl + 'print&' + modeParam + params);
        $('#download-url').val(config.baseUrl + modeParam + params);

        $('#preview-link').attr('href', config.baseUrl + 'print&' + modeParam + params);
        $('#download-link').attr('href', config.baseUrl + modeParam + params);
    }

    /***********
     HELPERS
     ***********/

    if (!Array.indexOf) {
        Array.prototype.indexOf = function(searchedElement) {
            for (var i = 0; i < this.length; i++) {
                if (this[i] === searchedElement)
                    return i;
            }
            ;
            return -1;
        };
    }

    Array.prototype.remove = function(searchedElement) {
        var i = this.indexOf(searchedElement);
        if (i != -1)
            this.splice(i, 1);
    };

    /***********
     MAIN
     ***********/

    if ($('input:checked').length > 0)
        $('#hidden-section').fadeIn(0);
    update();


});


}]);





