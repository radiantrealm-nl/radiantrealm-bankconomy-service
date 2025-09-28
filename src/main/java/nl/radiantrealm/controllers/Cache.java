package nl.radiantrealm.controllers;

import nl.radiantrealm.cache.FundingsAccountCache;
import nl.radiantrealm.cache.PlayerAccountCache;
import nl.radiantrealm.cache.SavingsAccountCache;

public class Cache {
    public static final FundingsAccountCache fundingsAccountCache = new FundingsAccountCache();
    public static final PlayerAccountCache playerAccountCache = new PlayerAccountCache();
    public static final SavingsAccountCache savingsAccountCache = new SavingsAccountCache();

    private Cache() {}
}
