package nz.ac.canterbury.seng302.gardenersgrove.entity;

import jakarta.persistence.*;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
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
    public Image() {}

    /**
     * Constructor for an image from a file that has been picked.
     * @param file The file that has been picked
     * @param isTemporary Whether the image is temporary or not
     * @throws IOException If the file cannot be read
     */
    public Image(MultipartFile file, boolean isTemporary) throws IOException {
        byte[] imageBytes = file.getBytes();
        String ext = file.getContentType();
        if (ext == null || !ext.startsWith("image")) {
            throw new FileUploadException("Invalid file type");
        } else {
            ext = ext.substring(ext.lastIndexOf('/') + 1);
        }
        this.init(imageBytes, ext, isTemporary, LocalDateTime.now().plusMinutes(5L));
    }

    public Image(byte[] data, String contentType, boolean isTemporary) {
        this(data, contentType, isTemporary, LocalDateTime.now().plusMinutes(5L));
    }

    public Image(byte[] data, String contentType, boolean isTemporary, LocalDateTime temporaryExpiryDate) {
        this.init(data, contentType, isTemporary, temporaryExpiryDate);
    }

    private void init(byte[] data, String contentType, boolean isTemporary, LocalDateTime temporaryExpiryDate) {
        this.data = data;
        this.contentType = contentType;
        this.expiryDate = (isTemporary ? temporaryExpiryDate : null);
    }

    public static Image getTemporaryImage(HttpSession session) {
        return (Image) session.getAttribute("temporaryImage");
    }

    public static void setTemporaryImage(HttpSession session, Image image) {
        session.setAttribute("temporaryImage", image);
    }

    public static Image removeTemporaryImage(HttpSession session, ImageService imageService) {
        Image temporaryImage = getTemporaryImage(session);
        if (temporaryImage != null) {
            session.removeAttribute("temporaryImage");
            imageService.deleteImage(temporaryImage);
        }
        return temporaryImage;
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

    /**
     * Make the image permanent by removing its expiry date and old ID.
     * @return The same image
     */
    public Image makePermanent() {
        this.Id = null;
        this.expiryDate = null;
        return this;
    }
}
