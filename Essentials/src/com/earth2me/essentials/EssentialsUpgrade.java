package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EssentialsUpgrade {
	private static boolean alreadyRun = false;
	private final static Logger logger = Logger.getLogger("Minecraft"); 

	EssentialsUpgrade(String version, File dataFolder) {
		if (alreadyRun == true) return;
		alreadyRun = true;
		moveWorthValuesToWorthYml(dataFolder);
	}

	private void moveWorthValuesToWorthYml(File dataFolder) {
		try {
			File configFile = new File(dataFolder, "config.yml");
			EssentialsConf conf = new EssentialsConf(configFile);
			conf.load();
			Worth w = new Worth(dataFolder);
			for (Material mat : Material.values()) {
				int id = mat.getId();
				double value = conf.getDouble("worth-"+id, Double.NaN);
				if (!Double.isNaN(value)) {
					w.setPrice(new ItemStack(mat, 1, (short)0, (byte)0), value);
				}
			}
			removeLinesFromConfig(configFile,"\\s*#?\\s*worth-[0-9]+.*", "# Worth values have been moved to worth.yml");
		} catch (Throwable e) {
			logger.log(Level.WARNING, "Error while upgrading the files", e);
		}
	}

	private void removeLinesFromConfig(File file, String regex, String info) throws Exception {
		boolean needUpdate = false;
		BufferedReader br = new BufferedReader(new FileReader(file));
		File tempFile = File.createTempFile("essentialsupgrade", ".yml");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
		do {
			String line = br.readLine();
			if (line == null) break;
			if (line.matches(regex)) {
				if (needUpdate == false && info != null) {
					bw.write(info, 0, info.length());
					bw.newLine();
				}
				needUpdate = true;
			} else {
				if (line.endsWith("\r\n")) {
					bw.write(line, 0, line.length() - 2);
				} else if (line.endsWith("\r") || line.endsWith("\n")) {
					bw.write(line, 0, line.length() - 1);
				} else {
					bw.write(line, 0, line.length());
				}
				bw.newLine();
			}
		} while(true);
		br.close();
		bw.close();
		if (needUpdate) {
			file.renameTo(new File(file.getParentFile(), file.getName().concat("."+System.currentTimeMillis()+".upgradebackup")));
			tempFile.renameTo(file);
		}
	}
}
