package net.frazew.bukkittty;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitTTY extends JavaPlugin {
	
    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("shell")) {
			String cmdLine = "";
			String dir1 = ".";
			for (int i = 0 ; i < args.length; i++) {
				if (!args[i].contains("--shellDir")) {
					cmdLine = cmdLine + args[i] + " ";
				}
				else dir1 = args[i].replace("--shellDir", "");
			}
			if (cmdLine.charAt(cmdLine.length()-1) == ' ') cmdLine = cmdLine.substring(0, cmdLine.length() - 1);
			final String commandLine = cmdLine;
			final String dir = dir1;
    		sender.sendMessage("Commande : " + cmdLine + " dans " + dir);
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<String> result = command(commandLine, dir, sender);
					if (!result.isEmpty()) {
						for (String line : result) {
							sender.sendMessage(line);
						}
					}
					sender.sendMessage("Terminé");
				}				
			}).start();
    		return true;
    	}
    	return false; 
    }
    
    /** Returns null if it failed for some reason.
     */
    public static ArrayList<String> command(final String cmdline,
    final String directory, CommandSender sender) {
        try {
            Process process = 
                new ProcessBuilder(new String[] {"bash", "-c", cmdline})
                    .redirectErrorStream(true)
                    .directory(new File(directory))
                    .start();

            ArrayList<String> output = new ArrayList<String>();
            BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            String line = null;
            while ( (line = br.readLine()) != null ) {
            	sender.sendMessage(line);
            	output.add(line);
            }
                

            if (0 != process.waitFor()) {
            	ArrayList<String> err = new ArrayList<String>();
	        	err.add("Error : " + process.exitValue());
	            br = new BufferedReader(
	                new InputStreamReader(process.getErrorStream()));
	            while ( (line = br.readLine()) != null )
	                err.add(line);
	            return err;
            }

            return output;

        } catch (Exception e) {
        	ArrayList<String> err = new ArrayList<String>();
        	err.add(e.getMessage());
            return err;
        }
    }  
}
