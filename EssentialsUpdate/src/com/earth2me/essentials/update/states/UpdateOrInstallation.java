package com.earth2me.essentials.update.states;

import com.earth2me.essentials.update.UpdateCheck;
import org.bukkit.entity.Player;


public class UpdateOrInstallation extends AbstractState
{
	private final transient UpdateCheck updateCheck;
	private transient boolean update = false;

	public UpdateOrInstallation(final StateMap stateMap, final UpdateCheck updateCheck)
	{
		super(stateMap);
		this.updateCheck = updateCheck;
	}

	@Override
	public boolean guessAnswer()
	{
		if (getUpdateCheck().isEssentialsInstalled())
		{
			update = true;
		}
		return update;
	}

	@Override
	public AbstractState getNextState()
	{
		return update ? getState(Changelog.class) : getState(EssentialsChat.class);
	}

	@Override
	public void askQuestion(final Player sender)
	{
		sender.sendMessage("Thank you for choosing Essentials.");
		sender.sendMessage("The following installation wizard will guide you through the installation of Essentials.");
		sender.sendMessage("Your answers will be saved for a later update.");
		sender.sendMessage("Please answer the messages with yes or no, if not otherwise stated.");
		sender.sendMessage("Write bye/exit/quit if you want to exit the wizard at anytime.");
		sender.sendMessage("Type ok to continue...");
	}

	@Override
	public boolean reactOnAnswer(final String answer)
	{
		return answer.equalsIgnoreCase("ok") || answer.equalsIgnoreCase("k") || answer.equalsIgnoreCase("continue");
	}

	public UpdateCheck getUpdateCheck()
	{
		return updateCheck;
	}

	public boolean isUpdate()
	{
		return update;
	}
}
