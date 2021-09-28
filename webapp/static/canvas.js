/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
class Canvas
{
  static constructor() {
    let canvasFinch = $("#canvasFinch");
    Canvas.canvasWidth = canvasFinch.width;
    Canvas.canvasHeight = canvasFinch.height;

    Canvas.ctxFinch = canvasFinch.getContext("2d");
    Canvas.ctxFinch.translate(Canvas.canvasWidth / 2, 2 * Canvas.canvasHeight / 3);
    Canvas.ctxFinch.scale(1.0, -0.66667);
  }

  static addRobotCanvas(target) {
    let canvasStack = $("#canvasStack");
    let canvasRobot = document.createElement("canvas");
    canvasRobot.id = "canvasRobot" + target;
    canvasRobot.width = Canvas.canvasWidth;
    canvasRobot.height = Canvas.canvasHeight;
    canvasRobot.style.zIndex = target;
    canvasStack.appendChild(canvasRobot);

    let ctxRobot = canvasRobot.getContext("2d");  
    ctxRobot.translate(Canvas.canvasWidth / 2, 2 * Canvas.canvasHeight / 3);
    ctxRobot.scale(1.0, -0.66667);
    return ctxRobot;
  }

  static drawMotors(target, leftSpeed, rightSpeed) {
    let robot = Robot.$(target);
    robot.motorCount++;

    let maxSpeed = Math.max(leftSpeed, rightSpeed);
    let avgSpeed = (leftSpeed + rightSpeed) /2;
    if (avgSpeed == 0) return;

    let turnAngle = toDegrees(Math.atan((maxSpeed - avgSpeed) / avgSpeed));
    let isRight = (Math.abs(leftSpeed) > Math.abs(rightSpeed));
    if (isRight) turnAngle = -turnAngle;

    Canvas._drawArc(robot, turnAngle, leftSpeed, rightSpeed, robot.motorCount);
  }

  static _drawArc(robot, angle, left, right, index) {
    if (robot.motorCount != index) return;

    robot.heading += angle;
    let xMove = Math.cos(toRadians(robot.heading));
    let yMove = Math.sin(toRadians(robot.heading));
    
    let xStart = robot.xCurrent;
    let yStart = robot.yCurrent;
    let xEnd = xStart + xMove;
    let yEnd = yStart + yMove;
    
    Canvas._drawLine(Canvas.ctxFinch, xStart, yStart, xEnd, yEnd, robot.color);
    robot.xCurrent = xEnd;
    robot.yCurrent = yEnd;
    Canvas._drawRobot(robot);

    if (left >= right) {
      robot.leftTicks += 49.7 * Math.abs(1);
      robot.rightTicks += 49.7 * Math.abs(right/left);
    }
    else {
      robot.leftTicks += 49.7 * Math.abs(left/right);
      robot.rightTicks += 49.7 * Math.abs(1);
    }

    setTimeout(() => {
      Canvas._drawArc(robot, angle, left, right, index);
    }, 1000 / Math.max(Math.abs(left), Math.abs(right)));
  }

  static drawForward(target, distance, speed) {
    let robot = Robot.$(target);
    robot.isWorking = true;
    robot.motorCount++;

    let xMove = distance * Math.cos(toRadians(robot.heading));
    let yMove = distance * Math.sin(toRadians(robot.heading));
    let steps = Math.round(Math.max(Math.abs(xMove), Math.abs(yMove)));
    let move = distance / steps;

    Canvas._drawStep(robot, xMove, yMove, steps, 1, speed, move);
  }

  static _drawStep(robot, xMove, yMove, steps, step, speed, move) {
    setTimeout(() => {
      let xStart = robot.xCurrent;
      let yStart = robot.yCurrent;
      let xEnd = xStart + xMove / steps;
      let yEnd = yStart + yMove / steps;

      Canvas._drawLine(Canvas.ctxFinch, xStart, yStart, xEnd, yEnd, robot.color);
      robot.xCurrent = xEnd;
      robot.yCurrent = yEnd;
      Canvas._drawRobot(robot);

      robot.leftTicks += 49.7 * Math.abs(move);
      robot.rightTicks += 49.7 * Math.abs(move);
      if (step < steps) Canvas._drawStep(robot, xMove, yMove, steps, step + 1, speed, move);
      else robot.isWorking = false;
    }, 500 / speed);
  }

