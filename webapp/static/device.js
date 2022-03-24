/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
class Device
{
  static serviceId = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
  static txCharId = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
  static rxCharId = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
  static fnOptions = {
    filters: [{namePrefix: "FN", services: [Device.serviceId]}]
  };

  static startNotify0Cmd = Uint8Array.of(0x62);
  static startNotify1Cmd = Uint8Array.of(0x62, 0x67);
  static startNotify2Cmd = Uint8Array.of(0x62, 0x70);
  static getVersionCmd = Uint8Array.of(0xD4);

  static constructor() {
    Device.map = new Map();
  }

  static $(target) {
    if (!Device.map.has(target)) {
      Device.map.set(target, new Device(target));
    }
    return Device.map.get(target);
  }

  static disconnectAll() {
    for (let target of Device.map.keys()) {
      let finch = Device.map.get(target);
      finch.disconnect();
      Device.map.delete(target);
    }
  }

  constructor(target) {
    this.target = target;
    this._initialize();
  }

  _initialize() {
    this.device = null;
    this.txChar = null;
    this.rxChar = null;
    this.version = -1;
    this.name = null;
    this._reading = false;
    this._disconnected = null;
  }

  async connect() {
    try {
      this.device = await navigator.bluetooth.requestDevice(Device.fnOptions)
        .catch((error) => this.device = new MockDevice(this.target));
      let gatt = await this.device.gatt.connect();
      let service = await gatt.getPrimaryService(Device.serviceId);
      this.txChar = await service.getCharacteristic(Device.txCharId);
      this.rxChar = await service.getCharacteristic(Device.rxCharId);
      this.name = getDeviceFancyName(this.device.name);

      await this.rxChar.startNotifications();
			
      this.rxChar.addEventListener("characteristicvaluechanged",
        (event) => { this._onRxValueChanged(event); }
      );

      await this.sendCommand(Device.getVersionCmd);
			await this.setVersion();

      this._disconnected = this._onDisconnected.bind(this);
      this.device.addEventListener("gattserverdisconnected", this._disconnected);
    }
    catch (error) {
      debug(error);
      this._initialize();
    }
    debug(this);

    return this;
  }
  
  async sendCommand(command) {
    if (!this.txChar) return;
    await this.txChar.writeValueWithoutResponse(command);
    return;
  }

  async _onRxValueChanged(event) {
    if (this._reading) return;
    this._reading = true;
    
    let dataView = event.target.value;
    let dataList = new Uint8Array(dataView.buffer);
    let deviceName = event.target.service.device.name;

    if (this.version == -1) {
      await this.getVersion(dataList);
      this._reading = false;
    }
    else {
      let status = new Status(this.target, dataList);
      setTimeout(() => {
        Main.updateStatus(this, status);
        this._reading = false;
      }, 50);
    }

  }

	async setVersion() {
		if (this.device._isMock) {
			this.version = 0;
		}
		else {
			while (this.version == -1) {
				await new Promise(resolve => setTimeout(resolve, 50));
			}
		}
	}
  
  async getVersion(dataList) {
    if (![2,3,4].includes(dataList.length)) return;
    let startNotifyCmd = null;
    switch (dataList.length) {
      case 3: this.version = 1; startNotifyCmd = Device.startNotify1Cmd; break;
      case 4: this.version = 2; startNotifyCmd = Device.startNotify2Cmd; break;
      default: this.version = 0; startNotifyCmd = Device.startNotify0Cmd;
    }
    await this.sendCommand(startNotifyCmd);
  }

  getStatus() {
    if (this.version != 0) return;
    this.rxChar.readValue()
    .then((dataView) => {
      let dataList = new Uint8Array(dataView.buffer);
      let status = new Status(this.target, dataList);
      Main.updateStatus(this, status);
    });
  }

  disconnect() {
    this.device.gatt.disconnect();
  }

  _onDisconnected(event) {
    debug(event);
    event.target.removeEventListener("gattserverdisconnected", this._disconnected);
    this._initialize();
  }
}
Device.constructor();
