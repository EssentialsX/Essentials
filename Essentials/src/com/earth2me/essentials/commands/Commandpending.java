package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;

import java.util.ArrayList;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandpending extends EssentialsCommand{

    public Commandpending() {
        super("pending");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        if (!user.isAuthorized("essentials.pending")) {
            throw new Exception(tl("noPerm", "essentials.pending"));
        }
        if(user.getPending() == null){
            user.sendMessage(tl("noPending"));
            return;
        }
        switch (args[0].toLowerCase()){
            case "confirm":
                user.getPending().run(server,user,commandLabel,args);
                break;
            case "decline":
                user.setPending(null);
                break;
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        List<String> compl = new ArrayList<>();
        compl.add("confirm");
        compl.add("decline");
        return compl;
    }

}
