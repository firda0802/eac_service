package com.eac.controller;

import com.eac.entity.Assignment;
import com.eac.entity.AssignmentCollection;
import com.eac.entity.Course;
import com.eac.entity.MasterUser;
import com.eac.messages.Messages;
import com.eac.messages.ReqGantiPassword;
import com.eac.messages.RespAssignmentStudent;
import com.eac.messages.RespDashboard;
import com.eac.repository.AssignmentCollectionRepository;
import com.eac.repository.AssignmentRepository;
import com.eac.repository.CourseRepository;
import com.eac.repository.MasterUserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feature")
public class FeatureController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Value("${upload-dir}")
    private String uploadDir;

    @Autowired
    private MasterUserRepository masterUserRepository;

    @Autowired
    private AssignmentCollectionRepository assignmentCollectionRepository;

    @GetMapping(value = "/list-course")
    public ResponseEntity<Messages> listCourse() {

        Messages resp = new Messages();
        resp.success();
        resp.setData(courseRepository.findByIsDeleted(false));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/list-course/{id}")
    public ResponseEntity<Messages> listCourseByUseer(@PathVariable(name = "id") Integer id) {

        Messages resp = new Messages();
        resp.success();
        resp.setData(courseRepository.findByIsDeletedAndIdTeacher(false, id));
        return ResponseEntity.ok().body(resp);
    }


    @PostMapping("/create-course")
    public ResponseEntity<Messages> createCourse(
            @RequestParam("files") MultipartFile file,
            @RequestParam("nameCourse") String courseName,
            @RequestParam("teacher") int teacher,
            @RequestParam("description") String description
    ) throws IOException {
        Messages resp = new Messages();
        Course course = new Course();
        if (file.isEmpty()) {
            course.setLearningMaterials("");
        } else {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String generatedFileName = "Course-" + LocalDate.now() + "-" + RandomStringUtils.randomAlphanumeric(10) + fileExtension;

            Path fileStorageLocation = Paths.get(uploadDir)
                    .toAbsolutePath().normalize();
            Path targetLocation = fileStorageLocation.resolve(generatedFileName);

            Files.copy(file.getInputStream(), targetLocation);

            String fileDownloadUri = ServletUriComponentsBuilder.fromUriString("http://localhost:8080")
                    .path("/downloadFile/")
                    .path(generatedFileName)
                    .toUriString();
            course.setLearningMaterials(fileDownloadUri);
        }


        course.setNameCourse(courseName);
        course.setDescription(description);
        course.setIdTeacher(teacher);
        course.setIsDeleted(false);
        courseRepository.save(course);

        resp.success();
        return ResponseEntity.ok().body(resp);
    }

    @PutMapping(value = "/update-course/{id}")
    public ResponseEntity<Messages> updateCourse(
            @PathVariable(name = "id") Integer id,
            @RequestParam(value = "files", required = false) MultipartFile file,
            @RequestParam("nameCourse") String courseName,
            @RequestParam("teacher") int teacher,
            @RequestParam("description") String description
    ) {
        Messages resp = new Messages();
        Optional<Course> cekData = courseRepository.findById(id);

        if (cekData.isPresent()) {
            Course course = cekData.get();
            course.setNameCourse(courseName);
            course.setDescription(description);
            course.setIdTeacher(teacher);

            // Cek apakah berkas tidak kosong
            if (file != null && !file.isEmpty()) {
                // Menghasilkan nama berkas baru dengan format yang diinginkan dan mempertahankan ekstensinya
                String originalFileName = file.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String generatedFileName = "COURSE-" + LocalDate.now() + "-" + RandomStringUtils.randomAlphanumeric(10) + fileExtension;

                // Simpan berkas dengan nama yang baru
                try {
                    Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
                    Path targetLocation = fileStorageLocation.resolve(generatedFileName);
                    Files.copy(file.getInputStream(), targetLocation);

                    // Set URL berkas yang baru di objek course
                    String fileDownloadUri = ServletUriComponentsBuilder.fromUriString("http://localhost:8080")
                            .path("/downloadFile/")
                            .path(generatedFileName)
                            .toUriString();
                    course.setLearningMaterials(fileDownloadUri);
                } catch (IOException e) {
                    resp.failed("Gagal mengunggah atau menyimpan berkas.");
                    return ResponseEntity.badRequest().body(resp);
                }
            }

            courseRepository.saveAndFlush(course);
            resp.success();
            return ResponseEntity.ok().body(resp);
        } else {
            resp.failed("Id tidak ditemukan");
            return ResponseEntity.ok().body(resp);
        }
    }


    @DeleteMapping(value = "/delete-course/{id}")
    public ResponseEntity<Messages> deleteClass(@PathVariable(name = "id") Integer id) {
        Messages resp = new Messages();
        Optional<Course> cekData = courseRepository.findById(id);
        if (cekData.isPresent()) {
            Course cls = cekData.get();
            cls.setIsDeleted(true);
            courseRepository.saveAndFlush(cls);
            resp.success();
        } else {
            resp.failed("Id tidak ditemukan");
        }

        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/list-assignment/{id}")
    public ResponseEntity<Messages> listAssignment(@PathVariable(name = "id") int id) {

        Messages resp = new Messages();
        resp.success();
        if (id > 0) {
            resp.setData(assignmentRepository.findByIdCourseAndIsDeleted(id, false));
        } else {
            resp.setData(assignmentRepository.findByIsDeleted(false));
        }
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/assignment-student/{id}")
    public ResponseEntity<Messages> listAssignmentStudent(@PathVariable(name = "id") int id) {

        Messages resp = new Messages();
        resp.success();
        List<RespAssignmentStudent> respAssignmentStudent = new ArrayList<>();
        List<Assignment> list = assignmentRepository.findByIsDeleted(false);
        if (!list.isEmpty()) {
            for (Assignment asg : list) {
                RespAssignmentStudent r = new RespAssignmentStudent();
                r.setAssignment(asg);
                AssignmentCollection cek = assignmentCollectionRepository.findByIdAssignmentAndIdUser(asg.getIdAssignment(), id);
                if (cek == null) {
                    r.setStatus("pending");
                } else {
                    r.setStatus("completed");
                }
                respAssignmentStudent.add(r);
            }
        }
        resp.setData(respAssignmentStudent);
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/assignment-score/{id}")
    public ResponseEntity<Messages> listAssignmentStudentScore(@PathVariable(name = "id") int id) {

        Messages resp = new Messages();
        resp.success();
        resp.setData(assignmentCollectionRepository.findByIdUser(id));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/assignment-score-teach/{id}")
    public ResponseEntity<Messages> listAssignmentStudentScoreTeacher(@PathVariable(name = "id") int id) {

        Messages resp = new Messages();
        resp.success();
        resp.setData(assignmentCollectionRepository.getByDesc(id));
        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/assignment-teacher/{id}")
    public ResponseEntity<Messages> listAssignmentByTeacher(@PathVariable(name = "id") int id) {

        Messages resp = new Messages();
        resp.success();
        resp.setData(assignmentRepository.getByDesc(id, false));
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/create-assignment")
    public ResponseEntity<Messages> createAssignment(
            @RequestParam("files") MultipartFile file,
            @RequestParam("idCourse") int idCourse,
            @RequestParam("title") String title,
            @RequestParam("deadline") String deadline
    ) throws IOException {
        Messages resp = new Messages();

        Assignment assignment = new Assignment();
        if (file.isEmpty()) {
            assignment.setAssignmentFile("");
        } else {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String generatedFileName = "Assignment-" + LocalDate.now() + "-" + RandomStringUtils.randomAlphanumeric(10) + fileExtension;

            Path fileStorageLocation = Paths.get(uploadDir)
                    .toAbsolutePath().normalize();
            Path targetLocation = fileStorageLocation.resolve(generatedFileName);

            Files.copy(file.getInputStream(), targetLocation);

            String fileDownloadUri = ServletUriComponentsBuilder.fromUriString("http://localhost:8080")
                    .path("/downloadFile/")
                    .path(generatedFileName)
                    .toUriString();
            assignment.setAssignmentFile(fileDownloadUri);
        }

        assignment.setIdCourse(idCourse);
        assignment.setTitle(title);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(deadline, formatter);
        assignment.setDeadline(dateTime);
        assignment.setIsDeleted(false);
        assignmentRepository.save(assignment);

        resp.success();
        return ResponseEntity.ok().body(resp);
    }

    @PutMapping("/update-assignment/{id}")
    public ResponseEntity<Messages> updateAssignment(
            @PathVariable(name = "id") Integer id,
            @RequestParam(value = "files", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("idCourse") int idCourse,
            @RequestParam("deadline") String deadline
    ) throws IOException {
        Messages resp = new Messages();
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);

        if (assignmentOptional.isPresent()) {
            Assignment assignment = assignmentOptional.get();

            // Cek apakah berkas tidak kosong
            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String generatedFileName = "Assignment-" + LocalDate.now() + "-" + RandomStringUtils.randomAlphanumeric(10) + fileExtension;

                Path fileStorageLocation = Paths.get(uploadDir)
                        .toAbsolutePath().normalize();
                Path targetLocation = fileStorageLocation.resolve(generatedFileName);

                Files.copy(file.getInputStream(), targetLocation);

                String fileDownloadUri = ServletUriComponentsBuilder.fromUriString("http://localhost:8080")
                        .path("/downloadFile/")
                        .path(generatedFileName)
                        .toUriString();

                assignment.setAssignmentFile(fileDownloadUri);
            }

            assignment.setTitle(title);
            assignment.setIdCourse(idCourse);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(deadline, formatter);
            assignment.setDeadline(dateTime);

            assignmentRepository.save(assignment);
            resp.success();
            return ResponseEntity.ok().body(resp);
        } else {
            resp.failed("Assignment dengan ID " + id + " tidak ditemukan.");
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping(value = "/delete-assignment/{id}")
    public ResponseEntity<Messages> deleteAssignment(@PathVariable(name = "id") Integer id) {
        Messages resp = new Messages();
        Optional<Assignment> cekData = assignmentRepository.findById(id);
        if (cekData.isPresent()) {
            Assignment assignment = cekData.get();
            assignment.setIsDeleted(true);
            assignmentRepository.saveAndFlush(assignment);
            resp.success();
        } else {
            resp.failed("Id tidak ditemukan");
        }

        return ResponseEntity.ok().body(resp);
    }

    @GetMapping(value = "/dashboard")
    public ResponseEntity<Messages> dashboard() {

        Messages resp = new Messages();
        resp.success();
        RespDashboard respDashboard = new RespDashboard();
        respDashboard.setJumlahStudent(masterUserRepository.findByIdRoleAndIsDeleted(3, false).size());
        respDashboard.setJumlahTeacher(masterUserRepository.findByIdRoleAndIsDeleted(2, false).size());
        respDashboard.setJumlahCourse(courseRepository.findByIsDeleted(false).size());
        respDashboard.setJumlahAssignment(assignmentRepository.findByIsDeleted(false).size());
        resp.setData(respDashboard);
        return ResponseEntity.ok().body(resp);
    }

    @PostMapping("/submit-assignment")
    public ResponseEntity<Messages> submitAssignment(
            @RequestParam("files") MultipartFile file,
            @RequestParam("idAssignment") int idAssignment,
            @RequestParam("idUser") int idUser
    ) throws IOException {
        Messages resp = new Messages();

        AssignmentCollection assignment = new AssignmentCollection();
        if (file.isEmpty()) {
            resp.failed("File tidak dikirim");
        } else {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String generatedFileName = "Jawaban-" + LocalDate.now() + "-" + RandomStringUtils.randomAlphanumeric(10) + fileExtension;

            Path fileStorageLocation = Paths.get(uploadDir)
                    .toAbsolutePath().normalize();
            Path targetLocation = fileStorageLocation.resolve(generatedFileName);

            Files.copy(file.getInputStream(), targetLocation);

            String fileDownloadUri = ServletUriComponentsBuilder.fromUriString("http://localhost:8080")
                    .path("/downloadFile/")
                    .path(generatedFileName)
                    .toUriString();
            assignment.setSubmitFile(fileDownloadUri);
            assignment.setIdAssignment(idAssignment);
            assignment.setIdUser(idUser);
            assignment.setSubmitAssignment(LocalDateTime.now());
            assignmentCollectionRepository.save(assignment);
            resp.success();
        }


        return ResponseEntity.ok().body(resp);
    }

    @PutMapping(value = "/assignment-score-submit/{id}")
    public ResponseEntity<Messages> assignmentScoreSubmit(@PathVariable(name = "id") Integer id, @RequestBody AssignmentCollection req) {
        Messages resp = new Messages();
        Optional<AssignmentCollection> cekData = assignmentCollectionRepository.findById(id);
        if (cekData.isPresent()) {
            AssignmentCollection ass = cekData.get();
            ass.setScore(req.getScore());
            assignmentCollectionRepository.saveAndFlush(ass);
            resp.success();
        } else {
            resp.failed("ID tidak ditemukan");
        }

        return ResponseEntity.ok().body(resp);
    }

}
