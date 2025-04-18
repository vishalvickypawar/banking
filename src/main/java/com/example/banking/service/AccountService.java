package com.example.banking.service;

import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.model.Account;
import com.example.banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account){
        return accountRepository.save(account);
    }

    public Optional<Account> getAccount(Long id){
        return accountRepository.findById(id);
    }

    public Account deposit(Long id, double amount){
        Account account = getAccount(id).orElseThrow(()->new AccountNotFoundException("Account not found"));
        account.setBalance(account.getBalance()+amount);
        return accountRepository.save(account);
    }

    public Account withdraw(Long id, double amount){
        Account account = getAccount(id).orElseThrow(()->new AccountNotFoundException("Account not found"));
        if(account.getBalance()<amount){
            throw new InsufficientFundsException("Insufficient funds");
        }
        account.setBalance(account.getBalance()-amount);
        return accountRepository.save(account);
    }
}
