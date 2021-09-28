/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson.internal;

import java.util.HashMap;
import java.util.UUID;
import wilson.*;

public class Worker
{
  private static HashMap<UUID, Worker> workers = new HashMap<UUID, Worker>();

  public static Worker getById(UUID sourceId) {
    Common.debug("WORKER getById", sourceId);
    if (Worker.workers.containsKey(sourceId)) {
      return Worker.workers.get(sourceId);
    }
    else {
      return null;
    }
  }
  
  private UUID sourceId;
  private String runModule;
  private int target;
  private HashMap<Integer, Finch> finches;
  private Command command;
  private Command pending;
  private Command queue;
  private Object lock;
  private HashMap<String, Object> response;

  public Worker(UUID sourceId, String runModule) {
    Common.debug("WORKER init", sourceId);
    this.sourceId = sourceId;
    this.runModule = runModule;
    this.target = 0;
    this.finches = new HashMap<Integer, Finch>();
    this.command = null;
    this.pending = null;
    this.queue = null;
    this.response = null;
    this.lock = null;
    Worker.workers.put(sourceId, this);

    MainCode mainCode = new MainCode(sourceId, runModule, this);
    mainCode.start();
  }
  
  public static int nextTarget() {
    Thread thread = Thread.currentThread();
    UUID sourceId = UUID.fromString(thread.getName());
    Worker worker = Worker.getById(sourceId);
    worker.target += 1;
    Common.debug("WORKER nextTarget", worker.target);
    return worker.target;
  }

  public Finch getFinchById(int target) {
    Common.debug("WORKER getFinchById", this.sourceId, target);
    if (this.finches.containsKey(target)) {
      return this.finches.get(target);
    }
    else {
      return null;
    }
  }

  class MainCode extends Thread
  {
    private UUID sourceId;
    private String runModule;
    private Worker worker;

    MainCode(UUID sourceId, String runModule, Worker worker) {
      this.setName(sourceId.toString());
      this.sourceId = sourceId;
      this.runModule = runModule;
      this.worker = worker;
    }

    public void run() {
      Common.debug("WORKER runMainCode", this.sourceId, this.runModule);

      try {
        Class.forName(this.runModule).getMethod("main", String[].class).invoke(null, (Object)null);
      }
      catch (Exception exception) {
        Common.debug("WORKER ERROR MainCode", this.sourceId, exception);
      }

      HashMap<String, Object> params = new HashMap<String, Object>();
      Command command = new Command(0, "done", params);
      this.worker.queueCommand(command);
      Worker.workers.remove(this.sourceId);
      Common.debug("WORKER endMainCode", this.sourceId);
      this.interrupt();
    }
  }

  public static void queueCommand(Command command) {
    Common.debug("WORKER queueCommand", command);
    Thread thread = Thread.currentThread();
    UUID sourceId = UUID.fromString(thread.getName());
    Worker worker = Worker.getById(sourceId);
    worker.command = command;
    worker.response = null;
    
    worker.lock = new Object();
    synchronized (worker.lock) {
      try {
        worker.lock.wait();
      }
      catch (Exception exception) {
        Common.debug("ERROR", exception);
      }
    }
    worker.lock = null;
  }

  public Command getCommand() {
    Common.debug("WORKER getCommand");
    Command command = this.command;
    this.pending = command;
    this.command = null;
    return command;
  }

  public void popCommand(Message message) {
    Common.debug("WORKER popCommand", message, this.pending);
    if (message.command.equals(this.pending.id.toString())) {
      this.response = message.response;
      this.pending = null;
      if (this.lock != null) {
        synchronized (this.lock) {
          this.lock.notify();
        }
      }
    }
  }

  public static HashMap<String, Object> getResponse() {
    Common.debug("WORKER getResponse");
    Thread thread = Thread.currentThread();
    UUID sourceId = UUID.fromString(thread.getName());
    Worker worker = Worker.getById(sourceId);
    HashMap<String, Object> response = worker.response;
    worker.response = null;
    return response;
  }

  public static void trackFinch(int target, Finch finch) {
    Thread thread = Thread.currentThread();
    UUID sourceId = UUID.fromString(thread.getName());
    Common.debug("WORKER trackFinch", sourceId, target);
    Worker worker = Worker.getById(sourceId);
    worker.finches.put(target, finch);
  }

  public static void queueStatus(Command command) {
    Common.debug("WORKER queueStatus", command);
    Thread thread = Thread.currentThread();
    UUID sourceId = UUID.fromString(thread.getName());
    Worker worker = Worker.getById(sourceId);
    worker.queue = command;
    
    worker.lock = new Object();
    synchronized (worker.lock) {
      try {
        worker.lock.wait();
      }
      catch (Exception exception) {
        // Do Nothing
      }
    }
    worker.lock = null;
  }

  public void updateStatus(int target, HashMap<String, Object> status) {
    Common.debug("WORKER updateStatus", this.sourceId, target, status);
    Finch finch = this.finches.get(target);
    if (finch == null) return;
    
    finch.status = status;
    Command command = this.queue;
    if (command == null) return;

    switch (command.type.toLowerCase()) {
      case "init":
      case "reset":
      case "calibrate":
        this.popStatus(command);
        break;
      case "forward":
        int leftStart = Status.getLeftTicks(finch.start);
        int rightStart = Status.getRightTicks(finch.start);
        int moveTicks = Device.getMoveTicks((double)command.params.get("distance"));
        boolean isWorking = Status.getIsWorking(status);
        int leftTicks = Status.getLeftTicks(status);
        int rightTicks = Status.getRightTicks(status);
        int leftMove = Math.abs(leftTicks - leftStart);
        int rightMove = Math.abs(rightTicks - rightStart);
        int moveActual = Math.max(leftMove, rightMove);
        if (!isWorking && ((double)moveActual / moveTicks) > 0.99) {
          this.popStatus(command);
        }
        break;
      case "right":
        int leftStart2 = Status.getLeftTicks(finch.start);
        int rightStart2 = Status.getRightTicks(finch.start);
        int turnTicks = Device.getTurnTicks((double)command.params.get("angle"));
        boolean isWorking2 = Status.getIsWorking(status);
        int leftTicks2 = Status.getLeftTicks(status);
        int rightTicks2 = Status.getRightTicks(status);
        int leftTurn = Math.abs(leftTicks2 - leftStart2);
        int rightTurn = Math.abs(rightTicks2 - rightStart2);
        int turnActual = Math.max(leftTurn, rightTurn);
        if (!isWorking2 && ((double)turnActual / turnTicks) > 0.99) {
          this.popStatus(command);
        }
        break;
    }
  }

  public void popStatus(Command command) {
    Common.debug("WORKER popStatus", command);
    this.queue = null;
    if (this.lock != null) {
      synchronized (this.lock) {
        this.lock.notify();
      }
    }
  }
}
