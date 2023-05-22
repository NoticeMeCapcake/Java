package server.cryptoserver.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    public RecordModel(byte[] file, String name, String key, String mode, String IV) {
        this.file = file;
        this._key = key;
        this.name = name;
        this.mode = mode;
        this.IV = IV;
    }

}
