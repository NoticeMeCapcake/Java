package server.cryptoserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import server.cryptoserver.models.SaltWrapper;
import server.cryptoserver.repo.RecordRepository;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequestMapping(path = "/crypto")
public class CryptoController {
    private Map<Long, XTR> usersCiphers = new ConcurrentHashMap<>();
    private static final String UPLOADS_DIR = "D:\\Danon\\uploads\\";
    private Random randomizer = new Random(LocalDateTime.now().getNano());
    @Autowired
    ObjectMapper mapper;

    private RecordRepository recordRepository;
    public CryptoController(@Autowired RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
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
    public ResponseEntity<Object> setKey(@RequestParam("key") String key,
                                         @RequestParam("id") Long id,
                                         @RequestParam("clientId") Long clientId)
            throws JsonProcessingException {
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

        Path path = Paths.get(UPLOADS_DIR + myRecord.getName() + "." + myRecord.getSalt());
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        }
        catch (IOException e) {
            log.error(Arrays.toString(e.getStackTrace()));
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ByteArrayResource resource = new ByteArrayResource(bytes) {
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

    private boolean anyOfAll(List<SaltWrapper> salts, long salt) {
        return salts.stream().anyMatch(s -> s.salt == salt);
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
        System.out.println("saved File");
        if (usersCiphers.containsKey(id)) {
            long salt;
            do {
                salt = randomizer.nextLong();
            } while (anyOfAll(recordRepository.findAllSalt(), salt));
            Path path = Paths.get(UPLOADS_DIR + file.getOriginalFilename() + "." + salt);
            log.info("Transferring file {}", file.getOriginalFilename());
            file.transferTo(path.toFile());
            log.info("Transferred file {}", file.getOriginalFilename());
            XTR xtr = usersCiphers.get(id);
            usersCiphers.remove(id);
            byte[] decryptedKey = xtr.decryptKey(key.getBytes(StandardCharsets.ISO_8859_1), traceGB2);
            System.out.println("Ключ расшифрован и получен" + Arrays.toString(decryptedKey));
            byte[] decryptedIV = xtr.decryptKey(IV.getBytes(StandardCharsets.ISO_8859_1), traceGB2);
            System.out.println(Arrays.toString(decryptedKey));
            try {
                recordRepository.save(new RecordModel(salt, (float)file.getSize() / 1024,
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
    public ResponseEntity<Object> deleteFile(@PathVariable("id") Long id)  {
        var optRecord = recordRepository.findById(id);
        if (optRecord.isPresent()) {
            recordRepository.deleteById(id);
            var presentRecord = optRecord.get();
            var path = Paths.get(UPLOADS_DIR + presentRecord.getName() + "." + presentRecord.getSalt());
            try {
                Files.delete(path);
            } catch (IOException e) {
                log.warn(Arrays.toString(e.getStackTrace()));
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
