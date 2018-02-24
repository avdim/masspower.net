import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//todo переписать на kotlin
public class GenBuildParams extends DefaultTask {
@TaskAction
public void run() {
    try{
        System.out.println("getProject().getProjectDir() = " + getProject().getProjectDir());
        BufferedWriter writer = new BufferedWriter(new FileWriter(getProject().getProjectDir().getAbsolutePath() + "/src/main/kotlin/Gen.kt"));
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