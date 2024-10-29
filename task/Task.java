package task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class Task 
{
	private static final String JAVA_EXE_PATH = "start /b javaw.exe";
	private static final String TOOL_TITLE = "backup.jar";
	private static final String[] WEEKDAYS = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
	static int bakFreq=1, weekday=0;
	static int[] startTimeOptions = {0,0,0};
	static String schedType="";
	public static void makeBatchFile(String task, String command, String currDir) throws IOException
	{
		File batch = new File(currDir+"\\config\\scheduler.bat");
		FileWriter writer = new FileWriter(batch);
		writer.write(task+" /F");
		writer.close();
		
		batch = new File(currDir+"\\config\\task.bat");
		writer = new FileWriter(batch);
		String changeDir = "cd /d "+'"'+currDir+'"'+"\n";
		writer.write(changeDir+"\n"+command);
		writer.close();
	}
	public static void runBatchFile(String currDir) throws InterruptedException, IOException
	{
		ProcessBuilder builder = new ProcessBuilder(currDir+"\\config\\scheduler.bat");
		Process proc = builder.start();
		proc.waitFor();
	}
	public static void setScheduledTask()
	{
		String os = System.getProperty("os.name");
		if (os.contains("Win"))
		{
			try
			{
				String finalJavaPath = JAVA_EXE_PATH;
				String currDir = new File(".").getCanonicalPath();
				String schedJar = currDir+'\\'+TOOL_TITLE;
				String command = finalJavaPath+" -jar"+' '+schedJar;
				//important arguments for scheduled task creation
				String task = "schtasks.exe /CREATE /TN "+"\"ScheduledBackupTask\""+
				" /TR "+currDir+"\\config\\task.bat"+" /SC "+schedType+" /ST ";
				//add leading zeroes for /ST argument to work as intended
				String[] extraZeroes = {"","",""};
				for (int i=0; i<startTimeOptions.length; i++)
				{
					if (startTimeOptions[i]>=10) continue;
					extraZeroes[i]="0";
				}
				task+=extraZeroes[0]+startTimeOptions[0]+":"+extraZeroes[1]+startTimeOptions[1]+":"+extraZeroes[2]+startTimeOptions[2];
				//add /MO argument (mandatory)
				task+=" /MO "+bakFreq;
				//add /D argument if weekday is provided (optional)
				if (weekday!=0) task+=" /D "+WEEKDAYS[weekday-1];
				makeBatchFile(task,command,currDir);
				runBatchFile(currDir);
			}
			catch (IOException | InterruptedException e)
			{
				System.out.println("Exception of "+e.getCause()+" type occured!"); return;
			}
		}
		else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) //TODO: add Unix code
			JOptionPane.showMessageDialog(null, Scheduler.HTML_TEXT+"Unix support has not been added yet!", Scheduler.WINDOW_TITLE, JOptionPane.ERROR_MESSAGE);
	}
}
