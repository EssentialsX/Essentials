package com.earth2me.essentials.commands;

import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import java.util.regex.Matcher;  
import java.util.regex.Pattern; 

public class Commandbanip extends EssentialsCommand
{
    public Commandbanip()
    {
        super("banip");
    }

    @Override
    public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
    {
        if (args.length < 1)
        {
            throw new NotEnoughArgumentsException();
        }
        
        if ( isIPAddress(args[0]) {
            ess.getServer().banIP(args[0]);
            sender.sendMessage(Util.i18n("banIpAddress"));
        }
        else {
            User u = ess.getUser(p);
            ess.getServer().banIP(u.getAddress().getAddress().toString());
            sender.sendMessage(Util.i18n("banIpAddress"));
        }
    }
    
    private boolean isIPAddress(String str) {  
        String expression = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        
        return matcher.matches();   
    }
}
