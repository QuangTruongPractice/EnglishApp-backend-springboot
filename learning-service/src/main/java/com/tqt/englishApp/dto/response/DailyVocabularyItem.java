package com.tqt.englishApp.dto.response;

import com.tqt.englishApp.entity.Vocabulary;
import com.tqt.englishApp.enums.VocabularyStatus;
import lombok.Value;

@Value
public class DailyVocabularyItem {
    Vocabulary vocabulary;
    boolean isReview;
    VocabularyStatus status;
}
