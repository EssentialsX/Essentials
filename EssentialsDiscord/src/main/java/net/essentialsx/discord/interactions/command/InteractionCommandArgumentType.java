package net.essentialsx.discord.interactions.command;

public enum InteractionCommandArgumentType {
    SUBCOMMAND(1),
    SUBCOMMAND_GROUP(2),
    STRING(3),
    INTEGER(4),
    BOOLEAN(5),
    USER(6),
    CHANNEL(7),
    ROLE(8);

    private final int id;
    InteractionCommandArgumentType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
