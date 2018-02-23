import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GenBuildParams extends DefaultTask {
@TaskAction
public void run() {
    try{
        BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/kotlin/Gen.kt"));
        String time = new SimpleDateFormat("HH:mm:ss dd-MMMM-yy").format(Calendar.getInstance().getTime());
        writer.write ("object Gen {\n");
        writer.write ("fun date():String{return \"" + time + "\"}" + "\n");
        writer.write ("}\n");
        writer.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    System.out.println("Hello from task " + getPath() + "!");
}
}