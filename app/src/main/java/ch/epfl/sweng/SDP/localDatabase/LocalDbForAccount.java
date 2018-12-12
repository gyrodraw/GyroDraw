package ch.epfl.sweng.SDP.localDatabase;

import ch.epfl.sweng.SDP.auth.Account;

/**
 * Interface representing a generic handler for the local database, responsible of operations
 * involving {@link Account}.
 */
public interface LocalDbForAccount {

    /**
     * Saves the given account in the local database.
     *
     * @param account the account to be saved
     */
    void saveAccount(Account account);

    /**
     * Retrieves the account data stored in the local database and update the given account with
     * it.
     *
     * @param account the account to be updated
     */
    void retrieveAccount(Account account);
}
