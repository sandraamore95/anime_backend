package com.appanime.appanime.services;

import com.appanime.appanime.models.Account;
import com.appanime.appanime.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {


    @Autowired
    AccountRepository accountRepository;

    public Account save(Account account) {
        return  this.accountRepository.save(account);
    }

    public Account getAccountByToken(String token){
        return this.accountRepository.findByToken(token);
    }

    public Account findByToken(String token) {
        return this.accountRepository.findByToken(token);
    }

    public void delete(Account account) {
        this.accountRepository.delete(account);    }
}
