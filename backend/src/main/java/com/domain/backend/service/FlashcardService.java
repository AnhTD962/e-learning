package com.domain.backend.service;

import com.domain.backend.dto.request.FlashcardRequest;
import com.domain.backend.dto.request.FlashcardSetRequest;
import com.domain.backend.dto.response.FlashcardResponse;
import com.domain.backend.dto.response.FlashcardSetResponse;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.entity.Flashcard;
import com.domain.backend.entity.FlashcardSet;
import com.domain.backend.entity.Lesson;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.FlashcardSetRepository;
import com.domain.backend.repository.LessonRepository;
import com.domain.backend.security.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến quản lý Flashcard Set.
 */
@Service
public class FlashcardService {

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Autowired
    private LessonRepository lessonRepository; // Để liên kết flashcard set với bài học

    /**
     * Chuyển đổi FlashcardSet entity sang FlashcardSetResponse DTO.
     *
     * @param flashcardSet Entity FlashcardSet.
     * @return FlashcardSetResponse DTO.
     */
    private FlashcardSetResponse convertToFlashcardSetResponse(FlashcardSet flashcardSet) {
        FlashcardSetResponse response = new FlashcardSetResponse();
        BeanUtils.copyProperties(flashcardSet, response);
        if (flashcardSet.getFlashcards() != null) {
            response.setFlashcards(flashcardSet.getFlashcards().stream()
                    .map(this::convertToFlashcardResponse) // Gọi phương thức chuyển đổi sang Response DTO
                    .collect(Collectors.toList()));
        } else {
            response.setFlashcards(new ArrayList<>()); // Đảm bảo không trả về null
        }
        return response;
    }

    /**
     * Chuyển đổi Flashcard entity sang FlashcardResponse DTO.
     *
     * @param flashcard Entity Flashcard.
     * @return FlashcardResponse DTO.
     */
    private FlashcardResponse convertToFlashcardResponse(Flashcard flashcard) {
        FlashcardResponse response = new FlashcardResponse();
        BeanUtils.copyProperties(flashcard, response);
        return response;
    }

    /**
     * Chuyển đổi FlashcardRequest DTO sang Flashcard entity.
     *
     * @param flashcardRequest DTO FlashcardRequest.
     * @return Entity Flashcard đã chuyển đổi.
     */
    private Flashcard convertFlashcardRequestToFlashcard(FlashcardRequest flashcardRequest) {
        Flashcard flashcard = new Flashcard();
        BeanUtils.copyProperties(flashcardRequest, flashcard);
        // Đảm bảo rằng nếu có ID được cung cấp cho một flashcard hiện có, nó sẽ được sao chép.
        if (flashcardRequest.getId() != null) {
            flashcard.setId(flashcardRequest.getId());
        }
        return flashcard;
    }

    /**
     * Tạo một bộ flashcard mới và liên kết nó với một bài học.
     *
     * @param lessonId            ID của bài học mà bộ flashcard này thuộc về.
     * @param flashcardSetRequest Payload yêu cầu để tạo bộ flashcard.
     * @return Đối tượng FlashcardSetResponse đã tạo.
     * @throws ResourceNotFoundException nếu không tìm thấy bài học.
     * @throws ValidationException       nếu bài học đã có bộ flashcard liên kết.
     * @throws UnauthorizedException     nếu người dùng không được phép.
     */
    @Transactional // Đảm bảo tính nguyên tử cho các hoạt động liên quan đến nhiều tài liệu
    public FlashcardSetResponse createFlashcardSet(String lessonId, FlashcardSetRequest flashcardSetRequest) {
        // Kiểm tra ủy quyền: chỉ ADMIN hoặc TEACHER mới có thể tạo flashcard set
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép tạo bộ flashcard.");
        }

        // Đảm bảo bài học tồn tại
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học", "id", lessonId));

        // Kiểm tra xem bài học đã có bộ flashcard chưa
        if (lesson.getFlashcardSetId() != null) {
            throw new ValidationException("Bài học " + lessonId + " đã có một bộ flashcard liên kết.");
        }

        FlashcardSet flashcardSet = new FlashcardSet();
        BeanUtils.copyProperties(flashcardSetRequest, flashcardSet);
        flashcardSet.setLessonId(lessonId);

        // Chuyển đổi FlashcardRequest sang đối tượng Flashcard
        if (flashcardSetRequest.getFlashcards() != null && !flashcardSetRequest.getFlashcards().isEmpty()) {
            List<Flashcard> flashcards = flashcardSetRequest.getFlashcards().stream()
                    .map(this::convertFlashcardRequestToFlashcard)
                    .collect(Collectors.toList());
            flashcardSet.setFlashcards(flashcards);
        } else {
            flashcardSet.setFlashcards(new ArrayList<>());
        }

