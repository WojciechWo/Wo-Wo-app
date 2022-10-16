package com.WojciechWo.Wo.Woapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Wo {

    @GetMapping("/Wo")
    public String get(){
        return "WoWo";
    }
}
