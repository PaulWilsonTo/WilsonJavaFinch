/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
class MockDevice extends EventTarget
{
  constructor(target) {
    super();
    let randomId = 1000 + Math.floor(9000 * Math.random());
    this.id = "MockFinch" + randomId;
    this.name = "FNF" + randomId;
    this.gatt = new MockGatt(this);
    this._version = 0;
    this._target = target;
  }
  
  get [Symbol.toStringTag]() {
    return this.constructor.name;
  }
}

class MockGatt {
  constructor(device) {
    this.device = device;
    this.connected = false;
  }

  connect() {
    this.connected = true;
    return MockDelay.promise(this);
  }

  disconnect() {
    this.connected = false;
    this.device.dispatchEvent(
      new Event("gattserverdisconnected")
    );
  }

  getPrimaryService(serviceId) {
    return MockDelay.promise(new MockService(serviceId, this.device));
  }
}

class MockService {
  constructor(serviceId, device) {
    this.uuid = serviceId;
    this.device = device;
    this._finch = new MockFinch(device._version, device._target);
  }

  getCharacteristic(characteristicId) {
    if (characteristicId == Device.txCharId) {
      return MockDelay.promise(new MockTxChar(characteristicId, this));
    }
    else if (characteristicId == Device.rxCharId) {
      return MockDelay.promise(new MockRxChar(characteristicId, this));
    }
  }
}

class MockTxChar {
  constructor(characteristicId, service) {
    this.uuid = characteristicId;
    this.service = service;
  }

  writeValue(value) {
    this.service._finch.writeValue(value);
    return MockDelay.promise();
  }

  writeValueWithoutResponse(value) {
    return this.writeValue(value);
  }
}

class MockRxChar {
  constructor(characteristicId, service) {
    this.uuid = characteristicId;
    this.service = service;
    this._notify = false;
  }

  startNotifications() {
    this._notify = true;
    return MockDelay.promise();
  }

  stopNotifications() {
    this._notify = false;
    return MockDelay.promise();
  }

  readValue() {
    let value = this.service._finch.readValue();
    return MockDelay.promise(new DataView(value.buffer));
  }
}

class MockFinch {
  constructor(version, target) {
    this.version = version;
    this.target = target;
    this.isVersion = false;
  }

  writeValue(value) {
    let baseCmd = value[0];
    this.isVersion = (baseCmd == Device.getVersionCmd[0]);
  }

  readValue() {
    if (this.isVersion) { // MicroBit v1 or v2
      if (this.version == 1) return Uint8Array.of(1, 2, 3);
      if (this.version == 2) return Uint8Array.of(1, 2, 3, 4);
      else return Uint8Array.of(1, 2);
    }
    else {
      let robot = Robot.$(this.target);
      let leftBytes = splitBytes(robot.leftTicks);
      let rightBytes = splitBytes(robot.rightTicks);
      return Uint8Array.of(
        0, 0, 0, 0, (robot.isWorking ? 128 : 0), 0 , 0,
        leftBytes.highByte, leftBytes.medByte, leftBytes.lowByte,
        rightBytes.highByte, rightBytes.medByte, rightBytes.lowByte,
        0, 0, 0, 50, 0, 0, 0
      );
    }
  }
}

class MockDelay {
  static promise(result) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        resolve(result);
      }, 50);
    });
  }
}

function splitBytes(number) {
  let lowByte = number % 256;
  let medByte = Math.floor(number / 256) % 256;
  let highByte = Math.floor(number / 256**2) % 256;
  return {
    lowByte: lowByte, medByte: medByte, highByte: highByte
  };
}
