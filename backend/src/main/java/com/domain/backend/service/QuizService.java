package com.domain.backend.service;

import com.domain.backend.dto.request.*;
import com.domain.backend.dto.response.MessageResponse;
import com.domain.backend.dto.response.QuestionResponse;
import com.domain.backend.dto.response.QuizAttemptResponse;
import com.domain.backend.dto.response.QuizResponse;
import com.domain.backend.entity.*;
import com.domain.backend.exception.ResourceNotFoundException;
import com.domain.backend.exception.UnauthorizedException;
import com.domain.backend.exception.ValidationException;
import com.domain.backend.repository.LessonRepository;
import com.domain.backend.repository.QuizAttemptRepository;
import com.domain.backend.repository.QuizRepository;
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
 * Dịch vụ xử lý logic nghiệp vụ liên quan đến quản lý Quiz và các lần thử Quiz của người dùng.
 */
@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private LessonRepository lessonRepository; // Để liên kết quiz với bài học

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    /**
     * Chuyển đổi Quiz entity sang QuizResponse DTO.
     *
     * @param quiz Entity Quiz.
     * @return QuizResponse DTO.
     */
    private QuizResponse convertToQuizResponse(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        BeanUtils.copyProperties(quiz, response);
        if (quiz.getQuestions() != null) {
            response.setQuestions(quiz.getQuestions().stream()
                    .map(this::convertToQuestionResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    /**
     * Chuyển đổi Question entity sang QuestionResponse DTO.
     *
     * @param question Entity Question.
     * @return QuestionResponse DTO.
     */
    private QuestionResponse convertToQuestionResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        BeanUtils.copyProperties(question, response);
        // Không sao chép correctAnswer ở đây để tránh lộ đáp án cho người dùng thông thường
        // Trừ khi đây là API dành riêng cho admin/teacher hoặc sau khi nộp bài.
        // Bạn có thể thêm logic kiểm tra vai trò ở đây nếu cần.
        return response;
    }

    /**
     * Chuyển đổi QuizAttempt entity sang QuizAttemptResponse DTO.
     *
     * @param attempt Entity QuizAttempt.
     * @return QuizAttemptResponse DTO.
     */
    private QuizAttemptResponse convertToQuizAttemptResponse(QuizAttempt attempt) {
        QuizAttemptResponse response = new QuizAttemptResponse();
        BeanUtils.copyProperties(attempt, response);
        return response;
    }

    /**
     * Chuyển đổi QuestionRequest DTO sang Question entity.
     *
     * @param questionRequest DTO QuestionRequest.
     * @return Entity Question đã chuyển đổi.
     */
    private Question convertQuestionRequestToQuestion(QuestionRequest questionRequest) {
        Question question = new Question();
        BeanUtils.copyProperties(questionRequest, question);
        if (questionRequest.getId() != null) {
            question.setId(questionRequest.getId());
        }

        if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
            List<QuestionOption> options = questionRequest.getOptions().stream()
                    .map(this::convertOptionRequestToOption)
                    .collect(Collectors.toList());
            question.setOptions(options);
        }
        return question;
    }

    /**
     * Chuyển đổi QuestionOptionRequest DTO sang QuestionOption entity.
     *
     * @param optionRequest DTO QuestionOptionRequest.
     * @return Entity QuestionOption đã chuyển đổi.
     */
    private QuestionOption convertOptionRequestToOption(QuestionOptionRequest optionRequest) {
        QuestionOption option = new QuestionOption();
        BeanUtils.copyProperties(optionRequest, option);
        if (optionRequest.getId() != null) {
            option.setId(optionRequest.getId());
        }
        return option;
    }

    /**
     * Tạo một quiz mới và liên kết nó với một bài học.
     *
     * @param lessonId    ID của bài học mà quiz này thuộc về.
     * @param quizRequest Payload yêu cầu để tạo quiz.
     * @return Đối tượng QuizResponse đã tạo.
     * @throws ResourceNotFoundException nếu không tìm thấy bài học.
     * @throws ValidationException       nếu bài học đã có quiz liên kết.
     * @throws UnauthorizedException     nếu người dùng không được phép.
     */
    @Transactional // Đảm bảo tính nguyên tử cho các hoạt động liên quan đến nhiều tài liệu
    public QuizResponse createQuiz(String lessonId, QuizRequest quizRequest) {
        // Kiểm tra ủy quyền: chỉ ADMIN hoặc TEACHER mới có thể tạo quiz
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép tạo quiz.");
        }

        // Đảm bảo bài học tồn tại
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài học", "id", lessonId));

        // Kiểm tra xem bài học đã có quiz chưa
        if (lesson.getQuizId() != null) {
            throw new ValidationException("Bài học " + lessonId + " đã có một quiz liên kết.");
        }

        Quiz quiz = new Quiz();
        BeanUtils.copyProperties(quizRequest, quiz);
        quiz.setLessonId(lessonId);

        // Chuyển đổi QuestionRequest sang đối tượng Question
        if (quizRequest.getQuestions() != null && !quizRequest.getQuestions().isEmpty()) {
            List<Question> questions = quizRequest.getQuestions().stream()
                    .map(this::convertQuestionRequestToQuestion)
                    .collect(Collectors.toList());
            quiz.setQuestions(questions);
        }

        Quiz savedQuiz = quizRepository.save(quiz);

        // Cập nhật bài học để liên kết với quiz mới này
        lesson.setQuizId(savedQuiz.getId());
        lesson.setUpdatedAt();
        lessonRepository.save(lesson);

        return convertToQuizResponse(savedQuiz);
    }

    /**
     * Lấy một quiz theo ID của nó.
     *
     * @param quizId ID của quiz.
     * @return Optional chứa QuizResponse nếu tìm thấy, rỗng nếu không.
     */
    public Optional<QuizResponse> getQuizById(String quizId) {
        return quizRepository.findById(quizId)
                .map(this::convertToQuizResponse);
    }

    /**
     * Lấy một quiz theo ID bài học liên quan của nó.
     *
     * @param lessonId ID của bài học.
     * @return Optional chứa QuizResponse nếu tìm thấy, rỗng nếu không.
     */
    public Optional<QuizResponse> getQuizByLessonId(String lessonId) {
        return quizRepository.findByLessonId(lessonId)
                .map(this::convertToQuizResponse);
    }

    /**
     * Cập nhật một quiz hiện có.
     *
     * @param quizId      ID của quiz cần cập nhật.
     * @param quizRequest Payload yêu cầu để cập nhật quiz.
     * @return Đối tượng QuizResponse đã cập nhật.
     * @throws ResourceNotFoundException nếu không tìm thấy quiz.
     * @throws UnauthorizedException     nếu người dùng không được phép.
     */
    public QuizResponse updateQuiz(String quizId, QuizRequest quizRequest) {
        // Kiểm tra ủy quyền: chỉ ADMIN hoặc TEACHER mới có thể cập nhật quiz
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép cập nhật quiz.");
        }

        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        BeanUtils.copyProperties(quizRequest, existingQuiz, "id", "createdAt", "lessonId"); // Loại trừ các trường không thay đổi

        if (quizRequest.getQuestions() != null) {
            List<Question> updatedQuestions = quizRequest.getQuestions().stream()
                    .map(this::convertQuestionRequestToQuestion)
                    .collect(Collectors.toList());
            existingQuiz.setQuestions(updatedQuestions);
        } else {
            existingQuiz.setQuestions(new ArrayList<>());
        }

        existingQuiz.setUpdatedAt();
        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return convertToQuizResponse(updatedQuiz);
    }

    /**
     * Xóa một quiz theo ID của nó.
     *
     * @param quizId ID của quiz cần xóa.
     * @return MessageResponse thành công.
     * @throws ResourceNotFoundException nếu không tìm thấy quiz.
     * @throws UnauthorizedException     nếu người dùng không được phép.
     */
    @Transactional
    public MessageResponse deleteQuiz(String quizId) {
        // Kiểm tra ủy quyền: chỉ ADMIN hoặc TEACHER mới có thể xóa quiz
        if (!SecurityUtils.isAdmin() && !SecurityUtils.isTeacher()) {
            throw new UnauthorizedException("Bạn không được phép xóa quiz.");
        }

        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        // Xóa quizId khỏi bài học liên quan
        Optional<Lesson> optionalLesson = lessonRepository.findById(existingQuiz.getLessonId());
        if (optionalLesson.isPresent()) {
            Lesson lesson = optionalLesson.get();
            lesson.setQuizId(null); // Hủy liên kết quiz khỏi bài học
            lesson.setUpdatedAt();
            lessonRepository.save(lesson);
        }

        quizRepository.delete(existingQuiz);
        return new MessageResponse("Quiz đã xóa thành công!");
    }

    /**
     * Nộp câu trả lời của người dùng cho một quiz và tính điểm.
     *
     * @param submitQuizRequest Yêu cầu chứa ID quiz và câu trả lời của người dùng.
     * @return Đối tượng QuizAttemptResponse với điểm số và kết quả.
     * @throws ResourceNotFoundException nếu không tìm thấy quiz.
     * @throws ValidationException       nếu câu trả lời không hợp lệ.
     */
    public QuizAttemptResponse submitQuiz(SubmitQuizRequest submitQuizRequest) {
        String userId = SecurityUtils.getCurrentUserId();
        String quizId = submitQuizRequest.getQuizId();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        int score = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();

        // Lặp qua các câu trả lời đã nộp và so sánh với các câu trả lời đúng
        for (UserAnswerRequest userAnswerRequest : submitQuizRequest.getAnswers()) {
            Optional<Question> optionalQuestion = quiz.getQuestions().stream()
                    .filter(q -> q.getId().equals(userAnswerRequest.getQuestionId()))
                    .findFirst();

            if (optionalQuestion.isEmpty()) {
                throw new ValidationException("Câu hỏi " + userAnswerRequest.getQuestionId() + " không tìm thấy trong quiz " + quizId);
            }
            Question question = optionalQuestion.get();

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setQuestionId(question.getId());

            boolean isCorrect = false;
            if ("MCQ".equalsIgnoreCase(question.getQuestionType())) {
                if (userAnswerRequest.getSelectedOptionId() != null) {
                    Optional<QuestionOption> selectedOption = question.getOptions().stream()
                            .filter(o -> o.getId().equals(userAnswerRequest.getSelectedOptionId()))
                            .findFirst();
                    if (selectedOption.isPresent() && selectedOption.get().isCorrect()) {
                        isCorrect = true;
                    }
                    userAnswer.setSelectedOptionId(userAnswerRequest.getSelectedOptionId());
                }
            } else if ("FILL_BLANK".equalsIgnoreCase(question.getQuestionType())) {
                if (userAnswerRequest.getSubmittedTextAnswer() != null &&
                        question.getCorrectAnswer() != null &&
                        question.getCorrectAnswer().equalsIgnoreCase(userAnswerRequest.getSubmittedTextAnswer().trim())) {
                    isCorrect = true;
                }
                userAnswer.setSubmittedTextAnswer(userAnswerRequest.getSubmittedTextAnswer());
            }
            userAnswer.setCorrect(isCorrect);
            userAnswers.add(userAnswer);

            if (isCorrect) {
                score++;
            }
        }

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setUserId(userId);
        quizAttempt.setQuizId(quizId);
        quizAttempt.setLessonId(quiz.getLessonId());
        quizAttempt.setScore(score);
        quizAttempt.setTotalQuestions(quiz.getQuestions().size());
        quizAttempt.setPercentageScore((double) score / quiz.getQuestions().size() * 100);
        quizAttempt.setPassed(quizAttempt.getPercentageScore() >= 70); // Ví dụ điểm đậu: 70%
        quizAttempt.setAnswers(userAnswers);

        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);
        return convertToQuizAttemptResponse(savedAttempt);
    }

    /**
     * Lấy tất cả các lần thử quiz của người dùng hiện tại.
     *
     * @return Danh sách các đối tượng QuizAttemptResponse.
     */
    public List<QuizAttemptResponse> getUserQuizAttempts() {
        String userId = SecurityUtils.getCurrentUserId();
        return quizAttemptRepository.findByUserId(userId).stream()
                .map(this::convertToQuizAttemptResponse)
                .collect(Collectors.toList());
    }

    /**
     * Lấy một lần thử quiz cụ thể theo ID của nó.
     *
     * @param attemptId ID của lần thử quiz.
     * @return Optional chứa QuizAttemptResponse nếu tìm thấy.
     */
    public Optional<QuizAttemptResponse> getQuizAttemptById(String attemptId) {
        return quizAttemptRepository.findById(attemptId)
                .map(this::convertToQuizAttemptResponse);
    }
}
