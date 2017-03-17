Selenium.prototype.runningVars = {};
Selenium.prototype.runningVars.nextCommands = new Array();
Selenium.prototype.runningVars.lastCommands = new Array();
Selenium.prototype.doOxOriginalOpen = Selenium.prototype.doOpen;
Selenium.prototype.doOxOriginalClick = Selenium.prototype.doClick;
Selenium.prototype.doOxOriginalType = Selenium.prototype.doType;

Selenium.prototype.doOxOpen = function(application, module) {
	var locator = "/" + application + "/modules/" + module;
	storedVars['application'] = application;
	storedVars['module'] = module;
	selenium.runningVars.nextCommands.unshift(new SeleniumCommand("oxOriginalOpen", locator, ""));
}

/*
 * Performs the click sequence in openxava buttons.
 */
Selenium.prototype.doOxClick = function(actionName) {
	locator =  selenium.oxDecorateId(actionName);
    selenium.runningVars.nextCommands.unshift(new SeleniumCommand("oxOriginalClick", locator, ""));
}

/*
 * Performs the type sequence in openxava buttons.
 */
Selenium.prototype.doOxType = function(property, value) {
	locator = selenium.oxDecorateId(property);
    selenium.runningVars.nextCommands.unshift(new SeleniumCommand("oxOriginalType", locator, value));
}


/*
 * SUPPORT functions for OpenXava
 */
Selenium.prototype.oxDecorateId = function(simpleName) {
	return "ox_" + storedVars['application'] + "_" + storedVars['module'] + "__" + simpleName.replace(/\./g, "___"); 
}