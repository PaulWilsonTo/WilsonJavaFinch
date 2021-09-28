/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
let isDebug = false;

function debug(...params) {
  if (isDebug) console.log(params)
}

function $(selector) {
  return document.querySelector(selector);
}

function $$(selector) {
  return document.querySelectorAll(selector);
}

function importScript(jsScript) {
  let script = document.createElement("script");
  script.src = jsScript;
  document.head.appendChild(script);
}

function toRadians(angle) {
  return Math.PI * angle / 180;
}

function toDegrees(angle) {
  return 180 * angle / Math.PI;
}
