package ru.polyaeva.DiplomaThesis.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.polyaeva.DiplomaThesis.dto.response.FileRS;
import ru.polyaeva.DiplomaThesis.exception.UnauthorizedException;
import ru.polyaeva.DiplomaThesis.model.User;
import ru.polyaeva.DiplomaThesis.repository.AuthenticationRepository;
import ru.polyaeva.DiplomaThesis.repository.UserRepository;
import ru.polyaeva.DiplomaThesis.service.StorageFileService;
import ru.polyaeva.DiplomaThesis.dto.request.EditFileNameRQ;

import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class StorageFileController {

    private StorageFileService cloudStorageService;
    private AuthenticationRepository authenticationRepository;
    private UserRepository userRepository;

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, MultipartFile file) {
        User user = this.getUserByAuthToken(authToken);
        if (user == null) {
            throw new UnauthorizedException("Delete file: Unauthorized");
        }
        cloudStorageService.uploadFile(user, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        User user = this.getUserByAuthToken(authToken);
        if (user == null) {
            throw new UnauthorizedException("Delete file: Unauthorized");
        }
        cloudStorageService.deleteFile(user, filename);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        User user = this.getUserByAuthToken(authToken);
        if (user == null) {
            throw new UnauthorizedException("Delete file: Unauthorized");
        }
        byte[] file = cloudStorageService.downloadFile(user, filename);
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PutMapping(value = "/file")
    public ResponseEntity<?> editFileName(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename, @RequestBody EditFileNameRQ editFileNameRQ) {
        User user = this.getUserByAuthToken(authToken);
        if (user == null) {
            throw new UnauthorizedException("Delete file: Unauthorized");
        }
        cloudStorageService.editFileName(user, filename, editFileNameRQ);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/list")
    public List<FileRS> getAllFiles(@RequestHeader("auth-token") String authToken, @RequestParam("limit") Integer limit) {
        User user = this.getUserByAuthToken(authToken);
        if (user == null) {
            throw new UnauthorizedException("Delete file: Unauthorized");
        }
        return cloudStorageService.getAllFiles(user, limit);
    }

    private User getUserByAuthToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = authenticationRepository.getUsernameByToken(authTokenWithoutBearer);
            return userRepository.findByUsername(username);
        }
        return null;
    }
}