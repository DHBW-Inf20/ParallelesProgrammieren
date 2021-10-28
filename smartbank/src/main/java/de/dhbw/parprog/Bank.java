package de.dhbw.parprog;

@SuppressWarnings("GrazieInspection")
public final class Bank {

    private long idCounter = Long.MIN_VALUE;

    /**
     * Erzeugt ein neues Konto mit initialem Kontostand 0.
     *
     * @return das neue Konto
     */
    public synchronized Account createAccount() {
        return new Account(idCounter++);
    }

    /**
     * Ruft den aktuellen Kontostand ab
     *
     * @param account Konto, dessen Kontostand bestimmt werden soll
     * @return der aktuelle Kontostand
     * @throws NullPointerException wenn account {@code null} ist
     */
    public long getBalance(Account account) {
        return account.getBalance();
    }

    /**
     * Einzahlen eines bestimmten Betrags
     *
     * @param account das Konto, auf den der Betrag eingezahlt werden soll
     * @param amount  der Betrag (muss >= 0 sein)
     * @throws NullPointerException         wenn account {@code null} ist
     * @throws IllegalArgumentException     bei ungültigen Parametern
     * @throws IllegalAccountStateException falls der Kontostand außerhalb des gültigen Wertebereichs geraten würde
     */
    public void deposit(Account account, long amount) {
        account.deposit(amount);
    }

    /**
     * Abheben eines bestimmten Betrags
     *
     * @param account das Konto, von dem der Betrag abgehoben werden soll
     * @param amount  der Betrag (muss >= 0 sein)
     * @throws NullPointerException         wenn account {@code null} ist
     * @throws IllegalArgumentException     bei ungültigen Parametern
     * @throws IllegalAccountStateException falls der Kontostand außerhalb des gültigen Wertebereichs geraten würde
     */
    public void withdraw(Account account, long amount) {
        account.withdraw(amount);
    }

    /**
     * Überweisen eines Betrags von einem Konto auf ein anderes
     *
     * @param fromAccount Konto, von dem abgebucht werden soll
     * @param toAccount   Konto, auf das gutgeschrieben werden soll
     * @param amount      der zu transferierende Betrag (muss >= 0 sein)
     * @throws NullPointerException         wenn fromAccount oder toAccount {@code null} ist
     * @throws IllegalArgumentException     bei ungültigen Parametern
     * @throws IllegalAccountStateException falls einer der Kontostände außerhalb des gültigen Wertebereichs geraten würde
     */
    public void transfer(Account fromAccount, Account toAccount, long amount) {
        if (fromAccount.id == toAccount.id || amount < 0) {
            throw new IllegalArgumentException();
        }

        var fromHasSmallerId = fromAccount.id < toAccount.id;

        synchronized (fromHasSmallerId ? fromAccount : toAccount) {
            synchronized (fromHasSmallerId ? toAccount : fromAccount) {
                fromAccount.withdraw(amount);
                try {
                    toAccount.deposit(amount);
                } catch (IllegalAccountStateException e) {
                    fromAccount.deposit(amount);
                    throw e;
                }
            }
        }
    }


    public static final class Account {

        public static final long LOWER_LIMIT = 0;
        public static final long UPPER_LIMIT = 100000;


        private final long id;
        private long balance = 0;

        private Account(long id) {
            this.id = id;
        }

        private synchronized long getBalance() {
            return balance;
        }

        private synchronized void deposit(long amount) {
            depositOrWithdraw(amount, true);
        }

        private synchronized void withdraw(long amount) {
            depositOrWithdraw(amount, false);
        }

        private void depositOrWithdraw(long amount, boolean deposit) {
            if (amount < 0) {
                throw new IllegalArgumentException();
            }
            var newBalance = deposit ? balance + amount : balance - amount;
            if (newBalance < LOWER_LIMIT || newBalance > UPPER_LIMIT) {
                throw new IllegalAccountStateException();
            } else {
                balance = newBalance;
            }
        }
    }
}
