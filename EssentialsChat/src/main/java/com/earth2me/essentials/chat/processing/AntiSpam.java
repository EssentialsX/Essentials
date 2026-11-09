package com.earth2me.essentials.chat.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AntiSpam {
    //Needs a listener, last message cache, compare to last message,
    Map<UUID, String> lastMessage = new HashMap<>();
    public boolean newMessage(UUID uuid, String message){
        String storedMessage = lastMessage.get(uuid);
        if(storedMessage == message){
            return true;
        }
        lastMessage.put(uuid, message);
        return false;
    }
}