        FlashcardSet savedFlashcardSet = flashcardSetRepository.save(flashcardSet);

        // Cập nhật bài học để liên kết với bộ flashcard mới này
        lesson.setFlashcardSetId(savedFlashcardSet.getId());
        lesson.setUpdatedAt();
        lessonRepository.save(lesson);

        return convertToFlashcardSetResponse(savedFlashcardSet); // Trả về DTO phản hồi
    }

    /**
     * Lấy một bộ flashcard theo ID của nó.
     *
     * @param flashcardSetId ID của bộ flashcard.
     * @return Optional chứa FlashcardSetResponse nếu tìm thấy, rỗng nếu không.
     */
    public Optional<FlashcardSetResponse> getFlashcardSetById(String flashcardSetId) {
        return flashcardSetRepository.findById(flashcardSetId)
                .map(this::convertToFlashcardSetResponse);
    }

    /**
     * Lấy một bộ flashcard theo ID bài học liên quan của nó.
     *
     * @param lessonId ID của bài học.
     * @return Optional chứa FlashcardSetResponse nếu tìm thấy, rỗng nếu không.
     */
    public Optional<FlashcardSetResponse> getFlashcardSetByLessonId(String lessonId) {
        return flashcardSetRepository.findByLessonId(lessonId)
                .map(this::convertToFlashcardSetResponse);
    }

    /**
     * Cập nhật một bộ flashcard hiện có.
     *
     * @param flashcardSetId      ID của bộ flashcard cần cập nhật.
     * @param flashcardSetRequest Payload yêu cầu để cập nhật bộ flashcard.
     * @return Đối tượng FlashcardSetResponse đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy bộ flashcard.
     * @throws UnauthorizedException     nếu người dùng không được phép.
     */
    public FlashcardSetResponse updateFlashcardSet(String flashcardSetId, FlashcardSetRequest flashcardSetRequest) {
        // Kiểm tra ủy quyền: chỉ ADMIN hoặc TEACHER mới có thể cập nhật flashcard set
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật bộ flashcard.");
        }

        FlashcardSet existingFlashcardSet = flashcardSetRepository.findById(flashcardSetId)
                .orElseThrow(() -> new ResourceNotFoundException("Bộ Flashcard", "id", flashcardSetId));

        BeanUtils.copyProperties(flashcardSetRequest, existingFlashcardSet, "id", "createdAt", "lessonId"); // Loại trừ các trường không thay đổi

        if (flashcardSetRequest.getFlashcards() != null) {
            List<Flashcard> updatedFlashcards = flashcardSetRequest.getFlashcards().stream()
                    .map(this::convertFlashcardRequestToFlashcard)
                    .collect(Collectors.toList());
            existingFlashcardSet.setFlashcards(updatedFlashcards);
        } else {
            existingFlashcardSet.setFlashcards(new ArrayList<>());
        }

        existingFlashcardSet.setUpdatedAt();
        return convertToFlashcardSetResponse(flashcardSetRepository.save(existingFlashcardSet));
    }

    /**
     * Xóa một bộ flashcard theo ID của nó.
     *
     * @param flashcardSetId ID của bộ flashcard cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy bộ flashcard.
     * @throws UnauthorizedException     nếu người dùng không được phép.
     */
    @Transactional
    public MessageResponse deleteFlashcardSet(String flashcardSetId) {
        // Kiểm tra ủy quyền: chỉ ADMIN hoặc TEACHER mới có thể xóa flashcard set
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép xóa bộ flashcard.");
        }

        FlashcardSet existingFlashcardSet = flashcardSetRepository.findById(flashcardSetId)
                .orElseThrow(() -> new ResourceNotFoundException("Bộ Flashcard", "id", flashcardSetId));

        // Xóa flashcardSetId khỏi bài học liên quan
        Optional<Lesson> optionalLesson = lessonRepository.findById(existingFlashcardSet.getLessonId());
        if (optionalLesson.isPresent()) {
            Lesson lesson = optionalLesson.get();
            lesson.setFlashcardSetId(null); // Hủy liên kết flashcard set khỏi bài học
            lesson.setUpdatedAt();
            lessonRepository.save(lesson);
        }

        flashcardSetRepository.delete(existingFlashcardSet);
        return new MessageResponse("Bộ Flashcard đã xóa thành công!");
    }
}
