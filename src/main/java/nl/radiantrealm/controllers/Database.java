package nl.radiantrealm.controllers;

import nl.radiantrealm.library.sql.HikariDatabase;

public class Database extends HikariDatabase {

    @Override
    protected String databaseURL() {
        return System.getenv("DB_URL");
    }

    @Override
    protected String databaseUsername() {
        return System.getenv("DB_USERNAME");
    }

    @Override
    protected String databasePassword() {
        return System.getenv("DB_PASSWORD");
    }
}
