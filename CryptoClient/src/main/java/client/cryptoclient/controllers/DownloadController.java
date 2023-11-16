package client.cryptoclient.controllers;

import client.cryptoclient.cryptoAlgorithms.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.text.Transliterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class DownloadController {
    @Autowired
    ObjectMapper mapper;
    Random randomizer = new Random(LocalDateTime.now().getNano());

    private final Map<Long, ClientCryptoProgress> downloadClientMap = new ConcurrentHashMap<>();
    private final Map<Long, ByteArrayResource> downloadFileMap = new ConcurrentHashMap<>();
    private final Map<Long, XTR> xtrClientMap = new HashMap<>();

    @GetMapping("/getdownloadprogress/{id}")
    public ResponseEntity<Object> getDownloadProgress(@PathVariable("id") Long id) {
        System.out.println("UserID in download = " + id);
        Map<String, Object> body = new HashMap<>();
        ClientCryptoProgress clientProgress = downloadClientMap.get(id);
        if (clientProgress == null) {
            body.put("progress", 0);
            body.put("state", "NOT_FOUND");
        }
        else {
            var state = clientProgress.getState();
            switch (state) {
                case WAITING -> body.put("progress", 0);

                case STARTED -> body.put("progress", clientProgress.getCrypto().getDecryptProgress());

                case FINISHED -> {
                    body.put("progress", clientProgress.getCrypto().getDecryptProgress());
                    downloadClientMap.remove(id);
                }
                case NOT_FOUND -> {
                    body.put("progress", 0);
                    downloadClientMap.remove(id);
                }
            }
            body.put("state", state.toString());
        }


        return new ResponseEntity<>(body, HttpStatus.OK);
    }
    @PostMapping("/decryptfile")  // получает запрос от сервака с зашифрованым файлом, чтобы его расшифровать
    public ResponseEntity<Object> setFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam("key") String key,
                                          @RequestParam("mode") String strMode,
                                          @RequestParam("clientId") Long clientID,
                                          @RequestParam("IV") String strIV,
                                          @RequestParam("traceGB") String traceGB ) throws IOException {
        System.out.println("SetFile");
        GFP2Element traceGB2 = mapper.readValue(traceGB, GFP2Element.class);
        XTR clientXTR = xtrClientMap.remove(clientID);
        byte[] decKey = clientXTR.decryptKey(key.getBytes(StandardCharsets.ISO_8859_1), traceGB2);
        byte[] decIV = clientXTR.decryptKey(strIV.getBytes(StandardCharsets.ISO_8859_1), traceGB2);
        System.out.println("Ключ при получке = " + Arrays.toString(decKey));
        System.out.println("IV при получке = " + Arrays.toString(decIV));
        Magenta magenta = new Magenta(decKey);
        Crypto crypto = new Crypto(Modes.valueOf(strMode), magenta, decIV);
        System.out.println("ID for map = " + clientID);

        downloadClientMap.get(clientID).setCrypto(crypto);
        downloadClientMap.get(clientID).setState(State.STARTED);
        byte[] bytes = crypto.decryptFile(file.getInputStream(), file.getSize());
        downloadFileMap.put(clientID, new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            try {
                Thread.sleep(180000); // 3 min
                downloadFileMap.remove(clientID);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        service.shutdown();
        downloadClientMap.get(clientID).setState(State.FINISHED);


        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{id}") // получение страницы скачивания
    public ResponseEntity<Object> download(@PathVariable("id") Long id)  {
        Map<String, Long> body = new HashMap<>();

        Long clientId;

        do {
            clientId = randomizer.nextLong();
        } while(xtrClientMap.containsKey(id));
        model.addAttribute("clientId", clientId);
        downloadClientMap.put(clientId, ClientCryptoProgress.builder().state(State.WAITING).build());

        return "download";
    }

    @GetMapping("/startdownload/{fileId}/{clientId}")
    public ResponseEntity<Object> startDownload(@PathVariable("fileId") Long fileId,
                                                @PathVariable("clientId") Long clientId) throws IOException {
        System.out.println("Requested download with id = " + fileId);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        XTR xtr = new XTR(128);

        xtrClientMap.put(clientId, xtr);
        PublicKey publicKey = xtr.getPublicKey();

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();


        body.add("key", mapper.writeValueAsString(publicKey));
        System.out.println("key = " + mapper.writeValueAsString(publicKey));
        body.add("id", fileId);
        body.add("clientId", clientId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);


        ResponseEntity<String> response
                = restTemplate.exchange("http://localhost:8080/crypto/setkey", HttpMethod.POST, requestEntity, String.class);
        System.out.println(response.getStatusCode());
        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("Body = " + response.getBody());
        }
        else {
            System.out.println("NOT_FOUND FILE");
            downloadClientMap.get(clientId).setState(State.NOT_FOUND);
        }
        Map<String, String> mapResponse = new HashMap<>();
        mapResponse.put("status", "OK");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/downloadfile/{id}")  // скачивает
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        System.out.println("Downloading file: " + id);
        // Создание объекта Resource для представления файла
        Resource fileResource = downloadFileMap.get(id);

        // Установка заголовков ответа
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                URLEncoder.encode(Optional.ofNullable(
                        fileResource.getFilename()).orElse("file"),
                        StandardCharsets.UTF_8));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(fileResource);
    }



}
