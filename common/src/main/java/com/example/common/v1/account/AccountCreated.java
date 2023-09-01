package com.example.common.v1.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

@Getter
@JsonTypeName("AccountCreated")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountCreated extends AccountEvent {

    private Long id;
    private String lastName;
    private String firstName;
    private String email;

    @JsonCreator
    public AccountCreated(Long id, String lastName, String firstName, String email) {
        super(EventType.ACCOUNT_CREATED);
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }

}
