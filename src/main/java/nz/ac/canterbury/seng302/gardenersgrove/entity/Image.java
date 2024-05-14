package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private String contentType;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] data;

    /**
     * Required constructor
     */
    protected Image() {}

    public Image(Image tempImage) {
        this.data = tempImage.getData();
        this.contentType = tempImage.getContentType();
        this.expiryDate = null;
    }

    public Image(MultipartFile file, boolean isTemporary) throws IOException {
        byte[] imageBytes = file.getBytes();
        String ext = file.getContentType();
        if (ext == null || !ext.startsWith("image")) {
            throw new FileUploadException("Invalid file type");
        } else {
            ext = ext.substring(ext.lastIndexOf('/') + 1);
        }
        this.init(imageBytes, ext, isTemporary, LocalDateTime.now().plusMinutes(10L));
    }

    public Image(byte[] data, String contentType, boolean isTemporary) {
        this(data, contentType, isTemporary, LocalDateTime.now().plusMinutes(10L));
    }

    public Image(byte[] data, String contentType, boolean isTemporary, LocalDateTime temporaryExpiryDate) {
        this.init(data, contentType, isTemporary, temporaryExpiryDate);
    }

    private void init(byte[] data, String contentType, boolean isTemporary, LocalDateTime temporaryExpiryDate) {
        this.data = data;
        this.contentType = contentType;
        this.expiryDate = (isTemporary ? temporaryExpiryDate : null);
    }


    public Long getId() {
        return Id;
    }

    public byte[] getData() {
        return data;
    }

    public String getContentType() {
        return contentType;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
