package com.threeatom.guidecore.controller.api;

import com.threeatom.guidecore.dto.request.AssignCourseDto;
import com.threeatom.guidecore.dto.response.ContentGroupCourseAssignmentDto;
import com.threeatom.guidecore.service.GcContentGroupCourseAssignmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/api/v2/content-groups",
        produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ContentGroupCourseAssignmentController {

    private final GcContentGroupCourseAssignmentService gcContentGroupCourseAssignmentService;

    @GetMapping("/{content-group-id}/course-assignments")
    public ResponseEntity<List<ContentGroupCourseAssignmentDto>> getCourseAssignment(
            @PathVariable("content-group-id") Integer contentGroupId) {
        return ResponseEntity.ok().body(gcContentGroupCourseAssignmentService.findByContentGroupId(contentGroupId));
    }

    @PostMapping("/course/assign")
    public ResponseEntity<Void> assignCourse(@RequestBody AssignCourseDto assignCourseDto) {
        gcContentGroupCourseAssignmentService.assignCourse(assignCourseDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
