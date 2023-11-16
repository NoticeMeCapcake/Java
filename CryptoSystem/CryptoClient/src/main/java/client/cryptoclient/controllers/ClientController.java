package client.cryptoclient.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@Slf4j
public class ClientController {
    @Autowired
    ObjectMapper mapper;
    Random randomizer = new Random(LocalDateTime.now().getNano());

    @NoArgsConstructor
    class RecordModel {
        public Long id;
        public String name;
        public String mode;
        public Float size;
        public String _date;

        public RecordModel(Long id, String name, String mode, Float size, String _date) {
            this.name = name;
            this.id = id;
            this.mode = mode;
            this.size = size;
            this._date = _date;
        }
    }


    @GetMapping("/")
    public String home(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("title", "Home");
        RestTemplate restTemplate = new RestTemplate();
        List<RecordModel> files = restTemplate.getForObject("http://localhost:8080/crypto/getfiles", List.class);
        model.addAttribute("files", files);
        return "home";
    }

    @DeleteMapping("/deletefile/{id}")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete("http://localhost:8080/crypto/deletefile/" + id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
