package server.cryptoserver.controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import server.cryptoserver.cryptoAlgorithms.GFP2Element;
import server.cryptoserver.cryptoAlgorithms.PublicKey;
import server.cryptoserver.cryptoAlgorithms.XTR;
import server.cryptoserver.models.RecordModel;
import server.cryptoserver.repo.RecordRepository;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping(path = "/crypto")
public class CryptoController {
    private Map<Long, XTR> usersCiphers = new ConcurrentHashMap<>();
    private Random randomizer = new Random(LocalDateTime.now().getNano());
    @Autowired
    ObjectMapper mapper;

    private RecordRepository recordRepository;
    public CryptoController(@Autowired RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public class FileInfo {
        public String name;
        public long id;
        public FileInfo(RecordModel record) {
            this.name = record.getName();
            this.id = record.getId();
        }
    }

    @GetMapping("/getkey")
    public ResponseEntity<Object> getKey() throws JsonProcessingException {
        // генерируем ключи: Public + Private ключи XTR
        // У юзера есть ID сессии, нужно запомнить, что мы ему выдали
        // Получили файл с ключом, тогда обнуляем паблик ключ
        XTR xtr = new XTR(128);
        PublicKey publicKey = xtr.getPublicKey();
        // Генерим id сессии
        long sessionId;
        do {
            sessionId = randomizer.nextLong();
        } while (usersCiphers.containsKey(sessionId));
        usersCiphers.put(sessionId, xtr);
        Map<String, Object> map = new HashMap<>();
        map.put("id", sessionId);
        map.put("key", mapper.writeValueAsString(publicKey));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/setkey") // Первый этап общения в случае запроса файла
    public ResponseEntity<Object> setKey(@RequestParam("key") String key, @RequestParam("id") Long id, @RequestParam("clientId") Long clientId) throws JsonProcessingException {
        System.out.println("Постучались");
        PublicKey publicKey = mapper.readValue(key, PublicKey.class);
        RecordModel myRecord = recordRepository.findById(id).orElse(null);
        if (myRecord == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        byte[] symmetricKey = myRecord.get_key().getBytes(StandardCharsets.ISO_8859_1);

        BigInteger b = XTR.generateB(publicKey);
        GFP2Element traceGB = XTR.generateTraceGB(publicKey, b);
        byte[] encKey = XTR.encryptKey(symmetricKey, publicKey, b);
        byte[] encIV = XTR.encryptKey(myRecord.getIV().getBytes(StandardCharsets.ISO_8859_1), publicKey, b);
        System.out.println("KEYYY -- "+Arrays.toString(myRecord.get_key().getBytes(StandardCharsets.ISO_8859_1)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ByteArrayResource resource = new ByteArrayResource(myRecord.getFile()) {
            @Override
            public String getFilename() {
                return myRecord.getName();
            }
        };
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", new String(encKey, StandardCharsets.ISO_8859_1));
        body.add("file", resource);
        body.add("IV", new String(encIV, StandardCharsets.ISO_8859_1));
        body.add("mode", myRecord.getMode());
        body.add("clientId", clientId);
        body.add("traceGB", traceGB);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        var response = restTemplate.postForEntity("http://localhost:8081/decryptfile", requestEntity, Object.class);
        System.out.println(response.getStatusCode());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getfiles")
    public ResponseEntity<Object> getFiles() {
        var files = recordRepository.findAllFields();
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam("key") String key,
                                         @RequestParam("mode") String mode,
                                         @RequestParam("IV") String IV,
                                         @RequestParam("traceGB") String traceGB,
                                         @RequestParam("name") String name,
                                         @RequestParam("id") Long id) throws IOException {
        System.out.println("traceGB: " + traceGB);
        GFP2Element traceGB2 = mapper.readValue(traceGB, GFP2Element.class);
        System.out.println("name: " + name);
        System.out.println("file: " + file.isEmpty());
        System.out.println("key: " + key);
        System.out.println("Пришёл ключ = " + Arrays.toString(key.getBytes(StandardCharsets.ISO_8859_1)));
        System.out.println("Пришёл IV = " + Arrays.toString(IV.getBytes(StandardCharsets.ISO_8859_1)));
//        recordRepository.save(record);
        System.out.println("saved File");
        if (usersCiphers.containsKey(id)) {
            XTR xtr = usersCiphers.get(id);
            usersCiphers.remove(id);
            byte[] decryptedKey = xtr.decryptKey(key.getBytes(StandardCharsets.ISO_8859_1), traceGB2);
            System.out.println("Ключ расшифрован и получен" + Arrays.toString(decryptedKey));
            byte[] decryptedIV = xtr.decryptKey(IV.getBytes(StandardCharsets.ISO_8859_1), traceGB2);
            System.out.println(Arrays.toString(decryptedKey));
            try {
                recordRepository.save(new RecordModel(file.getBytes(),
                        name, new String(decryptedKey, StandardCharsets.ISO_8859_1),
                        mode,  new String(decryptedIV, StandardCharsets.ISO_8859_1)));
            }
            catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deletefile/{id}")
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id) {
        recordRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
