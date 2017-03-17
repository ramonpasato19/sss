This selenium tests are designed for selenium-ide.

Download the firefox plugin and run the Renderer.suite.
Also install https://addons.mozilla.org/en-US/firefox/addon/85794/ 
which is a flow control plug in REQUIRED by the tests.

For improved testing reliability configure Selenium core extensions.
The core extension files is ox-extensions.js
located in the selenium-tests folder.

Commands implemented in ox-extensions.js:
oxOpen application module. Opens the module in the browser.
oxClick Controller.action. Perform a click on the button specified in Controller.action.
oxType propertyName value. Types the value in the property name.



