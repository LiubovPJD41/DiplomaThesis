package ru.polyaeva.DiplomaThesis.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.polyaeva.DiplomaThesis.dto.request.EditFileNameRQ;
import ru.polyaeva.DiplomaThesis.dto.response.FileRS;
import ru.polyaeva.DiplomaThesis.exception.InputDataException;
import ru.polyaeva.DiplomaThesis.exception.UnauthorizedException;
import ru.polyaeva.DiplomaThesis.model.StorageFile;
import ru.polyaeva.DiplomaThesis.repository.StorageFileRepository;
import ru.polyaeva.DiplomaThesis.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StorageFileService {
    private final Logger log = LoggerFactory.getLogger(StorageFileService.class);
    private StorageFileRepository storageFileRepository;

    public boolean uploadFile(User user, String filename, MultipartFile file) {
        if (user == null) {
            log.error("Upload file: Unauthorized");
            throw new UnauthorizedException("Upload file: Unauthorized");
        }

        try {
            storageFileRepository.save(new StorageFile(filename, LocalDateTime.now(), file.getSize(), file.getBytes(), user));
            log.info("Success upload file. User {}", user.getUsername());
            return true;
        } catch (IOException e) {
            log.error("Upload file: Input data exception");
            throw new InputDataException("Upload file: Input data exception");
        }
    }

    @Transactional
    public void deleteFile(User user, String filename) {
        storageFileRepository.deleteByUserAndFilename(user, filename);

        final StorageFile tryingToGetDeletedFile = storageFileRepository.findByUserAndFilename(user, filename);
        if (tryingToGetDeletedFile != null) {
            log.error("Delete file: Input data exception");
            throw new InputDataException("Delete file: Input data exception");
        }
        log.info("Success delete file. User {}", user.getUsername());
    }

    public byte[] downloadFile(User user, String filename) {
        final StorageFile file = storageFileRepository.findByUserAndFilename(user, filename);
        if (file == null) {
            log.error("Download file: Input data exception");
            throw new InputDataException("Download file: Input data exception");
        }
        log.info("Success download file. User {}", user.getUsername());
        return file.getFileContent();
    }

    @Transactional
    public void editFileName(User user, String filename, EditFileNameRQ editFileNameRQ) {
        storageFileRepository.editFileNameByUser(user, filename, editFileNameRQ.getFilename());

        final StorageFile fileWithOldName = storageFileRepository.findByUserAndFilename(user, filename);
        if (fileWithOldName != null) {
            log.error("Edit file name: Input data exception");
            throw new InputDataException("Edit file name: Input data exception");
        }
        log.info("Success edit file name. User {}", user.getUsername());
    }

    public List<FileRS> getAllFiles(User user, long limit) {
        log.info("Success get all files. User {}", user.getUsername());
        return storageFileRepository.findAllByUser(user).stream()
                .limit(limit)
                .map(o -> new FileRS(o.getFilename(), o.getSize()))
                .collect(Collectors.toList());
    }
}