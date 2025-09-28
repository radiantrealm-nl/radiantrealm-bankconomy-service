package nl.radiantrealm.enumerator;

public enum AccountType {
    PLAYER_ACCOUNT("bankconomy_players"),
    SAVINGS_ACCOUNT("bankconomy_savings"),
    FUNDINGS_ACCOUNT("bankconomy_fundings");

    public final String tableName;

    AccountType(String tableName) {
        this.tableName = tableName;
    }
}
