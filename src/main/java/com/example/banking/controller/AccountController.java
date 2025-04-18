package com.example.banking.controller;

import com.example.banking.dto.Amount;
import com.example.banking.exception.AccountNotFoundException;
import com.example.banking.model.Account;
import com.example.banking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping
    public Account createAccount(@RequestBody Account account){
        return accountService.createAccount(account);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id){
        return accountService.getAccount(id).orElseThrow(()->new AccountNotFoundException("Account not found"));
    }

    @PostMapping("/{id}/deposit")
    public Account deposit(@PathVariable Long id, @RequestBody Amount request){
        return accountService.deposit(id, request.getAmount());
    }

    @PostMapping("/{id}/withdraw")
    public Account withdraw(@PathVariable Long id, @RequestBody Amount request){
        return accountService.withdraw(id, request.getAmount());
    }
}
