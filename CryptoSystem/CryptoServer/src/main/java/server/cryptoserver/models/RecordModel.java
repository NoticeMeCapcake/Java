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
    private long salt;
    private String name;

    private String _key;
    private String mode;
    private String IV;
    private Float size;
    private Date _date;

    public RecordModel(long salt, float size, String name, String key, String mode, String IV) {
        this.salt = salt;
        this._key = key;
        this.name = name;
        this.mode = mode;
        this.IV = IV;
        this.size = size;
        this._date = new Date();
    }

}
