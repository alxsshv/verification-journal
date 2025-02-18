package com.alxsshv.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    @GetMapping("/")
    public String getMainPage() {
        return "index";
    }

    @GetMapping("/registration")
    public String registration() {
        return "security/auth/registration";
    }

    @GetMapping("/login")
    public String login() {
        return "security/auth/login";
    }

    @GetMapping("/access_denied")
    public String accessDenied() {
        return "security/auth/accessDenied";
    }

    @GetMapping("/journal")
    public String getJournalList() {
        return "journal/list";
    }

    @GetMapping("/journal/form")
    public String getJournalForm() {
        return "journal/form";
    }

    @GetMapping("/journal/{id}")
    public String getJournalCard(@PathVariable("id") long id, Model model) {
        model.addAttribute("id", id);
        return "journal/card";
    }

    @GetMapping("/journal/edit/{id}")
    public String getJournalEditForm(@PathVariable("id") long id, Model model) {
        model.addAttribute("id", id);
        return "journal/edit";
    }

    @GetMapping("/journal/add/{id}")
    public String getProtocolAddingForm(@PathVariable("id") long id, Model model){
        model.addAttribute("id",id);
        return "journal/protocol/form";
    }

    @GetMapping("/journal/protocol/wait")
    public String getProtocolSigningForm(){
        return "journal/protocol/wait_sign";
    }

    @GetMapping("/user")
    public String getUsersListView(){
        return "security/users/list";
    }

    @GetMapping("/user/{id}")
    public String getUserView(@RequestParam("id") String id, Model model){
        model.addAttribute("id",id);
        return "security/users/card";
    }

    @GetMapping("/user/form")
    public String getUserForm(){
        return "security/users/form";
    }

    @GetMapping("/user/form/{id}")
    public String getEditUserView(@RequestParam("id") String id, Model model){
        model.addAttribute("id",id);
        return "security/users/edit";
    }

    @GetMapping("/user/wait")
    public String getWaitingCheckUsersList(){
        return "security/users/wait_list";
    }


    @GetMapping("/settings")
    public String getEditUserView(){
        return "security/settings/system_admin";
    }

}
