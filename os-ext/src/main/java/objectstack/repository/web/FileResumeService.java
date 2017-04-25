package objectstack.repository.web;

import objectstack.api.StorageService;
import objectstack.repository.impl.StorageFileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/drive/v1")
public class FileResumeService {

    private final StorageService storageService;

    @Autowired
    public FileResumeService(StorageService storageService) {
        this.storageService = storageService;
    }

    /*
     * 请求断点续传
		POST https://www.googleapis.com/drive/v3/files?uploadType=resumable HTTP/1.1
		Authorization: Bearer [YOUR_AUTH_TOKEN]
		Content-Length: 38
		Content-Type: application/json; charset=UTF-8
		X-Upload-Content-Type: image/jpeg
		X-Upload-Content-Length: 2000000
		 
		{
		  "name": "myObject"
		}
	 * 服务端响应
		HTTP/1.1 200 OK
		Location: https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable&upload_id=xa298sd_sdlkj2
		Content-Length: 0
     */
    @PostMapping("/files")
    public String listUploadedFiles(@RequestParam("upload_type") String uploadType) throws IOException {

        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    /*
     * 获取断点续传的偏移值
        PUT https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable&upload_id=xa298sd_sdlkj2 HTTP/1.1
		Content-Length: 0
		Content-Range: bytes *\/2000000
	 * 微服务端响应
		HTTP/1.1 308 Resume Incomplete
		Content-Length: 0
		Range: bytes=0-42
     * 然后，进行断点续传
		PUT https://www.googleapis.com/upload/drive/v3/files?uploadType=resumable&upload_id=xa298sd_sdlkj2 HTTP/1.1
		Content-Length: 1999957
		Content-Range: bytes 43-1999999/2000000
		
		[BYTES 43-1999999]
     */
    @PutMapping("/files")
    @CrossOrigin(origins = "http://localhost:9000")
    public String handleFileUpload(@RequestParam("upload_type") String uploadType, @RequestParam("upload_id") String uploadId,
                                   RedirectAttributes redirectAttributes) {

    	for (MultipartFile file: files) {
            storageService.store(file);
            System.out.println("file uploaded: " + file.getOriginalFilename());
    	}
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + files.length + "!");

        return "redirect:/storage";
    }

    @GetMapping("/files")
    public @ResponseBody ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}