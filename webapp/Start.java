/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package webapp;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.rendering.JavalinRenderer;
import io.javalin.plugin.rendering.template.JavalinFreemarker;
import java.util.HashMap;
import java.util.UUID;
import java.io.File;
import java.io.FilenameFilter;
import static io.javalin.apibuilder.ApiBuilder.*;
import wilson.internal.*;

class Start
{
  private static final String STATIC_FILES = "/webapp/static/";
  private static final String TEMPLATES = "/webapp/templates/";
  private static final String FM_EXT = ".html";

  public static void main(String[] args) {
    JavalinRenderer.register(JavalinFreemarker.INSTANCE, FM_EXT);
    Javalin app = Javalin.create(config -> {
      config.addStaticFiles(STATIC_FILES, Location.CLASSPATH);
    }).start(0);

    app.get("/", context -> homePage(context));
    app.get("/finch", context -> finchPage(context));
    app.get("/finch/<runModule>", context -> finchPage(context));
    app.post("/startModule/<runModule>", context -> startModule(context));
    app.post("/nextCommand", context -> nextCommand(context));
    app.post("/replyMessage", context -> replyMessage(context));
    app.post("/updateStatus", context -> updateStatus(context));
  }

  private static void homePage(Context context) {
    Common.debug("WEBAPP homePage");
    HashMap<String, Object> model = getModel();
    try {
      FilenameFilter filterJava = new FilenameFilter() {
        public boolean accept(File f, String name) {
          return name.endsWith(".java");
        }
      };
      File fileRoot = new File("").getAbsoluteFile();
      File[] filesJava = fileRoot.listFiles(filterJava);
      String[] fileList = new String[filesJava.length];
      for (int index = 0; index < filesJava.length; index++) {
        fileList[index] = filesJava[index].getName().split("\\.")[0];
      }
      model.put("fileList", fileList);
    }
    catch (Exception exception) {
      model.put("fileList", new String[] {"Main"});
    }
    fmRender(context, "index", model);
  }

  private static void finchPage(Context context) {
    String runModule = null;
    try {
      runModule = context.pathParam("runModule");
    }
    catch (Exception exception) {
      runModule = "Main";
    }
    UUID uniqueId = UUID.randomUUID();
    Common.debug("WEBAPP finchPage", uniqueId, runModule);

    HashMap<String, Object> model = getModel();
    model.put("uniqueId", uniqueId);
    model.put("runModule", runModule);
    fmRender(context, "finch", model);
  }

  private static void startModule(Context context) {
    String runModule = context.pathParam("runModule");
    byte[] body = context.bodyAsBytes();
    String uuid = (new String(body)).replace("\"", "");
    UUID sourceId = UUID.fromString(uuid);
    Common.debug("WEBAPP nextCommand", sourceId, runModule);

    Worker worker = new Worker(sourceId, runModule);
    Command command = worker.getCommand();

    String response = "{}";
    int status = 204;
    if (command != null) {
      response = command.toJson();
      status = 200;
    }
    context.status(status).json(response);
  }

  private static void nextCommand(Context context) {
    byte[] body = context.bodyAsBytes();
    String uuid = (new String(body)).replace("\"", "");
    UUID sourceId = UUID.fromString(uuid);
    Common.debug("WEBAPP nextCommand", sourceId);

    Worker worker = Worker.getById(sourceId);
    Command command = worker.getCommand();

    String response = "{}";
    int status = 204;
    if (command != null) {
      response = command.toJson();
      status = 200;
    }
    context.status(status).json(response);
  }

  private static void replyMessage(Context context) {
    Message message = context.bodyAsClass(Message.class);
    Common.debug("WEBAPP replyMessage", message);

    Worker worker = Worker.getById(message.sourceId);
    worker.popCommand(message);

    context.status(200).json("{}");
  }

  private static void updateStatus(Context context) {
    Status status = context.bodyAsClass(Status.class);
    Common.debug("WEBAPP updateStatus", status);

    Worker worker = Worker.getById(status.sourceId);
    worker.updateStatus(status.target, status.status);

    context.status(200).json("{}");
  }

  private static HashMap<String, Object> getModel() {
    return new HashMap<String, Object>();
  }

  private static void fmRender(Context context, String template, HashMap<String, Object> model) {
    context.render(TEMPLATES + template + FM_EXT, model);    
  }
}


