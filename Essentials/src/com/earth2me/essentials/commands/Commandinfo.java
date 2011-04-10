package com.earth2me.essentials.commands;

import com.earth2me.essentials.Essentials;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class Commandinfo extends EssentialsCommand {

	public Commandinfo() {
		super("info");
	}

	@Override
	protected void run(Server server, Essentials parent, CommandSender sender, String commandLabel, String[] args) throws Exception {
		String pageStr = args.length > 0 ? args[0].trim() : null;

		List<String> lines = new ArrayList<String>();
		List<String> chapters = new ArrayList<String>();
		Map<String, Integer> bookmarks = new HashMap<String, Integer>();
		File file = new File(parent.getDataFolder(), "info.txt");
		if (file.exists())
		{
			BufferedReader rx = new BufferedReader(new FileReader(file));
			int i = 0;
			for (String l = null; rx.ready() && (l = rx.readLine()) != null; i++)
			{
				if (l.startsWith("#")) {
					bookmarks.put(l.substring(1).toLowerCase(), i);
					chapters.add(l.substring(1));
				}
				lines.add(l.replace('&', '§'));
			}
		} else {
			sender.sendMessage("File info.txt does not exists.");
			return;
		}
		
		if (bookmarks.isEmpty()) {
			int page = 1;
			try
			{
				page = Integer.parseInt(pageStr);
			}
			catch (Exception ex)
			{
				page = 1;
			}
			
			int start = (page - 1) * 9;
			int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);

			sender.sendMessage("Page §c" + page + "§f of §c" + pages + "§f:");
			for (int i = start; i < lines.size() && i < start + 9; i++)
			{
				sender.sendMessage(lines.get(i));
			}
			return;
		}
		
		if (pageStr == null || pageStr.isEmpty() || pageStr.matches("[0-9]+")) {
			if (lines.get(0).startsWith("#")) {
				sender.sendMessage("Select chapter:");
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (String string : chapters) {
					if (!first) {
						sb.append(", ");
					}
					first = false;
					sb.append(string);
				}
				sender.sendMessage(sb.toString());
				return;
			} else {
				int page = 1;
				try
				{
					page = Integer.parseInt(pageStr);
				}
				catch (Exception ex)
				{
					page = 1;
				}

				int start = (page - 1) * 9;
				int end;
				for (end = 0; end < lines.size(); end++)
				{
					String line = lines.get(end);
					if (line.startsWith("#")) {
						break;
					}
				}
				int pages = end / 9 + (end % 9 > 0 ? 1 : 0);

				sender.sendMessage("Page §c" + page + "§f of §c" + pages + "§f:");
				for (int i = start; i < end && i < start + 9; i++)
				{
					sender.sendMessage(lines.get(i));
				}
				return;
			}
		}
		
		int chapterpage = 0;
		if (args.length >= 2) {
			try
			{
				chapterpage = Integer.parseInt(args[1]) - 1;
			}
			catch (Exception ex)
			{
				chapterpage = 0;
			}
		}
		
		if (!bookmarks.containsKey(pageStr.toLowerCase())) {
			sender.sendMessage("Unknown chapter.");
			return;
		}
		int chapterstart = bookmarks.get(pageStr.toLowerCase()) + 1;
		int chapterend;
		for (chapterend = chapterstart; chapterend < lines.size(); chapterend++)
		{
			String line = lines.get(chapterend);
			if (line.startsWith("#")) {
				break;
			}
		}
		int start = chapterstart + chapterpage * 9;
		int page = chapterpage + 1;
		int pages = (chapterend - chapterstart) / 9 + ((chapterend - chapterstart) % 9 > 0 ? 1 : 0);

		sender.sendMessage("Chapter "+ pageStr +", page §c" + page + "§f of §c" + pages + "§f:");
		for (int i = start; i < chapterend && i < start + 9; i++)
		{
			sender.sendMessage(lines.get(i));
		}
	}
}
