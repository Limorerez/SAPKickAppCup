fragments = {
    createFragments: function() {
        sap.ui.jsfragment('HeaderToolbar', {
            createContent: function(params) {
                var homeButton = new sap.m.Image(this.createId('headerToolbarHomeButton'), {
                    src: 'images/homeWhite.png',
                    densityAware: false,
                    height: '2em',
                    visible: params.showHomeButton,
                    press: function() {
                        alsApp.backToPage('weeklyTasksPage');
                    }
                }).addStyleClass('homeButton');

                var title = new sap.m.Text(this.createId('headerToolbarTitle'), {
                        text: params.title}
                ).addStyleClass('headerTitle');

                var user = localStorage.getItem('alsFirstName');
                if (user === "") {
                    user = localStorage.getItem('alsEmail').substring(0, localStorage.getItem('alsEmail').indexOf('@'));
                }

                var firstNameText = new sap.m.Label(this.createId('headerToolbarFirstName'), {
                    text: 'Hi ' + user,
                    visible: params.showLoginInfo || false
                }).addStyleClass('headerFirstName');

                var logoutIcon = new sap.ui.core.Icon(this.createId('headerLogoutIcon'), {
                    src: "sap-icon://log",
                    color: 'white',
                    visible: params.showLoginInfo || false,
                    press: function() {
                        $.ajax({
                            type: 'GET',
                            url: alsApp.config.SERVER_URL + '/auth/logout',
                            success: function(result) {
                            	localStorage.setItem('alsSessionExist',false);
                                alsApp.goToPage('loginPage');
                            }
                        })
                    }
                }).addStyleClass('headerLogoutIcon');

                var toolbar = new sap.m.Toolbar(this.createId('headerToolbar'), {
                    content: [homeButton, params.showLoginInfo ? null : new sap.m.ToolbarSpacer(),
                        title, new sap.m.ToolbarSpacer(), firstNameText, logoutIcon]
                }).addStyleClass('header');

                return toolbar;
            }
        });

        sap.ui.jsfragment('FooterToolbar', {
            createContent: function(params) {
                var nav = params.nav;
                var pagesNum = nav.getPages().length;

                var nextButton = new sap.m.Button(this.createId('footerNextButton'), {
                    iconDensityAware: false,
                    width: params.actionCallback ? '33%' : '50%',
                    icon: (pagesNum === 1) ? 'images/finishTask.png' : 'images/next.png',
                    press: function() {
                        var currentPage = nav.getCurrentPage();
                        var nextPageIndex = nav.indexOfPage(currentPage) + 1;
                        var pages = nav.getPages();

                        if (nextPageIndex === pages.length) {
                            if (params.finishCallback) {
                                params.finishCallback();
                            }
                            return;
                        }
                        if (nextPageIndex === pages.length - 1) {
                            nextButton.setIcon('images/finishTask.png').addStyleClass('finishButton');
                        }

                        prevButton.setEnabled(true);
                        if (params.nextCallback) {
                            params.nextCallback();
                        }
                        nav.to(nav.getPages()[nextPageIndex]);
                    }
                }).addStyleClass('footerButton footerNextButton');
                if (pagesNum === 1) {
                    nextButton.addStyleClass('finishButton');
                }

                var prevButton = new sap.m.Button(this.createId('footerPrevButton'), {
                    icon: 'images/previous.png',
                    enabled: false,
                    iconDensityAware: false,
                    width: params.actionCallback ? '33%' : '50%',
                    press: function() {
                        var currentPage = nav.getCurrentPage();
                        var prevPageIndex = nav.indexOfPage(currentPage) - 1;
                        var pages = nav.getPages();

                        if (prevPageIndex === 0) {
                            prevButton.setEnabled(false);
                        }
                        if (prevPageIndex === pages.length - 2) {
                            nextButton.setIcon("images/next.png").removeStyleClass('finishButton');
                        }
                        if (params.prevCallback) {
                            params.prevCallback();
                        }
                        nav.back();
                    }
                }).addStyleClass('footerButton footerPrevButton');

                var actionButton;
                if (params.actionCallback) {
                    actionButton = new sap.m.Button(this.createId('footerActionButton'), {
                        text: params.actionName,
                        width: '34%',
                        press: params.actionCallback
                    }).addStyleClass('footerButton footerClearButton');
                }

                var toolbar = new sap.m.Toolbar(this.createId('footerToolbar'), {
                    content: [prevButton, actionButton, nextButton]
                }).addStyleClass('footer');
                return toolbar;
            }
        });
    }
};