package server.cryptoserver.models;

import lombok.AllArgsConstructor;

import java.util.Date;

//@AllArgsConstructor
public class FileShortcut {
    public Long id;
    public String name;
    public String mode;
    public Float size;
    public Date _date;

    public FileShortcut(Long id, String name, String mode, Float size, Date date) {
        this.id = id;
        this.name = name;
        this.mode = mode;
        this.size = size;
        this._date = date;
    }
}
