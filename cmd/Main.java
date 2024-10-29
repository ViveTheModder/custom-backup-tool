package cmd;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main 
{
	private static ArrayList<File> inputDirList = new ArrayList<File>();
	private static File outputDir;
	private static final String TIME_FORMAT = "dd-MM-yy-hh-mm-ss";
	static boolean includeDateTime;
	static long bytes=0, files=0, folders=0;
	static String currPath, errorMsg;
	public static boolean isDirsFileValid(File dirs)
	{
		boolean isInputDir=true;
		String[] dirType = {"Input","Output"};
		Scanner sc=null;
		try 
		{
			sc = new Scanner(dirs);
			while (sc.hasNextLine())
			{
				String input = sc.nextLine();
				System.out.println(input);
				File inputAsFile = new File(input);
				//check if input is directory
				if (inputAsFile.isDirectory())
				{
					//check for input directories
					if (isInputDir) inputDirList.add(inputAsFile);
					//check for output directory
					else outputDir=inputAsFile;
				}
				else
				{
					//check if input specifies type of directory or option
					if (input.contains(dirType[0])) isInputDir=true;
					else if (input.contains(dirType[1])) isInputDir=false;
					else if (input.startsWith("Include Date & Time in Backup Folder:"))
					{
						if (input.endsWith("No")) includeDateTime=false;
						else if (input.endsWith("Yes")) includeDateTime=true;
						else 
						{
							errorMsg="Date & Time in Backup Folder option has not been specified properly!";
							sc.close(); return false;
						}
					}
				}
				
			}
			sc.close(); 
		} 
		catch (FileNotFoundException e) 
		{
			errorMsg="Missing directories!"; return false;		
		}
		return true;
	}
	public static String getSizeInMultipleOfBytes()
	{
		double newSize=0;
		long GB = 1073741824;
		long MB = 1048576;
		long TB = 1099511627776L;
		String unit=" ";
		if (bytes>=TB) 
		{
			newSize=(double)bytes/TB; unit+="TB";
		}
		else if (bytes>=GB) 
		{
			newSize=(double)bytes/GB; unit+="GB";
		}
		else if (bytes>=MB) 
		{
			newSize=(double)bytes/MB; unit+="MB";
		}
		else if (bytes>=1024)
		{
			newSize=(double)bytes/1024; unit+="KB";
		}
		else unit+="B";
		if (bytes>=1024) return String.format("%.3f", newSize)+unit;
		else return bytes+unit;
	}
	public static void backup(File src, File dst) throws IOException
	{
		if (src.equals(dst)) return; //avoid infinite recursion in accidental cases
		if (src.isDirectory())
		{
			dst.mkdirs(); //make folders and subfolders in case source has them
			folders++;
			//check for subfolders
			String[] list = src.list();
			if (list!=null)
			{
				for (String name: list)
				{
					File newSrc = new File(src,name);
					File newDst = new File(dst,name);
					backup(newSrc,newDst);
				}
			}
		}
		else if (src.isFile()) //copy files
		{
			files++; bytes+=src.length();
			currPath = src.toPath().toString();
			MsgBox.label.setText(MsgBox.HTML_TEXT+"Working on:<br>"+currPath);
			try 
			{
				dst.getParentFile().mkdirs(); //I put this just to be 100% sure
				boolean canBeRenamed = src.renameTo(src);
				if (!canBeRenamed) //and here is a cheap way of checking if a file is in use or not
				{
					String os = System.getProperty("os.name");
					if (os.contains("Win"))
					{
						String srcFileName = src.getName();
						String srcFolder = src.toPath().toString().replace(srcFileName, "");
						String dstFolder = dst.toPath().toString().replace(srcFileName, "");
						//remove slashes at end of folder name via regex
						srcFolder = srcFolder.replaceFirst(".$","");
						dstFolder = dstFolder.replaceFirst(".$", "");
						//add quotes before and after
						srcFileName='"'+srcFileName+'"';
						srcFolder='"'+srcFolder+'"'; dstFolder='"'+dstFolder+'"';
						//execute robocopy command
						ProcessBuilder builder = new ProcessBuilder("robocopy",srcFolder,dstFolder,srcFileName);
						builder.redirectErrorStream(true); //actual life saver
						Process proc = builder.start();
						proc.waitFor();
					}
				}
				else Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} 
			catch (IOException | SecurityException | InterruptedException e) 
			{
				//I wrote this because I find it far more favorable than e.printStackTrace();
				File errorLog = new File("errors.log");
				try {
					FileWriter logWriter = new FileWriter(errorLog,true);
					logWriter.append(new SimpleDateFormat(TIME_FORMAT).format(new Date())+":\n"+e.getMessage());
					logWriter.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		MsgBox.resultsLabel.setText(MsgBox.HTML_TEXT+"Files: "+files+"<br>Folders: "+folders+"<br>Size: "+Main.getSizeInMultipleOfBytes());
	}
	public static void main(String[] args) throws IOException 
	{
		String currWorkDir = new File(".").getCanonicalPath();
		File dirs = new File(currWorkDir+"\\config\\dirs.txt");
		if (isDirsFileValid(dirs)) 
		{
			MsgBox.setMsgBox();
			Date d = new Date();
			long start = System.currentTimeMillis();
			for (File inputDir: inputDirList)
			{
				String backupFolder = outputDir+"\\"+inputDir.getName()+"-BACKUP";
				if (includeDateTime) backupFolder+='-'+new SimpleDateFormat(TIME_FORMAT).format(d);
				File newOutputDir = new File(backupFolder);
				newOutputDir.mkdir(); //make backup folder
				backup(inputDir,newOutputDir);
			}
			long finish = System.currentTimeMillis();
			double time = (finish-start)/(double)1000;
			MsgBox.frame.setVisible(false);
			MsgBox.setMsgBoxSuccess(time);
			System.exit(0);
		}
		else 
		{
			MsgBox.setMsgBoxError(errorMsg);
			System.exit(1);
		}
	}
}