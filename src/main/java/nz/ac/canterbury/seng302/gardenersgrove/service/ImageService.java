package nz.ac.canterbury.seng302.gardenersgrove.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Image;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.ImageRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class ImageService {
    private Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Optional<Image> findImage(Long id) { return imageRepository.findById(id); }

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Image saveImage(Image image) {
        if (image.getExpiryDate() != null) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    cleanupExpiredTemporaries();
                }
            }, 300000);
        }
        return imageRepository.save(image);
    }

    public void cleanupExpiredTemporaries() {
        logger.info("Cleaning up expired temporary images");
        imageRepository.deleteAllExpiredTemporaries(LocalDateTime.now());
    }

    public void deleteImage(Image image) {
        imageRepository.delete(image);
    }

}
