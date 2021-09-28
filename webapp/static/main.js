/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
class Main
{
  static constructor() {
    Main.isDone = false;
    Main.startModule(runModule);
  }

  static startModule(runModule) {
    if (Main.isDone) return;
    fetch("/startModule/" + runModule, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(uniqueId)
    })
    .then((response) => {
      if (response.status == 200) {
        Main.handleCommand(response);
      }
      else {
        setTimeout(Main.nextCommand, 50);
      }
    })
    .catch((error) => {
      Main.handleError(error);
      setTimeout(Main.nextCommand, 50);
    });
  }

  static nextCommand() {
    if (Main.isDone) return;
    fetch("/nextCommand", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(uniqueId)
    })
    .then((response) => {
      if (response.status == 200) {
        Main.handleCommand(response);
      }
      else {
        setTimeout(Main.nextCommand, 50);
      }
    })
    .catch((error) => {
      Main.handleError(error);
      setTimeout(Main.nextCommand, 50);
    });
  }

  static replyMessage(message) {
    if (Main.isDone) return;
    fetch("/replyMessage", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(message)
    })
    .then((response) => {
      setTimeout(Main.nextCommand, 50);
    })
    .catch((error) => {
      Main.handleError(error);
      setTimeout(Main.nextCommand, 50);
    });
  }

  static updateStatus(finch, status) {
    if (Main.isDone) return;
    fetch("/updateStatus", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(status)
    })
    .then((response) => {
      setTimeout(() => finch.getStatus(), 50);
    })
    .catch((error) => {
      Main.handleError(error);
      setTimeout(() => finch.getStatus(), 50);
    });
  }

  static handleCommand(response) {
    response.json()
    .then((data) => {
      if (data != null) {
        let command = new Command(data);
        console.log(command);
        switch (command.type) {
          case "done": Main.handleDone(command); break;
          case "input": Main.handleInput(command); break;
          case "print": Main.handlePrint(command); break;
          case "debug": Main.handleDebug(command); break;
          case "init": Main.handleInit(command); break;
          case "stop": Main.handleStop(command); break;
          case "forward": Main.handleForward(command); break;
          case "right": Main.handleRight(command); break;
          case "motors": Main.handleMotors(command); break;
          case "lights": Main.handleLights(command); break;
          case "flash": Main.handleFlash(command); break;
          case "leds": Main.handleLeds(command); break;
          case "sound": Main.handleSound(command); break;
          case "reset": Main.handleReset(command); break;
          case "calibrate": Main.handleCalibrate(command); break;
          default: Main.handleUnknown(command);
        }
      }
    })
    .catch ((error) => {
      setTimeout(Main.nextCommand, 50);
      Main.handleError(error);
    });
  }

  static handleError(error) {
    console.error(error);
    $("#printFinch").innerHTML += "<br>ERROR: " + error.message; 
  }

  static handleDone(command) {
    Main.isDone = true;
    $("#printFinch").innerHTML += "<br>DONE";
    Device.disconnectAll();
  }

  static handleInput(command) {
    let input = prompt(command.params.prompt) ?? "";
    let response = {
      input: input
    };
    let message = new Message(command.id, response);
    Main.replyMessage(message);
  }

  static handlePrint(command) {
    $("#printFinch").innerHTML += "<br>" + command.params.message;
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleDebug(command) {
    console.log(command.params.message);
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleInit(command) {
    $("#connectFinch").setAttribute("command", command.id);
    $("#connectFinch").setAttribute("target", command.target);
    $("#connectFinch").setAttribute("initX", command.params.initX);
    $("#connectFinch").setAttribute("initY", command.params.initY);
    $("#connectFinch").innerText = command.params.prompt;
    $("#connectFinch").style.display = "inline";
  }

  static handleStop(command) {
    Canvas.drawMotors(command.target, 0, 0);
    Canvas.drawLight(command.target, 0, [0,0,0]);
    Canvas.drawLight(command.target, 1, [0,0,0]);
    Canvas.drawLight(command.target, 2, [0,0,0]);
    Canvas.drawLight(command.target, 3, [0,0,0]);
    Canvas.drawLight(command.target, 4, [0,0,0]);
    Canvas.drawLetter(command.target, "");

    let robot = Robot.$(command.target);
    robot.motorCount++;
    robot.flashCount++;
    robot.soundCount++;

    let bytes = command.params.bytes;
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));

    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleForward(command) {
    let distance = command.params.distance;
    let speed = command.params.speed;
    Canvas.drawForward(command.target, distance, speed);

    let bytes = command.params.bytes;
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));

    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleRight(command) {
    let angle = command.params.angle;
    let speed = command.params.speed;
    Canvas.drawRight(command.target, angle, speed);

    let bytes = command.params.bytes;
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));
    
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleMotors(command) {
    let left = command.params.left;
    let right = command.params.right;
    Canvas.drawMotors(command.target, left, right);
    
    let bytes = command.params.bytes;
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));

    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleLights(command) {
    let robot = Robot.$(command.target);
    Canvas.drawLight(command.target, 0, command.params.beak);
    Canvas.drawLight(command.target, 1, command.params.tail1);
    Canvas.drawLight(command.target, 2, command.params.tail2);
    Canvas.drawLight(command.target, 3, command.params.tail3);
    Canvas.drawLight(command.target, 4, command.params.tail4);

    let bytes = command.params.bytes;
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));
    
    robot.finchLights = bytes;
    robot.finchLights.splice(16, 4);
    
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleFlash(command) {
    let phrase = command.params.phrase;
    let bytesList = command.params.bytes;

    let robot = Robot.$(command.target);
    robot.flashCount++;

    let finch = Device.$(command.target);
    Main.flashLetter(command.target, finch, phrase, bytesList, robot.flashCount);
    
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static flashLetter(target, finch, phrase, bytesList, index) {
    if (bytesList.length == 0) return;
    
    let robot = Robot.$(target);
    if (robot.flashCount != index) return;

    let letter = phrase.substring(0,1);
    let bytes = bytesList.shift();
    Canvas.drawLetter(target, letter);
    
    finch.sendCommand(Uint8Array.from(bytes));

    setTimeout(() => {
      Main.flashLetter(target, finch, phrase.substring(1), bytesList, index);
    }, 750);
  }

  static handleLeds(command) {
    let robot = Robot.$(command.target);
    robot.flashCount++;

    Canvas.drawLEDs(command.target, Main.cleanByteArray(command.params.ledGrid));

    let bytes = Main.cleanByteArray(command.params.bytes);
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));
    
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleSound(command) {
    let music = command.params.music;
    let bytesList = command.params.bytes;

    let robot = Robot.$(command.target);
    robot.soundCount++;

    let finch = Device.$(command.target);
    Main.playSound(command.target, finch, music, bytesList, robot.soundCount);
    
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static playSound(target, finch, music, bytesList, index) {
    if (bytesList.length == 0) return;
    
    let robot = Robot.$(target);
    if (robot.soundCount != index) return;

    let sound = music.shift();
    let bytes = bytesList.shift();
    
    if (finch.version == 0 && sound.midiNote > 0) {
      let frequency = 440 * 2**((sound.midiNote - 69) / 12);
      soundEffect(frequency, 0, sound.duration / 1000, "square", 1,
          0, 0, 0, false, 0, 0, null, null, sound.duration / 1000);
    }
    
    if (robot.finchLights) {
      bytes.splice(0, 16);
      bytes = robot.finchLights.concat(bytes);
    }
    finch.sendCommand(Uint8Array.from(bytes));

    setTimeout(() => {
      Main.playSound(target, finch, music, bytesList, index);
    }, sound.duration);
  }

  static handleReset(command) {
    let robot = Robot.$(command.target);
    robot.leftTicks = 0;
    robot.rightTicks = 0;
    
    let bytes = command.params.bytes;
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));
    
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleCalibrate(command) {  
    let bytes = command.params.bytes;
    let finch = Device.$(command.target);
    finch.sendCommand(Uint8Array.from(bytes));
    
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static handleUnknown(command) {
    console.warn(command);
    let message = new Message(command.id, null);
    Main.replyMessage(message);
  }

  static cleanByteArray(bytes) {
    for (let index = 0; index < bytes.length; index++) {
      if (bytes[index] == true) bytes[index] = 1;
      else if (bytes[index] == false) bytes[index] = 0;
    }
    return bytes;
  }
}
Main.constructor();

async function onConnectFinch() {
  $("#connectFinch").style.display = "none";
  let target = parseInt($("#connectFinch").getAttribute("target"));
  let initX = parseFloat($("#connectFinch").getAttribute("initX"));
  let initY = parseFloat($("#connectFinch").getAttribute("initY"));

  let robot = Robot.$(target);
  robot.xCurrent = initX;
  robot.yCurrent = initY;
  Canvas.changeFinchTarget(target);
  let option = new Option("Finch " + target, target);
  $("#selectFinch").options.add(option);
  $("#selectFinch").value = target;

  importScript("https://cdn.jsdelivr.net/npm/sound.js@1.0.1/sound.min.js")

  let finch = Device.$(target);
  finch = await finch.connect();
  console.log(finch);

  let commandId = $("#connectFinch").getAttribute("command");
  let response = {
    device: finch.device.name,
    name: finch.name,
    version: finch.version
  };
  let message = new Message(commandId, response);
  Main.replyMessage(message);

  finch.getStatus();
}
