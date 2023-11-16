package server.cryptoserver.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@Table(name = "filekey")
public class RecordModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private byte[] file;
    private String name;

    private String _key;
    private String mode;
    private String IV;
    private Float size;
    private Date _date;

    public RecordModel(byte[] file, String name, String key, String mode, String IV) {
        this.file = file;
        this._key = key;
        this.name = name;
        this.mode = mode;
        this.IV = IV;
        this.size = file.length/1024f;
        this._date = new Date();
    }

}
