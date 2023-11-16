package client.cryptoclient.controllers;

import client.cryptoclient.cryptoAlgorithms.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class UploadController {
    @Autowired
    private ObjectMapper mapper;
    private Random randomizer = new Random(LocalDateTime.now().getNano());

    private final Map<Long, ClientCryptoProgress> uploadClientMap = new ConcurrentHashMap<>();

    @GetMapping("/upload")  // выдаёт страницу с формой загрузки файла
    public String upload(Model model) {
        model.addAttribute("title", "Upload");
        return "upload";
    }

    @GetMapping("getClientIdUpload")
    public ResponseEntity<Object> getClientIdUpload() {
        Map<String, Long> body = new HashMap<>();
        Long id;
        do {
            id = randomizer.nextLong();
        } while (uploadClientMap.containsKey(id));
//        uploadClientMap.put(id, ClientCryptoProgress.builder().state(State.WAITING).build());
//        ExecutorService service = Executors.newFixedThreadPool(1);
//        Long finalId = id;
//        service.submit(() -> {
//           try {
//               Thread.sleep(600000);
//               uploadClientMap.remove(finalId);
//           }
//           catch (InterruptedException e) {
//               e.printStackTrace();
//           }
//        });
//        service.shutdown();
        body.put("clientId", id);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/upload")  // обработка формы загрузки файла
    public ResponseEntity<Object> handleFileUpload(@RequestParam("file") MultipartFile file,
                                                       @RequestParam("mode") String modeStr,
                                                   @RequestParam("clientId") Long clientId) {
        uploadClientMap.put(clientId, ClientCryptoProgress.builder().state(State.WAITING).build());
        Long id = null;
        Map<String, String> endpointResponseBody = new HashMap<>();

        if (!file.isEmpty()) {

            System.out.println("Got file");
            // Шифруем файл, потом отправляем на сервер
            byte[] key = new byte[16];
            Modes mode = Modes.valueOf(modeStr);
            randomizer.nextBytes(key);
            System.out.println("Ключ при отправке = " + (Arrays.toString(key)));
            Magenta magenta = new Magenta(key);
            Crypto crypto = new Crypto(mode, magenta);
            uploadClientMap.get(clientId).setCrypto(crypto);

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

                    PublicKey publicKey = mapper.readValue(node.get("key").asText(), PublicKey.class);

                    System.out.println("id: " + id);
                    System.out.println("p = " + publicKey.p);
                    System.out.println("q = " + publicKey.q);
                    System.out.println("trace = " + publicKey.trace);
                    System.out.println("traceGK = " + publicKey.traceGK);



                    byte[] bytes = new byte[0];
                    uploadClientMap.get(clientId).setState(State.STARTED);
                    try {
                        long fileSize = file.getSize();
                        bytes = crypto.encryptFile(file.getInputStream(), fileSize);
                    } catch (IOException e) {
                        uploadClientMap.get(clientId).setState(State.NOT_FOUND);
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
                    body.add("id", id);
                    body.add("name", file.getOriginalFilename());
                    body.add("traceGB", traceGB);
                    HttpEntity<MultiValueMap<String, Object>> requestEntity
                            = new HttpEntity<>(body, headers);

                    ResponseEntity<String> response
                            = restTemplate.exchange("http://localhost:8080/crypto/upload", HttpMethod.POST, requestEntity, String.class);

                    if (response.getStatusCode() != HttpStatus.OK) {
                        uploadClientMap.get(clientId).setState(State.NOT_FOUND);
                        return new ResponseEntity<>(HttpStatus.OK);
                    }

                    System.out.println(response.getStatusCode());
                    uploadClientMap.get(clientId).setState(State.FINISHED);

                    endpointResponseBody.put("message", "Success");
                }
                else {
                    System.out.println("Не удалось получить ключ");
                    endpointResponseBody.put("message", "Didnt get key");
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            System.out.println("Empty File");
            endpointResponseBody.put("message", "Empty File");
            uploadClientMap.get(clientId).setState(State.NOT_FOUND);
            return new ResponseEntity<>(endpointResponseBody, HttpStatus.OK);
        }
        if (id == null) {
            endpointResponseBody.put("message", "Not Found");
            uploadClientMap.get(clientId).setState(State.FINISHED);
            return new ResponseEntity<>(endpointResponseBody, HttpStatus.OK);

        }

        return new ResponseEntity<>(endpointResponseBody, HttpStatus.OK);
    }

    @GetMapping("/getuploadprogress/{id}")
    public ResponseEntity<Object> getUploadProgress(@PathVariable Long id) {
        System.out.println("UserID in download = " + id);
        Map<String, Object> body = new HashMap<>();
        ClientCryptoProgress clientProgress = uploadClientMap.get(id);
        if (clientProgress == null) {
            System.out.println("Client progress is waiting");
            body.put("progress", 0);
            body.put("state", "WAITING");
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

}
