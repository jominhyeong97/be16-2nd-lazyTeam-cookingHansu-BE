package lazyteam.cooking_hansu.domain.lecture.controller;


import jakarta.validation.Valid;
import lazyteam.cooking_hansu.domain.lecture.dto.lecture.*;
import lazyteam.cooking_hansu.domain.lecture.service.LectureService;
import lazyteam.cooking_hansu.global.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("lecture")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

//    @PreAuthorize("hasAnyRole('CHEF', 'OWNER')")
    @PostMapping("/post")
    public ResponseEntity<?> create(@Valid @RequestPart LectureCreateDto lectureCreateDto,
                                    @RequestPart List<LectureIngredientsListDto> lectureIngredientsListDto,
                                    @RequestPart List<LectureStepDto> lectureStepDto,
                                    @RequestPart List<LectureVideoDto> lectureVideoDto,
                                    @RequestPart List<MultipartFile> lectureVideoFiles,
                                    @RequestPart MultipartFile multipartFile) {
        UUID lectureId = lectureService.create(lectureCreateDto, lectureIngredientsListDto,lectureStepDto,lectureVideoDto,lectureVideoFiles, multipartFile);

        return new ResponseEntity<>(ResponseDto.ok("강의등록번호 : " + lectureId,HttpStatus.CREATED), HttpStatus.CREATED);
    }

//    @PreAuthorize("hasAnyRole('CHEF', 'OWNER') or hasRole('ADMIN')")
    @PatchMapping(value = "/update/{lectureId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateLecture(
            @PathVariable UUID lectureId,
            @RequestPart("lectureUpdateDto") LectureUpdateDto lectureUpdateDto,
            @RequestPart("lectureIngredientsListDto") List<LectureIngredientsListDto> lectureIngredientsListDto,
            @RequestPart("lectureStepDto") List<LectureStepDto> lectureStepDto,
            @RequestPart("lectureVideoDto") List<LectureVideoDto> lectureVideoDto,
            @RequestPart(value = "lectureVideoFiles", required = false) List<MultipartFile> lectureVideoFiles, // 파일 선택
            @RequestPart(value = "multipartFile",    required = false) MultipartFile multipartFile             // 썸네일 선택
    ) {
        UUID lectureID = lectureService.update( lectureUpdateDto, lectureId, lectureIngredientsListDto,lectureStepDto,lectureVideoDto,lectureVideoFiles, multipartFile);

        return new ResponseEntity<>(ResponseDto.ok("수정된 강의번호 : " + lectureID,HttpStatus.OK), HttpStatus.OK);
    }

//    강의 목록조회(delyn 적용, 강의 영상과 재료, 순서까지 일괄 조회되게끔)
    @GetMapping("/list")
    public ResponseEntity<?> findAllLecture(@PageableDefault(size = 8, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LectureResDto> lectureResDto = lectureService.findAllLecture(pageable);
        return new ResponseEntity<>(ResponseDto.ok(lectureResDto,HttpStatus.OK),HttpStatus.OK);
    }

//    강의 상세조회
    @GetMapping("/detail/{lectureId}")
    public ResponseEntity<?> findDetailLecture(@PathVariable UUID lectureId) {
        LectureDetailDto detailDto = lectureService.findDetailLecture(lectureId);
        return new ResponseEntity<>(ResponseDto.ok(detailDto,HttpStatus.OK),HttpStatus.OK);
    }

//    내가 업로드 한 강의목록 조회(판매자)
    @PreAuthorize("hasAnyRole('CHEF', 'OWNER')")
    @GetMapping("/mylist")
    public ResponseEntity<?> findAllMyLecture(@PageableDefault(size = 8, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {
        Page<LectureResDto> lectureResDtos = lectureService.findAllMyLecture(pageable);
        return new ResponseEntity<>(ResponseDto.ok(lectureResDtos,HttpStatus.OK),HttpStatus.OK);

    }

//    강의 삭제
    @PreAuthorize("hasAnyRole('CHEF', 'OWNER') or hasRole('ADMIN')")
    @DeleteMapping("/delete/{lectureId}")
    public ResponseEntity<?> deleteLecture(@PathVariable UUID lectureId) {
        lectureService.deleteLecture(lectureId);
        return new ResponseEntity<>(ResponseDto.ok("강의가 삭제되었습니다.", HttpStatus.OK),HttpStatus.OK);
    }

    // 영상 시청 진행도 업데이트
    @PostMapping("/progress/{videoId}")
    public ResponseEntity<?> updateProgress(@PathVariable UUID videoId,
                                            @RequestParam int second) {
        UUID progressId = lectureService.updateProgress(videoId, second);
        return new ResponseEntity<>(
                ResponseDto.ok("진행도 저장 id=" + progressId, HttpStatus.OK),
                HttpStatus.OK
        );
    }



}
