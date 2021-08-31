package lielietea.mirai.plugin.administration.blacklist;

import lielietea.mirai.plugin.administration.Operation;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CommandParser {
    static Pattern ADD_REG = Pattern.compile("/blacklist (user|group) add ([1-9]\\d{4,10}) (\\S+)");
    static Pattern DELETE_REG = Pattern.compile("/blacklist (user|group) (remove|delete) ([1-9]\\d{4,10})");
    static Pattern SET_REASON_REG = Pattern.compile("/blacklist (user|group) (updateReason|setReason) ([1-9]\\d{4,10}) (\\S+)");
    static Pattern EDIT_NOTE_REG = Pattern.compile("/blacklist (user|group) (setNote|addNote) ([1-9]\\d{4,10}) (\\S+)");
    static Pattern SEARCH_REG = Pattern.compile("/blacklist (user|group) (search|find|get) ([1-9]\\d{4,10})");
    static Pattern VIEW_ALL_REG = Pattern.compile("/blacklist (user|group) all(( detailed)?)");

    static Optional<Operation> parse(MessageEvent event){
        String message = event.getMessage().contentToString();
        if(SEARCH_REG.matcher(message).matches()) return Optional.of(handleSearchCommand(event));
        else if(VIEW_ALL_REG.matcher(message).matches()) return Optional.of(handleViewAllCommand(event));
        else if(ADD_REG.matcher(message).matches()) return Optional.of(handleAddCommand(event));
        else if(DELETE_REG.matcher(message).matches()) return Optional.of(handleDeleteCommand(event));
        else if(SET_REASON_REG.matcher(message).matches()) return Optional.of(handleSetReasonCommand(event));
        else if(EDIT_NOTE_REG.matcher(message).matches()) return Optional.of(handleEditNoteCommand(event));
        else return Optional.empty();
    }

    static Operation handleAddCommand(MessageEvent event){
        Matcher matcher = ADD_REG.matcher(event.getMessage().contentToString());
        return new BlacklistOperation.AddToBlacklist(event,Long.parseLong(matcher.group(2)),matcher.group(1).equals("group"),matcher.group(3));
    }

    static Operation handleDeleteCommand(MessageEvent event){
        Matcher matcher = DELETE_REG.matcher(event.getMessage().contentToString());
        return new BlacklistOperation.RemoveFromBlacklist(event,Long.parseLong(matcher.group(3)),matcher.group(1).equals("group"));
    }

    static Operation handleSetReasonCommand(MessageEvent event){
        Matcher matcher = SET_REASON_REG.matcher(event.getMessage().contentToString());
        return new BlacklistOperation.ResetBanReason(event,Long.parseLong(matcher.group(3)),matcher.group(1).equals("group"),matcher.group(4));
    }

    static Operation handleEditNoteCommand(MessageEvent event){
        Matcher matcher = EDIT_NOTE_REG.matcher(event.getMessage().contentToString());
        return new BlacklistOperation.EditBlackListNote(event,Long.parseLong(matcher.group(3)),matcher.group(1).equals("group"),matcher.group(4),matcher.group(2).equals("setNote"));
    }

    static Operation handleSearchCommand(MessageEvent event){
        Matcher matcher = SEARCH_REG.matcher(event.getMessage().contentToString());
        return new BlacklistOperation.SearchInBlackList(event,Long.parseLong(matcher.group(3)),matcher.group(1).equals("group"));
    }

    static Operation handleViewAllCommand(MessageEvent event){
        Matcher matcher = VIEW_ALL_REG.matcher(event.getMessage().contentToString());
        return new BlacklistOperation.GetAllInBlackList(event,matcher.group(1).equals("group"),matcher.group(2).equals(" detailed"));
    }



}