  static _drawLine(drawContext, xStart, yStart, xEnd, yEnd, color) {
    drawContext.beginPath();
    drawContext.moveTo(xStart, yStart);
    drawContext.lineTo(xEnd, yEnd);
    drawContext.strokeStyle = color;
    drawContext.stroke();
  }

  static drawRight(target, angle, speed) {
    let robot = Robot.$(target);
    robot.isWorking = true;
    robot.motorCount++;
    let steps = Math.round(Math.abs(angle / 5));
    let turn = -angle / steps;

    Canvas._drawTurn(robot, turn, steps, 1, speed);
  }

  static _drawTurn(robot, turn, steps, step, speed) {
    setTimeout(() => {
      robot.heading += turn;
      Canvas._drawRobot(robot);

      robot.leftTicks += 4.335 *  Math.abs(turn);
      robot.rightTicks += 4.335 *  Math.abs(turn);
      if (step < steps) Canvas._drawTurn(robot, turn, steps, step + 1, speed);
      else robot.isWorking = false;
    }, 100 / speed);
  }

  static _drawRobot(robot) {
    let ctxRobot = robot.ctxRobot;
    ctxRobot.clearRect(-Canvas.canvasWidth / 2, -Canvas.canvasHeight / 3,
      Canvas.canvasWidth, Canvas.canvasHeight);

    ctxRobot.beginPath();
    ctxRobot.arc(robot.xCurrent, robot.yCurrent, 5,
      toRadians(robot.heading - 125), toRadians(robot.heading + 125));
    ctxRobot.closePath();
    ctxRobot.strokeStyle = robot.color;
    ctxRobot.stroke();
  }

  static drawLight(target, light, rgbList) {
    let robot = Robot.$(target);
    switch (light) {
      case 0: robot.lightFinch0 = rgbList; break;
      case 1: robot.lightFinch1 = rgbList; break;
      case 2: robot.lightFinch2 = rgbList; break;
      case 3: robot.lightFinch3 = rgbList; break;
      case 4: robot.lightFinch4 = rgbList; break;
      default: // Not Applicable
    }

    if (Robot.currentTarget == target) {
      $("#lightFinch" + light).style.color = Canvas._getColorStyle(rgbList);
    }
  }

  static drawLetter(target, letter) {
    let robot = Robot.$(target);
    robot.ledsFinch = letter;

    if (Robot.currentTarget == target) {
      $("#ledsFinch").innerText = letter;
    }
  }

  static drawLEDs(target, ledGrid) {
    let robot = Robot.$(target);
    robot.ledsFinch = ledGrid.toString();

    if (Robot.currentTarget == target) {
      let output = "";
      for (let row = 0; row < 5; row++) {
        for (let col = 0; col < 5; col++) {
          output += ledGrid[5 * row + col] + " ";
        }
        output += "<br>";
      }
      $("#ledsFinch").innerHTML = output;
    }
  }

  static changeFinchTarget(target) {
    Robot.currentTarget = target;
    let robot = Robot.$(target);
    
    $("#currentFinch").innerText = target;
    $("#currentFinch").style.color = robot.color;
    $("#lightFinch0").style.color = Canvas._getColorStyle(robot.lightFinch0);
    $("#lightFinch1").style.color = Canvas._getColorStyle(robot.lightFinch1);
    $("#lightFinch2").style.color = Canvas._getColorStyle(robot.lightFinch2);
    $("#lightFinch3").style.color = Canvas._getColorStyle(robot.lightFinch3);
    $("#lightFinch4").style.color = Canvas._getColorStyle(robot.lightFinch4);
    $("#ledsFinch").innerText = robot.ledsFinch;
  }

  static _getColorStyle(rgbList) {
    return "rgb(" + rgbList[0] + "," + rgbList[1] + "," + rgbList[2] + ")";
  }
}
Canvas.constructor();

function onSelectFinch() {
  let target = $("#selectFinch").value;
  Canvas.changeFinchTarget(parseInt(target));
}
