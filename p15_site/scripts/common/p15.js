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
            jsf:[
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
        fillDefaultModules('gwt');
        _gaq.push(['_trackEvent', 'preconfig', 'h5bp-classic']);
        manageStyles('gwt');
        $('#buildKeyDataHidden').val('P15-GWTGEN');
    });
    
    
    $('#preconfig-initializr').click(function() {
        fillDefaultModules('jsf');
        _gaq.push(['_trackEvent', 'preconfig', 'responsive']);
        manageStyles('jsf');
        $('#buildKeyDataHidden').val('P15-GS');
    });

    $('#preconfig-bootstrap').click(function() {
        fillDefaultModules('html5');
        _gaq.push(['_trackEvent', 'preconfig', 'bootstrap']);
        manageStyles('html5');
    });
    
    $('#download').click(function(){
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
    
    function manageStyles(type){
        //console.log(type);
        for (var i = 0; i<3; i++ ) {
           var curStyle = config.defaultStyles[type][i];
           var buttonName = config.defaultStyles['button'][i];
           removeButtonClass(buttonName, config.defaultStyles['default'][i]);
           removeButtonClass(buttonName,config.defaultStyles['noDefault'][i]);
           addButtonClass(buttonName, curStyle);
           //console.log(buttonName + " --> "+ curStyle);
        }
        ;
    }
    
    function removeButtonClass(sel, styleName){
        $(sel).removeClass(styleName);
        $(sel).parent().removeClass(styleName);
    }
    
    function addButtonClass(sel, styleName){
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