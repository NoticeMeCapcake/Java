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
    Random randomizer = new Random(LocalDateTime.now().getNano());

//    private XTR myxtr;

    @NoArgsConstructor
    class RecordModel {
        public Long id;
        public String name;

        public RecordModel(String name, Long id) {
            this.name = name;
            this.id = id;
        }
    }

    private final Map<Long, ClientCryptoProgress> downloadClientMap = new ConcurrentHashMap<>();
    private final Map<Long, ByteArrayResource> downloadFileMap = new ConcurrentHashMap<>();
    private final Map<Long, ClientCryptoProgress> uploadClientMap = new ConcurrentHashMap<>();
    private Map<Long, XTR> xtrClientMap = new HashMap<>();

    @GetMapping("/getdownloadprogress/{id}")
    public ResponseEntity<Object> getDownloadProgress(@PathVariable("id") Long id) throws IOException {
        System.out.println("UserID in download = " + id);
        Map<String, Object> body = new HashMap<>();
        ClientCryptoProgress clientProgress = downloadClientMap.get(id);
        if (clientProgress == null) {
            body.put("progress", 100);
            body.put("state", "FINISHED");
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
    public Float progress = 0f;
    @PostMapping("/setfile")
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
        ClientCryptoProgress cryptoProgress = downloadClientMap.get(clientID);
        cryptoProgress.setCrypto(crypto);
        cryptoProgress.setState(State.STARTED);
        byte[] bytes = crypto.decryptFile(file.getInputStream(), file.getSize());
        downloadFileMap.put(clientID, new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        cryptoProgress.setState(State.FINISHED);

//        try {
//            File myFile = new File("D:/" + file.getOriginalFilename());
//            FileOutputStream fos = new FileOutputStream(myFile);
//            fos.write(bytes);
//            fos.close();
//            System.out.println("File saved successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download{id}")
    public String download(Model model, @PathVariable("id") Long id) throws IOException, MessagingException {
        model.addAttribute("title", "Download: " + id);
        System.out.println("Requested download with id = " + id);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        XTR xtr = new XTR(128);
        Long clientId;
        do {
            clientId = randomizer.nextLong();
        } while(xtrClientMap.containsKey(id));
        model.addAttribute("clientId", clientId);
        xtrClientMap.put(clientId, xtr);
        downloadClientMap.put(clientId, ClientCryptoProgress.builder().state(State.WAITING).build());
        PublicKey publicKey = xtr.getPublicKey();

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();


        body.add("key", mapper.writeValueAsString(publicKey));
        System.out.println("key = " + mapper.writeValueAsString(publicKey));
        body.add("id", id);
        body.add("clientId", clientId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            ResponseEntity<String> response
                    = restTemplate.exchange("http://localhost:8080/crypto/setkey", HttpMethod.POST, requestEntity, String.class);
            System.out.println(response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("Body = " + response.getBody());
            }
        });
        executorService.shutdown();

        return "dowload";
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
    @GetMapping("/upload")
    public String upload(Model model) {
        model.addAttribute("title", "Upload");
        return "upload";
    }

    @PostMapping("/upload")
    public @ResponseBody RedirectView handleFileUpload(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("mode") String modeStr) {
        Long id = null;

        if (!file.isEmpty()) {
            System.out.println("Got file");
            // Шифруем файл, потом отправляем на сервер
            byte[] key = new byte[16];
            Modes mode = Modes.valueOf(modeStr);
            randomizer.nextBytes(key);
            System.out.println("Ключ при отправке = " + (Arrays.toString(key)));
            Magenta magenta = new Magenta(key);
            Crypto crypto = new Crypto(mode, magenta);
            System.out.println("IV при отправке = " + (Arrays.toString(crypto.getIV())));

            try {
                URL url = new URL("http://localhost:8080/crypto/getkey");
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("GET");

                if(httpConnection.getResponseCode() == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    String inputLine;
                    StringBuilder getKeyResponse = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        getKeyResponse.append(inputLine);
                    }
                    in.close();
                    System.out.println(getKeyResponse.toString());

                    JsonNode node = mapper.readTree(getKeyResponse.toString());
                    id = node.get("id").asLong();
                    uploadClientMap.put(id, ClientCryptoProgress.builder().crypto(crypto).state(State.WAITING).build());

                    PublicKey publicKey = mapper.readValue(node.get("key").asText(), PublicKey.class);

                    System.out.println("id: " + id);
                    System.out.println("p = " + publicKey.p);
                    System.out.println("q = " + publicKey.q);
                    System.out.println("trace = " + publicKey.trace);
                    System.out.println("traceGK = " + publicKey.traceGK);

                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    Long finalId = id;
                    executorService.submit(() -> {
                        byte[] bytes = new byte[0];
                        uploadClientMap.get(finalId).setState(State.STARTED);
                        try {
                            long fileSize = file.getSize();
                            bytes = crypto.encryptFile(file.getInputStream(), fileSize);
                        } catch (IOException e) {
                            uploadClientMap.get(finalId).setState(State.NOT_FOUND);
                            System.out.println("File inputstream error");
                        }

                        //TODO: Добавить состояний, для отправки на сервер
                        ByteArrayResource resource = new ByteArrayResource(bytes) {
                            @Override
                            public String getFilename() {
                                return file.getOriginalFilename();
                            }
                        };
                        RestTemplate restTemplate = new RestTemplate();
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                        body.add("file", resource);

                        String keyToString = new String(key, StandardCharsets.ISO_8859_1);
                        System.out.println(Arrays.toString(key));
                        System.out.println("key = " + keyToString);
                        BigInteger b = XTR.generateB(publicKey);
                        GFP2Element traceGB = XTR.generateTraceGB(publicKey, b);
                        byte[] encKey = XTR.encryptKey(key, publicKey, b);
                        byte[] encIV = XTR.encryptKey(crypto.getIV(), publicKey, b);
                        System.out.println("Key Encrypted при отправке = " + Arrays.toString(encKey));
                        System.out.println("IV Encrypted при отправке = " + Arrays.toString(encIV));
                        body.add("key", new String(encKey, StandardCharsets.ISO_8859_1));
                        body.add("mode", modeStr);
                        body.add("IV", new String(encIV, StandardCharsets.ISO_8859_1));
                        body.add("id", finalId);
                        body.add("name", file.getOriginalFilename());
                        body.add("traceGB", traceGB);
                        HttpEntity<MultiValueMap<String, Object>> requestEntity
                                = new HttpEntity<>(body, headers);

                        ResponseEntity<String> response
                                = restTemplate.exchange("http://localhost:8080/crypto/upload", HttpMethod.POST, requestEntity, String.class);

                        System.out.println(response.getStatusCode());
                        uploadClientMap.get(finalId).setState(State.FINISHED);

                    });
                    executorService.shutdown();
                }
                else {
                    System.out.println("Не удалось получить ключ");
                    return new RedirectView("/error");
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            System.out.println("Empty File");
            return new RedirectView("/error");
        }
        if (id == null) {
            return new RedirectView("/error");
        }
        var redirectView = new RedirectView("/uploadprogress/" + id);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }

    @GetMapping("/uploadprogress/{id}")
    public String uploadProgress(Model model, @PathVariable Long id) {
        model.addAttribute("title", "Upload Progress");
        model.addAttribute("clientId", id);
        return "upload-progress";
    }

    @GetMapping("/getuploadprogress/{id}")
    public ResponseEntity<Object> getUploadProgress(@PathVariable Long id) {
        System.out.println("UserID in download = " + id);
        Map<String, Object> body = new HashMap<>();
        ClientCryptoProgress clientProgress = uploadClientMap.get(id);
        if (clientProgress == null) {
            body.put("progress", 100);
            body.put("state", "FINISHED");
        }
        else {
            var state = clientProgress.getState();
            switch (state) {
                case WAITING -> body.put("progress", 0);

                case STARTED -> body.put("progress", clientProgress.getCrypto().getEncryptProgress());

                case FINISHED -> {
                    body.put("progress", clientProgress.getCrypto().getEncryptProgress());
                    uploadClientMap.remove(id);
                }
                case NOT_FOUND -> {
                    body.put("progress", 0);
                    uploadClientMap.remove(id);
                }
            }
            body.put("state", state.toString());
        }
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/downloadfile/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {
        System.out.println("Downloading file: " + id);
        // Создание объекта Resource для представления файла
        Resource fileResource = downloadFileMap.remove(id);

        // Установка заголовков ответа
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                Transliterator.getInstance("Cyrillic-Latin")
                        .transliterate(Optional.ofNullable(fileResource.getFilename()).orElse("file")));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(fileResource);
    }


}
