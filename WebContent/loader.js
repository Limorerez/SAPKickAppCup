loader = {
    load: function(src, id, libs, theme, callback) {
        setTimeout(this.loadSAPUI5(src, id, libs, theme, callback));
    },
    
    loadSAPUI5: function(src, id, libs, theme, callback) {
        var s, r, t;
        r = false;
        s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = src;
        s.id = id;
        s.setAttribute("data-sap-ui-libs", libs);
        s.setAttribute("data-sap-ui-theme", theme);
        s.setAttribute("data-sap-ui-rtl", 'false');

        s.onload = s.onreadystatechange = function () {
            if (!r && (!this.readyState || this.readyState == 'complete')) {
                r = true;
                callback();
            }

        };
        t = document.getElementsByTagName('script')[0];
        t.parentElement.insertBefore(s, t);
    },

    onSAPUI5Loaded: function() {
        $("#content").empty();
        $("#content").removeAttr('style');
        fragments.createFragments();
        loader.loadStyleCSS();
        loader.appInit();
    },

    loadStyleCSS: function() {
        $('head').append('<link type="text/css" rel="stylesheet" href="style/style.css"/>');
    },
    
    appInit: function() {
        sap.ui.localResources('view');
        jQuery.sap.require('sap.m.MessageBox');

        function isAudioContextAvailable() {
            return (typeof window.AudioContext !== 'undefined') || (typeof window.webkitAudioContext !== 'undefined');
        }

        window.alsApp = new sap.m.App('alsApp',{
            initialPage: 'weeklyTasksPage',
            navigate: function(event) {
                var targetPage = event.getParameter('to');
                if (targetPage.sId === 'weeklyTasksPage') {
                    targetPage.getController().updateTasksList();
                }
            }
        });

        alsApp.busyDialog = new sap.m.BusyDialog();

        alsApp.config = {
            APP_NAME: 'My Healthcare',
            USER_TYPE: {
            	PATIENT: 'PATIENT',
            	CONTROL: 'CONTROL'
            },

            SERVER_URL: '/SAPKickAppCup/api',
            //SERVER_URL: 'http://localhost:8080/als/api',
            //SERVER_URL: 'https://alsprize4life.hana.ondemand.com/als/api',
            
            email: localStorage.getItem('alsEmail'),
            pages: {
                loginPage: {viewName: 'view.login', type: sap.ui.core.mvc.ViewType.JS},
                registerPage: {viewName: 'view.register', type: sap.ui.core.mvc.ViewType.JS},
                weeklyTasksPage: {viewName: 'view.weeklyTasks', type: sap.ui.core.mvc.ViewType.JS},
                questionsPage: {viewName: 'view.questions', type: sap.ui.core.mvc.ViewType.JS},
                writingPage: {viewName: 'view.handwriting', type: sap.ui.core.mvc.ViewType.JS},
                speechPage: {viewName: 'view.speechMp3', type: sap.ui.core.mvc.ViewType.JS, condition: isAudioContextAvailable},
                completedPage: {viewName: 'view.completed', type: sap.ui.core.mvc.ViewType.JS}
            },
            
            isDemoMode: false
        };

        alsApp.createPage = function(pageId) {
            var page = alsApp.config.pages[pageId];
            if (!page.condition || page.condition()) {
                alsApp.addPage(sap.ui.view({
                    id: pageId,
                    viewName: page.viewName,
                    type: page.type
                }));
                return true;
            }
            return false;
        };

        
        alsApp.goToPage = function(pageId) {
            var navigated = true;
            if (!alsApp.getPage(pageId)) {
                alsApp.busyDialog.open();
                navigated = alsApp.createPage(pageId);
                alsApp.busyDialog.close();
            }
            alsApp.to(pageId);
            return navigated;
        };

                        
        alsApp.setEmail = function(email) {
            alsApp.config.email = email;
            localStorage.setItem('alsEmail', email);
            alsApp.getPage('loginPage').setEmail(email);

            var registerPage = alsApp.getPage('registerPage');
            if (registerPage) {
                registerPage.setEmail(email);
            }
        };

        alsApp.resetPage = function(pageId) {
            alsApp.getPage(pageId).destroy();
            alsApp.createPage(pageId);
        };
        
        alsApp.createPage(alsApp.getInitialPage());
        
        alsApp.placeAt('content');
    }
};
