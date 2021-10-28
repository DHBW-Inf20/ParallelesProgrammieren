package de.dhbw.parprog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SmartBankTest {

    private Bank bank;

    @BeforeEach
    public void setupTest() {
        bank = new Bank();
    }

    @Test
    public void newAccountHasNoCredits() {
        var a = bank.createAccount();
        assertThat(bank.getBalance(a)).isEqualTo(0);
    }

    @Test
    public void canDepositAndWithdraw() {
        var a = bank.createAccount();
        bank.deposit(a, 10);
        bank.withdraw(a, 5);
    }

    @Test
    public void cantDepositMoreThanMaximum() {
        var a = bank.createAccount();
        assertThatExceptionOfType(IllegalAccountStateException.class)
                .isThrownBy(() -> {
                    bank.deposit(a, Bank.Account.UPPER_LIMIT);
                    bank.deposit(a, 1);
                });
    }

    @Test
    public void cantWithdrawBelowMinimum() {
        var a = bank.createAccount();
        assertThatExceptionOfType(IllegalAccountStateException.class)
                .isThrownBy(() -> bank.withdraw(a, 1));
    }

    @Test
    public void transferIsDeadlockFree() throws InterruptedException {
        var transfers = 100;
        var startBalance = 1000;

        var a = bank.createAccount();
        var b = bank.createAccount();

        bank.deposit(a, startBalance);
        bank.deposit(b, startBalance);

        var t1 = new Thread(() -> {
            for (int i = 0; i < transfers; i++) {
                bank.transfer(a, b, 1);
            }
        });

        var t2 = new Thread(() -> {
            for (int i = 0; i < transfers; i++) {
                bank.transfer(b, a, 1);
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertThat(bank.getBalance(a)).isEqualTo(startBalance);
        assertThat(bank.getBalance(b)).isEqualTo(startBalance);
    }
}
