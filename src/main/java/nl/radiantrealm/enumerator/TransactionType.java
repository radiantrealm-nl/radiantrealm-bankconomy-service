package nl.radiantrealm.enumerator;

public enum TransactionType {
    SAVINGS_DEPOSIT(AccountType.PLAYER_ACCOUNT, AccountType.SAVINGS_ACCOUNT),
    SAVINGS_WITHDRAW(AccountType.SAVINGS_ACCOUNT, AccountType.PLAYER_ACCOUNT),
    SAVINGS_DELETE(AccountType.SAVINGS_ACCOUNT, AccountType.PLAYER_ACCOUNT),
    SAVINGS_INTEREST_ACCUMULATE(AccountType.FUNDINGS_ACCOUNT, AccountType.PLAYER_ACCOUNT),
    SAVINGS_INTEREST_PAYOUT(AccountType.SAVINGS_ACCOUNT, AccountType.SAVINGS_ACCOUNT),

    PLAYER_PAYMENT(AccountType.PLAYER_ACCOUNT, AccountType.PLAYER_ACCOUNT),

    FUNDINGS_PAYMENT(AccountType.PLAYER_ACCOUNT, AccountType.FUNDINGS_ACCOUNT),
    FUNDINGS_CREDIT(AccountType.FUNDINGS_ACCOUNT, AccountType.PLAYER_ACCOUNT);

    public final AccountType sourceAccount;
    public final AccountType offsetAccount;

    TransactionType(AccountType sourceAccount, AccountType offsetAccount) {
        this.sourceAccount = sourceAccount;
        this.offsetAccount = offsetAccount;
    }
}
