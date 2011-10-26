package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.UpdateCheck;
import com.earth2me.essentials.update.VersionInfo;
import java.util.List;
import org.bukkit.entity.Player;


public class Changelog extends AbstractState
{
	private static final int CHANGES_PER_PAGE = 5;
	private transient int page = 0;
	private transient boolean confirmed = false;
	private transient final List<String> changes;
	private transient final int pages;

	public Changelog(final StateMap stateMap)
	{
		super(stateMap);
		changes = getChanges();
		pages = changes.size() / CHANGES_PER_PAGE + (changes.size() % CHANGES_PER_PAGE > 0 ? 1 : 0);
	}

	@Override
	public AbstractState getNextState()
	{
		return confirmed ? getState(EssentialsChat.class) : this;
	}

	@Override
	public boolean guessAnswer()
	{
		if (pages == 0)
		{
			confirmed = true;
		}
		return confirmed;
	}

	private List<String> getChanges()
	{
		final UpdateCheck updateCheck = getState(UpdateOrInstallation.class).getUpdateCheck();
		final VersionInfo versionInfo = updateCheck.getNewVersionInfo();
		return versionInfo.getChangelog();
	}

	@Override
	public void askQuestion(final Player sender)
	{
		if (pages > 1)
		{
			sender.sendMessage("Changelog, page " + page + " of " + pages + ":");
		}
		else
		{
			sender.sendMessage("Changelog:");
		}
		for (int i = page * CHANGES_PER_PAGE; i < Math.min(page * CHANGES_PER_PAGE + CHANGES_PER_PAGE, changes.size()); i++)
		{
			sender.sendMessage(changes.get(i));
		}
		if (pages > 1)
		{
			sender.sendMessage("Select a page by typing the numbers 1 to " + pages + " to view all changes and then type confirm to update Essentials.");
		}
		else
		{
			sender.sendMessage("Type confirm to update Essentials.");
		}
	}

	@Override
	public boolean reactOnAnswer(final String answer)
	{
		if (answer.equalsIgnoreCase("confirm"))
		{
			confirmed = true;
			return true;
		}
		if (answer.matches("[0-9]+"))
		{
			final int page = Integer.parseInt(answer);
			if (page <= pages && page > 0)
			{
				this.page = page - 1;
				return true;
			}
		}
		return false;
	}
}
