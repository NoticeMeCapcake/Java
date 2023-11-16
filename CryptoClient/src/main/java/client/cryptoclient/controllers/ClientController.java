package client.cryptoclient.controllers;

import client.cryptoclient.cryptoAlgorithms.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.text.Transliterator;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

@Controller
public class ClientController {
    @Autowired
    ObjectMapper mapper;


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
    public String home(Model model) {
        model.addAttribute("title", "Home");
        RestTemplate restTemplate = new RestTemplate();
        List<RecordModel> files = restTemplate.getForObject("http://localhost:8080/crypto/getfiles", List.class);
        model.addAttribute("files", files);

        System.out.println(files.toString());
        return "home";
    }

    @DeleteMapping("/deletefile/{id}")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete("http://localhost:8080/crypto/deletefile/" + id);
//        System.out.println();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
