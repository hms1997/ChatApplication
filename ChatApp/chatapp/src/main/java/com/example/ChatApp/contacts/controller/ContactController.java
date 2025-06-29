package com.example.ChatApp.contacts.controller;

import com.example.ChatApp.contacts.dto.ContactResponse;
import com.example.ChatApp.contacts.dto.ContactSyncRequest;
import com.example.ChatApp.contacts.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;
    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/sync")
    public ResponseEntity<List<ContactResponse>> syncContacts(@RequestBody ContactSyncRequest request) {
        List<ContactResponse> matchedContacts = contactService.syncContacts(request.getContacts());
        return ResponseEntity.ok(matchedContacts);
    }
}
