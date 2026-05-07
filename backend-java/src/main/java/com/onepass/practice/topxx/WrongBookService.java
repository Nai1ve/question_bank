package com.onepass.practice.topxx;

import com.onepass.practice.common.AppException;
import com.onepass.practice.practice.persistence.QuestionWrongStatMapper;
import com.onepass.practice.practice.persistence.WrongBookQuestionRowDO;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class WrongBookService {

    private final QuestionWrongStatMapper questionWrongStatMapper;

    public WrongBookService(ObjectProvider<QuestionWrongStatMapper> questionWrongStatMapperProvider) {
        this.questionWrongStatMapper = questionWrongStatMapperProvider.getIfAvailable();
    }

    public List<WrongBookItemView> listWrongBook(Long studentId, int limit) {
        if (questionWrongStatMapper == null) {
            throw new AppException("Wrong book data is not available");
        }

        return questionWrongStatMapper.selectTopWrongQuestions(studentId, limit).stream()
                .map(this::toView)
                .toList();
    }

    private WrongBookItemView toView(WrongBookQuestionRowDO item) {
        List<String> tags = item.getTags() == null || item.getTags().isBlank()
                ? List.of()
                : Arrays.stream(item.getTags().split("\\|\\|")).toList();
        return new WrongBookItemView(
                item.getQuestionId(),
                item.getStem(),
                item.getCategoryName(),
                item.getWrongCount() == null ? 0 : item.getWrongCount(),
                item.getAnsweredCount() == null ? 0 : item.getAnsweredCount(),
                tags
        );
    }
}
